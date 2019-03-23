package com.DDebbieinc.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.Session.Session;
import com.DDebbieinc.adapter.NavListAdapter;
import com.DDebbieinc.entity.NavItem;
import com.DDebbieinc.entity.RideAccept;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.entity.Vehicle;
import com.DDebbieinc.fragment.AboutUsFragment;
import com.DDebbieinc.fragment.AdvanceBookingFragment;
import com.DDebbieinc.fragment.ConfirmEditCardFragment;
import com.DDebbieinc.fragment.ContactUsFragment;
import com.DDebbieinc.fragment.EditCardFragment;
import com.DDebbieinc.fragment.EditProfileFragment;
import com.DDebbieinc.fragment.FareEstimateFragment;
import com.DDebbieinc.fragment.HomeFragment;
import com.DDebbieinc.fragment.NotificationFragment;
import com.DDebbieinc.fragment.PaymentInfoFragment;
import com.DDebbieinc.fragment.PromocodeFragment;
import com.DDebbieinc.fragment.SettingFragment;
import com.DDebbieinc.util.AppUtils;
import com.DDebbieinc.util.Constants;
import com.DDebbieinc.util.FetchAddressIntentService;
import com.DDebbieinc.util.GPSTracker;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends AppCompatActivity
        implements HomeFragment.OnFragmentInteractionListener, AdvanceBookingFragment.OnFragmentInteractionListener,
        SettingFragment.OnFragmentInteractionListener, EditProfileFragment.OnFragmentInteractionListener,
        PaymentInfoFragment.OnFragmentInteractionListener, ConfirmEditCardFragment.OnFragmentInteractionListener,
        EditCardFragment.OnFragmentInteractionListener, NotificationFragment.OnFragmentInteractionListener,
        PromocodeFragment.OnFragmentInteractionListener, ContactUsFragment.OnFragmentInteractionListener,
        FareEstimateFragment.OnFragmentInteractionListener, OnMarkerClickListener, GoogleMap.OnMapLongClickListener,
        AboutUsFragment.OnFragmentInteractionListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, View.OnClickListener {


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

    private Marker mHomeMarker;


    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private ListView mNavListView;
    private ArrayList<NavItem> arrayList;
    private NavListAdapter navListAdapter;
    private DrawerLayout drawer;
    private MenuArrowDrawable menuArrowDrawable;


    private RelativeLayout mRelSmall, mRelMedium, mRelLimo, mRelSUV, mRelToing, mRelRental, mRelWheel, linear_id;
    private Button mBtnPromo, mBtnFare;
    private TextView mEdtSearchPlace, text;
    public static TextView mTxtName;
    private ImageView mImgCurrent;
    public static CircleImageView imgProfilePic;
    private RelativeLayout mRelHome, mRelAdvance;
    private UserDetailsPojo userDetailsPojo;

    //Advance Booking layout
    public TextView edtDestination;
    public TextView edt_sec_destination;
    public TextView edt_pickup;
    public static TextView edt_date;
    public static TextView edt_time;
    private ImageView imgAddDest, imgScheduler;
    private ArrayList<Vehicle> vehicleArrayList;
    private ArrayList<Vehicle> vehicleArrayListTemp;
    private ArrayList<MarkerOptions> markerOptionsArrayList;
    private ArrayList<Marker> markerArrayList;

    private static final String LOG_TAG = "DashboardActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;

    private static String mdate_time;
    private LatLng advanceHome, advanceDest1 = null, advanceDest2 = null;
    private String rate;
    private Context context = this;
    private int aVehicleType = 0;
    private ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private Utils utils;
    private boolean advanceBooking = false;
    private int advanceVehicleType = 0;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public static boolean active = false;
    private boolean isWheelSelected = false;

    public boolean ddebbie_x = false, taxi = false, medium = false, limousine = false, suv = false, towing = false, rental = false, wheel = false;

    //Map
    List<Address> addresses;
    String address = "", city = "", state = "", country = "", postalCode = "", knownName = "";
    Geocoder geocoder;
    private LatLng mCenterLatLong;

    GPSTracker gps;
    Context mContext;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        active = true;

        session = new Session(this);
        init();
        arrayList = new ArrayList<>();
        arrayList.add(new NavItem(getResources().getString(R.string.advance), R.mipmap.advance_booking));
        arrayList.add(new NavItem(getResources().getString(R.string.setting), R.mipmap.settings));
        //arrayList.add(new NavItem(getResources().getString(R.string.payment_info), R.mipmap.payment));
        arrayList.add(new NavItem(getResources().getString(R.string.notifications), R.mipmap.notification));
        arrayList.add(new NavItem(getResources().getString(R.string.promo), R.mipmap.promocode));
        arrayList.add(new NavItem(getResources().getString(R.string.help), R.mipmap.info));
        arrayList.add(new NavItem(getResources().getString(R.string.about), R.mipmap.about));
        arrayList.add(new NavItem("Log Out", R.mipmap.signout));

        vehicleArrayListTemp = new ArrayList<>();
        gps = new GPSTracker(mContext);

        navListAdapter = new NavListAdapter(arrayList, DashboardActivity.this);
        mNavListView.setAdapter(navListAdapter);
        mNavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                IOUtils.hideSoftKeyboard(DashboardActivity.this);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction();
                switch (i) {
                    case 0:

                        IOUtils.hideSoftKeyboard(DashboardActivity.this);
                        showHome(false);

                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        advanceBooking = true;
                        hideVehiclesInAdvance();
                        if (mMap != null) {
                            mMap.clear();
                        }
                        break;
                    case 1:

                        IOUtils.hideSoftKeyboard(DashboardActivity.this);
                        Intent intentSetting = new Intent(DashboardActivity.this, SettingsActivity.class);
                        intentSetting.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentSetting);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
              /*      case 2:

                        IOUtils.hideSoftKeyboard(DashboardActivity.this);
                        Intent intentPayment = new Intent(DashboardActivity.this, PaymentInfoActivity.class);
                        intentPayment.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentPayment);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;*/
                    case 2:

                        IOUtils.hideSoftKeyboard(DashboardActivity.this);
                        Intent intentNotifiation = new Intent(DashboardActivity.this, NotificationActivity.class);
                        intentNotifiation.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        intentNotifiation.putExtra("waiting", false);
                        startActivity(intentNotifiation);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case 3:


                        IOUtils.hideSoftKeyboard(DashboardActivity.this);
                        Intent intentPromo = new Intent(DashboardActivity.this, PromocodeActivity.class);
                        intentPromo.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentPromo);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case 4:

                        IOUtils.hideSoftKeyboard(DashboardActivity.this);
                        Intent intentContact = new Intent(DashboardActivity.this, ContactUsActivity.class);
                        intentContact.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentContact);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case 5:

                        IOUtils.hideSoftKeyboard(DashboardActivity.this);
                        Intent intentAbout = new Intent(DashboardActivity.this, AboutUsActivity.class);
                        intentAbout.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentAbout);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                    case 6:

                        IOUtils ioUtils = new IOUtils(DashboardActivity.this);
                        ioUtils.clearData();
                        finish();
                        Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
                        startActivity(intent);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        }
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);

            }
        });

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

        imgProfilePic = (CircleImageView) findViewById(R.id.profile_image);
        imgProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IOUtils.hideSoftKeyboard(DashboardActivity.this);
                Intent intentSetting = new Intent(DashboardActivity.this, SettingsActivity.class);
                intentSetting.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentSetting);
            }
        });

    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        menuArrowDrawable = new MenuArrowDrawable(this);
        getSupportActionBar().setHomeAsUpIndicator(menuArrowDrawable);
        utils = new Utils(context);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        imgProfilePic = (CircleImageView) findViewById(R.id.profile_image);
        setDP();

        IOUtils ioUtils = new IOUtils(DashboardActivity.this);
        userDetailsPojo = ioUtils.getUser();
        mTxtName = (TextView) findViewById(R.id.txtName);
        mTxtName.setText(userDetailsPojo.getCustomerName());
        mRelSmall = (RelativeLayout) findViewById(R.id.relativeSmall);
        mRelMedium = (RelativeLayout) findViewById(R.id.relativeMedium);
        mRelLimo = (RelativeLayout) findViewById(R.id.relativeLimo);
        mRelSUV = (RelativeLayout) findViewById(R.id.relativeSUV);

        mRelToing = (RelativeLayout) findViewById(R.id.relativeToing);
        mRelWheel = (RelativeLayout) findViewById(R.id.relativeWheelChair);
        mRelRental = (RelativeLayout) findViewById(R.id.relativeRental);

        edtDestination = (TextView) findViewById(R.id.edtDestination);
        edt_sec_destination = (TextView) findViewById(R.id.edt_sec_destination);
        edt_pickup = (TextView) findViewById(R.id.edt_pickup);

        edt_date = (TextView) findViewById(R.id.edt_date);
        edt_time = (TextView) findViewById(R.id.edt_time);
        imgAddDest = (ImageView) findViewById(R.id.imgAddDest);
        imgScheduler = (ImageView) findViewById(R.id.imgScheduler);
        mNavListView = (ListView) findViewById(R.id.listNav);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        mRelHome = (RelativeLayout) findViewById(R.id.relativeHome);
        mRelAdvance = (RelativeLayout) findViewById(R.id.relativeAdvance);
        mEdtSearchPlace = (TextView) findViewById(R.id.edtSearch);


        linear_id = (RelativeLayout) findViewById(R.id.linear_id);
        text = (TextView) findViewById(R.id.text);
        mImgCurrent = (ImageView) findViewById(R.id.imgCurrentLoc);
        mBtnFare = (Button) findViewById(R.id.btnFare);
        mBtnPromo = (Button) findViewById(R.id.btnPromocode);
        mRelSmall.setOnClickListener(this);
        mRelMedium.setOnClickListener(this);
        mRelLimo.setOnClickListener(this);
        mRelSUV.setOnClickListener(this);

        mRelToing.setOnClickListener(this);
        mRelRental.setOnClickListener(this);
        mRelWheel.setOnClickListener(this);

        imgScheduler.setOnClickListener(this);
        mEdtSearchPlace.setOnClickListener(this);
        mBtnPromo.setOnClickListener(this);
        mBtnFare.setOnClickListener(this);
        imgAddDest.setOnClickListener(this);
        mImgCurrent.setOnClickListener(this);
        edt_date.setOnClickListener(this);
        edt_time.setOnClickListener(this);
        edtDestination.setOnClickListener(this);
        edt_sec_destination.setOnClickListener(this);
        edt_pickup.setOnClickListener(this);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        markerArrayList = new ArrayList<>();

        boolean kill = sharedPreferences.getBoolean("kill", false);
        if (kill) {
            if (utils.getRideStatus()) {
                Gson gson = new Gson();
                String json = sharedPreferences.getString("rideAccept", "");
                RideAccept rideAccept = gson.fromJson(json, RideAccept.class);
                Intent intent = new Intent(context, RidersInfoActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("ride", rideAccept);
                startActivity(intent);
                finish();
            }
        }

        IOUtils.VEHICLE_TYPE = aVehicleType = 1;

    }


    public void settingsrequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(DashboardActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }


    public void setDP() {
        if (!utils.getDP().equals("")) {
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
                    (utils.getDP()), imgProfilePic,
                    options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            imgProfilePic.setImageResource(R.mipmap.user_default);
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


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();


            if (attributions != null) {
            }
        }
    };


    private void showHome(Boolean check) {
        if (check == true) {
            mRelHome.setVisibility(View.VISIBLE);
            mRelAdvance.setVisibility(View.GONE);
            setCurrentLoc();
        } else {
            mRelHome.setVisibility(View.GONE);
            mRelAdvance.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mRelHome.getVisibility() == View.GONE) {
                showHome(true);
                advanceBooking = false;
                showVehiclesInAdvance();
            } else {
                super.onBackPressed();

            }

        }

    }

    @Override
    public void onClick(View view) {
        Intent intentDest = new Intent(DashboardActivity.this, DestinationActivity.class);
        intentDest.putExtra("wheelchairStatus", isWheelSelected);

        switch (view.getId()) {
            case R.id.relativeSmall:


                IOUtils.VEHICLE_TYPE = aVehicleType = advanceVehicleType = 1;
                if (!advanceBooking) {


                    if (ddebbie_x) {

                        intentDest.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentDest);
                    } else {

                        IOUtils.toastMessage(context, "This Type of Car Vechicle is Not Avaliable Now");
                    }

                } else {


                    advanceDest2 = null;
                    validate();
                    IOUtils.toastMessage(context, "Vehicle selected.");


                }


                break;
            case R.id.relativeMedium:
                IOUtils.VEHICLE_TYPE = aVehicleType = advanceVehicleType = 3;


                if (!advanceBooking) {

                    if (medium) {

                        intentDest.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentDest);
                    } else {
                        IOUtils.toastMessage(context, "This Type of Car Vechicle is Not Avaliable Now");
                    }

                } else {


                    advanceDest2 = null;
                    validate();
                    IOUtils.toastMessage(context, "Vehicle selected.");


                }

                break;
            case R.id.relativeLimo:
                IOUtils.VEHICLE_TYPE = aVehicleType = advanceVehicleType = 4;

                if (!advanceBooking) {

                    if (limousine) {

                        intentDest.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentDest);
                    } else {

                        IOUtils.toastMessage(context, "This Type of Car Vechicle is Not Avaliable Now");
                    }

                } else {

                    advanceDest2 = null;
                    validate();
                    IOUtils.toastMessage(context, "Vehicle selected.");

                }

                break;
            case R.id.relativeSUV:
                IOUtils.VEHICLE_TYPE = aVehicleType = advanceVehicleType = 5;


                if (!advanceBooking) {

                    if (suv) {

                        intentDest.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentDest);
                    } else {

                        IOUtils.toastMessage(context, "This Type of Car Vechicle is Not Avaliable Now");
                    }

                } else {
                    advanceDest2 = null;
                    validate();
                    IOUtils.toastMessage(context, "Vehicle selected.");

                }

                break;

            case R.id.relativeToing:
                IOUtils.VEHICLE_TYPE = aVehicleType = advanceVehicleType = 6;

                if (!advanceBooking) {

                    if (towing) {
                        intentDest.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentDest);

                    } else {

                        IOUtils.toastMessage(context, "This Type of Car Vechicle is Not Avaliable Now");
                    }

                } else {

                    advanceDest2 = null;
                    validate();
                    IOUtils.toastMessage(context, "Vehicle selected.");

                }

                break;


            case R.id.relativeRental:
                IOUtils.VEHICLE_TYPE = aVehicleType = advanceVehicleType = 7;


                if (!advanceBooking) {


                    if (rental) {

                        intentDest.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intentDest);
                    } else {

                        IOUtils.toastMessage(context, "This Type of Car Vechicle is Not Avaliable Now");
                    }

                } else {

                    advanceDest2 = null;
                    validate();
                    IOUtils.toastMessage(context, "Vehicle selected.");

                }

                break;

            case R.id.relativeWheelChair:
                IOUtils.VEHICLE_TYPE = aVehicleType = advanceVehicleType = 8;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage("Select another vehicle with wheelchair.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isWheelSelected = true;
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        isWheelSelected = false;
                    }
                });
                builder.show();

                break;

            case R.id.btnFare:
                intentDest.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intentDest);

                break;
            case R.id.btnPromocode:
                // custom dialog
                IOUtils ioUtils = new IOUtils(DashboardActivity.this);
                ioUtils.showPromo();
                break;

            case R.id.edtSearch:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(DashboardActivity.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }

                break;
            case R.id.imgCurrentLoc:

                setCurrentLoc();
                break;
            case R.id.imgAddDest:
                if (edt_sec_destination.getVisibility() == View.VISIBLE) {
                    edt_sec_destination.setVisibility(View.GONE);
                } else {
                    edt_sec_destination.setVisibility(View.VISIBLE);
                }
                advanceDest2 = null;
                break;
            case R.id.edt_date:
                showDatePickerDialog();
                break;
            case R.id.edt_time:
                showTimePickerDialog();
                break;
            case R.id.edtDestination:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(DashboardActivity.this);
                    startActivityForResult(intent, 2);
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
                                    .build(DashboardActivity.this);
                    startActivityForResult(intent, 3);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            case R.id.edt_pickup:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(DashboardActivity.this);
                    startActivityForResult(intent, 4);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
                break;
            case R.id.imgScheduler:
                validate();

                break;

        }

    }

    private void hideVehiclesInAdvance() {
        mRelSmall.setVisibility(View.GONE);
        mRelMedium.setVisibility(View.GONE);
        mRelToing.setVisibility(View.GONE);
    }

    private void showVehiclesInAdvance() {
        mRelSmall.setVisibility(View.VISIBLE);
        mRelMedium.setVisibility(View.VISIBLE);
        mRelToing.setVisibility(View.VISIBLE);
    }


    private void validate() {
        if (edt_pickup.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Select pickup location", "OK");
        } else if (edtDestination.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Select destination location", "OK");
        } else if (edt_date.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Select booking date", "OK");
        } else if (edt_time.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Select booking time", "OK");
        } else if (edt_time.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Select booking time", "OK");
        } else if (aVehicleType == 0) {
            IOUtils.alertMessegeDialog(context, "Select vehicle type", "OK");
        } else {
            if (advanceVehicleType == 0) {
                IOUtils.toastMessage(context, "Please select vehicle type");
            } else {
                if (IOUtils.isNetworkAvailable(context)) {
                    createJsonobjectForFare();
                }
            }
        }
    }

    private void setCurrentLoc() {
        if (vehicleArrayList != null) {
            vehicleArrayList.clear();
        }

        vehicleArrayListTemp.clear();
        createJsonobjectForNearVehicles();
        if (mCurrentLocation != null) {
            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            utils.setPickup(latLng);
            mHomeMarker.setPosition(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

            if (IOUtils.getPlaceName(DashboardActivity.this, latLng) != null)
                mEdtSearchPlace.setText(IOUtils.getPlaceName(DashboardActivity.this, latLng));
            edt_pickup.setText(IOUtils.getPlaceName(DashboardActivity.this, latLng));

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                if (place != null) {
                    Log.i(TAG, "Place: " + place.getAddress());
                    mEdtSearchPlace.setText(place.getAddress());
                    LatLng latLng = place.getLatLng();

                    if (mMap != null) {
                        mMap.clear();
                    }
                    MarkerOptions options = new MarkerOptions().title("Pickup location")
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                            .anchor(0.5f, 0.5f);
                    options.position(latLng);
                    mHomeMarker = mMap.addMarker(options);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    createJsonobjectForNearVehicles();
                    utils.setPickup(latLng);
                }


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getAddress());
                edtDestination.setText(place.getAddress());
                advanceDest1 = place.getLatLng();


            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                edt_sec_destination.setText(place.getAddress());
                advanceDest2 = place.getLatLng();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                edt_pickup.setText(place.getAddress());
                advanceHome = place.getLatLng();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    startLocationUpdates();
                    break;
                case Activity.RESULT_CANCELED:
                    settingsrequest();//keep asking if imp or do whatever
                    break;
            }
        } else if (requestCode == 10) {
            if (resultCode == 10)
                onBackPressed();
        }

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

                        vehicleArrayList.get(j).getModel();

                        Log.e("getModel", "getModel===>" + vehicleArrayList.get(j).getModel());

                        break;
                    }
                }
            }
        }

        return false;
    }


    @Override
    public void onMapLongClick(LatLng latLng) {

        mHomeMarker.setPosition(latLng);
        mHomeMarker.setTitle("Pickup location");
        mEdtSearchPlace.setText(IOUtils.getPlaceName(DashboardActivity.this, latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        utils.setPickup(latLng);

    }

    @Override
    public void onFragmentInteraction(String s) {

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = new LatLng(-34, 151);

        if (mMap != null) {
            mMap.clear();

            MarkerOptions options = new MarkerOptions().title("Your Current location")
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                    .anchor(0.5f, 0.5f);
            options.position(latLng);
            mHomeMarker = mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            mMap.setOnMapLongClickListener(this);
            mMap.setOnMarkerClickListener(this);


        } else {
            LatLng sydney = new LatLng(-34, 151);
            mHomeMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Point your pickup location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }


    }

    protected void startIntentService(Location mLocation) {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        //  intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
        startService(intent);
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
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

            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }

            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();

        }
    }

    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build();
        createLocationRequest();
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(10);


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
                LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                mMap.clear();

                MarkerOptions options = new MarkerOptions().title("Your current location")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                        .anchor(0.5f, 0.5f);
                options.position(latLng);
                mHomeMarker = mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

                utils.setPickup(latLng);


                geocoder = new Geocoder(this, Locale.getDefault());

                try {

                    addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    city = addresses.get(0).getLocality();
                    state = addresses.get(0).getAdminArea();
                    country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                    knownName = addresses.get(0).getFeatureName();

                    // Only if available else return NULL
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mEdtSearchPlace.setText(address + "," + city + "," + state + "," + country + "," + postalCode + "," + knownName);


                // mEdtSearchPlace.setText(IOUtils.getPlaceName(DashboardActivity.this, latLng));

                createJsonobjectForNearVehicles();

            } else {
                Toast.makeText(getApplicationContext(), "Please start GPS", Toast.LENGTH_SHORT).show();
                LatLng latLng = new LatLng(-34, 151);

                if (mMap != null) {
                    mMap.clear();
                }
                MarkerOptions options = new MarkerOptions().title("Your current location")
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                        .anchor(0.5f, 0.5f);
                options.position(latLng);
                mMap.addMarker(options);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                mEdtSearchPlace.setText(IOUtils.getPlaceName(DashboardActivity.this, latLng));
            }
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        settingsrequest();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
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

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
            startUpdatesButtonHandler();
        }

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
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
        //       mPlaceArrayAdapter.setGoogleApiClient(null);
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


    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            edt_date.setText(month + "/" + day + "/" + year);
            mdate_time = year + "-" + month + "-" + day;
        }
    }

    private void showTimePickerDialog() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");

    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);


            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute, android.text.format.DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            edt_time.setText(IOUtils.getTime(hourOfDay, minute));
            mdate_time = mdate_time + " " + hourOfDay + ":" + minute + ":00";
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


        RequestQueue queue = Volley.newRequestQueue(DashboardActivity.this);


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


                                            Utils utils = new Utils(context);
                                            utils.setDriverInfo(vehicleArrayList.get(i).getDriverId(), vehicleArrayList.get(i).getName(), vehicleArrayList.get(i).getPhoto(), vehicleArrayList.get(i).getVehicleType(),
                                                    vehicleArrayList.get(i).getModel(), vehicleArrayList.get(i).getNumber(), vehicleArrayList.get(i).getLatitude(), vehicleArrayList.get(i).getLongitude());

                                            vehicleArrayList.get(i).getVehicleType();

                                            Log.e("getVehicleType", "getVehicleType===>" + vehicleArrayList.get(i).getVehicleType());


                                            if (vehicleArrayList.get(i).getVehicleType().equalsIgnoreCase("1")) {
                                                ddebbie_x = true;

                                            } else if (vehicleArrayList.get(i).getVehicleType().equalsIgnoreCase("2")) {

                                                taxi = true;
                                            } else if (vehicleArrayList.get(i).getVehicleType().equalsIgnoreCase("3")) {
                                                medium = true;
                                            } else if (vehicleArrayList.get(i).getVehicleType().equalsIgnoreCase("4")) {
                                                limousine = true;
                                            } else if (vehicleArrayList.get(i).getVehicleType().equalsIgnoreCase("5")) {
                                                suv = true;
                                            } else if (vehicleArrayList.get(i).getVehicleType().equalsIgnoreCase("6")) {
                                                towing = true;
                                            } else if (vehicleArrayList.get(i).getVehicleType().equalsIgnoreCase("7")) {
                                                rental = true;
                                            } else if (vehicleArrayList.get(i).getVehicleType().equalsIgnoreCase("8")) {
                                                wheel = true;
                                            }

                                        }
                                    }


                                setVehicle();
                                progressDialog.dismiss();

                            } else {
                                /*IOUtils.alertMessegeDialog(DashboardActivity.this, response.getString("response"), "OK");*/
                                Toast.makeText(getApplicationContext(), response.getString("response"), Toast.LENGTH_SHORT).show();

                                if (response.getString("response").equalsIgnoreCase("Vehicles not found")) {
                                    linear_id.setVisibility(View.VISIBLE);
                                } else {
                                    linear_id.setVisibility(View.GONE);
                                }
                                progressDialog.dismiss();
                            }

                            vehicleArrayListTemp.clear();
                            if (vehicleArrayList != null) {
                                for (int i = 0; i < vehicleArrayList.size(); i++) {
                                    if (Integer.parseInt(vehicleArrayList.get(i).getVehicleType()) == 1) {
                                        vehicleArrayListTemp.add(vehicleArrayList.get(i));
                                    } else {
                                        Log.e("check", "check==>");
                                    }
                                }
                            }

                            //  setVehicle();
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

    private void setVehicle() {
        LatLng l = mHomeMarker.getPosition();

        if (mMap != null) {
            mMap.clear();
        }
        MarkerOptions options = new MarkerOptions().title("Your current location")
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_pin))
                .anchor(0.5f, 0.5f);
        options.position(l);
        mHomeMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(l));

        markerOptionsArrayList = new ArrayList<>();
        Log.v("Cars", vehicleArrayListTemp.size() + "");
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

        Log.e("Check", "Check===>");

    }


    /*
     * createJsonobjectForAdvanceBooking - In this method we create JsonObject for api call...
     */

    public void createJsonobjectForAdvanceBooking() {
        try {
        /*    progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Loading..");
            progressDialog.setCancelable(false);
            progressDialog.show();*/
            IOUtils ioUtils = new IOUtils(DashboardActivity.this);
            userDetailsPojo = ioUtils.getUser();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());
            jsonObject.put(Constants.DATE_TIME, mdate_time);
            jsonObject.put(Constants.PICKUP_LOC, edt_pickup.getText().toString().trim());
            jsonObject.put(Constants.DROPOFF_LOC, edtDestination.getText());
            jsonObject.put(Constants.RIDE_TYPE, "2");
            jsonObject.put(Constants.FROM_LONG, "" + advanceHome.longitude);
            jsonObject.put(Constants.FROM_LAT, "" + advanceHome.latitude);

            if (advanceDest2 != null) {
                jsonObject.put("toLongitude", "" + advanceDest2.longitude);
                jsonObject.put("toLatitude", "" + advanceDest2.latitude);
                jsonObject.put("vehiclesTypeId", aVehicleType);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("lat", "" + advanceDest1.latitude);
                jsonObject1.put("long", "" + advanceDest1.longitude);
                jsonObject1.put("address", edtDestination.getText().toString());
                jsonArray.put(jsonObject1);
                jsonObject.put("destinations", jsonArray);

            } else {
                jsonObject.put("toLongitude", "" + advanceDest1.longitude);
                jsonObject.put("toLatitude", "" + advanceDest1.latitude);
                jsonObject.put("vehiclesTypeId", aVehicleType);
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("lat", "" + advanceDest1.latitude);
                jsonObject1.put("long", "" + advanceDest1.longitude);
                jsonObject1.put("address", edtDestination.getText().toString());
                jsonArray.put(jsonObject1);
                jsonObject.put("destinations", jsonArray);

            }

            jsonObject.put(Constants.PROMO, IOUtils.PROMO_CODE);
            jsonObject.put(Constants.DISC, IOUtils.DISCOUNT);
            jsonObject.put(Constants.PAY_MODE, "2");
            jsonObject.put(Constants.TOTAL_AMT, rate);

            Log.v("JsonObject", jsonObject.toString());
            advanceBookingApiCall(jsonObject);
        } catch (Exception e) {
            progressDialog.dismiss();
        }
    }


    public void advanceBookingApiCall(JSONObject js) {

        RequestQueue queue = Volley.newRequestQueue(DashboardActivity.this);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_NEW_RIDE, js,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            if (response.getBoolean("result")) {

                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
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


    public void createJsonobjectForFare() {


        distance(advanceHome.latitude, advanceHome.longitude, advanceDest1.latitude, advanceDest1.longitude);
        try {

            IOUtils ioUtils = new IOUtils(context);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("fromLongitude", "" + advanceHome.longitude);
            jsonObject.put("fromLatitude", "" + advanceHome.latitude);
            if (advanceDest2 != null) {
                jsonObject.put("toLongitude", "" + advanceDest2.longitude);
                jsonObject.put("toLatitude", "" + advanceDest2.latitude);
                jsonObject.put("vehiclesTypeId", aVehicleType);
                jsonObject.put("numOfPassengers", 1);
                JSONArray jsonArray = new JSONArray();

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("lat", "" + advanceDest1.latitude);
                jsonObject1.put("long", "" + advanceDest1.longitude);
                jsonObject1.put("address", edt_pickup.getText().toString());
                jsonArray.put(jsonObject1);
                jsonObject.put("destinations", jsonArray);
                //fareApiCall(jsonObject);

            } else {
                jsonObject.put("toLongitude", "" + advanceDest1.longitude);
                jsonObject.put("toLatitude", "" + advanceDest1.latitude);
                jsonObject.put("vehiclesTypeId", aVehicleType);
                jsonObject.put("distance", "" + session.getdistance());
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("lat", "" + advanceDest1.latitude);
                jsonObject1.put("long", "" + advanceDest1.longitude);
                jsonObject1.put("address", edt_pickup.getText().toString());
                jsonArray.put(jsonObject1);
                // jsonObject.put("destinations", jsonArray);


                // fareApiCall(jsonObject);


                userDetailsPojo = ioUtils.getUser();
                JSONObject jsonObj = new JSONObject();
                jsonObj.put(Constants.EMAIL, userDetailsPojo.getEmail());
                jsonObj.put(Constants.DATE_TIME, mdate_time);
                jsonObj.put(Constants.PICKUP_LOC, edt_pickup.getText().toString().trim());
                jsonObj.put(Constants.DROPOFF_LOC, edtDestination.getText());
                jsonObj.put(Constants.RIDE_TYPE, "2");
                jsonObj.put(Constants.FROM_LONG, "" + advanceHome.longitude);
                jsonObj.put(Constants.FROM_LAT, "" + advanceHome.latitude);

                if (advanceDest2 != null) {
                    jsonObj.put("toLongitude", "" + advanceDest2.longitude);
                    jsonObj.put("toLatitude", "" + advanceDest2.latitude);
                    jsonObj.put("vehiclesTypeId", aVehicleType);
                    JSONArray jsonArray1 = new JSONArray();
                    JSONObject jsonObj1 = new JSONObject();
                    jsonObj1.put("lat", "" + advanceDest1.latitude);
                    jsonObj1.put("long", "" + advanceDest1.longitude);
                    jsonObj1.put("address", edtDestination.getText().toString());
                    jsonArray1.put(jsonObj1);
                    jsonObj.put("destinations", jsonArray1);

                } else {
                    //new

                    jsonObj.put("toLongitude", "" + advanceDest1.longitude);
                    jsonObj.put("toLatitude", "" + advanceDest1.latitude);
                    jsonObj.put("vehiclesTypeId", aVehicleType);
                    JSONArray jsonArray2 = new JSONArray();
                    JSONObject jsonObj2 = new JSONObject();
                    jsonObj2.put("lat", "" + advanceDest1.latitude);
                    jsonObj2.put("long", "" + advanceDest1.longitude);
                    jsonObj2.put("address", edtDestination.getText().toString());
                    jsonArray2.put(jsonObj2);
                    jsonObj.put("destinations", jsonArray2);

                }

                jsonObj.put(Constants.PROMO, IOUtils.PROMO_CODE);
                jsonObj.put(Constants.DISC, IOUtils.DISCOUNT);
                jsonObj.put(Constants.PAY_MODE, "2");
                jsonObj.put(Constants.PAYMENT_STATUS, "0");
                jsonObj.put(Constants.TOTAL_AMT, rate);

                Log.v("jsonObj", jsonObj.toString());


                String json1 = jsonObj.toString();
                String json = jsonObject.toString();
                Intent intent = new Intent(context, AdvanceFareEstimateActivity.class);
                intent.putExtra("json", json);
                intent.putExtra("json1", json1);
                intent.putExtra("wheelchairStatus", isWheelSelected);
                startActivityForResult(intent, 10);

            }
            Log.v("JsonObject", jsonObject.toString());
        } catch (Exception e) {


        }
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
                                rate = String.valueOf(response.getInt("rate"));
                                createJsonobjectForAdvanceBooking();
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

    private double distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        //  Log.e("distance==>", "dist==>" + String.format("%.2f", dist));
        session.setdistance(String.format("%.2f", dist));

        return (dist);


    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

}
