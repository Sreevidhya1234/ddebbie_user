package com.DDebbieinc.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.Session.Session;
import com.DDebbieinc.util.DirectionParser;
import com.DDebbieinc.util.IOUtils;
import com.DDebbieinc.util.MenuArrowDrawable;
import com.DDebbieinc.util.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
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

public class DestinationActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener, View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    private Button mBtnPromo, mBtnFare;
    private ImageButton mImgAddDest;
    private TextView mEdtSecDest, mEdtFirstDest;
    private PlaceAutocompleteFragment autocompleteFragment;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE2 = 2;
    private MenuArrowDrawable menuArrowDrawable;
    private MarkerOptions options, opt1, opt2;
    private Marker mark1, mark2;
    ArrayList<LatLng> points;
    private ArrayList<LatLng> markerPoints;

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
    private Polyline polyline1, polyline2;
    private int poly;
    private String distance = "";
    private String duration = "";
    private IOUtils ioUtils;
    private String source = "", dest = "";
    private int toggle;
    private Utils utils;
    private boolean isWheelSelected = false;
    Session session;

    public String Po_line = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destination);

        session = new Session(this);
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
        markerPoints = new ArrayList<LatLng>();
        mBtnFare = (Button) findViewById(R.id.btnFare);
        mBtnPromo = (Button) findViewById(R.id.btnPromocode);
        mBtnPromo.setOnClickListener(this);
        mBtnFare.setOnClickListener(this);
        mImgAddDest = (ImageButton) findViewById(R.id.imgAdd);
        mEdtSecDest = (TextView) findViewById(R.id.edt_sec_destination);
        mEdtFirstDest = (TextView) findViewById(R.id.edtDestination);
        mImgAddDest.setOnClickListener(this);
        mEdtFirstDest.setOnClickListener(this);
        mEdtSecDest.setOnClickListener(this);
        ioUtils = new IOUtils(DestinationActivity.this);
        utils = new Utils(DestinationActivity.this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgAdd:
                if (mEdtSecDest.getVisibility() == View.VISIBLE) {
                    mEdtSecDest.setVisibility(View.GONE);
                    mEdtSecDest.setText("");
                } else {
                    mEdtSecDest.setVisibility(View.VISIBLE);
                }
                IOUtils.DEST2_LATLNG = null;

                break;
            case R.id.btnFare:
                if (!source.equals("") || !dest.equals("")) {

                    if (session.getPoints().equalsIgnoreCase("0")) {


                        Toast.makeText(getApplicationContext(), "Please Select the Proper Destination.", Toast.LENGTH_LONG).show();


                    } else {
                        Intent intentFare = new Intent(DestinationActivity.this, RidersProfileActivity.class);
                        intentFare.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intentFare.putExtra("lat", mHomeMarker.getPosition().latitude);
                        intentFare.putExtra("long", mHomeMarker.getPosition().longitude);
                        intentFare.putExtra("wheelchairStatus", isWheelSelected);
                        startActivity(intentFare);

                        Po_line = "1";
                        session.setPoints(Po_line);
                    }


                } else {
                    Toast.makeText(getApplicationContext(), "Please select destination", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edtDestination:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(DestinationActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                break;

            case R.id.edt_sec_destination:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(DestinationActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE2);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                break;
            case R.id.btnPromocode:
                // custom dialog
                IOUtils ioUtils = new IOUtils(DestinationActivity.this);
                ioUtils.showPromo();
                break;
        }

    }


    @Override
    public void onBackPressed() {
        this.finish();
        overridePendingTransition(0, 0);
        IOUtils.DEST2_LATLNG = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                toggle = 1;
                poly = 1;
                if (polyline1 != null) {
                    polyline1.remove();
                    mMarker.remove();

                }
                Place place = PlaceAutocomplete.getPlace(this, data);
                mEdtFirstDest.setText(place.getAddress());
                IOUtils.PLACE1 = (String) place.getAddress();
                source = (String) place.getAddress();
                mEdtSecDest.setText("Second Destination");
                LatLng latLng = place.getLatLng();
                LatLng origin = mHomeMarker.getPosition();
                MarkerOptions options = new MarkerOptions().title((String) place.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin1))
                        .anchor(0.5f, 0.5f);
                options.position(latLng);
                mMarker = mMap.addMarker(options);
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.18);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(origin);
                builder.include(latLng);
                LatLngBounds bound = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, width, height, padding));
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, width, height, padding), 1000, null);

                LatLng dest = latLng;
                lastLatLang = dest;
                IOUtils.DEST1_LATLNG = dest;

