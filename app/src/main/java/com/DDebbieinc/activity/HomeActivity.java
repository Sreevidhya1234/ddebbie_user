package com.DDebbieinc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.DDebbieinc.R;
import com.DDebbieinc.util.AppLogger;
import com.google.firebase.analytics.FirebaseAnalytics;


public class HomeActivity extends AppCompatActivity implements View.OnClickListener{
    private Button mBtnSignUp, mBtnSignIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mBtnSignIn = (Button) findViewById(R.id.btnSignIN);
        mBtnSignUp = (Button) findViewById(R.id.btnSignUp);
        mBtnSignUp.setOnClickListener(this);
        mBtnSignIn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnSignIN:
                Intent intent = new Intent(HomeActivity.this, SigninActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSignUp:
                Intent intent1 = new Intent(HomeActivity.this, SignUpActivity.class);
                startActivity(intent1);
                break;

        }
    }
}
