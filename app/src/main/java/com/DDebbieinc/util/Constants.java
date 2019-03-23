package com.DDebbieinc.util;

/**
 * Created by savera on 10/2/16.
 */
public interface Constants {

/*
    String URL_BASE = "http://vishalbhosale.com/projects/debbie/?r=";
*/

    String URL_BASE = "http://www.ddebbie.com/api/?r=";
    String URL_SIGNIN = URL_BASE + "customers/login";
    String URL_SIGNUP = URL_BASE + "customers/register";
    String URL_FORGOT_PASSWORD = URL_BASE + "customers/forgotpassword";
    String URL_PROFILE_UPDATE = URL_BASE + "customers/profileupdate";
    String URL_NEAR_BY = URL_BASE + "customers/nearbyvehicles";
    String URL_NEAR_BY_NEW = URL_BASE + "Vehicle/VehicleTypes";
    String URL_NEW_RIDE = URL_BASE + "customers/newride";
    String URL_PROMOCODE = URL_BASE + "customers/validatepromocode";
    String URL_FARE = URL_BASE + "customers/fareestimate";
    String URL_GET_DIVER_LOCATION = URL_BASE + "drivers/location";
    String URI_UPLOAD_PROF_IMG =  URL_BASE + "customers/uploadphoto";
    String URL_REJECT_RIDE = URL_BASE + "customers/cancelride";
    String IMAGE_BASE_URL = "http://www.ddebbie.com/api/dimg/";
    String URL_RATE_RIDE = URL_BASE + "customers/rateride";
    String RATE = "rating";


    // ************************************GCM SENDER ID*****************************************

    String GCM_SENDER_ID="976236736209";

    // ************************************HEADER KEYS*****************************************

    String CONTENT_TYPE = "Content-Type";
    String API_KEY = "Api-Key";

    // ************************************HEADER VALUES*****************************************

    String CONTENT_TYPE_VALUE = "application/json";
    String API_KEY_VALUE = "8b64d2451b7a8f3fd17390f88ea35917";

    // ************************************PARAMETER KEYS*****************************************

    String ID = "id";
    String DRIVER_ID = "driverId";
    String CUSTOMER_ID = "customerId";
    String NEW_EMAIL = "newEmail";
    String CUSTOMER_NAME = "customerName";
    String EMAIL = "email";
    String PASSWORD = "password";
    String DEVICE_TOKEN = "deviceToken";
    String DEVICE_TYPE = "deviceType";
    String CONTACT_NUMBER = "contactNumber";
    String EMERGENCY_CONTACT_NUMBER = "emgContactNumber";

    // ************************************USER_LOCATION KEYS*****************************************

    String USER_LAT = "lat";
    String USER_LONG = "long";
    String DATE_TIME = "dateTime";
    String PICKUP_LOC = "pickUpLocation";
    String DROPOFF_LOC = "dropOffLocation";
    String RIDE_TYPE = "rideType";
    String FROM_LAT = "fromLatitude";
    String FROM_LONG = "fromLongitude";
    String TO_LAT = "toLatitude";
    String TO_LONG = "toLongitude";
    String PAY_MODE = "paymentMode";
    String TOTAL_AMT = "totalAmount";
    String PROMO = "promoCode";
    String DISC = "discount";
    String PAYMENT_STATUS = "paymentStatus";
    int LOCATION_INTERVAL = 1000 * 15;
    String PHOTO = "photo";
    String RIDE_ID = "rideId" ;
    String PAYPAL_CLIENT_ID = "AdzOuI4QBsCljaBN5I_tRj1dka9DM4ehsttRDL1yd-xWs-Jp-dCFfyfMCPJmDwGnn1x5CfEhId0xF7WI";

    String URL_UPDATE_PAYMENT = URL_BASE + "customers/updatepayment";
    String URL_WAITING_CHARGE = URL_BASE + "customers/WaitingChargePayTransactionId";
    String FB_AD_ID = "1163726370430540_1163729680430209";

 /*   Read the integration guide and download the SDK here:
    https://developers.facebook.com/docs/audience-network/android
    Platform:	Android app
    Format:	Interstitial
    Placement ID:	1970719276578730_1970719486578709*/

}
