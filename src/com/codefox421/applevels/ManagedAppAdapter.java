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
 * Purpose:		Maps cursor data to a list row View.
 */

package com.codefox421.applevels;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ManagedAppAdapter extends CursorAdapter {
	
	public ManagedAppAdapter(Context context, Cursor cursor, int flags) {
		super(context, cursor, flags);
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// package name
		TextView name = (TextView)view.findViewById(R.id.package_name_text);
		name.setText(cursor.getString(
				cursor.getColumnIndex(AppLevelsDBHelper.KEY_PACKAGE)));
		// volume level
		TextView level = (TextView)view.findViewById(R.id.volume_level_text);
		level.setText("" + cursor.getInt(
				cursor.getColumnIndex(AppLevelsDBHelper.KEY_VOLUME)));
		// application icon
		Drawable appIcon;
		try {
			appIcon = context.getPackageManager().getApplicationIcon(cursor.getString(
					cursor.getColumnIndex(AppLevelsDBHelper.KEY_PACKAGE)));
		} catch(Exception exception) {
			appIcon = context.getResources().getDrawable(R.drawable.default_app);
		}
		ImageView icon = (ImageView)view.findViewById(R.id.app_icon);
		icon.setImageDrawable(appIcon);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.app_entry, parent, false);
		bindView(v, context, cursor);
		return v;
	}

}
