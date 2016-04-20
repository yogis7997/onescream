package com.onescream.login;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.onescream.HomeActivity;
import com.onescream.R;
import com.onescream._SplashActivity;
import com.onescream.intros.FirstScreenActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJCONST;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for Login Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class LoginActivity extends Activity implements View.OnClickListener {

	private final String TAG = "LoginActivity";

	private Context mContext;

	private EditText m_etEmail,m_etPassword;
	Typeface facethin,facebold,faceRegular,EstiloRegular;
	Typeface sanfacebold,sanfaceRegular, sanfacesemibold
			,sanfaceMedium, proximasemi;


	//	CallbackManager callbackManager = CallbackManager.Factory.create();
	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ActivityTask.INSTANCE.add(this);
		
		mContext = (Context) this;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));
		}
		
//		FacebookSdk.sdkInitialize(this.getApplicationContext());

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
		initTypeFace();
		Typeface  EstiloRegular = Typeface.createFromAsset( getAssets(),
	            "fonts/EstiloRegular.otf");

		Typeface  RobotoBold = Typeface.createFromAsset( getAssets(),
				"fonts/Roboto-Bold.ttf");
//
		TextView tv_btn_submit= (TextView)findViewById(R.id.tv_btn_login);
		TextView Text1= (TextView)findViewById(R.id.text1);
		TextView introtxt= (TextView)findViewById(R.id.intro);
		TextView Text2= (TextView)findViewById(R.id.logintxt);
		Text1.setTypeface(sanfacesemibold);
		introtxt.setTypeface(sanfaceMedium);
		tv_btn_submit.setTypeface(sanfacebold);
		Text2.setTypeface(sanfacesemibold);

//		screen_title.setTypeface(EstiloRegular);
		findViewById(R.id.frm_board).setOnClickListener(this);

		// /> Information
		m_etEmail = (EditText) findViewById(R.id.et_email);
		m_etPassword = (EditText) findViewById(R.id.et_password);

		m_etEmail.setHintTextColor(getResources().getColor(R.color.et_default_color));
		m_etPassword.setHintTextColor(getResources().getColor(R.color.et_default_color));
		m_etPassword.setTypeface(sanfaceRegular);
		m_etEmail.setTypeface(sanfaceRegular);

		// /> Signup
		TextView btn_signup= (TextView)findViewById(R.id.tv_btn_signup);
		btn_signup.setTypeface(sanfaceMedium);
		btn_signup.setOnClickListener(this);
// /> Reset password
		TextView btn_resetpasword= (TextView)findViewById(R.id.tv_btn_resetpassword);
		btn_resetpasword.setTypeface(sanfaceMedium);
		btn_resetpasword.setOnClickListener(this);
		// /> Login
		findViewById(R.id.frm_btn_login).setOnClickListener(this);
		
		///> Login With Facebook
//		findViewById(R.id.frm_btn_fb_login).setOnClickListener(this);
		findViewById(R.id.iv_back).setOnClickListener(this);
	}

	// /////////////////////////////////////
	public void goBack() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	private String m_strEmail;
	private String m_strPassword;

	private void login() {

		PRJFUNC.showProgress(mContext, "Log in...");

		ParseUser.logInInBackground(m_strEmail, m_strPassword,
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
														LoginActivity.this,
														e.getMessage());
												return;
											}
											goToHomeActivity();
										}
									});

							GlobalValues.sharedInstance().m_stUserId = parseUser
									.getObjectId();
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
								PRJFUNC.showAlertDialog(LoginActivity.this,
										e.getMessage());
							} else {
								PRJFUNC.showAlertDialog(LoginActivity.this,
										"The email address/password does not match an existing account");
							}
						}
					}
				});
	}
	private void initTypeFace()
	{
		facethin = Typeface.createFromAsset(this.getAssets(),
				"fonts/Roboto-Thin.ttf");
		facebold = Typeface.createFromAsset(this.getAssets(),
				"fonts/Roboto-Bold.ttf");
		faceRegular = Typeface.createFromAsset(this.getAssets(),
				"fonts/Roboto-Regular.ttf");
		EstiloRegular = Typeface.createFromAsset(this.getAssets(),
				"fonts/EstiloRegular.otf");
		sanfacebold = Typeface.createFromAsset(this.getAssets(),
				"fonts/SanFranciscoDisplay-Bold.otf");
		sanfaceRegular = Typeface.createFromAsset(this.getAssets(),
				"fonts/SanFranciscoDisplay-Regular.otf");
		sanfaceMedium = Typeface.createFromAsset(this.getAssets(),
				"fonts/SanFranciscoDisplay-Medium.otf");
		sanfacesemibold = Typeface.createFromAsset(this.getAssets(),
				"fonts/SanFranciscoDisplay-Semibold.otf");
		proximasemi= Typeface.createFromAsset(this.getAssets(),
				"fonts/Proxima Nova Semibold.otf");

	}
	public void goToSignupActivity() {
		Intent intent = new Intent(LoginActivity.this, FirstScreenActivity.class);
		intent.putExtra("activity",true);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
		finish();
	}
	public void goToResetPasswordActivity() {
		Intent intent = new Intent(LoginActivity.this, ResetPassword.class);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
//		finish();
	}
	public void goToHomeActivity() {
		Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
		finish();
	}

	/**
	 * OnClick Event method
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frm_board:
			PRJFUNC.hideKeyboard(LoginActivity.this);
			break;
		case R.id.tv_btn_signup:
			goToSignupActivity();
			break;
		case R.id.frm_btn_login:
			m_strEmail = m_etEmail.getText().toString();

			if (!PRJFUNC.isNullOrEmpty(PRJCONST.DEFAULT_PASSWORD)) {
				m_strPassword = PRJCONST.DEFAULT_PASSWORD;
			}
			m_strPassword=m_etPassword.getText().toString();
			if (m_strEmail.isEmpty() ) {
				PRJFUNC.showAlertDialog(LoginActivity.this,
						"Invalid Email");
				return;
			}
			if ( m_strPassword.isEmpty()) {
			PRJFUNC.showAlertDialog(LoginActivity.this,
					"Please Enter Password");
			return;
		}

			login();
			break;
			case R.id.tv_btn_resetpassword:
				goToResetPasswordActivity();
				break;
//		case R.id.frm_btn_fb_login:
////			onFbLogin();
//			break;
			
		case R.id.iv_back:
			Intent intent = new Intent(this, FirstScreenActivity.class);
	        intent.putExtra(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN, false);
	        startActivity(intent);
	        finish();
			overridePendingTransition(R.anim.hold, R.anim.right_out);
			break;
		default:
			break;
		}
	}
	
	
//	public void onFbLogin() {
//
//		LoginManager.getInstance().registerCallback(callbackManager,
//				new FacebookCallback<LoginResult>() {
//					@Override
//					public void onSuccess(LoginResult loginResult) {
//						PRJFUNC.showProgress(mContext, "Loading facebook information...");
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
//											m_strEmail = "";
//											try {
//												m_strEmail = object
//														.getString("email");
//
//												if (!PRJFUNC.isNullOrEmpty(PRJCONST.DEFAULT_PASSWORD)) {
//													m_strPassword = PRJCONST.DEFAULT_PASSWORD;
//												}
//											} catch (JSONException e) {
//											}
//
//											login();
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
//
//					}
//
//					@Override
//					public void onError(FacebookException exception) {
//						PRJFUNC.showAlertDialog(LoginActivity.this,
//								exception.getMessage());
//					}
//
//				});
//
//		LoginManager.getInstance().logInWithReadPermissions(this,
//				Arrays.asList("public_profile", "email"));
//
//	}
}
