package com.onescream.settings;

import android.R.bool;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.onescream.R;
import com.onescream.info.WiFiItemInfo;
import com.onescream.intros.ThankyouActivity;
import com.onescream.library.SuggestAddressesMgr;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.NetFuncs;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for WiFis List Screen
 *
 * Created by Anwar Almojarkesh
 *
 */

public class WiFiItemsActivity extends Activity implements OnClickListener {

	private final String TAG = "WiFiItemsActivity";

	public static final String PARAM_DIRECTLY_OPEN_DETAIL = "open_detail";
	public static final String PARAM_IS_FROM_SIGNUP = "is_from_signup";

	private final int PAGE_WIFI_ITEM_INFO = 10001;

	private Context mContext;

	private boolean m_bFromSignup;
	private boolean m_bDirectlyOpenDetail;
	List<String> listTitles = new ArrayList<String>();

	List<String> listAddress = new ArrayList<String>();

	List<String> listTitlesDefaults = new ArrayList<String>();

	List<String> listAddressDefaults = new ArrayList<String>();
	//private ListView m_lvWifiItems;
	private WifiItemsListAdapter m_adapterWifiItems;

	private EditText et_wifi_address1,et_wifi_address2,et_wifi_address3,et_wifi_title1,et_wifi_title2,et_wifi_title3;
	private ListView m_lvAddressSuggestion1,m_lvAddressSuggestion2,m_lvAddressSuggestion3;
	private SuggestAddressesMgr addrMgr = SuggestAddressesMgr.sharedInstance();
	private SuggestAddressesMgr addrMgr2 = SuggestAddressesMgr.sharedInstance();
	private SuggestAddressesMgr addrMgr3 = SuggestAddressesMgr.sharedInstance();
	
	TextView titile;

	public String m_szUniqueTokenForAddressSuggestionRequest;
	private boolean m_isTextUpdatedByUser = true;


	// ////////////////////////////////////////////////////////////

	public static final String PARAM_INDEX = "index";
	public static final String PARAM_IS_NEW = "is_new";
	private int m_nWiFiItemIdx;
	private boolean m_bNewAdded;
	private WiFiItemInfo m_wifiItem;
	Typeface facethin,facebold,faceRegular,EstiloRegular;

	// //////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityTask.INSTANCE.add(this);
		 
		mContext = (Context) this;

		m_bDirectlyOpenDetail = getIntent().getBooleanExtra(
				PARAM_DIRECTLY_OPEN_DETAIL, false);

		m_bFromSignup = getIntent()
				.getBooleanExtra(PARAM_IS_FROM_SIGNUP, false);
		if(m_bFromSignup)
		{
			setContentView(R.layout.activity_wifi_items);
			initTypeFace();
//			 TextView labelTitle = (TextView) findViewById(R.id.labelll); 
//			 labelTitle.setTypeface(EstiloRegular);
			titile = (TextView ) findViewById(R.id.titile);
			titile.setTypeface(facethin);
		}
		else {
			setContentView(R.layout.update_frequent_address);

		}
		updateLCD();

		// - update position
		if (!PRJFUNC.DEFAULT_SCREEN) {
			scaleView();
		}
		findViewById(R.id.iv_page5).setSelected(true);

		//refreshList();

