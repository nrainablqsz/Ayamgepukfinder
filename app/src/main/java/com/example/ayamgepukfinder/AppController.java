package com.example.ayamgepukfinder;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AppController extends Application {

    private static AppController instance;
    private static SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("AyamGepukPrefs", Context.MODE_PRIVATE);

        // Initialize other components (database, analytics, etc.)
        initializeApp();
    }

    public static synchronized AppController getInstance() {
        return instance;
    }

    public static SharedPreferences getAppPreferences() {
        return sharedPreferences;
    }

    private void initializeApp() {
    }

    // Helper method to check if first launch
    public boolean isFirstLaunch() {
        return sharedPreferences.getBoolean("isFirstLaunch", true);
    }

    public void setFirstLaunchCompleted() {
        sharedPreferences.edit().putBoolean("isFirstLaunch", false).apply();
    }
}