package com.vxplo.vxshow.activity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.configure.Config;
import com.vxplo.vxshow.entity.Idea;
import com.vxplo.vxshow.util.NetworkUtil;
import com.vxplo.vxshow.util.WXUtil;
import com.vxplo.vxshow.util.uihider.VxShowSystemUiHider;
import com.vxplo.vxshow.util.zxing.CaptureActivity;

public class ProjectShowActivity extends VxBaseActivity {
	private static final String TAG = ProjectShowActivity.class.getSimpleName();
	private static final String APP_CACAHE_DIRNAME = "/webcache";
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
	private static final int MOVE_DOWN_LENGTH = 150;
	private static final int MOVE_HORI_LENGTH = 40;
	private IWXAPI api;
	private VxShowSystemUiHider mSystemUiHider;
	
	private View contentView;
	private ProgressBar pBar;
	private WebView webView;
	private Idea idea;
	private String url;
	
	private float x,y;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.activity_project_show);
		initActionBar();
		initUiHider();
		
		initData();
		
		initWebView();
		
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initWebView() {
		// TODO Auto-generated method stub
		webView = (WebView) findViewById(R.id.show_webview);
		pBar = (ProgressBar) findViewById(R.id.webview_progress);
		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setUseWideViewPort(true);
		settings.setLoadWithOverviewMode(true);
        settings.setRenderPriority(RenderPriority.HIGH);
        if(NetworkUtil.getNetworkState(ctx) == NetworkUtil.NETWORN_NONE) {
        	settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        } else {
        	settings.setCacheMode(WebSettings.LOAD_DEFAULT);  //设置 缓存模式
        }
          
        // 开启 DOM storage API 功能  
        settings.setDomStorageEnabled(true);  
        //开启 database storage API 功能  
        settings.setDatabaseEnabled(true);   
        String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;  
//	      String cacheDirPath = getCacheDir().getAbsolutePath()+Constant.APP_DB_DIRNAME;  
        Log.i(TAG, "cacheDirPath="+cacheDirPath);  
        //设置数据库缓存路径  
        //settings.setDatabasePath(cacheDirPath);  
        //设置  Application Caches 缓存目录  
        settings.setAppCachePath(cacheDirPath);  
        //开启 Application Caches 功能  
        settings.setAppCacheEnabled(true);  
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				pBar.setVisibility(View.VISIBLE);
				pBar.setProgress(0);
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				pBar.setVisibility(View.GONE);
				webView.postInvalidate();
				super.onPageFinished(view, url);
			}

			@Override
			public void onScaleChanged(WebView view, float oldScale,
					float newScale) {
				// TODO Auto-generated method stub
				super.onScaleChanged(view, oldScale, newScale);
			}
			
		});
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onReceivedTitle(WebView view, String title) {
				idea.setTitle(title);
				getActionBar().setTitle(title);
				super.onReceivedTitle(view, title);
			}
			
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if(pBar.getVisibility()==View.GONE) {
					pBar.setVisibility(View.VISIBLE);
				}
				pBar.setProgress(newProgress);
				pBar.postInvalidate();

				if(newProgress==100) {
					pBar.setVisibility(View.GONE);
				}
				super.onProgressChanged(view, newProgress);
			}
			
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
					JsResult result) {
				/*
				AlertDialog dialog = new AlertDialog.Builder(ctx, AlertDialog.THEME_HOLO_LIGHT)
				.setTitle(R.string.alert_title)
				.setMessage(message)
				.setPositiveButton(R.string.ok, new OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
				dialog.show();*/
				return super.onJsAlert(view, url, message, result);
			}
			
			
			
		});
		webView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.onTouchEvent(event);
				return true;
			}
		});
		if(idea != null)
		webView.loadUrl(idea.getPlayUrl());
		else 
			webView.loadUrl(url);
	}

	private void initData() {
		// TODO Auto-generated method stub
		if(getIntent().getSerializableExtra("idea") != null) {
			idea = (Idea) getIntent().getSerializableExtra("idea");
		}
		if(getIntent().getStringExtra("url") != null) {
			url = getIntent().getStringExtra("url");
			idea = new Idea();
			idea.setPlayUrl(url);
		}
	}

	private void initUiHider() {
		// TODO Auto-generated method stub
		contentView = findViewById(R.id.fullscreen_content);
		mSystemUiHider = new VxShowSystemUiHider(this, contentView, true);

	}

	
	private void initActionBar() {
		ActionBar actionBar = getActionBar();
		if(idea != null) {
			actionBar.setTitle(idea.getTitle());
		}
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem itemRotate = menu.add(0, 0, 0, R.string.rotate);
		itemRotate.setIcon(R.drawable.selector_btn_rotate);
		itemRotate.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		
		/*
		MenuItem itemShare = menu.add(0, 1, 1, "分享");
		itemShare.setIcon(R.drawable.selector_btn_share);
		itemShare.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);*/
		
		//MenuItem itemShare = menu.add(0, 1, 1, "分享");
		//SubMenu sub = itemShare.
		SubMenu sub = menu.addSubMenu(1, 1, 0, R.string.share);
		MenuItem itemShare = sub.getItem();
		itemShare.setIcon(R.drawable.selector_btn_share);
		
		//sub.setIcon(R.drawable.selector_btn_share);
        sub.add(1, 10, 1, R.string.send_to_wechat_friends);
        sub.add(1, 11, 1, R.string.share_to_timeline);
        itemShare.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		
		MenuItem itemScan = menu.add(0, 2, 2, R.string.saoyisao);
		itemScan.setIcon(R.drawable.qrcode);
		itemScan.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return super.onCreateOptionsMenu(menu);
	}
	
	
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		delayedHide(AUTO_HIDE_DELAY_MILLIS);
	}
	
	/**
	 * 设置下划显示antionBar，上划隐藏全屏显示
	 * 处理activity的触摸事件，判断是否改变actionBar的显示状态后，继续将触摸事件分发给下一级
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch(ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.v("ProjectShowActivity", "TouchDown");
			x = ev.getX();
			y = ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			Log.v("ProjectShowActivity", "TouchMove");
			float newX, newY;
			newX = ev.getX();
			newY = ev.getY();
			Log.v("ProjectShowActivity", "newx - x: " + (newX - x));
			Log.v("ProjectShowActivity", "newY - y: " + (newY - y));
			if(Math.abs(newX - x) < MOVE_HORI_LENGTH) {
				if((newY - y) > MOVE_DOWN_LENGTH) {
					Log.v("ProjectShowActivity", "newY - y: " + (newY - y));
					mSystemUiHider.show();
				}
				else if((y - newY) > MOVE_DOWN_LENGTH) {
					Log.v("ProjectShowActivity", "newY - y: " + (newY - y));
					mSystemUiHider.hide();
				}
			}
				
			break;
		case MotionEvent.ACTION_UP:
			
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
	
	/**
	 * 处理选中时间
	 */
	public boolean onOptionsItemSelected(android.view.MenuItem item) {
		switch (item.getItemId()) {
   		case android.R.id.home:
   			VxploApplication.getInstance().removeActivity(this);
   			return true;
   		case 0:
   			toogleOrientation();
   			break;
   		case 10:
   			shareToWeChat(false);
   			break;
   		case 11:
   			shareToWeChat(true);
   			break;
   		case 2:
   			Intent intent = new Intent(this, CaptureActivity.class);
   			startActivity(intent);
   			break;
   		default:
   			break;
   		}
   		return true;
	};
	
	/**
	 * 横竖屏切换
	 */
	private void toogleOrientation() {
		Configuration config = VxploApplication.resources.getConfiguration();
		int orientation = config.orientation;
		if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
	}
	
	private void share() {
		/*
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		if(idea != null) {
			intent.putExtra(Intent.EXTRA_SUBJECT, idea.getTitle());
			intent.putExtra(Intent.EXTRA_TEXT, idea.getTitle() + " 快来戳我→" + idea.getPlayUrl());
		}else {
			intent.putExtra(Intent.EXTRA_TEXT, url);
		}
		startActivity(Intent.createChooser(intent, "Share"));
		*/
		//shareToWeChat();
		
	}
	
	/**
	 * 分享到微信
	 * @param timeline 是否分享到朋友圈
	 */
	private void shareToWeChat(boolean timeline) {
		new WXUtil(this).shareWebPage(idea, timeline);
	}

	
	/**
	 * 
	 */
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
	
	@Override
	protected void onResume() {
		webView.onResume();
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		webView.onPause();
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
