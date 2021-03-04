package com.aryupay.shrijicabs.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.adapters.ShrijicabsUserSessionManager;
import com.aryupay.shrijicabs.interfaces.ApiClient;
import com.aryupay.shrijicabs.interfaces.ApiInterface;
import com.aryupay.shrijicabs.model.model.NotificationData;
import com.aryupay.shrijicabs.model.model.NotificationResponse;
import com.aryupay.shrijicabs.utils.Keys;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity {

    public static final String TAG = NotificationsActivity.class.getSimpleName();

    ImageView imageViewBackButton;
    RecyclerView recylerViewNotification;
    ShrijicabsUserSessionManager sessionManager;
    ApiInterface apiInterface;
    NotificationRecylerViewAdapter notificationRecylerViewAdapter;
    List<NotificationData> notificationList;
    RecyclerView.LayoutManager layoutManager;
    String pickup_lat, pickup_longt, drop_lat, drop_longt, f_name, l_name, date, amount, status, pick_add, drop_add, token;
    private Context context;
    private View view;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        sessionManager = new ShrijicabsUserSessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(Keys.KEY_TOKEN);
//        imageViewBackButton = findViewById(R.id.imageViewBackButton);
        recylerViewNotification = findViewById(R.id.recylerViewNotification);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                finish();
                onBackPressed();
            }

        });

        notificationList = new ArrayList<>();
        progressDialog = new ProgressDialog(NotificationsActivity.this);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        getAllNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void getAllNotification() {

        String tokens = "Bearer " + token;
        showLoader();
        Call<NotificationResponse> call = apiInterface.allnotification("Bearer " + token);
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {

                if (response.isSuccessful()) {
                    hideLoader();
                    Log.d("all_not", "" + new Gson().toJson(response.body()));
                    List<NotificationData> data = response.body().getSuccess();
                    for (NotificationData list : data) {
                        notificationList.add(new NotificationData(list.getId(), list.getTitle(), list.getDescription(), list.getUserId(), list.getCreatedAt(), list.getUpdatedAt()));
                    }
                    notificationRecylerViewAdapter = new NotificationRecylerViewAdapter(NotificationsActivity.this, notificationList);
                    layoutManager = new LinearLayoutManager(NotificationsActivity.this);
                    recylerViewNotification.setLayoutManager(layoutManager);
                    recylerViewNotification.setAdapter(notificationRecylerViewAdapter);
                    notificationRecylerViewAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(NotificationsActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                new SweetAlertDialog(NotificationsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText("Please Check Your Connectivity")
                        .show();
//                Toast.makeText(NotificationsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }


    public class NotificationRecylerViewAdapter extends RecyclerView.Adapter<NotificationRecylerViewAdapter.ViewHolder> {
        private Context context;
        private List<NotificationData> notificationModelArrayList;

        public NotificationRecylerViewAdapter(Context context, List<NotificationData> notificationModelArrayList) {
            this.context = context;
            this.notificationModelArrayList = notificationModelArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_notification, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            //    holder.textViewMessage.setText(notificationModelArrayList.get(position).get());
            // holder.textViewDescription.setText(notificationModelArrayList.get(position).getDescription());
            //     holder.textViewTime.setText(notificationModelArrayList.get(position).getCreatedAt());

            String val = notificationModelArrayList.get(position).getDescription();
            //   pickup_lat,pickup_longt,drop_lat,drop_longt,client_name,date,amount,status;
            try {
                // get JSONObject from JSON file
                JSONObject obj = new JSONObject(val);
                // fetch JSONObject named employee
                // get employee name and salary
                date = obj.getString("schedule_at");
                amount = obj.getString("amount");
                pickup_lat = obj.getString("latitude");
                pickup_longt = obj.getString("longitude");
                drop_lat = obj.getString("drop_latitude");
                drop_longt = obj.getString("drop_longitude");
                String clientprofile = obj.getString("client_profile");
                JSONObject data1 = new JSONObject(clientprofile);
                f_name = data1.getString("firstname");
                l_name = data1.getString("lastname");

                Log.d("log_data_client", "" + f_name + l_name);

                Log.d("log_data", "" + pickup_lat + pickup_longt + drop_lat + drop_longt);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            double pick_lat = Double.parseDouble(pickup_lat);
            double pick_longt = Double.parseDouble(pickup_longt);
            double dr_lat = Double.parseDouble(drop_lat);
            double dr_long = Double.parseDouble(drop_longt);


            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            List<Address> addresses1 = null;
            try {
                addresses = geocoder.getFromLocation(pick_lat, pick_longt, 1);
                addresses1 = geocoder.getFromLocation(dr_lat, dr_long, 1);

                pick_add = addresses.get(0).getAddressLine(0);
                drop_add = addresses1.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String zip = addresses.get(0).getPostalCode();
                String country = addresses.get(0).getCountryName();

                Log.d("my_add", "" + drop_add);
                holder.tvPickup.setText(pick_add);
                holder.tvDropLoc.setText(drop_add);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException illegalArgumentException) {
                Log.e("errorMessage", "errorMessage " + ". " +
                        "Latitude = ");
            }

            holder.textViewTitle.setText(notificationModelArrayList.get(position).getTitle());
            holder.tvDate.setText(date);
            holder.tvClientName.setText(f_name + " " + l_name);
            holder.tvAmount.setText(amount);
        }

        @Override
        public int getItemCount() {
            return notificationModelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewTitle;
            TextView tvDate;
            TextView tvPickup;
            TextView tvDropLoc;
            TextView tvClientName;
            TextView tvAmount;

            public ViewHolder(View itemView) {
                super(itemView);
//                ButterKnife.bind(this, itemView);
                textViewTitle = itemView.findViewById(R.id.textViewTitle);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvPickup = itemView.findViewById(R.id.tvPickup);
                tvDropLoc = itemView.findViewById(R.id.tvDropLoc);
                tvClientName = itemView.findViewById(R.id.tvClientName);
                tvAmount = itemView.findViewById(R.id.tvAmount);

            }

        }
    }

}
