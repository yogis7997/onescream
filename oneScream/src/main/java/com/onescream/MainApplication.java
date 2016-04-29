package com.onescream;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.parse.Parse;
import com.parse.PushService;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.onescream.engine.UniversalScreamEngine;
import com.onescream.service.OneScreamService;

//import com.segment.analytics.Analytics;
import com.segment.analytics.Analytics;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJCONST;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Application class for this application
 * <p/>
 * Created by Anwar Almojarkesh
 */

public class MainApplication extends Application {

    public static Context m_Context = null;
    private SharedPreferencesMgr m_pPhoneDb = null;

    @Override
    public void onCreate() {

        super.onCreate();
//		ACRA.init(this);
        m_Context = getApplicationContext();

        Analytics analytics = new Analytics.Builder(m_Context, getResources().getString(R.string.segment_key)).build();
        Analytics.setSingletonInstance(analytics);


        // ///////////////// ///////////
        m_pPhoneDb = new SharedPreferencesMgr(this);
        long first_time = m_pPhoneDb.getFirstRunTime();
        if (first_time == 0) {
            GlobalValues.sharedInstance()._firstRun = true;
        }

        long firstDateTime = m_pPhoneDb.getFirstDateTime();
        if (firstDateTime == 0) {
            Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            m_pPhoneDb.setFirstDateTime(cal.getTimeInMillis());
        }

        m_pPhoneDb.loadDetectedActInfo();
        m_pPhoneDb.loadSmsSettings();
        m_pPhoneDb.loadUserExtraInfo();

        m_pPhoneDb.loadWiFiItemsFromStorage();

        m_pPhoneDb.loadUnivThresholdDelta();

        Parse.initialize(this, "HdNdCY5iTsjBUYkLDFBDGMOzF2XddXunAzkzQGE1", "TvJIRwOymkvwIWtiBHnJGvS36x6TQ0w8cpU708sh");
        // }

        startService();
    }


    @Override
    public void onTerminate() {

        m_Context = null;
        super.onTerminate();
    }

    public void startService() {
        Intent intent = new Intent(getApplicationContext(), OneScreamService.class);
        startService(intent);
    }

    public SharedPreferencesMgr getSharedPreferencesMgrPoint() {
        return m_pPhoneDb;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
