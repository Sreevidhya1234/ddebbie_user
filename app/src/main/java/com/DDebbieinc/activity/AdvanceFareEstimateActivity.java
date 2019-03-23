package com.DDebbieinc.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.util.Config;
import com.DDebbieinc.util.Constants;
import com.DDebbieinc.util.IOUtils;
import com.DDebbieinc.util.JsonObjectRequestWithHeader;
import com.DDebbieinc.util.Utils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class AdvanceFareEstimateActivity extends AppCompatActivity {
    private Button mBtnRide;
    private TextView mTxtDuration, mTxtDistance, mTxtEstRate, mTxtRate;
    private TextView txtTitleTime;
    private Toolbar toolbar;
    private UserDetailsPojo userDetailsPojo;
    private Utils utils;
    private Context context = this;
    private ProgressDialog progressDialog;
    public static boolean active = false;
    private Spinner spinnerDonate;
    private int donation = 1;
    private View viewTime;
    JSONObject jsonAdvance, jsonAdvance1;
    private int rate;
    private boolean isWheelSelected = false;


    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);
    private String TAG = "Paypal";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_estimate);

        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        startService(intent);
        String json = getIntent().getStringExtra("json");
        String json1 = getIntent().getStringExtra("json1");
        isWheelSelected = getIntent().getBooleanExtra("wheelchairStatus", false);
        try {
            jsonAdvance = new JSONObject(json);
            jsonAdvance1 = new JSONObject(json1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        init();
        mBtnRide.setEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mBtnRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (IOUtils.isNetworkAvailable(context)) {
                    //createJsonobjectForNewRide();
                    PayPalPayment payment = new PayPalPayment(new BigDecimal(rate), "USD", "Ddebbie Ride",
                            PayPalPayment.PAYMENT_INTENT_SALE);

                    Intent intent = new Intent(AdvanceFareEstimateActivity.this, PaymentActivity.class);

                    // send the same configuration for restart resiliency
                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                    startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                } else {
                    Toast.makeText(getApplicationContext(), "You have already requested a ride.", Toast.LENGTH_SHORT).show();
                }


            }
        });

        spinnerDonate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        donation = 1;
                        break;
                    case 1:
                        donation = 3;
                        break;
                    case 2:
                        donation = 5;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                donation = 1;

            }
        });
        if (IOUtils.isNetworkAvailable(context)) {
            createJsonobjectForFare();
        }
    }


    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.back_icon);
        mBtnRide = (Button) findViewById(R.id.btnRequestRide);
        mTxtDistance = (TextView) findViewById(R.id.txtDistance);
        mTxtDuration = (TextView) findViewById(R.id.txtTime);
        mTxtEstRate = (TextView) findViewById(R.id.txtEstRate);
        mTxtRate = (TextView) findViewById(R.id.txtRate);
        txtTitleTime = (TextView) findViewById(R.id.txtTitleTime);
        spinnerDonate = (Spinner) findViewById(R.id.spinnerDonate);
        viewTime = findViewById(R.id.viewTime);
        viewTime.setVisibility(View.GONE);
        viewTime.setVisibility(View.GONE);
        utils = new Utils(context);
        ioUtils = new IOUtils(context);
        txtTitleTime.setVisibility(View.GONE);
        mTxtDuration.setVisibility(View.GONE);
        userDetailsPojo = ioUtils.getUser();

    }


    private static final int REQUEST_CODE_PAYMENT = 0;
    private String paymentId;

    /**
     * Receiving the PalPay payment response
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e(TAG, confirm.toJSONObject().toString(4));
                        Log.e(TAG, confirm.getPayment().toJSONObject()
                                .toString(4));

                        paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e(TAG, "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);


                        // Now verify the payment on the server side
                        if (IOUtils.isNetworkAvailable(context)) {
                            createJsonobjectForNewRide();
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ",
                                e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
                if (!utils.getPaymentStatus()) {
                    showPayment();
                }

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted.");
                if (!utils.getPaymentStatus()) {
                    showPayment();
                }
            }
        }

        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.e(TAG, confirm.toJSONObject().toString(4));
                        Log.e(TAG, confirm.getPayment().toJSONObject()
                                .toString(4));

                        String paymentId = confirm.toJSONObject()
                                .getJSONObject("response").getString("id");

                        String payment_client = confirm.getPayment()
                                .toJSONObject().toString();

                        Log.e(TAG, "paymentId: " + paymentId
                                + ", payment_json: " + payment_client);


                        // Now verify the payment on the server side
                        if (IOUtils.isNetworkAvailable(context)) {
                            //  createJsonobjectWaitingCharge(paymentId);
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ",
                                e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "The user canceled.");
                showPayment();

            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.e(TAG,
                        "An invalid Payment or PayPalConfiguration was submitted.");
                showPayment();
            }
        }
    }


    public void showPayment() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Something went wrong! Please confirm your payment.");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PayPalPayment payment = new PayPalPayment(new BigDecimal(rate), "USD", "Ddebbie Ride",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(AdvanceFareEstimateActivity.this, PaymentActivity.class);

                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                startActivityForResult(intent, REQUEST_CODE_PAYMENT);
            }
        });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, 0);
    }


    public void createJsonobjectForFare() {
        try {
            utils.setDonation("" + donation);
            progressDialog = IOUtils.getProgessDialog(context);
            progressDialog.show();

            fareApiCall(jsonAdvance);
            Log.v("JsonObject", jsonAdvance.toString());
        } catch (Exception e) {
            progressDialog.dismiss();

        }
    }

    IOUtils ioUtils;


    public void createJsonobjectUpdatePayment(String trandId, String amount) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideId);
            Log.e("email", userDetailsPojo.getEmail());
            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());

            jsonObject.put("transactionId", trandId);
            jsonObject.put("totalAmount", amount);
            Log.v("JsonObject", jsonObject.toString());

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Updating paymaent..");
            progressDialog.show();
            if (IOUtils.isNetworkAvailable(context)) {
                updatePaymentApiCall(jsonObject);

            }


        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
 /*
    * regitrationApiCall - In this method we call the api...
    */

    public void updatePaymentApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_UPDATE_PAYMENT, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(response.getString("response"));
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setResult(10);
                                        finish();
                                    }
                                });
                                builder.show();

                            } else {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();

                            }
                            progressDialog.dismiss();


                        } catch (JSONException e) {
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });

        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);
    }


    public void fareApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_FARE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                rate = response.getInt("rate");
                                mTxtRate.setText("$ " + response.getInt("rate"));
                                int rate = Integer.parseInt(String.valueOf(response.getInt("rate")));

                                int per = (int) (15 * rate) / 100;
                                String min = String.valueOf(rate - per);
                                String max = String.valueOf(rate + per);

                                mTxtEstRate.setText("$" + min + " - " + "$" + max);
                                mTxtDistance.setText(response.getString("distance"));
                                IOUtils.TOTAL_DISTANCE = response.getString("distance");


                                ioUtils.setRate(String.valueOf(response.getInt("rate")));
                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                            }

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                        }
                        progressDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });
        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);
        queue.add(jsonObjReq);
    }

    public void createJsonobjectForNewRide() {
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Requesting for ride..");
            progressDialog.setCancelable(true);
            progressDialog.show();
            Log.v("JsonObject", jsonAdvance1.toString());

            jsonAdvance1.put(Constants.TOTAL_AMT, rate);
            jsonAdvance1.put("donatedAmount", donation);
            jsonAdvance1.put("numOfPassengers", "1");
            jsonAdvance1.put("totalKm", IOUtils.TOTAL_DISTANCE);
            jsonAdvance1.put(Constants.PAYMENT_STATUS, "1");
            jsonAdvance1.put("isWheelSelected", isWheelSelected);

            newRideApiCall(jsonAdvance1);
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }


    private String rideId;

    public void newRideApiCall(JSONObject js) {

        RequestQueue queue = Volley.newRequestQueue(context);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_NEW_RIDE, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());


                        try {
                            if (response.getBoolean("result")) {
                                Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();
                                mBtnRide.setEnabled(false);
                                JSONObject jsonObject = new JSONObject(response.toString());
                                JSONObject jsonObject1 = jsonObject.getJSONObject("rideData");
                                rideId = jsonObject1.getString("rideId");
                                createJsonobjectUpdatePayment(paymentId, "" + rate);


                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                                progressDialog.dismiss();
                            }


                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.dismiss();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error", error.getMessage());
                VolleyLog.d("Response", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        });


        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);

        queue.add(jsonObjReq);


    }


}
