package com.aryupay.shrijicabs.payumoney;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.multidex.MultiDex;

public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {
    private static boolean isActive;
    AppEnvironment appEnvironment;

    public static boolean isActivityVisible() {
        return isActive;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        isActive = true;
    }

    @Override
    public void onActivityPaused(Activity activity) {
        isActive = false;
    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onCreate() {
        super.onCreate();
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/GARABD.TTF")
//                .setFontAttrId(R.attr.fontPath)
//                .build());
        registerActivityLifecycleCallbacks(this);
        appEnvironment = AppEnvironment.SANDBOX;
    }

    public AppEnvironment getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(AppEnvironment appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

}
