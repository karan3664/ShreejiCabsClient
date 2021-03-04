package com.aryupay.shrijicabs.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ShrijiCabsSingleton {
    private static ShrijiCabsSingleton mInstance;
    private RequestQueue mRequestQueue;

    private ShrijiCabsSingleton(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized ShrijiCabsSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ShrijiCabsSingleton(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }
}

