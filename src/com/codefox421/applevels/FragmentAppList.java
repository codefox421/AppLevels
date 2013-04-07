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
 * Filename:	FragmentAppList.java
 * Class:		FragmentAppList
 * 
 * Purpose:		Represents a UI-fragment displaying the list of
 *              app packages.
 */

package com.codefox421.applevels;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.ActionMode;

public abstract class FragmentAppList extends SherlockListFragment {

	Activity self;
	ListView managedAppsList;
	ManagedAppAdapter appAdapter;
	AppLevelsDBAdapter datasource;
	ArrayList<ManagedPackage> packageList;
	ActionMode mMode;
	
	protected boolean getIgnored;
	protected int cabMenuId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onCreate");
		
		self = getActivity();
		datasource = new AppLevelsDBAdapter(self.getApplicationContext());
		datasource.open();
		
		Cursor cursor = datasource.GetAppVolumes(getIgnored);
		appAdapter = new ManagedAppAdapter(self, cursor, 0);
		
		setListAdapter(appAdapter);
		
		datasource.close();
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onCreateView");
		return inflater.inflate(R.layout.apps_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onActivityCreated");
		managedAppsList = getListView(); //(ListView)self.findViewById(R.id.managedAppsList);
        
		// Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(self.getApplicationContext());
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        managedAppsList.setEmptyView(progressBar);
        
        
		
        this.invalidate();
        
        // setup interaction
        if ( Build.VERSION.SDK_INT >= 11 ) {
        	
        	// setup standard actionbar multiselect
        	managedAppsList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
			
        	try {
        		managedAppsList.setMultiChoiceModeListener(/*listener)
				Method setMultiChoiceModeListener = Context.class.getMethod("setMultiChoiceModeListener", new Class[] { ListView.class });
				setMultiChoiceModeListener.invoke(managedAppsList, */new MultiChoiceModeListener() {

					@Override
					public boolean onActionItemClicked(
							android.view.ActionMode mode, MenuItem item) {
						// respond to clicks on the actions in the CAB
						return handleActionItemClick(mode, item);
					}

					@TargetApi(Build.VERSION_CODES.HONEYCOMB)
					@Override
					public boolean onCreateActionMode(android.view.ActionMode mode,
							Menu menu) {
						// inflate the menu for the CAB
						MenuInflater inflater = mode.getMenuInflater();
						inflater.inflate(cabMenuId, menu);
						return true;
					}

					@Override
					public void onDestroyActionMode(android.view.ActionMode mode) {
						// by default, selected items are deselected
					}

					@Override
					public boolean onPrepareActionMode(
							android.view.ActionMode mode, Menu menu) {
						// perform updates to the CAB due to invalidate() request
						return false;
					}

					@Override
					public void onItemCheckedStateChanged(
							android.view.ActionMode mode, int position, long id,
							boolean checked) {
						// TODO Auto-generated method stub
						
					}
					
				});
//			} catch (NoSuchMethodException e) {
//				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			} catch (InvocationTargetException e) {
//				e.printStackTrace();
			}
        } else {
        	
        }
        
	}
	
	protected abstract boolean handleActionItemClick(android.view.ActionMode mode, MenuItem item);
	
	protected void invalidateList() {
		
		if (appAdapter != null) {
			datasource.open();
			appAdapter.changeCursor(datasource.GetAppVolumes(getIgnored));
			datasource.close();
		}
        
	}
	
	public void invalidate() {
		// update everything here
		
		invalidateList();
		
	}

}
