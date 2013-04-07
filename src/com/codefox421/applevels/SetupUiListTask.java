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
 * Filename:	SetupUiListTask.java
 * Class:		SetupUiListTask
 * 
 * Purpose:		Threaded initialization of managed/ignored apps list.
 */

package com.codefox421.applevels;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ListView;

//public class SetupUiListTask extends AsyncTask<Object, Void, ManagedAppAdapter> {
//	
//	Activity self;
//	ArrayList<ManagedPackage> packageList;
//	ListView managedAppsList;
//	ManagedPackage packageArrayTemplate[] = new ManagedPackage[1];
//
//	@Override
//	protected ManagedAppAdapter doInBackground(Object... params) {
//		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "doInBackground");
//		
//		self = (Activity)params[0];
//		
//		managedAppsList = (ListView)params[1];
//
//		AppLevelsDBAdapter datasource = (AppLevelsDBAdapter)params[2];
//		datasource.open();
//		
//		Boolean getIgnored = (Boolean)params[3];
//        
//        // Create list of managed applications
//        fillData(datasource, getIgnored);
//        
//        // Create and attach the adapter
//        ManagedAppAdapter appAdapter = (ManagedAppAdapter)params[4];
//        appAdapter.clear();
//        if(packageList != null && !packageList.isEmpty()) {
//        	appAdapter.addAll(packageList);
////        	appAdapter = new ManagedAppAdapter(self.getApplicationContext(),
////        			R.layout.app_entry, packageList.toArray(packageArrayTemplate));
//        }
//        appAdapter.notifyDataSetChanged();
//		
//		datasource.close();
//		
//		return appAdapter;
//		
//	}
//
//	@Override
//	protected void onPostExecute(ManagedAppAdapter result) {
//		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onPostExecute");
//		
//		if(result != null && managedAppsList != null && managedAppsList.getAdapter() == null) {
//			managedAppsList.setAdapter(result);
//			result.notifyDataSetChanged();
//		}
//		
//	}
//	
//	protected void fillData(AppLevelsDBAdapter datasource, Boolean getIgnored) {
//		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "fillData");
//    	
//    	// Retrieve the cursor
//    	Cursor cursor = datasource.GetAppVolumes(getIgnored);
//    	if(cursor == null) {
//    		packageList = null;
//    		return;
//    	}
//        packageList = new ArrayList<ManagedPackage>();
//    	
//    	while(!cursor.isAfterLast()) {
//    		
//    		// Retrieve application icon
//    		Drawable appIcon;
//    		try {
//    			appIcon = self.getPackageManager().getApplicationIcon(cursor.getString(
//    					cursor.getColumnIndex(AppLevelsDBHelper.KEY_PACKAGE)));
//    		} catch(Exception exception) {
//    			appIcon = self.getResources().getDrawable(R.drawable.default_app);
//    		}
//    		
//    		// Retrieve package name
//    		String packageName = cursor.getString(cursor.getColumnIndex(AppLevelsDBHelper.KEY_PACKAGE));
//    		
//    		// Retrieve volume level
//    		int volumeLevel = cursor.getInt(cursor.getColumnIndex(AppLevelsDBHelper.KEY_VOLUME));
//    		
//    		packageList.add(new ManagedPackage(packageName, appIcon, volumeLevel));
//    		
//    		cursor.moveToNext();
//    	}
//    	
//    	//Log.d("AppLevels:" + this.getClass().getSimpleName(), "fillData (end)");
//    }
//	
//}
