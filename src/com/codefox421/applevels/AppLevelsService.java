package com.codefox421.applevels;

import java.util.Timer;
import java.util.TimerTask;

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
	
	private static final String LOG_TAG = "AppLevelsService";				// Tag used for logging
	private static VolumeReceiver volumeReceiver;							// Volume Change listener
	private static final int NOTIF_ID = 0;									// ID number for notification manager
	private static final String NOTIF_TAG = "com.codefox421.AppLevels";		// Tag for notification manager
	private static NotificationManager notificationManager;					// Notification Manager for persistent notifications
	private static AudioManager audioManager;								// Audio Manager for volume level sensing
	private static IntentFilter volumeFilter =								// Intent Filter for volume level sensing
			new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
	private static ActivityManager activityManager;							// Activity Manager for running activity sensing
	private int lastVolume;													// Stores last volume level to check against
	private String lastPackage;												// Stores last running application to check against
	private static AppLevelsDBAdapter database;								// DB access to applications' volume records
	private static Timer timer;												// Timer for scheduled application sniffing
	private static boolean databaseLocked = false;									// Lock for changing volume without affecting database

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	@Override
	public void onCreate() {
	//code to execute when the service is first created
		Log.d(LOG_TAG, "onCreate method start");
		
		volumeReceiver = new VolumeReceiver();
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
		Log.d(LOG_TAG, "onCreate method complete");
	}
	
	
	@Override
	public void onStart(Intent intent, int startid) {
	//code to execute when the service is starting up
		Log.d(LOG_TAG, "onStart method start");
		
		Toast.makeText(this, "AppLevelsService Started", Toast.LENGTH_SHORT).show();
		
		registerReceiver(volumeReceiver, volumeFilter);		//start listening for volume changes
		lastVolume = getVolume();
		
		lastPackage = getFrontPackage();
		scheduleAppSniffing();		//start sniffing for launched applications
		
		Log.d(LOG_TAG, "onStart method complete");
	}
	
	
	@Override
	public void onDestroy() {
	//code to execute when the service is shutting down
		Log.d(LOG_TAG, "onDestroy method start");
		
		unregisterReceiver(volumeReceiver);		//stop listening for volume changes
		
		notificationManager.cancel(NOTIF_TAG, NOTIF_ID);		//close the active notification
		
		timer.cancel();		//stop sniffing for launched applications
		
		Toast.makeText(this, "AppLevelsService Stopped", Toast.LENGTH_SHORT).show();
		
		Log.d(LOG_TAG, "onDestroy method complete");
	}
	
	
	private void recordVolume() {
		
		// Check for database lock
		if(databaseLocked)
			return;
		
		// Find the current volume
    	int currentVolume = getVolume();
    	if(currentVolume == lastVolume)
    		return;		//cancel record if volume is unchanged
    	
    	// Find the currently running application
    	String packageName = getFrontPackage();
    	if(packageName.equalsIgnoreCase("com.codefox421.applevels"))
    		return;		//cancel record if self is active
    	
    	// Write new record to the database
    	Log.d(LOG_TAG, "Recording volume level (" + currentVolume + ") for " + packageName + "...");
    	database.updateAppVolume(packageName, currentVolume);
    	
    	// Update volume tracker
    	lastVolume = currentVolume;
	}
	
	
	private class VolumeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			recordVolume();
		}

	}
	
	private void scheduleAppSniffing() {
		timer = new Timer("appSniffTimer", true);
		timer.schedule(new appLaunchSniff(), 1000L, 1000L);
	}
	
	private class appLaunchSniff extends TimerTask {
		
		public void run() {
			
			Log.d(LOG_TAG, "TimerTask run start");
			
			String currentPackage = getFrontPackage();
			if(!currentPackage.equalsIgnoreCase(lastPackage)) {

				// Set the media volume to stored level (if exists)
				initAppVolume(currentPackage);
				
				lastPackage = currentPackage;
			}
			
			Log.d(LOG_TAG, "TimerTask run complete");
		}
	}
	
	private void initAppVolume(String packageName) {
		
		Log.d(LOG_TAG, "Initializing App Volume...");
		
		// Retrieve the stored value
		int storedLevel = database.getAppVolume(packageName);
		if(storedLevel < 0)
			return;
		
		Log.d(LOG_TAG, "Volume to be set to " + storedLevel + ".");
		
		// Lock the database
		databaseLocked = true;
		
		// Adjust the volume
		while(getVolume() < storedLevel)
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, 0);
		while(getVolume() > storedLevel)
			audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, 0);
		
		Log.d(LOG_TAG, "Volume adjusted to " + getVolume() + ".");
		
		// Unlock the database
		databaseLocked = false;
		
		Log.d(LOG_TAG, "App Volume Initialized!");
	}
	
	private String getFrontPackage() {
		return activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
	}
	
	private int getVolume() {
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

}
