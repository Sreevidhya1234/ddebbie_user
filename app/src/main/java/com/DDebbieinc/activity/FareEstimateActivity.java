package com.DDebbieinc.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.RideAccept;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.util.AppLogger;
import com.DDebbieinc.util.Config;
import com.DDebbieinc.util.Constants;
import com.DDebbieinc.util.Consts;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FareEstimateActivity extends AppCompatActivity {
    private Button mBtnRide;
    private TextView mTxtDuration, mTxtDistance, mTxtEstRate, mTxtRate;
    private Toolbar toolbar;
    private UserDetailsPojo userDetailsPojo;
    private Utils utils;
    private Context context = this;
    private ProgressDialog progressDialog;
    public static boolean active = false;
    private Spinner spinnerDonate;
    // private int donation = 0;
    private boolean isWheelSelected = false;
    private int rate;
    public EditText Edit_poor;
    public String donation = "0";


    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);
    private String TAG = "Paypal";


    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "", Area = "", Connec = "";
    Geocoder geocoder;
    String Check_lat = "";
    String Check_Long = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fare_estimate);


        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        startService(intent);


        init();
        active = true;

        Check( utils.getPickup().latitude,utils.getPickup().longitude);
        isWheelSelected = getIntent().getBooleanExtra("wheelchairStatus", false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mBtnRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intentInfo = new Intent(FareEstimateActivity.this, RidersInfoActivity.class);
                intentInfo.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentInfo);*/
                if (mBtnRide.isEnabled()) {
                    createJsonobjectForNewRide();
                } else {
                    Toast.makeText(getApplicationContext(), "You have already requested a ride.", Toast.LENGTH_SHORT).show();
                }


            }
        });
        if (!IOUtils.DISTANCE1.equals("") || !IOUtils.DURATION1.equals("")) {

            mTxtDistance.setText(IOUtils.TOTAL_DISTANCE);
            mTxtDuration.setText(IOUtils.TOTAL_TIME);
            ioUtils.setTotalTime(IOUtils.TOTAL_TIME);

        }
        donation = Edit_poor.getText().toString();



