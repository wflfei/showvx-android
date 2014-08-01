package com.vxplo.vxshow.util.upload;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.asynchttp.VxHttpClient;
import com.vxplo.vxshow.db.VxshowSqliteOpenHelper;
import com.vxplo.vxshow.util.NotificationUtil;

public class UploadTask {
	String[] params = new String[] {"file", "filename", "nid", "filemime", "filesize"};
	static String[] types = new String[]{"video/phone", "image/phone", "audio/phone", "Html"};
	Context context;
	MediaType mediaType;
	String filePath = null;
	List<String> paths;
	int fileSize, fileCount = 0, fileNum;
	
	VxshowSqliteOpenHelper dbHelper;
	
	
	public UploadTask(MediaType mediaType, String filePath) {
		this.context = VxploApplication.getInstance();
		this.mediaType = mediaType;
		this.filePath = filePath;
		this.fileNum = 1;
	}
	
	public UploadTask(MediaType mediaType, List<String> paths) {
		this.context = VxploApplication.getInstance();
		this.mediaType = mediaType;
		this.fileNum = paths.size();
		this.paths = paths;
	}
	
	public void execute() {
		
		if(paths != null) {
			NotificationUtil.notifyUploadingPro(fileNum);
			postUpload(paths.get(0));
		}else if(filePath != null){
			NotificationUtil.notifyUploadingPro();
			postUpload(filePath);
		}
	}
	
	private void postUpload(final String fileName) {
		
		String url = "http://lin-pc:8080/FileReceiver/servlet/ReceiveFile";
		
		File file = new File(fileName);
		
		if(file.exists() && file.length() > 0){
			RequestParams params = new RequestParams();
			try {
				//params.put("profile_picture", file);
				params.put("profile-image", file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			VxHttpClient.post(url, params, true, new AsyncHttpResponseHandler(){
				int step = 5, lastSize = 0;
				@Override
				public void onFailure(int arg0, Header[] arg1,
						byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub
					Toast.makeText(context, "上传失败", Toast.LENGTH_SHORT).show();
					Log.v("UploadActivity", "上传失败");
					UploadManager.getInstance().removeUploadTask(UploadTask.this);
					NotificationUtil.notifyUploadingFailed();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1,
						byte[] arg2) {
					storeUploadPath(fileName);
					fileCount++;
					//String done = VxploApplication.resources.getString(R.string.has_finished, fileCount, fileNum);
					//Toast.makeText(VxploApplication.getInstance(), done, Toast.LENGTH_SHORT).show();
					//DialogUtil.showToast(context, "已完成" + fileCount + "(个)/" + fileNum);
					if(fileCount >= fileNum) {
						NotificationUtil.notifyUploadingOk();
						UploadManager.getInstance().removeUploadTask(UploadTask.this);
						//UploadTask.getInstance().removeUploadTask(FileUpload.this);
					}else {
						NotificationUtil.setProgress(fileCount, fileNum);
						postUpload(paths.get(fileCount)); 
					}
				}
				
				
				@Override
				public void onProgress(int bytesWritten,
						int totalSize) {
					if(paths != null) {
						return;
					}
					int now = (int)(bytesWritten * 100 / totalSize);
					if(now - lastSize >= step) {
						NotificationUtil.setProgress(now);
						lastSize = now;
					}
					// TODO Auto-generated method stub
					//super.onProgress(bytesWritten, totalSize);
				}
			});
		}else {
			Toast.makeText(context, "文件不存在", 0).show();
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
