package com.onescream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.parse.ParseUser;

import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for Ask to Join Popup Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class AskToJoinActivity extends Activity implements View.OnClickListener {

	private final String TAG = "AskToJoinActivity";

	private Context mContext;

	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_asktojoin);
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
			// goBack();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	// //////////////////////////////////////////////////
	private void updateLCD() {
		// Yes, please!
		findViewById(R.id.frm_btn_yes).setOnClickListener(this);

		// Not now
		findViewById(R.id.frm_btn_no).setOnClickListener(this);
	}


	// /////////////////////////////////////
	private void goBack() {
		finish();
	}

	private void onOk() {
		setResult(RESULT_OK);
		finish();
	}

	private void onCancel() {
		ParseUser user = ParseUser.getCurrentUser();
		String strKey = String.format("%s_ask", PRJFUNC.convertEmailToChannelStr(user.getEmail()));

		SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
		phoneDb.setBoolean(strKey, true);

		setResult(RESULT_CANCELED);
		finish();
	}



	/**
	 * OnClick Event method
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.frm_btn_yes:
				onOk();
				break;

			case R.id.frm_btn_no:
				onCancel();
				break;

			default:
				break;
		}
	}
}