/*        spinnerDonate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        });*/
        if (IOUtils.isNetworkAvailable(context))
            createJsonobjectForFare();
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

        Edit_poor = (EditText) findViewById(R.id.Edit_poor);

        spinnerDonate = (Spinner) findViewById(R.id.spinnerDonate);
        utils = new Utils(context);
        ioUtils = new IOUtils(context);
    }

    private static final int REQUEST_CODE_PAYMENT = 0;
    private String paymentId;

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
            //IOUtils ioUtils = new IOUtils(FareEstimateActivity.this);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromLongitude", "" + utils.getPickup().longitude);
            jsonObject.put("fromLatitude", "" + utils.getPickup().latitude);




            if (IOUtils.DEST2_LATLNG != null) {
                jsonObject.put("toLongitude", "" + IOUtils.DEST2_LATLNG.longitude);
                jsonObject.put("toLatitude", "" + IOUtils.DEST2_LATLNG.latitude);
                jsonObject.put("vehiclesTypeId", IOUtils.VEHICLE_TYPE);
                jsonObject.put("numOfPassengers", 1);
                JSONArray jsonArray = new JSONArray();


                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("lat", "" + IOUtils.DEST1_LATLNG.latitude);
                jsonObject1.put("long", "" + IOUtils.DEST1_LATLNG.longitude);
                jsonObject1.put("address", IOUtils.PLACE1);

                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("lat", "" + IOUtils.DEST2_LATLNG.latitude);
                jsonObject2.put("long", "" + IOUtils.DEST2_LATLNG.longitude);
                jsonObject2.put("address", IOUtils.PLACE2);
                jsonArray.put(jsonObject2);

                jsonArray.put(jsonObject1);
                jsonObject.put("destinations", jsonArray);

            } else {
                jsonObject.put("toLongitude", "" + IOUtils.DEST1_LATLNG.longitude);
                jsonObject.put("toLatitude", "" + IOUtils.DEST1_LATLNG.latitude);
                jsonObject.put("vehiclesTypeId", IOUtils.VEHICLE_TYPE);
                jsonObject.put("distance", "" + IOUtils.TOTAL_DISTANCE);
                jsonObject.put("countryname", "" + country);
                //JSONArray jsonArray = new JSONArray();
                //   JSONObject jsonObject1 = new JSONObject();
                //jsonObject1.put("lat", "" +IOUtils.DEST1_LATLNG.latitude);
                // jsonObject1.put("long", "" +IOUtils.DEST1_LATLNG.latitude);
                // jsonObject1.put("address", "");
                //jsonArray.put(jsonObject1);
                // jsonObject.put("destinations", jsonArray);
            }
            fareApiCall(jsonObject);

            Log.v("JsonObject", jsonObject.toString());
        } catch (Exception e) {
            progressDialog.dismiss();

        }
    }

    IOUtils ioUtils;

    public void fareApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(FareEstimateActivity.this);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_FARE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
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
                error.printStackTrace();
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
            Date dt = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String currentDateandTime = sdf.format(dt);
            int hours = dt.getHours();
            int minutes = dt.getMinutes();
            int seconds = dt.getSeconds();
            String datetime = currentDateandTime + " " + hours + ":" + minutes + ":" + seconds;

            Log.e("DateTime", datetime);
            IOUtils ioUtils = new IOUtils(context);
            userDetailsPojo = ioUtils.getUser();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());
            jsonObject.put(Constants.DATE_TIME, datetime);
            //  jsonObject.put(Constants.PICKUP_LOC, IOUtils.getPlaceName(context, utils.getPickup()));
            // jsonObject.put(Constants.DROPOFF_LOC, IOUtils.getPlaceName(context, IOUtils.DEST1_LATLNG));

            jsonObject.put(Constants.PICKUP_LOC, utils.getPickup());
            jsonObject.put(Constants.DROPOFF_LOC, IOUtils.DEST1_LATLNG);
            jsonObject.put(Constants.RIDE_TYPE, "1");
            jsonObject.put(Constants.FROM_LONG, "" + utils.getPickup().longitude);
            jsonObject.put(Constants.FROM_LAT, "" + utils.getPickup().latitude);
            jsonObject.put("isWheelSelected", isWheelSelected);

            if (IOUtils.DEST2_LATLNG != null) {
                jsonObject.put("toLongitude", "" + IOUtils.DEST2_LATLNG.longitude);
                jsonObject.put("toLatitude", "" + IOUtils.DEST2_LATLNG.latitude);
                jsonObject.put("vehiclesTypeId", IOUtils.VEHICLE_TYPE);
                JSONArray jsonArray = new JSONArray();


                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("lat", "" + IOUtils.DEST1_LATLNG.latitude);
                jsonObject1.put("long", "" + IOUtils.DEST1_LATLNG.longitude);
                jsonObject1.put("address", IOUtils.PLACE1);


                JSONObject jsonObject2 = new JSONObject();
                jsonObject2.put("lat", "" + IOUtils.DEST2_LATLNG.latitude);
                jsonObject2.put("long", "" + IOUtils.DEST2_LATLNG.longitude);
                jsonObject2.put("address", IOUtils.PLACE2);
                jsonArray.put(jsonObject2);


                jsonArray.put(jsonObject1);
                jsonObject.put("destinations", jsonArray.toString());


            } else {
                jsonObject.put("toLongitude", "" + IOUtils.DEST1_LATLNG.longitude);
                jsonObject.put("toLatitude", "" + IOUtils.DEST1_LATLNG.latitude);
                jsonObject.put("vehiclesTypeId", IOUtils.VEHICLE_TYPE);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("lat", "" + IOUtils.DEST1_LATLNG.latitude);
                jsonObject1.put("long", "" + IOUtils.DEST1_LATLNG.longitude);
                jsonObject1.put("address", IOUtils.PLACE1);
                jsonArray.put(jsonObject1);
                jsonObject.put("destinations", jsonArray.toString());

            }
            //  jsonObject.put(Constants.PROMO, IOUtils.PROMO_CODE);
            //  jsonObject.put(Constants.DISC, IOUtils.DISCOUNT);

            jsonObject.put(Constants.PROMO, "3");
            jsonObject.put(Constants.DISC, "4");
            jsonObject.put(Constants.PAY_MODE, "2");
            jsonObject.put(Constants.PAYMENT_STATUS, "0");
            jsonObject.put(Constants.TOTAL_AMT, ioUtils.getRate());
            jsonObject.put("donatedAmount", donation);
            jsonObject.put("numOfPassengers", "1");
            jsonObject.put("totalKm", IOUtils.TOTAL_DISTANCE);
            Log.v("JsonObject", jsonObject.toString());
            newRideApiCall(jsonObject);
            Toast.makeText(getApplicationContext(),"Please Wait Until the driver accept the request!",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }


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
                                AppLogger.generateLog("trip_book ");


                                //   JSONObject jsonObject = response.getJSONObject("rideData");
                                // RideAccept rideAccept = new RideAccept();
                                // rideAccept.setRideId(jsonObject.getString("rideId"));


                                // IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                                // Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                                // Intent intentInfo = new Intent(FareEstimateActivity.this, RidersInfoActivity.class);
                                // intentInfo.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                // startActivity(intentInfo);


                                progressDialog.dismiss();
                                mBtnRide.setEnabled(false);
                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");

                                if (response.getString("response").equalsIgnoreCase("Driver not available")) {

                                    // Intent intentInfo = new Intent(FareEstimateActivity.this, PaymentDetailsActivity.class);
                                    //  startActivity(intentInfo);

                                } else {

                                }
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
        Toast.makeText(getApplicationContext(),"Please Wait Until the driver accept the request!",Toast.LENGTH_LONG).show();
    }


    public void Check(double lat_address, double long_address) {

      /*  Log.e("lat_lng==>", "latlng==>" + lat_address);
        String lat_str = lat_address.substring(10, lat_address.length() - 1);
        Log.e("lat_address==>", "lat_address==>" + lat_str);
        String[] lat = lat_str.split(",");
        Log.e("SPLIT_lat==>", "lat==>" + lat[0] + "\n" + lat[1]);*/
      //  double Source_Lat = 0.0;
    //    double Source_Long = 0.0;


     /*   try {


            // Source_Lat = Double.parseDouble(lat[0]);
            //Source_Long = Double.parseDouble(lat[1]);

            Check_lat = String.valueOf(lat_address);
            Check_Long = String.valueOf(long_address);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }*/

        geocoder = new Geocoder(FareEstimateActivity.this, Locale.getDefault());

        try {

            addresses = geocoder.getFromLocation(lat_address, long_address, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            this.address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            country = addresses.get(0).getCountryName();
            postalCode = addresses.get(0).getPostalCode();
            knownName = addresses.get(0).getFeatureName();
            Area = addresses.get(0).getSubLocality();
            Connec = addresses.get(0).getAddressLine(0);
            Connec = addresses.get(0).getFeatureName();


            Consts.Country = country;

            // txtAddress.setText("Pickup location :\n" + knownName + "," + Area + "," + city + "," + state + "," + country + "," + postalCode);

            //txtInfo.setText("Pickup location :\n" + this.address);

            // Only if available else return NULL
        } catch (IOException e) {
            e.printStackTrace();
        }
        // mEdtSearchPlace.setText(this.address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);
    }


}
