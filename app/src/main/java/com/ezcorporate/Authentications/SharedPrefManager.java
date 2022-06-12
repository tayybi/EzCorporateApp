package com.ezcorporate.Authentications;

import android.content.Context;
import android.content.SharedPreferences;

public class
SharedPrefManager {

    public static final String SHARED_PREF_USER_LOGIN = "prefuserlogin";
    public static final String SHARED_PREF_COMPANY_INFO = "prefcompanyinfo";
    public static final String SHARED_PREF_VCC= "prefvcc";

    public static final String KEY_COMPANY_URL="companyurl";
    public static final String KEY_MAC = "mac";
    public static final String KEY_NAME = "uname";
    public static final String KEY_ID = "uid";
    public static final String KEY_VCC_CHECK_UNCHECK = "vcccheckuncheck";



    public static SharedPrefManager mInstance;
    public static Context mCtx;
    public SharedPreferences sharedPrefs;
    public SharedPreferences.Editor editor;

    public SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }


    public boolean isLogIn() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_USER_LOGIN, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_NAME, null) != null;
    }

    public boolean isUrlExist() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_COMPANY_INFO, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_COMPANY_URL, null) != null;
    }
    public boolean isCheckedVCC() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_VCC, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_VCC_CHECK_UNCHECK, null) != null;
    }

    public void logout() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_USER_LOGIN, Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
        editor.clear();
        editor.apply();
    }



    public void userDetail(String userId, String userName, String mac) {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_USER_LOGIN, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(KEY_NAME, userName);
        editor.putString(KEY_ID, userId);
        editor.putString(KEY_MAC, mac);
        editor.apply();
    }
    public void setVCCValue(String checkuncheck) {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_VCC, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(KEY_VCC_CHECK_UNCHECK, checkuncheck);
        editor.apply();
    }
    public String getVCCValue() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_VCC, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_VCC_CHECK_UNCHECK, "");
    }
    public void setCompanyInfo(String compUrl) {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_COMPANY_INFO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(KEY_COMPANY_URL, compUrl);
        editor.apply();
    }

    public String getUId() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_USER_LOGIN, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_ID, "");
    }

    public String getUName() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_USER_LOGIN, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_NAME, "");
    }

    public String getUMac() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_USER_LOGIN, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_MAC, "");
    }

    public String getUrl() {
        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_COMPANY_INFO, Context.MODE_PRIVATE);
        return sharedPrefs.getString(KEY_COMPANY_URL, "");
    }

    /////////////////////////////////////////////////




//    public void userLogin(String Email_ID, String Password,String type) {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_LOGIN, Context.MODE_PRIVATE);
//        editor = sharedPrefs.edit();
//        editor.putString(KEY_EMAIL, Email_ID);
//        editor.putString(KEY_Password, Password);
//        editor.putString(KEY_TYPE, type);
//        editor.apply();
//    }

//    public boolean isUserExist(String email, String pass) {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_REGISTER, Context.MODE_PRIVATE);
//        if (sharedPrefs.getString(KEY_EMAIL, "").equals(email) && sharedPrefs.getString(KEY_Password, "").equals(pass)) {
//            return true;
//        }
//        return false;
//    }

//    public void saveLocation(String value) {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_PROPERTY, Context.MODE_PRIVATE);
//        editor = sharedPrefs.edit();
//        editor.putString(KEY_LOCATION, value);
//        editor.apply();
//        editor.commit();
//    }
//
//    public boolean isLocationSelected() {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_PROPERTY, Context.MODE_PRIVATE);
//        if (sharedPrefs.getString(KEY_LOCATION, "").equals("")) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    public String getPropLocation() {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_PROPERTY, Context.MODE_PRIVATE);
//        return sharedPrefs.getString(KEY_LOCATION, "");
//    }
//
//    public void clearLocation() {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_PROPERTY, Context.MODE_PRIVATE);
//        editor = sharedPrefs.edit();
//        editor.remove(KEY_LOCATION);
//        editor.apply();
//        editor.commit();
//    }
//
//    public void clearLocationPref() {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_PROPERTY, Context.MODE_PRIVATE);
//        editor = sharedPrefs.edit();
//        editor.clear();
//        editor.commit();
//    }
//
//    public boolean isPropExist(String key) {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_FAV, Context.MODE_PRIVATE);
//        return sharedPrefs.contains(key);
//    }
//
//    public int getFavourite(String key) {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_FAV, Context.MODE_PRIVATE);
//        return sharedPrefs.getInt(key, -1);
//    }
//
//    public void saveFavourite(String key, int value) {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_FAV, Context.MODE_PRIVATE);
//        editor = sharedPrefs.edit();
//        editor.putInt(key, value);
//        editor.apply();
//    }
//
//    public void removeFavourite(String key) {
//        sharedPrefs = mCtx.getSharedPreferences(SHARED_PREF_FAV, Context.MODE_PRIVATE);
//        editor = sharedPrefs.edit();
//        editor.remove(key);
//        editor.apply();
//    }


}
