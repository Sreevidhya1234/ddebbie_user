package com.DDebbieinc.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.RideAccept;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.util.AppLogger;
import com.DDebbieinc.util.Config;
import com.DDebbieinc.util.Constants;
import com.DDebbieinc.util.Consts;
import com.DDebbieinc.util.DirectionParser;
import com.DDebbieinc.util.IOUtils;
import com.DDebbieinc.util.JsonObjectRequestWithHeader;
import com.DDebbieinc.util.MenuArrowDrawable;
import com.DDebbieinc.util.Utils;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RidersInfoActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private Button mBtnContinue, mBtnCancel;

    ArrayList<LatLng> points;
    ProgressDialog progressDialog;
    public static boolean active = false;

    private GoogleMap mMap;
    protected static final String TAG = "location-updates-sample";
    private int rideStatus = 0;
    private int flag = 0;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;
    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;
    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    private Marker mHomeMarker;
    private CircleImageView mImgProfile;
    private String distance = "";
    private String duration = "";
    private IOUtils ioUtils;
    private Utils utils;
    private UserDetailsPojo userDetailsPojo;
    private Context context = this;
    private TextView txtName, txtCarName, txtArrival, txtFare, txtTravelTime;
    private RideAccept rideAccept;
    private int accept = 0, arrive = 0, start = 0, end = 0;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    // Progress dialog
    private ProgressDialog pDialog;
    ImageView img_go, img_cross;
    TextView edit_feed;
    TextView text_send, text_title;
    LinearLayout linear_ok, linear_cancel;
    private static final int REQUEST_CODE_PAYMENT = 0;

    // PayPal configuration
    private static PayPalConfiguration paypalConfig = new PayPalConfiguration()
            .environment(Config.PAYPAL_ENVIRONMENT).clientId(
                    Config.PAYPAL_CLIENT_ID);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riders_info);
        IOUtils.RUNNING = true;
        active = true;
        Intent intent = new Intent(this, PayPalService.class);

        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

        startService(intent);
        init();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
        //showRateDialog();

     /*   Glide.with(RidersInfoActivity.this)
                .load(rideAccept.getDriverPhoto())
                .centerCrop()
                .placeholder(R.mipmap.user_default)
                .error(R.mipmap.user_default)
                .into(mImgProfile);*/

        //setDP(rideAccept.getDriverPhoto());
    }


    public void setDP(String url) {
        if (!url.equals("")) {

            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.mipmap.user_default)
                    .showStubImage(R.mipmap.user_default)
                    .showImageForEmptyUri(R.mipmap.user_default).cacheInMemory()
                    .cacheOnDisc().build();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            imageLoader.displayImage(
                    (url), mImgProfile,
                    options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            mImgProfile.setImageResource(R.mipmap.user_default);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });

        }
    }


    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.back_icon);
        utils = new Utils(context);

        progressDialog = new ProgressDialog(RidersInfoActivity.this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        rideAccept = (RideAccept) getIntent().getSerializableExtra("ride");
        Gson gson = new Gson();
        String json = gson.toJson(rideAccept);
        editor.putString("rideAccept", json);
        editor.putBoolean("ride_requested", false);
        editor.commit();

        mBtnContinue = (Button) findViewById(R.id.btnConfirmPayment);
        mBtnCancel = (Button) findViewById(R.id.btnCancel);
        mBtnContinue.setOnClickListener(this);
        mBtnCancel.setOnClickListener(this);
        ioUtils = new IOUtils(context);
        userDetailsPojo = ioUtils.getUser();
        txtName = (TextView) findViewById(R.id.txtName);
        txtCarName = (TextView) findViewById(R.id.txtCarName);
        txtArrival = (TextView) findViewById(R.id.txtTime);
        txtFare = (TextView) findViewById(R.id.txtEstFare);
        txtTravelTime = (TextView) findViewById(R.id.txtTravellingTime);
        txtCarName.setText(rideAccept.getVehicleModel());
        txtName.setText(rideAccept.getDriverName());
        txtFare.setText("$ " + ioUtils.getRate());
        txtTravelTime.setText(ioUtils.getTotalTime());
        txtArrival.setText(ioUtils.getArrivalTime());
        mImgProfile = (CircleImageView) findViewById(R.id.imgProfile);
        utils.setRideStatus(true);

        // Log.e("dp", rideAccept.getDriverPhoto());


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if (utils.getPaymentStatus()) {
            mBtnContinue.setVisibility(View.GONE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            mBtnCancel.setLayoutParams(params);
        } else {
            mBtnContinue.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        if (utils.getRideStatus()) {
            utils.setKill(true);
            Log.e("destroy", "destroy");
        }

        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnConfirmPayment:
                //Toast.makeText(getApplicationContext(),"Please wait till ride complete", Toast.LENGTH_SHORT).show();
                if (IOUtils.isNetworkAvailable(context)) {

                    if (!utils.getPaymentStatus()) {
                        PayPalPayment payment = new PayPalPayment(
                                new BigDecimal(rideAccept.getTotalAmount()),
                                Config.DEFAULT_CURRENCY,
                                "Ddebbie Ride",
                                Config.PAYMENT_INTENT);

                        //payment.items(items).paymentDetails(paymentDetails);

                        // Custom field like invoice_number etc.,
                        payment.custom("This is text that will be associated with the payment that the app can use.");
                        PayPalPayment thingsToBuy = payment;
                        Intent intent = new Intent(RidersInfoActivity.this, PaymentActivity.class);
                        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
                        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingsToBuy);
                        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
                    } else {
                        IOUtils.toastMessage(context, "Payment is already done.");
                    }
                }

                break;
            case R.id.btnCancel:
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Cancellation charges will be added.");
                builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (IOUtils.isNetworkAvailable(context))
                            createJsonobjectForCancelRide();
                    }
                });
                builder.setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                builder.show();
                break;

        }

    }

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
                            createJsonobjectUpdatePayment(paymentId, rideAccept.getTotalAmount());
                        }
                        utils.setPaymentStatus(true);

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
                            createJsonobjectWaitingCharge(paymentId);
                        }
                        utils.setPaymentStatus(true);

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
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do You want Cancel the Payment");
        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

               /* PayPalPayment payment = new PayPalPayment(new BigDecimal(rideAccept.getTotalAmount()), Config.DEFAULT_CURRENCY, "Ddebbie Ride",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(RidersInfoActivity.this, PaymentActivity.class);

                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                startActivityForResult(intent, REQUEST_CODE_PAYMENT);*/
            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                PayPalPayment payment = new PayPalPayment(new BigDecimal(rideAccept.getTotalAmount()), Config.DEFAULT_CURRENCY, "Ddebbie Ride",
                        PayPalPayment.PAYMENT_INTENT_SALE);

                Intent intent = new Intent(RidersInfoActivity.this, PaymentActivity.class);
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
       /* this.finish();
        overridePendingTransition(0, 0);*/
        Toast.makeText(getApplicationContext(), "Please complete or cancel ride ", Toast.LENGTH_SHORT).show();

    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {


        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }


    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            br.close();
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.e("place json", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    //** A class to parse the Google Places in JSON format *//*
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionParser parser = new DirectionParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);
                        if (j == 0) {    // Get distance from the list
                            distance = (String) point.get("distance");
                            continue;
                        } else if (j == 1) { // Get duration from the list
                            IOUtils.ARRIVAL = duration = (String) point.get("duration");
                            ioUtils.setArrivalTime(duration);
                            txtArrival.setText(duration);
                            continue;
                        }


                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(4);
                    lineOptions.color(Color.BLUE);
                }
                String[] timeItems = {distance, duration};
                duration = new Utils(RidersInfoActivity.this).totalTime(timeItems);
            }
            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);

            }


        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {

        mHomeMarker.setPosition(latLng);
        mHomeMarker.setTitle("Your selected location");

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(5));

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng latLng = utils.getPickup();
        MarkerOptions options = new MarkerOptions().title("Pickup location")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                .anchor(0.5f, 0.5f);
        options.position(latLng);
        mHomeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

    }


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    public void startUpdatesButtonHandler() {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            startLocationUpdates();
        }
    }

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler() {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */


    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    Timer timer;

    private void updateUI() {
        if (mMap != null) {
            if (mCurrentLocation != null) {

                timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        createJsonobjectForGetLoc();
                    }
                }, 0, Constants.LOCATION_INTERVAL);


            } else {
                Toast.makeText(getApplicationContext(), "Please start GPS", Toast.LENGTH_SHORT).show();
                LatLng latLng = new LatLng(-34, 151);

                mMap.clear();
                MarkerOptions options = new MarkerOptions().title("Your current location")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                        .anchor(0.5f, 0.5f);
                options.position(latLng);
                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            }
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.
        IOUtils.RUNNING = true;

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        IOUtils.RUNNING = false;
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    long[] v = {500, 1000};

    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }


    public void createJsonobjectForGetLoc() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.DRIVER_ID, rideAccept.getDriverId());
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            Log.v("JsonObject1", jsonObject.toString());

            getLocApiCall(jsonObject);


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    /*
    * nearVehicleApiCall - In this method we call the api..
    *
    * Response: {"result":"true","response":"Location","location":{"longitude":"73.81571478","latitude":"18.49299339"}}
.
    */

    String waitingCharge, waitingTime;

    public void getLocApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(RidersInfoActivity.this);


        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_GET_DIVER_LOCATION, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.v("Response", response.toString());

                        try {
                            if (response.getString("result").equals("true")) {
                                //{"result":"true","response":"Location","location":{"longitude":"73.8156152","latitude":"18.49315","rideStatus":"5","rideId":439,"vehicleTypeId":"4","waitingTime":"16.00","waitingCharges":"5.00"}}

                                JSONObject jsonObject = response.getJSONObject("location");
                                rideStatus = Integer.parseInt(jsonObject.getString("rideStatus"));
                                waitingCharge = jsonObject.getString("waitingCharges");
                                waitingTime = jsonObject.getString("waitingTime");

                                if (rideStatus == 2) {

                                    Log.v("Status1", "" + rideStatus);
                                    if (flag == 0) {
                                        if (accept == 0) {
                                            if (!utils.getPaymentStatus()) {


                                                if (Consts.Confirm_check.equalsIgnoreCase("1")) {
                                                    //Alert Check
                                                    Ride_Dialog();
                                                    Consts.Confirm_check = "0";
                                                } else {
                                                    Consts.Confirm_check = "0";
                                                }



                                              /*  PayPalPayment payment = new PayPalPayment(new BigDecimal(rideAccept.getTotalAmount()), Config.DEFAULT_CURRENCY, "Ddebbie Ride",
                                                        PayPalPayment.PAYMENT_INTENT_SALE);
                                                Intent intent = new Intent(RidersInfoActivity.this, PaymentActivity.class);
                                                // send the same configuration for restart resiliency
                                                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
                                                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                                                startActivityForResult(intent, 0);*/
                                            }
                                        /*    if(!utils.getPaymentStatus()) {
                                                showPayment();
                                            }*/
                                            // accept = 1;
                                        }
                                    }

                                }
                                if (rideStatus == 3) {

                                    Log.v("Status1", "" + rideStatus);
                                    //if (flag == 0) {
                                    if (arrive == 0) {
                                        Resources r = getResources();
                                        Notification notification = new NotificationCompat.Builder(context)
                                                .setTicker("Driver arrived at your location")
                                                .setSmallIcon(R.mipmap.notification)
                                                .setContentTitle("Driver arrived")
                                                .setContentText("Driver arrived at your location")
                                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                                .setVibrate(v)
                                                .setAutoCancel(true)
                                                .build();

                                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        notificationManager.notify(0, notification);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RidersInfoActivity.this);
                                        builder.setMessage("Driver arrived at your location");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                flag = 1;
                                            }
                                        });
                                        if (!((Activity) context).isFinishing()) {
                                            //show dialog
                                            builder.show();

                                        }
                                        arrive = 1;
                                    }
                                    //}

                                } else {
                                    LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
                                    if (mMap != null) {
                                        mMap.clear();
                                    }
                                    MarkerOptions options = new MarkerOptions().title("Pickup location")
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                                            .anchor(0.5f, 0.5f);
                                    options.position(utils.getPickup());
                                    mHomeMarker = mMap.addMarker(options);
                                    MarkerOptions options1 = new MarkerOptions().title("Drivers location")
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin1))
                                            .anchor(0.5f, 0.5f);
                                    options1.position(latLng);
                                    mMap.addMarker(options1);

                                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                    builder.include(utils.getPickup());
                                    builder.include(latLng);
                                    LatLngBounds bound = builder.build();
                                    int padding = 300; // offset from edges of the map in pixels
                                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bound, padding);
                                    mMap.animateCamera(cu);

                                    String url = getDirectionsUrl(utils.getPickup(), latLng);

                                    DownloadTask downloadTask = new DownloadTask();

                                    // Start downloading json data from Google Directions API
                                    downloadTask.execute(url);
                                }


                                if (rideStatus == 4) {
                                    //if (flag == 1) {
                                    if (start == 0) {
                                        Resources r = getResources();
                                        Notification notification = new NotificationCompat.Builder(context)
                                                .setTicker("Trip started")
                                                .setSmallIcon(R.mipmap.notification)
                                                .setContentTitle("Trip started")
                                                .setContentText("Trip started")
                                                .setAutoCancel(true)
                                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                                .setVibrate(v)
                                                .build();

                                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        notificationManager.notify(0, notification);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RidersInfoActivity.this);
                                        builder.setMessage("Trip started");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                flag = 2;
                                            }
                                        });

                                        if (!((Activity) context).isFinishing()) {
                                            //show dialog
                                            builder.show();

                                        }
                                        start = 1;
                                        // }
                                    }
                                    LatLng latLng = new LatLng(Double.parseDouble(jsonObject.getString("latitude")), Double.parseDouble(jsonObject.getString("longitude")));
                                    if (mMap != null) {
                                        mMap.clear();
                                    }
                                    MarkerOptions options = new MarkerOptions().title("Drivers location")
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                                            .anchor(0.5f, 0.5f);
                                    options.position(latLng);
                                    mHomeMarker = mMap.addMarker(options);
                                    LatLng latLng1 = new LatLng(Double.parseDouble(rideAccept.getToLatitude()), Double.parseDouble(rideAccept.getToLongitude()));
                                    MarkerOptions options1 = new MarkerOptions().title("Destination")
                                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin1))
                                            .anchor(0.5f, 0.5f);
                                    options1.position(latLng1);
                                    mMap.addMarker(options1);

                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int height = getResources().getDisplayMetrics().heightPixels;
                                    int padding = (int) (width * 0.18);
                                    LatLngBounds.Builder builder1 = new LatLngBounds.Builder();
                                    builder1.include(latLng);
                                    builder1.include(latLng1);
                                    LatLngBounds bound = builder1.build();
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, 200));
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 200));
                                    // Getting URL to the Google Directions API
                                    String url = getDirectionsUrl(latLng, latLng1);

                                    DownloadTask downloadTask = new DownloadTask();

                                    // Start downloading json data from Google Directions API
                                    downloadTask.execute(url);

                                }


                                if (rideStatus == 5) {
                                    // if (flag == 2) {
                                    if (end == 0) {
                                        Resources r = getResources();
                                        Notification notification = new NotificationCompat.Builder(context)
                                                .setTicker("Arrived to destination")
                                                .setSmallIcon(R.mipmap.notification)
                                                .setContentTitle("Trip ended")
                                                .setContentText("Arrived to destination")
                                                .setAutoCancel(true)
                                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                                .setVibrate(v)
                                                .build();

                                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                        notificationManager.notify(0, notification);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(RidersInfoActivity.this);
                                        builder.setMessage("Arrived to destination");
                                        builder.setCancelable(false);
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                flag = 3;
                                                if (!waitingCharge.equals("0.00")) {

                                                    PayPalPayment payment = new PayPalPayment(new BigDecimal(waitingCharge), Config.DEFAULT_CURRENCY, "Waiting Time:" + waitingTime + "min Charge:" + waitingCharge,
                                                            PayPalPayment.PAYMENT_INTENT_SALE);

                                                    Intent intent = new Intent(context, PaymentActivity.class);

                                                    // send the same configuration for restart resiliency
                                                    intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);

                                                    intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

                                                    startActivityForResult(intent, 3);

                                                } else {
                                                    showRateDialog();
                                                }
                                            }
                                        });
                                        if (!((Activity) context).isFinishing()) {
                                            //show dialog
                                            builder.show();

                                        }

                                        end = 1;
                                    }

                                    //}
                                }
                            } else {
                                //IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                                Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());

            }
        });


        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);

        queue.add(jsonObjReq);

    }


    public void createJsonobjectForCancelRide() {
        try {
            progressDialog.setMessage("Cancelling ride");
            progressDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());
            Log.v("JsonObject", jsonObject.toString());
            cancelRideApiCall(jsonObject);

        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
 /*
    * regitrationApiCall - In this method we call the api...
    */

    public void cancelRideApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_REJECT_RIDE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                AppLogger.generateLog("trip_cancel");

                                Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                                utils.setRideStatus(false);
                                Intent intent = new Intent(context, FareEstimateActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                overridePendingTransition(0, 0);
                                finish();
                                utils.setRideStatus(false);
                                utils.setPaymentStatus(false);

                            } else {
                                Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();

                            }
                            utils.setRideStatus(false);
                            utils.setPaymentStatus(false);
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


    public void createJsonobjectUpdatePayment(String trandId, String amount) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
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
                                mBtnContinue.setVisibility(View.GONE);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                mBtnCancel.setLayoutParams(params);

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


    public void createJsonobjectWaitingCharge(String trandId) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());
            jsonObject.put("paymentTransId", trandId);
            Log.v("JsonObject", jsonObject.toString());

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Updating paymaent..");
            progressDialog.show();
            if (IOUtils.isNetworkAvailable(context)) {
                waitingChargeApiCall(jsonObject);

            }


        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
 /*
    * regitrationApiCall - In this method we call the api...
    */

    public void waitingChargeApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_WAITING_CHARGE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
                                showRateDialog();

                            } else {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
                                showRateDialog();
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


    private int toggle = 0, n = 0;


    public void showRateDialog() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rate_dialog);
        dialog.setCancelable(false);
        //dialog.setTitle("Title...");
        // set the custom dialog components - text, image and button
        final ImageView star1, star2, star3, star4, star5;
        TextView txtDonate = (TextView) dialog.findViewById(R.id.txtDonate);
        star1 = (ImageView) dialog.findViewById(R.id.star1);
        star2 = (ImageView) dialog.findViewById(R.id.star2);
        star3 = (ImageView) dialog.findViewById(R.id.star3);
        star4 = (ImageView) dialog.findViewById(R.id.star4);
        star5 = (ImageView) dialog.findViewById(R.id.star5);
        toggle = 0;
        txtDonate.setText("Donation: $" + utils.getDonation());

        star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toggle == 0) {
                    star1.setImageResource(R.mipmap.star_yellow);
                    star2.setImageResource(R.mipmap.star_grey);
                    star3.setImageResource(R.mipmap.star_grey);
                    star4.setImageResource(R.mipmap.star_grey);
                    star5.setImageResource(R.mipmap.star_grey);
                    toggle = 1;
                    n = 1;
                } else {
                    star1.setImageResource(R.mipmap.star_grey);
                    star2.setImageResource(R.mipmap.star_grey);
                    star3.setImageResource(R.mipmap.star_grey);
                    star4.setImageResource(R.mipmap.star_grey);
                    star5.setImageResource(R.mipmap.star_grey);
                    toggle = 0;
                    n = 0;

                }

            }
        });

        star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.mipmap.star_yellow);
                star2.setImageResource(R.mipmap.star_yellow);
                star3.setImageResource(R.mipmap.star_grey);
                star4.setImageResource(R.mipmap.star_grey);
                star5.setImageResource(R.mipmap.star_grey);
                toggle = 0;
                n = 2;
            }
        });

        star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.mipmap.star_yellow);
                star2.setImageResource(R.mipmap.star_yellow);
                star3.setImageResource(R.mipmap.star_yellow);
                star4.setImageResource(R.mipmap.star_grey);
                star5.setImageResource(R.mipmap.star_grey);
                n = 3;
            }
        });

        star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.mipmap.star_yellow);
                star2.setImageResource(R.mipmap.star_yellow);
                star3.setImageResource(R.mipmap.star_yellow);
                star4.setImageResource(R.mipmap.star_yellow);
                star5.setImageResource(R.mipmap.star_grey);
                n = 4;

            }
        });
        star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                star1.setImageResource(R.mipmap.star_yellow);
                star2.setImageResource(R.mipmap.star_yellow);
                star3.setImageResource(R.mipmap.star_yellow);
                star4.setImageResource(R.mipmap.star_yellow);
                star5.setImageResource(R.mipmap.star_yellow);
                n = 5;


            }
        });
        Button btnCanel = (Button) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the custom dialog
        btnCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                utils.setRideStatus(false);
                utils.setPaymentStatus(false);
                Intent intent = new Intent(context, DashboardActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();

            }
        });
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        // if button is clicked, close the custom dialog
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IOUtils.isNetworkAvailable(context))
                    createJsonobjectForRateRide(n);
                dialog.dismiss();
                utils.setRideStatus(false);
                utils.setPaymentStatus(false);


            }
        });

        dialog.show();
    }

    public void createJsonobjectForRateRide(int n) {
        try {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.RIDE_ID, rideAccept.getRideId());
            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());
            jsonObject.put(Constants.RATE, "" + n);
            jsonObject.put("review", "");
            Log.v("JsonObject", jsonObject.toString());

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please wait while rating..");
            progressDialog.show();
            rateRideApiCall(jsonObject);


        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }
 /*
    * regitrationApiCall - In this method we call the api...
    */

    public void rateRideApiCall(final JSONObject js) {
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_RATE_RIDE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        AppLogger.generateLog("trip_rating");
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, response.getString("response"), Toast.LENGTH_SHORT).show();

                            }
                            progressDialog.dismiss();

                            Intent intent = new Intent(context, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            finish();

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


    public void Ride_Dialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        //  builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        //getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        View content = inflater.inflate(R.layout.customdialog, null);
        builder.setView(content);

        //  AlertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        text_title = (TextView) content.findViewById(R.id.text_title);
        linear_ok = (LinearLayout) content.findViewById(R.id.linear_ok);
        linear_cancel = (LinearLayout) content.findViewById(R.id.linear_cancel);

        text_title.setText("Driver Accept Your Ride");

        edit_feed = (TextView) content.findViewById(R.id.edit_feed);
        edit_feed.setText(" \n Ready to  Start Ride");


        img_cross = (ImageView) content.findViewById(R.id.img_cross);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //  alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Transparent_white)));

        // alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.card_view));

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        img_cross.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                alertDialog.dismiss();

            }
        });

        linear_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                PayPalPayment payment = new PayPalPayment(new BigDecimal(rideAccept.getTotalAmount()), Config.DEFAULT_CURRENCY, "Ddebbie Ride",
                        PayPalPayment.PAYMENT_INTENT_SALE);
                Intent intent = new Intent(RidersInfoActivity.this, PaymentActivity.class);
                // send the same configuration for restart resiliency
                intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, paypalConfig);
                intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
                startActivityForResult(intent, 0);
                accept = 1;

            }
        });
        linear_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

                createJsonobjectForCancelRide();
            }
        });

    }


}
