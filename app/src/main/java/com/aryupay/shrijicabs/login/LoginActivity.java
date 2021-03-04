package com.aryupay.shrijicabs.login;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Callback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.aryupay.shrijicabs.activities.MapsActivity;
import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.register.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    TextView register,forgotPassword;
    Button linearButton;
    EditText phoneNumber,password;
    ApiInterface apiInterface;
    String token;
    String tokenFirebase;
    ShrijicabsUserSessionManager sessionManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new ShrijicabsUserSessionManager(this);
        phoneNumber = findViewById(R.id.phoneNumber);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        linearButton = findViewById(R.id.login);
        forgotPassword = findViewById(R.id.forgot_password_tv);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        linearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendLoginDetailsToServer();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter_layout,R.anim.exit_layout);
                finish();
            }
        });

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            tokenFirebase = task.getResult().getToken();
                            Log.d("firebase_token",""+tokenFirebase);
                        }else {

                        }
                    }
                });
    }

    private void sendLoginDetailsToServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait .....");
        dialog.show();

        Map<String, String> map = new HashMap<>();
        map.put("mobile", phoneNumber.getText().toString());
        map.put("password", password.getText().toString());
        map.put("role", "client");

        Call<JsonObject> call = apiInterface.onLogin(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i("onLogin", response.body().toString());
                    token = response.body().get("success").getAsJsonObject().get("token").getAsString();
                    String userDetails = response.body().get("success").getAsJsonObject().get("user").getAsJsonObject().toString();

                    try {
                        JSONObject jsonObjectUser = new JSONObject(userDetails);
                        String name = jsonObjectUser.getString("firstname");
                        String email = jsonObjectUser.getString("email");
                        String mobile = jsonObjectUser.getString("mobile");
                        sessionManager.createUserLoginSession(name, mobile, token, email, getApplicationContext());
                        registerDeviceToNetwork();
                    } catch (JSONException e) {
                        e.printStackTrace();
                     }
                 }else{
                    try {
                        JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                        String[] error = object.get("error").toString().split(",");
                        StringBuilder stringBuilder = new StringBuilder();
                        for(int i=0;i<error.length;i++){
                            stringBuilder.append(error[i]+"\n");
                        }
                        showDailogForError(stringBuilder.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                 }
             }

             @Override
             public void onFailure(Call<JsonObject> call, Throwable t) {
                 dialog.dismiss();
                 new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                         .setTitleText("Shriji Cabs")
                         .setContentText("Please Check Your Connectivity")
                         .show();
                 Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
             }
        });
    }

    public void showDailogForError(String error){
//        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
//        alert.setMessage(error);
//        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                dialogInterface.dismiss();
//            }
//        });
//        AlertDialog dialog = alert.create();
//        dialog.setTitle(R.string.app_name);
//        dialog.show();

        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Shriji Cabs")
                .setContentText(error)
                .show();
    }

    private void registerDeviceToNetwork() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait....");
        dialog.show();

        String androidVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
        HashMap<String,String> map = new HashMap<>();
        map.put("registration_id",tokenFirebase);
        map.put("device_id", Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        map.put("device_os","Android "+androidVersion);
        Call<JsonObject> call = apiInterface.updateAppInfo(map,"Bearer " + token);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                Log.i("Tregdevesponse",new Gson().toJson(response.body()));
                Log.d("respon",response.code()+" ");
                if(response.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter_layout,R.anim.exit_layout);
                    finish();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(LoginActivity.this);
        dialog.setMessage("Do You Want to Close this application");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                finishAffinity();
                startActivity(intent);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        AlertDialog builder = dialog.create();
        builder.setTitle("Shriji Cabs");
        builder.show();
    }
}
