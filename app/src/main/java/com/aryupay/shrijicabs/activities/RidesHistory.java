package com.aryupay.shrijicabs.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import com.aryupay.shrijicabs.model.BookingHistoryModel;
import com.aryupay.shrijicabs.model.model.NotificationData;
import com.aryupay.shrijicabs.model.model.NotificationResponse;
import com.aryupay.shrijicabs.model.your_ride_history.YourRideHistoryModel;
import com.aryupay.shrijicabs.utils.Keys;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RidesHistory extends AppCompatActivity {

    public static final String TAG = RidesHistory.class.getSimpleName();
    //    @BindView(R.id.imageViewBackButton)
    ImageView imageViewBackButton;
    //    @BindView(R.id.recylerViewBookingHistory)
    RecyclerView recylerViewBookingHistory;
    ArrayList<BookingHistoryModel> bookingHistoryModelArrayList = new ArrayList<>();
    ShrijicabsUserSessionManager sessionManager;
    ApiInterface apiInterface;
    String token;
    private Context context;
    private View view;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rides_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        progressDialog = new ProgressDialog(RidesHistory.this);
        sessionManager = new ShrijicabsUserSessionManager(this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        token = user.get(Keys.KEY_TOKEN);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        recylerViewBookingHistory = findViewById(R.id.recylerViewBookingHistory);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recylerViewBookingHistory.setLayoutManager(layoutManager);
//        recylerViewBookingHistory.setAdapter(bookingHistoryRecylerViewAdapter);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getAllRide();
    }

    public void showLoader() {
        progressDialog.show();
    }

    public void hideLoader() {
        progressDialog.dismiss();
    }


    private void getAllRide() {


        showLoader();
        Call<YourRideHistoryModel> call = apiInterface.transactions("Bearer " + token);
        call.enqueue(new Callback<YourRideHistoryModel>() {
            @Override
            public void onResponse(Call<YourRideHistoryModel> call, Response<YourRideHistoryModel> response) {
                hideLoader();
                Log.d("all_not", "" + new Gson().toJson(response.body()));
                YourRideHistoryModel data = response.body();
                BookingHistoryRecylerViewAdapter bookingHistoryRecylerViewAdapter = new BookingHistoryRecylerViewAdapter(context, data);
                recylerViewBookingHistory.setAdapter(bookingHistoryRecylerViewAdapter);
                if (response.isSuccessful()) {


                } else {
                    Toast.makeText(RidesHistory.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<YourRideHistoryModel> call, Throwable t) {
                new SweetAlertDialog(RidesHistory.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Shriji Cabs")
                        .setContentText("Please Check Your Connectivity")
                        .show();
                Toast.makeText(RidesHistory.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public class BookingHistoryRecylerViewAdapter extends RecyclerView.Adapter<BookingHistoryRecylerViewAdapter.ViewHolder> {


        private Context context;
        private ArrayList<YourRideHistoryModel.Success> bookingHistoryModelArrayList;

        public BookingHistoryRecylerViewAdapter(Context context, ArrayList<YourRideHistoryModel.Success> bookingHistoryModelArrayList) {
            this.context = context;
            this.bookingHistoryModelArrayList = bookingHistoryModelArrayList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_booking_history, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            YourRideHistoryModel.Success data = bookingHistoryModelArrayList.get(position);

            if (data.getStatus().equals(1)) {
                holder.textViewStatus.setText("Completed");
                holder.textViewStatus.setTextColor(context.getResources().getColor(R.color.dark_greeen));
            } else {
                holder.textViewStatus.setText("Cancelled");
                holder.textViewStatus.setTextColor(context.getResources().getColor(R.color.red));
            }

            holder.textViewDate.setText(data.getCreatedAt());
//            holder.textViewSource.setText(data.getSuccess().get(position).getSource());
//            holder.textViewDestination.setText(data.getSuccess().get(position).get());
            holder.textViewAmount.setText(data.getAmount());
        }

        @Override
        public int getItemCount() {
            return bookingHistoryModelArrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView imageViewBooking;
            TextView textViewDate;
            //            TextView textViewSource;
//            TextView textViewDestination;
            TextView textViewAmount;
            TextView textViewStatus;

            public ViewHolder(View itemView) {
                super(itemView);
                imageViewBooking = itemView.findViewById(R.id.imageViewBooking);
                textViewDate = itemView.findViewById(R.id.textViewDate);
//                textViewSource = itemView.findViewById(R.id.textViewSource);
//                textViewDestination = itemView.findViewById(R.id.textViewDestination);
                textViewAmount = itemView.findViewById(R.id.textViewAmount);
                textViewStatus = itemView.findViewById(R.id.textViewStatus);

            }
        }
    }

}
