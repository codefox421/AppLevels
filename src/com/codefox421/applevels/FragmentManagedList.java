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
 * Filename:	FragmentManagedList.java
 * Class:		FragmentManagedList
 * 
 * Purpose:		Represents a UI-fragment displaying the list of
 *              managed packages.
 */

package com.codefox421.applevels;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;

public class FragmentManagedList extends Fragment {

	Activity self;
	ListView managedAppsList;
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
		
		managedAppsList = (ListView)self.findViewById(R.id.managedAppsList);
        
		// Create a progress bar to display while the list loads
        ProgressBar progressBar = new ProgressBar(self.getApplicationContext());
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT));
        progressBar.setIndeterminate(true);
        managedAppsList.setEmptyView(progressBar);
		
        Object params[] = new Object[4];
        params[0] = (Object)self;
        params[1] = (Object)managedAppsList;
        params[2] = (Object)datasource;
        params[3] = (Object)false;
        new SetupUiListTask().execute(params);
        
	}

}
