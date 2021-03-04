package com.aryupay.shrijicabs.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.activities.MapsActivity;
import com.aryupay.shrijicabs.activities.PaymentActivity;
import com.aryupay.shrijicabs.activities.SourceDestinationOnMapActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Map;

public class MyFirebaseMessage extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    String CHANNEL_ID = "client_app_01";
    CharSequence name = "client_app";

    public static final int ID_BIG_NOTIFICATION = 123;
    public int ID_SMALL_NOTIFICATION = 1;

    SharedPreferences preferences;
    public static final String PREFS_NAME = "user_info";

    String vendroName, vehicleName, VehicalModel, VehicalNumber, booking_status_id, Booking_ID, OTP, amount;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "@@@ Data Payload: " + (remoteMessage.getData().toString()));
            try {

                ID_SMALL_NOTIFICATION = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                Map<String, String> pushdata = remoteMessage.getData();
                Log.d(TAG, "fcm_data" + remoteMessage.getData());

                String drop_lat = pushdata.get("drop_latitude");
                String drop_long = pushdata.get("drop_longitude");
                String pick_lat = pushdata.get("latitude");
                String pic_long = pushdata.get("longitude");
                String vendor = pushdata.get("vendor");
                Booking_ID = pushdata.get("id");
                booking_status_id = pushdata.get("booking_status_id");
                OTP = pushdata.get("otp");
                amount = pushdata.get("amount");
                Log.d("drop_location", "" + Booking_ID + "-" + OTP + "-" + booking_status_id + " -" + pic_long);
                Log.d("vendor", "" + vendor);
                if (vendor != null) {
                    try {
                        JSONObject data1 = new JSONObject(vendor);

                        vehicleName = data1.getString("vehicle_company");
                        VehicalModel = data1.getString("vehicle_model");
                        VehicalNumber = data1.getString("bank_account_number");
                        JSONObject data2 = new JSONObject(String.valueOf(data1.get("user")));

                        vendroName = data2.getString("firstname");

//                    Log.d("fn", "" + fn + mobileno + gender);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                // Writing data to SharedPreferences
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("drop_let", drop_lat);
                editor.putString("drop_long", drop_long);
                editor.putString("pick_lat", pick_lat);
                editor.putString("pic_long", pic_long);
                editor.putString("vendroName", vendroName);
                editor.putString("vehicleName", vehicleName);
                editor.putString("VehicalModel", VehicalModel);
                editor.putString("VehicalNumber", VehicalNumber);
                editor.putString("id", Booking_ID);
                editor.putString("otp", OTP);
                editor.putString("booking_status_id", booking_status_id);
                editor.putString("amount", amount);
                editor.commit();
                Log.d("user_data_store", "" + editor);


                sendPushNotification(pushdata);
                if (booking_status_id != null) {
                    if (booking_status_id.matches("4")) {
                        Intent intent = new Intent(this, SourceDestinationOnMapActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
//
                    } else if (booking_status_id.matches("5")) {
                        Intent intent = new Intent(this, PaymentActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
//
                    }
                }


            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());

            }
        } else {
            Log.e(TAG, "@@@ Data Payload: " + (remoteMessage.getNotification().toString()));

            try {

                ID_SMALL_NOTIFICATION = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);

                RemoteMessage.Notification notification = remoteMessage.getNotification();
                sendPushNotification(notification);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());

            }
        }
    }

    private void sendPushNotification(RemoteMessage.Notification notification) {
        try {
            String title = notification.getTitle();
            String message = notification.getBody();
            //creating an intent for the notification

            /*if (booking_status_id != null) {
                if (booking_status_id.matches("4")) {
                    Intent intent = new Intent(this, SourceDestinationOnMapActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                    showSmallNotification(title, message, intent);

                }
                else if (booking_status_id.matches("5")) {
                    Intent intent = new Intent(this, PaymentActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
                    showSmallNotification(title, message, intent);
//
                }
                else {


                }
            }*/
            Intent intent = null;
            intent = new Intent(this, MapsActivity.class);
            showSmallNotification(title, message, intent);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(Map<String, String> json) {
        try {
            String title = json.get("title");
            String message = json.get("body");
            Log.d("Display_msg", "" + title + message);

            if (json.get(booking_status_id) != null) {


                if (json.get(booking_status_id).matches("4")) {
                    Intent intent = new Intent(this, SourceDestinationOnMapActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    showSmallNotification(title, message, intent);
//
                } else if (json.get(booking_status_id).matches("5")) {
                    Intent intent = new Intent(this, PaymentActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    showSmallNotification(title, message, intent);
//
                } else {
                    //parsing json data

                    //creating an intent for the notification

                }
            }
            Intent intent = null;

            intent = new Intent(this, MapsActivity.class);
            if (intent != null) {
             /*   Constant.addFromNotification(intent);
                intent.putExtra("backPressed", false);*/

                showSmallNotification(title, message, intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }


    //the method will show a small notification
    //parameters are title for message title, message for message text and an intent that will open
    //when you will tap on the notification
    public void showSmallNotification(String title, String message, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);

            if (notificationManager != null) {

                notificationManager.createNotificationChannel(mChannel);
            }

        }

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());

        // stackBuilder.addParentStack(DashboardActivity.class);
        stackBuilder.addNextIntent(intent);


        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        Notification notification;
        notification = mBuilder
                .setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setContentText(message) //sub title
                .setColor(getApplicationContext().getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                .setDefaults(Notification.FLAG_AUTO_CANCEL | Notification.DEFAULT_LIGHTS |
                        Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(),
                        R.mipmap.ic_launcher_round))

                .build();

        if (notificationManager != null) {
            notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
        }
    }

    //the method will show a big notification with an image
    //parameters are title for message title, message for message text, url of the big image and an intent that will open
    //when you will tap on the notification
    public void showBigNotification(String title, String message, String url, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(true);

            if (notificationManager != null) {

                notificationManager.createNotificationChannel(mChannel);
            }

        }

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        ID_BIG_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(getBitmapFromURL(url));
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        Notification notification;
        notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle(title)
                .setStyle(bigPictureStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.mipmap.ic_launcher))
                .setContentText(message)
                .build();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        notificationManager.notify(ID_BIG_NOTIFICATION, notification);
    }

    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}