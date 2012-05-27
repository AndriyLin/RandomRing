package edu.tongji.andriylin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 操作SQLite数据库，存储已选择的铃声等信息
 * @author Andriy
 */
public class RRDBManager {

	
	private class RingtonesHelper extends SQLiteOpenHelper {
		private static final String DB_NAME = "ringtones_andriy_db";
		private static final int DB_VERSION = 1;
		private static final String TABLE_NAME = "ringtones_list_table";
		private static final String FIELD_ID = "_id";
		private static final String FIELD_NAME = "ringtone_name";
		private static final String FIELD_URI = "ringtone_uri";
		
		public RingtonesHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "CREATE TABLE " + TABLE_NAME + " (" + FIELD_ID
					+ " INTEGER primary key autoincrement,  " + FIELD_NAME
					+ " text,  " + FIELD_URI + " text)";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "DROP TABLE IF EXISTS " + TABLE_NAME;
			db.execSQL(sql);
			onCreate(db);
		}
		
		public long insert(String name, String uri) {
			SQLiteDatabase db = this.getWritableDatabase();
			
			ContentValues cv = new ContentValues();
			cv.put(FIELD_NAME, name);
			cv.put(FIELD_URI, uri);

			long row = db.insert(TABLE_NAME, null, cv);
			return row;
		}
		
		public Cursor selectAll() {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
			return cursor;
		}
		
		public void delete(int id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String where = FIELD_ID + " = ?";
			String[] whereValue = {Integer.toString(id)};
			
			db.delete(TABLE_NAME, where, whereValue);
		}
		
		public void update (int id, String name, String uri) {
			SQLiteDatabase db = this.getWritableDatabase();
			String where = FIELD_ID + " = ?";
			String[] whereValue = {Integer.toString(id)};
			
			ContentValues cv = new ContentValues();
			cv.put(FIELD_NAME, name);
			cv.put(FIELD_URI, uri);

			db.update(TABLE_NAME, cv, where, whereValue);
		}
	}
}
