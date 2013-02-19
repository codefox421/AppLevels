package com.codefox421.applevels;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class AppLevelsService extends Service {
	
	private static final String TAG = "AppLevelsService";					// Tag used for logging
	private static VolumeReceiver volumeReceiver;							// Volume Change listener
	private static final int NOTIF_ID = 0;									// ID number for notification manager
	private static final String NOTIF_TAG = "com.codefox421.AppLevels";		// Tag for notification manager
	private static NotificationManager notificationManager;					// Notification Manager for persistent notifications
	private static AudioManager audioManager;								// Audio Manager for volume level sensing
	private static IntentFilter volumeFilter =								// Intent Filter for volume level sensing
			new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
	private static ActivityManager activityManager;							// Activity Manager for running activity sensing
	private int lastVolume;

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
	//code to execute when the service is first created
		Log.d(TAG, "onCreate method start");
		
		volumeReceiver = new VolumeReceiver();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		Log.d(TAG, "onCreate method complete");
	}
	
	
	@Override
	public void onStart(Intent intent, int startid) {
	//code to execute when the service is starting up
		Log.d(TAG, "onStart method start");
		
		Toast.makeText(this, "AppLevelsService Started", Toast.LENGTH_SHORT).show();
		
		registerReceiver(volumeReceiver, volumeFilter);
		
		lastVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		
		Log.d(TAG, "onStart method complete");
	}
	
	
	@Override
	public void onDestroy() {
	//code to execute when the service is shutting down
		Log.d(TAG, "onDestroy method start");
		
		unregisterReceiver(volumeReceiver);
		
		notificationManager.cancel(NOTIF_TAG, NOTIF_ID);		//close the active notification
		
		Toast.makeText(this, "AppLevelsService Stopped", Toast.LENGTH_SHORT).show();
		
		Log.d(TAG, "onDestroy method complete");
	}
	
	
	private void recordVolume() {
		// Find the current volume
    	int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    	if(currentVolume == lastVolume)
    		return;		//cancel record if volume is unchanged
    	
    	// Find the currently running application
    	String packageName = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
    	if(packageName.equalsIgnoreCase("com.codefox421.applevels"))
    		return;		//cancel record if self is active
    	
    	Log.d(TAG, "Recording volume level (" + currentVolume + ") for " + packageName + "...");
    	
    	// Update volume tracker
    	lastVolume = currentVolume;
	}
	
	
	private class VolumeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			recordVolume();
		}

	}

}
