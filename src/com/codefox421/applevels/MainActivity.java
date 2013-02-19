package com.codefox421.applevels;

import java.util.ArrayList;

import android.R.drawable;
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

public class MainActivity extends Activity {
	
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
        
        // Create list of managed applications
        packageList = new ArrayList<ManagedPackage>();
        fillData();
        
        // Create and attach the adapter
        if(!packageList.isEmpty()) {
	        ManagedAppAdapter appAdapter = new ManagedAppAdapter(this, R.layout.managed_app, packageList.toArray(packageArrayTemplate));
	        managedAppsList.setAdapter(appAdapter);
        }
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
    	
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	datasource.close();
    }
    
    
    public void toggleService(View view) {
    	if(isServiceRunning()) {
    		Log.d("AppLevels", "Stopping Service...");
    		stopService(new Intent(MainActivity.this, AppLevelsService.class));
    	} else {
    		Log.d("AppLevels", "Starting Service...");
    		startService(new Intent(MainActivity.this, AppLevelsService.class));
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
    	
    	while(!cursor.isAfterLast()) {
    		
    		// Retrieve application icon
    		Drawable appIcon;
    		try {
    			appIcon = packageManager.getApplicationIcon(cursor.getString(cursor.getColumnIndex(AppLevelsDBAdapter.KEY_PACKAGE)));
    		} catch(Exception exception) {
    			appIcon = getResources().getDrawable(R.drawable.default_app);
    		}
    		
    		// Retrieve package name
    		String packageName = cursor.getString(cursor.getColumnIndex(AppLevelsDBAdapter.KEY_PACKAGE));
    		
    		// Retrieve volume level
    		int volumeLevel = cursor.getInt(cursor.getColumnIndex(AppLevelsDBAdapter.KEY_VOLUME));
    		
    		packageList.add(new ManagedPackage(packageName, appIcon, volumeLevel));
    		
    		cursor.moveToNext();
    	}
    }
}
