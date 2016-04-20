package com.uc.prjcmn;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.onescream.R;
import com.onescream.info.WiFiItemInfo;
import com.parse.ParsePush;

/**
 * This class includes general functions used in application
 *
 * Created by Anwar Almojarkesh
 *
 */
public class PRJFUNC {
	public static String TAG = "_PRJFUNC";
	public static boolean DEFAULT_SCREEN = true;

	public static int DETECTED_NUMBER = 1;
	public static int DETECTED_TYPE = 1;

	public static int W_LCD = 0;
	public static int H_LCD = 0;
	public static int H_STATUSBAR = 0;
	public static int DPI = 0; // density dpi

	public static GPSTracker gpsTracker;

	private static Typeface mFace_FONT_OpenSans = null;
	private static Typeface mFace_FONT_OpenSansBold = null;

	public static String g_strDeviceID = null;

	public static WiFiItemInfo[] g_WiFiItems = null;

	public enum Signal {
		NONE, ONE_SCREAM
	}

	public static final String[] SIGNAL_NAMES = { "One Scream", };

	public static String getDeviceID(Context pContext) {
		if (PRJFUNC.g_strDeviceID == null)
			PRJFUNC.g_strDeviceID = Secure.getString(
					pContext.getContentResolver(), Secure.ANDROID_ID);
		return PRJFUNC.g_strDeviceID;
	}

	public static Typeface getFont(Context context, String font) {
		Typeface typeFont = null;
		if (font.equals(PRJCONST.FONT_OpenSansBold)) {
			if (mFace_FONT_OpenSansBold == null) {
				mFace_FONT_OpenSansBold = Typeface.createFromAsset(
						context.getAssets(), font);
			}
			typeFont = mFace_FONT_OpenSansBold;
		} else if (font.equals(PRJCONST.FONT_OpenSans)) {
			if (mFace_FONT_OpenSans == null) {
				mFace_FONT_OpenSans = Typeface.createFromAsset(
						context.getAssets(), font);
			}
			typeFont = mFace_FONT_OpenSans;
		}
		return typeFont;
	}

	public static void setTextViewFont(Context context, TextView tv, String font) {
		if (tv == null || context == null) {
			return;
		}
		// if (mGrp == null)
		// resetGraphValue(context);

		Typeface typeFont = getFont(context, font);
		if (typeFont != null)
			tv.setTypeface(typeFont);
	}

	public static void setTextViewFont(Context context, CheckBox chkBox,
			String font) {
		if (chkBox == null || context == null) {
			return;
		}
		// if (mGrp == null)
		// resetGraphValue(context);

		Typeface typeFont = getFont(context, font);
		if (typeFont != null)
			chkBox.setTypeface(typeFont);
	}

	public static int ConvSignalToInt(Signal p_signal) {
		int ret = -1;
		if (p_signal == Signal.ONE_SCREAM) {
			ret = 0;
		}

		return ret;
	}

	public static Signal ConvIntToSignal(int p_number) {
		Signal ret = Signal.NONE;
		if (p_number == 0) {
			ret = Signal.ONE_SCREAM;
		}
		return ret;
	}

	public static void deleteFile(File file, boolean bExcpetDir) {
		if (file.isDirectory()) {
			String[] miniChildren = file.list();
			for (int j = 0; j < miniChildren.length; j++) {
				File child = new File(file, miniChildren[j]);
				deleteFile(child, false);
			}
		}

		if (!bExcpetDir)
			file.delete();
	}

