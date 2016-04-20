package com.onescream.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.onescream.R;
import com.uc.prjcmn.ActivityTask;

/**
 * Activity class for Founders Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class FoundersActivity extends Activity implements View.OnClickListener {

	private final String TAG = "FoundersActivity";

	private Context mContext;

	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_founders);
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
		findViewById(R.id.iv_back).setOnClickListener(this);
	}


	// /////////////////////////////////////
	public void goBack() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}


	/**
	 * OnClick Event method
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_back:
				goBack();
				break;

			default:
				break;
		}
	}
}
