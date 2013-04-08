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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class AppLevelsMain extends SherlockFragmentActivity {
	
	private static final String MANAGED_TAG = "managed_list_view";
	private static final String IGNORED_TAG = "ignored_list_view";
	private static final String ACTIVE_TAB_INDEX = "activeTabIndex";
	
	private Tab managedTab;
	private Tab ignoredTab;
//	private Tab instructTab;
	
	private ActionBar actionBar;
	private MenuItem power;
	
	private boolean justCreated = false;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Log.d("AppLevels:" + this.getClass().getSimpleName(), "onCreate");
            	
        // Setup action bar for tabs
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayShowTitleEnabled(false);

        // Create and add the managed apps tab
        managedTab = actionBar.newTab()
                .setText(R.string.managed_tab)
                .setTabListener(new TabListener<FragmentManagedList>(
                        this, MANAGED_TAG, FragmentManagedList.class));
        actionBar.addTab(managedTab);
        
        // Create and add the ignored apps tab
        ignoredTab = actionBar.newTab()
        		.setText(R.string.ignored_tab)
        		.setTabListener(new TabListener<FragmentIgnoredList>(
        				this, IGNORED_TAG, FragmentIgnoredList.class));
        actionBar.addTab(ignoredTab);
        
        // Create and add the instructions tab
//        instructTab = actionBar.newTab()
//        		.setText("Instructions")
//        		.setTabListener(new TabListener<FragmentInstructions>(
//        				this, "instructions_view", FragmentInstructions.class));
//        actionBar.addTab(instructTab);
        
        // Run "resume" setup
        if (savedInstanceState != null) {
        	//do resume state actions here
        	
        	// Return to last selected tab
        	int tabIndex = savedInstanceState.getInt(ACTIVE_TAB_INDEX);
        	actionBar.setSelectedNavigationItem(tabIndex);
        }
        
        justCreated = true;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onCreateOptionsMenu");
    	
    	// inflate the menu
    	MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        
        // grab the power button
        power = menu.findItem(R.id.power);
        
        //Log.d("AppLevels:" + this.getClass().getSimpleName(), "onCreateOptionsMenu (end)");
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onOptionsItemSelected");
    	
    	switch (item.getItemId()) {
    	case R.id.power:
    		toggleService(item);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }

    
    @Override
    public void onResume() {
    	//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onResume");
    	
    	// update the power button
		updatePowerButton();
		
		if (!justCreated) {
			// Update the lists
			FragmentManager fragmentManager = getSupportFragmentManager();
			// Managed apps list
			Fragment fragment = fragmentManager.findFragmentByTag(MANAGED_TAG);
			if (fragment != null) {
				((FragmentManagedList) fragment).invalidate();
			}
			// Ignored apps list
			fragment = fragmentManager.findFragmentByTag(IGNORED_TAG);
			if (fragment != null) {
				((FragmentIgnoredList) fragment).invalidate();
			}
		}
		justCreated = false;
    	
    	super.onResume();
    }
    
    
    @Override
    public void onPause() {
    	//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onPause");
    	super.onPause();
    }
    
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	
    	// Store the active tab
    	outState.putInt(ACTIVE_TAB_INDEX, actionBar.getSelectedNavigationIndex());
    }
    
    
    private void updatePowerButton() {
    	//Log.d("AppLevels:" + this.getClass().getSimpleName(), "updatePowerButton");
    	
    	if ( power != null ) {
    		boolean serviceActive = AppLevelsService.isRunning(this);
    		power.setIcon(
    			serviceActive
    				? R.drawable.ic_action_power_on
    				: R.drawable.ic_action_power_off
    		).setTitle(
    			serviceActive
    				? R.string.power_off
    				: R.string.power_on
    		);
    	}
    }
    
    
    public void toggleService(MenuItem item) {
    	//Log.d("AppLevels:" + this.getClass().getSimpleName(), "toggleService");
    	
    	if (AppLevelsService.isRunning(this)) {
    		Log.d("AppLevels", "Stopping Service...");
    		stopService(new Intent(AppLevelsMain.this, AppLevelsService.class));
    	} else {
    		Log.d("AppLevels", "Starting Service...");
    		startService(new Intent(AppLevelsMain.this, AppLevelsService.class));
    	}
    	
    	updatePowerButton();
    }

}
