package com.aryupay.shrijicabs.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.network.Apis;

public class AboutUsActivity extends AppCompatActivity {
    WebView webviewAboutUs;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading Please Wait.....");
        progressDialog.show();
        webviewAboutUs = (WebView)findViewById(R.id.webview_about_us);
        webviewAboutUs.setWebViewClient(new WebViewClient());
        webviewAboutUs.loadUrl(Apis.ABOUT_US_URL);
    }
    public class WebViewClient extends android.webkit.WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AboutUsActivity.this,MapsActivity.class);
        finish();
        startActivity(intent);
        overridePendingTransition(R.anim.exit, R.anim.enter);
    }
}