		// if (m_bDirectlyOpenDetail) {
		// onAddWiFiItem();
		// }
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PAGE_WIFI_ITEM_INFO) {
			if (resultCode == RESULT_OK) {
				//refreshList();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// //////////////////////////////////////////////////
	private void updateLCD() {

		View _v = null;

		findViewById(R.id.iv_back).setOnClickListener(this);
		findViewById(R.id.frm_btn_add_location).setOnClickListener(this);
		findViewById(R.id.frm_btn_finished).setOnClickListener(this);
		TextView finishButton= (TextView) findViewById(R.id.tv_btn_finished);
		//finishButton.setOnClickListener(this);


		et_wifi_address1 = (EditText) findViewById(R.id.et_wifi_address1);
		et_wifi_address2 = (EditText) findViewById(R.id.et_wifi_address2);
		et_wifi_address3 = (EditText) findViewById(R.id.et_wifi_address3);

		et_wifi_title1 = (EditText) findViewById(R.id.et_wifi_title1);
		et_wifi_title2 = (EditText) findViewById(R.id.et_wifi_title2);
		et_wifi_title3 = (EditText) findViewById(R.id.et_wifi_title3);


		et_wifi_address2.setOnClickListener(this);
		et_wifi_address3.setOnClickListener(this);
		et_wifi_address1.setOnClickListener(this);
		listTitlesDefaults.add("");
		listTitlesDefaults.add("");
		listTitlesDefaults.add("");


		listAddressDefaults.add("");
		listAddressDefaults.add("");
		listAddressDefaults.add("");
		if(!m_bFromSignup)
		{
			
			finishButton.setText("Finished");
			loadFrequentAddresses();
		}

		//		m_lvWifiItems = (ListView) findViewById(R.id.lv_wifi_items);
		//		m_adapterWifiItems = new WifiItemsListAdapter(mContext,
		//				R.layout.list_item_wifi_item, new ArrayList<WiFiItemInfo>());
		//		m_adapterWifiItems.setCallback(m_listCallback);
		//		m_lvWifiItems.setAdapter(m_adapterWifiItems);
		if (m_bDirectlyOpenDetail) {
			//m_adapterWifiItems.setDeleteButtonVisible(false);

			//findViewById(R.id.frm_buttons).setVisibility(View.GONE);
		}

		if (m_bFromSignup) {
			findViewById(R.id.frm_indicator).setVisibility(View.VISIBLE);
			//findViewById(R.id.iv_page5).setSelected(true);
		} else {
			findViewById(R.id.frm_indicator).setVisibility(View.GONE);
		}


		m_lvAddressSuggestion1 = (ListView) findViewById(R.id.lv_address_presets1);
		m_lvAddressSuggestion1.setAdapter(new AccountLocationListItemAdapter(
				this));
		m_lvAddressSuggestion1.setVisibility(View.GONE);



		m_lvAddressSuggestion2 = (ListView) findViewById(R.id.lv_address_presets2);
		m_lvAddressSuggestion2.setAdapter(new AccountLocationListItemAdapter(
				this));
		m_lvAddressSuggestion2.setVisibility(View.GONE);


		m_lvAddressSuggestion3 = (ListView) findViewById(R.id.lv_address_presets3);
		m_lvAddressSuggestion3.setAdapter(new AccountLocationListItemAdapter(
				this));
		m_lvAddressSuggestion3.setVisibility(View.GONE);

		registerListeners();
	}

	private void scaleView() {

	}

	// /////////////////////////////////////
	private void goBack() {
		if (m_bFromSignup) {
			Intent intent = new Intent(WiFiItemsActivity.this,
					ThankyouActivity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.right_in, R.anim.left_out);
		} else {
			finish();
			overridePendingTransition(R.anim.left_in, R.anim.right_out);
		}
	}

	/*WifiItemsListAdapter.Callback m_listCallback = new WifiItemsListAdapter.Callback() {

		@Override
		public void onItemDelete(int p_nPosition) {
			final int _position = p_nPosition;
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
			alertDialog.setTitle("One Scream");
			alertDialog.setMessage("Are you sure to delete this WiFi?");
			alertDialog.setPositiveButton(R.string.word_yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(
									mContext);

							WiFiItemInfo item = m_adapterWifiItems
									.getItem(_position);

							int idx = PRJFUNC
									.getWiFiItemIdxWithTitle(item.m_szTitle);
							for (int i = idx; i < PRJFUNC.g_WiFiItems.length - 1; i++) {
								PRJFUNC.g_WiFiItems[i] = PRJFUNC.g_WiFiItems[i + 1];
								phoneDb.saveWiFiItemToStorage(idx);
							}
							PRJFUNC.g_WiFiItems[PRJFUNC.g_WiFiItems.length - 1] = new WiFiItemInfo();
							phoneDb.saveWiFiItemToStorage(PRJFUNC.g_WiFiItems.length - 1);

							refreshList();
						}
					});
			alertDialog.setNegativeButton(R.string.word_no, null);
			alertDialog.show();
		}

		@Override
		public void onItemClick(int p_nPosition) {
			if (m_bDirectlyOpenDetail) {
				int idx = PRJFUNC.getWiFiItemIdxWithTitle(m_adapterWifiItems
						.getItem(p_nPosition).m_szTitle);
				PRJFUNC.g_WiFiItems[idx].m_szWiFiID = PRJFUNC.getCurrentWiFiSSID(mContext);
		        SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
		        phoneDb.saveWiFiItemToStorage(idx);
		        goBack();
			} else {
				int idx = PRJFUNC.getWiFiItemIdxWithTitle(m_adapterWifiItems
						.getItem(p_nPosition).m_szTitle);
				goToWiFiItemDetailActivity(idx, false);
			}
		}
	};
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			goBack();
			break;
		case R.id.frm_btn_add_location:


			hideKeyboard();
			String strTitle1 = et_wifi_title1.getText().toString();
			String strAddress1 = et_wifi_address1.getText().toString();

			if (strTitle1.isEmpty()) {
				Toast.makeText(mContext, "Please input a title.",
						Toast.LENGTH_SHORT).show();
				et_wifi_title1.requestFocus();
				return;
			}

			if (strAddress1.isEmpty()) {
				Toast.makeText(mContext, "Please input an address.",
						Toast.LENGTH_SHORT).show();
				et_wifi_address1.requestFocus();
				return;
			}


			String strTitle2 = et_wifi_title2.getText().toString();
			String strAddress2 = et_wifi_address2.getText().toString();
			String strTitle3 = et_wifi_title3.getText().toString();
			String strAddress3 = et_wifi_address3.getText().toString();

			listTitles.add(strTitle1);
			listTitles.add(strTitle2);
			listTitles.add(strTitle3);


			listAddress.add(strAddress1);
			listAddress.add(strAddress2);
			listAddress.add(strAddress3);

			for(int x=0 ; x<listAddress.size();x++)
			{
				 
				if(!listAddress.get(x).equals(listAddressDefaults.get(x))||!listTitles.get(x).equals(listTitlesDefaults.get(x)))
				{
					Log.d("",listAddress.get(x)+" listTitles "+listAddressDefaults.get(x));
					if(listAddress.get(x)!="" && listTitles.get(x)!="")

					{
						 
						if(m_bFromSignup)
						{
						onAddWiFiItem(x);
						}
						else {
							if (m_nWiFiItemIdx >= 0 && PRJFUNC.g_WiFiItems != null)
								m_wifiItem = PRJFUNC.g_WiFiItems[x];
							else
								m_wifiItem = null;
							onSave(x);
						}
					
						 
					}
				}
			}		

			for (int i = 0; i < PRJFUNC.g_WiFiItems.length; i++) {
				WiFiItemInfo wifiItem = PRJFUNC.g_WiFiItems[i];
				if (wifiItem != null && wifiItem.m_szTitle != null
						&& !wifiItem.m_szTitle.isEmpty()) {

					Log.d("",wifiItem.m_szWiFiID+"----"+wifiItem.m_szTitle+"--wifiItem--"+wifiItem.m_szAddress);
				}
			}
			goBack();
			break;
		case R.id.frm_btn_finished:

			if(m_bFromSignup)
			{
				String strTitle = et_wifi_title1.getText().toString();
				String strAddress = et_wifi_address1.getText().toString();



				if (strAddress.isEmpty()) {
					Toast.makeText(mContext, "you have to fill in home address",
							Toast.LENGTH_SHORT).show();
					et_wifi_address1.requestFocus();
					return;
				}
			}
			goBack();
			break;
		default:
			break;
		}
	}

	private void onAddWiFiItem(int indexx) {

		// String strCurWifiId = PRJFUNC.getCurrentWiFiSSID(mContext);
		// if (strCurWifiId == null || strCurWifiId.isEmpty()) {
		// Toast.makeText(mContext, "Can not find the WiFi hub.",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }
		//
		// if (PRJFUNC.getWiFiItemIdx(strCurWifiId) >= 0) {
		// Toast.makeText(mContext, "Current WiFi was already added.",
		// Toast.LENGTH_SHORT).show();
		// return;
		// }

		if (PRJFUNC.g_WiFiItems == null) {
			SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
			phoneDb.loadWiFiItemsFromStorage();
		}

		int idxToAdd = -1;
		for (int i = 0; i < PRJFUNC.g_WiFiItems.length; i++) {
			if (PRJFUNC.g_WiFiItems[i].m_szTitle == null
					|| PRJFUNC.g_WiFiItems[i].m_szTitle.isEmpty()) {
				idxToAdd = i;
				break;
			}
		}

		if (idxToAdd < 0) {
			Toast.makeText(mContext, "WiFi can not be added any more.",
					Toast.LENGTH_SHORT).show();
			return;
		}


		goToWiFiItemDetailActivity(idxToAdd, true,indexx);


	}

	private void refreshList() {
		m_adapterWifiItems.clear();

		if (PRJFUNC.g_WiFiItems == null)
			return;

		for (int i = 0; i < PRJFUNC.g_WiFiItems.length; i++) {
			WiFiItemInfo wifiItem = PRJFUNC.g_WiFiItems[i];
			if (wifiItem != null && wifiItem.m_szTitle != null
					&& !wifiItem.m_szTitle.isEmpty()) {
				m_adapterWifiItems.add(wifiItem);
				Log.d("",wifiItem.m_szWiFiID+"----"+wifiItem.m_szTitle+"--wifiItem--"+wifiItem.m_szAddress);
			}
		}
	}

	public void goToWiFiItemDetailActivity(int p_nIdx, boolean p_bNew,int indexx) {
		/*Intent intent = new Intent(WiFiItemsActivity.this,
				WiFiItemDetailActivity.class);
		intent.putExtra(WiFiItemDetailActivity.PARAM_INDEX, p_nIdx);
		intent.putExtra(WiFiItemDetailActivity.PARAM_IS_NEW, p_bNew);
		startActivityForResult(intent, PAGE_WIFI_ITEM_INFO);
		overridePendingTransition(R.anim.right_in, R.anim.hold);
		 */

		m_nWiFiItemIdx = p_nIdx;
		m_bNewAdded = p_bNew;

		if (m_nWiFiItemIdx >= 0 && PRJFUNC.g_WiFiItems != null)
			m_wifiItem = PRJFUNC.g_WiFiItems[m_nWiFiItemIdx];
		else
			m_wifiItem = null;
		onSave(indexx);

	}
	public void registerListeners() {
		et_wifi_address1.addTextChangedListener(new TextWatcher() {
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

		et_wifi_address2.addTextChangedListener(new TextWatcher() {
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
				onTxtAddressChanged2();
			}
		});

		et_wifi_address3.addTextChangedListener(new TextWatcher() {
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
				onTxtAddressChanged3();
			}
		});


		m_lvAddressSuggestion1.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onSuggestionItemClick(position);
			}
		});
		m_lvAddressSuggestion2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onSuggestionItemClick2(position);
			}
		});
		m_lvAddressSuggestion3.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onSuggestionItemClick3(position);
			}
		});
	}

	public void onSuggestionItemClick(int index) {
		m_isTextUpdatedByUser = false;

		et_wifi_address1.setText(addrMgr.getAddressSuggestionByIndex(index));

		m_lvAddressSuggestion1.setVisibility(View.GONE);

		hideKeyboard();
	}

	public void onSuggestionItemClick2(int index) {
		m_isTextUpdatedByUser = false;

		et_wifi_address2.setText(addrMgr2.getAddressSuggestionByIndex(index));

		m_lvAddressSuggestion2.setVisibility(View.GONE);

		hideKeyboard();
	}

	public void onSuggestionItemClick3(int index) {
		m_isTextUpdatedByUser = false;

		et_wifi_address3.setText(addrMgr3.getAddressSuggestionByIndex(index));

		m_lvAddressSuggestion3.setVisibility(View.GONE);

		hideKeyboard();
	}
	public void onTxtAddressChanged() {
		if (m_isTextUpdatedByUser == false) {
			m_isTextUpdatedByUser = true;
			return;
		}

		String szPrefix = et_wifi_address1.getText().toString();
		if (szPrefix.length() == 0) {
			addrMgr.clearSuggestionList();

			m_lvAddressSuggestion1.invalidateViews();
			return;
		}

		m_lvAddressSuggestion1.setVisibility(View.VISIBLE);

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
					Toast.makeText(WiFiItemsActivity.this,
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

					m_lvAddressSuggestion1.invalidateViews();
				} catch (JSONException exception) {
				} catch (Exception e) {

				}
				super.onPostExecute(jsonResponse);
			}

		};

		// execute AsyncTask
		threadHttpRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void onTxtAddressChanged2() {
		if (m_isTextUpdatedByUser == false) {
			m_isTextUpdatedByUser = true;
			return;
		}

		String szPrefix = et_wifi_address2.getText().toString();
		if (szPrefix.length() == 0) {
			addrMgr2.clearSuggestionList();

			m_lvAddressSuggestion2.invalidateViews();
			return;
		}

		m_lvAddressSuggestion2.setVisibility(View.VISIBLE);

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
					Toast.makeText(WiFiItemsActivity.this,
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

					addrMgr2.m_arrAddressSuggestion = jsonResponse
							.getJSONArray("predictions");

					m_lvAddressSuggestion2.invalidateViews();
				} catch (JSONException exception) {
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onPostExecute(jsonResponse);
			}

		};

		// execute AsyncTask
		threadHttpRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public void onTxtAddressChanged3() {
		if (m_isTextUpdatedByUser == false) {
			m_isTextUpdatedByUser = true;
			return;
		}

		String szPrefix = et_wifi_address3.getText().toString();
		if (szPrefix.length() == 0) {
			addrMgr3.clearSuggestionList();

			m_lvAddressSuggestion3.invalidateViews();
			return;
		}

		m_lvAddressSuggestion3.setVisibility(View.VISIBLE);

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
					Toast.makeText(WiFiItemsActivity.this,
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

					addrMgr3.m_arrAddressSuggestion = jsonResponse
							.getJSONArray("predictions");

					m_lvAddressSuggestion3.invalidateViews();
				} catch (JSONException exception) {
				} catch (Exception e) {

				}
				super.onPostExecute(jsonResponse);
			}

		};

		// execute AsyncTask
		threadHttpRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	private void hideKeyboard() {
		View view = this.getCurrentFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	private void onSave(int indexx) {
		hideKeyboard();

		//		String strTitle1 = et_wifi_title1.getText().toString();
		//	    String strAddress1 = et_wifi_address1.getText().toString();
		//		String strTitle2 = et_wifi_title2.getText().toString();
		//		String strAddress2 = et_wifi_address2.getText().toString();
		//		String strTitle3 = et_wifi_title3.getText().toString();
		//	   String strAddress3 = et_wifi_address3.getText().toString();



		m_wifiItem.m_szAddress = listAddress.get(indexx);
		m_wifiItem.m_szTitle = listTitles.get(indexx);

		Log.d(""+m_nWiFiItemIdx,listAddress.get(indexx)+ " m_nWiFiItemIdx "+listTitles.get(indexx));
		SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
		phoneDb.saveWiFiItemToStorage(indexx);

		if (PRJFUNC.g_WiFiItems == null)
			return;


		//setResult(RESULT_OK);
		//finish();
		//overridePendingTransition(R.anim.hold, R.anim.right_out);
	}

	private void loadFrequentAddresses()
	{
		if (PRJFUNC.g_WiFiItems == null)
			return;

		for (int i = 0; i < PRJFUNC.g_WiFiItems.length; i++) {
			WiFiItemInfo wifiItem = PRJFUNC.g_WiFiItems[i];
			if (wifiItem != null && wifiItem.m_szTitle != null
					&& !wifiItem.m_szTitle.isEmpty()) {
				//m_adapterWifiItems.add(wifiItem);

				if(i==0)
				{
					listAddressDefaults.add(i, wifiItem.m_szAddress);
					listTitlesDefaults.add(i, wifiItem.m_szTitle);

					et_wifi_title1.setText(wifiItem.m_szTitle);
					et_wifi_address1.setText(wifiItem.m_szAddress);
				}
				else if(i==1)
				{
					listAddressDefaults.add(i, wifiItem.m_szAddress);
					listTitlesDefaults.add(i, wifiItem.m_szTitle);
					et_wifi_title2.setText(wifiItem.m_szTitle);
					et_wifi_address2.setText(wifiItem.m_szAddress);
				}
				if(i==2)
				{
					listAddressDefaults.add(i, wifiItem.m_szAddress);
					listTitlesDefaults.add(i, wifiItem.m_szTitle);
					et_wifi_title3.setText(wifiItem.m_szTitle);
					et_wifi_address3.setText(wifiItem.m_szAddress);
				}
				Log.d("",wifiItem.m_szWiFiID+"----"+wifiItem.m_szTitle+"--wifiItem--"+wifiItem.m_szAddress);
			}
		}

	}
	private void initTypeFace()
	{
		  facethin = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Thin.ttf");
		  facebold = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Bold.ttf");
		  faceRegular = Typeface.createFromAsset(getAssets(),
	            "fonts/Roboto-Regular.ttf");
	
		  EstiloRegular = Typeface.createFromAsset (getAssets(),
		            "fonts/EstiloRegular.otf");}
}
