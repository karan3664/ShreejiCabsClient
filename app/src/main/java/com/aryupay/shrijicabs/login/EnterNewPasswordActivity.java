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
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
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

public class EnterNewPasswordActivity extends AppCompatActivity {
    ApiInterface apiInterface;
    EditText passwordEt,confirmPasswordEt;
    Button submit;
    String phoneNumber;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_new_password);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        phoneNumber = getIntent().getStringExtra(Keys.PHONE_NUMBER_OTP);
        passwordEt = (EditText)findViewById(R.id.password);
        confirmPasswordEt = (EditText)findViewById(R.id.confirm_password);
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEt.getText().toString().trim();
                String confirmPassword = confirmPasswordEt.getText().toString().trim();
                if(password.equals(confirmPassword)){
                    sendDetailsToServer(phoneNumber,password);
                }else{
                    new SweetAlertDialog(EnterNewPasswordActivity.this,SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Shriji Cabs")
                            .setContentText("Password doesnt matches")
                            .setConfirmText("Ok")
                            .show();
                }
            }
        });
    }

    private void sendDetailsToServer(String phoneNumber, String password) {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait .....");
        dialog.show();

        HashMap<String,String> map = new HashMap<>();
        map.put("mobile",phoneNumber);
        map.put("password",password);
        map.put("c_password",password);

        Call<JsonObject> call = apiInterface.newPassword(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                if (response.isSuccessful()){
                    Log.i("onVerify",response.body().get("message").getAsString());
                    Toast.makeText(EnterNewPasswordActivity.this, response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                    new SweetAlertDialog(EnterNewPasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Shriji Cabs")
                            .setContentText(response.body().get("message").getAsString())
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    Intent intent = new Intent(EnterNewPasswordActivity.this,LoginActivity.class);
                                    finish();
                                    startActivity(intent);
                                }
                            })
                            .show();
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
                new SweetAlertDialog(EnterNewPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText("Please Check Your Connectivity")
                        .show();
                Toast.makeText(EnterNewPasswordActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDailogForError(String error) {
        new SweetAlertDialog(EnterNewPasswordActivity.this,SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Shriji Cabs")
                .setContentText(error)
                .show();
    }

    @Override
    public void onBackPressed() {

    }
}
