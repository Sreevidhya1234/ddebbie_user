package com.DDebbieinc.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.DDebbieinc.R;
import com.DDebbieinc.fragment.ConfirmEditCardFragment;
import com.DDebbieinc.fragment.EditCardFragment;
import com.DDebbieinc.fragment.PaymentInfoFragment;

public class PaymentInfoActivity extends AppCompatActivity implements PaymentInfoFragment.OnFragmentInteractionListener,
        ConfirmEditCardFragment.OnFragmentInteractionListener, EditCardFragment.OnFragmentInteractionListener {
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_info);
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

        PaymentInfoFragment paymentInfoFragment = new PaymentInfoFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, paymentInfoFragment, paymentInfoFragment.getClass().getName());
        fragmentTransaction.addToBackStack(paymentInfoFragment.getClass().getName());
        fragmentTransaction.commit();

    }
    @Override
    public void onBackPressed() {

        if(fragmentManager.getBackStackEntryCount() == 1)
        {
            this.finish();
            overridePendingTransition(0, 0);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
