package com.aryupay.shrijicabs.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.utils.Keys;

import java.util.HashMap;

public class ReferAndEarnActivity extends AppCompatActivity {
    ShrijicabsUserSessionManager sessionManager;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer_and_earn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sessionManager = new ShrijicabsUserSessionManager(ReferAndEarnActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        TextView userName = (TextView)findViewById(R.id.userName);
        ImageView whatsapp = (ImageView)findViewById(R.id.whatsapp);
        ImageView message = (ImageView)findViewById(R.id.message);
        ImageView fb = (ImageView)findViewById(R.id.fb);
        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "bizitatech.com");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }catch (Exception e){
                    e.printStackTrace();
                    String url = "https://play.google.com/store/apps/details?id=com.whatsapp&hl=en_IN";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:"));
                sendIntent.putExtra("sms_body", "bizitatech.com");
                startActivity(sendIntent);
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.facebook.katana");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "hi i am hemanth sql http://www.bizitatech.com/");
                    sendIntent.setType("text/plain");
                    startActivity(Intent.createChooser(sendIntent, "Share with"));
                }catch (Exception e){
                    e.printStackTrace();
                    String url = "https://play.google.com/store/apps/details?id=com.facebook.katana&hl=en_IN";
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            }
        });
        userName.setText(user.get(Keys.KEY_NAME));
    }
}
