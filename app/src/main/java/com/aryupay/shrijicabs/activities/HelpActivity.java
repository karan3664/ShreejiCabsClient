package com.aryupay.shrijicabs.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aryupay.shrijicabs.R;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String TAG = HelpActivity.class.getSimpleName();
    private static final int CALL_REQUEST = 1337;
    ImageView imageViewCall;
    TextView textViewCall;
    LinearLayout relativeLayoutCall;
    ImageView imageViewEmail;
    TextView textViewEmail;
    LinearLayout relativeLayoutEmail;
    ImageView imageViewMsg;
    TextView textViewMsg;
    LinearLayout relativeLayoutMessageMe;
    ImageView imageViewWhatsUp;
    TextView textViewWhatsUp;
    LinearLayout relativeLayoutWhatsUs;
    private Context context;
    private View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        context = this;
        imageViewCall = findViewById(R.id.imageViewCall);
        textViewCall = findViewById(R.id.textViewCall);
        relativeLayoutCall = findViewById(R.id.relativeLayoutCall);

        imageViewEmail = findViewById(R.id.imageViewEmail);
        textViewEmail = findViewById(R.id.textViewEmail);
        relativeLayoutEmail = findViewById(R.id.relativeLayoutEmail);

        imageViewMsg = findViewById(R.id.imageViewMsg);
        textViewMsg = findViewById(R.id.textViewMsg);
        relativeLayoutMessageMe = findViewById(R.id.relativeLayoutMessageMe);

        imageViewWhatsUp = findViewById(R.id.imageViewWhatsUp);
        relativeLayoutWhatsUs = findViewById(R.id.relativeLayoutWhatsUs);
        textViewWhatsUp = findViewById(R.id.textViewWhatsUp);

        relativeLayoutCall.setOnClickListener(this);
        relativeLayoutEmail.setOnClickListener(this);
        relativeLayoutMessageMe.setOnClickListener(this);
        relativeLayoutWhatsUs.setOnClickListener(this);


    }

    private void sendMsg(String mobileNumber) {
        Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address", mobileNumber);
        smsIntent.putExtra("sms_body", "Hello I have Problem Please contact me");
        startActivity(smsIntent);
    }


    private void openWhatsApp() {
        if (!isWhatsaapClientInstalled(context)) {
            goToMarketForWhats(context);
            return;
        }

        Uri uri = Uri.parse("smsto:" + textViewWhatsUp.getText().toString().trim());
        Intent i = new Intent(Intent.ACTION_SENDTO, uri);
        i.putExtra("sms_body", "Hello whatsaap");
        i.setPackage("com.whatsapp");
        try {
            startActivity(i);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(context, "Opps not able to connect", Toast.LENGTH_SHORT).show();
        }

    }

    public void goToMarketForWhats(Context myContext) {
        Uri marketUri = Uri.parse("market://details?id=com.whatsapp");
        Intent myIntent = new Intent(Intent.ACTION_VIEW, marketUri);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        myContext.startActivity(myIntent);
    }


    public boolean isWhatsaapClientInstalled(Context myContext) {
        PackageManager myPackageMgr = myContext.getPackageManager();
        try {
            myPackageMgr.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            return (false);
        }
        return (true);
    }


    private void sendMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] recipients = {"info@shriji.com"};
        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Shri Ji cab Vendor Care");
        intent.putExtra(Intent.EXTRA_TEXT, "Body of the content here...");
        intent.setType("text/html");
        intent.setPackage("com.google.android.gm");
        startActivity(Intent.createChooser(intent, "Send mail"));
    }

    private void makeCall(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (checkCameraPermission()) {
            startActivity(callIntent);
        } else {
            requestCallPermission();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CALL_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    makeCall("8602830015");
                } else {
                    makeCall("8602830015");
                }
                return;
            }
        }
    }


    private boolean checkCameraPermission() {
        int result1 = ContextCompat.checkSelfPermission(HelpActivity.this, Manifest.permission.CALL_PHONE);
        return result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCallPermission() {
        ActivityCompat.requestPermissions(HelpActivity.this, new String[]{Manifest.permission.CALL_PHONE}, CALL_REQUEST);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.relativeLayoutCall:
                makeCall("458796321");
                break;
            case R.id.relativeLayoutEmail:
                sendMail();
                break;
            case R.id.relativeLayoutMessageMe:
                sendMsg("1234569875");
                break;
            case R.id.relativeLayoutWhatsUs:
                openWhatsApp();
                break;
        }
    }
}