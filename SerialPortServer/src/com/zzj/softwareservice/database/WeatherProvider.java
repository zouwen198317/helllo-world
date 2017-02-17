package com.zzj.softwareservice.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class WeatherProvider extends ContentProvider {
	private SQLiteOpenHelper mOpenHelper;
	private static final UriMatcher sURLMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	private static final int ALARMS = 1;
	private static final int ALARMS_ID = 2;
	static {
		sURLMatcher.addURI(DBConstant.WEATHER_AUTHORITY, "weather", ALARMS);
		sURLMatcher
				.addURI(DBConstant.WEATHER_AUTHORITY, "weather/#", ALARMS_ID);
	}

	@Override
	public boolean onCreate() {
		mOpenHelper = WeatherDBOpenHelper.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		int match = sURLMatcher.match(uri);
		switch (match) {
		case ALARMS:
			qb.setTables(DBConstant.WeatherTable.TABLE_NAME);
			break;
		case ALARMS_ID:
			qb.setTables(DBConstant.WeatherTable.TABLE_NAME);
			qb.appendWhere("_id=");
			qb.appendWhere(uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}
		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cur = qb.query(db, projection, selection, selectionArgs, null,
				null, sortOrder);
		if (cur != null) {
			cur.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return cur;

	}

	@Override
	public String getType(Uri uri) {
		int match = sURLMatcher.match(uri);

		switch (match) {
		case ALARMS:
			return "vnd.android.cursor.dir/alarms";
		case ALARMS_ID:
			return "vnd.android.cursor.item/alarms";
		default:
			throw new IllegalArgumentException("Unknown URL");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues initialValues) {
		/*
		 * if (sURLMatcher.match(uri) != ALARMS) { throw new
		 * IllegalArgumentException("Cannot insert into URL: " + uri); }
		 * 
		 * ContentValues values; if (initialValues != null) { values = new
		 * ContentValues(initialValues); } else { values = new ContentValues();
		 * }
		 * 
		 * SQLiteDatabase db = mOpenHelper.getWritableDatabase(); long rowId =
		 * db.insert(DBConstant.WeatherTable.TABLE_NAME, null, values); if
		 * (rowId < 0) { rowId = db.insert(DBConstant.WeatherTable.TABLE_NAME,
		 * null, values); Log.e("DB","Failed to insert row into " + uri); } Uri
		 * newUrl =
		 * ContentUris.withAppendedId(DBConstant.WeatherTable.CONTENT_URI,
		 * rowId); getContext().getContentResolver().notifyChange(newUrl, null);
		 */
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		/*
		 * SQLiteDatabase db = mOpenHelper.getWritableDatabase(); int count;
		 * long rowId = 0; switch (sURLMatcher.match(uri)) { case ALARMS: count
		 * = db.delete(DBConstant.WeatherTable.TABLE_NAME, selection,
		 * selectionArgs); break; case ALARMS_ID: String segment =
		 * uri.getPathSegments().get(1); rowId = Long.parseLong(segment); if
		 * (TextUtils.isEmpty(selection)) { selection = "_id=" + rowId; } else {
		 * selection = "_id=" + rowId + " AND (" + selection + ")"; } count =
		 * db.delete(DBConstant.WeatherTable.TABLE_NAME, selection,
		 * selectionArgs); break; default: throw new
		 * IllegalArgumentException("Cannot delete from URL: " + uri); }
		 * getContext().getContentResolver().notifyChange(uri, null);
		 */
		return 0;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count;
		long rowId = 0;
		int match = sURLMatcher.match(uri);
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		switch (match) {

		case ALARMS_ID: {
			String segment = uri.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			if (selection == null) {
				count = db.update(DBConstant.WeatherTable.TABLE_NAME, values,
						"_id=" + rowId, null);
			} else {
				count = db.update(DBConstant.WeatherTable.TABLE_NAME, values,
						selection, selectionArgs);
			}
			break;
		}

		case ALARMS:
			if (selection == null) {
				count = db.update(DBConstant.WeatherTable.TABLE_NAME, values,
						null, null);
			} else {
				count = db.update(DBConstant.WeatherTable.TABLE_NAME, values,
						selection, selectionArgs);
			}
			break;

		default: {
			Log.e("navi", "Cannot update URL: " + uri);
			throw new UnsupportedOperationException("Cannot update URL: " + uri);
		}
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;

	}

}
