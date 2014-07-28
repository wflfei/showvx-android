package com.vxplo.vxshow.activity;

import java.util.Date;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity.ListType;
import com.vxplo.vxshow.app.VxploApplication.DataLoadingCallBack;
import com.vxplo.vxshow.configure.Config;
import com.vxplo.vxshow.entity.User;
import com.vxplo.vxshow.util.NetworkUtil;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

/**
 * 
 * @author lin
 * 
 *
 */
public class SplashActivity extends VxBaseActivity {
	private IWXAPI api;
	long startTime;
	private final static int MIN_TIME = 800;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		regToWx();
		new SplashAsyncTask().execute();
		Log.v("SplashAcitivty", "on Create End! " + new Date().toString());
	}
	
	class SplashAsyncTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = 1;
			startTime = System.currentTimeMillis();
            //result = loadingCache();
            if(mApplication.getPref("user").getBoolean("auto", false)) {
				return 0;
			}
			return result;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Integer result) {
			if(result == 0) {
				if(NetworkUtil.getNetworkState(getApplicationContext()) == NetworkUtil.NETWORN_NONE) {
					tryAutoLogin();
					return;
				}
				mApplication.getIdeaList(ListType.INSPIRED, new DataLoadingCallBack() {
					
					@Override
					public void onSuccess() {
						long loadingTime = System.currentTimeMillis() - startTime;
			            if (loadingTime < MIN_TIME) {
			                try {
			                    Thread.sleep(MIN_TIME - loadingTime);
			                } catch (InterruptedException e) {
			                    e.printStackTrace();
			                }
			            }
			            tryAutoLogin();
					}
					
					@Override
					public void onFail() {
						// TODO Auto-generated method stub
						long loadingTime = System.currentTimeMillis() - startTime;
			            if (loadingTime < MIN_TIME) {
			                try {
			                    Thread.sleep(MIN_TIME - loadingTime);
			                } catch (InterruptedException e) {
			                    e.printStackTrace();
			                }
			            }
			            tryAutoLogin();
					}
				});
			}
			if(result != 0) {
				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
				startActivity(intent);
				mApplication.removeActivity(SplashActivity.this);
			}
			
			super.onPostExecute(result);
		}
	}
	
	private void tryAutoLogin() {
		User.setCurrentUser(User.getUserFromPref());
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("current", 1);
		startActivity(intent);
		mApplication.removeAllActivities();
	}
	
	private void regToWx() {
		api = WXAPIFactory.createWXAPI(ctx, Config.APP_ID, true);
		api.registerApp(Config.APP_ID);
	}
}
