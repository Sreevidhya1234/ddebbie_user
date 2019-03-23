package com.DDebbieinc.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.DDebbieinc.R;
import com.DDebbieinc.activity.DashboardActivity;
import com.DDebbieinc.activity.FareEstimateActivity;
import com.DDebbieinc.activity.NotificationActivity;
import com.DDebbieinc.activity.PromocodeActivity;
import com.DDebbieinc.activity.RidersInfoActivity;
import com.DDebbieinc.entity.PromoNotification;
import com.DDebbieinc.entity.RideAccept;
import com.DDebbieinc.helper.DatabaseHandler;
import com.DDebbieinc.util.Consts;
import com.DDebbieinc.util.Utils;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tushar Katakdound on 7/28/2015.
 */


/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService {
    private String paymentStatus = "0";
    private Gson gson;
    private RideAccept rideAccept;
    private PromoNotification promoNotification;
    private DatabaseHandler db = new DatabaseHandler(this);
    private Utils utils;
    private long[] v = {500, 1000};

    public GcmService() {

    }

    @Override
    public void onMessageReceived(String from, Bundle data) {


        utils = new Utils(this);

        Log.v("Notification", from + " " + data.toString());
        String message = data.getString("message");
       /* [{message={
       "rideId":"5",
       "pickUpLocation":"Karve Nagar",
       "dropOffLocation":"Narveer Tanaji Wadi",
       "noOfPassengers":"0",
       "noOfBags":"0",
       "totalAmount":"40.00",
       "specialInstruction":"",
       "rideType":"1",
       "fromLongitude":"73.8156599","fromLatitude":"18.4932139",
       "toLongitude":"73.8474647","toLatitude":"18.5308225",
       "promoCode":"",
       "discount":"0.00","totalKm":"0.00",
       "paymentMode":"2","paymentStatus":"1",
       "destinations":"","status":"0","driverId":"0",
       "action":"RIDE_ACCEPTED",
       "description":"Ride Accepted","title":"Ride Accepted"}, collapse_key=do_not_collapse}]
*/

        //Promocode Push
        //{"title":"DD10",
        // "body":"10% discount",
        // "link":"",
        // "action":"GENERAL_PUSH",
        // "image":""}

        try {
            JSONObject jsonObject = new JSONObject(message);


            gson = new Gson();
            rideAccept = gson.fromJson(jsonObject.toString(), RideAccept.class);
            promoNotification = gson.fromJson(jsonObject.toString(), PromoNotification.class);

            if (promoNotification.getAction().equals("PROMO_PUSH")) {

                db.addPromo(promoNotification);
                Intent intent = new Intent(this, PromocodeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), (int) System.currentTimeMillis(), intent, 0);

                Notification noti = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    noti = new Notification.Builder(this)
                            .setContentTitle(promoNotification.getTitle())
                            .setContentText(promoNotification.getBody())
                            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                            .setVibrate(v)
                            .setLights(0xff00ff00, 300, 100)
                            .setSmallIcon(R.mipmap.notification)
                            .setContentIntent(pIntent)
                            .build();
                }
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                // hide the notification after its selected
                noti.flags |= Notification.FLAG_AUTO_CANCEL;
                // Play default notification sound
                noti.defaults |= Notification.DEFAULT_SOUND;
                noti.defaults |= Notification.FLAG_SHOW_LIGHTS;
                // Vibrate if vibrate is enabled
                noti.defaults |= Notification.DEFAULT_VIBRATE;
                notificationManager.notify(0, noti);
            }

            if (rideAccept.getAction().equals("RIDE_ACCEPTED")) {
                Consts.Confirm_check = "1";
                paymentStatus = jsonObject.getString("paymentStatus");
                if (paymentStatus.equals("1")) {
                    utils.setPaymentStatus(true);

                } else {
                    utils.setPaymentStatus(false);
                }

                if (FareEstimateActivity.active) {
                    Intent intent = new Intent(this, RidersInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ride", rideAccept);
                    intent.putExtra("is_advance", false);
                    startActivity(intent);
                    showNotification();

                } else {

                    if (DashboardActivity.active) {
                        Intent intent = new Intent(this, RidersInfoActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("ride", rideAccept);
                        intent.putExtra("is_advance", true);
                        startActivity(intent);
                        showNotification();
                    } else {
                        generateNotification(getBaseContext(), message);
                    }


                }

            }


            if (rideAccept.getAction().equals("AARIVED_AT")) {
                if (RidersInfoActivity.active) {
                    Intent intent = new Intent(this, NotificationActivity.class);
                    intent.putExtra("waiting", true);
                    intent.putExtra("message", rideAccept.getDescription());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    showNotification();

                } else {
                    Intent intent = new Intent(getBaseContext(), NotificationActivity.class);
                    intent.putExtra("waiting", true);
                    intent.putExtra("message", rideAccept.getDescription());
                    PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), (int) System.currentTimeMillis(), intent, 0);

                    Notification noti = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        noti = new Notification.Builder(getBaseContext())
                                .setContentTitle(rideAccept.getTitle())
                                .setContentText(rideAccept.getDescription())
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setVibrate(v)
                                .setLights(0xff00ff00, 300, 100)
                                .setSmallIcon(R.mipmap.notification)
                                .setContentIntent(pIntent).build();
                    }
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    // Play default notification sound
                    noti.defaults |= Notification.DEFAULT_SOUND;
                    noti.defaults |= Notification.FLAG_SHOW_LIGHTS;

                    noti.defaults |= Notification.FLAG_NO_CLEAR;
                    // Vibrate if vibrate is enabled
                    noti.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, noti);
                }

            }


            if (rideAccept.getAction().equals("DRIVER_WAITING")) {

                if (RidersInfoActivity.active) {
                    Intent intent = new Intent(this, NotificationActivity.class);
                    intent.putExtra("waiting", true);
                    intent.putExtra("message", rideAccept.getDescription());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    showNotification();

                } else {
                    Intent intent = new Intent(getBaseContext(), NotificationActivity.class);
                    intent.putExtra("waiting", true);
                    intent.putExtra("message", rideAccept.getDescription());
                    PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), (int) System.currentTimeMillis(), intent, 0);

                    Notification noti = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        noti = new Notification.Builder(getBaseContext())
                                .setContentTitle(rideAccept.getTitle())
                                .setContentText(rideAccept.getDescription())
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setVibrate(v)
                                .setLights(0xff00ff00, 300, 100)
                                .setSmallIcon(R.mipmap.notification)
                                .setContentIntent(pIntent).build();
                    }
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    // Play default notification sound
                    noti.defaults |= Notification.DEFAULT_SOUND;
                    noti.defaults |= Notification.FLAG_SHOW_LIGHTS;

                    noti.defaults |= Notification.FLAG_NO_CLEAR;
                    // Vibrate if vibrate is enabled
                    noti.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, noti);
                }

            }


            if (rideAccept.getAction().equals("DIRVER_BUSY")) {

                if (FareEstimateActivity.active) {
                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.putExtra("message", rideAccept.getDescription());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    showNotification();


                } else {
                    Intent intent = new Intent(getBaseContext(), DashboardActivity.class);
                    intent.putExtra("message", rideAccept.getDescription());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), (int) System.currentTimeMillis(), intent, 0);

                    Notification noti = null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        noti = new Notification.Builder(getBaseContext())
                                .setContentTitle(rideAccept.getTitle())
                                .setContentText(rideAccept.getDescription())
                                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                                .setVibrate(v)
                                .setLights(0xff00ff00, 300, 100)
                                .setSmallIcon(R.mipmap.notification)
                                .setContentIntent(pIntent).build();
                    }
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;
                    // Play default notification sound
                    noti.defaults |= Notification.DEFAULT_SOUND;
                    noti.defaults |= Notification.FLAG_SHOW_LIGHTS;

                    noti.defaults |= Notification.FLAG_NO_CLEAR;
                    // Vibrate if vibrate is enabled
                    noti.defaults |= Notification.DEFAULT_VIBRATE;
                    notificationManager.notify(0, noti);
                }

            }


            if (rideAccept.getAction().equals("RIDE_REJECTED")) {

                    Intent intent = new Intent(this, DashboardActivity.class);
                    intent.putExtra("message", rideAccept.getDescription());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    showNotification();


            }


        } catch (JSONException e) {
        }


    }

    private void showNotification() {
        Notification noti = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            noti = new Notification.Builder(this)
                    .setContentTitle(rideAccept.getTitle())
                    .setContentText(rideAccept.getDescription())
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setVibrate(v)
                    .setLights(0xff00ff00, 300, 100)
                    .setSmallIcon(R.mipmap.notification)
                    .build();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        // Play default notification sound
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.FLAG_SHOW_LIGHTS;
        // Vibrate if vibrate is enabled
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, noti);
    }


    @Override
    public void onDeletedMessages() {
        generateNotification(getBaseContext(), "Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        generateNotification(getBaseContext(), "Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        generateNotification(getBaseContext(), "Upstream message send error. Id=" + msgId);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void generateNotification(Context context, String message) {

        Intent intent = new Intent(context, RidersInfoActivity.class);
        intent.putExtra("ride", rideAccept);
        if (paymentStatus.equals("1")) {
            intent.putExtra("is_advance", true);

        } else {
            intent.putExtra("is_advance", false);
        }

        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.color.colorPrimary);

        Notification noti = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            noti = new Notification.Builder(context)
                    .setContentTitle(rideAccept.getTitle())
                    .setContentText(rideAccept.getDescription())
                    .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                    .setVibrate(v)
                    .setLights(0xff00ff00, 300, 100)
                    .setSmallIcon(R.mipmap.notification)
                    .setContentIntent(pIntent).build();
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // hide the notification after its selected
        noti.flags |= Notification.FLAG_AUTO_CANCEL;
        // Play default notification sound
        noti.defaults |= Notification.DEFAULT_SOUND;
        noti.defaults |= Notification.FLAG_SHOW_LIGHTS;

        noti.defaults |= Notification.FLAG_NO_CLEAR;
        // Vibrate if vibrate is enabled
        noti.defaults |= Notification.DEFAULT_VIBRATE;
        notificationManager.notify(0, noti);

    }
}