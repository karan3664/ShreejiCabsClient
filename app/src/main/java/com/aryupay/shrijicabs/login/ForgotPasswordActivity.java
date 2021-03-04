package com.aryupay.shrijicabs.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.activities.MapsActivity;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.utils.Keys;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText phoneNumber;
    ApiInterface apiInterface;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        phoneNumber = (EditText)findViewById(R.id.phoneNumber_forgot_pwd);
        Button submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendPhoneNumberToServer();
            }
        });
    }

    private void sendPhoneNumberToServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait .....");
        dialog.show();

        HashMap<String,String> map = new HashMap<>();
        map.put("mobile",phoneNumber.getText().toString());

        Call<JsonObject> call = apiInterface.onPasswordChange(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                if (response.isSuccessful()){
                    Log.i("onVerify",response.body().toString());
                    String responseString = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String status = jsonObject.getString("status");
                        if(status.equals("success")){
                            Toast.makeText(ForgotPasswordActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            String data = jsonObject.getString("data");
                            JSONObject jsonObjectOtp = new JSONObject(data);
                            String otp = jsonObjectOtp.getString("otp");
                            Intent intent = new Intent(ForgotPasswordActivity.this,OtpForgotPasswordActivity.class);
                            intent.putExtra(Keys.OTP_FORGOT_PASSWORD_INTENT,otp);
                            intent.putExtra(Keys.PHONE_NUMBER_OTP,phoneNumber.getText().toString());
                            finish();
                            startActivity(intent);

                        }else{
                            Toast.makeText(ForgotPasswordActivity.this, responseString, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }else{
                    try {
                        JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                        String error = object.get("error").toString();

                        showDailogForError(error);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                dialog.dismiss();
                new SweetAlertDialog(ForgotPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText("Please Check Your Connectivity")
                        .show();
                Toast.makeText(ForgotPasswordActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDailogForError(String error) {
        new SweetAlertDialog(ForgotPasswordActivity.this,SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Shriji Cabs")
                .setContentText(error)
                .show();
    }
}
