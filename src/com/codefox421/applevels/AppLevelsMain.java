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
 * Filename:	AppLevelsMain.java
 * Class:		AppLevelsMain
 * 
 * Purpose:		As the user-facing portion of AppLevels, AppLevelsMain is
 * 				responsible for managing the GUI and the operation of the
 * 				background helper service, AppLevelsService.
 */

package com.codefox421.applevels;

import java.util.ArrayList;

import android.R.drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.ProgressBar;

public class AppLevelsMain extends Activity {
	
	CompoundButton toggleButton;
	ListView managedAppsList;
	PackageManager packageManager;
	AppLevelsDBAdapter datasource;
	Cursor cursor;
	ArrayList<ManagedPackage> packageList;
	ManagedPackage packageArrayTemplate[];

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        toggleButton = (CompoundButton)findViewById(R.id.serviceToggleButton);
        managedAppsList = (ListView)findViewById(R.id.managedAppsList);
        packageManager = getPackageManager();
        datasource = new AppLevelsDBAdapter(this);
        datasource.open();
    	
    	// Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        managedAppsList.setEmptyView(progressBar);
        /*
        // Create list of managed applications
        packageList = new ArrayList<ManagedPackage>();
        fillData();
        
        // Create and attach the adapter
        if(packageList != null && !packageList.isEmpty()) {
	        ManagedAppAdapter appAdapter = new ManagedAppAdapter(this, R.layout.managed_app, packageList.toArray(packageArrayTemplate));
	        managedAppsList.setAdapter(appAdapter);
        }*/
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    @Override
    public void onResume() {
    	super.onResume();
		toggleButton.setChecked(isServiceRunning());
    	datasource.open();
//    	AudioManager audioManager = ((AudioManager)getSystemService(AUDIO_SERVICE));
//    	for(int i = 0; i < 15; i++)
//    		audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	datasource.close();
    }
    
    
    public void toggleService(View view) {
    	if(isServiceRunning()) {
    		Log.d("AppLevels", "Stopping Service...");
    		stopService(new Intent(AppLevelsMain.this, AppLevelsService.class));
    	} else {
    		Log.d("AppLevels", "Starting Service...");
    		startService(new Intent(AppLevelsMain.this, AppLevelsService.class));
    	}
    	
    	toggleButton.setChecked(isServiceRunning());
    }
    
    
    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AppLevelsService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    
    
    private void fillData() {
    	
    	// Retrieve the cursor
    	cursor = datasource.GetAppVolumes();
    	if(cursor == null) {
    		packageList = null;
    		return;
    	}
    	
    	while(!cursor.isAfterLast()) {
    		
    		// Retrieve application icon
    		Drawable appIcon;
    		try {
    			appIcon = packageManager.getApplicationIcon(cursor.getString(cursor.getColumnIndex(AppLevelsDBHelper.KEY_PACKAGE)));
    		} catch(Exception exception) {
    			appIcon = getResources().getDrawable(R.drawable.default_app);
    		}
    		
    		// Retrieve package name
    		String packageName = cursor.getString(cursor.getColumnIndex(AppLevelsDBHelper.KEY_PACKAGE));
    		
    		// Retrieve volume level
    		int volumeLevel = cursor.getInt(cursor.getColumnIndex(AppLevelsDBHelper.KEY_VOLUME));
    		
    		packageList.add(new ManagedPackage(packageName, appIcon, volumeLevel));
    		
    		cursor.moveToNext();
    	}
    }
}
