package com.onescream;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.location.Address;
import android.location.Location;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.onescream.service.OneScreamService;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.Calendar;
import java.util.List;

import com.google.android.gms.games.internal.GamesLog;
import com.onescream.engine.UniversalScreamEngine;
import com.onescream.service.PlayAudio;
import com.uc.prjcmn.ActivityTask;
import com.uc.prjcmn.GPSTracker;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJCONST;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Activity class for Detected Scream Screen
 * <p>
 * Created by Anwar Almojarkesh
 */

public class DetectedScreamActivity extends Activity implements
        View.OnClickListener {

    private final String TAG = "DetectedScreamActivity";

    public static final String PARAM_SOUND_TYPE = "sound_type";

    public static final int TOTAL_SOUND_TIME = 10000; // ms
    public static final long VIBRATE_DURATION = 700L;

    private Context mContext;

    /**
     * Display address to send
     */
    private TextView m_tvAddress, text_dont_call, tv_btn_false;

    /**
     * Display count down seconds
     */
    private TextView m_tvRemainedSeconds, screen_title, detected_text, topTextPolice;

    /**
     * Variable for vibration
     */
    private long m_nVibrationDuration = VIBRATE_DURATION;
    private long m_nVibrationSpace = 300L;

    private boolean m_bRinging;
    private Camera mCamera = null;

    private int m_nStep = 0;
    private int m_nOrgStreamVolume = 0;

    private String dateToShow;
    private String timeToShow;

    // ////////////////////////////////////////////////////////////
    // //////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detected);
        ActivityTask.INSTANCE.add(this);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        mContext = (Context) this;
        topTextPolice = (TextView) findViewById(R.id.top_text_police);
        text_dont_call = (TextView) findViewById(R.id.text_dont_call);
        //screen_title= (TextView)findViewById(R.id.screen_title);
        detected_text = (TextView) findViewById(R.id.detected_text);
        topTextPolice = (TextView) findViewById(R.id.top_text_police);
        tv_btn_false = (TextView) findViewById(R.id.tv_btn_false);
        updateLCD();

        GlobalValues.sharedInstance()._bEnabledActivity = true;

        SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
        phoneDb.loadUserInfo();
        phoneDb.loadUserExtraInfo();

        startAlarm();

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        m_nOrgStreamVolume = audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);

        if (GlobalValues.sharedInstance()._soundEngine != null) {
            GlobalValues.sharedInstance()._soundEngine.clearDetectedStatus();
        }

        processDetected();
    }

    private void processDetected() {

//		if (!PRJCONST.isTesting) {
        Intent intent = new Intent(this, PlayAudio.class);
        startService(intent);
//		}

        detectionTime();

        getAddress(true);

        countdownStart();

        try {
            final ParseObject notification = new ParseObject("userNotification");
            notification
                    .put("userId", GlobalValues.sharedInstance().m_stUserId);
            notification.put("userName",
                    GlobalValues.sharedInstance().m_stUserName);
            notification.put("soundType", "OneScream");
            notification.put("date", dateToShow);
            notification.put("time", timeToShow);
            notification.put("os", "Android");
            notification.put("status", "not yet");
            notification.saveInBackground(new SaveCallback() {

                @Override
                public void done(ParseException arg0) {
                    // TODO Auto-generated method stub
                    if (arg0 == null) {
                        GlobalValues.sharedInstance().soundObjectId = notification
                                .getObjectId();
                    }
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        stopService(new Intent(DetectedScreamActivity.this, OneScreamService.class));

    }

    @Override
    protected void onStop() {
        super.onStop();
        startService(new Intent(DetectedScreamActivity.this, OneScreamService.class));
    }

    @Override
    protected void onDestroy() {

        m_bGettingAddress = false;

        releaseValues();

        if (m_bRinging)
            stopAlarm();

        if (GlobalValues.sharedInstance()._soundEngine != null) {
            GlobalValues.sharedInstance()._soundEngine.clearDetectedStatus();
        }

        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                m_nOrgStreamVolume, 0);

        GlobalValues.sharedInstance()._bEnabledActivity = false;
        ActivityTask.INSTANCE.remove(this);
        Intent intent = new Intent(this, PlayAudio.class);
        stopService(intent);
        super.onDestroy();
    }

    private void releaseValues() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onCloseActivity();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    // //////////////////////////////////////////////////
    private void updateLCD() {
        //	m_tvAddress = (TextView) findViewById(R.id.tv_address);

        Typeface facethin = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Thin.ttf");
        Typeface facebold = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Bold.ttf");
        Typeface faceRegular = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Regular.ttf");
        Typeface EstiloRegular = Typeface.createFromAsset(getAssets(),
                "fonts/EstiloRegular.otf");

        m_tvRemainedSeconds = (TextView) findViewById(R.id.tv_remained_secs);
        tv_btn_false.setTypeface(faceRegular);
        text_dont_call.setTypeface(facethin);
        m_tvRemainedSeconds.setTypeface(faceRegular);
        //screen_title.setTypeface(EstiloRegular);
        detected_text.setTypeface(facebold);
        topTextPolice.setTypeface(faceRegular);

        // Confirm Button
        findViewById(R.id.frm_btn_confirm).setOnClickListener(this);

        // False Button
        findViewById(R.id.frm_btn_false).setOnClickListener(this);
    }

    // /////////////////////////////////////
    private void goMainActivity() {
        finish();
        overridePendingTransition(R.anim.hold, R.anim.right_out);
    }

    void onCloseActivity() {
        try {
            if (m_bRinging) {
                stopAlarm();

                ParseQuery<ParseObject> pQuery = ParseQuery
                        .getQuery("userNotification");
                pQuery.whereEqualTo("objectId",
                        GlobalValues.sharedInstance().soundObjectId);
                pQuery.findInBackground(new FindCallback<ParseObject>() {

                    @Override
                    public void done(List<ParseObject> arg0, ParseException arg1) {
                        if (arg1 == null) {
                            for (ParseObject object : arg0) {
                                object.put("status",
                                        GlobalValues.sharedInstance().setStatus);
                                object.saveInBackground();
                            }
                        } else {
                            Log.d(TAG,
                                    "Error while retrieving registration key :"
                                            + arg1.getMessage());
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (GlobalValues.sharedInstance()._myService != null
                && GlobalValues.sharedInstance()._myService.m_handler != null) {
            GlobalValues.sharedInstance()._myService.m_handler
                    .sendEmptyMessage(GlobalValues.COMMAND_REMOVE_NOTIFY);
        }

        {
            NotificationManager mNotificationManager;
            String ns = Context.NOTIFICATION_SERVICE;
            mNotificationManager = (NotificationManager) getSystemService(ns);
            mNotificationManager.cancelAll();

            GlobalValues.sharedInstance().m_bDetectionPhone = false;
        }

        goMainActivity();
    }

    Thread thread;
    CountDownTask1 countDown;

    private void countdownStart() {

        countDown = new CountDownTask1();
        countDown.execute();

    }

    class CountDownTask1 extends AsyncTask<Void, Integer, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = 12; i >= 0; i--) {
                try {
                    publishProgress(i);
                    Thread.sleep(1000);
                } catch (Exception e) {

                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            super.onProgressUpdate(values);
            String val = String.valueOf(values[0]);
            if (values[0] <= 9) {
                val = 0 + "" + String.valueOf(values[0]);
            }
            m_tvRemainedSeconds.setText(val);

        }

        @Override
        protected void onPostExecute(Void result) {

            if (GlobalValues.sharedInstance().m_strPhoneNumber.length() > 0) {

                if (GlobalValues.sharedInstance()._myService != null
                        && GlobalValues.sharedInstance()._myService.m_handler != null) {

                    if (!PRJFUNC.isNullOrEmpty(m_strAddress)) {
                        Message msg = new Message();
                        msg.what = GlobalValues.COMMAND_SEND_SMS;
                        msg.obj = m_strAddress;
                        GlobalValues.sharedInstance()._myService.m_handler
                                .sendMessage(msg);
                    } else {
                        GlobalValues.sharedInstance()._myService.m_handler
                                .sendEmptyMessage(GlobalValues.COMMAND_SEND_SMS_ADDRESS);
                    }

                    // GlobalValues._myService.m_handler
                    // .sendEmptyMessage(GlobalValues.COMMAND_PHONE_CALL);
                }

            }


            topTextPolice.setText("We are communicating with the Police");
            text_dont_call.setText("DO NOT MAKE A CALL");
            text_dont_call.setVisibility(View.VISIBLE);

            m_tvRemainedSeconds.setVisibility(View.GONE);
            findViewById(R.id.frm_btn_false).setVisibility(View.GONE);
            //stopAlarm();

            //	onCloseActivity();
            callMessage();
            super.onPostExecute(result);
        }
    }

    private void callMessage() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                stopAlarm();

                onCloseActivity();
            }
        }, 3000);
    }

    private void detectionTime() {

        Calendar cal = Calendar.getInstance();

        int year = cal.get(Calendar.YEAR);
        String monthName = getMonthName(cal.get(Calendar.MONTH));
        String dat = String.format("%02d", cal.get(Calendar.DATE));

        String hour = String.format("%02d", cal.get(Calendar.HOUR_OF_DAY));
        String min = String.format("%02d", cal.get(Calendar.MINUTE));

        dateToShow = dat + "/" + monthName + "/" + year;
        timeToShow = hour + ":" + min;

    }

    private String getMonthName(int month) {
        String[] STR_MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        return STR_MONTHS[month];
    }

    private void startAlarm() {
        if (GlobalValues.sharedInstance()._myService != null) {
        }

        m_nStep = 0;
        m_bRinging = true;
        // Camera Flash
        if (GlobalValues.sharedInstance().m_bNotifyFlash) {
            if (mCamera == null) {
                try {
                    mCamera = Camera.open();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG," error2== "+e.getMessage());
                }

            }
            flashing();
        }

        // Vibration
        if (GlobalValues.sharedInstance().m_bNotifyVibrate) {
            vibrating(true);
        }
    }

    private void stopAlarm() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();

        if (!PRJCONST.isTesting) {
            Intent intent = new Intent(this, PlayAudio.class);
            stopService(intent);
        }

        m_bRinging = false;

    }

    private void vibrating(boolean immediately) {
        if (!m_bRinging)
            return;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!m_bRinging)
                    return;

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(m_nVibrationDuration);

                vibrating(false);
            }
        }, immediately ? 100 : m_nVibrationDuration + m_nVibrationSpace);
    }

    private void flashing() {
        m_nStep++;

        if (m_nStep * UniversalScreamEngine.FLASH_DURATION > TOTAL_SOUND_TIME) {
            stopAlarm();

        }

        if (!m_bRinging)
            return;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!m_bRinging)
                    return;

                if (mCamera != null) {
                    try {
                        Camera.Parameters mParameters = mCamera.getParameters();
                        if (m_nStep % 2 == 1) {
                            mParameters
                                    .setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            mCamera.setParameters(mParameters);
                        } else {
                            mParameters
                                    .setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCamera.setParameters(mParameters);
                        }
                    } catch (Exception e) {

                        Log.e(TAG," error1== "+e.getMessage());
                        e.printStackTrace();
                    }
                    flashing();
                }
            }
        }, UniversalScreamEngine.FLASH_DURATION);
    }

    /**
     * OnClick Event method
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frm_btn_false:
                onFalseBtn();
                break;

            case R.id.frm_btn_confirm:
                onConfirmBtn();
                break;
            default:
                break;
        }
    }

    private void onConfirmBtn() {
        GlobalValues.sharedInstance().setStatus = "Confirmed";
        if (countDown != null)
            countDown.cancel(true);
        onCloseActivity();
    }

    private void onFalseBtn() {
        GlobalValues.sharedInstance().setStatus = "False";

        UniversalScreamEngine.UNIVERSAL_THRESHOLD_DELTA += UniversalScreamEngine.FALSE_DETECTION_CORRECTION;
        SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(mContext);
        phoneDb.saveUnivThresholdDelta();

        if (countDown != null)
            countDown.cancel(true);
        stopAlarm();
        onCloseActivity();
    }

    int nMiliSecs = 0; // 10s

    private String m_strAddress = "";
    private boolean m_bGettingAddress = false;

    private void getAddress(boolean bFirst) {
        if (bFirst) {
            nMiliSecs = 0;
            m_bGettingAddress = true;
            if (GlobalValues.sharedInstance().gpsTracker == null) {
                GlobalValues.sharedInstance().gpsTracker = new GPSTracker(
                        mContext);
            }
        } else if (GlobalValues.sharedInstance().gpsTracker.canGetLocation() == false) {
            m_bGettingAddress = false;
            return;
        }

        GPSTracker tracker = GlobalValues.sharedInstance().gpsTracker;

        if (!m_bGettingAddress)
            return;

        try {
            Location location = tracker.getLocation();

            String myAddress = "";

            String szCurWifiId = PRJFUNC.getCurrentWiFiSSID(this);
            if (szCurWifiId != null && !szCurWifiId.isEmpty()) {
                int idx = PRJFUNC.getWiFiItemIdxWithWifiID(szCurWifiId);
                if (idx >= 0) {
                    myAddress = PRJFUNC.g_WiFiItems[idx].m_szAddress;
                }
            }

            double latitude = 0;
            double longitude = 0;
            Log.e("Location", "get location");
            if (location != null) {
                Log.e("Location", "location = " + location.getLongitude());

                latitude = tracker.getLatitude();
                longitude = tracker.getLongitude();

                if (myAddress.isEmpty()) {
                    List<Address> addresses = tracker
                            .getGeocoderAddress(mContext);

                    if (addresses != null) {
                        Address returnedAddress = addresses.get(0);
                        StringBuilder strReturnedAddress = new StringBuilder("");
                        for (int i = 0; i < returnedAddress
                                .getMaxAddressLineIndex(); i++) {
                            strReturnedAddress.append(
                                    returnedAddress.getAddressLine(i)).append(
                                    " ");
                        }

                        myAddress = strReturnedAddress.toString();
                    }
                }
            }

            if (!myAddress.isEmpty()) {
                SharedPreferencesMgr phoneDb = ((MainApplication) getApplication())
                        .getSharedPreferencesMgrPoint();
                if (PRJFUNC
                        .isNullOrEmpty(GlobalValues.sharedInstance().m_strFirstName)
                        && PRJFUNC
                        .isNullOrEmpty(GlobalValues.sharedInstance().m_strLastName)) {

                    phoneDb.loadUserExtraInfo();
                }
                phoneDb.loadSmsSettings();

                String strName = GlobalValues.sharedInstance().m_strFirstName
                        + " " + GlobalValues.sharedInstance().m_strLastName;

                String strText = strName + " has screamed at ";

                if (GlobalValues.sharedInstance().m_bSmsMapLink) {
                    // strText += String.format("(%.4f,  %.4f)",
                    // latitude,longitude);
                    // strText += String
                    // .format(" http://maps.google.com/maps?z=16&t=h&q=loc:%.6f+%.6f",
                    // latitude, longitude);

                    strText += myAddress;
                }

                if (GlobalValues.sharedInstance().m_bSmsFullAddress) {
                    strText += "\n " + myAddress;
                }

                if (strText.length() >= 200)
                    strText = strText.substring(0, 200);

                m_strAddress = strText;
                //m_tvAddress.setText(m_strAddress);

                final String strLocation = String.format("(%.4f,  %.4f)",
                        latitude, longitude);
                final String strAddress = myAddress;

                if (!PRJFUNC
                        .isNullOrEmpty(GlobalValues.sharedInstance().m_strDetectedObjectId)) {
                    ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(
                            "detection_history");
                    query.whereEqualTo("objectId",
                            GlobalValues.sharedInstance().m_strDetectedObjectId);

                    query.findInBackground(new FindCallback<ParseObject>() {

                        @Override
                        public void done(List<ParseObject> objects,
                                         ParseException arg1) {
                            if (objects != null && objects.size() > 0) {
                                ParseObject detect = objects.get(0);
                                detect.put("address", strAddress);
                                detect.put("location", strLocation);
                                detect.saveInBackground();

                                GlobalValues.sharedInstance().m_strDetectedObjectId = null;
                            }
                        }
                    });
                } else {
                    GlobalValues.sharedInstance().m_strDetectedAddress = strAddress;
                    GlobalValues.sharedInstance().m_strDetectedLocation = strLocation;
                }

                return;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        nMiliSecs += 1000;
        if (nMiliSecs < 10000) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    getAddress(false);
                }
            }, 1000);
        }
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
            Log.d(TAG, "releaseCamera -- done");
        }
        super.onPause();
    }

//    @Override
//    protected void onResume() {
//        // TODO Auto-generated method stub
//        if (mCamera != null) {
//            try {
//                Camera.open();
//                Log.d(TAG, "openCamera -- done");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//        super.onResume();
//    }
}
