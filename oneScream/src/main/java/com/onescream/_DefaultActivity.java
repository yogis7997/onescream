package com.onescream;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.uc.prjcmn.ActivityTask;

/**
 *  Activity class to template general screens
 * Created by Anwar Almojarkesh
 *
 */

public class _DefaultActivity extends Activity implements View.OnClickListener {

	private final String TAG = "_DefaultActivity";

	private Context mContext;

	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_default);
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
			goMainActivity();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	// //////////////////////////////////////////////////
	private void updateLCD() {

	}


	// /////////////////////////////////////
	private void goMainActivity() {
		finish();
		overridePendingTransition(R.anim.from_bottom, R.anim.hold);
	}


	/**
	 * OnClick Event method
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			default:
				break;
		}
	}
}
