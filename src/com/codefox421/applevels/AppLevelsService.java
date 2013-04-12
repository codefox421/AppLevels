/**
 * Copyright 2013 Nick Iaconis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Filename:	AppLevelsService.java
 * Class:		AppLevelsService
 * 
 * Purpose:		Runs and manages a background alarm in support of the AppLevels
 * 				application. Deals with monitoring switches between top-level (user-
 * 				facing) applications, monitoring adjustments to media volume level,
 * 				writing/retrieving data from storage, and adjusting media volume
 * 				upon application switching when there exists data stored for the
 * 				newly top-level application. This is where the meat and potatoes of
 * 				AppLevels resides.
 */

package com.codefox421.applevels;

import java.util.Calendar;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class AppLevelsService extends Service {
	
	// Constants
	private static final String LOG_TAG = "AppLevelsService";				// Tag used for logging
	private static final int NOTIF_ID = 421;								// ID number for notification manager
	private static final String NOTIF_TAG = "com.codefox421.AppLevels";		// Tag for notification manager
	private static final String ACTION_NAME = "com.codefox421.appsniffnow";	// 
	private static final Intent ACTION_INTENT = new Intent(ACTION_NAME);	// 
	
	// Managers
	private static ActivityManager activityManager;							// Activity Manager for running activity sensing
	private AlarmManager alarmManager;										// Alarm Manager for scheduled application sniffing
	private static AudioManager audioManager;								// Audio Manager for volume level sensing
	private static NotificationManager notificationManager;					// Notification Manager for persistent notifications
	
	// Receivers
	private static AlarmReceiver alarmReceiver;								// Alarm Receiver to help with application sniffing
	private static VolumeReceiver volumeReceiver;							// Volume Change listener
	
	// Filters
	private static final IntentFilter volumeFilter =						// Intent Filter for volume level sensing
			new IntentFilter("android.media.VOLUME_CHANGED_ACTION");
	private static final IntentFilter alarmFilter =							// Intent Filter for alarm response
			new IntentFilter(ACTION_NAME); 
	
	// Miscellaneous
	private static AppLevelsDBAdapter database;								// DB access to applications' volume records
	private static PendingIntent operation;									// Pending Intent for use with Alarm Manager
	
	// Member variables
	private static boolean databaseLocked = false;							// Lock for changing volume without affecting database
	private String lastPackage;												// Stores last running application to check against
	private int lastVolume;													// Stores last volume level to check against

	
	@Override
	public void onCreate() {
		Log.d(LOG_TAG, "onCreate method start");
		
		// Create managers
		activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Create receivers
		alarmReceiver = new AlarmReceiver();
		volumeReceiver = new VolumeReceiver();
		
		// Initialize database
		database = new AppLevelsDBAdapter(this);
		
		// Make foreground service
		Notification notification = buildNotification(null, 0);
		startForeground(NOTIF_ID, notification);
		
		Log.d(LOG_TAG, "onCreate method complete");
	}
	
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(LOG_TAG, "onStart method start");
		
		database.open();
		
		// Start listening for volume changes
		lastVolume = getVolume();
		registerReceiver(volumeReceiver, volumeFilter);
		
		// Initialize alarm intent
		operation = PendingIntent.getBroadcast(this, 0, ACTION_INTENT, PendingIntent.FLAG_UPDATE_CURRENT);
		
		// Start sniffing for launched applications
		lastPackage = getFrontPackage();
		alarmManager.setRepeating(AlarmManager.RTC, Calendar.getInstance()
				.getTimeInMillis() + 1000, 1000, operation);
		registerReceiver(alarmReceiver, alarmFilter);
		
		Toast.makeText(this, "AppLevelsService Running", Toast.LENGTH_SHORT).show();
		
		Log.d(LOG_TAG, "onStart method complete");
	}
	
	
	@Override
	public void onDestroy() {
		Log.d(LOG_TAG, "onDestroy method start");
		
		// Stop listening for volume changes
		unregisterReceiver(volumeReceiver);
		
		// Stop sniffing for launched applications
		alarmManager.cancel(operation);
		operation.cancel();
		unregisterReceiver(alarmReceiver);
		
		// Unregister foreground service
		stopForeground(true);
		
		notificationManager.cancel(NOTIF_TAG, NOTIF_ID);		//close the active notification
		
		database.close();
		
		Toast.makeText(this, "AppLevelsService Stopped", Toast.LENGTH_SHORT).show();
		
		Log.d(LOG_TAG, "onDestroy method complete");
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
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
    	// If music is playing, use the last known application
    	String packageName = audioManager.isMusicActive() ? lastPackage : getFrontPackage();
    	if(packageName.equalsIgnoreCase("com.codefox421.applevels"))
    		return;		//cancel record if self is active
    	
    	// Write new record to the database
    	Log.d(LOG_TAG, "Recording volume level (" + currentVolume + ") for " + packageName + "...");
    	boolean result = database.updateAppVolume(packageName, currentVolume);
    	Log.d(LOG_TAG, "Data " + ((result) ? "written to database successfully!" : "not written to database."));
    	
    	// Update volume tracker
    	lastVolume = currentVolume;
    	
    	// Update notification
    	Notification notification = buildNotification(packageName, lastVolume);
    	notificationManager.notify(NOTIF_ID, notification);
	}
	
	private void comparePackages() {
//		Log.d(LOG_TAG, "Comparing packages...");
		
		String currentPackage = getFrontPackage();
		boolean musicPlaying = audioManager.isMusicActive();
		if(!musicPlaying && !currentPackage.equalsIgnoreCase(lastPackage)) {

			// Set the media volume to stored level (if exists)
			boolean isManaged = initAppVolume(currentPackage);
			
			// Update the notification
			Notification notification = buildNotification((isManaged ? currentPackage : null), lastVolume);
			notificationManager.notify(NOTIF_ID, notification);
			
			lastPackage = currentPackage;
		}
	}
	
	private boolean initAppVolume(String packageName) {
		Log.d(LOG_TAG, "Initializing App Volume for " + packageName + "...");
		
		// Retrieve the stored value
		int storedLevel = database.getAppVolume(packageName);
		if(storedLevel < 0) {
			Log.d(LOG_TAG, "No record exists for " + packageName);
			return false;
		}
		
		Log.d(LOG_TAG, "Volume to be set to " + storedLevel + ".");
		
		// Lock the database
		databaseLocked = true;
		
		// Adjust the volume
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
				Math.min(storedLevel, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)), 0);
		
		Log.d(LOG_TAG, "Volume adjusted to " + getVolume() + ".");
		
		// Unlock the database
		databaseLocked = false;
		
		Log.d(LOG_TAG, "App Volume Initialized!");
		return true;
	}
	
	private String getFrontPackage() {
		return activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
	}
	
	private int getVolume() {
		return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	
	private Notification buildNotification(String packageName, int volume) {
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		
		mBuilder.setContentTitle(getResources().getString(R.string.stat_title));
		if (packageName == null) {
			mBuilder.setContentText(getResources().getString(R.string.stat_unmanaged));
			//mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_inactive));
			mBuilder.setSmallIcon(R.drawable.ic_stat_inactive);
		} else {
			mBuilder.setContentText(packageName + " " + getResources().getString(R.string.stat_managed) + " " + volume);
			//mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_active));
			mBuilder.setSmallIcon(R.drawable.ic_stat_active);
		}
		mBuilder.setOngoing(true);
		mBuilder.setWhen(0);
		
		return mBuilder.build();
	}
	
	
	private class VolumeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			recordVolume();
		}

	}
	
	private class AlarmReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			boolean serviceIsRunning = false;
			ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
				if (AppLevelsService.class.getName().equals(service.service.getClassName())) {
					serviceIsRunning = true;
				}
			}
			if (!serviceIsRunning) {
				context.startService(new Intent(context, AppLevelsService.class));
			}
			
			comparePackages();
		}
	}
	
	
	public static void start(Context context) {
		context.startService(new Intent(context, AppLevelsService.class));
	}
	
	public static void stop(Context context) {
		context.stopService(new Intent(context, AppLevelsService.class));
	}
	
	public static boolean isRunning(Context context) {
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "isRunning");

		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (AppLevelsService.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;

	}

}
