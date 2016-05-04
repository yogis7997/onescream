package com.onescream.service;

import android.Manifest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import com.onescream.DetectedScreamActivity;
import com.onescream.HomeActivity;
import com.onescream.MainApplication;
import com.onescream.R;
import com.onescream._SplashActivity;
import com.onescream.engine.UniversalScreamEngine;
import com.uc.prjcmn.GPSTracker;
import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJCONST;
import com.uc.prjcmn.PRJFUNC;
import com.uc.prjcmn.SharedPreferencesMgr;

/**
 * Service class to work in background
 *
 * Created by Anwar Almojarkesh
 *
 */

public class OneScreamService extends Service {

	NotificationManager notiManager;
	public static final int MyNoti = 30056;

	private boolean m_bNotiBackground = false;
	int times = 0;
	public static int times_for_location = 0;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {

		super.onCreate();

		GlobalValues.sharedInstance()._myService = OneScreamService.this;

		notiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				running();
			}
		}, 10000);
	}

	@SuppressWarnings("deprecation")
	private void registerNotificationIcon() {
		try {
				if (!m_bNotiBackground) {

					PendingIntent contentIntent = PendingIntent.getActivity(OneScreamService.this, 0,
							new Intent(OneScreamService.this, HomeActivity.class),
							PendingIntent.FLAG_UPDATE_CURRENT);
					Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
					Notification.Builder builder = new Notification.Builder(OneScreamService.this);
					builder.setSmallIcon(R.drawable. ic_launcher)
							.setContentTitle("Detecting Sound").setLargeIcon(largeIcon).setAutoCancel(true).setContentText("One scream is detecting sounds.")
					.setContentIntent(contentIntent);

					Notification notification = builder.getNotification();
					notification.flags = Notification.FLAG_ONGOING_EVENT;
					notiManager.notify(33333, notification);

//				Notification notification = new Notification(
//				R.drawable.ic_launcher, "Detecting Sound",
//				System.currentTimeMillis());
//
//				PendingIntent contentIntent = PendingIntent.getActivity(OneScreamService.this, 0,
//				new Intent(OneScreamService.this, HomeActivity.class),
//				PendingIntent.FLAG_UPDATE_CURRENT);
//
//
//					notiManager.notify(33333, notification);
//				startForeground(33333, notification);
				m_bNotiBackground = true;
				}
		}catch (Exception e){

		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		if (notiManager != null) {
			notiManager.cancelAll();
		}

		if (GlobalValues.sharedInstance()._soundEngine != null
				&& GlobalValues.sharedInstance().m_bDetecting) {
			registerNotificationIcon();
		}

		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (GlobalValues.sharedInstance()._soundEngine != null
				&& GlobalValues.sharedInstance().m_bDetecting) {
			registerNotificationIcon();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		GlobalValues.sharedInstance()._myService = null;

		super.onDestroy();
	}

	public Handler m_handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case GlobalValues.COMMAND_OPEN_ACITIVITY:

					Log.d("", "its worst man 1 ");
					if (!GlobalValues.sharedInstance()._bEnabledActivity)
						createActivity((PRJFUNC.Signal) msg.obj);
					break;
				case GlobalValues.COMMAND_PUSH_NOTIFICATION:
					Log.d("", "its worst man 2 ");
					PRJFUNC.Signal signal = PRJFUNC
							.ConvIntToSignal((Integer) msg.obj);
					PRJFUNC.sendPushMessage(OneScreamService.this, signal);
					createActivity(signal);
					break;
				case GlobalValues.COMMAND_PUSH_SENDING:
					// same as above, but without activity creating
					// - used for proxying paging events from pebble to net
					PRJFUNC.sendPushMessage(OneScreamService.this,
							PRJFUNC.ConvIntToSignal((Integer) msg.obj));
					break;
				case GlobalValues.COMMAND_SEND_SMS_ADDRESS:
					sendGpsAddressViaSms(true);
					break;
				case GlobalValues.COMMAND_SEND_SMS:
					sendSmsDirectly((String) msg.obj);
					break;
				case GlobalValues.COMMAND_PHONE_CALL:
					initPhoneCall();
					break;
				case GlobalValues.COMMAND_ENABLE_DETECT:
					if (GlobalValues.sharedInstance()._soundEngine != null
							&& GlobalValues.sharedInstance().m_bDetecting) {
						registerNotificationIcon();
					}
					break;
				case GlobalValues.COMMAND_DISABLE_DETECT:
					if (m_bNotiBackground) {
						unregisterForeground();
					}
					break;
				case GlobalValues.COMMAND_REMOVE_NOTIFY:
					notiManager.cancel(MyNoti);
					break;

				case GlobalValues.COMMAND_SYSTEM_EXIT:
					if (m_bNotiBackground) {
						stopForeground(true);
						m_bNotiBackground = false;
					}
					System.exit(0);
					break;
				case GlobalValues.COMMAND_NOTIFY_NOISY_ENV:
					// notifyNoiseAlarm();
					Toast.makeText(OneScreamService.this, "Noise Environment",
							Toast.LENGTH_SHORT).show();
					if (m_bNotiBackground) {
						unregisterForeground();
					}
					break;
				default:
					break;
			}
		}
	};

	private void createActivity(PRJFUNC.Signal p_signal) {
		// PushWakeLock.acquireCpuWakeLock(this);

		Intent intent = null;

		intent = new Intent(OneScreamService.this, DetectedScreamActivity.class);
		intent.putExtra(DetectedScreamActivity.PARAM_SOUND_TYPE,
				PRJFUNC.ConvSignalToInt(p_signal));
		if (intent != null) {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			OneScreamService.this.startActivity(intent);
		}
	}

	@SuppressWarnings("deprecation")
	private void notifyAlarm(PRJFUNC.Signal p_signal) {
		Notification noti = new Notification(R.drawable.ic_launcher,
				"Sound Detected!", System.currentTimeMillis());

		noti.defaults = Notification.DEFAULT_SOUND;
		noti.flags = Notification.FLAG_ONLY_ALERT_ONCE;
		noti.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = null;
		String strCommand = getCommentWithSignal(p_signal);
		intent = new Intent(OneScreamService.this, _SplashActivity.class);

		if (intent == null)
			return;

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingI = PendingIntent.getActivity(
				OneScreamService.this, 0, intent, 0);
		String arrivedMsg = strCommand;
//		noti.setLatestEventInfo(OneScreamService.this, "[One Scream]",
//				arrivedMsg, pendingI);
		notiManager.notify(MyNoti, noti);

		//
		// PRJFUNC.showPushPopup(BtService.this, "Command " + strCommand);
	}

	private String getCommentWithSignal(PRJFUNC.Signal p_signal) {
		String strContent = "Special sound is heard.";

		String[] strSoundTypes = {"One Scream"};

		int nSignal = PRJFUNC.ConvSignalToInt(p_signal);
		if (nSignal >= 0 && nSignal < strSoundTypes.length) {
			strContent = strSoundTypes[nSignal];
		}

		return strContent;
	}

	void running() {
		times++;
		if (times >= 1800)
			times = 0;
		if (times % 6 == 0) {
			SharedPreferencesMgr pPhoneDb = ((MainApplication) getApplication())
					.getSharedPreferencesMgrPoint();

			boolean bDetecting = pPhoneDb.IsDetectionMode();

			if (bDetecting) {
				if (GlobalValues.sharedInstance()._soundEngine == null)
					GlobalValues.sharedInstance()._soundEngine = new UniversalScreamEngine();

				if (!GlobalValues.sharedInstance().m_bDetecting)
					GlobalValues.sharedInstance().m_bDetecting = true;
			}
			times = 0;
		}

		if (GlobalValues.sharedInstance().m_bDetecting) {
			if (GlobalValues.sharedInstance()._soundEngine != null
					&& !GlobalValues.sharedInstance()._soundEngine.m_bInNoiseEnvironment)
				registerNotificationIcon();
		} else {
			if (m_bNotiBackground) {
				unregisterForeground();
			}
		}

		new Handler().postDelayed( new Runnable() {
			@Override
			public void run() {
				running();
			}
		}, 10000);

		// Checking WIFI every 60 seconds
		if (times == 2) {
			String strCode = PRJFUNC.getCurrentWiFiSSID(this);
			if (!PRJFUNC.isNullOrEmpty(strCode)) {
				SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(
						OneScreamService.this);
				long firstDateTime = phoneDb.getFirstDateTime();
				long daysFromStart = ((new Date()).getTime() - firstDateTime)
						/ (1000 * 3600 * 24);

				String strKey = String.format("%d_%s", daysFromStart, strCode);
				if (!phoneDb.getBoolean(strKey)) {
					phoneDb.setBoolean(strKey, true);
				}
			}
		}
	}

	int nMiliSecs = 0; // 10s

	private void sendGpsAddressViaSms(boolean bFirst) {
		if (bFirst) {
			nMiliSecs = 0;
			if (GlobalValues.sharedInstance().gpsTracker == null) {
				GlobalValues.sharedInstance().gpsTracker = new GPSTracker(
						OneScreamService.this);
			}
		} else if (GlobalValues.sharedInstance().gpsTracker.canGetLocation() == false) {
			return;
		}

		GPSTracker tracker = GlobalValues.sharedInstance().gpsTracker;
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

			if (myAddress.isEmpty() && location != null) {
				List<Address> addresses = tracker
						.getGeocoderAddress(OneScreamService.this);

				// double latitude = tracker.getLatitude();
				// double longitude = tracker.getLongitude();

				if (addresses != null) {
					Address returnedAddress = addresses.get(0);
					StringBuilder strReturnedAddress = new StringBuilder("");
					for (int i = 0; i < returnedAddress
							.getMaxAddressLineIndex(); i++) {
						strReturnedAddress.append(
								returnedAddress.getAddressLine(i)).append(" ");
					}

					myAddress = strReturnedAddress.toString();
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

				if (strText.length() >= 120)
					strText = strText.substring(0, 120);

				sendSmsDirectly(strText);
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
					sendGpsAddressViaSms(false);
				}
			}, 1000);
		}
	}

	private void sendSmsDirectly(String p_strMessage) {

		if (PRJFUNC
				.isNullOrEmpty(GlobalValues.sharedInstance().m_strPhoneNumber)) {
			SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(this);
			phoneDb.loadUserExtraInfo();
		}

		String phoneNumber = GlobalValues.sharedInstance().m_strPhoneNumber;
		if (phoneNumber.isEmpty()) {
			Toast.makeText(getApplicationContext(),
					"Please set phone number to receive SMS message",
					Toast.LENGTH_LONG).show();
			return;
		}

		try {
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNumber, null, p_strMessage, null,
					null);
			Toast.makeText(getApplicationContext(),
					"SMS Sent! Content : " + p_strMessage, Toast.LENGTH_LONG)
					.show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(),
					"SMS faild, please try again later!", Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
		}
	}

	protected void initPhoneCall() {
		// TODO Auto-generated method stub

		try {
			if (PRJFUNC
					.isNullOrEmpty(GlobalValues.sharedInstance().m_strPhoneNumber)) {
				SharedPreferencesMgr phoneDb = new SharedPreferencesMgr(this);
				phoneDb.loadUserExtraInfo();
			}
			String phoneNumber = GlobalValues.sharedInstance().m_strPhoneNumber;
			Intent intent = new Intent(Intent.ACTION_CALL);

			intent.setData(Uri.parse("tel:" + phoneNumber));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
				// TODO: Consider calling
				//    ActivityCompat#requestPermissions
				// here to request the missing permissions, and then overriding
				//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
				//                                          int[] grantResults)
				// to handle the case where the user grants the permission. See the documentation
				// for ActivityCompat#requestPermissions for more details.
				return;
			}
			OneScreamService.this.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unregisterForeground() {
		stopForeground(true);
		m_bNotiBackground = false;
	}
}
