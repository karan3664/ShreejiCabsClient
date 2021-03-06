package com.aryupay.shrijicabs.activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.alertDialog.FeedbackDialog;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.login.EnterNewPasswordActivity;
import com.aryupay.shrijicabs.login.LoginActivity;
import com.aryupay.shrijicabs.model.model.NotificationData;
import com.aryupay.shrijicabs.model.model.NotificationResponse;
import com.aryupay.shrijicabs.payumoney.AppEnvironment;
import com.aryupay.shrijicabs.payumoney.AppPreference;

import com.aryupay.shrijicabs.utils.Keys;
import com.aryupay.shrijicabs.utils.ShreejiApplicationClass;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    SharedPreferences preferences;
    String token, amount;
    ShrijicabsUserSessionManager sessionManager;
    TextView textTotalAmountPay;
    LinearLayout llPayuMoney, llCash;
    Button feedback;
    String txnID, Booking_ID;
    ApiInterface apiInterface;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private AppPreference mAppPreference;
    private SharedPreferences settings;

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte[] messageDigest = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        settings = this.getSharedPreferences("settings", MODE_PRIVATE);

        preferences = this.getSharedPreferences("user_info", MODE_PRIVATE);
        sessionManager = new ShrijicabsUserSessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(Keys.KEY_TOKEN);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        textTotalAmountPay = findViewById(R.id.textTotalAmountPay);
        llPayuMoney = findViewById(R.id.llPayuMoney);
        llCash = findViewById(R.id.llCash);
        feedback = findViewById(R.id.feedback);

        if (settings.getBoolean("is_prod_env", false)) {
            ((ShreejiApplicationClass) PaymentActivity.this.getApplication()).setAppEnvironment(AppEnvironment.PRODUCTION);
//            radio_btn_production.setChecked(true);
        } else {
            ((ShreejiApplicationClass) PaymentActivity.this.getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);
//            radio_btn_sandbox.setChecked(true);
        }

//        selectSandBoxEnv();

        setupCitrusConfigs();
        amount = preferences.getString("amount", "");
        Booking_ID = preferences.getString("id", "");

        textTotalAmountPay.setText("Total Pay" + "\n " + amount);


        llPayuMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double amt = Double.parseDouble(amount);
                launchPayUMoneyFlow(amt, "9893707894", "Test");
            }
        });

        llCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> map = new HashMap<>();
                map.put("status", "in-progress");
                map.put("amount", amount);
                map.put("payment_gateway_transaction_id", "0");
                map.put("booking_id", Booking_ID);
                Log.e("onVerify", map + "");

                Call<JsonObject> call = apiInterface.transactionsPayment(map, "Bearer " + token);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
                            feedback.setEnabled(true);
                            feedback.setBackgroundResource(R.color.mapboxYellow);

//                                    Toast.makeText(PaymentActivity.this, response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                            feedback.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    FeedbackDialog mFragment = new FeedbackDialog();
                                    Bundle mBundle = new Bundle();
                                    mFragment.setArguments(mBundle);
                                    mFragment.show(getSupportFragmentManager(), "FeedbackDialogFragment");
                                }
                            });
                        } else {
                            try {
                                JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                                String error = object.get("error").toString();


                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText("Please Check Your Connectivity")
                                .show();
                        Toast.makeText(PaymentActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    private void launchPayUMoneyFlow(double amount, String contact, String name) {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
//        payUmoneyConfig.setDoneButtonText(((EditText) findViewById(R.id.status_page_et)).getText().toString());

        //Use this to set your custom title for the activity
//        payUmoneyConfig.setPayUmoneyActivityTitle(((EditText) findViewById(R.id.activity_title_et)).getText().toString());

        payUmoneyConfig.disableExitConfirmation(false);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

//        double amount = 0;
//        try {
//            amount = Double.parseDouble("10");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String txnId = System.currentTimeMillis() + "";
        //String txnId = "TXNID720431525261327973";
        String phone = contact;
        String productName = "Premium Membership";
        String firstName = name;
        String email = "abc@abc.com";
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((ShreejiApplicationClass) PaymentActivity.this.getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
//                generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            if (AppPreference.selectedTheme != -1) {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PaymentActivity.this, AppPreference.selectedTheme, true);
            } else {
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PaymentActivity.this, R.style.AppTheme_default, true);
            }

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("ErrroPayu==>", e.getMessage() + "");

//            payNowButton.setEnabled(true);
        }
    }

    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((ShreejiApplicationClass) PaymentActivity.this.getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    /**
     * This method generates hash from server.
     * <p>
     * //     * @param paymentParam payments params used for hash generation
     */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                    String payuResponse = transactionResponse.getPayuResponse();

                    try {
                        JSONObject jsonObject = new JSONObject(payuResponse);
                        JSONObject result = new JSONObject(String.valueOf(jsonObject.get("result")));
                        Log.e("PAYU=>", result.getString("txnid") + "");
                        txnID = result.getString("txnid");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (txnID != null) {

                        feedback.setEnabled(true);
                        feedback.setBackgroundResource(R.color.mapboxYellow);

                        HashMap<String, String> map = new HashMap<>();
                        map.put("status", "paid");
                        map.put("amount", amount);
                        map.put("payment_gateway_transaction_id", txnID);
                        map.put("booking_id", Booking_ID);
                        Log.e("onVerify", map + "");

                        Call<JsonObject> call = apiInterface.transactionsPayment(map, "Bearer " + token);
                        call.enqueue(new Callback<JsonObject>() {
                            @Override
                            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                                if (response.isSuccessful()) {
//                                    Toast.makeText(PaymentActivity.this, response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();
                                    feedback.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            FeedbackDialog mFragment = new FeedbackDialog();
                                            Bundle mBundle = new Bundle();
                                            mFragment.setArguments(mBundle);
                                            mFragment.show(getSupportFragmentManager(), "FeedbackDialogFragment");
                                        }
                                    });
                                } else {
                                    try {
                                        JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                                        String error = object.get("error").toString();


                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<JsonObject> call, Throwable t) {
                                new SweetAlertDialog(PaymentActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Shriji Cabs")
                                        .setContentText("Please Check Your Connectivity")
                                        .show();
                                Toast.makeText(PaymentActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                } else {
                    //Failure Transaction
                }

                // Response from Payumoney
           /*     String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();*/
            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d("TAG", "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d("TAG", "Both objects are null!");
            }
        }
    }

    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1) : postParamsBuffer.toString();

//        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }

    private void setupCitrusConfigs() {
        AppEnvironment appEnvironment = ((ShreejiApplicationClass) PaymentActivity.this.getApplication()).getAppEnvironment();
        if (appEnvironment == AppEnvironment.PRODUCTION) {
//            Toast.makeText(PaymentActivity.this, "Environment Set to Production", Toast.LENGTH_SHORT).show();
        } else {
//            Toast.makeText(PaymentActivity.this, "Environment Set to SandBox", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This AsyncTask generates hash from server.
     */
    @SuppressLint("StaticFieldLeak")
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(PaymentActivity.this);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
//server side API returning the payment_hash
                URL url = new URL("http://skillforce.co.in/api//test");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes(StandardCharsets.UTF_8);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    System.out.println("KEY >> " + key);
                    System.out.println("VALUE >> " + response.getString(key));
                    switch (key) {

                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;

                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            progressDialog.dismiss();

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(PaymentActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, PaymentActivity.this, R.style.AppTheme_default, true);
            }
        }
    }

}
