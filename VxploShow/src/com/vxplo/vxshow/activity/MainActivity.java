package com.vxplo.vxshow.activity;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.slidingmenu.lib.SlidingMenu;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.asynchttp.VxHttpClient;
import com.vxplo.vxshow.entity.User;
import com.vxplo.vxshow.fragment.ProjectListFragment2;
import com.vxplo.vxshow.http.VxHttpCallback;
import com.vxplo.vxshow.http.VxHttpRequest;
import com.vxplo.vxshow.http.VxHttpRequest.VxHttpMethod;
import com.vxplo.vxshow.http.VxHttpTask.ErrorStatus;
import com.vxplo.vxshow.http.constant.Constant;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.FileUtil;
import com.vxplo.vxshow.util.NetworkUtil;
import com.vxplo.vxshow.util.NotificationUtil;
import com.vxplo.vxshow.util.PopMenuUtil;
import com.vxplo.vxshow.util.fileupload.FileUpload;
import com.vxplo.vxshow.util.fileupload.UploadTask;
import com.vxplo.vxshow.util.imageloader.MD5;
import com.vxplo.vxshow.util.upload.UploadManager;
import com.vxplo.vxshow.util.zxing.CaptureActivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends VxBaseActivity {
	private final static String TAG = "MainActivity";
	public enum ListType {
		INSPIRED, PROJECTS, FAVORITES
	}
	public enum MediaType {
		VIDEO, IMAGE, AUDIO, TEXT
	}
	private final static int REQUEST_CODE_TAKE_VIDEO = 1;
	private final static int REQUEST_CODE_TAKE_PHOTO = 2;
	private final static int REQUEST_CODE_SELECT_VIDEO = 3;
	private final static int REQUEST_CODE_SELECT_PHOTO = 4;
	private long fisrtBackPress;
	private SlidingMenu menu;
	private FragmentManager manager;
	FragmentTransaction transaction;
	private ProjectListFragment2 vxInspiredFragment, vxProjectFragment,
			vxFavoriteFragment;
	private View noNetworkView;
	private ImageButton picBtn, recordBtn, textBtn, saomiaoBtn;
	private ImageView headImg;
	private TextView nickName;
	private Button leftInsBtn, leftProBtn, leftFavBtn, leftLogoutBtn;
	private Button[] leftBtns;
	private int current = 0;
	private LeftMenuOnClickListener leftOnClickListener = new LeftMenuOnClickListener();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		initSlidingMenu();
		initActionBar();
		findViews();
		initVIews();
		initLeftView();
		
		manager = getFragmentManager();
		setCurrentBtn();
		setCurrentFragmet();
		checkForUpdate();
		if(getIntent().getBooleanExtra("upload", false)) {
			UploadManager.getInstance().startUploadIfHave();
		}
	}
	
	private void findViews() {
		noNetworkView = findViewById(R.id.main_nowifi);
		picBtn = (ImageButton) findViewById(R.id.main_pic_btn);
		recordBtn = (ImageButton) findViewById(R.id.main_voice_btn);
		textBtn = (ImageButton) findViewById(R.id.main_text_btn);
		saomiaoBtn = (ImageButton) findViewById(R.id.main_saomiao_btn);
		headImg = (ImageView) findViewById(R.id.leftmenu_user_head);
		nickName = (TextView) findViewById(R.id.leftmenu_user_name);
		leftInsBtn = (Button) findViewById(R.id.leftmenu_btn_ins);
		leftProBtn = (Button) findViewById(R.id.leftmenu_btn_proj);
		leftFavBtn = (Button) findViewById(R.id.leftmenu_btn_fav);
		leftLogoutBtn = (Button) findViewById(R.id.leftmenu_btn_log_out);
	}
	
	private void initVIews() {
		// TODO Auto-generated method stub
		picBtn.setOnTouchListener(mediaBtnOnTouch);
		picBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				PopMenuUtil.popBottomIphoneDialog(ctx, new PopClickListener());
			}
		});
		recordBtn.setOnTouchListener(mediaBtnOnTouch);
		recordBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ctx, RecordActivity.class);
				startActivity(intent);
			}
		});
		textBtn.setOnTouchListener(mediaBtnOnTouch);
		textBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(ctx, RichTextActivity.class);
				startActivity(intent);
			}
		});
		saomiaoBtn.setOnTouchListener(mediaBtnOnTouch);
		saomiaoBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
	        	startActivity(intent);
			}
		});
		noNetworkView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent("android.settings.WIRELESS_SETTINGS");
				startActivity(intent);
			}
		});
		
		
	}
	
	OnTouchListener mediaBtnOnTouch = new  OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(30).start();
				break;
			case MotionEvent.ACTION_UP:
				v.animate().scaleX(1f).scaleY(1f).setStartDelay(100).start();
				break;
			}
			return false;
		}
	};
	
	private void initLeftView() {
		String avatarUrl = User.getCurrentUser().getAvatarUrl();
        Log.v("MainActivity", "headUri: " + avatarUrl);
        VxploApplication.imageLoader.displayImage(avatarUrl, headImg);
        headImg.setOnClickListener(new OnClickListener() {
			float ro = 0f;
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				headImg.animate().rotation(ro = Math.abs(ro - 360f)).setDuration(500).start();
			}
		});
        if(null != User.getCurrentUser().getNickName()) {
        	nickName.setText(User.getCurrentUser().getNickName());
        }
        
        leftInsBtn.setOnClickListener(leftOnClickListener);
        leftProBtn.setOnClickListener(leftOnClickListener);
        leftFavBtn.setOnClickListener(leftOnClickListener);
        leftLogoutBtn.setOnClickListener(leftOnClickListener);
        leftBtns = new Button[] {leftInsBtn, leftProBtn, leftFavBtn};
	}

	private void initSlidingMenu() {
		// TODO Auto-generated method stub
		menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //menu.setShadowWidthRes(R.dimen.shadow_width);
       // menu.setShadowDrawable(R.drawable.fade);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeEnabled(true);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMenu(R.layout.menu_left);
	}
	
	public SlidingMenu getMenu() {
		return menu;
	}
	
	private void initActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setTitle("VXPLO");
		//actionBar.setBackgroundDrawable(VxploApplication.resources.getDrawable(R.drawable.vxred));
		actionBar.setHomeButtonEnabled(true);
		actionBar.setIcon(R.drawable.slide_menu_selector);
		//actionBar.show();
	}
	
	private void requestTakeVideo() {
		
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
		intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 10);
		
		startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
		
	}
	
	public void requestCameraActivity() {
		Intent intent = new Intent(this, CameraActivity.class);
		startActivity(intent);
	}
	
	private void requestSelectVideo() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
	}

	private void requestSelectPhoto() {
		Intent intent = new Intent(ctx, AlbumActivity.class);
		startActivity(intent);
	}
	
	private void requestTakePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
	        if(requestCode == REQUEST_CODE_TAKE_VIDEO) {
	        	Uri uri = data.getData();
	        	Cursor c = getContentResolver().query(uri, new String[] { MediaStore.MediaColumns.DATA }, null, null, null);//根据返回的URI，查找数据库，获取视频的路径
	        	if (c != null && c.moveToFirst()) {
	        		String filPath = c.getString(0);
	        		Log.v("Take Video Path", filPath);
	        		//UploadTask.getInstance().addUploadTask(new FileUpload(this, MediaType.VIDEO).upload(filPath));
	        		UploadManager.getInstance().addUploadTask(new com.vxplo.vxshow.util.upload.UploadTask(MediaType.VIDEO, filPath));
	        	}
	        	
	        }
	        else if(requestCode == REQUEST_CODE_TAKE_PHOTO) {
	        	/*
	        	Uri uri = data.getData();
	        	Cursor c = getContentResolver().query(uri, new String[] { MediaStore.MediaColumns.DATA }, null, null, null);//根据返回的URI，查找数据库，获取视频的路径
	        	if (c != null && c.moveToFirst()) {
	        		String filPath = c.getString(0);
	        		Log.v("Take Photo Path", filPath);
	        		mApplication.addUploadTask(new FileUpload(this, MediaType.IMAGE).upload(filPath));
	        	}*/
	        	String path = resolvePhotoFromIntent(ctx, data);
	        	//UploadTask.getInstance().addUploadTask(new FileUpload(this, MediaType.IMAGE).upload(path));
	        	UploadManager.getInstance().addUploadTask(new com.vxplo.vxshow.util.upload.UploadTask(MediaType.IMAGE, path));
	        }
	        else if(requestCode == REQUEST_CODE_SELECT_VIDEO) {
	        	Uri uri = data.getData();
	        	Cursor c = getContentResolver().query(uri, new String[] { MediaStore.MediaColumns.DATA }, null, null, null);//根据返回的URI，查找数据库，获取视频的路径
	        	if (c != null && c.moveToFirst()) {
	        		String filPath = c.getString(0);
	        		Log.v("Select Video Path", filPath);
	        		//UploadTask.getInstance().addUploadTask(new FileUpload(this, MediaType.VIDEO).upload(filPath));
	        		UploadManager.getInstance().addUploadTask(new com.vxplo.vxshow.util.upload.UploadTask(MediaType.VIDEO, filPath));
	        	}
	        	
	        }
	        else if (requestCode == REQUEST_CODE_SELECT_PHOTO) {
	        	Uri uri = data.getData();
	        	Cursor c = getContentResolver().query(uri, new String[] { MediaStore.MediaColumns.DATA }, null, null, null);//根据返回的URI，查找数据库，获取视频的路径
	        	if (c != null && c.moveToFirst()) {
	        		String filPath = c.getString(0);
	        		Log.v("Select Photo Path", filPath);
	        		//UploadTask.getInstance().addUploadTask(new FileUpload(this, MediaType.IMAGE).upload(filPath));
	        		UploadManager.getInstance().addUploadTask(new com.vxplo.vxshow.util.upload.UploadTask(MediaType.IMAGE, filPath));
	        	}
	        }
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public String resolvePhotoFromIntent(final Context ctx, final Intent data) {
		if (ctx == null || data == null) {
			Log.e(TAG, "resolvePhotoFromIntent fail, invalid argument");
			return null;
		}

		String filePath = null;

		final Uri uri = Uri.parse(data.toURI());
		Cursor cu = ctx.getContentResolver().query(uri, null, null, null, null);
		if (cu != null && cu.getCount() > 0) {
			try {
				cu.moveToFirst();
				final int pathIndex = cu.getColumnIndex(MediaColumns.DATA);
				Log.e(TAG, "orition: " + cu.getString(cu.getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)));
				filePath = cu.getString(pathIndex);
				Log.d(TAG, "photo from resolver, path:" + filePath);

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (data.getData() != null) {
			filePath = data.getData().getPath();
			if (!(new File(filePath)).exists()) {
				filePath = null;
			}
			Log.d(TAG, "photo file from data, path:" + filePath);

		} else if (data.getAction() != null && data.getAction().equals("inline-data")) {

			try {
				Date date = new Date();
				SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd-HHmmss");
				String str = formater.format(date);
				//rawPath = recordsDir + File.separator + "audio" + str + ".raw";
				final String fileName = File.separator + "iamge" + str + ".jpg";
				filePath = FileUtil.getVxImagesDir() +  fileName;

				final Bitmap bitmap = (Bitmap) data.getExtras().get("data");
				final File file = new File(filePath);
				if (!file.exists()) {
					file.createNewFile();
				}

				BufferedOutputStream out;
				out = new BufferedOutputStream(new FileOutputStream(file));
				final int cQuality = 100;
				bitmap.compress(Bitmap.CompressFormat.PNG, cQuality, out);
				out.close();
				Log.d(TAG, "photo image from data, path:" + filePath);

			} catch (final Exception e) {
				e.printStackTrace();
			}

		} else {
			if (cu != null) {
				cu.close();
				cu = null;
			}
			Log.e(TAG, "resolve photo from intent failed");
			return null;
		}
		if (cu != null) {
			cu.close();
			cu = null;
		}
		return filePath;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem itemRefresh = menu.add(0, 0, 0, R.string.refresh);
		itemRefresh.setIcon(R.drawable.selector_btn_refresh);
		itemRefresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		MenuItem itemAbout = menu.add(0, 1, 1, R.string.title_activity_history);
		itemAbout.setIcon(R.drawable.selector_btn_refresh);
		itemAbout.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		
		MenuItem itemCheck = menu.add(0, 2, 2, R.string.check_update);
		//itemCheck.setIcon(R.drawable.selector_btn_refresh);
		itemCheck.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home) {
			menu.toggle();
		}else if(id == 0) {
			refresh();
		}else if(id == 1) {
			Intent intent = new Intent(this, HistoryActivity.class);
			startActivity(intent);
		}
		else if(id == 2) {
			checkForUpdate();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void refresh() {
		int netState = NetworkUtil.getNetworkState(this);
		if(netState != NetworkUtil.NETWORN_NONE) {
			if(VxploApplication.networkState == NetworkUtil.NETWORN_NONE) {
				noNetworkView.setVisibility(View.GONE);
				picBtn.setEnabled(true);
				recordBtn.setEnabled(true);
				VxploApplication.networkState = NetworkUtil.getNetworkState(this);
			}
			if(current == 0) {
				vxInspiredFragment.refreshIdeasList();
			}else if(current == 1) {
				vxProjectFragment.refreshIdeasList();
			}else {
				vxFavoriteFragment.refreshIdeasList();
			}
		}else {
			if(VxploApplication.networkState != NetworkUtil.NETWORN_NONE) {
				noNetworkView.setVisibility(View.VISIBLE);
				picBtn.setEnabled(false);
				recordBtn.setEnabled(false);
				VxploApplication.networkState = NetworkUtil.getNetworkState(this);
			}
		}
	}
	
	private void doLogoutAsync() {
		Log.v("MainActivity", "logout...");
		VxHttpClient.post(Constant.getUserLogoutUrl(), null, true, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				DialogUtil.showLoadingDialog(ctx);
				super.onStart();
			}
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				User.clearUser();
				VxploApplication.getInstance().removeSession("sessid");
				VxploApplication.getInstance().removeSession("sessname");
				logoutToLoginPage();
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				if(arg2 != null && arg2.length > 0) {
					DialogUtil.showToast(ctx, new String(arg2));
					return;
				}
				Toast.makeText(ctx, R.string.failed_connect, Toast.LENGTH_SHORT).show();
			}
			
			@Override
			public void onFinish() {
				DialogUtil.closeLoadingDialog();
				super.onFinish();
			}
		});
	}
	
	private void doLogout() {
		// TODO Auto-generated method stub
		Log.v("MainActivity", "logout...");
		VxHttpRequest request = new VxHttpRequest(this, VxHttpMethod.POST,
				Constant.getUserLogoutUrl(), null, null);
		request.setCallback(new VxHttpCallback() {

			@Override
			public void beforeSend() {
				// TODO Auto-generated method stub
				DialogUtil.showLoadingDialog(ctx);
			}

			@Override
			public void success(String result) {
				// TODO Auto-generated method stub
				User.clearUser();
				VxploApplication.getInstance().removeSession("sessid");
				VxploApplication.getInstance().removeSession("sessname");
				logoutToLoginPage();
			}

			@Override
			public void error(ErrorStatus status, Exception... exs) {
				// TODO Auto-generated method stub
				if (exs.length > 0) {
					DialogUtil.showToast(ctx, exs[0].getMessage());
					return;
				}
				DialogUtil.showToast(ctx, status.name());
			}

			@Override
			public void complete() {
				// TODO Auto-generated method stub
				DialogUtil.closeLoadingDialog();
			}

		});
		request.send();
	}
	
	public boolean isLoggedIn() {
		return User.getCurrentUser().getUid() > 0;
	}
	
	public void logoutToLoginPage()
	{
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
    	startActivity(intent);
    	//overridePendingTransition(R.anim.push_left_in, R.anim.stay); 
    	finish();
	}
	
	
	private void setCurrentFragmet() {
		// TODO Auto-generated method stub
		switch(current) {
		case 0:
			transaction = manager.beginTransaction();
			if (null == vxInspiredFragment) {
				vxInspiredFragment = ProjectListFragment2.newInstance(ListType.INSPIRED);
				//transaction.add(R.id.main_content, vxInspiredFragment, "inspired");
			}
			transaction.replace(R.id.main_content, vxInspiredFragment, "inspired");
			/*
			transaction.show(vxInspiredFragment);
			if (null != vxProjectFragment) {
				transaction.hide(vxProjectFragment);
			}
			if (null != vxFavoriteFragment) {
				transaction.hide(vxFavoriteFragment);
			}*/
			transaction.commit();
			break;
		case 1:
			transaction = manager.beginTransaction();
			if (null == vxProjectFragment) {
				vxProjectFragment = ProjectListFragment2.newInstance(ListType.PROJECTS);
				//transaction.add(R.id.main_content, vxProjectFragment, "inspired");
				
			}
			transaction.replace(R.id.main_content, vxProjectFragment, "myproject");
			/*
			transaction.show(vxProjectFragment);
			if (null != vxInspiredFragment) {
				transaction.hide(vxInspiredFragment);
			}
			if (null != vxFavoriteFragment) {
				transaction.hide(vxFavoriteFragment);
			}*/
			transaction.commit();
			break;
		case 2:
			transaction = manager.beginTransaction();
			if (null == vxFavoriteFragment) {
				vxFavoriteFragment = ProjectListFragment2.newInstance(ListType.FAVORITES);
				//transaction.add(R.id.main_content, vxFavoriteFragment, "inspired");
				
			}
			transaction.replace(R.id.main_content, vxFavoriteFragment, "favourite");
			/*
			transaction.show(vxFavoriteFragment);
			if (null != vxProjectFragment) {
				transaction.hide(vxProjectFragment);
			}
			if (null != vxInspiredFragment) {
				transaction.hide(vxInspiredFragment);
			}*/
			transaction.commit();
			break;
		}
	}
	
	
	class LeftMenuOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			int id = v.getId();
			int last = current;
			if(id == R.id.leftmenu_btn_log_out) {
				doLogoutAsync();
				return;
			} else if (id == R.id.leftmenu_btn_ins) {
				current = 0;
			} else if (id == R.id.leftmenu_btn_proj) {
				if (!isLoggedIn()) {
					menu.toggle();
					goLoginPage();
					return;
				}
				current = 1;
			} else if(id == R.id.leftmenu_btn_fav){
				if (!isLoggedIn()) {
					menu.toggle();
					goLoginPage();
					return;
				}
				current = 2;
			}
			setCurrentBtn();
			menu.toggle();
			if (last == current) {
				return;
			}
			setCurrentFragmet();
		}
	}
	
	
	class PopClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()) {
			case R.id.take_video_button:
				PopMenuUtil.popupWindow.dismiss();
				//requestTakeVideo();
				requestCameraActivity();
				break;
			case R.id.take_picture_button:
				PopMenuUtil.popupWindow.dismiss();
				requestTakePhoto();
				break;
			case R.id.sel_video_button:
				PopMenuUtil.popupWindow.dismiss();
				requestSelectVideo();
				break;
			case R.id.sel_picture_button:
				PopMenuUtil.popupWindow.dismiss();
				requestSelectPhoto();
				break;
			default:
				break;
			}
		}
		 
	}

	public void checkForUpdate() {
		if(NetworkUtil.getNetworkState(this) == NetworkUtil.NETWORN_WIFI) {
			startService(new Intent("com.vxplo.vxshow.service.SERVICE_UPDATE"));
		}else if(NetworkUtil.getNetworkState(this) == NetworkUtil.NETWORN_NONE){
			networkDisable();
		}
	}
	
	public void networkDisable() {
		noNetworkView.setVisibility(View.VISIBLE);
		picBtn.setEnabled(false);
		recordBtn.setEnabled(false);
		textBtn.setEnabled(false);
	}
	
	public void networkEnable() {
		noNetworkView.setVisibility(View.GONE);
		picBtn.setEnabled(true);
		recordBtn.setEnabled(true);
		refresh();
	}
	
	public void setCurrentBtn() {
		// TODO Auto-generated method stub
		int len = leftBtns.length;
		for (int i = 0; i < len; i++) {
			if (i == current) {
				leftBtns[i].setSelected(true);
			} else {
				leftBtns[i].setSelected(false);
			}
		}
	}
	

	public void goLoginPage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onBackPressed() {
		if(System.currentTimeMillis() - fisrtBackPress > 2000) {
			fisrtBackPress = System.currentTimeMillis();
			Toast.makeText(this, R.string.click_again_to_exit, Toast.LENGTH_SHORT).show();
		}else {
			if(UploadTask.getInstance().getUploadTasks().size() > 0) {
				showExitDialog();
			}else {
				super.onBackPressed();
			}
		}
		
	}
	
	private void showExitDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this)
		.setTitle(R.string.tips)
		.setMessage(R.string.uploading_tip)
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		}).setPositiveButton(R.string.exit_app, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				mApplication.appExit();
			}
		}).create();
		dialog.show();
	}
	
}
