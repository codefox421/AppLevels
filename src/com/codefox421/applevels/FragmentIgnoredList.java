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

import android.database.Cursor;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.MenuItem;

public class FragmentIgnoredList extends FragmentAppList {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//Log.d("AppLevels:" + this.getClass().getSimpleName(), "onCreate");

		getIgnored = true;
		cabMenuId = R.menu.ignored_cab;
		
		super.onCreate(savedInstanceState);
	}

	@Override
	protected boolean handleActionItemClick(ActionMode mode, MenuItem item) {
		// respond to clicks on the actions in the CAB
		switch (item.getItemId()) {
		case R.id.cab_manage:
			manageSelected();
			mode.finish();
			return true;
		case R.id.cab_delete:
			deleteSelected();
			mode.finish();
			return true;
		default:
			return false;
		}
	}
	
	private void manageSelected() {
		datasource.open();
		SparseBooleanArray checked = managedAppsList.getCheckedItemPositions();
		for (int k = 0; k < managedAppsList.getCount(); k++) {
			if (checked.valueAt(k)) {
				Cursor cursor = (Cursor) managedAppsList.getItemAtPosition(k);
				datasource.setIgnoredBit(cursor.getString(
						cursor.getColumnIndex(AppLevelsDBHelper.KEY_PACKAGE)), false);
			}
		}
		super.invalidate();
		datasource.close();
	}

}
