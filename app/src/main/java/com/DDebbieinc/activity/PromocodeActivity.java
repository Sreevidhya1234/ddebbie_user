package com.DDebbieinc.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import com.DDebbieinc.R;
import com.DDebbieinc.adapter.PromoListAdapter;
import com.DDebbieinc.entity.PromoNotification;
import com.DDebbieinc.fragment.PromocodeFragment;
import com.DDebbieinc.helper.DatabaseHandler;

import java.util.ArrayList;

public class PromocodeActivity extends AppCompatActivity implements PromocodeFragment.OnFragmentInteractionListener {
    private ListView promoListView;
    private ArrayList<PromoNotification> notificationArrayList;
    private DatabaseHandler db;
    private PromoListAdapter promoListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promocode);
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

      /*  PromocodeFragment promocodeFragment = new PromocodeFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, promocodeFragment, promocodeFragment.getClass().getName());
        fragmentTransaction.addToBackStack(promocodeFragment.getClass().getName());
        fragmentTransaction.commit();*/

        db = new DatabaseHandler(this);
        promoListView = (ListView) findViewById(R.id.listPromo);
        notificationArrayList = db.getAllPromo();
        promoListAdapter = new PromoListAdapter(notificationArrayList, this);
        promoListView.setAdapter(promoListAdapter);

    }

    @Override
    public void onBackPressed() {

     /*   if(fragmentManager.getBackStackEntryCount() == 1)
        {
            this.finish();
            overridePendingTransition(0, 0);
        }else {
            super.onBackPressed();
        }*/

        super.onBackPressed();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void deletePromocode(int index) {
        db.deletePromo(notificationArrayList.get(index));
        notificationArrayList.remove(index);
        promoListAdapter.notifyDataSetChanged();

    }
}