// Getting URL to the Google Directions API
                String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

// Start downloading json data from Google Directions API
                downloadTask.execute(url);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }


        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE2) {
            if (resultCode == RESULT_OK) {
                toggle = 2;
                poly = 2;
                if (polyline2 != null) {
                    polyline2.remove();
                    mMarker.remove();
                }
                Place place = PlaceAutocomplete.getPlace(this, data);
                IOUtils.PLACE2 = (String) place.getAddress();
                mEdtSecDest.setText(place.getAddress());
                //dest = (String) place.getName();

                LatLng secDestLatLang = place.getLatLng();
                //LatLng origin = mHomeMarker.getPosition();

                //if(mMarker == null) {
                MarkerOptions options = new MarkerOptions().title((String) place.getName())
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin1))
                        .anchor(0.5f, 0.5f);
                options.position(secDestLatLang);
                mMarker = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(secDestLatLang));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                int width = getResources().getDisplayMetrics().widthPixels;
                int height = getResources().getDisplayMetrics().heightPixels;
                int padding = (int) (width * 0.18);
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(mHomeMarker.getPosition());
                builder.include(lastLatLang);
                builder.include(secDestLatLang);
                LatLngBounds bound = builder.build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bound, width, height, padding));
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, width, height, padding), 1000, null);

                IOUtils.DEST2_LATLNG = secDestLatLang;
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(lastLatLang, secDestLatLang);
                DownloadTask downloadTask = new DownloadTask();

