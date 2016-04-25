package com.onescream.settings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.onescream.R;
import com.onescream.info.WiFiItemInfo;
import com.onescream.intros.AddressFragment;
import com.onescream.intros.FirstScreenActivity;
import com.onescream.library.SuggestAddressesMgr;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.NetFuncs;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddAdressActivity extends Activity implements View.OnClickListener {
    private Context mContext;
    Typeface facethin, facebold, faceRegular, EstiloRegular, sanfacebold;
    private EditText et_wifi_address1, et_wifi_address2, et_wifi_address3, et_wifi_address4, et_wifi_address5, et_wifi_address6;
    private ListView m_lvAddressSuggestion1, m_lvAddressSuggestion2;
    List<String> listAddress = new ArrayList<String>();
    List<String> listAddressDefaults = new ArrayList<String>();
    private boolean m_bFromSignup = false;
    private boolean m_bDirectlyOpenDetail;
    public static final String PARAM_DIRECTLY_OPEN_DETAIL = "open_detail";
    public static final String PARAM_IS_FROM_SIGNUP = "is_from_signup";
    public static final String pref_name = "onescreamshared";
    private int m_nWiFiItemIdx;
    private WiFiItemInfo m_wifiItem;
    private boolean m_bNewAdded;
    private WifiItemsListAdapter m_adapterWifiItems;
    private boolean m_isTextUpdatedByUser = true;
    private SuggestAddressesMgr addrMgr = SuggestAddressesMgr.sharedInstance();
    private SuggestAddressesMgr addrMgr2 = SuggestAddressesMgr.sharedInstance();
    private SuggestAddressesMgr addrMgr3 = SuggestAddressesMgr.sharedInstance();
    public String m_szUniqueTokenForAddressSuggestionRequest, Type_screen;
    public boolean goToactivity;
    public SharedPreferences sharedpreferences;
    public SharedPreferences.Editor editor;
    public String txt1 = "", txt2 = "", txt3 = "", txt4 = "", txt5 = "", txt6 = "";
    int edit = 0;
    String object_id1 = "", object_id2 = "", object_id3 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adreess_two);
        ActivityTask.INSTANCE.add(this);

        mContext = (Context) this;
        m_bDirectlyOpenDetail = getIntent().getBooleanExtra(
                PARAM_DIRECTLY_OPEN_DETAIL, false);

        m_bFromSignup = getIntent()
                .getBooleanExtra(PARAM_IS_FROM_SIGNUP, false);
        initTypeFace();
        TextView titile = (TextView) findViewById(R.id.tv_sub_title);
        TextView btn_tv = (TextView) findViewById(R.id.save);
        titile.setTypeface(sanfacebold);
        btn_tv.setTypeface(sanfacebold);
        Bundle extras = getIntent().getExtras();
        sharedpreferences = getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        if (extras != null) {
            String value = extras.getString("btn_txt", "Save Address");
            btn_tv.setText(value);
            String titile_txt = extras.getString("title_txt", "Address");
            titile.setText(titile_txt);
            Type_screen = extras.getString("type", "0");
            goToactivity = extras.getBoolean(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN, true);
            edit = extras.getInt("edit", 0);

        }
        updateLCD();


        if (edit == 1) {
            loadFrequentAddresses();
        }
    }

    private void initTypeFace() {
        facethin = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");
        facebold = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Bold.ttf");
        faceRegular = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Regular.ttf");

        EstiloRegular = Typeface.createFromAsset(getAssets(),
                "fonts/EstiloRegular.otf");
        sanfacebold = Typeface.createFromAsset(this
                        .getAssets(),
                "fonts/SanFranciscoDisplay-Bold.otf");

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
        et_wifi_address1 = (EditText) findViewById(R.id.et_wifi_busines);
        et_wifi_address2 = (EditText) findViewById(R.id.et_wifi_street1);
        et_wifi_address3 = (EditText) findViewById(R.id.et_wifi_stree2);
        et_wifi_address4 = (EditText) findViewById(R.id.et_wifi_addresscity);
        et_wifi_address5 = (EditText) findViewById(R.id.et_wifi_addresstate);
        et_wifi_address6 = (EditText) findViewById(R.id.et_wifi_addresspostal);
        et_wifi_address1.setHintTextColor(getResources().getColor(R.color.et_default_color));
        et_wifi_address2.setHintTextColor(getResources().getColor(R.color.et_default_color));
        et_wifi_address3.setHintTextColor(getResources().getColor(R.color.et_default_color));
        et_wifi_address4.setHintTextColor(getResources().getColor(R.color.et_default_color));
        et_wifi_address5.setHintTextColor(getResources().getColor(R.color.et_default_color));
        et_wifi_address6.setHintTextColor(getResources().getColor(R.color.et_default_color));
        switch (Type_screen) {
            case "1":
                et_wifi_address1.setHint("Address 1");
                et_wifi_address2.setHint("Address 2");
                et_wifi_address3.setHint("Apt/Flat/Suite");
                et_wifi_address4.setHint("City");
                et_wifi_address5.setHint("Country");
                et_wifi_address6.setHint("Postcode");
                break;
            case "2":
                et_wifi_address1.setHint("Business Name");
                et_wifi_address2.setHint("Address 1");
                et_wifi_address3.setHint("Address 2");
                et_wifi_address4.setHint("City");
                et_wifi_address5.setHint("Country");
                et_wifi_address6.setHint("Postcode");
                break;
            case "3":
                et_wifi_address1.setHint("Address 1");
                et_wifi_address2.setHint("Address 2");
                et_wifi_address3.setHint("Apt/Flat/Suite");
                et_wifi_address4.setHint("City");
                et_wifi_address5.setHint("Country");
                et_wifi_address6.setHint("Postcode");
                break;
        }
        m_lvAddressSuggestion1 = (ListView) findViewById(R.id.lv_address_streets1);
        m_lvAddressSuggestion1.setAdapter(new AccountLocationListItemAdapter(
                this));
        m_lvAddressSuggestion1.setVisibility(View.GONE);
        m_lvAddressSuggestion2 = (ListView) findViewById(R.id.lv_address_streets2);
        m_lvAddressSuggestion2.setAdapter(new AccountLocationListItemAdapter(
                this));
        m_lvAddressSuggestion2.setVisibility(View.GONE);
        findViewById(R.id.iv_back).setOnClickListener(this);
//        TextView Title_txt = (TextView) findViewById(R.id.tv_sub_title);
//        Title_txt.setText("Home adderes");
//        TextView Save_btn = (TextView) findViewById(R.id.save);
//        Title_txt.setText("Home adderes");
        findViewById(R.id.add).setOnClickListener(this);
        listAddressDefaults.add("");
        listAddressDefaults.add("");
        listAddressDefaults.add("");
        listAddressDefaults.add("");
        listAddressDefaults.add("");
        listAddressDefaults.add("");
        if (!m_bFromSignup) {

            loadFrequentAddresses();
        }
        registerListeners();
    }

    private void scaleView() {

    }

    // /////////////////////////////////////
    private void goBack() {
        if (goToactivity) {
            Intent intent = new Intent(AddAdressActivity.this,
                    AddressFragment.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        } else {
            Intent intent = new Intent(AddAdressActivity.this,
                    FrequentAddress.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.right_in, R.anim.left_out);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                goBack();
                break;
            case R.id.add:

                hideKeyboard();
                String strAddress1 = et_wifi_address1.getText().toString();
                String strpostal = et_wifi_address6.getText().toString();
                String strCity = et_wifi_address4.getText().toString();
                String straddrres = et_wifi_address2.getText().toString();
                if (Type_screen.equalsIgnoreCase("2")) {


                    if (strAddress1.isEmpty()) {
                        PRJFUNC.showAlertDialog(this,
                                "Please input the companies name");
                        //                    Toast.makeText(mContext, "",
                        //                            Toast.LENGTH_SHORT).show();
                        et_wifi_address1.requestFocus();
                        return;
                    }
                    if (straddrres.isEmpty()) {
                        PRJFUNC.showAlertDialog(this,
                                "Please input your street name and number");
//                    Toast.makeText(mContext, "",
//                            Toast.LENGTH_SHORT).show();
                        et_wifi_address1.requestFocus();
                        return;
                    }
                    if (strCity.isEmpty()) {
                        PRJFUNC.showAlertDialog(this,
                                "Please input your city");
//                    Toast.makeText(mContext, "",
//                            Toast.LENGTH_SHORT).show();
                        et_wifi_address1.requestFocus();
                        return;
                    }
                } else {
                    if (strAddress1.isEmpty()) {
                        PRJFUNC.showAlertDialog(this,
                                "Please input your street name and number");
                        //                    Toast.makeText(mContext, "",
                        //                            Toast.LENGTH_SHORT).show();
                        et_wifi_address1.requestFocus();
                        return;
                    }
                }
                if (strpostal.isEmpty()) {
                    PRJFUNC.showAlertDialog(this,
                            "Please verify your postcode is correct");
//                    Toast.makeText(mContext, "",
//                            Toast.LENGTH_SHORT).show();
                    et_wifi_address1.requestFocus();
                    return;
                }
                if (strpostal.length() > 8 || strpostal.length() < 5) {
                    PRJFUNC.showAlertDialog(this,
                            "Please verify your postcode is correct");
//                    Toast.makeText(mContext, "",
//                            Toast.LENGTH_SHORT).show();
                    et_wifi_address1.requestFocus();
                    return;
                }


                String strAddress2 = et_wifi_address2.getText().toString();
                String strAddress3 = et_wifi_address3.getText().toString();
                String strAddress4 = et_wifi_address4.getText().toString();
                String strAddress5 = et_wifi_address5.getText().toString();
                String strAddress6 = et_wifi_address6.getText().toString();

                if (strAddress4.isEmpty()) {
//                Toast.makeText(mContext, "",
//                        Toast.LENGTH_SHORT).show();
                    PRJFUNC.showAlertDialog(this,
                            "Please input your city");
                    et_wifi_address1.requestFocus();
                    return;
                }
                if (strAddress6.isEmpty()) {
//                    Toast.makeText(mContext, "Please input your postcode",
//                            Toast.LENGTH_SHORT).show();
                    PRJFUNC.showAlertDialog(this,
                            "Please input your postcode");
                    et_wifi_address1.requestFocus();
                    return;
                }
                if (strAddress1.isEmpty()) {
                    listAddress.add("");
                } else {
                    listAddress.add(strAddress1);
                }
                if (strAddress2.isEmpty()) {
                    listAddress.add("");
                } else {
                    listAddress.add(strAddress2);
                }
                if (strAddress3.isEmpty()) {
                    listAddress.add("");
                } else {
                    listAddress.add(strAddress3);
                }
                if (strAddress4.isEmpty()) {
                    listAddress.add("");
                } else {
                    listAddress.add(strAddress4);
                }
                if (strAddress5.isEmpty()) {
                    listAddress.add("");
                } else {
                    listAddress.add(strAddress5);
                }
                if (strAddress6.isEmpty()) {
                    listAddress.add("");
                } else {
                    listAddress.add(strAddress6);
                }


                if (Type_screen.equalsIgnoreCase("1")) {
                    if (object_id1 == null || object_id1.trim().equalsIgnoreCase("")) {
                        addHomeInformation(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Home");
                    } else {
                        updateHomeInformation(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Work");
                    }
                }
                if (Type_screen.equalsIgnoreCase("2")) {
                    if (object_id2 == null || object_id2.trim().equalsIgnoreCase("")) {
                        addWorkaddres(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Work");
                    } else {
                        updateWorkAddress(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Work");
                    }
                }
                if (Type_screen.equalsIgnoreCase("3")) {
                    if (object_id3 == null || object_id3.trim().equalsIgnoreCase("")) {
                        addOtherAddress(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Other");
                    } else {
                        updateFreqAddress(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Work");
                    }
                }

//                for(int x=0 ; x<listAddress.size();x++)
//                {
//
//                    if(!listAddress.get(x).equals(listAddressDefaults.get(x)))
//                    {
//                        Log.d("", listAddress.get(x) + " listTitles " + listAddressDefaults.get(x));
//                        if(listAddress.get(x)!="" )
//
//                        {
//
//                            if(m_bFromSignup)
//                            {
//                                onAddWiFiItem(x);
//                            }
//                            else {
//                                if (m_nWiFiItemIdx >= 0 && PRJFUNC.g_WiFiItems != null)
//                                    m_wifiItem = PRJFUNC.g_WiFiItems[x];
//                                else
//                                    m_wifiItem = null;
//                                onSave(x);
//                            }
////                            onAddWiFiItem(x);
//
//                        }
//                    }
//                }
//
//                for (int i = 0; i < PRJFUNC.g_WiFiItems.length; i++) {
//                    WiFiItemInfo wifiItem = PRJFUNC.g_WiFiItems[i];
//                    if (wifiItem != null && wifiItem.m_szTitle != null
//                            && !wifiItem.m_szTitle.isEmpty()) {
//
//                        Log.d("",wifiItem.m_szWiFiID+"----"+wifiItem.m_szTitle+"--wifiItem--"+wifiItem.m_szAddress);
//                    }
//                }

                break;

            default:
                break;
        }
    }

    private void updateWorkAddress(final String strAddress1, final String strAddress2, final String strAddress3, final String strAddress4, final String strAddress5, final String strAddress6, String work) {

        ParseQuery query = new ParseQuery("UserAddress");
        PRJFUNC.showProgress(AddAdressActivity.this, "");
        query.getInBackground(object_id2, new GetCallback<ParseObject>() {


            @Override
            public void done(ParseObject user, ParseException e) {
                if (e == null) {

                    user.put("streetAddress1", strAddress2);
                    user.put("streetAddress2", strAddress3);
                    user.put("postal", strAddress6);
                    user.put("city", strAddress4);
                    user.put("state", strAddress5);
                    user.put("addressType", "work");
                    user.put("businessName", strAddress1);
                    user.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e("a", ""+e.getMessage());
                            }
                            PRJFUNC.closeProgress();
                            updateUserTable(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Home",object_id2,"workAddress");

                        }
                    });
                } else {
                    e.printStackTrace();
                }
                PRJFUNC.closeProgress();
            }
        });


//        ParseUser usernew = ParseUser.getCurrentUser();
//        usernew.put("objectId", usernew.getObjectId());
//
//        usernew.put("workAddress", object_id2);
//
//        PRJFUNC.showProgress(AddAdressActivity.this, "");
//
//        usernew.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                PRJFUNC.closeProgress();
//                if (e != null) {
//                    e.printStackTrace();
//                    return;
//                }
//                PRJFUNC.closeProgress();
//                SaveAddress(object_id2);
//                goBack();
//            }
//        });

    }

    private void updateFreqAddress(final String strAddress1, final String strAddress2, final String strAddress3, final String strAddress4, final String strAddress5, final String strAddress6, String work) {
        ParseQuery query = new ParseQuery("UserAddress");
        PRJFUNC.showProgress(AddAdressActivity.this, "");
        query.getInBackground(object_id3, new GetCallback<ParseObject>() {


            @Override
            public void done(ParseObject user, ParseException e) {
                if (e == null) {

                    user.put("streetAddress1", strAddress1);
                    user.put("streetAddress2", strAddress2);
                    user.put("postal", strAddress6);
                    user.put("city", strAddress4);
                    user.put("apt_flat", strAddress3);
                    user.put("state", strAddress5);
                    user.put("addressType", "other");
                    user.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("a", "");
                            }
                            updateUserTable(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Home",object_id3,"frequentedAddress");

                            PRJFUNC.closeProgress();

                        }
                    });

                } else {
                    e.printStackTrace();
                }
                PRJFUNC.closeProgress();
            }
        });

//        ParseUser usernew = ParseUser.getCurrentUser();
//        usernew.put("objectId", usernew.getObjectId());
//
//        usernew.put("frequentedAddress", object_id3);
//
//        PRJFUNC.showProgress(AddAdressActivity.this, "");
//
//        usernew.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                PRJFUNC.closeProgress();
//                if (e != null) {
//                    e.printStackTrace();
//                    return;
//                }
//                PRJFUNC.closeProgress();
//
//                SaveAddress(object_id3);
//                goBack();
//            }
//        });


    }

    private void updateHomeInformation(final String strAddress1, final String strAddress2, final String strAddress3, final String strAddress4, final String strAddress5, final String strAddress6, String work) {


        ParseQuery query = new ParseQuery("UserAddress");
        PRJFUNC.showProgress(AddAdressActivity.this, "");
        query.getInBackground(object_id1, new GetCallback<ParseObject>() {


            @Override
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.put("streetAddress1", strAddress1);
                    object.put("streetAddress2", strAddress2);
                    object.put("postal", strAddress6);
                    object.put("city", strAddress4);
                    object.put("apt_flat", strAddress3);
                    object.put("addressType", "Home");
                    object.put("state", strAddress5);
                    object.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.d("a", "");
                            }
                            updateUserTable(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Home",object_id1,"homeAddress");

                            PRJFUNC.closeProgress();

                        }
                    });
                } else {
                    e.printStackTrace();
                }
                PRJFUNC.closeProgress();
            }
        });


