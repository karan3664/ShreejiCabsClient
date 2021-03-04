package com.aryupay.shrijicabs.alertDialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Rating;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.activities.MapsActivity;
import com.aryupay.shrijicabs.activities.PaymentActivity;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.utils.Keys;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackDialog extends DialogFragment {

    RatingBar ratingBar;
    EditText edtSubject, edtFeedback;
    Button btnFeedBackSubmit;
    ShrijicabsUserSessionManager sessionManager;
    String token, Booking_ID;
    ApiInterface apiInterface;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.feedback_dialog);

        sessionManager = new ShrijicabsUserSessionManager(getContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(Keys.KEY_TOKEN);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        ratingBar = dialog.findViewById(R.id.ratingBar);
        edtSubject = dialog.findViewById(R.id.edtSubject);
        edtFeedback = dialog.findViewById(R.id.edtFeedback);
        btnFeedBackSubmit = dialog.findViewById(R.id.btnFeedBackSubmit);

        btnFeedBackSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = new HashMap<>();
                map.put("subject", edtSubject.getText().toString());
                map.put("description", edtFeedback.getText().toString());
                map.put("rating", ratingBar.getRating() + "");

                Log.e("onVerify", map + "");

                Call<JsonObject> call = apiInterface.feedback(map, "Bearer " + token);
                call.enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                        if (response.isSuccessful()) {
//                                    Toast.makeText(PaymentActivity.this, response.body().get("message").getAsString(), Toast.LENGTH_SHORT).show();

                            Toast.makeText(getContext(), "Thank you for feedback...", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), MapsActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        } else {
                            try {
                                JsonObject object = new JsonParser().parse(response.errorBody().string()).getAsJsonObject();
                                String error = object.get("error").toString();
                                Toast.makeText(getContext(), error + "", Toast.LENGTH_SHORT).show();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<JsonObject> call, Throwable t) {
                        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Shriji Cabs")
                                .setContentText("Please Check Your Connectivity")
                                .show();
                        Toast.makeText(getContext(), t.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


        return dialog;
    }
}
