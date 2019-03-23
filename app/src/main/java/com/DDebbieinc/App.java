package com.DDebbieinc;

import android.support.multidex.MultiDexApplication;

import com.google.firebase.analytics.FirebaseAnalytics;

public class App extends MultiDexApplication {
    private static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    public static FirebaseAnalytics get(){
        return mFirebaseAnalytics;
    }
}
