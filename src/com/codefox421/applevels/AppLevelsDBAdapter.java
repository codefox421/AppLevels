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
 * Filename:	AppLevelsDBAdapter.java
 * Class:		AppLevelsDBAdapter
 * 
 * Purpose:		Manages CRUD operations on the SQLite database in support of
 * 				AppLevels.
 */

package com.codefox421.applevels;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class AppLevelsDBAdapter {

	// Database fields
	private static final String LOG_TAG = "AppLevelsDBAdap";
	private Context context;
	private SQLiteDatabase database;
	private AppLevelsDBHelper dbHelper;
	
	
	public AppLevelsDBAdapter(Context context) {
		this.context = context;
	}
	
	
	public AppLevelsDBAdapter open() throws SQLException {
		dbHelper = new AppLevelsDBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	
	public void close() {
		dbHelper.close();
	}
	
	
	/**
	 * VOLUME RECORDS
	 *   package
	 *   volume
	 */
	
	public boolean updateAppVolume(String packageName, int volumeLevel) {
		
		// Compile values
		ContentValues valuesToUpdate = new ContentValues();
		valuesToUpdate.put(AppLevelsDBHelper.KEY_VOLUME, volumeLevel);
		
		// Write to database
		boolean updateSucceeded = false;
		try {
			updateSucceeded = database.update(AppLevelsDBHelper.VOLUME_TABLE, valuesToUpdate,
					AppLevelsDBHelper.KEY_PACKAGE + "='" + packageName + "'", null) > 0;
			if ( !updateSucceeded )
				throw new SQLiteException();
		} catch(SQLiteException ex_up) {
			Log.w(LOG_TAG, "Could not update volume record for " + packageName + "\nAttempting new entry...");
			try {
				valuesToUpdate.put(AppLevelsDBHelper.KEY_PACKAGE, packageName);
				updateSucceeded = database.insertOrThrow(AppLevelsDBHelper.VOLUME_TABLE, null, valuesToUpdate) != -1;
			} catch(SQLiteException ex_in) {
				//well damn, it failed twice
				Log.e(LOG_TAG, "SQLite error while inserting new record for " + packageName);
			}
		}
		return updateSucceeded;
	}
	
	
	public int getAppVolume(String packageName) throws SQLException {
		// Values less than 0 indicate an error
		
		// Query the database
		Cursor mCursor;
		try {
			mCursor = database.query(true, AppLevelsDBHelper.VOLUME_TABLE, new String[] { AppLevelsDBHelper.KEY_VOLUME },
					AppLevelsDBHelper.KEY_PACKAGE + "='" + packageName + "'", null, null, null, null, null);
		} catch(SQLiteException ex) {
			Log.e(LOG_TAG, "SQLite exception while querying " + packageName);
			return -1;		//query error
		} catch(Exception ex) {
			Log.e(LOG_TAG, "Unknown exception while querying " + packageName);
			return -3;		//unknown error
		}
		
		// Verify cursor and extract value
		if (mCursor != null && mCursor.getCount() > 0) {
			mCursor.moveToFirst();
			int value = mCursor.getInt(mCursor.getColumnIndex(AppLevelsDBHelper.KEY_VOLUME));
			mCursor.close();
			return value;
		}
		
		Log.w(LOG_TAG, "Received null cursor from query for " + packageName);
		return -2;		//null cursor error
	}
	
	
	public Cursor GetAppVolumes() throws SQLException {
		
		// Query the database
		Cursor mCursor;
		try {
			mCursor = database.query(AppLevelsDBHelper.VOLUME_TABLE, new String[] { AppLevelsDBHelper.KEY_PACKAGE, AppLevelsDBHelper.KEY_VOLUME },
					null, null, null, null, null);
		} catch(SQLiteException ex) {
			Log.e(LOG_TAG, "SQLite exception while querying packages.");
			return null;
		}
		
		// Verify cursor
		if (mCursor != null) {
			mCursor.moveToFirst();
		} else {
			Log.e(LOG_TAG, "Received mull cursor from querying for packages.");
		}
		
		return mCursor;
	}
	
	
	public boolean deleteAppVolume(String packageName) {
		
		boolean deleteSucceeded = false;
		try {
			deleteSucceeded = database.delete(AppLevelsDBHelper.VOLUME_TABLE, AppLevelsDBHelper.KEY_PACKAGE + "='" + packageName + "'", null) > 0;
		} catch(SQLiteException ex) {
			Log.e(LOG_TAG, "SQLite exception while deleting " + packageName);
		}
		return deleteSucceeded;
	}
}
