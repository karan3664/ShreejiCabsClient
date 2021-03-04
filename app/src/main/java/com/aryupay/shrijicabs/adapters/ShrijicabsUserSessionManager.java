package com.aryupay.shrijicabs.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.aryupay.shrijicabs.R;
import com.aryupay.shrijicabs.activities.SplashActivity;
import com.aryupay.shrijicabs.login.LoginActivity;
import com.aryupay.shrijicabs.utils.Keys;

import java.util.HashMap;

public class ShrijicabsUserSessionManager {
    SharedPreferences pref; // shared preferences reference
    SharedPreferences.Editor editor; //editor reference for shared preferences
    Context _context,ctx;
    int PRIVATE_MODE = 0;

    public ShrijicabsUserSessionManager(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(Keys.PREFER_NAME,PRIVATE_MODE);
    }
    // create login session
    public void createUserLoginSession(String name,String mobile,String token,String email,Context ctx){
        this.ctx = ctx; // getting context from main activity
        editor = ctx.getSharedPreferences(Keys.PREFER_NAME,PRIVATE_MODE).edit(); // create shared preferences using instance
        editor.putBoolean(Keys.LOGIN_STATUS,true);         //storing login value as true
        editor.putString(Keys.KEY_NAME,name);
        editor.putString(Keys.KEY_PHONE_NUMBER,mobile);
        editor.putString(Keys.KEY_TOKEN,token);
        editor.putString(Keys.KEY_EMAIL,email);
        editor.apply(); // commit changes
    }
    public void updateProfileToSp(String prifilePic,Context context){
        editor = context.getSharedPreferences(Keys.PREFER_NAME,PRIVATE_MODE).edit();
        editor.putString(Keys.KEY_PROFILE_IMAGE,prifilePic);
        editor.apply();
    }

    /*
     * checkLogin method will checks the user login status
     * if it is false the Main activity is called
     * else Home activity is called
     * */
    public boolean checKLogin(){
        //check login status
        if(!this.isUserLoggedIn()){
            // user is not logged in redirect to login activity
            Intent intent = new Intent(_context, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(intent);
            return true; // if user is not logged in it returns true else returns false
        }
        return false;
    }


    public boolean isUserLoggedIn() {
        return pref.getBoolean(Keys.LOGIN_STATUS, false);
    }
    /*
    get stored session data
     */

    public HashMap<String,String> getUserDetails(){
        // using hashmap to store login credentials
        HashMap<String,String> user = new HashMap<String, String>();
        user.put(Keys.KEY_NAME,pref.getString(Keys.KEY_NAME,""));
        user.put(Keys.KEY_PHONE_NUMBER,pref.getString(Keys.KEY_PHONE_NUMBER,""));
        user.put(Keys.KEY_TOKEN,pref.getString(Keys.KEY_TOKEN,""));
        user.put(Keys.KEY_EMAIL,pref.getString(Keys.KEY_EMAIL,""));
        user.put(Keys.KEY_PROFILE_IMAGE,pref.getString(Keys.KEY_PROFILE_IMAGE,""));
        return user; // returning user
    }

    /*
    once user pressed logogut will clear session details
     */

    public void logOutUser(){
        editor = _context.getSharedPreferences(Keys.PREFER_NAME,PRIVATE_MODE).edit();
        //clearing shared preferences
        editor.clear();
        editor.commit();
    }
}