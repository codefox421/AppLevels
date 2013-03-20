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

import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

public class AppLevelsMain extends Activity {
	

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            	
        // setup action bar for tabs
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        Tab tab = actionBar.newTab()
                .setText(R.string.managed_tab)
                .setTabListener(new TabListener<FragmentManagedList>(
                        this, "managed_list_view", FragmentManagedList.class));
        actionBar.addTab(tab);
        
        tab = actionBar.newTab()
        		.setText("Instructions")
        		.setTabListener(new TabListener<FragmentInstructions>(
        				this, "instructions_view", FragmentInstructions.class));
        actionBar.addTab(tab);
    }
    
    
    @Override
    public void onResume() {
    	super.onResume();
    	
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    
    public void toggleService(View view) {
    	if(isServiceRunning()) {
    		Log.d("AppLevels", "Stopping Service...");
    		stopService(new Intent(AppLevelsMain.this, AppLevelsService.class));
    	} else {
    		Log.d("AppLevels", "Starting Service...");
    		startService(new Intent(AppLevelsMain.this, AppLevelsService.class));
    	}
    	
    	//toggleButton.setChecked(isServiceRunning());
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

}
