package com.DDebbieinc.activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.util.Constants;
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
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.scottyab.showhidepasswordedittext.ShowHidePasswordEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SigninActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mBtnSignin, mBtnCancle, mBtnForgot, mBtnRegister;
    private EditText mEdtEmail;
    private ShowHidePasswordEditText mEdtPassword;
    private Context context = this;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private ProgressDialog dialog;
    private LinearLayout main_layout;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        gcmRegistration();
        mBtnSignin = (Button) findViewById(R.id.btnSignIN);
        mBtnCancle = (Button) findViewById(R.id.btnCancel);
        mBtnForgot = (Button) findViewById(R.id.btnForgot);
        mBtnRegister = (Button) findViewById(R.id.btnRegister);

        mEdtEmail = (EditText) findViewById(R.id.edtEmail);
        mEdtPassword = (ShowHidePasswordEditText) findViewById(R.id.edtPassword);

        main_layout = (LinearLayout) findViewById(R.id.main_layout);

        main_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IOUtils.hideSoftKeyboard(SigninActivity.this);

            }
        });


        mBtnCancle.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mBtnForgot.setOnClickListener(this);
        mBtnSignin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIN:
                /*Intent intent2 = new Intent(SigninActivity.this, DashboardActivity.class);
                startActivity(intent2);
                finish();*/
                validate();
                break;
            case R.id.btnCancel:
                finish();
                break;
            case R.id.btnForgot:
                Intent intent = new Intent(SigninActivity.this, ForgotActivity.class);
                startActivity(intent);
                break;
            case R.id.btnRegister:
                Intent intent1 = new Intent(SigninActivity.this, SignUpActivity.class);
                startActivity(intent1);
                break;

        }
    }


    /*
   * validate - This method is used validate app all the feild...
   */
    public void validate() {


        if (mEdtEmail.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Please enter your Email.", "OK");
        } else if (!mEdtEmail.getText().toString().matches(EMAIL_PATTERN)) {
            IOUtils.alertMessegeDialog(context, "Please enter valid Email.", "OK");
        } else if (mEdtPassword.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(context, "Please enter Password.", "OK");
        } else {

            if (IOUtils.isNetworkAvailable(SigninActivity.this)) {
                try {
                    createJsonobjectForApiCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                IOUtils.setAlertForActivity(SigninActivity.this);
            }


        }
    }
   /*
    *
    * gcmRegistration - This method is user to register Device Google Cloud messaging...
    *
    * */

    public void gcmRegistration() {
        new AsyncTask() {

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    token =
                            InstanceID.getInstance(context).getToken(Constants.GCM_SENDER_ID,
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                    Log.v("Registration id", token);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }.execute(null, null, null);
    }


    /*
    * createJsonobjectForApiCall - In this method we create JsonObject for api call...
    */

    public void createJsonobjectForApiCall() {
        try {
            dialog = IOUtils.getProgessDialog(context);
            dialog.show();

            JSONObject jsonObject = new JSONObject();




         /*   jsonObject.put(Constants.EMAIL,"tushar02.katakdound@gmail.com");
            jsonObject.put(Constants.PASSWORD,"123456");
*/
            jsonObject.put(Constants.EMAIL, mEdtEmail.getText().toString());
            jsonObject.put(Constants.PASSWORD, mEdtPassword.getText().toString());
            jsonObject.put(Constants.DEVICE_TOKEN, token);
            jsonObject.put(Constants.DEVICE_TYPE, "1");
            Log.v("JsonObject", jsonObject.toString());


            regitrationApiCall(jsonObject);


        } catch (Exception e) {

        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    */
    public void regitrationApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(SigninActivity.this);

        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_SIGNIN, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());

                        //{"result": "true","response": "Successfully login","customerData": {"id": "5","customerName": "Tushar Katakdound ",
                        //            "contactNumber": "8983472919", "emgContactNumber": "8983472919", "email": "tushar02.katakdound@gmail.com", "status": "1"}}


                        try {
                            if (response.getString("response").equals("Successfully login")) {

                                JSONObject jsonObject = response.getJSONObject("customerData");

                                UserDetailsPojo userDetailsPojo = new UserDetailsPojo();
                                userDetailsPojo.setId(jsonObject.getString("id"));
                                userDetailsPojo.setCustomerName(jsonObject.getString("customerName"));
                                userDetailsPojo.setContactNumber(jsonObject.getString("contactNumber"));
                                userDetailsPojo.setEmgContactNumber(jsonObject.getString("emgContactNumber"));
                                userDetailsPojo.setEmail(jsonObject.getString("email"));
                                userDetailsPojo.setStatus(jsonObject.getString("status"));
                                Utils util = new Utils(context);
                                util.setDP(jsonObject.getString("photo"));
                                IOUtils ioUtils = new IOUtils(context);
                                ioUtils.setUser(userDetailsPojo);

                                IOUtils.toastMessage(context, response.getString("response"));

                                Intent intent2 = new Intent(SigninActivity.this, DashboardActivity.class);
                                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent2);
                                finish();


                            } else {
                                IOUtils.alertMessegeDialog(context, response.getString("response"), "OK");
                            }

                            dialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dialog.dismiss();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Response", "Error: " + error.getMessage());
                dialog.dismiss();
            }
        });


        int socketTimeout = 30000;//30 seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsonObjReq.setRetryPolicy(policy);

        queue.add(jsonObjReq);


    }

}
