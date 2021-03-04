package com.aryupay.shrijicabs.activities;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.network.Apis;
import com.aryupay.shrijicabs.network.ShrijiCabsSingleton;
import com.aryupay.shrijicabs.register.OtpActivity;
import com.aryupay.shrijicabs.register.ProfilePicUploadActivity;
import com.aryupay.shrijicabs.register.RegisterActivity;
import com.aryupay.shrijicabs.utils.Keys;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileActivity extends AppCompatActivity {
    ShrijicabsUserSessionManager sessionManager;
    TextView userNameTv,mobileNoTv,emailTv,dateOfBirth_tv,toolbarText;
    EditText nameEt,emailEt,dateOfBirthEt;
    String token;
    Button updateProfile_btn,profileSubmit;
    LinearLayout updateProfile,viewProfile;
    DatePickerDialog datePickerDialog;
    Toolbar toolbar;
    int year;
    int month;
    int dayOfMonth;
    Calendar calendar;
    private static final int CAMERA_REQUEST = 1088;
    private static final int MY_CAMERA_PERMISSION_CODE = 101;
    ApiInterface apiInterface;
    CircleImageView circleImageView;
    FrameLayout pictureFromCamera;
    String profileName;
    Bitmap photo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new ShrijicabsUserSessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetails();
        token = user.get(Keys.KEY_TOKEN);
        profileName = user.get(Keys.KEY_PHONE_NUMBER);
        circleImageView = (CircleImageView)findViewById(R.id.iv_profileImage_update);
        getProfileImageFromServer();

        circleImageView.setImageResource(R.drawable.avatar);

        pictureFromCamera = (FrameLayout)findViewById(R.id.picture_from_camera);
        pictureFromCamera.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });



        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(updateProfile.getVisibility()==View.VISIBLE){
                    Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.exit, R.anim.enter);
                }else{
                    Intent intent = new Intent(ProfileActivity.this,MapsActivity.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.exit, R.anim.enter);
                }
            }
        });

        userNameTv = (TextView)findViewById(R.id.userName_tv);
        mobileNoTv = (TextView)findViewById(R.id.mobile_no_tv);
        emailTv = (TextView)findViewById(R.id.email_tv);

        nameEt = (EditText)findViewById(R.id.name_et);
        emailEt = (EditText)findViewById(R.id.email_et);
        dateOfBirthEt = (EditText)findViewById(R.id.dateOfBirth_et);
        toolbarText = (TextView)findViewById(R.id.toolbarText);
        dateOfBirthEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTimeField();
            }
        });

        updateProfile_btn = (Button)findViewById(R.id.updateProfile_btn);
        profileSubmit = (Button)findViewById(R.id.profileSubmit);
        profileSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDetailsToServer();
            }
        });
        dateOfBirth_tv = (TextView)findViewById(R.id.dateOfBirth);
        updateProfile = (LinearLayout)findViewById(R.id.updateProfile);
        viewProfile = (LinearLayout)findViewById(R.id.viewProfile);
        mobileNoTv.setText(user.get(Keys.KEY_PHONE_NUMBER));
        updateProfile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarText.setText(R.string.updateProfle);
                viewProfile.setVisibility(View.GONE);
                updateProfile.setVisibility(View.VISIBLE);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                Animation slideOut = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.exit_layout);
                viewProfile.startAnimation(slideOut);
                Animation slide = AnimationUtils.loadAnimation(ProfileActivity.this, R.anim.enter_layout);
                updateProfile.startAnimation(slide);
            }
        });
        getDetailsFromServer();
    }

    private void getProfileImageFromServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait....");
        dialog.show();

        Call<JsonObject> call = apiInterface.getFiles("Bearer " + token);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                dialog.dismiss();
                Log.i("Tesponse ",response.body().toString());
                try {
                    JSONObject responseinjsonObject = new JSONObject(response.body().toString());
                    if(responseinjsonObject.getString("status").equals("success")){
                        String data = responseinjsonObject.getString("data");
                        JSONArray jsonArray = new JSONArray(data);
                        JSONObject jsonObject = jsonArray.getJSONObject(0);
                        String file = jsonObject.getString("file");
                        JSONObject fileDesc = new JSONObject(file);
                        String imagePath = fileDesc.getString("path");
                        Picasso.get().load(imagePath).into(circleImageView);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(ProfileActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            photo = (Bitmap) data.getExtras().get("data");
            File file = persistImage(photo,profileName);
            uploadProfileImageToServer(file);
        }
    }

    private File persistImage(Bitmap bitmap, String name) {
        File filesDir = getApplicationContext().getFilesDir();
        File imageFile = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
        }
        return imageFile;
    }


    private void setDateTimeField() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        Calendar calendar= Calendar.getInstance();
                        calendar.set(year,month,day);
                        String s =String.format(Locale.getDefault(),"%1$tY-%1$tm-%1$td",calendar);
                        dateOfBirthEt.setText(s);
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void updateDetailsToServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait ...........");
        dialog.show();
        StringRequest putRequest = new StringRequest(Request.Method.PUT, Apis.USER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.dismiss();
                        // response
                        Log.d("Response", response.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getString("status").equals("success")){
                                String message = jsonObject.getString("message");
                                new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText(message)
                                        .setContentText(response.toString())
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                Intent intent = new Intent(ProfileActivity.this, MapsActivity.class);
                                                finish();
                                                startActivity(intent);
                                            }
                                        })
                                        .show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        // error
                        new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText("Please Check Your Connectivity")
                                .show();
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("firstname",nameEt.getText().toString().trim());
                params.put("email",emailEt.getText().toString().trim());
                params.put("date_of_birth",dateOfBirthEt.getText().toString().trim());
                return params;
            }
        };

        RequestQueue requestQueue = ShrijiCabsSingleton.getInstance(this).getRequestQueue();
        requestQueue.add(putRequest);
    }

    private void getDetailsFromServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait ...........");
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.GET, Apis.USER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                dialog.dismiss();
                Log.d(response.toString(),"sdkufhiaf");
                if(response.contains("status")){
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String status =  jsonObject.getString("status");
                        if(status.equals("success")){
                            String data = jsonObject.getString("data");
                            JSONObject dataJsonObject = new JSONObject(data);
                            String name = dataJsonObject.getString("name");
                            String email = dataJsonObject.getString("email");
                            String dateOfBirth = dataJsonObject.getString("date_of_birth");
                            userNameTv.setText(name);
                            nameEt.setText(name);
                            emailTv.setText(email);
                            emailEt.setText(email);
                            dateOfBirth_tv.setText(dateOfBirth);
                            dateOfBirthEt.setText(dateOfBirth);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(ProfileActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText("Please Check Your Connectivity")
                        .show();
                Toast.makeText(ProfileActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + token);
                return params;
            }
        };
        RequestQueue requestQueue = ShrijiCabsSingleton.getInstance(this).getRequestQueue();
        requestQueue.add(request);
    }

    private void uploadProfileImageToServer(File file) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait .....");
        dialog.show();

        Map<String,String> map = new HashMap<>();
        map.put("type","pic");
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", "names", RequestBody.create(MediaType.parse("image/*"), file));

        Call<JsonObject> call = apiInterface.uploadFile(map,filePart,"Bearer " + token);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                dialog.dismiss();
                if (response.isSuccessful()){
                    Log.i("onLogin",response.body().toString());
                    String status = response.body().get("status").getAsString();
                    if(status.equals("success")){
                        Picasso.get().load(response.body().get("data").getAsString()).into(circleImageView);
                        new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText(response.body().get("message").getAsString())
                                .show();
                        Toast.makeText(ProfileActivity.this, response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }else{
                        Picasso.get().load(response.body().get("data").getAsString()).into(circleImageView);
                        new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText(response.body().get("message").getAsString())
                                .show();
                        Toast.makeText(ProfileActivity.this, response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    try {
                        JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                        String error = object.get("error").toString();
                        Picasso.get().load(response.body().get("data").getAsString()).into(circleImageView);
                        new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText(error)
                                .show();
                        Toast.makeText(ProfileActivity.this, response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                if(isNetworkAvailable()){
                    new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Shriji Cabs")
                            .setContentText(t.toString())
                            .show();
                    Toast.makeText(ProfileActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                }else{
                    new SweetAlertDialog(ProfileActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Shriji Cabs")
                            .setContentText("Please Check Your Connectivity")
                            .show();
                }
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if(updateProfile.getVisibility()==View.VISIBLE){
            Intent intent = new Intent(ProfileActivity.this,ProfileActivity.class);
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.exit, R.anim.enter);
        }else{
            Intent intent = new Intent(ProfileActivity.this,MapsActivity.class);
            finish();
            startActivity(intent);
            overridePendingTransition(R.anim.exit, R.anim.enter);
        }
    }
}