//        ParseUser usernew = ParseUser.getCurrentUser();
//        usernew.put("objectId", usernew.getObjectId());
//        usernew.put("streetAddress1", strAddress1);
//        usernew.put("streetAddress2", strAddress2);
//        usernew.put("postal", strAddress6);
//        usernew.put("city", strAddress4);
//        usernew.put("apt_flat", strAddress3);
//        usernew.put("addressType", "Home");
////        usernew.put("homeAddress", object_id1);
//
//        PRJFUNC.showProgress(AddAdressActivity.this, "");
//
//        usernew.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                PRJFUNC.closeProgress();
//                if (e != null) {
//                    e.printStackTrace();
//                    return;
//                }
//                PRJFUNC.closeProgress();
//                SaveAddress(object_id1);
//                goBack();
//
//                ParseObject student = new ParseObject("User");
//                student.put("homeAddress", ParseObject.createWithoutData("UserAddress", object_id1));
////...set other values for the student object
//                student.saveInBackground();
//            }
//        });
////        final ParseObject classAObject = new ParseObject("User");
////
////         classAObject.put("homeAddress", ParseObject.createWithoutData(ParseUser.class, object_id1));


    }

    private void addOtherAddress(final String strAddress1, final String strAddress2, final String strAddress3, final String strAddress4, final String strAddress5, final String strAddress6, String work) {
        final ParseObject user = new ParseObject("UserAddress");
//        user.put("objectId", getu.getObjectId());
        user.put("streetAddress1", strAddress1);
        user.put("streetAddress2", strAddress2);
        user.put("postal", strAddress6);
        user.put("city", strAddress4);
        user.put("apt_flat", strAddress3);
        user.put("state", strAddress5);
        user.put("addressType", "other");
        PRJFUNC.showProgress(AddAdressActivity.this, "");
        user.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("a", "");
                }
                object_id3 = user.getObjectId();
                updateUserTable(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Home",object_id3,"frequentedAddress");
                PRJFUNC.closeProgress();

            }
        });

