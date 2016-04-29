package com.onescream;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import net.simonvt.menudrawer.MenuDrawer;
import net.simonvt.menudrawer.MenuDrawer.OnDrawerStateChangeListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Typeface;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onescream.Utils.Utility;
import com.onescream.engine.UniversalScreamEngine;
import com.onescream.intros.About;
import com.onescream.intros.FirstScreenActivity;
import com.onescream.intros.TourActivity;
import com.onescream.settings.ContactUsActivity;
import com.onescream.settings.FaqsActivity;
import com.onescream.settings.FoundersActivity;
import com.onescream.settings.HowitworksActivity;
import com.onescream.settings.PrivacyPolicyActivity;
import com.onescream.settings.TermsActivity;
import com.onescream.settings.SubscribeActivity;
import com.onescream.settings.WiFiItemsActivity;
import com.onescream.settings.YourDetailsActivity;
import com.parse.LogOutCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParsePush;
import com.parse.ParseUser;

import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.GPSTracker;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJCONST;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;


/**
 * Activity class for Home Screen
 * <p/>
 * Created by Anwar Almojarkesh
 */

@SuppressLint("ResourceAsColor")
public class HomeActivity extends Activity implements View.OnClickListener {

    private final String TAG = "_DefaultActivity";

    public static final String PARAM_NEED_SUBSCRIBE = "need_subscribe";

    private final int PAGE_ASK_TO_JOIN = 10001;
    private final int MSG_MOTION_EVENT = 10002;

    private float screen_rate = 2.0f;
    TextView menu1, menu2, menu3, menu4, menu5, menu6, menu7;
    ImageView menui1, menui2, menui3, menui4, menui5, menui6, menui7;


    /**
     * For Left Menu
     */
    private MenuDrawer mDrawer;

    private Context mContext;

    /**
     * Control for displaying detect status
     */
    private TextView m_tvTitle;
    private ImageView m_ivDetectStatus;
    private ImageView m_ivDetectStatus1;
    private TextView m_tvDetectStatus;

    /**
     * Detect Button Control
     */
    private ImageView m_ivDetectBtn;

    private boolean m_bNeedSubscribe;

    private boolean m_bWiFiPopuped = false;
    private Utility utility;
    Typeface facethin, facebold, faceRegular, EstiloRegular, sanfaceRegular, sanfacesemibold;

    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityTask.INSTANCE.add(this);
        utility = new Utility(this);
        utility.RegisterScreen(this, getResources().getString(R.string.standing_by));
        screen_rate = getResources().getDisplayMetrics().density;

        mDrawer = MenuDrawer.attach(this);
        mDrawer.setContentView(R.layout.activity_home);
        mDrawer.setMenuView(R.layout.layout_left_menu);
        int screen_width = getResources().getDisplayMetrics().widthPixels;
        mDrawer.setMenuSize((int) (screen_width * 0.70f));
        mDrawer.setTouchMode(MenuDrawer.TOUCH_MODE_FULLSCREEN);

        mContext = (Context) this;

        m_bNeedSubscribe = getIntent().getBooleanExtra(PARAM_NEED_SUBSCRIBE,
                false);

        initTypeFace();
        updateLCD();

        {
            ParseUser user = ParseUser.getCurrentUser();
            user.fetchInBackground();
            ParsePush.subscribeInBackground(PRJFUNC
                    .convertEmailToChannelStr(ParseUser.getCurrentUser()
                            .getEmail()));
            ParseAnalytics.trackAppOpenedInBackground(getIntent());
        }

        if (m_bNeedSubscribe) {
            //goToUpgradeActivity();
        }

        m_bWiFiPopuped = false;

