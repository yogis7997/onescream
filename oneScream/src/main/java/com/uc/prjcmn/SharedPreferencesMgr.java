package com.uc.prjcmn;

import com.onescream.engine.UniversalScreamEngine;
import com.onescream.info.WiFiItemInfo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


 

/**
 * The class for storing and loading the specific values
 *
 * Created by Anwar Almojarkesh
 *
 */

public class SharedPreferencesMgr {

    private SharedPreferences pref = null;

    private final String DB_NAME = "com.ugs.onescream1";

    private final String F_FIRSTRUN = "f_first_runtime";
    private final String F_FIRST_DATE = "f_first_datetime";
    private final String F_FIRST_HOME_SCREEN = "f_first_home_screen";

    // -- screen info
    private final String F_DPI = "scr_dpi";
    private final String F_W_LCD = "scr_wlcd";
    private final String F_H_LCD = "scr_hlcd";
    private final String F_X_Z = "scr_xz";
    private final String F_Y_Z = "scr_yz";
    private final String F_DEFAULT_FONT_SIZE = "scr_fs";
    private final String F_IS_DEFAULT = "scr_default";


    private final String F_IS_LOGGED_IN_ONCE = "logged_in_once";
    private final String F_IS_VIBRATE = "is_notify_vibrate";
    private final String F_IS_CAMERA_FLASH = "is_notify_flash";
    private final String F_IS_PEBBLE_WATCH = "is_notify_pebblewatch";
    private final String F_IS_ANDROID_WEAR = "is_notify_androidwear";

    private final String F_SMS_MAP_LINK = "sms_map_link";
    private final String F_SMS_FULL_ADDRESS = "sms_full_address";

    private final String F_DETECT_MODE = "is_detecting";

    private final String F_USERID = "userId";
    private final String F_USERNAME = "username";
    private final String F_PASSWORD = "password";

    private final String F_EMAIL = "email";
    private final String F_FIRST_NAME = "firstname";
    private final String F_LAST_NAME = "lastname";
    private final String F_PHONE = "phone";
    private final String F_POSTCODE = "postcode";
    private final String F_ADDRESS = "address";

    private final String F_UNIVERSAL_ENGINE_THRESHOLD_DELTA = "univ_threshold_delta";

    private final String F_WIFI_TITLE = "wifi_title_";
    private final String F_WIFI_ID = "wifi_id_";
    private final String F_WIFI_ADDRESS = "wifi_address_";

    // ==========================================
    public SharedPreferencesMgr(Context context) {

        pref = context.getSharedPreferences(DB_NAME, Activity.MODE_PRIVATE);

    }

    public void clearSharedPreferences() {
        if (pref != null) {
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
        }
    }

