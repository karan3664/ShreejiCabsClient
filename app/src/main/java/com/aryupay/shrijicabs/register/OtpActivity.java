package com.aryupay.shrijicabs.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.utils.Keys;
import com.github.irvingryan.VerifyCodeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {
    VerifyCodeView verifyCodeView;
    String token;
    String otpFromRsponse,tokenFromResponse,nameFromresponse,mobileFromResponse,emailFromResponse;

    ShrijicabsUserSessionManager sessionManager;
    ApiInterface apiInterface;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new ShrijicabsUserSessionManager(getApplicationContext());
        tokenFromResponse = getIntent().getStringExtra(Keys.TOKEN_INTENT);
        otpFromRsponse = getIntent().getStringExtra(Keys.OTP_INTENT);
        nameFromresponse = getIntent().getStringExtra(Keys.NAME_INTENT);
        mobileFromResponse = getIntent().getStringExtra(Keys.MOBILE_INTENT);
        emailFromResponse = getIntent().getStringExtra(Keys.EMAIL_INTENT);
        Log.d(otpFromRsponse,"responseOtp");
        verifyCodeView = findViewById(R.id.verify);
        verifyCodeView.setListener(new VerifyCodeView.OnTextChangListener() {
            @Override
            public void afterTextChanged(String text) {
                if(text.length() == Integer.parseInt("4"))
                    if(text.equals(otpFromRsponse)) {
                        registerDeviceToNetwork();
                    }else{
                        new SweetAlertDialog(OtpActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText("Invalid Code")
                                .setConfirmText("Ok")
                                .show();
                    }
            }
        });
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            token = task.getResult().getToken();

                        }else {

                        }
                    }
                });
    }

    private void registerDeviceToNetwork() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait....");
        dialog.show();

        String androidVersion = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
        HashMap<String,String> map = new HashMap<>();
        map.put("registration_id",token);
        map.put("device_id", Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
        map.put("device_os","Android "+androidVersion);
        Call<JsonObject> call = apiInterface.updateAppInfo(map,"Bearer " + tokenFromResponse);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                Log.i("Tregdevesponse",new Gson().toJson(response.body()));
                Log.d("respon",response.code()+" ");
                if(response.isSuccessful()){
                    notifyApiThatOtpHasBeenVerified();
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(OtpActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void notifyApiThatOtpHasBeenVerified() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait....");
        dialog.show();

        Call<JsonObject> call = apiInterface.onSubmitRegistration("Bearer " + tokenFromResponse);

        call.enqueue(new Callback<JsonObject>() {
             @Override
             public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                 dialog.dismiss();
                 Log.i("Tesponse ",response.code()+" ");
                 if(response.code()==401){
                     sessionManager.createUserLoginSession(nameFromresponse,mobileFromResponse,tokenFromResponse,emailFromResponse,getApplicationContext());
                     try {
                        // registerDeviceToNetwork();
                         JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                         new SweetAlertDialog(OtpActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                 .setTitleText("Shriji Cabs")
                                 .setContentText(object.get("message").toString())
                                 .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                     @Override
                                     public void onClick(SweetAlertDialog sDialog) {
                                         Intent intent = new Intent(OtpActivity.this, ProfilePicUploadActivity.class);
                                         startActivity(intent);
                                         overridePendingTransition(R.anim.enter_layout,R.anim.exit_layout);
                                         finish();
                                     }
                                 })
                                 .show();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }else{
                     Toast.makeText(OtpActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                 }
             }
             @Override
             public void onFailure(Call<JsonObject> call, Throwable t) {
                 dialog.dismiss();
                 Toast.makeText(OtpActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
             }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
