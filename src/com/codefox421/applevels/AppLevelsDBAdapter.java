package com.codefox421.applevels;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class AppLevelsDBAdapter {

	// Database fields
	private static final String VOLUME_TABLE = "volume_records";
	public static final String KEY_PACKAGE = "package";
	public static final String KEY_VOLUME = "volume";
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
	
	public boolean updateAppVolume(String packageName, String volumeLevel) {
		
		// Compile values
		ContentValues valuesToUpdate = new ContentValues();
		valuesToUpdate.put(KEY_VOLUME, volumeLevel);
		
		// Write to database
		boolean updateSucceeded = database.update(VOLUME_TABLE, valuesToUpdate,
				KEY_PACKAGE + "=" + packageName, null) > 0;
		if(updateSucceeded)
			return true;
		return database.insert(VOLUME_TABLE, null, valuesToUpdate) != -1;
	}
	
	
	public int getAppVolume(String packageName) throws SQLException {
		
		// Query the database
		Cursor mCursor = database.query(true, VOLUME_TABLE, new String[] { KEY_VOLUME },
				KEY_PACKAGE + "=" + packageName, null, null, null, null, null);
		
		// Verify cursor and extract value
		if (mCursor != null) {
			int value = mCursor.getInt(mCursor.getColumnIndex(KEY_VOLUME));
			mCursor.close();
			return value;
		}
		
		// Values less than 0 indicate an error
		return -1;
	}
	
	
	public Cursor GetAppVolumes() throws SQLException {
		
		// Query the database
		Cursor mCursor = database.query(VOLUME_TABLE, new String[] { KEY_PACKAGE, KEY_VOLUME },
				null, null, null, null, null);
		
		// Verify cursor
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		
		return mCursor;
	}
	
	
	public boolean deleteAppVolume(String packageName) {
		
		return database.delete(VOLUME_TABLE, KEY_PACKAGE + "=" + packageName, null) > 0;
	}
}
