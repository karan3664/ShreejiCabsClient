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
import com.aryupay.shrijicabs.model.BookingData;
import com.aryupay.shrijicabs.model.BookingResponse;
import com.aryupay.shrijicabs.utils.Keys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourBookingsActivity extends AppCompatActivity {
    ShrijicabsUserSessionManager sessionManager;
    YourBookingAdapter yourBookingAdapter;
    List<BookingData> bookingList;
    ApiInterface apiInterface;
    RecyclerView.LayoutManager layoutManager;
    //    @BindView(R.id.rvYourBookingList
    RecyclerView rvYourBookingList;
    String pick_add, drop_add, token;
    //    @BindView(R.id.imageViewBackButton)
    ImageView imageViewBackButton;
    private Context context;
    private View view;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_bookings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        rvYourBookingList = findViewById(R.id.rvYourBookingList);
        sessionManager = new ShrijicabsUserSessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(Keys.KEY_TOKEN);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });

        bookingList = new ArrayList<>();
        progressDialog = new ProgressDialog(YourBookingsActivity.this);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        getBookingList();

    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }

    public void getBookingList() {
//        String token = "Bearer " + sharedData.getToken();
        showLoader();
        Call<BookingResponse> call = apiInterface.getallBookings("Bearer " + token);
        call.enqueue(new Callback<BookingResponse>() {
            @Override
            public void onResponse(Call<BookingResponse> call, Response<BookingResponse> response) {
                hideLoader();
                if (response.isSuccessful()) {
                    Log.d("getbooking_response", "" + response.body().getSuccess());
                    List<BookingData> data = response.body().getSuccess();
                    for (BookingData list : data) {
                        bookingList.add(new BookingData(list.getId(), list.getClientId(), list.getVendorId(),
                                list.getVendorCategory(), list.getOtp(), list.getBookingStatusId(),
                                list.getAmount(), list.getScheduleAt(), list.getLatitude(), list.getLongitude(),
                                list.getDropLatitude(), list.getDropLongitude(), list.getLandmark(), list.getRemarks(),
                                list.getStartsAt(), list.getEndsAt(), list.getCreatedAt(), list.getUpdatedAt(),
                                list.getWay(), list.getClient(), list.getVendor()));
                    }
                    yourBookingAdapter = new YourBookingAdapter(YourBookingsActivity.this, bookingList);
                    layoutManager = new LinearLayoutManager(YourBookingsActivity.this);
                    rvYourBookingList.setLayoutManager(layoutManager);
                    rvYourBookingList.setAdapter(yourBookingAdapter);
                    yourBookingAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(YourBookingsActivity.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                    Log.d("Booking_error", "" + response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<BookingResponse> call, Throwable t) {
                hideLoader();
                new SweetAlertDialog(YourBookingsActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText("Please Check Your Connectivity")
                        .show();
                Toast.makeText(YourBookingsActivity.this, t.toString(), Toast.LENGTH_SHORT).show();
                Log.d("error_log", "" + t.toString());
            }
        });
    }


    public class YourBookingAdapter extends RecyclerView.Adapter<YourBookingAdapter.ViewHolder> {
        private Context context;
        private List<BookingData> notificationModelArrayList;

        public YourBookingAdapter(Context context, List<BookingData> notificationModelArrayList) {
            this.context = context;
            this.notificationModelArrayList = notificationModelArrayList;
        }

        @Override
        public YourBookingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_your_booking_list, parent, false);
            return new YourBookingAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(YourBookingAdapter.ViewHolder holder, int position) {
            holder.tvDateTime.setText(notificationModelArrayList.get(position).getCreatedAt());
            try {
                holder.tvClientName.setText(notificationModelArrayList.get(position).getClient().getUser().getFirstname() + " " + notificationModelArrayList.get(position).getClient().getUser().getLastname());
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.tvAmount.setText(notificationModelArrayList.get(position).getAmount());

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());
            List<Address> addresses = null;
            List<Address> addresses1 = null;
            try {
                addresses = geocoder.getFromLocation(notificationModelArrayList.get(position).getLatitude(), notificationModelArrayList.get(position).getLongitude(), 1);
                addresses1 = geocoder.getFromLocation(notificationModelArrayList.get(position).getDropLatitude(), notificationModelArrayList.get(position).getDropLongitude(), 1);

                pick_add = addresses.get(0).getAddressLine(0);
                drop_add = addresses1.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String zip = addresses.get(0).getPostalCode();
                String country = addresses.get(0).getCountryName();

                Log.d("my_add", "" + drop_add);
                holder.tvPickupLocation.setText(pick_add);
                holder.tvDropLocation.setText(drop_add);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException illegalArgumentException) {
                Log.e("errorMessage", "errorMessage " + ". " +
                        "Latitude = ");
            }

        }

        @Override
        public int getItemCount() {
            return notificationModelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDateTime;
            TextView tvClientName;
            TextView tvPickupLocation;
            TextView tvDropLocation;
            TextView tvAmount;

            public ViewHolder(View itemView) {
                super(itemView);
                tvDateTime = itemView.findViewById(R.id.tvDateTime);
                tvClientName = itemView.findViewById(R.id.tvClientName);
                tvPickupLocation = itemView.findViewById(R.id.tvPickupLocation);
                tvDropLocation = itemView.findViewById(R.id.tvDropLocation);
                tvAmount = itemView.findViewById(R.id.tvAmount);

            }
        }
    }
}
