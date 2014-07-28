package com.vxplo.vxshow.util.fileupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.MappedByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Log;
import android.widget.Toast;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.db.VxshowSqliteOpenHelper;
import com.vxplo.vxshow.http.VxHttpCallback;
import com.vxplo.vxshow.http.VxHttpRequest;
import com.vxplo.vxshow.http.VxHttpRequest.VxHttpMethod;
import com.vxplo.vxshow.http.VxHttpTask.ErrorStatus;
import com.vxplo.vxshow.http.constant.Constant;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.NotificationUtil;

public class FileUpload {
	String[] params = new String[] {"file", "filename", "nid", "filemime", "filesize"};
	static String[] types = new String[]{"video/phone", "image/phone", "audio/phone", "Html"};
	Context context;
	MediaType mediaType;
	String filePath = null;
	List<String> paths;
	int fileSize, fileCount = 0, fileNum;
	
	VxshowSqliteOpenHelper dbHelper;
	
	
	public FileUpload(Context context, MediaType mediaType) {
		this.context = context;
		this.mediaType = mediaType;
	}
	
	public FileUpload upload(String filePath) {
		this.filePath = filePath;
		this.fileNum = 1;
		return this;
	}
	
	public FileUpload upload(List<String> paths) {
		
		this.fileNum = paths.size();
		this.paths = paths;
		return this;
	}
	
	public void execute() {
		NotificationUtil.notifyUploading();
		if(paths != null) {
			new UploadAsyncTasks(paths.get(fileCount)).execute();
		}else if(filePath != null){
			new UploadAsyncTasks(filePath).execute();
		}
	}
	
	class UploadAsyncTasks extends AsyncTask<String, Integer, String>
	{
		String filePath;
		public UploadAsyncTasks(String filePath) {
			this.filePath = filePath;
		}
		@Override
		protected String doInBackground(String... params) {
			if(filePath == null)
				return null;
			try {
				File  file = new File(filePath);
				FileInputStream inputFile = new FileInputStream(file);
				fileSize = (int) file.length();
				byte[] buffer = new byte[fileSize];
				inputFile.read(buffer);
				        inputFile.close();
				        return Base64.encodeToString(buffer,Base64.DEFAULT);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			VxHttpRequest uploadRequest = new VxHttpRequest(context, VxHttpMethod.POST, Constant.getFileUploadUrl(), params, new String[] {result, getName(filePath), "0", types[mediaType.ordinal()], fileSize+""});
			uploadRequest.setCallback(new VxHttpCallback() {
				
				@Override
				public void success(String result) throws Exception {
					// TODO Auto-generated method stub
					storeUploadPath(filePath);
					fileCount++;
					String done = VxploApplication.resources.getString(R.string.has_finished, fileCount, fileNum);
					Toast.makeText(VxploApplication.getInstance(), done, Toast.LENGTH_SHORT).show();
					//DialogUtil.showToast(context, "已完成" + fileCount + "(个)/" + fileNum);
					if(fileCount >= fileNum) {
						NotificationUtil.notifyUploadingOk();
						UploadTask.getInstance().removeUploadTask(FileUpload.this);
					}else {
						new UploadAsyncTasks(paths.get(fileCount)).execute(); 
					}
				}
				
				@Override
				public void error(ErrorStatus status, Exception... exs) {
					NotificationUtil.cancleNotify(NotificationUtil.NOTIFY_UPLOADING);
					UploadTask.getInstance().removeUploadTask(FileUpload.this);
					if (exs.length > 0) {
						DialogUtil.showToast(context, exs[0].getMessage());
						return;
					}
					DialogUtil.showToast(context, status.name());
				}
				
				@Override
				public void complete() {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beforeSend() {
					// TODO Auto-generated method stub
					
				}
			});
			uploadRequest.send();
			super.onPostExecute(result);
			result = null;
		}
	}
	
	/**
	 * 截取文件的名字,名字中添加当前时间
	 * @param path
	 * @return
	 */
	private String getName(String path) {
		int po = path.lastIndexOf("/");
		String name = path.substring(po + 1);
		if(mediaType == MediaType.AUDIO) {
			return name;
		}
		SimpleDateFormat simDateFormate = new SimpleDateFormat("yyyy-MM-dd-hh:mm:ss");
		String date = simDateFormate.format(new Date());
		int point = name.lastIndexOf(".");
		String str1 = name.substring(0, point);
		String str2 = name.substring(point);
		return str1 + "(" + date + ")" + str2;
	}
	
	
	public void storeUploadPath(String path) {
		dbHelper = new VxshowSqliteOpenHelper(context);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		try {
			db.execSQL("insert into uploadfile values (null,?,?,?)", new Object[]{path, new Date().toString(), mediaType.name()});
			db.setTransactionSuccessful();
		} catch(SQLException e) {
			e.printStackTrace();
		}finally {
			db.endTransaction();
		}
	}

}
