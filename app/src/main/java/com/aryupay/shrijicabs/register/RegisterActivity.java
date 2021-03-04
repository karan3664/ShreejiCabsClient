package com.aryupay.shrijicabs.register;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.aryupay.shrijicabs.R;

import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.login.LoginActivity;
import com.aryupay.shrijicabs.utils.Keys;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    ApiInterface apiInterface;
    EditText fullnameEdittext,emailEdittext,phoneEdittext,passwordEdittext,confirmPasswordEdittext;
    Button signUpBtn;
    TextView login_tv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        fullnameEdittext = (EditText)findViewById(R.id.fullname_et);
        emailEdittext = (EditText)findViewById(R.id.email_et);
        phoneEdittext = (EditText)findViewById(R.id.phone_no_et);
        passwordEdittext = (EditText)findViewById(R.id.password_et);
        confirmPasswordEdittext = (EditText)findViewById(R.id.confirm_password_et);
        login_tv = (TextView)findViewById(R.id.login_tv);
        signUpBtn = (Button)findViewById(R.id.signUp_btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateIfAnyFieldsIsEmpty();
            }
        });
        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.exit,R.anim.enter);
            }
        });
    }

    private void validateIfAnyFieldsIsEmpty() {
        if(fullnameEdittext.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please Enter The Full Name", Toast.LENGTH_SHORT).show();
        }else if(emailEdittext.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please Enter The Email", Toast.LENGTH_SHORT).show();
        }else if (phoneEdittext.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please Enter The Phone Number", Toast.LENGTH_SHORT).show();
        }else if (passwordEdittext.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please Enter The Password", Toast.LENGTH_SHORT).show();
        }else if(confirmPasswordEdittext.getText().toString().trim().equals("")){
            Toast.makeText(this, "Please Confirm Your Password", Toast.LENGTH_SHORT).show();
        } else if(phoneEdittext.getText().toString().trim().length()!=10){
            Toast.makeText(this, "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
        } else if(passwordEdittext.getText().toString().trim().length()<6){
            Toast.makeText(this, "Please Enter The Six Digit Password", Toast.LENGTH_SHORT).show();
        } else{
            if(passwordEdittext.getText().toString().trim().equals(confirmPasswordEdittext.getText().toString().trim())){
                sendDetailsToServer();
            }else {
                showDailogForError("Password is not Matching");
            }
        }
    }

    private void sendDetailsToServer() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait....");
        dialog.show();
        HashMap<String,String> params = new HashMap<>();
        params.put("firstname",fullnameEdittext.getText().toString().trim());
        params.put("lastname","");
        params.put("role","client");
        params.put("email",emailEdittext.getText().toString().trim());
        params.put("mobile",phoneEdittext.getText().toString().trim());
        params.put("password",passwordEdittext.getText().toString().trim());

        Call<JsonObject> call = apiInterface.onRegister(params);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                if (response.isSuccessful()){
                    Log.i("onLogin",response.body().toString());

                    if(response.body().toString().contains("status")){
                        try {
                            String status = new JSONObject(response.body().toString()).getString("status");
                            if(status.equals("success")){
                                String data = new JSONObject(response.body().toString()).getString("data");
                                JSONObject userData = new JSONObject(data);
                                String name = userData.getString("name");
                                String mobile = userData.getString("mobile");
                                String otp = userData.getString("otp");
                                String token = userData.getString("token");
                                Intent intent = new Intent(RegisterActivity.this,OtpActivity.class);
                                intent.putExtra(Keys.NAME_INTENT,name);
                                intent.putExtra(Keys.MOBILE_INTENT,mobile);
                                intent.putExtra(Keys.OTP_INTENT,otp);
                                intent.putExtra(Keys.TOKEN_INTENT,token);
                                intent.putExtra(Keys.EMAIL_INTENT,emailEdittext.getText().toString().trim());
                                startActivity(intent);
                                overridePendingTransition(R.anim.enter_layout,R.anim.exit_layout);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            String[] error = new JSONObject(response.body().toString()).getString("error").split(",");
                            StringBuilder stringBuilder = new StringBuilder();
                            for(int i=0;i<error.length;i++){
                                stringBuilder.append(error[i]+"\n");
                            }
                            showDailogForError(stringBuilder.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                showDailogForError("Please Check Your Connectivity");
                Toast.makeText(RegisterActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showDailogForError(String error){
        new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Shriji Cabs")
                .setContentText(error)
                .show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.exit,R.anim.enter);
        finish();
    }
}
