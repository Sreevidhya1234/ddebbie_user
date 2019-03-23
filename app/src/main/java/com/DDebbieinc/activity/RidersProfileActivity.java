package com.DDebbieinc.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.Vehicle;
import com.DDebbieinc.util.Constants;
import com.DDebbieinc.util.DirectionParser;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RidersProfileActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private Button mBtnContinue;
    private MarkerOptions options, opt1, opt2;
    private Marker mark1, mark2;
    ArrayList<LatLng> points;
    private GoogleMap mMap;
    protected static final String TAG = "location-updates-sample";

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


    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    private Marker mMarker, mHomeMarker;
    private LatLng lastLatLang;
    private ArrayList<Vehicle> vehicleArrayList;
    private ArrayList<Vehicle> vehicleArrayListTemp;
    private ArrayList<MarkerOptions> markerOptionsArrayList;
    private boolean carStatus;
    private ArrayList<Marker> markerArrayList;
    private TextView mTxtName, mTxtCarName, mTxtTime;
    private Context context = this;
    private CircleImageView mImgPic;
    private int vehicleType = 1, aVehicleType = 0;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Utils utils;
    private boolean isWheelSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riders_profile);
        init();
        isWheelSelected = getIntent().getBooleanExtra("wheelchairStatus", false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();
    }

    private void init() {
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
        mBtnContinue = (Button) findViewById(R.id.btnContinue);
        mBtnContinue.setOnClickListener(this);
        markerArrayList = new ArrayList<>();
        utils = new Utils(context);
        mTxtName = (TextView) findViewById(R.id.txtName);
        mTxtCarName = (TextView) findViewById(R.id.txtCarName);
        mTxtTime = (TextView) findViewById(R.id.txtTime);

        mImgPic = (CircleImageView) findViewById(R.id.imgDp);

 /*       Picasso.with(context).load(utils.getDriverPhoto()).transform(new CircleTransform())
                .placeholder(R.mipmap.user_default)
                .error(R.mipmap.user_default)
                .into(mImgPic);
                      mTxtName.setText(utils.getDriverName());
        mTxtCarName.setText(utils.getDriverModel());*/

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
        vehicleArrayListTemp = new ArrayList<>();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnContinue:
                if (vehicle_status) {
                    Intent intentRide = new Intent(RidersProfileActivity.this, FareEstimateActivity.class);
                    intentRide.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    intentRide.putExtra("wheelchairStatus", isWheelSelected);
                    startActivity(intentRide);
                } else {
                    IOUtils.toastMessage(context, "This Type of Vehicle not Avaliable Now");
                }
               /* Intent intentRide = new Intent(RidersProfileActivity.this, FareEstimateActivity.class);
                intentRide.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intentRide.putExtra("wheelchairStatus", isWheelSelected);
                startActivity(intentRide);
*/

                break;
        }

    }


    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, 0);
    }


    @Override
    public boolean onMarkerClick(Marker marker) {

        for (int i = 0; i < markerArrayList.size(); i++) {
            if (marker.equals(markerArrayList.get(i))) {

                for (int j = 0; j < markerArrayList.size(); j++) {
                    if (marker.getSnippet().equals(vehicleArrayList.get(j).getDriverId())) {
                        //Toast.makeText(getApplicationContext(), "Driver Name:  " + vehicleArrayList.get(j).getName(), Toast.LENGTH_SHORT).show();

                        Utils utils = new Utils(context);
                        utils.setDriverInfo(vehicleArrayList.get(j).getDriverId(), vehicleArrayList.get(j).getName(), vehicleArrayList.get(j).getPhoto(), vehicleArrayList.get(j).getVehicleType(),
                                vehicleArrayList.get(j).getModel(), vehicleArrayList.get(j).getNumber(), vehicleArrayList.get(j).getLatitude(), vehicleArrayList.get(j).getLongitude());

                        mTxtName.setText(utils.getDriverName());
                        mTxtCarName.setText(utils.getDriverModel());

                        /*Picasso.with(context).load(utils.getDriverPhoto()).transform(new CircleTransform())
                                .placeholder(R.mipmap.user_default)
                                .error(R.mipmap.user_default)
                                .into(mImgPic);*/

                        Log.e("img", utils.getDriverPhoto());
                      /*  Glide.with(context)
                                .load(utils.getDriverPhoto())
                                .centerCrop()
                                .placeholder(R.mipmap.user_default)
                                .error(R.mipmap.user_default)
                                .into(mImgPic);*/
                        setDP(utils.getDriverPhoto());

                        LatLng latLng = new LatLng(Double.parseDouble(vehicleArrayList.get(j).getLatitude()), Double.parseDouble(vehicleArrayList.get(j).getLongitude()));
                        String url = getDirectionsUrl(mHomeMarker.getPosition(), latLng);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                        break;
                    }
                }
            }
        }

        return false;
    }

    public void setDP(String url) {
        if (!url.equals("")) {
         /*   Picasso.with(DashboardActivity.this).load(utils.getDP()).transform(new CircleTransform())
                    .placeholder(R.mipmap.user_default)
                    .error(R.mipmap.user_default)
                    .into(imgProfilePic);*/

/*
            Glide.with(context)
                    .load(utils.getDP())
                    .centerCrop()
                    .placeholder(R.mipmap.user_default)
                    .error(R.mipmap.user_default)
                    .into(imgProfilePic);*/
            ImageLoader imageLoader = ImageLoader.getInstance();
            DisplayImageOptions options;
            options = new DisplayImageOptions.Builder()
                    .showImageOnFail(R.mipmap.user_default)
                    .showStubImage(R.mipmap.user_default)
                    .showImageForEmptyUri(R.mipmap.user_default).cacheInMemory()
                    .cacheOnDisc().build();
            imageLoader.init(ImageLoaderConfiguration.createDefault(context));
            imageLoader.displayImage(
                    (url), mImgPic,
                    options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            mImgPic.setImageResource(R.mipmap.user_default);
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


    @Override
    public void onMapClick(LatLng latLng) {

        //Toast.makeText(getApplicationContext(), "click  ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = utils.getPickup();
        mMap.clear();
        MarkerOptions options = new MarkerOptions().title("Pickup location")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                .anchor(0.5f, 0.5f);
        options.position(latLng);
        mHomeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        // mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);
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
    private void updateUI() {

        if (mMap != null) {

            if (mCurrentLocation != null) {
              /*  Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                Double lat = bundle.getDouble("lat");
                Double lng = bundle.getDouble("long");
                LatLng latLng = utils.getPickup();
                mMap.clear();
                MarkerOptions options = new MarkerOptions().title("Pickup location")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                        .anchor(0.5f, 0.5f);
                options.position(latLng);
                mHomeMarker = mMap.addMarker(options);

                MarkerOptions options1 = new MarkerOptions().title("Driver location")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin))
                        .anchor(0.5f, 0.5f);
                LatLng latLng1 = new LatLng(Double.parseDouble(utils.getDriverLat()), Double.parseDouble(utils.getDriverLong()));
                options1.position(latLng1);
                mMarker = mMap.addMarker(options1);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(latLng, latLng1);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);*/

                if (IOUtils.isNetworkAvailable(context)) {
                    createJsonobjectForNearVehicles();
                }

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


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
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
            Log.d("Exception while downloading url", e.toString());
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
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    if (j == 0) {    // Get distance from the list
                        IOUtils.DRIVER_DISTANCE = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        String ARRIVAL_TIME = (String) point.get("duration");
                        IOUtils ioUtils = new IOUtils(context);
                        ioUtils.setArrivalTime(ARRIVAL_TIME);
                        mTxtTime.setText(ARRIVAL_TIME);
                        continue;
                    }

                    IOUtils ioUtils = new IOUtils(context);
                    ioUtils.setArrivalTime(mTxtTime.getText().toString());
/*
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);*/
                }


            }
        }
    }




    /*
     * createJsonobjectForNearVehicles - In this method we create JsonObject for api call...
     */

    public void createJsonobjectForNearVehicles() {
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading near vehicles");
            progressDialog.setCancelable(true);
            progressDialog.show();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.USER_LAT, "" + mHomeMarker.getPosition().latitude);
            jsonObject.put(Constants.USER_LONG, "" + mHomeMarker.getPosition().longitude);
            Log.v("JsonObject", jsonObject.toString());
            nearVehicleApiCall(jsonObject);
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }

    /*
     * nearVehicleApiCall - In this method we call the api...
     */

    public void nearVehicleApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(context);


        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_NEAR_BY, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getString("response").equals("Vehicles")) {
                                JSONArray jsonArray = response.getJSONArray("nearVehicles");
                                vehicleArrayList = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    vehicleArrayList.add(new Vehicle("" + i, jsonObject.getString("driverId"), jsonObject.getString("vehicleTypeId"),
                                            jsonObject.getString("latitude"), jsonObject.getString("longitude"),
                                            jsonObject.getString("driverName"), jsonObject.getString("photo"),
                                            jsonObject.getString("vehicleModel"), jsonObject.getString("vehicleNumber")));
                                }
                                if (vehicleArrayList.size() != 0)


                                    if (vehicleArrayList != null) {
                                        for (int i = 0; i < vehicleArrayList.size(); i++) {

                                            vehicleArrayListTemp.add(vehicleArrayList.get(i));

                                            /*if (Integer.parseInt(vehicleArrayList.get(i).getVehicleType()) == 5) {
                                                vehicleArrayListTemp.add(vehicleArrayList.get(i));
                                            } else {
                                                Log.e("check", "check==>");
                                            }*/

                                        }
                                    }
                                setVehicle();
                                progressDialog.dismiss();

                            } else {
                                /*IOUtils.alertMessegeDialog(DashboardActivity.this, response.getString("response"), "OK");*/
                                Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();
                                carStatus = false;
                                progressDialog.dismiss();
                            }


                            vehicleArrayListTemp.clear();
                            if (vehicleArrayList != null) {
                                for (int i = 0; i < vehicleArrayList.size(); i++) {
                                    //  if (Integer.parseInt(vehicleArrayList.get(i).getVehicleType()) == IOUtils.VEHICLE_TYPE) {

                                    if (Integer.parseInt(vehicleArrayList.get(i).getVehicleType()) == 1) {
                                        vehicleArrayListTemp.add(vehicleArrayList.get(i));
                                    } else {
                                        Log.e("check", "check==>");
                                    }

                                    // }
                                }
                            }

                            //setVehicle();
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

    boolean vehicle_status = false;

    private void setVehicle() {
        LatLng l = mHomeMarker.getPosition();

        if (mMap != null) {
            mMap.clear();
        }
        MarkerOptions options = new MarkerOptions().title("Pickup location")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                .anchor(0.5f, 0.5f);
        options.position(l);
        mHomeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(l));

        markerOptionsArrayList = new ArrayList<>();
        Log.v("Cars", vehicleArrayListTemp.size() + "");
        if (vehicleArrayListTemp.size() == 0) {

            vehicle_status = false;
        } else {
            vehicle_status = true;
        /*    Picasso.with(context).load(vehicleArrayListTemp.get(0).getPhoto()).transform(new CircleTransform())
                    .placeholder(R.mipmap.user_default)
                    .error(R.mipmap.user_default)
                    .into(mImgPic);*/

           /* Glide.with(context)
                    .load(vehicleArrayListTemp.get(0).getPhoto())
                    .centerCrop()
                    .placeholder(R.mipmap.user_default)
                    .error(R.mipmap.user_default)
                    .into(mImgPic);*/

            setDP(vehicleArrayListTemp.get(0).getPhoto());
            mTxtName.setText(vehicleArrayListTemp.get(0).getName());
            mTxtCarName.setText(vehicleArrayListTemp.get(0).getModel());
            LatLng latLng = new LatLng(Double.parseDouble(vehicleArrayListTemp.get(0).getLatitude()), Double.parseDouble(vehicleArrayListTemp.get(0).getLongitude()));
            String url = getDirectionsUrl(mHomeMarker.getPosition(), latLng);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
        for (int i = 0; i < vehicleArrayListTemp.size(); i++) {
            int type = Integer.parseInt(vehicleArrayListTemp.get(i).getVehicleType());
            Log.v("Cars1", vehicleArrayListTemp.size() + "");
            MarkerOptions markerOptions = new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_pin))
                    .snippet(vehicleArrayListTemp.get(i).getDriverId())
                    .anchor(0.5f, 0.5f);
            markerOptionsArrayList.add(markerOptions);
            LatLng latLng = new LatLng(Double.parseDouble(vehicleArrayListTemp.get(i).getLatitude()),
                    Double.parseDouble(vehicleArrayListTemp.get(i).getLongitude()));
            markerOptions.position(latLng);
            Marker marker = mMap.addMarker(markerOptions);
            markerArrayList.add(marker);

        }

    }

}
