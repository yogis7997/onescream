package com.onescream.intros;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.onescream.R;
import com.onescream.Utils.Utility;

/**
 * Fragment class for Page1 of First Screen
 * <p/>
 * Created by Anwar Almojarkesh
 */
public final class Page1Fragment extends Fragment implements OnClickListener {
    Typeface facethin, facebold, faceRegular, EstiloRegular, faceMedium, sanfacesemibold;
    private Utility utility;

    public interface FragmentListener {
        public void clickListner(String Value);
    }

    FragmentListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        utility = new Utility(getActivity());
        utility.RegisterScreen(getActivity(), getActivity().getResources().getString(R.string.landing));
        final View v = inflater.inflate(R.layout.fragment_page1, container,
                false);

        initTypeFace();
        updateLCD(v);

        return v;
    }

    private void updateLCD(View v) {

        try {
            // Instantiate the Listener so we can send events to the host
            mListener = (FragmentListener) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement cadlrDialogListener");
        }

        TextView SignIn_tv = (TextView) v.findViewById(R.id.tv_btn_login);
        SignIn_tv.setTypeface(sanfacesemibold);
        TextView Register_tv = (TextView) v.findViewById(R.id.tv_btn_signup);
        Register_tv.setTypeface(sanfacesemibold);
        TextView text = (TextView) v.findViewById(R.id.text);
        text.setTypeface(faceMedium);
//		 labelTitle.setTypeface(EstiloRegular);

        View frmBtnLogin = v.findViewById(R.id.frm_btn_login);
        frmBtnLogin.setOnClickListener(this);

        View frmBtnSignup = v.findViewById(R.id.frm_btn_signup);
        frmBtnSignup.setOnClickListener(this);

        if (((FirstScreenActivity) getActivity()).m_bInTour) {
            frmBtnLogin.setVisibility(View.GONE);
            frmBtnSignup.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frm_btn_signup:
                mListener.clickListner("1");
//				((FirstScreenActivity)getActivity()).goToSignupActivity(false);
                break;
            case R.id.frm_btn_login:
                ((FirstScreenActivity) getActivity()).goToLoginActivity();
                break;
            default:
                break;
        }
    }

    private void initTypeFace() {
        facethin = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Thin.ttf");
        faceMedium = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");
        facebold = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");

        faceRegular = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");
        EstiloRegular = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/EstiloRegular.otf");
        sanfacesemibold = Typeface.createFromAsset(getActivity()
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Semibold.otf");

    }
}
