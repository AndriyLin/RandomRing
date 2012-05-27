package edu.tongji.andriylin;

import java.util.HashMap;
import java.util.Map;

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

	/*
	 * singleton
	 */
	private RRDBManager() {}
	private static RRDBManager instance = null;
	public static synchronized RRDBManager get() {
		if (instance == null) {
			instance = new RRDBManager();
		}
		return instance;
	}
	
	/**
	 * insert一个铃声，存储name和uri
	 * @param context
	 * @param name
	 * @param uri
	 */
	public synchronized void insertRingtone(Context context, String name, String uri) {
		RingtonesHelper helper = new RingtonesHelper(context);
		if (helper.contains(name)) {
			helper.update(name, uri);
		}
		else {
			helper.insert(name, uri);
		}
	}
	
	/**
	 * delete 一个铃声，根据其name
	 * @param context
	 * @param name
	 */
	public synchronized void deleteRingtone(Context context, String name) {
		RingtonesHelper helper = new RingtonesHelper(context);
		helper.delete(name);
	}
	
	/**
	 * 获取全部的ringtone_name + ringtone_uri
	 * @param context
	 * @return Map<Name, Uri>
	 */
	public synchronized Map<String, String> getRingtones(Context context) {
		RingtonesHelper helper = new RingtonesHelper(context);
		return helper.selectAll();
	}

	/**
	 * 对于ringtone们的SQLite的helper类
	 * @author Andriy
	 */
	private class RingtonesHelper extends SQLiteOpenHelper {
		private static final String DB_NAME = "ringtones_andriylin_db";
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
			db.close();
			return row;
		}
		
		public Map<String, String> selectAll() {
			SQLiteDatabase db = this.getReadableDatabase();
			Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
			cursor.moveToFirst();

			HashMap<String, String> ringtones = new HashMap<String, String>();
			for (int i = 0; i < cursor.getCount(); i++) {
				ringtones.put(cursor.getString(1), cursor.getString(2));
				cursor.moveToNext();
			}

			db.close();
			return ringtones;
		}
		
		public void delete(String name) {
			SQLiteDatabase db = this.getWritableDatabase();
			String where = FIELD_NAME + " = ?";
			String[] whereValue = {name};
			db.delete(TABLE_NAME, where, whereValue);
			db.close();
		}
		
		public boolean contains(String name) {
			SQLiteDatabase db = this.getReadableDatabase();
			String[] columns = {FIELD_ID};
			String where = FIELD_NAME + " = ?";
			String[] args = {name};
			Cursor cursor = db.query(TABLE_NAME, columns, where, args, null, null, null);
			boolean isContained =cursor.getCount() != 0; 

			db.close();
			return isContained;
		}
		
		public void update(String name, String uri) {
			SQLiteDatabase db = this.getWritableDatabase();
			String where = FIELD_NAME + " = ?";
			String[] args = {name};
			
			ContentValues cv = new ContentValues();
			cv.put(FIELD_URI, uri);
			
			db.update(TABLE_NAME, cv, where, args);
			db.close();
		}
	}
	
}
