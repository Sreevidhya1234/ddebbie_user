package com.DDebbieinc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.util.FourDigitCardFormatWatcher;

public class PaymentDetailsActivity extends AppCompatActivity {
    private Button mBtnSubmit;
    private EditText mEdtCardNo;
    private LinearLayout linearCredit,linearVisa,linearMaster,linearAmerican;
    private ImageView radioCredit,radioVisa,radioMaster,radioAmerican;
    private ImageButton imgBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        mBtnSubmit = (Button) findViewById(R.id.btnSubmit);

        mEdtCardNo = (EditText) findViewById(R.id.edtCardNo);

        mEdtCardNo.addTextChangedListener(new FourDigitCardFormatWatcher());

        linearCredit = (LinearLayout) findViewById(R.id.linearCredit);
        linearVisa = (LinearLayout) findViewById(R.id.linearVisa);
        linearMaster = (LinearLayout) findViewById(R.id.linearMaster);
        linearAmerican = (LinearLayout) findViewById(R.id.linearAmerican);


        radioCredit = (ImageView) findViewById(R.id.radioCredit);
        radioVisa = (ImageView) findViewById(R.id.radioVisa);
        radioMaster = (ImageView) findViewById(R.id.radioMaster);
        radioAmerican = (ImageView) findViewById(R.id.radioAmerican);

        imgBack = (ImageButton) findViewById(R.id.imgBack);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentDetailsActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });


        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Submit", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PaymentDetailsActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });


        linearCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCredit.setImageDrawable(getResources().getDrawable(R.mipmap.radio_on));
                radioVisa.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioMaster.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioAmerican.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
            }
        });


        linearVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCredit.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioVisa.setImageDrawable(getResources().getDrawable(R.mipmap.radio_on));
                radioMaster.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioAmerican.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
            }
        });

        linearMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCredit.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioVisa.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioMaster.setImageDrawable(getResources().getDrawable(R.mipmap.radio_on));
                radioAmerican.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
            }
        });

        linearAmerican.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCredit.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioVisa.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioMaster.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioAmerican.setImageDrawable(getResources().getDrawable(R.mipmap.radio_on));
            }
        });


    }

}
