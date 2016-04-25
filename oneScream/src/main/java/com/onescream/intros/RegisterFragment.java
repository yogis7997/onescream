package com.onescream.intros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.onescream.R;
import com.onescream.Utils.Utility;
import com.onescream.settings.PrivacyPolicyActivity;
import com.onescream.settings.TermsActivity;
import com.onescream.settings.WiFiItemsActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJCONST;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Fragment class for Page4 of First Screen
 * <p/>
 * Created by Anwar Almojarkesh
 */
public final class RegisterFragment extends Fragment implements View.OnClickListener {
    Typeface facethin, facebold, faceRegular, EstiloRegular, faceMedium;
    Typeface sanfacethin, sanfacebold, sanfaceRegular, sanEstiloRegular, sanfaceMedium;
    public static boolean m_bInTour;
    public static String PARAM_IN_TOUR = "in_tour";
    EditText m_etName, m_etPhone, m_etEmail, m_etpwd, m_etRepwd;

    private String m_strName;
    private String m_strPhone;
    private String m_strEmail;
    private String m_strFbId;
    private String m_strpaswrd;
    private String m_strrepaswrd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utility.RegisterScreen(getActivity(), getActivity().getResources().getString(R.string.ready_page));


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.activity_register, container,
                false);


//		FacebookSdk.sdkInitialize(this.getApplicationContext());

        m_bInTour = getActivity().getIntent().getBooleanExtra(PARAM_IN_TOUR, false);

        updateLCD(v);

        return v;
    }

    private void updateLCD(View v) {
        initTypeFace();
        TextView title = (TextView) v.findViewById(R.id.title);
        TextView Text1 = (TextView) v.findViewById(R.id.text1);
        TextView Text2 = (TextView) v.findViewById(R.id.text2);
        TextView Text3 = (TextView) v.findViewById(R.id.text3);
        TextView introtxt = (TextView) v.findViewById(R.id.intro);
        TextView tv_signup = (TextView) v.findViewById(R.id.tv_btn_signup);
        Text1.setTypeface(sanfaceRegular);
        Text2.setTypeface(sanfaceRegular);
        Text3.setTypeface(sanfaceRegular);
        title.setTypeface(sanfaceRegular);
        introtxt.setTypeface(sanfaceMedium);
        tv_signup.setTypeface(sanfacebold);


        LinearLayout Frm_board = (LinearLayout) v.findViewById(R.id.lnrlyt);
        Frm_board.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });
        initTypeFace();
        m_etName = (EditText) v.findViewById(R.id.et_name);
        m_etPhone = (EditText) v.findViewById(R.id.et_phone);
        m_etEmail = (EditText) v.findViewById(R.id.et_email);
        m_etpwd = (EditText) v.findViewById(R.id.et_password);
        m_etRepwd = (EditText) v.findViewById(R.id.et_repassword);
        m_etName.setHintTextColor(getResources().getColor(R.color.et_grey_color));
        m_etPhone.setHintTextColor(getResources().getColor(R.color.et_grey_color));
        m_etEmail.setHintTextColor(getResources().getColor(R.color.et_grey_color));
        m_etpwd.setHintTextColor(getResources().getColor(R.color.et_grey_color));
        m_etRepwd.setHintTextColor(getResources().getColor(R.color.et_grey_color));

        // /> Signup
        FrameLayout Signup_btn = (FrameLayout) v.findViewById(R.id.frm_btn_signup);
        Signup_btn.setOnClickListener(this);
//		findViewById(R.id.frm_btn_fb_login).setOnClickListener(this);

        TextView Privacy_btn = (TextView) v.findViewById(R.id.tv_privacy_policy);
        Text3.setTypeface(faceRegular);
        Privacy_btn.setOnClickListener(this);
        TextView Terms_btn = (TextView) v.findViewById(R.id.tv_terms_service);

        Terms_btn.setOnClickListener(this);

    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//                case R.id.frm_board:
//                    hideKeyboard();
//                    break;

            case R.id.frm_btn_signup:
                m_strName = m_etName.getText().toString().trim();
                m_strPhone = m_etPhone.getText().toString().trim();
                m_strEmail = m_etEmail.getText().toString().trim();
                m_strFbId = "";
                m_strpaswrd = m_etpwd.getText().toString().trim();
                m_strrepaswrd = m_etRepwd.getText().toString().trim();

                hideKeyboard();
                if (m_strName.isEmpty()) {
                    PRJFUNC.showAlertDialog(getActivity(),
                            "Please provide both your first and last name");
                    return;
                }
                if (m_strPhone.isEmpty()) {
                    PRJFUNC.showAlertDialog(getActivity(),
                            "Please input a mobile number");
                    return;
                }
                if (m_strPhone != null || !m_strPhone.equalsIgnoreCase("")) {
                    if (m_strPhone.length() > 11 || (m_strPhone.length() < 11)) {
                        PRJFUNC.showAlertDialog(getActivity(),
                                "Please verify your mobile number is correct");
                        return;
                    }
                }
                if (m_strEmail.isEmpty()) {
                    PRJFUNC.showAlertDialog(getActivity(),
                            "Please verify your email address is correct");
                    return;
                }
                if (m_strpaswrd.isEmpty()) {
                    PRJFUNC.showAlertDialog(getActivity(),
                            "Please choose a password");
                    return;
                }
                if (!m_strpaswrd.equalsIgnoreCase(m_strrepaswrd)) {
                    PRJFUNC.showAlertDialog(getActivity(),
                            "Your passwords do not match");
                    return;
                }
                String[] NameArray = m_strName.trim().split("\\s+");
                if (NameArray != null) {
                    if (NameArray.length < 2) {
                        PRJFUNC.showAlertDialog(getActivity(),
                                "Please provide both your first and last name");
                        return;
                    } else {

                        if (NameArray[1].length() < 2) {
                            PRJFUNC.showAlertDialog(getActivity(),
                                    "Please provide both your first and last name");
                            return;
                        } else {
                            signup();
                        }
                    }
                }


                break;
