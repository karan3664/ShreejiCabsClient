package com.aryupay.shrijicabs.activities;

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
import androidx.appcompat.widget.Toolbar;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.login.ForgotPasswordActivity;
import com.aryupay.shrijicabs.login.LoginActivity;
import com.aryupay.shrijicabs.login.OtpForgotPasswordActivity;
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

public class SettingsActivity extends AppCompatActivity {
    ShrijicabsUserSessionManager sessionManager;
    Button btnChangePassword;
    EditText etOldPassword, etNewPassword;
    String mobileno;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        sessionManager = new ShrijicabsUserSessionManager(SettingsActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        mobileno = user.get(Keys.KEY_PHONE_NUMBER);
        etOldPassword = (EditText) findViewById(R.id.etOldPassword);
        etNewPassword = (EditText) findViewById(R.id.etNewPassword);
        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sessionManager.logOutUser();
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.exit, R.anim.enter);
            }
        });

        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
      /*          Intent change_password=new Intent(SettingsActivity.this,LoginActivity.class);
                startActivity(change_password);*/
                if (validateIfAnyFieldsIsEmpty()) {
                    changepassword();
                }

            }
        });
    }

    private void changepassword() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Loading Please Wait .....");
        dialog.show();

        Map<String, String> map = new HashMap<>();
        map.put("mobile", mobileno);
        map.put("c_password", etOldPassword.getText().toString());
        map.put("password", etNewPassword.getText().toString());

        Call<JsonObject> call = apiInterface.resetpassword(map);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                dialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i("onVerify", response.body().toString());
                    String responseString = response.body().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(responseString);
                        String status = jsonObject.getString("status");
                        if (status.equals("success")) {
                            Toast.makeText(SettingsActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            Log.d("success_status", "" + status);
                            etOldPassword.setText("");
                            etNewPassword.setText("");
                        } else {
                            Toast.makeText(SettingsActivity.this, responseString, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
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
                new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText("Please Check Your Connectivity")
                        .show();
                Toast.makeText(SettingsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showDailogForError(String error) {
        new SweetAlertDialog(SettingsActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Shriji Cabs")
                .setContentText(error)
                .show();
    }

    private boolean validateIfAnyFieldsIsEmpty() {
        if (etOldPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please Enter Current Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etNewPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, "Please Enter New Password", Toast.LENGTH_SHORT).show();
            return false;
        } else if (etNewPassword.length() < 6) {
            Toast.makeText(this, "Please Enter The Six Digit Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}