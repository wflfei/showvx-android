package com.vxplo.vxshow.db;

import java.util.ArrayList;
import java.util.List;

import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.entity.History;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class VxshowDbManager {
	VxshowSqliteOpenHelper dbHelper;

	public VxshowDbManager(Context context) {
		dbHelper = new VxshowSqliteOpenHelper(context);
	}
	
	public List<String> getUploadPath() {
		List<String> paths = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction(); 
		Cursor cursor = db.rawQuery("select path from uploadfile", null);
		
		while(cursor.moveToNext()){
			paths.add(cursor.getString(0));
		}
		db.endTransaction(); 
		cursor.close();
		db.close();
		return paths;
	}
	
	public void cleanUploadHistory(MediaType type) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			if(type == MediaType.VIDEO) {
				//db.execSQL("delete from uploadfile where mediatype = ?", new String[] {"VIDEO"});
				int result = db.delete("uploadfile", "mediatype='VIDEO'", null);
				Log.v("Video Rows", result + "");
			} else if(type == MediaType.IMAGE) {
				int result = db.delete("uploadfile", "mediatype='IMAGE'", null);
				Log.v("Video Rows", result + "");
			} else if(type == MediaType.AUDIO) {
				//db.execSQL("delete from uploadfile where mediatype = ?", new String[] {"AUDIO"});
				int result = db.delete("uploadfile", "mediatype='AUDIO'", null);
				Log.v("Rows", result + "");
			}
			db.setTransactionSuccessful();
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction(); 
			db.close();
		}
		
	}
	
	public List<History> getImageUploadPath() {
		List<History> historys = new ArrayList<History>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction(); 
		Cursor cursor = db.rawQuery("select path, uptime, mediatype from uploadfile where mediatype = ?", new String[] {"IMAGE"});
		
		while(cursor.moveToNext()){
			History hi = new History();
			hi.setPath(cursor.getString(0));
			hi.setUptime(cursor.getString(1));
			hi.setType(cursor.getString(2));
			historys.add(hi);
		}
		db.endTransaction(); 
		cursor.close();
		db.close();
		return historys;
	}
	
	public List<History> getVideoUploadPath() {
		List<History> historys = new ArrayList<History>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction(); 
		Cursor cursor = db.rawQuery("select path, uptime, mediatype from uploadfile where mediatype = ?", new String[] {"VIDEO"});
		
		while(cursor.moveToNext()){
			History hi = new History();
			hi.setPath(cursor.getString(0));
			hi.setUptime(cursor.getString(1));
			hi.setType(cursor.getString(2));
			historys.add(hi);
		}
		db.endTransaction(); 
		cursor.close();
		db.close();
		return historys;
	}
	
	public List<History> getRecordUploadPath() {
		List<History> historys = new ArrayList<History>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction(); 
		Cursor cursor = db.rawQuery("select path, uptime, mediatype from uploadfile where mediatype = ?", new String[] {"AUDIO"});
		
		while(cursor.moveToNext()){
			History hi = new History();
			hi.setPath(cursor.getString(0));
			hi.setUptime(cursor.getString(1));
			hi.setType(cursor.getString(2));
			historys.add(hi);
		}
		db.endTransaction(); 
		cursor.close();
		db.close();
		return historys;
	}

}