//        ParseUser usernew = ParseUser.getCurrentUser();
//        usernew.put("objectId", usernew.getObjectId());
//
//        usernew.put("frequentedAddress", object_id3);
//
//        PRJFUNC.showProgress(AddAdressActivity.this, "");
//
//        usernew.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                PRJFUNC.closeProgress();
//                if (e != null) {
//                    e.printStackTrace();
//                    return;
//                }
//                PRJFUNC.closeProgress();
//
//                SaveAddress(object_id3);
//                goBack();
//            }
//        });
    }

    private void addWorkaddres(final String strAddress1, final String strAddress2, final String strAddress3, final String strAddress4, final String strAddress5, final String strAddress6, String work) {
        final ParseObject user = new ParseObject("UserAddress");
//        user.put("objectId", getu.getObjectId());
        user.put("streetAddress1", strAddress2);
        user.put("streetAddress2", strAddress3);
        user.put("postal", strAddress6);
        user.put("city", strAddress4);
        user.put("state", strAddress5);
        user.put("addressType", "work");
        user.put("businessName", strAddress1);
        PRJFUNC.showProgress(AddAdressActivity.this, "");
        user.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("a", "");
                }
                object_id2 = user.getObjectId();
                updateUserTable(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Home",object_id2,"workAddress");

                PRJFUNC.closeProgress();

            }
        });


