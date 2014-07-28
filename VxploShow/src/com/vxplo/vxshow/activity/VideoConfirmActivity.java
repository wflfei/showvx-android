package com.vxplo.vxshow.activity;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.R.layout;
import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.util.FileUtil;
import com.vxplo.vxshow.util.MediaUtil;
import com.vxplo.vxshow.util.fileupload.FileUpload;
import com.vxplo.vxshow.util.fileupload.UploadTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class VideoConfirmActivity extends VxBaseActivity {
	private Button cancelBtn, okBtn, videoPlayBtn;
	private String filePath;
	private Bitmap mBitmap;
	private ImageView imageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_confirm);
		Intent intent = getIntent();
		filePath = intent.getStringExtra("filepath");
		if(filePath == null) {
			finish();
		}
		cancelBtn = (Button) findViewById(R.id.cancel_btn);
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				Intent intent = new Intent(VideoConfirmActivity.this, CameraActivity.class);
//				startActivity(intent);
				FileUtil.deleteFile(filePath);
				finish();
			}
		});
		
		okBtn = (Button) findViewById(R.id.ok_btn);
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UploadTask.getInstance().addUploadTask(new FileUpload(getApplicationContext(), MediaType.VIDEO).upload(filePath));
				finish();
			}
		});
		videoPlayBtn = (Button) findViewById(R.id.video_play_btn);
		videoPlayBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Uri uri = Uri.parse(filePath);   
				//调用系统自带的播放器  
			    Intent intent = new Intent(Intent.ACTION_VIEW);  
			    Log.v("URI:::::::::", uri.toString());  
			    intent.setDataAndType(uri, "video/mp4");  
			    startActivity(intent);
			}
		});
		
		mBitmap = MediaUtil.getVideoThumbnail(filePath, 0, 0, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
		imageView = (ImageView) findViewById(R.id.video_confirm_thumb);
		imageView.setImageBitmap(mBitmap);
	}
	
}
