package com.DDebbieinc.util;

/**
 * Created by appsplanet on 20/4/16.
 */
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;

public class Config {

    // PayPal app configuration
    //Sandbox
    //public static final String PAYPAL_CLIENT_ID = "AdzOuI4QBsCljaBN5I_tRj1dka9DM4ehsttRDL1yd-xWs-Jp-dCFfyfMCPJmDwGnn1x5CfEhId0xF7WI";

    //Live
    public static final String PAYPAL_CLIENT_ID = "AYoPGMs6ldic_kHnE1X2N72L12wAH0dpI2gFcR6T2HFfUm88U-2YCClHnA4Z18ukfYfKzrsn-LUhFxWO";

    public static final String PAYPAL_CLIENT_SECRET = "";

    //Sandbox
  //  public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    //Production
    public static final String PAYPAL_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_PRODUCTION;
    public static final String PAYMENT_INTENT = PayPalPayment.PAYMENT_INTENT_SALE;
    public static final String DEFAULT_CURRENCY = "CAD";



}
