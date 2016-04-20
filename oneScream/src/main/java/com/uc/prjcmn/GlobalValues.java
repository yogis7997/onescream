package com.uc.prjcmn;

import java.util.ArrayList;

import com.onescream.engine.UniversalScreamEngine;
import com.onescream.service.OneScreamService;

/**
 * The class for saving global values
 *
 * Created by Anwar Almojarkesh
 *
 */

public class GlobalValues {
	public static final int COMMAND_OPEN_ACITIVITY = 20032;
	public static final int COMMAND_ENABLE_DETECT = 20033;
	public static final int COMMAND_DISABLE_DETECT = 20034;
	public static final int COMMAND_REMOVE_NOTIFY = 20035;
	public static final int COMMAND_PUSH_NOTIFICATION = 20036;
	public static final int COMMAND_SYSTEM_EXIT = 20038;
	public static final int COMMAND_PUSH_SENDING = 20039;
	public static final int COMMAND_SEND_SMS_ADDRESS = 20040;
	public static final int COMMAND_PHONE_CALL = 20041;
	public static final int COMMAND_SEND_SMS = 20042;
	public static final int COMMAND_NOTIFY_NOISY_ENV = 20043;


	public static GlobalValues singletoneInstance = new GlobalValues();

	public static GlobalValues sharedInstance() {
		return singletoneInstance;
	}

	public boolean _firstRun = false;

	public UniversalScreamEngine _soundEngine = null;
	public OneScreamService _myService = null;

	public int _frameCnt = 0;

	public boolean _bEnabledActivity = false;

	public boolean m_bDetecting = false;

	// Alerting methods
	public boolean m_bNotifyVibrate = true;
	public boolean m_bNotifyFlash = true;

	// Sms Notification Settings
	public boolean m_bSmsMapLink = false;
	public boolean m_bSmsFullAddress = false;
	
	// User Infos
	public String m_stUserName = "";
	public String m_stPassword = "";

	public String m_strEmail = "";
	public String m_strFirstName = "";
	public String m_strLastName = "";
	public String m_strPostCode = "";
	public String m_strPhoneNumber = "";
	public String m_strAddress = "";

	public String m_stUserId = "";
	public String m_stKeyId = "";
	public String m_stCompId = "";
	public String m_stUserType = "2";
	
	// notify user
	public String notifyUserName = "";
	public String sosNotifyUserName = "";
			
	
	// parse sound type info
	public String soundObjectId = "";
	public String setStatus = "not yet";
	

	public boolean m_bDetectionPhone = false;
	
	// in app purchased
	public int _fullModePurchased = 0;
	
	public ArrayList<String> m_arrayIgnoreWiFiIds = new ArrayList<String>();
	
	
	public GPSTracker gpsTracker = null;
	public String m_strDetectedLocation;
	public String m_strDetectedAddress;
	public String m_strDetectedObjectId;
	
}
