package com.onescream.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.onescream.engine.UniversalScreamEngine;
import com.uc.prjcmn.GlobalValues;

/**
 * Service Receiver class
 *
 * Created by Anwar Almojarkesh
 *
 */

public class ServiceReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
		if(state.equals(TelephonyManager.EXTRA_STATE_IDLE))
		{
			if (GlobalValues.sharedInstance()._myService != null) {
				if (GlobalValues.sharedInstance()._soundEngine == null) {
					GlobalValues.sharedInstance()._soundEngine = new UniversalScreamEngine();
				}
			}
		}
		else if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
		{
		}
		else if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))
		{
			if (GlobalValues.sharedInstance().sharedInstance()._myService != null) {
				if (GlobalValues.sharedInstance()._soundEngine != null) {
					UniversalScreamEngine soundEngine = GlobalValues.sharedInstance()._soundEngine;
					GlobalValues.sharedInstance()._soundEngine = null;
					soundEngine.Terminate();
				}
			}
		}
		else if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
		{
		}
	}
}