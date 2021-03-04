package com.aryupay.shrijicabs.utils;

import android.app.Application;

import com.aryupay.shrijicabs.payumoney.AppEnvironment;
import com.google.firebase.messaging.FirebaseMessaging;


/**
 * Created by Ravi Thakur on 09,September,2019
 * AryuPay Technologies,
 * India.
 */
public class ShreejiApplicationClass extends Application {
    AppEnvironment appEnvironment;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseMessaging.getInstance().subscribeToTopic("ravi");
        appEnvironment = AppEnvironment.SANDBOX;
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

}
