package com.vxplo.vxshow.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.entity.User;
import com.vxplo.vxshow.util.NotificationUtil;
import com.vxplo.vxshow.util.fileupload.FileUpload;
import com.vxplo.vxshow.util.fileupload.UploadTask;
import com.vxplo.vxshow.util.upload.UploadManager;

public class ShareActivity extends VxBaseActivity {
	private boolean multiple;
	private MediaType mediaType = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		
		Intent intent = getIntent();
		String type = intent.getType();
		Log.v("ShareActivity", "MediaType: " + type);
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		String path = null;
		ArrayList<Uri> uris = null;
		ArrayList<String> paths = null;
		if (Intent.ACTION_SEND.equals(action)) {
			multiple = false;
		   if (extras.containsKey(Intent.EXTRA_STREAM)) {
			   try {
				   // Get resource path from intent
				   Uri uri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
				   // 返回路径
				   path = getRealPathFromURI(this, uri);
				   System.out.println("path-->" + path);
			   } catch (Exception e) {
				   Log.e(this.getClass().getName(), e.toString());
			   }
		   	} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
		   	}
		} else if(Intent.ACTION_SEND_MULTIPLE.equals(action)) {
			multiple = true;
			if (extras.containsKey(Intent.EXTRA_STREAM)) {
				   try {
					   // Get resource path from intent
					   paths = new ArrayList<String>();
					   uris = extras.getParcelableArrayList(Intent.EXTRA_STREAM);
					   for(Uri uri : uris) {
						   String p = getRealPathFromURI(this, uri);
						   System.out.println("path-->" + p);
						   paths.add(p);
					   }
				   } catch (Exception e) {
					   Log.e(this.getClass().getName(), e.toString());
				   }
			   	} else if (extras.containsKey(Intent.EXTRA_TEXT)) {
			   	}
			extras.getParcelableArrayList(Intent.EXTRA_STREAM);
		}
		
		if(path != null || paths != null) {
			if(type.matches("image/.*")) {
				mediaType = MediaType.IMAGE;
			}else if(type.matches("video/.*")) {
				mediaType = MediaType.VIDEO;
			}else if(type.matches("audio/.*")) {
				mediaType = MediaType.AUDIO;
			}else {
				if(!multiple)
					mediaType = getFileType(path);
				else
					mediaType = getFileType(paths);
			}
			if(User.getUserFromPref().getUid() > 0) {
				if(mediaType != null) {
					if(multiple) {
						//UploadTask.getInstance().addUploadTask(new FileUpload(getApplicationContext(), mediaType).upload(paths));
						UploadManager.getInstance().addUploadTask(new com.vxplo.vxshow.util.upload.UploadTask(mediaType, paths));
					} else {
						//UploadTask.getInstance().addUploadTask(new FileUpload(getApplicationContext(), mediaType).upload(path));
						UploadManager.getInstance().addUploadTask(new com.vxplo.vxshow.util.upload.UploadTask(mediaType, path));
					}
				}
				Intent jump = new Intent(this, MainActivity.class);
				startActivity(jump);
				VxploApplication.getInstance().removeActivity(this);
			} else {
				if(mediaType != null) {
					if(multiple) {
						//UploadTask.getInstance().addUploadTask(new FileUpload(getApplicationContext(), mediaType).upload(paths));
						UploadManager.getInstance().addUploadTaskNoUpload(new com.vxplo.vxshow.util.upload.UploadTask(mediaType, paths));
					} else {
						//UploadTask.getInstance().addUploadTask(new FileUpload(getApplicationContext(), mediaType).upload(path));
						UploadManager.getInstance().addUploadTaskNoUpload(new com.vxplo.vxshow.util.upload.UploadTask(mediaType, path));
					}
				}
				goLoginPage();
			}
		}
		
		
	}
		
	/**
	  * 通过Uri获取文件在本地存储的真实路径
	  * @param act
	  * @param contentUri
	  * @return
	  */
	 public String getRealPathFromURI(Activity act, Uri contentUri) {
		 if(contentUri.getScheme().toString().compareTo("content")==0) {
			// can post image
			 String[] proj = { MediaStore.Images.Media.DATA };
			 Cursor cursor = act.managedQuery(contentUri, proj,
					 null,
					 null,
					 null);
			 int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			 cursor.moveToFirst();
			 return cursor.getString(column_index);
		 } else if(contentUri.getScheme().compareTo("file")==0){
			 String fileName = contentUri.toString();
		       fileName = contentUri.toString().replace("file://", "");
		       return fileName;
		 }
		 return null;
	 }
	 
	 private MediaType getFileType(String path) {
		 if(path.endsWith("jpeg") || path.endsWith("png") || path.endsWith("jpg") || path.endsWith("JPEG") || path.endsWith("PNG") || path.endsWith("JPG")) {
			 return MediaType.IMAGE;
		 }else if(path.endsWith("mp4")) {
			 return MediaType.VIDEO;
		 }else if(path.endsWith("mp3")) {
			 return MediaType.AUDIO;
		 }else if(path.endsWith("html")) {
			 return MediaType.TEXT;
		 }else {
			 return null;
		 }
	 }
	 
	 /**
	  * 通过分析文件名称尾缀判断媒体类型
	  * @param paths
	  * @return
	  */
	 private MediaType getFileType(List<String> paths) {
		 String path = paths.get(0);
		 if(path.endsWith("jpeg") || path.endsWith("png") || path.endsWith("jpg") || path.endsWith("JPEG") || path.endsWith("PNG") || path.endsWith("JPG")) {
			 return MediaType.IMAGE;
		 }else if(path.endsWith("mp4")) {
			 return MediaType.VIDEO;
		 }else if(path.endsWith("mp3")) {
			 return MediaType.AUDIO;
		 }else if(path.endsWith("html")) {
			 return MediaType.TEXT;
		 }else {
			 return null;
		 }
	 }
	 
	 /**
	  * 当应用没有登录时跳转到登录界面才能上传
	  */
	 public void goLoginPage() {
			// TODO Auto-generated method stub
			Intent intent = new Intent(this, LoginActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
	}
}
