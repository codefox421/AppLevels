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
 * Filename:	FragmentIgnoredList.java
 * Class:		FragmentIgnoredList
 * 
 * Purpose:		Represents a UI-fragment displaying the list of
 *              ignored packages.
 */

package com.codefox421.applevels;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockFragment;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;

public class FragmentIgnoredList extends SherlockFragment {

	Activity self;
	ListView ignoredAppsList;
	AppLevelsDBAdapter datasource;
	ArrayList<ManagedPackage> packageList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		self = getActivity();
		datasource = new AppLevelsDBAdapter(self.getApplicationContext());
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.apps_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		ignoredAppsList = (ListView)self.findViewById(R.id.managedAppsList);
        
		// Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(self.getApplicationContext());
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        ignoredAppsList.setEmptyView(progressBar);
		
        Object params[] = new Object[4];
        params[0] = (Object)self;  // activity
        params[1] = (Object)ignoredAppsList;  // list view object 
        params[2] = (Object)datasource;  // database adapter
        params[3] = (Object)true;  // specifies retrieval of ignored packages 
        new SetupUiListTask().execute(params);
        
	}

}
