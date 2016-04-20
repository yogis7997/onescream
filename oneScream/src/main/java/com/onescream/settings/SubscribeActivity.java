package com.onescream.settings;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.onescream.R;
import com.onescream.library.EMGTextFieldCVCWatcher;
import com.onescream.library.EMGTextFieldCardWatcher;
import com.onescream.library.EMGTextFieldExpiryWatcher;
import com.onescream.login.LoginActivity;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.PRJFUNC;

/**
 * Activity class for Upgrade Member Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class SubscribeActivity extends Activity implements View.OnClickListener {

	private final String TAG = "UpgradeMemberActivity";

	private final int USERTYPE_NONE = 0;
	private final int USERTYPE_MONTH = 1;
	private final int USERTYPE_YEAR = 2;

	private Context mContext;

	private TextView m_tvPlanMonth;
	private TextView m_tvPlanYear;

	private EditText m_etCardNumber;
	private EditText m_etCardExpiryMonth;
	private EditText m_etCardExpiryYear;
	private EditText m_etCardCVC;
	private EditText m_etEmail;

	private int m_nUserType = USERTYPE_NONE;

	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subscribe);
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

		findViewById(R.id.frm_board).setOnClickListener(this);

		m_tvPlanMonth = (TextView) findViewById(R.id.tv_plan_month);
		m_tvPlanMonth.setOnClickListener(this);
		m_tvPlanYear = (TextView) findViewById(R.id.tv_plan_year);
		m_tvPlanYear.setOnClickListener(this);

		m_etCardNumber = (EditText) findViewById(R.id.et_card_number);
		m_etCardNumber.addTextChangedListener(new EMGTextFieldCardWatcher(
				m_etCardNumber));

		m_etCardExpiryMonth = (EditText) findViewById(R.id.et_card_expiry_month);
		m_etCardExpiryYear = (EditText) findViewById(R.id.et_card_expiry_year);

		m_etCardCVC = (EditText) findViewById(R.id.et_card_cvc);
		m_etCardCVC.addTextChangedListener(new EMGTextFieldCVCWatcher(
				m_etCardCVC));

		m_etEmail = (EditText) findViewById(R.id.et_email);

		findViewById(R.id.frm_purchase).setOnClickListener(this);

	}

	// /////////////////////////////////////
	public void goBack() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	public void selectUserType(int p_nType) {
		m_nUserType = p_nType;

		if (m_nUserType == USERTYPE_MONTH) {
			m_tvPlanMonth.setSelected(true);
			m_tvPlanYear.setSelected(false);
		} else if (m_nUserType == USERTYPE_YEAR) {
			m_tvPlanMonth.setSelected(false);
			m_tvPlanYear.setSelected(true);
		}

	}

	private Date calcExpiryDate(Date p_date, int p_nUserType) {
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTime(p_date);

		if (p_nUserType == USERTYPE_MONTH) {
			calendar.add(Calendar.MONTH, 1);
		} else if (p_nUserType == USERTYPE_YEAR) {
			calendar.add(Calendar.YEAR, 1);
		}

		return calendar.getTime();
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

		case R.id.frm_board:
			PRJFUNC.hideKeyboard(SubscribeActivity.this);
			break;

		case R.id.tv_plan_month:
			selectUserType(USERTYPE_MONTH);
			break;

		case R.id.tv_plan_year:
			selectUserType(USERTYPE_YEAR);
			break;

		case R.id.frm_purchase:
			purchase();
			break;
		default:
			break;
		}
	}

	private void purchase() {

		if (m_nUserType == USERTYPE_NONE) {
			PRJFUNC.showAlertDialog(SubscribeActivity.this,
					"Please select the plan!");
			return;
		}

		String cardNumber = m_etCardNumber.getText().toString()
				.replaceAll("\\D", "");
		if (cardNumber.length() < 16) {
			PRJFUNC.showAlertDialog(SubscribeActivity.this,
					"Please complete the card number.");
			m_etCardNumber.requestFocus();
			return;
		}

		int nExpiryMonth = 0;
		
		try {
			nExpiryMonth = Integer.parseInt(m_etCardExpiryMonth.getText().toString());
		} catch (Exception e) {}
		
		if (nExpiryMonth == 0 || nExpiryMonth > 12) {
			PRJFUNC.showAlertDialog(SubscribeActivity.this,
					"Please input valid expiry month.");
			m_etCardExpiryMonth.requestFocus();
			return;
		}
		
		int nExpiryYear = 0;
		try {
			nExpiryYear = Integer.parseInt(m_etCardExpiryYear.getText().toString());
		} catch (Exception e) {}
		
		if (nExpiryYear < 2015 || nExpiryYear > 2050) {
			PRJFUNC.showAlertDialog(SubscribeActivity.this,
					"Please input valid expiry year.");
			m_etCardExpiryYear.requestFocus();
			return;
		}

		String strCVC = m_etCardCVC.getText().toString();
		if (strCVC.length() < 3) {
			PRJFUNC.showAlertDialog(SubscribeActivity.this,
					"Please input valid CVC.");
			m_etCardCVC.requestFocus();
			return;
		}

		ParseUser user = ParseUser.getCurrentUser();
		Date date = user.getDate("expiry_date");
		if (date == null)
			date = new Date();

		Date upgraded_date = calcExpiryDate(date, m_nUserType);
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
		String strMessage = String.format("One Scream is upgraded by %s.",
				format.format(upgraded_date));

		user.put("expiry_date", upgraded_date);
		user.saveInBackground();

		PRJFUNC.showAlertDialog(SubscribeActivity.this, strMessage, "OK", null,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						goBack();
					}
				}, null);
	}

}
