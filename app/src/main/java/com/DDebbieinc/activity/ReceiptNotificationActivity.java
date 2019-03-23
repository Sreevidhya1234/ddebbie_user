package com.DDebbieinc.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.util.AppLogger;
import com.google.firebase.analytics.FirebaseAnalytics;


public class ReceiptNotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt_notification);
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
        showReceiptDialog();
        AppLogger.generateLog("trip_completed");

    }


    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, 0);
        Intent intent = new Intent(ReceiptNotificationActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    private void showReceiptDialog() {
        // custom dialog
        final Dialog dialog = new Dialog(ReceiptNotificationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.receipt_dialog);
        //dialog.setTitle("Title...");
        // set the custom dialog components - text, image and button
      //  PinEntryView pinEntryView= (PinEntryView) dialog.findViewById(R.id.edtPromo);


        Button btnCanel = (Button) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the custom dialog
        btnCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        // if button is clicked, close the custom dialog
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Email sent", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                Intent intentArrival = new Intent(ReceiptNotificationActivity.this, RideArrivalInfoActivity.class);
                intentArrival.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentArrival);
            }
        });

        dialog.show();

    }

}
