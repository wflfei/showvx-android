package com.vxplo.vxshow.activity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vxplo.vxshow.util.fileupload.FileUpload;
import com.vxplo.vxshow.util.fileupload.UploadTask;
import com.vxplo.vxshow.util.mp3recorder.AudioRecorder2Mp3Util;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.FileUtil;
import com.vxplo.vxshow.util.NotificationUtil;
import com.vxplo.vxshow.util.mp3recorder.Mp3Recorder;

public class RecordActivity extends VxBaseActivity {
	private final static int MENU_ID_DELETE = 0;
	private final static int RECORDING = 1;
	private final static int TRANSFERING = 2;
	private final static int RECORDEND = 3;
	private List<String> recordPaths;
	private List<String> recordNames;
	private ListView listview;
	private Button recordBtn;
	private Chronometer chronometer;
	private ImageView recordAnimImage;
	private AnimationDrawable anim;
	private ArrayAdapter<String> arrayAdapter;
	MediaRecorder mRecorder;
	private MediaPlayer mPlayer;
	private int mPosition;
	String mVoiceFileName;
	private boolean isRecording = false;
	private int recordingState = RECORDEND;
	private Mp3Recorder mp3Recorder;
	
	String recordString;
	String convertString;
	String stopString;
	String rawPath;
	String mp3Path;
	private boolean canClean = false;
	AudioRecorder2Mp3Util util = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		initActionBar();
		findViews();
		initViews();
		
