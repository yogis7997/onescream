package com.onescream.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import com.onescream.R;

/**
 * Service class for playing music like police siren
 *
 * Created by Anwar Almojarkesh
 *
 */

public class PlayAudio extends Service {
	private static final String LOGCAT = "PlayAudio";
	MediaPlayer objPlayer;
	private boolean looping = true;

	public void onCreate() {
		super.onCreate();
		Log.d(LOGCAT, "Service Started!");

		try {
			AudioManager amanager = (AudioManager) this
					.getSystemService(Context.AUDIO_SERVICE);
			int maxVolume = amanager
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			amanager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

			objPlayer = MediaPlayer.create(this, R.raw.policesiren);
			objPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC); // this is
																		// important.

			objPlayer.start();

			Log.e(LOGCAT, "Media Player started!");
			if (objPlayer.isLooping() != true) {
				Log.e(LOGCAT, "Problem in Playing Audio");
			}
		} catch (Exception e) {
			Log.e(LOGCAT," audio exception"+e.getMessage());
			e.printStackTrace();
		}
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		objPlayer.setVolume(1.0f, 1.0f);
		objPlayer.start();

		Log.d(LOGCAT, "Media Player started!");
		if (objPlayer.isLooping() != true) {
			Log.d(LOGCAT, "Problem in Playing Audio");
		}
		return 1;
	}

	public void onStop() {
		objPlayer.stop();
		objPlayer.release();
	}

	public void onPause() {
		objPlayer.stop();
		objPlayer.release();
	}

	public void onDestroy() {
		objPlayer.stop();
		objPlayer.release();
	}

	@Override
	public IBinder onBind(Intent objIndent) {
		return null;
	}
}
