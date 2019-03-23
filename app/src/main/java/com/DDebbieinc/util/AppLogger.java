package com.DDebbieinc.util;

import android.os.Bundle;

import com.DDebbieinc.App;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by macbookappsplanet on 08/02/18.
 */

public class AppLogger {

    public static void generateLog(String event) {

        App.get().logEvent(event, null);
    }

}
