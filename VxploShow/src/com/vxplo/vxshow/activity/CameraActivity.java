package com.vxplo.vxshow.activity;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.util.FileUtil;
import com.vxplo.vxshow.util.camera.Preview;

public class CameraActivity extends Activity {
	private static final String TAG = "CameraActivity";
	Camera camera;
	private Preview preview;
	private SurfaceView surfaceView;
	MediaRecorder mMediaRecorder;
	private Button captureBtn, flashBtn;
	private ImageView recPoint, recText;
	private boolean isRecording = false;
	private boolean flashOpen = false;
	private String filePath;
	private Chronometer chronometer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_camera);
		surfaceView = (SurfaceView)findViewById(R.id.surfaceView);
		preview = new Preview(this, surfaceView);
		preview.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		((FrameLayout) findViewById(R.id.preview)).addView(preview);
		preview.setKeepScreenOn(true);
		
		captureBtn = (Button) findViewById(R.id.camera_capture_btn);
		captureBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (isRecording) { 
					stopRecord();  //停止录像并释放Camera
	            } else { 
	            	new MediaPrepareTask().execute(null, null, null);
	            }
			}
		});
		flashBtn = (Button) findViewById(R.id.flash_btn);
		chronometer = (Chronometer) findViewById(R.id.rec_chronnmeter);
		flashBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!flashOpen) {
					preview.openFlash();
					flashBtn.setBackgroundResource(R.drawable.flash);
					flashOpen = true;
				}else {
					preview.closeFlash();
					flashBtn.setBackgroundResource(R.drawable.no_flash);
					flashOpen = false;
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(filePath != null) {
			return;
		}
		camera = getCameraInstance();
		preview.setCamera(camera);
		camera.startPreview();
	}
	
	@Override
	protected void onPause() {
		if(isRecording) {
			mMediaRecorder.stop(); // 停止录像
	        releaseMediaRecorder(); // 释放MediaRecorder对象
	        camera.lock(); 
		}
		if(camera != null) {
			camera.stopPreview();
			preview.setCamera(null);
			camera.release();
			camera = null;
		}
		super.onPause();
	}
	
	/**
	 * 录像准备
	 * @return 成功则返回true，反之false
	 */
	private boolean prepareVideoRecorder(){ 
	    mMediaRecorder = new MediaRecorder(); 
	 
	    camera.unlock(); 
	    mMediaRecorder.setCamera(camera); 
	 
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER); 
	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA); 
	 
	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P)); 
	    mMediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
	    
	    mMediaRecorder.setMaxFileSize(6000000);
	    mMediaRecorder.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				// TODO Auto-generated method stub
				if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
					 stopRecord();
				}
			}
		});
	    filePath = FileUtil.getOneVxVideoPath();
	    mMediaRecorder.setOutputFile(filePath); 
	 
	    mMediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface()); 
	 
	    try { 
	        mMediaRecorder.prepare(); 
	    } catch (IllegalStateException e) { 
	        Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage()); 
	        releaseMediaRecorder(); 
	        return false; 
	    } catch (IOException e) { 
	        Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage()); 
	        releaseMediaRecorder(); 
	        return false; 
	    } 
	    return true; 
	}
	
	/**
	 * 停止录像
	 */
	public void stopRecord() {
		mMediaRecorder.stop(); // 停止录像
        releaseMediaRecorder(); // 释放MediaRecorder对象
        camera.lock();         // 将控制权从MediaRecorder 交回camera

        setCaptureButtonText(R.string.capture); 
        captureBtn.setVisibility(View.GONE);
        isRecording = false; 
        releaseCamera();
        gotoConfirm();
        finish();
	}
	
	/**
	 * 释放MediaRecoer，锁定摄像头
	 */
	private void releaseMediaRecorder(){ 
        if (mMediaRecorder != null) { 
            mMediaRecorder.reset(); // 清除recorder配置
            mMediaRecorder.release(); // 释放recorder对象
            mMediaRecorder = null; 
            camera.lock();           // 为后续使用锁定摄像头
        } 
    } 
	
	/**
	 * 停止预览，并释放Camera方便其他应用使用
	 */
	private void releaseCamera(){
        if (camera != null){
            
        	camera.stopPreview();
			preview.setCamera(null);
        	camera.release();
        	camera = null;
        }
    }

	private void showRecordAnim() {
		recPoint = (ImageView) findViewById(R.id.rec_point);
		recText = (ImageView) findViewById(R.id.rec_image);
		//findViewById(R.id.rec_anim).setVisibility(View.VISIBLE);
		recPoint.setVisibility(View.VISIBLE);
		Animation alphaAnimation = AnimationUtils.loadAnimation(this, R.anim.record_point);
		recPoint.startAnimation(alphaAnimation);
		chronometer.setBase(SystemClock.elapsedRealtime());
	    chronometer.start();
	}
	
	
	/*
	 * 得到Camera对象
	 */
	public static Camera getCameraInstance(){
	    return Camera.open();
	}
	
	private void setCaptureButtonText(String s) {
		if(null != s) {
			captureBtn.setText(s);
		}
	}
	
	private void setCaptureButtonText(int id) {
		if(id != 0) {
			captureBtn.setText(id);
		}
	}
	
	private void gotoConfirm() {
		Intent intent = new Intent(this, VideoConfirmActivity.class);
		intent.putExtra("filepath", filePath);
		startActivity(intent);
	}
	
	
	/**
     * Asynchronous task for preparing the {@link android.media.MediaRecorder} since it's a long blocking
     * operation.
     */
    class MediaPrepareTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            // initialize video camera
            if (prepareVideoRecorder()) {
                // Camera is available and unlocked, MediaRecorder is prepared,
                // now you can start recording
                mMediaRecorder.start();
                isRecording = true;
            } else {
                // prepare didn't work, release the camera
                releaseMediaRecorder();
                return false;
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result) {
                CameraActivity.this.finish();
            }
            // inform the user that recording has started
            setCaptureButtonText(R.string.stop);
            showRecordAnim();
        }
    }
}
