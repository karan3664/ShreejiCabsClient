package com.aryupay.shrijicabs.interfaces;


import com.aryupay.shrijicabs.model.BookingResponse;
import com.aryupay.shrijicabs.model.LocationVendorModel;
import com.aryupay.shrijicabs.model.model.NotificationResponse;
import com.aryupay.shrijicabs.model.your_ride_history.YourRideHistoryModel;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @POST("register")
    Call<JsonObject> onRegister(@QueryMap Map<String, String> queryMap);

    @GET("submitregistration")
    Call<JsonObject> onSubmitRegistration(@Header("Authorization") String token);

    @POST("login")
    Call<JsonObject> onLogin(@QueryMap Map<String, String> queryMap);

    @GET("user")
    Call<JsonObject> getUser(@Header("Authorization") String token);

    @GET("cities")
    Call<JsonArray> getCities();

    @POST("otp")
    Call<JsonObject> onPasswordChange(@QueryMap HashMap<String, String> queryMap);

    @Multipart
    @POST("file")
    Call<JsonObject> uploadFile(@QueryMap Map<String, String> queryMap, @Part MultipartBody.Part file, @Header("Authorization") String token);

    @GET("files")
    Call<JsonObject> getFiles(@Header("Authorization") String token);

    @PUT("appinfo")
    Call<JsonObject> updateAppInfo(@QueryMap Map<String, String> queryMap, @Header("Authorization") String token);

    @PUT("user")
    Call<JsonObject> updateUser(@QueryMap Map<String, String> queryMap, @Header("Authorization") String token);

    @POST("reset")
    Call<JsonObject> newPassword(@QueryMap HashMap<String, String> queryMap);

    @POST("booking")
    Call<JsonObject> booking(@QueryMap Map<String, String> map, @Header("Authorization") String token);

    @POST("reset")
    Call<JsonObject> resetpassword(@QueryMap Map<String, String> map);

    @GET("location/{id}")
    Call<LocationVendorModel> LocationVendorModel(@Path("id") String bookid, @Header("Authorization") String token);

    @GET("notifications")
    Call<NotificationResponse> allnotification(@Header("Authorization") String token);

    @GET("transactions")
    Call<YourRideHistoryModel> transactions(@Header("Authorization") String token);

    @GET("bookings")
    Call<BookingResponse> getallBookings(@Header("Authorization") String token);

    @POST("transactions")
    Call<JsonObject> transactionsPayment(@QueryMap Map<String, String> map, @Header("Authorization") String token);

    @POST("feedback")
    Call<JsonObject> feedback(@QueryMap Map<String, String> map, @Header("Authorization") String token);


    @POST("rental")
    Call<JsonObject> rental(@QueryMap Map<String, String> map, @Header("Authorization") String token);

}
