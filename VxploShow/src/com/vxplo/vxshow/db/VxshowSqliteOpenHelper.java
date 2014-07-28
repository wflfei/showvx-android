package com.vxplo.vxshow.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class VxshowSqliteOpenHelper extends SQLiteOpenHelper {
	private final static String TABLE_CREATE = "CREATE TABLE IF NOT EXISTS uploadfile(_id INTEGER PRIMARY KEY AUTOINCREMENT, path TEXT,uptime TEXT,mediatype TEXT);";
	private static final String DBNAME = "upload.db";
	private static final int VERSION = 1;
	
	/**
	 * ¹¹ÔìÆ÷
	 * @param context
	 */
	public VxshowSqliteOpenHelper(Context context) {
		super(context, DBNAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS uploadfile;");
		onCreate(db);
	}

}
