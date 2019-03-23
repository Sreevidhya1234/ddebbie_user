package com.DDebbieinc.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.DDebbieinc.R;
import com.DDebbieinc.util.FourDigitCardFormatWatcher;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditCardFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditCardFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditCardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditCardFragment newInstance(String param1, String param2) {
        EditCardFragment fragment = new EditCardFragment();
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

    private Button mBtnSubmit;
    private EditText mEdtCardNo,mEdtNewCardNo;
    private LinearLayout linearCredit,linearVisa,linearMaster,linearAmerican;
    private ImageView radioCredit,radioVisa,radioMaster,radioAmerican;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_card, container, false);


        mBtnSubmit = (Button) view.findViewById(R.id.btnSubmit);

        mEdtCardNo = (EditText) view.findViewById(R.id.edtCardNo);
        mEdtNewCardNo = (EditText) view.findViewById(R.id.edtNewCardNo);

        mEdtNewCardNo.addTextChangedListener(new FourDigitCardFormatWatcher());
        mEdtCardNo.addTextChangedListener(new FourDigitCardFormatWatcher());

        linearCredit = (LinearLayout) view.findViewById(R.id.linearCredit);
        linearVisa = (LinearLayout) view.findViewById(R.id.linearVisa);
        linearMaster = (LinearLayout) view.findViewById(R.id.linearMaster);
        linearAmerican = (LinearLayout) view.findViewById(R.id.linearAmerican);


        radioCredit = (ImageView) view.findViewById(R.id.radioCredit);
        radioVisa = (ImageView) view.findViewById(R.id.radioVisa);
        radioMaster = (ImageView) view.findViewById(R.id.radioMaster);
        radioAmerican = (ImageView) view.findViewById(R.id.radioAmerican);


        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Submit", Toast.LENGTH_SHORT).show();
                getFragmentManager().popBackStackImmediate();

            }
        });


        linearCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCredit.setImageDrawable(getResources().getDrawable(R.mipmap.radio_on));
                radioVisa.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioMaster.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioAmerican.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
            }
        });


        linearVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCredit.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioVisa.setImageDrawable(getResources().getDrawable(R.mipmap.radio_on));
                radioMaster.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioAmerican.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
            }
        });

        linearMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCredit.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioVisa.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioMaster.setImageDrawable(getResources().getDrawable(R.mipmap.radio_on));
                radioAmerican.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
            }
        });

        linearAmerican.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                radioCredit.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioVisa.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioMaster.setImageDrawable(getResources().getDrawable(R.mipmap.radio));
                radioAmerican.setImageDrawable(getResources().getDrawable(R.mipmap.radio_on));
            }
        });




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
}
