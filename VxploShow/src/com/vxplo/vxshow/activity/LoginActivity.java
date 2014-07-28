package com.vxplo.vxshow.activity;

import org.json.JSONException;
import org.json.JSONObject;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.entity.User;
//import com.vxplo.vxshow.fragment.ProjectListFragment.ListType;
import com.vxplo.vxshow.http.VxHttpCallback;
import com.vxplo.vxshow.http.VxHttpRequest;
import com.vxplo.vxshow.http.VxHttpRequest.VxHttpMethod;
import com.vxplo.vxshow.http.VxHttpTask.ErrorStatus;
import com.vxplo.vxshow.http.constant.Constant;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.StringUtil;
import com.vxplo.vxshow.util.zxing.CaptureActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.RelativeLayout;

public class LoginActivity extends VxBaseActivity {
	private EditText emailEdit;
	private EditText passwordEdit;
	private Button noneBtn;
	private Button forgetPwdBtn;
	private Button loginBtn;
	private Button scanBtn;
	private VxploApplication mApplication;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mApplication = VxploApplication.getInstance();
		findViews();
		initViews();
		startService(new Intent("com.vxplo.vxshow.service.SERVICE_UPDATE"));
	}
	
	private void findViews()
	{
		emailEdit = (EditText) findViewById(R.id.login_email_edit);
		passwordEdit = (EditText) findViewById(R.id.login_password_edit);
		loginBtn = (Button) findViewById(R.id.login_btn);
		noneBtn = (Button) findViewById(R.id.register_btn);
		forgetPwdBtn = (Button) findViewById(R.id.login_forgetpwd_btn);
		scanBtn = (Button) findViewById(R.id.login_scan_btn);
	}

	private void closeKeyboard()
	{
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(emailEdit.getWindowToken(), 0);
	}
	
	private void tryAutoLogin() {
		User.setCurrentUser(User.getUserFromPref());
		Intent intent = new Intent(this, MainActivity.class);
		intent.putExtra("current", 1);
		startActivity(intent);
		finish();
	}


	private void initViews() {
		findViewById(R.id.content).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeKeyboard();
			}
		});
		
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = emailEdit.getText().toString();
				String password = passwordEdit.getText().toString();
				if(!"".equals(username) && !"".equals(password)) {
					doLogin();
				}else {
					Toast.makeText(getApplicationContext(), "请输入用户名和密码", Toast.LENGTH_LONG).show();
				}
				
				
			}
		});
		
		noneBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				gotoSignUp();
			}
		});
		
		forgetPwdBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(), ResetPassActivity.class);
	        	startActivity(intent);
	        	//overridePendingTransition(R.anim.anim_activity_right_in, R.anim.anim_activity_left_out);
			}
		});
		
		scanBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), CaptureActivity.class);
	        	startActivityForResult(intent, 0);
			}
		});
	}

	private void gotoSignUp() {
		Intent intent = new Intent(this, SignUpActivity.class);
		startActivityForResult(intent, 100);
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==RESULT_OK) {
			String result = data.getStringExtra("result");
			try {
				tryLogin(result);
			} catch (Exception e) {
				DialogUtil.showToast(ctx, result);
			}
		}
	}

	private void doLogin() {
		closeKeyboard();
		String username = emailEdit.getText().toString().trim();
		String password = passwordEdit.getText().toString().trim();
		VxHttpRequest request = new VxHttpRequest(this, VxHttpMethod.POST,
				Constant.getUserLoginUrl(), new String[] { "username",
						"password" }, new String[] { username, password });
		request.setCallback(new VxHttpCallback(){

			@Override
			public void beforeSend() {
				// TODO Auto-generated method stub
				LoginActivity.this.showLoadingDialog();
			}

			@Override
			public void success(String result) throws Exception {
				// TODO Auto-generated method stub
				Log.d("LoginResult", result);
				tryLogin(result);
			}

			@Override
			public void complete() {
				// TODO Auto-generated method stub
				closeLoadingDialog();
			}

			@Override
			public void error(ErrorStatus status, Exception... exs) {
				// TODO Auto-generated method stub
				
				if(exs.length>0) {
					String wrongDetail = exs[0].getMessage();
					if(wrongDetail.startsWith("Unauthorized")) {
						DialogUtil.showToast(ctx, "用户名或密码错误");
					} else {
						DialogUtil.showToast(ctx, exs[0].getMessage());
						Log.e("LoginError EXp", exs[0].getMessage());
					}
				}else{
					DialogUtil.showToast(ctx, "Error, status:"+status.name());
					Log.e("LoginError Sta", status.name());
				}
			}

		});
		request.send();
		Log.v("LoginAcitivity", "startRequest!");
	}
	
	public void tryLogin(String result) throws Exception {
		try {
			JSONObject res = new JSONObject(result);
			JSONObject userJson = res.optJSONObject("user");
			User.setUserInfoByJson(userJson);
			VxploApplication.getInstance().setSession("sessid", res.optString("sessid"));
			VxploApplication.getInstance().setSession("sessname", res.optString("session_name"));
			final Context c = ctx;
			VxploApplication.getInstance().getToken(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent(c,MainActivity.class);
					//intent.putExtra("current", ListType.PROJECTS.ordinal());
					c.startActivity(intent);
					mApplication.removeAllActivities();
				}
			});
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			throw new Exception(result);
		}
	}
}
