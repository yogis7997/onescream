package com.onescream.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.onescream.HomeActivity;
import com.onescream.R;
import com.onescream.Utils.Utility;
import com.uc.prjcmn.ActivityTask;

/**
 * Activity class for Contact us Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class ContactUsActivity extends Activity implements View.OnClickListener {

	private final String TAG = "ContactUsActivity";

	private Context mContext;
	Typeface facethin,facebold,faceRegular,EstiloRegular;
	Typeface sanfacebold,sanfaceRegular, sanfacesemibold
			,sanfaceMedium, proximasemi;
	private Utility utility;

	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		utility = new Utility(this);
		utility.RegisterScreen(this, getResources().getString(R.string.contact));
		setContentView(R.layout.activity_contact_us);
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
		initTypeFace();
		findViewById(R.id.iv_back).setOnClickListener(this);
		TextView Email_txt=(TextView) findViewById(R.id.email);
		TextView Title_txt=(TextView)findViewById(R.id.title);
		Title_txt.setTypeface(sanfacebold);
		TextView Text1=(TextView)findViewById(R.id.txt1);
		TextView Text2=(TextView)findViewById(R.id.txt2);
		Text1.setTypeface(sanfacesemibold);
		Text2.setTypeface(sanfaceRegular);
		Email_txt.setTypeface(sanfaceMedium);
		Email_txt.setOnClickListener(this);
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
		sanfacebold = Typeface.createFromAsset(this
						.getAssets(),
				"fonts/SanFranciscoDisplay-Bold.otf");
		sanfaceRegular = Typeface.createFromAsset(this
						.getAssets(),
				"fonts/SanFranciscoDisplay-Regular.otf");
		sanfaceMedium = Typeface.createFromAsset(this
						.getAssets(),
				"fonts/SF-UI-Text-Regular.otf");
		sanfacesemibold = Typeface.createFromAsset(this
						.getAssets(),
				"fonts/SF-UI-Text-Bold.otf");

	}
	// /////////////////////////////////////
	public void goBack() {
		Intent intent = new Intent(ContactUsActivity.this, HomeActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
		finish();
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
			case R.id.email:
				Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
						"mailto", "Help@onescrem.com", null));
				emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
				emailIntent.putExtra(Intent.EXTRA_TEXT, "");
				startActivity(Intent.createChooser(emailIntent, "Send email..."));
				break;
			default:
				break;
		}
	}
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(ContactUsActivity.this, HomeActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.left_in, R.anim.right_out);
		finish();
	}
}