//        ParseUser usernew = ParseUser.getCurrentUser();
//        usernew.put("objectId", usernew.getObjectId());
//
//        usernew.put("workAddress", object_id2);
//
//        PRJFUNC.showProgress(AddAdressActivity.this, "");
//
//        usernew.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(ParseException e) {
//                PRJFUNC.closeProgress();
//                if (e != null) {
//                    e.printStackTrace();
//                    return;
//                }
//                PRJFUNC.closeProgress();
//                SaveAddress(object_id2);
//                goBack();
//            }
//        });


    }


    public void addHomeInformation(final String strAddress1, final String strAddress2, final String strAddress3, final String strAddress4, final String strAddress5, final String strAddress6, String home) {
        final ParseObject user = new ParseObject("UserAddress");
//        user.put("objectId", getu.getObjectId());
        user.put("streetAddress1", strAddress1);
        user.put("streetAddress2", strAddress2);
        user.put("postal", strAddress6);
        user.put("city", strAddress4);
        user.put("apt_flat", strAddress3);
        user.put("addressType", "Home");
        user.put("state", strAddress5);

        PRJFUNC.showProgress(AddAdressActivity.this, "");
        user.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    Log.d("a", "");
                }
                object_id1 = user.getObjectId();
                PRJFUNC.closeProgress();
                updateUserTable(strAddress1, strAddress2, strAddress3, strAddress4, strAddress5, strAddress6, "Home",object_id1, "homeAddress");

