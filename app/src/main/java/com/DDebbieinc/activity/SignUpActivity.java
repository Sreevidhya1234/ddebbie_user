package com.DDebbieinc.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.util.AppLogger;
import com.DDebbieinc.util.Constants;
import com.DDebbieinc.util.IOUtils;
import com.DDebbieinc.util.JsonObjectRequestWithHeader;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SignUpActivity extends AppCompatActivity {
    private InterstitialAd interstitialAd;
    private Button mBtnContinue;
    private EditText mEdtName, mEdtMob, mEdtEmail, mEdtPassword, mEdtConfirmPassword;
    private ImageView mImgCheckbox,mImgBack;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    Context context = this;
    private boolean checkBoxFlag = false;
    private ProgressDialog dialog;

    String token="";
    private LinearLayout main_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        interstitialAd = new InterstitialAd(this, Constants.FB_AD_ID);
        // Set listeners for the Interstitial Ad
        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial displayed callback
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Toast.makeText(SignUpActivity.this, "Error: " + adError.getErrorMessage(),
                        Toast.LENGTH_LONG).show();
                Log.e("ad error",adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Show the ad when it's done loading.
                interstitialAd.show();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
            }
        });

        // For auto play video ads, it's recommended to load the ad
        // at least 30 seconds before it is shown
        interstitialAd.loadAd();


        gcmRegistration();
        mBtnContinue = (Button) findViewById(R.id.btnContinue);
        mImgBack = (ImageView) findViewById(R.id.imgBack);

        mEdtName = (EditText) findViewById(R.id.edtName);
        mEdtMob = (EditText) findViewById(R.id.edtMob);
        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtPassword = (EditText) findViewById(R.id.edtPassword);
        mEdtConfirmPassword = (EditText) findViewById(R.id.edtConfirmPassword);
        mImgCheckbox = (ImageView) findViewById(R.id.imgCheckbox);

        main_layout = (LinearLayout) findViewById(R.id.main_layout);

        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IOUtils.hideSoftKeyboard(SignUpActivity.this);

            }
        });


        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mImgCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBoxFlag) {
                    mImgCheckbox.setImageDrawable(getResources().getDrawable(R.mipmap.check_mark));
                    checkBoxFlag = false;
                } else {
                    mImgCheckbox.setImageDrawable(getResources().getDrawable(R.mipmap.check_mark_on));
                    checkBoxFlag = true;
                }
            }
        });

        mBtnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(SignupActivity.this, Paymentactivity.class);
                startActivity(intent);*/
                validate();
            }
        });


    }
   /*
    *
    * gcmRegistration - This method is user to register Device Google Cloud messaging...
    *
    * */

    public void gcmRegistration(){
        new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    token =
                            InstanceID.getInstance(context).getToken(Constants.GCM_SENDER_ID,
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.v("Registration id", token);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onDestroy() {
        if (interstitialAd != null) {
            interstitialAd.destroy();
        }
        super.onDestroy();
    }

    /*
    * validate - This method is used validate app all the feild...
    */
    public void validate() {


        if (mEdtName.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Please enter your name.", "OK");
        } else if (mEdtMob.getText().toString().length() == 0) {
            IOUtils.alertMessegeDialog(context, "Please enter your mobile number.", "OK");
        } else if (mEdtMob.getText().toString().length() != 10) {
            IOUtils.alertMessegeDialog(context, "Please enter your valid mobile number.", "OK");
        } else if (mEdtEmail.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Please enter your email.", "OK");
        } else if (!mEdtEmail.getText().toString().matches(EMAIL_PATTERN)) {
            IOUtils.alertMessegeDialog(context, "Please enter valid email.", "OK");
        } else if (mEdtPassword.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Please enter password.", "OK");
        } else if (mEdtConfirmPassword.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Please enter confirm password.", "OK");
        } else if (!mEdtConfirmPassword.getText().toString().equals(mEdtPassword.getText().toString())) {
            IOUtils.alertMessegeDialog(context, "Password & confirm password does not matches.", "OK");
        } else if (checkBoxFlag) {
           /* Intent intent = new Intent(SignupActivity.this, Paymentactivity.class);
            startActivity(intent);*/
            if (IOUtils.isNetworkAvailable(SignUpActivity.this)) {
                try {
                    createJsonobjectForApiCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else
            {
                IOUtils.setAlertForActivity(SignUpActivity.this);
            }
        } else {
            IOUtils.alertMessegeDialog(context, "Please agree term & contdition.", "OK");
        }


    }

    /*
    * createJsonobjectForApiCall - In this method we create JsonObject for api call...
    */

    public void createJsonobjectForApiCall(){
        try{
            dialog = IOUtils.getProgessDialog(context);
            dialog.show();

            JSONObject jsonObject = new JSONObject();




            /*jsonObject.put(Constants.EMAIL,"tushar02.katakdound@gmail.com");
            jsonObject.put(Constants.PASSWORD,"123456");*/

            jsonObject.put(Constants.EMAIL,mEdtEmail.getText().toString());
            jsonObject.put(Constants.CUSTOMER_NAME,mEdtName.getText().toString());
            jsonObject.put(Constants.PASSWORD,mEdtConfirmPassword.getText().toString());
            jsonObject.put(Constants.DEVICE_TOKEN,token);
            jsonObject.put(Constants.DEVICE_TYPE,"1");
            jsonObject.put(Constants.CONTACT_NUMBER,mEdtMob.getText().toString());
            jsonObject.put(Constants.EMERGENCY_CONTACT_NUMBER, mEdtMob.getText().toString());


            Log.v("JsonObject", jsonObject.toString());


            regitrationApiCall(jsonObject);


        }catch (Exception e){
            dialog.dismiss();
        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    *
    *Response: {"result":"true",
    * "response":"Customer successfully registered",
    * "customerData":{"id":"32",
    * "customerName":"Nikhil",
    * "contactNumber":"8888705573",
    * "emgContactNumber":"8888705573",
    * "photo":"http:\/\/www.ddebbie.com\/api\/dimg\/default.png",
    * "email":"shete.nikhil@gmail.com",
    * "status":"1"}}
    */

    public void regitrationApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(SignUpActivity.this);


        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_SIGNUP, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {

                            if(response.getString("response").equals("Customer successfully registered")){
                                AppLogger.generateLog("Registration");

                                JSONObject jsonObject = response.getJSONObject("customerData");

                                UserDetailsPojo userDetailsPojo = new UserDetailsPojo();
                                userDetailsPojo.setId(jsonObject.getString("id"));
                                userDetailsPojo.setCustomerName(jsonObject.getString("customerName"));
                                userDetailsPojo.setContactNumber(jsonObject.getString("contactNumber"));
                                userDetailsPojo.setEmgContactNumber(jsonObject.getString("emgContactNumber"));
                                userDetailsPojo.setEmail(jsonObject.getString("email"));
                                userDetailsPojo.setStatus(jsonObject.getString("status"));
                                userDetailsPojo.setStatus(jsonObject.getString("photo"));

                                IOUtils ioUtils = new IOUtils(context);
                                ioUtils.setUser(userDetailsPojo);

                                IOUtils.toastMessage(context, response.getString("response"));
                                Intent intent2 = new Intent(SignUpActivity.this, DashboardActivity.class);
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent2);
                                finish();
                            }else {
                                IOUtils.alertMessegeDialog(context,response.getString("response"),"OK");
                            }
                            dialog.dismiss();


                        }catch (JSONException e){
                            dialog.dismiss();

                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                dialog.dismiss();

            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);

    }

}
