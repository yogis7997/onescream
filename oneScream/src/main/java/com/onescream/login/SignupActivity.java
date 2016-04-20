package com.onescream.login;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.Profile;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
import com.onescream.R;
import com.onescream._SplashActivity;
import com.onescream.intros.FirstScreenActivity;
import com.onescream.intros.ThankyouActivity;
import com.onescream.settings.PrivacyPolicyActivity;
import com.onescream.settings.TermsActivity;
import com.onescream.settings.WiFiItemsActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJCONST;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for Signup Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class SignupActivity extends Activity implements View.OnClickListener {

	private final String TAG = "SignupActivity";

	public static String PARAM_IN_TOUR = "in_tour";

	public static boolean m_bInTour;

	private Context mContext;

	private EditText m_etName;
	private EditText m_etPhone;
	private EditText m_etEmail;

	private View m_frmIndicator;

//	CallbackManager callbackManager = CallbackManager.Factory.create();
	 
	Typeface facethin,facebold,faceRegular,EstiloRegular;
	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		ActivityTask.INSTANCE.add(this);

		mContext = (Context) this;

//		FacebookSdk.sdkInitialize(this.getApplicationContext());

		m_bInTour = getIntent().getBooleanExtra(PARAM_IN_TOUR, false);

		updateLCD();
	}

	@Override
	protected void onDestroy() {
		releaseValues();

		ActivityTask.INSTANCE.remove(this);

		super.onDestroy();
	}

	private void releaseValues() {

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	// //////////////////////////////////////////////////
	private void updateLCD() {

		findViewById(R.id.frm_board).setOnClickListener(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
		initTypeFace();
      TextView title =(TextView) findViewById(R.id.title);
      title.setTypeface(facethin);
//      TextView labelTitle = (TextView) findViewById(R.id.labelll); 
//		 labelTitle.setTypeface(EstiloRegular);
		// /> Information
		m_etName = (EditText) findViewById(R.id.et_name);
		m_etPhone = (EditText) findViewById(R.id.et_phone);
		m_etEmail = (EditText) findViewById(R.id.et_email);

		// /> Signup
		findViewById(R.id.frm_btn_signup).setOnClickListener(this);
//		findViewById(R.id.frm_btn_fb_login).setOnClickListener(this);

		findViewById(R.id.tv_privacy_policy).setOnClickListener(this);

		findViewById(R.id.tv_terms_service).setOnClickListener(this);

		findViewById(R.id.iv_page4).setSelected(false);
		findViewById(R.id.iv_page5).setSelected(true);
	}

	// /////////////////////////////////////
	public void goBack() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	public void goToLoginActivity() {
		Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.hold);
		finish();
	}

	private String m_strName;
	private String m_strPhone;
	private String m_strEmail;
	private String m_strFbId;

	private void signup() {

		ParseUser user = new ParseUser();
		user.setUsername(m_strEmail);
		user.setEmail(m_strEmail);
		user.setPassword(PRJCONST.DEFAULT_PASSWORD);
		user.put("os", "Android");
		user.put("user_type", PRJCONST.USER_TYPE_TRIAL);
		user.put("first_name", m_strName);
		user.put("last_name", "");
		user.put("phone", m_strPhone);
		user.put("facebook_id", m_strFbId);

		Calendar calendar = GregorianCalendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, PRJCONST.FREE_USE_DAYS);
		user.put("expiry_date", calendar.getTime());

		PRJFUNC.showProgress(mContext, "Sign up...");
		user.signUpInBackground(new SignUpCallback() {
			@Override
			public void done(ParseException e) {
				PRJFUNC.closeProgress();

				if (e != null) {
					PRJFUNC.showAlertDialog(SignupActivity.this, e.getMessage());
					return;
				}

				login();
			}
		});
	}

	private void login() {
		PRJFUNC.showProgress(mContext, "Log in...");

		ParseUser.logInInBackground(m_strEmail, PRJCONST.DEFAULT_PASSWORD,
				new LogInCallback() {
					@Override
					public void done(ParseUser parseUser, ParseException e) {
						PRJFUNC.closeProgress();
						if (parseUser != null) {
							// set the logged once flag
							SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(
									mContext);
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
														SignupActivity.this,
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
								PRJFUNC.showAlertDialog(SignupActivity.this,
										e.getMessage());
							} else {
								PRJFUNC.showAlertDialog(SignupActivity.this,
										"There is no such user");
							}
						}
					}
				});
	}

	/**
	 * OnClick Event method
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frm_board:
			PRJFUNC.hideKeyboard(SignupActivity.this);
			break;

		case R.id.frm_btn_signup:
			m_strName = m_etName.getText().toString();
			m_strPhone = m_etPhone.getText().toString();
			m_strEmail = m_etEmail.getText().toString();
			m_strFbId = "";

			if (m_strName.isEmpty() || m_strPhone.isEmpty()
					|| m_strEmail.isEmpty()) {
				PRJFUNC.showAlertDialog(SignupActivity.this,
						"Please input all details.");
				return;
			}

			signup();
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
            SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
            goToIntroActivity(phoneDb.isLoggedInOnce());
			break;
			
		default:
			break;
		}
	}
	 public void goToIntroActivity(boolean p_bLoggedOnce) {
	        Intent intent = new Intent(this, FirstScreenActivity.class);
	        intent.putExtra(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN, p_bLoggedOnce);
	        startActivity(intent);
	        finish();
	        overridePendingTransition(R.anim.right_in, R.anim.hold);
	    }
	public void goToTermsActivity() {
		Intent intent = new Intent(SignupActivity.this, TermsActivity.class);
		intent.putExtra(TermsActivity.PARAM_IN_SIGNING, true);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.hold);
	}

	public void goToPrivacyActivity() {
		Intent intent = new Intent(SignupActivity.this,
				PrivacyPolicyActivity.class);
		intent.putExtra(PrivacyPolicyActivity.PARAM_IN_SIGNING, true);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.hold);
	}

	public void goToThankyouActivity() {
		Intent intent = new Intent(SignupActivity.this, ThankyouActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.hold);
		finish();
	}

	public void goToFrequentedAddressActivity() {
		Intent intent = new Intent(SignupActivity.this, WiFiItemsActivity.class);
		intent.putExtra(WiFiItemsActivity.PARAM_IS_FROM_SIGNUP, true);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
		finish();
	}

//	public void onFbLogin() {
//
//		LoginManager.getInstance().registerCallback(callbackManager,
//				new FacebookCallback<LoginResult>() {
//					@Override
//					public void onSuccess(LoginResult loginResult) {
//						PRJFUNC.showProgress(mContext,
//								"Loading facebook information...");
//						GraphRequest request = GraphRequest.newMeRequest(
//								loginResult.getAccessToken(),
//								new GraphRequest.GraphJSONObjectCallback() {
//									@Override
//									public void onCompleted(JSONObject object,
//											GraphResponse response) {
//
//										Profile profile = Profile
//												.getCurrentProfile();
//										if (profile != null) {
//
//											m_strName = profile.getName();
//											m_strFbId = profile.getId();
//											m_strPhone = "";
//											m_strEmail = "";
//											try {
//												m_strEmail = object
//														.getString("email");
//											} catch (JSONException e) {
//											}
//
//											signup();
//										}
//
//										Log.v("LoginActivity",
//												response.toString());
//									}
//								});
//						Bundle parameters = new Bundle();
//						parameters.putString("fields",
//								"id,name,email,gender, birthday");
//						request.setParameters(parameters);
//						request.executeAsync();
//
//					}
//
//					@Override
//					public void onCancel() {
//					}
//
//					@Override
//					public void onError(FacebookException exception) {
//						PRJFUNC.showAlertDialog(SignupActivity.this,
//								exception.getMessage());
//					}
//
//				});
//
//		LoginManager.getInstance().logInWithReadPermissions(this,
//				Arrays.asList("public_profile", "email"));
//
//	}
	private void initTypeFace()
	{
		  facethin = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Thin.ttf");
		  facebold = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Bold.ttf");
		  faceRegular = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Regular.ttf");
		  EstiloRegular = Typeface.createFromAsset(getAssets(),
		            "fonts/EstiloRegular.otf");
	}
}
