package com.aryupay.shrijicabs.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.register.OtpActivity;
import com.aryupay.shrijicabs.utils.Keys;
import com.github.irvingryan.VerifyCodeView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OtpForgotPasswordActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        final String otp = getIntent().getStringExtra(Keys.OTP_FORGOT_PASSWORD_INTENT);
        VerifyCodeView verifyCodeView = (VerifyCodeView)findViewById(R.id.verify);
        verifyCodeView.setListener(new VerifyCodeView.OnTextChangListener() {
            @Override
            public void afterTextChanged(String text) {
                if(text.length() == Integer.parseInt("4"))
                    if(text.equals(otp)) {
                        Intent intent = new Intent(OtpForgotPasswordActivity.this,EnterNewPasswordActivity.class);
                        intent.putExtra(Keys.PHONE_NUMBER_OTP,getIntent().getStringExtra(Keys.PHONE_NUMBER_OTP));
                        finish();
                        startActivity(intent);
                    }else{
                        new SweetAlertDialog(OtpForgotPasswordActivity.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText("Invalid Code")
                                .setConfirmText("Ok")
                                .show();
                    }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
