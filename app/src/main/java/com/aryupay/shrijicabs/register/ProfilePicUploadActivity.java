package com.aryupay.shrijicabs.register;

import android.Manifest;
import android.app.Activity;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.activities.MapsActivity;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.utils.Keys;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilePicUploadActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    ApiInterface apiInterface;
    CircleImageView circleImageView;
    FrameLayout pictureFromCamera;
    String token;
    String profileName;
    Bitmap photo;
    ShrijicabsUserSessionManager sessionManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_pic_update);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new ShrijicabsUserSessionManager(this);
        HashMap<String,String> user = sessionManager.getUserDetails();
        token = user.get(Keys.KEY_TOKEN);
        profileName = user.get(Keys.KEY_PHONE_NUMBER);
        circleImageView = (CircleImageView)findViewById(R.id.circular_image_view);
        pictureFromCamera = (FrameLayout)findViewById(R.id.picture_from_camera);
        Button uploadProfile = (Button)findViewById(R.id.uploadProfile);
        uploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(photo!=null){
                    File file = persistImage(photo,profileName);
                    uploadProfileImageToServer(file);
                }else{
                    Intent intent = new Intent(ProfilePicUploadActivity.this, MapsActivity.class);
                    finish();
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_layout,R.anim.exit_layout);
                }
            }
        });

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
            circleImageView.setImageBitmap(photo);
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
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                if (response.isSuccessful()){
                    Log.i("onLogin",response.body().toString());
                    String status = response.body().get("status").getAsString();
                    if(status.equals("success")){
                        new SweetAlertDialog(ProfilePicUploadActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText(response.body().get("message").getAsString())
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Intent intent = new Intent(ProfilePicUploadActivity.this, MapsActivity.class);
                                        finish();
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.enter_layout,R.anim.exit_layout);
                                    }
                                })
                                .show();
                        Toast.makeText(ProfilePicUploadActivity.this, response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    }else{
                        new SweetAlertDialog(ProfilePicUploadActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText(response.body().get("message").getAsString())
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Intent intent = new Intent(ProfilePicUploadActivity.this, MapsActivity.class);
                                        finish();
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.enter_layout,R.anim.exit_layout);
                                    }
                                })
                                .show();
                        Toast.makeText(ProfilePicUploadActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    try {
                        JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                        String error = object.get("error").toString();
                        new SweetAlertDialog(ProfilePicUploadActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText(error)
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        Intent intent = new Intent(ProfilePicUploadActivity.this, MapsActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.enter_layout,R.anim.exit_layout);
                                        finish();
                                    }
                                })
                                .show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                if(isNetworkAvailable()){
                    new SweetAlertDialog(ProfilePicUploadActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Shriji Cabs")
                            .setContentText(t.toString())
                            .show();
                    Toast.makeText(ProfilePicUploadActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                }else{
                    new SweetAlertDialog(ProfilePicUploadActivity.this, SweetAlertDialog.ERROR_TYPE)
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
}