// Start downloading json data from Google Directions API
                downloadTask.execute(url);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {


        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String Key_value = "&key=" + "AIzaSyD-1vZlJc6oJzyevLUfR2ikT6QZkXg93s8";

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + Key_value;


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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(mark1)) {

        }

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
/*
        mHomeMarker.setPosition(latLng);
        mHomeMarker.setTitle("Your selected location");

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));*/

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
                            duration = (String) point.get("duration");
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
                if (toggle == 1) {
                    IOUtils.DISTANCE1 = distance;
                    IOUtils.DURATION1 = duration;
                    Log.e("DISTANCE1", distance);
                    Log.e("DURATION1", duration);
                    IOUtils.DISTANCE2 = "";
                    IOUtils.DURATION2 = "";
                    IOUtils.TOTAL_DISTANCE = distance;
                    IOUtils.TOTAL_TIME = IOUtils.DURATION1;
                    Log.e("total_dist", IOUtils.TOTAL_DISTANCE);
                    Log.e("TOTAL_TIME", IOUtils.TOTAL_TIME);

                } else if (toggle == 2) {
                    IOUtils.DISTANCE2 = distance;
                    IOUtils.DURATION2 = duration;
                    Log.e("DISTANCE2", distance);
                    Log.e("DURATION2", duration);
                    float dist1 = Float.parseFloat(IOUtils.DISTANCE1.replace(" km", ""));
                    float dist2 = Float.parseFloat(IOUtils.DISTANCE2.replace(" km", ""));
                    float total_dist = dist1 + dist2;
                    IOUtils.TOTAL_DISTANCE = total_dist + " km";
                    Log.e("total_dist", total_dist + " km");

                    String[] timeItems = {IOUtils.DURATION1, IOUtils.DURATION2};
                    totalTime(timeItems);
                }
                // Drawing polyline in the Google Map for the i-th route
                if (poly == 1) {
                    if (lineOptions != null) {
                        polyline1 = mMap.addPolyline(lineOptions);
                        Po_line = "1";
                        session.setPoints(Po_line);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Route not available for selected destination.", Toast.LENGTH_LONG).show();
                        Po_line = "0";
                        session.setPoints(Po_line);

                        Log.e("check", "Check==>" + session.getPoints());
                    }
                } else {
                    if (lineOptions != null) {
                        polyline2 = mMap.addPolyline(lineOptions);
                        Po_line = "1";
                        session.setPoints(Po_line);
                    } else {
                        Toast.makeText(getApplicationContext(), "Route not available for selected destination.", Toast.LENGTH_LONG).show();
                        Po_line = "0";
                        session.setPoints(Po_line);
                    }
                }
            }
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mMap != null) {
            // Add a marker in Sydney and move the camera
      /*  LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Point your pickup location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));*/
            LatLng latLng = utils.getPickup();
            //mMap.clear();
            MarkerOptions options = new MarkerOptions().title("Pickup location")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                    .anchor(0.5f, 0.5f);
            options.position(latLng);
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            // mMap.setMyLocationEnabled(true);
            mMap.setOnMapClickListener(this);
        }
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
                IOUtils ioUtils = new IOUtils(DestinationActivity.this);
             /*   Double lat = new Double(ioUtils.getCurrentLat(DestinationActivity.this));
                Double lng = new Double(ioUtils.getCurrentLng(DestinationActivity.this));*/
                LatLng latLng = utils.getPickup();

                mMap.clear();
                MarkerOptions options = new MarkerOptions().title("Pickup location")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                        .anchor(0.5f, 0.5f);
                options.position(latLng);
                mHomeMarker = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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


    public void totalTime(String[] timeItems) {
        // as example for visibility
        int[] total = {0, 0, 0}; // days, hours, minutes
        for (int i = 0; i < timeItems.length; i++) {
            if (timeItems[i].contains("day ")) {
                total[0]++;
            } else if (timeItems[i].contains("days")) {
                total[0] += Integer.valueOf(timeItems[i].substring(0, timeItems[i].indexOf(" days")));
            }
            if (timeItems[i].contains("hour ")) {
                total[1]++;
            } else if (timeItems[i].contains("hours")) {
                if (timeItems[i].indexOf(" hours") <= 3) {
                    total[1] += Integer.valueOf(timeItems[i].substring(0, timeItems[i].indexOf(" hours")));
                } else {
                    if (timeItems[i].contains("days")) {
                        total[1] += Integer.valueOf(timeItems[i].substring(timeItems[i].lastIndexOf("days ")) + 5, timeItems[i].indexOf(" hours"));
                    } else {
                        total[1] += Integer.valueOf(timeItems[i].substring(timeItems[i].lastIndexOf("day ")) + 4, timeItems[i].indexOf(" hours"));
                    }
                }
            }
            if (timeItems[i].contains("min ")) {
                total[2]++;
            } else if (timeItems[i].contains("mins")) {
                if (timeItems[i].indexOf(" mins") <= 3) {
                    total[2] += Integer.valueOf(timeItems[i].substring(0, timeItems[i].indexOf(" mins")));
                } else {
                    if (timeItems[i].contains("hours")) {
                        total[2] += Integer.valueOf(timeItems[i].substring(timeItems[i].indexOf("hours ") + 6, timeItems[i].indexOf(" mins")));
                    } else {
                        total[2] += Integer.valueOf(timeItems[i].substring(timeItems[i].indexOf("hour ") + 5, timeItems[i].indexOf(" mins")));
                    }
                }
            }
        }
        Log.d("LOG", total[0] + " days " + total[1] + " hours " + total[2] + " mins.");
        String time = total[0] + " days " + total[1] + " hours " + total[2] + " mins.";
        IOUtils.TOTAL_TIME = time.replace("0 days", "").replace("0 hours", "");
        Log.d("LOG", IOUtils.TOTAL_TIME);

    }

}
