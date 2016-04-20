package com.onescream.intros;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.onescream.HomeActivity;
import com.onescream.R;
import com.onescream.settings.PrivacyPolicyActivity;
import com.onescream.settings.TermsActivity;
import com.uc.prjcmn.ActivityTask;

/**
 * Activity Class for last screen of first tour when the user downloaded app
 *
 * Created by Anwar Almojarkesh
 */
public class ThankyouActivity extends Activity implements View.OnClickListener {

	private final String TAG = "ThankyouActivity";

	private Context mContext;

	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_thankyou);
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

		// /> Free Trial
		findViewById(R.id.frm_btn_trial).setOnClickListener(this);

		// /> SubScribe
		findViewById(R.id.frm_btn_subscribe).setOnClickListener(this);

		//findViewById(R.id.iv_page6).setSelected(true);
	}

	// /////////////////////////////////////
	public void goBack() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	public void goToHomeActivity(boolean p_bNeedSubscribe) {
		Intent intent = new Intent(ThankyouActivity.this, HomeActivity.class);
		intent.putExtra(HomeActivity.PARAM_NEED_SUBSCRIBE, p_bNeedSubscribe);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.left_out);
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
		case R.id.frm_btn_trial:
			goToHomeActivity(false);
			break;
		case R.id.frm_btn_subscribe:
			//goToHomeActivity(true);
			break;
		default:
			break;
		}
	}
}