    public void setFirstRunTime(long first_time) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(F_FIRSTRUN, first_time);
        editor.commit();

    }

    public long getFirstRunTime() {
        return pref.getLong(F_FIRSTRUN, 0);
    }
    
    
    public void setFirstDateTime(long first_time) {

        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(F_FIRST_DATE, first_time);
        editor.commit();

    }

    public long getFirstDateTime() {
        return pref.getLong(F_FIRST_DATE, 0);
    }

    public void setFirstHomeScreen() {

        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(F_FIRST_HOME_SCREEN, false);
        editor.commit();

    }

    public boolean isFirstHomeScreen() {
        return pref.getBoolean(F_FIRST_HOME_SCREEN, true);
    }

    public void setScreenInfo() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(F_DPI, PRJFUNC.DPI);
        editor.putInt(F_W_LCD, PRJFUNC.W_LCD);
        editor.putInt(F_H_LCD, PRJFUNC.H_LCD);
        editor.putFloat(F_X_Z, RS.X_Z);
        editor.putFloat(F_Y_Z, RS.Y_Z);
        editor.putFloat(F_DEFAULT_FONT_SIZE, RS.DEFAULT_FONTSIZE);
        editor.putBoolean(F_IS_DEFAULT, PRJFUNC.DEFAULT_SCREEN);
        editor.commit();
    }

    public void loadScreenInfo() {
        PRJFUNC.DPI = pref.getInt(F_DPI, PRJCONST.SCREEN_DPI);
        PRJFUNC.W_LCD = pref.getInt(F_W_LCD, PRJCONST.SCREEN_WIDTH);
        PRJFUNC.H_LCD = pref.getInt(F_H_LCD, PRJCONST.SCREEN_HEIGHT);
        RS.X_Z = pref.getFloat(F_X_Z, 1);
        RS.Y_Z = pref.getFloat(F_Y_Z, 1);
        RS.DEFAULT_FONTSIZE = pref.getFloat(F_DEFAULT_FONT_SIZE,
                RS.DEFAULT_FONTSIZE);
        PRJFUNC.DEFAULT_SCREEN = pref.getBoolean(F_IS_DEFAULT, true);
    }

    public void saveSmsSettings() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(F_SMS_MAP_LINK, GlobalValues.sharedInstance().m_bSmsMapLink);
        editor.putBoolean(F_SMS_FULL_ADDRESS, GlobalValues.sharedInstance().m_bSmsFullAddress);
        editor.commit();
    }

    public void loadSmsSettings() {
        GlobalValues.sharedInstance().m_bSmsMapLink = pref.getBoolean(F_SMS_MAP_LINK, true);
        GlobalValues.sharedInstance().m_bSmsFullAddress = pref.getBoolean(F_SMS_FULL_ADDRESS,
                false);
    }

    public void saveDetectedActInfo() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(F_IS_VIBRATE, GlobalValues.sharedInstance().m_bNotifyVibrate);
        editor.putBoolean(F_IS_CAMERA_FLASH, GlobalValues.sharedInstance().m_bNotifyFlash);
        editor.commit();
    }

    public void loadDetectedActInfo() {
        GlobalValues.sharedInstance().m_bNotifyVibrate = pref.getBoolean(F_IS_VIBRATE, true);
        GlobalValues.sharedInstance().m_bNotifyFlash = pref.getBoolean(F_IS_CAMERA_FLASH, true);
    }


    public void saveDetectionMode(boolean bDetecting) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(F_DETECT_MODE, bDetecting);
        editor.commit();
    }

    public boolean IsDetectionMode() {
        return pref.getBoolean(F_DETECT_MODE, false);
    }

    public void saveUserInfo() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(F_USERID, GlobalValues.sharedInstance().m_stUserId);
        editor.putString(F_USERNAME, GlobalValues.sharedInstance().m_stUserName);
        editor.putString(F_PASSWORD, GlobalValues.sharedInstance().m_stPassword);
        editor.commit();
    }

    public void loadUserInfo() {
        GlobalValues.sharedInstance().m_stUserId = pref.getString(F_USERID, "");
        GlobalValues.sharedInstance().m_stUserName = pref.getString(F_USERNAME, "");
        GlobalValues.sharedInstance().m_stPassword = pref.getString(F_PASSWORD, "");
    }

    public void saveUserExtraInfo() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(F_EMAIL, GlobalValues.sharedInstance().m_strEmail);
        editor.putString(F_FIRST_NAME, GlobalValues.sharedInstance().m_strFirstName);
        editor.putString(F_LAST_NAME, GlobalValues.sharedInstance().m_strLastName);
        editor.putString(F_PHONE, GlobalValues.sharedInstance().m_strPhoneNumber);
        editor.putString(F_POSTCODE, GlobalValues.sharedInstance().m_strPostCode);
        editor.putString(F_ADDRESS, GlobalValues.sharedInstance().m_strAddress);
        editor.commit();
    }

    public void loadUserExtraInfo() {
        GlobalValues.sharedInstance().m_strEmail = pref.getString(F_EMAIL, "");
        GlobalValues.sharedInstance().m_strFirstName = pref.getString(F_FIRST_NAME, "");
        GlobalValues.sharedInstance().m_strLastName = pref.getString(F_LAST_NAME, "");
        GlobalValues.sharedInstance().m_strPhoneNumber = pref.getString(F_PHONE, "");
        GlobalValues.sharedInstance().m_strPostCode = pref.getString(F_POSTCODE, "");
        GlobalValues.sharedInstance().m_strAddress = pref.getString(F_ADDRESS, "");
    }

    public void loadUnivThresholdDelta() {

        UniversalScreamEngine.UNIVERSAL_THRESHOLD_DELTA = pref.getFloat(
                F_UNIVERSAL_ENGINE_THRESHOLD_DELTA,
                UniversalScreamEngine.UNIVERSAL_THRESHOLD_DELTA);
    }

    public void saveUnivThresholdDelta() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(F_UNIVERSAL_ENGINE_THRESHOLD_DELTA,
                UniversalScreamEngine.UNIVERSAL_THRESHOLD_DELTA);
        editor.commit();

    }

    public void loadWiFiItemsFromStorage() {
        PRJFUNC.g_WiFiItems = new WiFiItemInfo[PRJCONST.MAX_WIFI_ITEM_INFO];

        for (int i = 0; i < PRJFUNC.g_WiFiItems.length; i++) {
            PRJFUNC.g_WiFiItems[i] = new WiFiItemInfo();

            PRJFUNC.g_WiFiItems[i].m_szTitle = pref.getString(F_WIFI_TITLE + i,
                    "");
            PRJFUNC.g_WiFiItems[i].m_szAddress = pref.getString(F_WIFI_ADDRESS
                    + i, "");
            PRJFUNC.g_WiFiItems[i].m_szWiFiID = pref.getString(F_WIFI_ID + i,
                    "");
        }
    }

    public void saveWiFiItemToStorage(int p_idx) {
        if (PRJFUNC.g_WiFiItems == null || PRJFUNC.g_WiFiItems[p_idx] == null)
            return;

        SharedPreferences.Editor editor = pref.edit();
        editor.putString(F_WIFI_TITLE + p_idx,
                PRJFUNC.g_WiFiItems[p_idx].m_szTitle);
        editor.putString(F_WIFI_ADDRESS + p_idx,
                PRJFUNC.g_WiFiItems[p_idx].m_szAddress);
        editor.putString(F_WIFI_ID + p_idx,
                PRJFUNC.g_WiFiItems[p_idx].m_szWiFiID);

        editor.commit();
    }

    public boolean isLoggedInOnce() {
        return pref.getBoolean(F_IS_LOGGED_IN_ONCE, false);
    }

    public void setLoggedOnce() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(F_IS_LOGGED_IN_ONCE, true);
        editor.commit();
    }

    /**  The methods for User's Storage Fields */
    public void setBoolean(String p_key, boolean p_bVal) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(p_key, p_bVal);
        editor.commit();
    }

    public boolean getBoolean(String p_key) {
        return pref.getBoolean(p_key, false);
    }

    public void setInteger(String p_key, int p_nVal) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(p_key, p_nVal);
        editor.commit();
    }

    public int getInteger(String p_key) {
        return pref.getInt(p_key, 0);
    }
    
    public void setLong(String p_key, long p_nVal) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(p_key, p_nVal);
        editor.commit();
    }

    public long getLong(String p_key) {
        return pref.getLong(p_key, 0);
    }

    public void setString(String p_key, String p_strVal) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(p_key, p_strVal);
        editor.commit();
    }

    public String getString(String p_key) {
        return pref.getString(p_key, null);
    }

}