        if (GlobalValues.sharedInstance().gpsTracker == null) {
            GlobalValues.sharedInstance().gpsTracker = new GPSTracker(mContext);
        }
        mDrawer.setOnDrawerStateChangeListener(new OnDrawerStateChangeListener() {

            @Override
            public void onDrawerStateChange(int oldState, int newState) {
                // TODO Auto-generated method stub

                setButtons();
                menu1.setTextColor(Color.WHITE);
                menui1.setVisibility(View.VISIBLE);
            }

            @Override
            public void onDrawerSlide(float openRatio, int offsetPixels) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void onPause() {
        stopMotionTimer();

        super.onPause();

    }

    @Override
    protected void onResume() {
        updateUIForDetectState();

        super.onResume();

        if (GlobalValues.sharedInstance().m_bDetecting) {
            startMotionTimer();
        }

        if (!m_bWiFiPopuped) {
            checkCurrentWIFIAndAsk();
        }
    }

    private void checkCurrentWIFIAndAsk() {
        final String strWiFiID = PRJFUNC.getCurrentWiFiSSID(this);

        if (PRJFUNC.isNullOrEmpty(strWiFiID))
            return;

        if (PRJFUNC.getWiFiItemIdxWithWifiID(strWiFiID) >= 0) {
            // It's already exist, so no need to ask to user to register it
            return;
        }

        // Check ignore WIFI
        for (int i = 0; i < GlobalValues.sharedInstance().m_arrayIgnoreWiFiIds
                .size(); i++) {
            if (GlobalValues.sharedInstance().m_arrayIgnoreWiFiIds.get(i)
                    .equals(strWiFiID)) {
                return;
            }
        }

        SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(
                HomeActivity.this);
        long firstDateTime = phoneDb.getFirstDateTime();
        long daysFromStart = ((new Date()).getTime() - firstDateTime)
                / (1000 * 3600 * 24);

        String strIgnoreKey = "ignore_" + strWiFiID;
        long currentTimestamp = ((new Date()).getTime()) / 1000;
        long ignoreTimestamp = phoneDb.getLong(strIgnoreKey);
        if (currentTimestamp - ignoreTimestamp < 3600 * 24) {
            return;
        }

        int nCount = 0;
        for (long i = daysFromStart; i > daysFromStart
                - PRJCONST.WIFI_CHECKING_PERIOD; i--) {
            String strKey = String.format("%d_%s", i, strWiFiID);

            if (phoneDb.getBoolean(strKey)) {
                nCount++;
            }
        }

        // connected everyday in 7 days
        if (nCount == PRJCONST.WIFI_CHECKING_PERIOD) {
            m_bWiFiPopuped = true;

//			PRJFUNC.showAlertDialog(
//					HomeActivity.this,
//					"Current connected WIFI has been regularly connected every day. Please register this.",
//					"Register", "Not now", new OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							goToWiFiItemsActivity();
//							GlobalValues.sharedInstance().m_arrayIgnoreWiFiIds
//									.add(strWiFiID);
//							m_bWiFiPopuped = false;
//						}
//					}, new OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							String strKey = "ignore_" + strWiFiID;
//							long notNowTimestamp = ((new Date()).getTime()) / 1000;
//							SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(
//									mContext);
//							phoneDb.setLong(strKey, notNowTimestamp);
//
//							m_bWiFiPopuped = false;
//						}
//					});
        }
    }

    @Override
    protected void onDestroy() {
        releaseValues();

        stopMotionTimer();

        ActivityTask.INSTANCE.remove(this);

        if (m_bmpMainImg != null) {
            m_bmpMainImg.recycle();
            m_bmpMainImg = null;
        }

        if (m_bmpAlpha != null) {
            m_bmpAlpha.recycle();
            m_bmpAlpha = null;
        }

        super.onDestroy();
    }

    private void releaseValues() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDrawer.isMenuVisible()) {
                mDrawer.closeMenu();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PAGE_ASK_TO_JOIN) {
            if (resultCode == RESULT_OK) {
                //goToUpgradeActivity();
            } else {
                switchDetecting();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // //////////////////////////////////////////////////
    private void updateLCD() {

        View _v = null;

        updateLCDForMenu();

        findViewById(R.id.iv_menu).setOnClickListener(this);

        m_tvTitle = (TextView) findViewById(R.id.tv_title);
        m_tvDetectStatus = (TextView) findViewById(R.id.tv_detect_status);
        //	TextView screen_title= (TextView) findViewById(R.id.screen_title1);
        // Detect Status control
        m_ivDetectStatus = (ImageView) findViewById(R.id.iv_detect_status);
        m_ivDetectStatus1 = (ImageView) findViewById(R.id.iv_detect_status1);

        // Detect Button
        m_ivDetectBtn = (ImageView) findViewById(R.id.iv_detect_btn);
        m_ivDetectBtn.setOnClickListener(this);

        //	screen_title.setTypeface(EstiloRegular);
        m_tvDetectStatus.setTypeface(sanfacesemibold);
        m_tvTitle.setTypeface(sanfaceRegular);


    }

    public void updateLCDForMenu() {
        //	TextView screen_title = (TextView) findViewById(R.id.screen_title);
        menu1 = (TextView) findViewById(R.id.menu1);
        menu2 = (TextView) findViewById(R.id.menu2);
        menu3 = (TextView) findViewById(R.id.menu3);
        menu4 = (TextView) findViewById(R.id.menu4);
        menu5 = (TextView) findViewById(R.id.menu5);
        menu6 = (TextView) findViewById(R.id.menu6);
        menu7 = (TextView) findViewById(R.id.menu7);

        menui1 = (ImageView) findViewById(R.id.menui1);
        menui2 = (ImageView) findViewById(R.id.menui2);
        menui3 = (ImageView) findViewById(R.id.menui3);
        menui4 = (ImageView) findViewById(R.id.menui4);
        menui5 = (ImageView) findViewById(R.id.menui5);
        menui6 = (ImageView) findViewById(R.id.menui6);
        menui7 = (ImageView) findViewById(R.id.menui7);


        //	screen_title.setTypeface(EstiloRegular);
        menu1.setTypeface(faceRegular);
        menu2.setTypeface(faceRegular);
        menu3.setTypeface(faceRegular);
        menu4.setTypeface(faceRegular);
        menu5.setTypeface(faceRegular);
        menu6.setTypeface(faceRegular);
        menu7.setTypeface(faceRegular);
        findViewById(R.id.frm_menu_your_details).setOnClickListener(this);
        findViewById(R.id.frm_menu_contact_us).setOnClickListener(this);
        findViewById(R.id.frm_menu_faq).setOnClickListener(this);
        findViewById(R.id.frm_menu_tour).setOnClickListener(this);
        findViewById(R.id.frm_menu_logout).setOnClickListener(this);
        findViewById(R.id.close_button).setOnClickListener(this);
        findViewById(R.id.frm_menu_one_scream).setOnClickListener(this);
        findViewById(R.id.frm_menu_about).setOnClickListener(this);


        findViewById(R.id.tv_menu_toc).setOnClickListener(this);
        ImageView tv_menu_terms = (ImageView) findViewById(R.id.tv_menu_toc);
        ImageView tv_menu_privacy_policy = (ImageView) findViewById(R.id.tv_menu_privacy_policy);
        //tv_menu_privacy_policy.setText("P R I V A C Y  P O L I C Y");
        tv_menu_privacy_policy.setOnClickListener(this);
        tv_menu_terms.setOnClickListener(this);
    }

    // /////////////////////////////////////

    /**
     * OnClick Event method
     */
    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {


            case R.id.iv_menu:
                mDrawer.openMenu();
                break;

            case R.id.iv_detect_btn:
                onDetectButton();
                break;


            // Left Menu
            case R.id.close_button:
                setButtons();
                menu1.setTextColor(Color.WHITE);
                menui1.setVisibility(View.VISIBLE);
                mDrawer.closeMenu();
                break;
            case R.id.frm_menu_one_scream:
                setButtons();
                menu1.setTextColor(Color.WHITE);
                menui1.setVisibility(View.VISIBLE);
                mDrawer.closeMenu();

                break;
            case R.id.frm_menu_your_details:
                setButtons();
                menu2.setTextColor(Color.WHITE);
                menui2.setVisibility(View.VISIBLE);
                goToYourDetailsActivity();
                break;

            case R.id.frm_menu_contact_us:
                setButtons();
                menu3.setTextColor(Color.WHITE);
                menui3.setVisibility(View.VISIBLE);
                goToContactUsActivity();
                break;

            case R.id.frm_menu_faq:
                setButtons();

                menu5.setTextColor(Color.WHITE);
                menui5.setVisibility(View.VISIBLE);
                goToFaqsActivity();
                break;

            case R.id.frm_menu_about:
                setButtons();

                menu7.setTextColor(Color.WHITE);
                menui7.setVisibility(View.VISIBLE);
                goToAboutActivity();
                break;

            case R.id.frm_menu_tour:
                setButtons();
                menu4.setTextColor(Color.WHITE);
                menui4.setVisibility(View.VISIBLE);
                goToTourScreenActivity(false, true);
                break;

            case R.id.tv_menu_toc:

                goToTermsActivity();
                break;

            case R.id.tv_menu_privacy_policy:
                goToPrivacyActivity();
                break;

            case R.id.frm_menu_logout:

                setButtons();
                menu6.setTextColor(Color.WHITE);
                menui6.setVisibility(View.VISIBLE);
                logout();
                break;
            default:
                break;
        }
    }

    public void goToHomeActivity() {
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    public void goToWiFiItemsActivity() {
        Intent intent = new Intent(HomeActivity.this, WiFiItemsActivity.class);
        intent.putExtra(WiFiItemsActivity.PARAM_DIRECTLY_OPEN_DETAIL, true);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    public void goToUpgradeActivity() {
        Intent intent = new Intent(HomeActivity.this, SubscribeActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    public void goToAskToJoinActivity() {
        Intent intent = new Intent(HomeActivity.this, AskToJoinActivity.class);
        startActivityForResult(intent, PAGE_ASK_TO_JOIN);
    }

    /**
     * Start/Pause Protection Button Clicked
     */
    public void onDetectButton() {

        ParseUser user = ParseUser.getCurrentUser();
        boolean strUserTypew = isUserExpired();
        Log.d("", "strUserTypew " + strUserTypew);
        String strUserType = user.getString("user_type");
        if (!GlobalValues.sharedInstance().m_bDetecting
                && (strUserType != null && strUserType
                .equals(PRJCONST.USER_TYPE_TRIAL))) {
            String strKey = String.format("%s_ask",
                    PRJFUNC.convertEmailToChannelStr(user.getEmail()));
            SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
            //	if (!phoneDb.getBoolean(strKey) || isUserExpired())

            if (isUserExpired()) {
                //goToAskToJoinActivity();
                showDialog();
                return;
            }
        }

        switchDetecting();
    }

    /**
     * Check if the user is expired
     */
    private boolean isUserExpired() {
        ParseUser user = ParseUser.getCurrentUser();
        Date date_expiry = user.getDate("expiry_date");
        Date now = new Date();

        if (date_expiry == null)
            date_expiry = now;

        long seconds = (date_expiry.getTime() - now.getTime()) / 1000;
        if (seconds >= 0)
            return false;

        return false;
    }

    private void switchDetecting() {

        GlobalValues.sharedInstance().m_bDetecting = !GlobalValues
                .sharedInstance().m_bDetecting;

        if (GlobalValues.sharedInstance()._soundEngine != null) {

            UniversalScreamEngine soundEngine = GlobalValues.sharedInstance()._soundEngine;
            if (soundEngine != null) {
                GlobalValues.sharedInstance()._soundEngine = null;
                soundEngine.Terminate();
            }
        }

        if (GlobalValues.sharedInstance().m_bDetecting) {

            GlobalValues.sharedInstance()._soundEngine = new UniversalScreamEngine();
            startMotionTimer();
        } else {

            stopMotionTimer();
        }

        updateUIForDetectState();

        SharedPreferencesMgr pPhoneDb = ((MainApplication) getApplication())
                .getSharedPreferencesMgrPoint();
        pPhoneDb.saveDetectionMode(GlobalValues.sharedInstance().m_bDetecting);

        if (GlobalValues.sharedInstance()._myService != null
                && GlobalValues.sharedInstance()._myService.m_handler != null) {
            if (GlobalValues.sharedInstance().m_bDetecting) {
                GlobalValues.sharedInstance()._myService.m_handler
                        .sendEmptyMessage(GlobalValues.COMMAND_ENABLE_DETECT);
            } else {
                GlobalValues.sharedInstance()._myService.m_handler
                        .sendEmptyMessage(GlobalValues.COMMAND_DISABLE_DETECT);
            }
        }
    }

    int m_nPaddingOfPlanet = 0;

    private void makeSmallPlanet() {
        m_nPaddingOfPlanet += 3;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) m_ivDetectStatus1.getLayoutParams();
        params.width = (int) ((310 - m_nPaddingOfPlanet * 2) * screen_rate);
        params.height = (int) ((310 - m_nPaddingOfPlanet * 2) * screen_rate);
        m_ivDetectStatus1.requestLayout();

        if (m_nPaddingOfPlanet >= 30) {
            m_ivDetectStatus1.setVisibility(View.INVISIBLE);
            params = (RelativeLayout.LayoutParams) m_ivDetectStatus1.getLayoutParams();
            params.width = (int) (310 * screen_rate);
            params.height = (int) (310 * screen_rate);
            m_ivDetectStatus1.requestLayout();
            m_nPaddingOfPlanet = 0;
        } else {

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    makeSmallPlanet();
                }
            }, 25);
        }
    }

    private void updateUIForDetectState() {
        if (GlobalValues.sharedInstance().m_bDetecting) {
            utility.RegisterScreen(this, getResources().getString(R.string.listening));
            m_tvTitle.setText("Not to Worry");
            m_tvDetectStatus.setText("You are being protected by One Scream");

            m_ivDetectBtn.setImageResource(R.drawable.ic_pause);
            // m_ivDetectStatus.setVisibility(View.VISIBLE);
            m_ivDetectStatus1.setVisibility(View.VISIBLE);
            makeSmallPlanet();

        } else {
            utility.RegisterScreen(this, getResources().getString(R.string.standing_by));
            m_tvTitle.setText("Standing By");
            m_tvDetectStatus.setText("Press play and we will listen");

            m_ivDetectBtn.setImageResource(R.drawable.ic_play);
            m_ivDetectStatus.setImageResource(R.drawable.ic_planet);
            // m_ivDetectStatus.setVisibility(View.INVISIBLE);
            m_ivDetectStatus1.setVisibility(View.VISIBLE);
        }
    }

    Bitmap m_bmpMainImg = null;
    Bitmap m_bmpAlpha = null;

    private void setDetectionImgWithGlow(int p_nGlowOuter) {
        // the glow color
        int glowColor = Color.rgb(0xed, 0xe6, 0xde);

        // The original image to use
        if (m_bmpMainImg == null)
            m_bmpMainImg = BitmapFactory.decodeResource(getResources(),
                    R.drawable.ic_moon_home);

        // extract the alpha from the source image
        if (m_bmpAlpha == null)
            m_bmpAlpha = m_bmpMainImg.extractAlpha();

        // The output bitmap (with the icon + glow)
        Bitmap bmp = Bitmap.createBitmap(m_bmpMainImg.getWidth() + p_nGlowOuter
                        * 2, m_bmpMainImg.getHeight() + p_nGlowOuter * 2,
                Bitmap.Config.ARGB_8888);

        // The canvas to paint on the image
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint();
        paint.setColor(glowColor);

        // outer glow
        paint.setMaskFilter(new BlurMaskFilter(p_nGlowOuter, Blur.OUTER));
        canvas.drawBitmap(m_bmpAlpha, p_nGlowOuter, p_nGlowOuter, paint);

        // original icon
        canvas.drawBitmap(m_bmpMainImg, p_nGlowOuter, p_nGlowOuter, null);

        m_ivDetectStatus.setImageBitmap(bmp);
    }

    // Left menu

    /**
     * Faqs
     */
    public void goToFaqsActivity() {

        Intent intent = new Intent(HomeActivity.this, FaqsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
        finish();
    }
    // Left menu

    /**
     * About
     */
    public void goToAboutActivity() {

        Intent intent = new Intent(HomeActivity.this, About.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
        finish();
    }

    /**
     * Contact us
     */
    public void goToContactUsActivity() {

        Intent intent = new Intent(HomeActivity.this, ContactUsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
        finish();
    }

    /**
     * Your details
     */
    public void goToYourDetailsActivity() {

        Intent intent = new Intent(HomeActivity.this, YourDetailsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
        finish();

    }

    /**
     * Log out
     */
    private void logout() {
//		PRJFUNC.showAlertDialog(HomeActivity.this, "Do you really log out?",
//				"Yes", "No", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						ParseUser user = ParseUser.getCurrentUser();
//						ParsePush.unsubscribeInBackground(PRJFUNC
//								.convertEmailToChannelStr(user.getEmail()));
//
//						ParseUser.logOutInBackground(new LogOutCallback() {
//							@Override
//							public void done(ParseException e) {
//								if (e != null) {
//									PRJFUNC.showAlertDialog(HomeActivity.this,
//											e.getMessage());
//									return;
//								}
//
//								if (GlobalValues.sharedInstance().m_bDetecting) {
//									switchDetecting();
//								}
//
//								goToFirstScreenActivity(true, false);
//							}
//						});
//					}
//				}, null);

        ParseUser user = ParseUser.getCurrentUser();
        ParsePush.unsubscribeInBackground(PRJFUNC
                .convertEmailToChannelStr(user.getEmail()));

        ParseUser.logOutInBackground(new LogOutCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    PRJFUNC.showAlertDialog(HomeActivity.this,
                            e.getMessage());
                    return;
                }

                if (GlobalValues.sharedInstance().m_bDetecting) {
                    switchDetecting();
                }

                goToFirstScreenActivity(true, false);
            }
        });

    }

    /**
     * Founders
     */
    public void goToFoundersActivity() {
        Intent intent = new Intent(HomeActivity.this, FoundersActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * Terms and Conditions
     */
    public void goToTermsActivity() {
        Intent intent = new Intent(HomeActivity.this, TermsActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * Privacy Policy
     */
    public void goToPrivacyActivity() {
        Intent intent = new Intent(HomeActivity.this,
                PrivacyPolicyActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * How it works
     */
    public void goToHowitworksActivity() {
        Intent intent = new Intent(HomeActivity.this, HowitworksActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
    }

    /**
     * To the first screen activity
     */
    public void goToFirstScreenActivity(boolean p_bOnlyFirstScreen,
                                        boolean p_bInTour) {
        Intent intent = new Intent(HomeActivity.this, FirstScreenActivity.class);
        intent.putExtra(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN,
                p_bOnlyFirstScreen);
        intent.putExtra(FirstScreenActivity.PARAM_IN_TOUR, p_bInTour);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
        if (p_bOnlyFirstScreen) {
            ActivityTask.INSTANCE.clear();
        }
        finish();
    }

    public void goToTourScreenActivity(boolean p_bOnlyFirstScreen,
                                       boolean p_bInTour) {
        Intent intent = new Intent(HomeActivity.this, TourActivity.class);
        intent.putExtra(FirstScreenActivity.PARAM_ONLY_FIRST_SCREEN,
                p_bOnlyFirstScreen);
        intent.putExtra(FirstScreenActivity.PARAM_IN_TOUR, p_bInTour);
        startActivity(intent);
        overridePendingTransition(R.anim.right_in, R.anim.hold);
        if (p_bOnlyFirstScreen) {
            ActivityTask.INSTANCE.clear();
        }
        finish();
    }

    private Timer mMotionTimer;
    private TimerTask mMotionTask = null;
    private int m_nTimeFrameIdx = 0;

    private void stopMotionTimer() {
        if (mMotionTimer != null) {
            mMotionTimer.cancel();
        }
    }

    private void startMotionTimer() {
        stopMotionTimer();

        m_nTimeFrameIdx = 0;

        mMotionTimer = new Timer();
        mMotionTask = new TimerTask() {
            @Override
            public void run() {
                m_nTimeFrameIdx++;

                if (m_nTimeFrameIdx >= 36000) {
                    m_nTimeFrameIdx = 0;
                }

                mHandler.sendEmptyMessage(MSG_MOTION_EVENT);
            }
        };
        mMotionTimer.schedule(mMotionTask, 0, 40);
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MOTION_EVENT:
                    drawShiningPlanet();
                    break;
                default:
                    break;
            }
        }
    };

    private void drawShiningPlanet() {
        if (!GlobalValues.sharedInstance().m_bDetecting || m_nPaddingOfPlanet > 0)
            return;

        int nFramePeriod = 40; // 40ms * 100
        int nGlowWidth = (m_nTimeFrameIdx * 2) % (nFramePeriod * 2);
        if (nGlowWidth > nFramePeriod) {
            nGlowWidth = (nFramePeriod * 2) - nGlowWidth;
        }
        setDetectionImgWithGlow(nGlowWidth + 1);
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
        sanfaceRegular = Typeface.createFromAsset(getAssets(),
                "fonts/SanFranciscoDisplay-Regular.otf");
        sanfacesemibold = Typeface.createFromAsset(getAssets(),
                "fonts/SanFranciscoDisplay-Semibold.otf");
    }

    private void setButtons() {
        menu1.setTextColor(Color.parseColor("#344251"));
        menu2.setTextColor(Color.parseColor("#344251"));
        menu3.setTextColor(Color.parseColor("#344251"));
        menu4.setTextColor(Color.parseColor("#344251"));
        menu5.setTextColor(Color.parseColor("#344251"));
        menu6.setTextColor(Color.parseColor("#344251"));
        menu7.setTextColor(Color.parseColor("#344251"));

        menui1.setVisibility(View.INVISIBLE);
        menui2.setVisibility(View.INVISIBLE);
        menui3.setVisibility(View.INVISIBLE);
        menui4.setVisibility(View.INVISIBLE);
        menui5.setVisibility(View.INVISIBLE);
        menui6.setVisibility(View.INVISIBLE);
        menui7.setVisibility(View.INVISIBLE);


    }

    private void showDialog() {
//		PRJFUNC.showAlertDialog(HomeActivity.this, "Would you like to subscribe now?",
//				"Yes please ", "Maybe later", new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//
//					}
//				}, null);
    }
}
