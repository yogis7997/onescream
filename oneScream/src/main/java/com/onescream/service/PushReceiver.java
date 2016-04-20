package com.onescream.service;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.util.Log;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import com.uc.prjcmn.GlobalValues;
import com.uc.prjcmn.PRJFUNC;

/**
 * Serivce class for receiving push messages
 *
 * Created by Anwar Almojarkesh
 *
 */

public class PushReceiver extends ParsePushBroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    private Context mContext;

    public static int notifyOrNot = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (notifyOrNot == 1) {
            return;
        }

        try {

            if (GlobalValues.sharedInstance().m_bDetectionPhone)
                return;
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString(
                    "com.parse.Data"));

            PRJFUNC.Signal signal = PRJFUNC.Signal.ONE_SCREAM;
            try {
                String strMessage = json.getString("message");
                Log.d(TAG, "got action " + action + " on channel " + channel
                        + " with:" + strMessage);

                String[] strSoundsInfos = strMessage.split(",");
                String strDevId = strSoundsInfos[0];
                if (PRJFUNC.getDeviceID(context).equals(strDevId))
                    return;

                PRJFUNC.DETECTED_TYPE = Integer.parseInt(strSoundsInfos[1]);
                PRJFUNC.DETECTED_NUMBER = Integer.parseInt(strSoundsInfos[2]);
                signal = PRJFUNC.ConvIntToSignal(PRJFUNC.DETECTED_TYPE);
            } catch (Exception e) {
                return;
            }

            if (GlobalValues.sharedInstance()._myService.m_handler != null) {
                Message msg = new Message();
                msg.what = GlobalValues.COMMAND_OPEN_ACITIVITY;
                msg.obj = signal;
                GlobalValues.sharedInstance()._myService.m_handler.sendMessage(msg);
            }

        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }


}