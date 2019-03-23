package com.DDebbieinc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by appsplanet on 19/3/16.
 */
public class Utils {
    public Context context;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;

    public Utils(Context context) {
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }


    public void setDonation(String donation) {
        editor.putString("donation", donation);
        editor.commit();
    }

    public String getDonation() {
        return sharedPreferences.getString("donation", "1");
    }

    public void setPickup(LatLng latLng) {
        editor.putString("pick_lat", String.valueOf(latLng.latitude));
        editor.putString("pick_long", String.valueOf(latLng.longitude));
        editor.commit();
    }

    public LatLng getPickup() {
        LatLng latLng = new LatLng(Double.parseDouble(sharedPreferences.getString("pick_lat", "43.7608103")), Double.parseDouble(sharedPreferences.getString("pick_long", "-79.4144845")));
        return latLng;
    }

    public void setKill(boolean t) {
        editor.putBoolean("kill", t);
        editor.commit();
    }

    public boolean getKill() {
        return sharedPreferences.getBoolean("kill", false);
    }


    public void setPaymentStatus(boolean status) {
        editor.putBoolean("payment_status", status);
        editor.commit();
    }

    public boolean getPaymentStatus() {
        return sharedPreferences.getBoolean("payment_status", false);
    }

    public void setDriverInfo(String driverid, String name, String photo, String vehicleTypeId,
                              String vehicleModel, String vehicleNumber, String latitude, String longitude) {
        editor.putString("driver_id", driverid);
        editor.putString("driver_name", name);
        editor.putString("driver_photo", photo);
        editor.putString("driver_vid", vehicleTypeId);
        editor.putString("driver_vmodel", vehicleModel);
        editor.putString("driver_vnumber", vehicleNumber);
        editor.putString("driver_lat", latitude);
        editor.putString("driver_long", longitude);
        editor.commit();
    }

    public void setDP(String dpurl) {
        editor.putString("google_dp_url", dpurl);
        editor.commit();

    }

    public String getDP() {
        return sharedPreferences.getString("google_dp_url", "");
    }

    public void setRideStatus(boolean s) {
        editor.putBoolean("rideStatus", s);
        editor.commit();
    }

    public boolean getRideStatus() {
        return sharedPreferences.getBoolean("rideStatus", false);

    }

    public String getDriverId() {
        return sharedPreferences.getString("driver_id", "");
    }

    public String getDriverName() {
        return sharedPreferences.getString("driver_name", "");
    }

    public String getDriverPhoto() {
        return sharedPreferences.getString("driver_photo", "");
    }

    public String getDriverVehicleId() {
        return sharedPreferences.getString("driver_vid", "");
    }

    public String getDriverModel() {
        return sharedPreferences.getString("driver_vmodel", "");
    }

    public String getDriverNumber() {
        return sharedPreferences.getString("driver_vnumber", "");
    }

    public String getDriverLat() {
        return sharedPreferences.getString("driver_lat", "");
    }

    public String getDriverLong() {
        return sharedPreferences.getString("driver_long", "");
    }


    public String totalTime(String[] timeItems) {
        // as example for visibility
        try {
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
            return time.replace("0 days", "").replace("0 hours", "");
        } catch (NumberFormatException n) {
            n.printStackTrace();
            return "";
        }

    }
}
