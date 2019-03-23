package com.DDebbieinc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.util.AppLogger;
import com.DDebbieinc.util.IOUtils;
import com.google.firebase.analytics.FirebaseAnalytics;


public class SplashActivity extends AppCompatActivity {

    UserDetailsPojo userDetailsPojo;
    IOUtils ioUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppLogger.generateLog("app_open");

        ioUtils = new IOUtils(SplashActivity.this);
        userDetailsPojo = ioUtils.getUser();



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try{
                    String name = userDetailsPojo.getCustomerName();

                    Log.v("Name", userDetailsPojo.getCustomerName());

                    if (!name.equals("")){
                        Intent intent = new Intent(SplashActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }catch (NullPointerException e) {
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },3000);
    }


}
