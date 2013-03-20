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
 * Filename:	AppLevelsDBHelper.java
 * Class:		AppLevelsDBHelper
 * 
 * Purpose:		Manages SQLite database creation and update. Tracks database
 * 				version for database update triggering.
 */

package com.codefox421.applevels;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class AppLevelsDBHelper extends SQLiteOpenHelper {
	
	public static final String LOG_TAG = "AppLevelsDBHelp";
	public static final String VOLUME_TABLE = "volume_records";
	public static final String KEY_ID = "id";
	public static final String KEY_PACKAGE = "_id";
	public static final String KEY_VOLUME = "volume";
	public static final String KEY_IGNORE = "ignore";
	
	private static final String DATABASE_NAME = "applevels_appdata";

	private static final int DATABASE_VERSION = 1;

	
	// Create database for volume levels
	private static final String VOLUME_DB_CREATE = "create table " + VOLUME_TABLE + " ("
			+ KEY_PACKAGE + " text primary key, "
			+ KEY_VOLUME + " integer not null,"
			+ KEY_IGNORE + " integer);";


	// Constructor
	public AppLevelsDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	// Called when first created, creates databases tables if they do not exist
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(VOLUME_DB_CREATE);
		
	}


	// Called during an update of the database (if you increase the database version)
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(AppLevelsDBHelper.class.getName(),
				"AppLevels: Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which may destroy data...");
		// TODO: enact database upgrade changes
	}
}
