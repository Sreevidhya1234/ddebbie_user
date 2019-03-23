package com.DDebbieinc.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.fragment.EditProfileFragment;
import com.DDebbieinc.fragment.SettingFragment;
import com.DDebbieinc.util.AndroidMultiPartEntity;
import com.DDebbieinc.util.Constants;
import com.DDebbieinc.util.IOUtils;
import com.DDebbieinc.util.ImageInputHelper;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity implements ImageInputHelper.ImageActionListener, SettingFragment.OnFragmentInteractionListener, EditProfileFragment.OnFragmentInteractionListener {


    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private String selectedImagePath = "";
    final private int PICK_IMAGE = 1;
    final private int CAPTURE_IMAGE = 2;
    private String imgPath;

    private ProgressDialog progressDialog;
    File uploadFile1 ;
    long totalSize = 0;
    String photoName = "";
    Bitmap bitmap;
    Context context = this;

    private ImageInputHelper imageInputHelper;
    private UserDetailsPojo userDetailsPojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.back_icon);
        imageInputHelper = new ImageInputHelper(this);
        imageInputHelper.setImageActionListener(this);
        IOUtils ioUtils = new IOUtils(context);
        userDetailsPojo = ioUtils.getUser();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        SettingFragment settingFragment = new SettingFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, settingFragment, settingFragment.getClass().getName());
        fragmentTransaction.addToBackStack(settingFragment.getClass().getName());
        fragmentTransaction.commit();
    }
    @Override
    public void onBackPressed() {

        if(fragmentManager.getBackStackEntryCount() == 1)
        {
            this.finish();
            overridePendingTransition(0, 0);
        }else {
            super.onBackPressed();
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageInputHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onImageSelectedFromGallery(Uri uri, File imageFile) {
        // cropping the selected image. crop intent will have aspect ratio 16/9 and result image
        // will have size 800x450
        imageInputHelper.requestCropImage(uri, 200, 200, 10, 10);
    }

    @Override
    public void onImageTakenFromCamera(Uri uri, File imageFile) {
        // cropping the taken photo. crop intent will have aspect ratio 16/9 and result image
        // will have size 800x450
        imageInputHelper.requestCropImage(uri, 200, 200, 10, 10);
    }

    @Override
    public void onImageCropped(Uri uri, File imageFile) {
        try {
            // getting bitmap from uri
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            // showing bitmap in image view
            SettingFragment.imgProfilePic.setImageBitmap(bitmap);

            uploadFile1 = imageFile;

            if (IOUtils.isNetworkAvailable(context)) {
                try {
                    new UploadFileToServer().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                IOUtils.setAlertForActivity(context);
            }
            //new UploadFileToServer().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onFragmentInteraction(String path) {

        if(path.equals("Take Photo")) {
            imageInputHelper.takePhotoWithCamera();
        }
        else if(path.equals("Choose from Gallery")){
            imageInputHelper.selectImageFromGallery();
        }
    }



    @Override
    public void onFragmentInteraction(Uri uri) {

    }




    /**
     * Uploading the file to server
     * */
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setCancelable(false);
            progressDialog.setTitle("Uploading Photo...");
            progressDialog.show();
        }



        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constants.URI_UPLOAD_PROF_IMG);

            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {

                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                // Adding file data to http body
                entity.addPart("profilePhoto", new FileBody( uploadFile1));

                // Extra parameters if you want to pass to server
                entity.addPart("Content-Type",new StringBody("multipart/form-data"));
                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }

            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;

        }

        @Override
        protected void onPostExecute(String res) {
            super.onPostExecute(res);
            Log.e("***********", "Response from server: " + res);

            // showing the server response in an alert dialog
            try {
                JSONObject jsonObject = new JSONObject(res);
                if(jsonObject.getString("result").equals("true")){
                    // showAlert("Photo uploaded successfully");
                    photoName = jsonObject.getString("photo");
                    Utils util = new Utils(context);
                    util.setDP(Constants.IMAGE_BASE_URL + photoName);
               /*     Picasso.with(context).load(util.getDP()).transform(new CircleTransform())
                            .placeholder(R.mipmap.user_default)
                            .error(R.mipmap.user_default)
                            .into(DashboardActivity.imgProfilePic);*/

                    setDP(util.getDP());
                    if(IOUtils.isNetworkAvailable(context)) {
                        createJsonobjectForApiCall();
                    }else {
                        IOUtils.setAlertForActivity(context);
                    }

                }else {
                    Toast.makeText(context, "Opps something went wrong!", Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                        (url), DashboardActivity.imgProfilePic,
                        options, new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(String s, View view, FailReason failReason) {
                                DashboardActivity.imgProfilePic.setImageResource(R.mipmap.user_default);
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



           /*
    * createJsonobjectForApiCall - In this method we create JsonObject for api call...
    */

        public void createJsonobjectForApiCall(){
            try{
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.ID,userDetailsPojo.getId());
                jsonObject.put(Constants.EMAIL,userDetailsPojo.getEmail());
                jsonObject.put(Constants.PHOTO, photoName);
                Log.v("JsonObject", jsonObject.toString());
                regitrationApiCall(jsonObject);
            }catch (Exception e){
                progressDialog.dismiss();            }
        }

    /*
    * regitrationApiCall - In this method we call the api...
    */

        public void regitrationApiCall(final JSONObject js) {


            RequestQueue queue = Volley.newRequestQueue(context);


            JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                    Constants.URL_PROFILE_UPDATE, js,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("Response", response.toString());
                            //{"result":true,"response":"Profile updated","customerData":[]}
                            try {
                                if (response.getBoolean("result")) {
                                    IOUtils.toastMessage(context, response.getString("response"));
                                }else{
                                    IOUtils.alertMessegeDialog(context,response.getString("response"),"OK");
                                }
                            }catch (JSONException e){
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

    }

}
