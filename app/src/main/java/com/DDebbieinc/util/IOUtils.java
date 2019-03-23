package com.DDebbieinc.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import me.philio.pinentry.PinEntryView;

/**
 * Created by savera on 10/2/16.
 */
public class IOUtils {

    private  SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static String mSharedPrefName = "user_deatils";
    public static String mCurrentUser = "CurrentUser";
    public static String PROMO_CODE = "";
    public static String DISTANCE1= "";
    public static String DURATION1= "";
    public static String DISTANCE2= "";
    public static String DURATION2= "";
   // public static LatLng CURRENT_LATLNG = null;
    public static LatLng DEST1_LATLNG = null;
    public static LatLng DEST2_LATLNG = null;
    public static String DISCOUNT = "";
    private Context context;
    private ProgressDialog progressDialog;
    public static String TOTAL_DISTANCE= "";
    public static String TOTAL_TIME= "";
    public static String ARRIVAL_TIME= "";
    public static String DRIVER_DISTANCE= "";
    public static String PLACE1 = "" , PLACE2 = "" ;
    public String RATE = "";
    public static int VEHICLE_TYPE = 1;
    public static String ARRIVAL = "";
    public static String title, msg, rideId, pEst, pId, pName, pLoc,pPhone, pEmail, pTime, pPick, pDest, fromLang, fromLat, toLang, toLat, totalKm;

    public static boolean RUNNING = false;


    public IOUtils(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        this.context = context;

    }

    public void setTotalTime(String t){
        editor.putString("total_time", t);
        editor.commit();
    }

    public String getTotalTime(){
        return preferences.getString("total_time", "");
    }

    public void setArrivalTime(String t){
        editor.putString("arrival_time", t);
        editor.commit();
    }

    public String getArrivalTime(){
        return preferences.getString("total_time", "");
    }

    public void setRate(String t){
        editor.putString("rate", t);
        editor.commit();
    }

    public String getRate(){
        return preferences.getString("rate", "");
    }


    public void setUser(UserDetailsPojo user) {
        Log.e("", "set user value" + user);

        Gson gson=new Gson();
        if (user != null) {
            editor.putString(mCurrentUser,gson.toJson(user)).commit();
        }
        else {
            editor.putString(mCurrentUser, null).commit();
        }

    }

    public UserDetailsPojo getUser() {
        Log.e("", "**getUser**");

        Gson gson=new Gson();
        if (preferences.getString(mCurrentUser, null) != null) {
		/*	return null;
		} else {*/
            return gson.fromJson(preferences.getString(mCurrentUser, null),UserDetailsPojo.class);
        }
        else
        {
            return null;
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

        }catch (Exception e){

        }
    }

    public static void alertMessegeDialog(Context context,String message,String positiveButtonName){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton(positiveButtonName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // TODO Auto-generated method stub
                paramDialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    public static void toastMessage(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    public static ProgressDialog getProgessDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(null);
        dialog.setMessage("Loading...");
        return dialog;
    }



    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static void setAlertForActivity(final Context context) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        // Add the buttons
        builder.setMessage("Please check network connection");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(context, "Network connection not available",
                        Toast.LENGTH_SHORT).show();

                Intent dialogIntent = new Intent(
                        android.provider.Settings.ACTION_SETTINGS);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(dialogIntent);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        // Set other dialog properties

        // Create the AlertDialog
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static String getTime(int hr,int min) {
        Time tme = new Time(hr,min,0);//seconds by default set to zero
        Format formatter;
        formatter = new SimpleDateFormat("h:mm a");
        return formatter.format(tme);
    }

    public void showPromo() {

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.promocode_dialog);
        //dialog.setTitle("Title...");
        // set the custom dialog components - text, image and button
        final PinEntryView pinEntryView = (PinEntryView) dialog.findViewById(R.id.edtPromo);


        Button btnCanel = (Button) dialog.findViewById(R.id.btnCancel);
        // if button is clicked, close the custom dialog
        btnCanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);
        // if button is clicked, close the custom dialog
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!pinEntryView.getText().toString().equals("")) {
                    PROMO_CODE = pinEntryView.getText().toString();
                    dialog.dismiss();
                    createJsonobjectForPromocode ();
                } else {
                    Toast.makeText(context, "Invalid Promocode", Toast.LENGTH_SHORT).show();
                    PROMO_CODE = "";
                }
            }
        });

        dialog.show();

    }


  /*  public void setCurrentLoc(Context context,String lat, String lng){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.putString("lat", lat);
        editor.putString("lng", lng);

    }

    public String getCurrentLat(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("lat", "none");

    }

    public String getCurrentLng(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        return preferences.getString("lng","none");

    }

    public void setDestination1Loc(Context context,String lat, String lng){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.putString("dest1_lat", lat);
        editor.putString("dest1_lng", lng);
    }

    public String getDestination1Lat(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dest1_lat","none");
    }

    public String getDestination1Lng(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dest1_lng","none");
    }


    public void setDestination2Loc(Context context, String lat, String lng){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        editor.putString("dest2_lat", lat);
        editor.putString("dest2_lng", lng);
    }

    public String getDestination2Lat(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dest2_lat","none");
    }

    public String getDestination2Lng(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("dest2_lng","none");
    }*/

    public static String getPlaceName(Context context, LatLng latLng){
        Geocoder gcd = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = gcd.getFromLocation(latLng.latitude, latLng.longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses !=null) {
            String address="";
            for(int i=0;i<addresses.get(0).getMaxAddressLineIndex();i++){
                address = address+addresses.get(0).getAddressLine(i)+",";

            }

            return address;
        }
        return null;
    }


    public void createJsonobjectForPromocode () {
        try {
            showProgress("Applying Promocode..");
            IOUtils ioUtils = new IOUtils(context);
            UserDetailsPojo userDetailsPojo = ioUtils.getUser();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());
            jsonObject.put(Constants.CUSTOMER_ID, userDetailsPojo.getId());
            jsonObject.put(Constants.PROMO, IOUtils.PROMO_CODE);
            Log.v("JsonObject", jsonObject.toString());
            prompcodeApiCall(jsonObject);
        } catch (Exception e) {
            hideProgress();
        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    */

    public void prompcodeApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(context);


        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_PROMOCODE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        try {
                            if (response.getBoolean("result")) {
                                IOUtils.alertMessegeDialog(context, response.getString("response"),"OK");
                                DISCOUNT = response.getString("discount");
                            }else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"),"OK");
                            }
                            hideProgress();
                        } catch (JSONException e) {
                            hideProgress();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                hideProgress();

            }
        });


        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);

        queue.add(jsonObjReq);

    }

    public void showProgress(String message){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    public void hideProgress(){
        progressDialog.dismiss();
    }

    public void clearData(){
        editor.clear();
        editor.commit();
    }
}
