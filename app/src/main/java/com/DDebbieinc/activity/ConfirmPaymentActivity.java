package com.DDebbieinc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.DDebbieinc.R;


public class ConfirmPaymentActivity extends AppCompatActivity {

    private Button mBtnConfirm, mBtnDecline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_payment);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.back_icon);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mBtnConfirm = (Button) findViewById(R.id.btnConfirm);
        mBtnDecline = (Button) findViewById(R.id.btnDecline);
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNotify = new Intent(ConfirmPaymentActivity.this, ReceiptNotificationActivity.class);
                intentNotify.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentNotify);
            }
        });

        mBtnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentNotify = new Intent(ConfirmPaymentActivity.this, DashboardActivity.class);
                intentNotify.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intentNotify.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentNotify);
            }
        });


    }
    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, 0);
    }



}
