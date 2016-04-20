package com.onescream.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.onescream.R;
import com.onescream.info.WiFiItemInfo;
import com.onescream.library.SuggestAddressesMgr;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.NetFuncs;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for WIFI editing Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class WiFiItemDetailActivity extends Activity implements OnClickListener {

	private final String TAG = "WiFiItemDetailActivity";

	public static final String PARAM_INDEX = "index";
	public static final String PARAM_IS_NEW = "is_new";
	private int m_nWiFiItemIdx;
	private boolean m_bNewAdded;
	private WiFiItemInfo m_wifiItem;
	private Context mContext;

	private TextView m_tvWifiDescript;
	private EditText m_etWifiTitle;
	private EditText m_etWifiAddress;

	

	private ListView m_lvAddressSuggestion;

	public String m_szUniqueTokenForAddressSuggestionRequest;

	private SuggestAddressesMgr addrMgr = SuggestAddressesMgr.sharedInstance();

	private boolean m_isTextUpdatedByUser = true;

	// private String m_strCurWifiId;

	// ////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_item_detail);
		ActivityTask.INSTANCE.add(this);

		mContext = (Context) this;

		m_nWiFiItemIdx = getIntent().getIntExtra(PARAM_INDEX, -1);
		m_bNewAdded = getIntent().getBooleanExtra(PARAM_IS_NEW, false);

		if (m_nWiFiItemIdx >= 0 && PRJFUNC.g_WiFiItems != null)
			m_wifiItem = PRJFUNC.g_WiFiItems[m_nWiFiItemIdx];
		else
			m_wifiItem = null;
		
		addrMgr.clearSuggestionList();

		updateLCD();

		refreshUI();

		// - update position
		if (!PRJFUNC.DEFAULT_SCREEN) {
			scaleView();
		}

	}

	private void refreshUI() {
		// m_strCurWifiId = PRJFUNC.getCurrentWiFiSSID(mContext);

		if (!m_bNewAdded) {
			if (m_wifiItem != null) {
				m_etWifiTitle.setText(m_wifiItem.m_szTitle);
				m_isTextUpdatedByUser = false;
				m_etWifiAddress.setText(m_wifiItem.m_szAddress);
				if (!PRJFUNC.isNullOrEmpty(m_wifiItem.m_szWiFiID)) {
					m_tvWifiDescript.setText("WiFi ID is "
							+ m_wifiItem.m_szWiFiID + ".");
				} else {
					m_tvWifiDescript.setText("WiFI is not set.");
				}
			}
		} else {
			m_tvWifiDescript.setText("");
		}
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

	
	private void updateLCD() {

		findViewById(R.id.iv_back).setOnClickListener(this);

		TextView tvBtnSave = (TextView) findViewById(R.id.tv_btn_save);
		if (m_bNewAdded) {
			tvBtnSave.setText(getString(R.string.wifi_detail_register_btn));
		} else {
			tvBtnSave.setText(getString(R.string.wifi_detail_save_btn));
		}

		findViewById(R.id.frm_btn_save).setOnClickListener(this);

		m_tvWifiDescript = (TextView) findViewById(R.id.tv_wifi_descript);
		m_etWifiTitle = (EditText) findViewById(R.id.et_wifi_title);
		m_etWifiAddress = (EditText) findViewById(R.id.et_wifi_address);
		
		m_etWifiAddress.setOnClickListener(this);

		m_lvAddressSuggestion = (ListView) findViewById(R.id.lv_address_presets);
		m_lvAddressSuggestion.setAdapter(new AccountLocationListItemAdapter(
				this));
		m_lvAddressSuggestion.setVisibility(View.GONE);

		registerListeners();
	}

	public void registerListeners() {
		m_etWifiAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				onTxtAddressChanged();
			}
		});

		m_lvAddressSuggestion.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onSuggestionItemClick(position);
			}
		});

	}

	public void onSuggestionItemClick(int index) {
		m_isTextUpdatedByUser = false;
		m_etWifiAddress.setText(addrMgr.getAddressSuggestionByIndex(index));
		
		m_lvAddressSuggestion.setVisibility(View.GONE);

		hideKeyboard();
	}

	private void scaleView() {

	}

	// /////////////////////////////////////
	private void goBack() {
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			onCancel();
			break;
			
		case R.id.et_wifi_address:
			m_etWifiAddress.setText("");
			break;

		case R.id.frm_btn_save:
			onSave();
			break;
		default:
			break;
		}

	}

	private void onSave() {
		hideKeyboard();

		String strTitle = m_etWifiTitle.getText().toString();
		if (strTitle.isEmpty()) {
			Toast.makeText(mContext, "Please input a title.",
					Toast.LENGTH_SHORT).show();
			m_etWifiTitle.requestFocus();
			return;
		}

		String strAddress = m_etWifiAddress.getText().toString();
		if (strAddress.isEmpty()) {
			Toast.makeText(mContext, "Please input an address.",
					Toast.LENGTH_SHORT).show();
			m_etWifiTitle.requestFocus();
			return;
		}

		if (m_bNewAdded)
			m_wifiItem.m_szWiFiID = ""; // m_strCurWifiId;

		m_wifiItem.m_szAddress = strAddress;
		m_wifiItem.m_szTitle = strTitle;

		SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
		phoneDb.saveWiFiItemToStorage(m_nWiFiItemIdx);

		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	private void onCancel() {
		hideKeyboard();

		setResult(RESULT_CANCELED);
		finish();
		overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	private void hideKeyboard() {
		View view = this.getCurrentFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	public void onTxtAddressChanged() {
		if (m_isTextUpdatedByUser == false) {
			m_isTextUpdatedByUser = true;
			return;
		}
		
		String szPrefix = m_etWifiAddress.getText().toString();
		if (szPrefix.length() == 0) {
			addrMgr.clearSuggestionList();

			m_lvAddressSuggestion.invalidateViews();
			return;
		}

		m_lvAddressSuggestion.setVisibility(View.VISIBLE);
		
		m_szUniqueTokenForAddressSuggestionRequest = PRJFUNC
				.generateRandomString();

		final String[] params = { "input", "sensor", "key" };
		final String[] values = { szPrefix, "true",
				getResources().getString(R.string.app_googleapi_places_key) };

		final String szEndpoint = "https://maps.googleapis.com/maps/api/place/autocomplete/json";
		final String szCurrentToken = m_szUniqueTokenForAddressSuggestionRequest;

		AsyncTask<Void, Void, JSONObject> threadHttpRequest = new AsyncTask<Void, Void, JSONObject>() {
			@Override
			protected JSONObject doInBackground(Void... param) {
				JSONObject jsonResponse = NetFuncs.requestConnectionWithURL(
						szEndpoint, params, values);
				return jsonResponse;
			}

			@Override
			protected void onPostExecute(JSONObject jsonResponse) {
				if (jsonResponse == null) {
					Toast.makeText(WiFiItemDetailActivity.this,
							"getAddressSuggestion Request Failed!",
							Toast.LENGTH_SHORT).show();
					return;
				}

				try {
					if (szCurrentToken
							.equalsIgnoreCase(m_szUniqueTokenForAddressSuggestionRequest) == false)
						throw new Exception();
					String status = jsonResponse.getString("status");
					if (status.equalsIgnoreCase("OK") == false)
						throw new Exception();

					addrMgr.m_arrAddressSuggestion = jsonResponse
							.getJSONArray("predictions");

					m_lvAddressSuggestion.invalidateViews();
				} catch (JSONException exception) {
				} catch (Exception e) {

				}
				super.onPostExecute(jsonResponse);
			}

		};

		// execute AsyncTask
		threadHttpRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

}