//		case R.id.frm_btn_fb_login:
////			onFbLogin();
//			break;

            case R.id.tv_privacy_policy:
                goToPrivacyActivity();
                break;
            case R.id.tv_terms_service:
                goToTermsActivity();
                break;
            case R.id.iv_back:
                SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(getActivity());
                goToIntroActivity(phoneDb.isLoggedInOnce());
                break;

            default:
                break;
        }
    }

    private void initTypeFace() {
        facethin = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Thin.ttf");
        facebold = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Bold.ttf");
        faceRegular = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Regular.ttf");
        EstiloRegular = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/EstiloRegular.otf");
        faceMedium = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Roboto-Medium.ttf");


        sanfacebold = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");
        sanfaceRegular = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/SanFranciscoDisplay-Regular.otf");
        sanfaceMedium = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/SanFranciscoDisplay-Semibold.otf");

    }

    private void signup() {

        ParseUser user = new ParseUser();
        user.setUsername(m_strEmail);
        user.setEmail(m_strEmail);
        user.setPassword(m_strpaswrd);
        PRJCONST.DEFAULT_PASSWORD = m_strpaswrd;
        user.put("os", "Android");
        user.put("user_type", PRJCONST.USER_TYPE_TRIAL);
        user.put("first_name", m_strName);
        user.put("last_name", "");
        user.put("phone", m_strPhone);
        user.put("facebook_id", m_strFbId);

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("onescreamshared", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("home", "");
        editor.putString("work", "");
        editor.putString("freq", "");
        editor.commit();

        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, PRJCONST.FREE_USE_DAYS);
        user.put("expiry_date", calendar.getTime());

        PRJFUNC.showProgress(getActivity(), "Sign up...");
        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                PRJFUNC.closeProgress();

                if (e != null) {
                    PRJFUNC.showAlertDialog(getActivity(), "The email address " + m_strEmail + " is already registered");
                    return;
                }

                login();
            }
        });
    }

    private void login() {
        PRJFUNC.showProgress(getActivity(), "Log in...");

        ParseUser.logInInBackground(m_strEmail, PRJCONST.DEFAULT_PASSWORD,
                new LogInCallback() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        PRJFUNC.closeProgress();
                        if (parseUser != null) {
                            // set the logged once flag
                            SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(
                                    getActivity());
                            phoneDb.setLoggedOnce();

                            ParseInstallation currentInstallation = ParseInstallation
                                    .getCurrentInstallation();
                            ArrayList<String> channels = new ArrayList<String>();
                            channels.add(PRJFUNC
                                    .convertEmailToChannelStr(m_strEmail));
                            currentInstallation.put("channels", channels);
                            currentInstallation.put("user", parseUser);
                            currentInstallation
                                    .saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e != null) {
                                                PRJFUNC.showAlertDialog(
                                                        getActivity(),
                                                        e.getMessage());
                                                return;
                                            }
                                            goToFrequentedAddressActivity();
                                        }
                                    });

                            GlobalValues.sharedInstance().m_stUserName = parseUser
                                    .getUsername();
                            phoneDb.saveUserInfo();

                            GlobalValues.sharedInstance().m_strEmail = parseUser
                                    .getEmail();
                            GlobalValues.sharedInstance().m_strFirstName = parseUser
                                    .getString("first_name");
                            GlobalValues.sharedInstance().m_strLastName = parseUser
                                    .getString("last_name");
                            GlobalValues.sharedInstance().m_strPostCode = parseUser
                                    .getString("postcode");
                            GlobalValues.sharedInstance().m_strPhoneNumber = parseUser
                                    .getString("phone");
                            GlobalValues.sharedInstance().m_strAddress = parseUser
                                    .getString("address");
                            phoneDb.saveUserExtraInfo();

                        } else {
                            if (e != null) {
                                PRJFUNC.showAlertDialog(getActivity(),
                                        e.getMessage());
                            } else {
                                PRJFUNC.showAlertDialog(getActivity(),
                                        "There is no such user");
                            }
                        }
                    }
                });
    }

    public void goToIntroActivity(boolean p_bLoggedOnce) {
        Intent intent = new Intent(getActivity(), FirstScreenActivity.class);
        intent.putExtra(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN, p_bLoggedOnce);
        getActivity().startActivity(intent);
        getActivity().finish();
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    public void goToTermsActivity() {
        Intent intent = new Intent(getActivity(), TermsActivity.class);
        intent.putExtra(TermsActivity.PARAM_IN_SIGNING, true);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    public void goToPrivacyActivity() {
        Intent intent = new Intent(getActivity(),
                PrivacyPolicyActivity.class);
        intent.putExtra(PrivacyPolicyActivity.PARAM_IN_SIGNING, true);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    public void goToThankyouActivity() {
        Intent intent = new Intent(getActivity(), ThankyouActivity.class);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.hold);
        getActivity().finish();
    }

    public void goToFrequentedAddressActivity() {
//        Intent intent = new Intent(getActivity(), WiFiItemsActivity.class);
        Intent intent = new Intent(getActivity(), AddressFragment.class);
        intent.putExtra(WiFiItemsActivity.PARAM_IS_FROM_SIGNUP, true);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
        getActivity().finish();
    }
}