	public static void sendPushMessage(Context context, Signal signal) {
		// Push Notification

		JSONObject data2 = null;
		try {
			String strMessage = PRJFUNC.getDeviceID(context) + ","
					+ ConvSignalToInt(signal) + "," + PRJFUNC.DETECTED_NUMBER;
			String strName = GlobalValues.sharedInstance().m_strFirstName + " "
					+ GlobalValues.sharedInstance().m_strLastName;
			data2 = new JSONObject(
					"{\"action\": \"android.intent.action.PUSH_STATE\", \"message\": \""
							+ strMessage + "\", \"alert\": \"" + signal
							+ "- From " + strName + "\"}");

			ParsePush push = new ParsePush();

			push.setChannel(PRJFUNC.convertEmailToChannelStr(GlobalValues
					.sharedInstance().m_strEmail));

			push.setData(data2);
			push.sendInBackground();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static boolean copyFileFromAsset(String p_strAssetPath,
			String p_strDstDir, String p_strFileName, Context p_context) {
		try {

			InputStream is = p_context.getAssets().open(p_strAssetPath);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();

			File dir = new File(p_strDstDir);
			if (!dir.exists())
				dir.mkdirs();

			File f = new File(p_strDstDir + "/" + p_strFileName);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(buffer);
			fos.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static String convertEmailToChannelStr(String strEmail) {
		if (strEmail == null || strEmail.isEmpty())
			return strEmail;
		String str = strEmail.replace('.', '-');
		str = str.replace('@', '-');
		return str;
	}

	private static Dialog m_dlgProgress;
	private static Context m_dlgProgressContext;

	public static void showProgress(Context context, String strMsg) {
		if (m_dlgProgress != null) {
			return;
		}
		if (strMsg == null) {
			strMsg = "";
		}
		closeProgress(null);
		try {
			m_dlgProgressContext = context;
			m_dlgProgress = ProgressDialog.show(context, null, strMsg, true,
					false);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeProgress() {
		closeProgress(null);
	}

	public static void closeProgress(Context p_context) {
		if (m_dlgProgress == null) {
			return;
		}

		if (p_context != null && m_dlgProgressContext != p_context)
			return;

		m_dlgProgressContext = null;
		m_dlgProgress.dismiss();
		m_dlgProgress = null;

	}

	public static void showAlertDialog(Activity p_activity, String p_strMessage) {
		AlertDialog.Builder alert = new AlertDialog.Builder(p_activity);
		alert.setPositiveButton(p_activity.getString(R.string.word_ok), null);
		alert.setMessage(p_strMessage);
		alert.show();
	}

	public static void showAlertDialog(Activity p_activity,
			String p_strMessage, String p_strYesBtn, String p_strNoBtn,
			DialogInterface.OnClickListener p_listenerYes,
			DialogInterface.OnClickListener p_listenerNo) {
		AlertDialog.Builder alert = new AlertDialog.Builder(p_activity);
		if (p_strYesBtn != null)
			alert.setPositiveButton(p_strYesBtn, p_listenerYes);
		if (p_strNoBtn != null)
			alert.setNegativeButton(p_strNoBtn, p_listenerNo);
		alert.setMessage(p_strMessage);
		alert.setCancelable(false);
		alert.show();
	}

	public static boolean isNullOrEmpty(String p_str) {
		return p_str == null || p_str.isEmpty();
	}

	// ///////////////////////////////////////////////////////////////////
	// ////////////////// Wi-Fi implementation ///////////////////////////
	// ///////////////////////////////////////////////////////////////////

	/*
	 * Get WiFi Item from its SSID
	 */
	public static int getWiFiItemIdxWithWifiID(String p_ssid) {
		if (p_ssid == null || p_ssid.isEmpty())
			return -1;

		if (g_WiFiItems == null)
			return -1;

		try {
			for (int i = 0; i < g_WiFiItems.length; i++) {
				if (g_WiFiItems[i].m_szWiFiID.equals(p_ssid))
					return i;
			}
		} catch (Exception e) {
		}
		return -1;
	}

	/*
	 * Get WiFi Item from its title
	 */
	public static int getWiFiItemIdxWithTitle(String p_title) {
		if (p_title == null || p_title.isEmpty())
			return -1;

		if (g_WiFiItems == null)
			return -1;

		try {
			for (int i = 0; i < g_WiFiItems.length; i++) {
				if (g_WiFiItems[i].m_szTitle.equals(p_title))
					return i;
			}
		} catch (Exception e) {
		}
		return -1;
	}
	/*
	 * Get Current WiFi SSID of device
	 */
	public static String getCurrentWiFiSSID(Context context) {
		String ssid = null;
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo.isConnected()) {
			final WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
			if (connectionInfo != null
					&& !TextUtils.isEmpty(connectionInfo.getBSSID())) {
				ssid = connectionInfo.getBSSID();
			}
		}
		return ssid;
	}

	/**
	 * Hide Keyboard in Activity
	 */
	public static void hideKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(), 0);
	}

	/***********************************************
	 * Get History Directory and File Name Functions
	 ***********************************************/
	public static String getHistorySoundDir(Context p_context) {
		return p_context.getFilesDir().getPath() + "/History";
	}

	public static String getCurHistorySoundFileName() {
		return (new Date()).getTime() + ".wav";
	}
	
    public static String generateRandomString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }

}
