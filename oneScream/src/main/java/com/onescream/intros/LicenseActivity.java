package com.onescream.intros;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.onescream.R;
import com.onescream.login.LoginActivity;
import com.onescream.settings.PrivacyPolicyActivity;
import com.onescream.settings.TermsActivity;
import com.uc.prjcmn.ActivityTask;

/**
 * Activity class for showing and agree "Terms and conditions" and
 * "Privacy Policy"
 *
 * Created by Anwar Almojarkesh
 */
public class LicenseActivity extends Activity implements View.OnClickListener {

	private final String TAG = "LicenseActivity";

	private Context mContext;

	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_license);
		ActivityTask.INSTANCE.add(this);

		mContext = (Context) this;

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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			goBack();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	// //////////////////////////////////////////////////
	private void updateLCD() {

		// /> Terms and Conditions
		findViewById(R.id.tv_btn_toc).setOnClickListener(this);

		// /> Privacy Policy
		findViewById(R.id.tv_btn_privacy).setOnClickListener(this);

		// /> Agree
		findViewById(R.id.frm_btn_agree).setOnClickListener(this);

	}

	// /////////////////////////////////////
	public void goBack() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	public void goToTermsActivity() {
		Intent intent = new Intent(LicenseActivity.this, TermsActivity.class);
		intent.putExtra(TermsActivity.PARAM_IN_SIGNING, true);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.hold);
	}

	public void goToPrivacyActivity() {
		Intent intent = new Intent(LicenseActivity.this,
				PrivacyPolicyActivity.class);
		intent.putExtra(PrivacyPolicyActivity.PARAM_IN_SIGNING, true);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.hold);
	}

	public void goToThankyouActivity() {
		Intent intent = new Intent(LicenseActivity.this, ThankyouActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.hold);
		finish();
	}

	private void signup() {

	}

	/**
	 * OnClick Event method
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frm_btn_agree:
			goToThankyouActivity();
			break;
		case R.id.tv_btn_toc:
			goToTermsActivity();
			break;
		case R.id.tv_btn_privacy:
			goToPrivacyActivity();
			break;
		default:
			break;
		}
	}
}
