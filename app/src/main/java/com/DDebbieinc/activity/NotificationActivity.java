package com.DDebbieinc.activity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.DDebbieinc.R;


public class NotificationActivity extends AppCompatActivity{
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TextView txtTitle, txtMessage;
    private Button btnDelete;
    private CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
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

   /*     NotificationFragment notificationFragment = new NotificationFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, notificationFragment, notificationFragment.getClass().getName());
        fragmentTransaction.addToBackStack(notificationFragment.getClass().getName());
        fragmentTransaction.commit();*/

        cardView = (CardView) findViewById(R.id.card_view);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        if(getIntent().getBooleanExtra("waiting", false)){
            cardView.setVisibility(View.VISIBLE);
            txtTitle.setText("PromoNotification");
            txtMessage.setText(getIntent().getStringExtra("message"));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 5000);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    @Override
    public void onBackPressed() {

    /*    if(fragmentManager.getBackStackEntryCount() == 1)
        {
            this.finish();
            overridePendingTransition(0, 0);
        }else {
            super.onBackPressed();
        }*/
        super.onBackPressed();
    }


}
