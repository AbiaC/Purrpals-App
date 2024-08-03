package com.example.purrpalsapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalDataManager {
    private static final String PREFS_NAME = "user_details";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "pass";
    private static final String KEY_DOB = "dob";
    private static final String KEY_AGE = "age";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_PHONE_NUMBER = "phone_number";

    private final SharedPreferences prefs;

    public LocalDataManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Save data
    public void saveName(String username) {
        prefs.edit().putString(KEY_NAME, username).apply();
    }
    public void saveUserDob(String username) {
        prefs.edit().putString(KEY_DOB, username).apply();
    }
    public void saveUserPass(String username) {
        prefs.edit().putString(KEY_PASS, username).apply();
    }

    public void saveEmail(String email) {
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    public void saveAge(int age) {
        prefs.edit().putInt(KEY_AGE, age).apply();
    }

    public void saveIsLoggedIn(boolean isLoggedIn) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }


    // Get data
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }
    public String getPass() {
        return prefs.getString(KEY_PASS, "");
    }

    public int getAge() {
        return prefs.getInt(KEY_AGE, 0);
    }
    public String getDob() {
        return prefs.getString(KEY_DOB, "");
    }

    public boolean getIsLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void savePhoneNumber(String phoneNumber) {
        prefs.edit().putString(KEY_PHONE_NUMBER, phoneNumber).commit();
    }

    public String getPhoneNumber() {
        return prefs.getString(KEY_PHONE_NUMBER, "");
    }


}