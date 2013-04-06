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
 * Filename:	ManagedAppAdapter.java
 * Class:		ManagedAppAdapter
 * 
 * Purpose:		Adapts the ManagedPackage class to a ListView for 
 * 				presentation by the GUI.
 */

package com.codefox421.applevels;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ManagedAppAdapter extends ArrayAdapter<ManagedPackage> {
	
	Context context;
	int layoutResourceId;
	ManagedPackage data[] = null;
	
	public ManagedAppAdapter(Context context, int layoutResourceId,
			ManagedPackage[] data) {
		super(context, layoutResourceId, data);
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "ctor");
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "ctor (end)");
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "getView");
		View row = convertView;
		PackageHolder packageHolder = null;
		
		if(row == null) {
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
			
			packageHolder = new PackageHolder();
			packageHolder.appIcon = (ImageView)row.findViewById(R.id.app_icon);
			packageHolder.packageName = (TextView)row
					.findViewById(R.id.package_name_text);
			packageHolder.volumeLevel = (TextView)row
					.findViewById(R.id.volume_level_text);
			
			row.setTag(packageHolder);
		} else {
			packageHolder = (PackageHolder)row.getTag();
		}
		
		ManagedPackage managedPackage = data[position];
		packageHolder.appIcon.setImageDrawable(managedPackage.getIcon());
		packageHolder.packageName.setText(managedPackage.getName());
		packageHolder.volumeLevel.setText(
				Integer.toString(managedPackage.getVolume()));
		return row;
	}
	
	static class PackageHolder {
		ImageView appIcon;
		TextView packageName;
		TextView volumeLevel;
	}

}