		recordString = resources.getString(R.string.click_record);
		convertString = resources.getString(R.string.record_converting);
		stopString = resources.getString(R.string.click_stop);
		mp3Recorder = new Mp3Recorder();
	}

	private void initActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
	}

	private void findViews() {
		// TODO Auto-generated method stub
		listview = (ListView) findViewById(R.id.record_list);
		recordBtn = (Button) findViewById(R.id.record_btn);
		chronometer = (Chronometer) findViewById(R.id.record_chronnmeter);
		recordAnimImage = (ImageView) findViewById(R.id.record_anim);
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		initRecordsData();
		initListView();
		anim = (AnimationDrawable) recordAnimImage.getDrawable();
		recordBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(recordingState == RECORDING) {
					stopRecord();
				}else if(recordingState == RECORDEND){
					startRecord();
				} else if(recordingState == TRANSFERING) {
					
				}
			}
		});
		
	}

	private void initListView() {
		// TODO Auto-generated method stub
		arrayAdapter = new ArrayAdapter<>(mApplication,R.layout.record_list_item, R.id.record_list_item_text, recordNames);
		listview.setAdapter(arrayAdapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final ImageView iv = (ImageView) view.findViewById(R.id.record_list_item_play);
				final AnimationDrawable am = (AnimationDrawable) iv.getDrawable();
				if(mPlayer == null || !mPlayer.isPlaying()) {
					mPosition = position;
					mPlayer = new MediaPlayer();
					mPlayer.setOnCompletionListener(new OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							mPlayer.release();
							mPlayer = null;
							am.stop();
							iv.setVisibility(View.INVISIBLE);
						}
					});
					try {
						mPlayer.reset();
						mPlayer.setDataSource(recordPaths.get(position));
						mPlayer.prepare();
						mPlayer.start();
						iv.setVisibility(View.VISIBLE);
						am.start();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalStateException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else if(position == mPosition){
					mPlayer.release();
					mPlayer = null;
					am.stop();
					iv.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		listview.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v,
					ContextMenuInfo menuInfo) {
				menu.add(0, MENU_ID_DELETE, MENU_ID_DELETE, resources.getString(R.string.delete));
			}
		});
	}

	
	// start of record
	private void initRecordsData() {
		// TODO Auto-generated method stub
		if(recordPaths == null) {
			recordPaths = FileUtil.listRecords();
		}
		if(recordNames == null) {
			recordNames = new ArrayList<String>();
		}else {
			recordNames.clear();
		}
		for(int i=0; i<recordPaths.size(); i++) {
			String path = recordPaths.get(i);
			int po = path.lastIndexOf("/");
			String name = path.substring(po + 1);
			recordNames.add(name);
		}
	}
	
	private void startRecord()
	{
		String recordsDir = FileUtil.getVxRecordsDir();
		if(recordsDir == null) {
			Toast.makeText(mApplication, R.string.no_sdcard_tip, Toast.LENGTH_SHORT).show();
			return;
		}
		Date date = new Date();
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd-HHmmss");
		String str = formater.format(date);
		rawPath = recordsDir + File.separator + "audio" + str + ".raw";
		mp3Path = recordsDir + File.separator + "audio" + str + ".mp3";
		
		//if (util == null) {
			util = new AudioRecorder2Mp3Util(null, rawPath, mp3Path,
					new ConvertCompleteCallBack() {
						
						@Override
						public void onComplete() {
							//util.cleanFile(AudioRecorder2Mp3Util.RAW);
							// 如果要关闭可以
							if(RecordActivity.this.isFinishing()) {
								return;
							}
							isRecording = false;
							recordingState = RECORDEND;
							util = null;
							recordBtn.setText(recordString);
							showRecordUploadDialog(mp3Path);
						}
					});
			
		//}
		util.cleanFile(AudioRecorder2Mp3Util.RAW);
		Toast.makeText(this, "请说话", 0).show();

		util.startRecording();
		recordingState = RECORDING;
		canClean = true;
		recordAnimImage.setVisibility(View.VISIBLE);
		anim.start();
		recordBtn.setText(stopString);
		isRecording = true;
		chronometer.setBase(SystemClock.elapsedRealtime());
	    chronometer.start();  //开始计数
	}
	
	
	private void stopRecord() {
		Toast.makeText(this, "正在转换", 0).show();
		util.stopRecordingAndConvertFile();
		recordingState = TRANSFERING;
		chronometer.stop();
		recordBtn.setText(convertString);
		recordAnimImage.setVisibility(View.INVISIBLE);
		anim.stop();
		util.close();
	}
	// end of record
	
	/**
	 * 录音完成后显示对话框选项
	 * @param recordPath
	 */
	public void showRecordUploadDialog(final String recordPath) {
		final AlertDialog dialog = new AlertDialog.Builder(ctx).create();
		View view =  LayoutInflater.from(ctx).inflate(R.layout.record_upload_dialog, null);
		TextView recordNameText = (TextView) view.findViewById(R.id.record_dialog_text);
		Button cancelBtn = (Button) view.findViewById(R.id.record_dialog_cancel);
		Button playBtn = (Button) view.findViewById(R.id.record_dialog_play);
		Button uploadBtn = (Button) view.findViewById(R.id.record_dialog_upload);
		int po = recordPath.lastIndexOf("/");
		String name = recordPath.substring(po + 1);
		recordNameText.setText(String.format(resources.getString(R.string.record_name), name));
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				dialog.cancel();
			}
		});
		uploadBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				recordPaths.add(mp3Path);
				initRecordsData();
				arrayAdapter.notifyDataSetChanged();
				UploadTask.getInstance().addUploadTask(new FileUpload(ctx, MediaType.AUDIO).upload(mp3Path));
			}
		});
		playBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				recordPlay(recordPath);
			}
		});
		dialog.setView(view);
		dialog.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				deleteRecord(recordPath);
				if(mPlayer != null ) {
					try {
						mPlayer.release();
						mPlayer = null;
					} catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		});
		dialog.show();
		
	}
	
	/**
	 * 播放录音
	 * @param recordPath
	 */
	private void recordPlay(String recordPath) {
		if(mPlayer != null) {
			try {
				mPlayer.release();
				mPlayer = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		mPlayer = new MediaPlayer();
		try {
			mPlayer.reset();
			mPlayer.setDataSource(recordPath);
			mPlayer.prepare();
			mPlayer.start();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 长按录音列表选中
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		int position = info.position;
		int id = item.getItemId();
		if(id == MENU_ID_DELETE) {
			String filePath = recordPaths.get(position);
			deleteRecord(filePath);
			recordPaths.remove(position);
			initRecordsData();
			arrayAdapter.notifyDataSetChanged();
		}
		return super.onContextItemSelected(item);
	}
	
	private void deleteRecord(String filePath) {
		File file = new File(filePath);
		if(file.exists()) {
			file.delete();
		}
	}
	
	@Override
	protected void onPause() {
		if(recordingState == RECORDING) {
			util.stopRecord();
			util.cleanFile(util.RAW);
			//util.transTask.setRunning(false);
			util.transTask.cancel(true);
		}else if(recordingState == TRANSFERING){
			util.cleanFile(util.RAW | util.MP3);
			//util.transTask.setRunning(false);
			util.transTask.cancel(true);
		}
		if(mPlayer != null ) {
			try {
				mPlayer.release();
				mPlayer = null;
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		if(util != null) {
			util.close();
			util = null;
		}
		super.onDestroy();
	}
	
	public interface ConvertCompleteCallBack {
		public void onComplete();
	}
	
	@Override
   	public boolean onOptionsItemSelected(MenuItem item) {
   		// TODO Auto-generated method stub
   		switch (item.getItemId()) {
   		case android.R.id.home:
   			finish();
   			return true;

   		}
   		return true;
   	}
	
	/*
	private void startRecord()
	{
		String bbsDir = FileUtil.getVxRecordsDir();
		Date date = new Date();
		SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = formater.format(date);
		mVoiceFileName = bbsDir + "/audio" + str + ".amr";
		Log.d("Voice File Name", mVoiceFileName);
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
		mRecorder.setOutputFile(mVoiceFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
		mRecorder.setMaxDuration(30000);
		mRecorder.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public void onInfo(MediaRecorder mr, int what, int extra) {
				if(what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
					stopRecord();
					
				}
			}
		});
		
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e("", "prepare() failed");
		}
		mRecorder.start();
		recordAnimImage.setVisibility(View.VISIBLE);
		anim.start();
		recordBtn.setText("点击结束录音");
		isRecording = true;
		chronometer.setBase(SystemClock.elapsedRealtime());
	    chronometer.start();  //开始计数
	}
	*/
	/*
	private void stopRecord() {
		try {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		chronometer.stop();
		isRecording = false;
		recordBtn.setText("点击开始录音");
		recordAnimImage.setVisibility(View.INVISIBLE);
		anim.stop();
		recordPaths.add(mVoiceFileName);
		initRecordsData();
		arrayAdapter.notifyDataSetChanged();
		new FileUpload(ctx, MediaType.AUDIO).upload(mVoiceFileName);
		NotificationUtil.notifyUploading();
	}

*/

}
