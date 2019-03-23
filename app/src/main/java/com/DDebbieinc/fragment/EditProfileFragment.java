package com.DDebbieinc.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.DDebbieinc.R;
import com.DDebbieinc.activity.DashboardActivity;
import com.DDebbieinc.entity.UserDetailsPojo;
import com.DDebbieinc.util.Constants;
import com.DDebbieinc.util.IOUtils;
import com.DDebbieinc.util.JsonObjectRequestWithHeader;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private Button mBtnUpdateProfile;
    private EditText mEdtName, mEdtPassword, mEdtConfirmPassword;
    private ImageView mImgCheckbox;
    private TextView mEdtEmail;
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private ProgressDialog dialog;
    private UserDetailsPojo userDetailsPojo;
    String token = "";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mEdtName = (EditText) view.findViewById(R.id.edtName);
        mEdtEmail = (TextView) view.findViewById(R.id.edtEmail);
        mEdtPassword = (EditText) view.findViewById(R.id.edtPassword);
        mEdtConfirmPassword = (EditText) view.findViewById(R.id.edtConfirmPassword);
        mBtnUpdateProfile = (Button) view.findViewById(R.id.btnUpdateProfile);

        mBtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        IOUtils ioUtils = new IOUtils(getContext());
        userDetailsPojo = ioUtils.getUser();

        mEdtName.setText(userDetailsPojo.getCustomerName());
        mEdtEmail.setText(userDetailsPojo.getEmail());

        return view;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void validate() {


        if (mEdtName.getText().toString().equals("")) {
            IOUtils.alertMessegeDialog(getContext(), "Please enter your Name.", "OK");
        }
        else if (!mEdtPassword.getText().toString().trim().equals("")) {
            if (mEdtConfirmPassword.getText().toString().equals("")) {
                IOUtils.alertMessegeDialog(getContext(), "Please enter Confirm Password.", "OK");
            } else if (!mEdtConfirmPassword.getText().toString().equals(mEdtPassword.getText().toString())) {
                IOUtils.alertMessegeDialog(getContext(), "Please enter Password & Confirm Password does not matches.", "OK");
            } else {

                if (IOUtils.isNetworkAvailable(getContext())) {
                    try {
                        createJsonobjectForApiCall();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    IOUtils.setAlertForActivity(getContext());
                }

            }
        }else {
            if (IOUtils.isNetworkAvailable(getContext())) {
                try {
                    createJsonobjectForApiCall();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                IOUtils.setAlertForActivity(getContext());
            }

        }


    }

    /*
    * createJsonobjectForApiCall - In this method we create JsonObject for api call...
    */

    public void createJsonobjectForApiCall() {
        try {
            dialog = IOUtils.getProgessDialog(getContext());
            dialog.show();

            JSONObject jsonObject = new JSONObject();

            Log.e("id", userDetailsPojo.getId());
            jsonObject.put(Constants.ID, userDetailsPojo.getId());
            jsonObject.put(Constants.EMAIL, userDetailsPojo.getEmail());
            jsonObject.put(Constants.NEW_EMAIL,  userDetailsPojo.getEmail());
            jsonObject.put(Constants.CUSTOMER_NAME, mEdtName.getText().toString());
            jsonObject.put(Constants.PASSWORD, mEdtPassword.getText().toString());
            jsonObject.put(Constants.DEVICE_TYPE, "1");
            jsonObject.put(Constants.CONTACT_NUMBER, userDetailsPojo.getContactNumber());
            jsonObject.put(Constants.EMERGENCY_CONTACT_NUMBER, userDetailsPojo.getEmgContactNumber());

            Log.v("JsonObject", jsonObject.toString());
            regitrationApiCall(jsonObject);
        } catch (Exception e) {
            dialog.dismiss();
        }
    }

    /*
    * regitrationApiCall - In this method we call the api...
    */

    public void regitrationApiCall(final JSONObject js) {


        RequestQueue queue = Volley.newRequestQueue(getContext());


        JsonObjectRequestWithHeader jsonObjReq = new JsonObjectRequestWithHeader(Request.Method.POST,
                Constants.URL_PROFILE_UPDATE, js,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Response", response.toString());
                        //{"result":true,"response":"Profile updated","customerData":[]}
                        try {
                            if (response.getBoolean("result")) {
                                IOUtils.toastMessage(getContext(), response.getString("response"));
                                IOUtils.hideSoftKeyboard(getActivity());
                                JSONObject jsonObject = response.getJSONObject("customerData");

                                userDetailsPojo.setCustomerName(jsonObject.getString("customerName"));
                                userDetailsPojo.setId(userDetailsPojo.getId());
                                userDetailsPojo.setEmail(userDetailsPojo.getEmail());
                                userDetailsPojo.setContactNumber(userDetailsPojo.getContactNumber());
                                userDetailsPojo.setEmgContactNumber(userDetailsPojo.getEmgContactNumber());
                                userDetailsPojo.setPhoto(userDetailsPojo.getPhoto());
                                userDetailsPojo.setStatus(userDetailsPojo.getStatus());
                                IOUtils ioUtils = new IOUtils(getActivity());
                                ioUtils.setUser(userDetailsPojo);
                                getFragmentManager().popBackStackImmediate();
                                SettingFragment.mTxtEmail.setText(jsonObject.getString("email"));
                                SettingFragment.mTxtName.setText(jsonObject.getString("customerName"));
                                DashboardActivity.mTxtName.setText(jsonObject.getString("customerName"));
                                SettingFragment.mTxtMobile.setText(jsonObject.getString("contactNumber"));

                            } else {
                                IOUtils.alertMessegeDialog(getContext(), response.getString("response"), "OK");
                            }
                        } catch (JSONException e) {
                            dialog.dismiss();
                        }


                        dialog.dismiss();

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