//                final ParseObject classAObject = new ParseObject("User");
//                classAObject.put("homeAddress", ParseObject.createWithoutData("UserAddress", object_id1));
            }
        });


    }

    public void updateUserTable(String address1, final String strAddress1, final String strAddress2, final String strAddress3, final String strAddress4, String strAddress5, final String strAddress6, final String home, String homeAddress) {

        final ParseUser usernew = ParseUser.getCurrentUser();
        usernew.put("objectId", usernew.getObjectId());
       if(Type_screen.equalsIgnoreCase("1")) {
           usernew.put("streetAddress1", strAddress1);

           usernew.put("streetAddress2", strAddress2);
           usernew.put("postal", strAddress6);
           usernew.put("city", strAddress4);

           usernew.put("apt_flat", strAddress3);
           usernew.put("addressType", "Home");
       }
        usernew.put(homeAddress, ParseObject.createWithoutData("UserAddress", home));
//        usernew.put("homeAddress", object_id1);

        PRJFUNC.showProgress(AddAdressActivity.this, "");

        usernew.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                PRJFUNC.closeProgress();
                if (e != null) {
                    Log.e(" error "," home== "+e.getMessage());
                    e.printStackTrace();
                    return;
                }

                Log.e(" user "," home== "+usernew.get("homeAddress").toString());

                PRJFUNC.closeProgress();
                SaveAddress(home);
                goBack();
            }
        });
    }

    private void SaveAddress(String objectId) {
        SharedPreferences sharedpreferences = getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (Type_screen.equalsIgnoreCase("1")) {

//            Set<String> set = new HashSet<String>();
//            set.addAll(listAddress);
//            editor.putStringSet("home", set);
//            editor.commit();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listAddress.size(); i++) {
                sb.append(listAddress.get(i)).append(",,,");
            }
            editor.putString("home", sb.toString());
            editor.putString("object1", objectId);
            editor.commit();

        } else if (Type_screen.equalsIgnoreCase("2")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listAddress.size(); i++) {
                sb.append(listAddress.get(i)).append(",,,");
            }
            editor.putString("work", sb.toString());
            editor.putString("object2", objectId);
            editor.commit();

        } else if (Type_screen.equalsIgnoreCase("3")) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listAddress.size(); i++) {
                sb.append(listAddress.get(i)).append(",,,");
            }
            editor.putString("freq", sb.toString());
            editor.putString("object3", objectId);
            editor.commit();

        } else {

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


        goToWiFiItemDetailActivity(idxToAdd, true, indexx);


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
                Log.d("", wifiItem.m_szWiFiID + "----" + wifiItem.m_szTitle + "--wifiItem--" + wifiItem.m_szAddress);
            }
        }
    }

    public void goToWiFiItemDetailActivity(int p_nIdx, boolean p_bNew, int indexx) {
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
//                onTxtAddressChanged();
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


        m_lvAddressSuggestion1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onSuggestionItemClick(position);
            }
        });
        m_lvAddressSuggestion2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                onSuggestionItemClick2(position);
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


        hideKeyboard();
    }

    public void onTxtAddressChanged() {
        if (m_isTextUpdatedByUser == false) {
            m_isTextUpdatedByUser = true;
            return;
        }

        String szPrefix = et_wifi_address2.getText().toString();
        if (szPrefix.length() == 0) {
            addrMgr.clearSuggestionList();

            m_lvAddressSuggestion1.invalidateViews();
            return;
        }

//        m_lvAddressSuggestion1.setVisibility(View.VISIBLE);

        m_szUniqueTokenForAddressSuggestionRequest = PRJFUNC
                .generateRandomString();

        final String[] params = {"input", "sensor", "key"};
        final String[] values = {szPrefix, "true",
                getResources().getString(R.string.app_googleapi_places_key)};

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
//                    Toast.makeText(AddAdressActivity.this,
//                            "getAddressSuggestion Request Failed!",
//                            Toast.LENGTH_SHORT).show();
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

        String szPrefix = et_wifi_address3.getText().toString();
        if (szPrefix.length() == 0) {
            addrMgr2.clearSuggestionList();

            m_lvAddressSuggestion2.invalidateViews();
            return;
        }

//        m_lvAddressSuggestion2.setVisibility(View.VISIBLE);

        m_szUniqueTokenForAddressSuggestionRequest = PRJFUNC
                .generateRandomString();

        final String[] params = {"input", "sensor", "key"};
        final String[] values = {szPrefix, "true",
                getResources().getString(R.string.app_googleapi_places_key)};

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
//                    Toast.makeText(AddAdressActivity.this,
//                            "getAddressSuggestion Request Failed!",
//                            Toast.LENGTH_SHORT).show();
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

            return;
        }


        m_szUniqueTokenForAddressSuggestionRequest = PRJFUNC
                .generateRandomString();

        final String[] params = {"input", "sensor", "key"};
        final String[] values = {szPrefix, "true",
                getResources().getString(R.string.app_googleapi_places_key)};

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
//                    Toast.makeText(AddAdressActivity.this,
//                            "getAddressSuggestion Request Failed!",
//                            Toast.LENGTH_SHORT).show();
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


        int size = listAddress.size();
        if (size > indexx) {
            m_wifiItem.m_szAddress = listAddress.get(indexx);

            SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
            phoneDb.saveWiFiItemToStorage(indexx);
        }
        if (PRJFUNC.g_WiFiItems == null)
            return;


        //setResult(RESULT_OK);
        //finish();
        //overridePendingTransition(R.anim.hold, R.anim.right_out);
    }

    private void loadFrequentAddresses() {
        String home_str = sharedpreferences.getString("home", "");
        object_id1 = sharedpreferences.getString("object1", "");
        object_id2 = sharedpreferences.getString("object2", "");
        object_id3 = sharedpreferences.getString("object3", "");
        String work_str = sharedpreferences.getString("work", "");
        String frqk_str = sharedpreferences.getString("freq", "");
//        Set<String> setHome = sharedpreferences.getStringSet("home", null);
//        Set<String> setWork = sharedpreferences.getStringSet("work", null);
//        Set<String> setFrequent = sharedpreferences.getStringSet("frequent", null);

        List<String> list_frequent = null, list_home = null, list_work = null;
        String[] homelists = home_str.split(",,,");
        String[] worklists = work_str.split(",,,");
        String[] frqlists = frqk_str.split(",,,");

        switch (Type_screen) {
            case "1":
                getvalues(homelists);
                break;
            case "2":
                getvalues(worklists);
                break;
            case "3":
                getvalues(frqlists);
                break;
            default:
                break;
        }
        et_wifi_address1.setText(txt1);
        et_wifi_address2.setText(txt2);
        et_wifi_address3.setText(txt3);
        et_wifi_address4.setText(txt4);
        et_wifi_address5.setText(txt5);
        et_wifi_address6.setText(txt6);

    }

    private void getvalues(String[] homelists) {
        int size = homelists.length;

        switch (size) {
            case 1:
                txt1 = homelists[0];
                break;
            case 2:
                txt1 = homelists[0];
                txt2 = homelists[1];
                break;
            case 3:
                txt1 = homelists[0];
                txt2 = homelists[1];
                txt3 = homelists[2];
                break;
            case 4:
                txt1 = homelists[0];
                txt2 = homelists[1];
                txt3 = homelists[2];
                txt4 = homelists[3];
                break;
            case 5:
                txt1 = homelists[0];
                txt2 = homelists[1];
                txt3 = homelists[2];
                txt4 = homelists[3];
                txt5 = homelists[4];
                break;
            case 6:
                txt1 = homelists[0];
                txt2 = homelists[1];
                txt3 = homelists[2];
                txt4 = homelists[3];
                txt5 = homelists[4];
                txt6 = homelists[5];
                break;
            default:
                break;
        }
    }
//    private void loadFrequentAddresses()
//    {


//        if (PRJFUNC.g_WiFiItems == null)
//            return;
//
//        for (int i = 0; i < PRJFUNC.g_WiFiItems.length; i++) {
//            WiFiItemInfo wifiItem = PRJFUNC.g_WiFiItems[i];
//            if (wifiItem != null ) {
//                //m_adapterWifiItems.add(wifiItem);
//
//                if(i==0)
//                {
//                    listAddressDefaults.add(i, wifiItem.m_szAddress);
//
//                    et_wifi_address1.setText(wifiItem.m_szAddress);
//                }
//                else if(i==1)
//                {
//                    listAddressDefaults.add(i, wifiItem.m_szAddress);
//                    et_wifi_address2.setText(wifiItem.m_szAddress);
//                }
//                if(i==2)
//                {
//                    listAddressDefaults.add(i, wifiItem.m_szAddress);
//                    et_wifi_address3.setText(wifiItem.m_szAddress);
//                }
//                if(i==3)
//                {
//                    listAddressDefaults.add(i, wifiItem.m_szAddress);
//                    et_wifi_address4.setText(wifiItem.m_szAddress);
//                }
//                if(i==4)
//                {
//                    listAddressDefaults.add(i, wifiItem.m_szAddress);
//                    et_wifi_address5.setText(wifiItem.m_szAddress);
//                }
//                if(i==5)
//                {
//                    listAddressDefaults.add(i, wifiItem.m_szAddress);
//                    et_wifi_address6.setText(wifiItem.m_szAddress);
//                }
//                Log.d("",wifiItem.m_szWiFiID+"----"+wifiItem.m_szTitle+"--wifiItem--"+wifiItem.m_szAddress);
//            }
//        }

//    }
}

