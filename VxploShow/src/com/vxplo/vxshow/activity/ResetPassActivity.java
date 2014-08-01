package com.vxplo.vxshow.activity;

import java.nio.MappedByteBuffer;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.asynchttp.VxHttpClient;
import com.vxplo.vxshow.http.VxHttpCallback;
import com.vxplo.vxshow.http.VxHttpRequest;
import com.vxplo.vxshow.http.VxHttpRequest.VxHttpMethod;
import com.vxplo.vxshow.http.VxHttpTask.ErrorStatus;
import com.vxplo.vxshow.http.constant.Constant;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.ValidateUtil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

public class ResetPassActivity extends VxBaseActivity {
	private final String GET_UID_PARAMS = "";
	private EditText emailEdit;
	private Button loginBtn, sendBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_pwd);
		findViews();
		initViews();
	}
	
	private void findViews()
	{
		emailEdit = (EditText) findViewById(R.id.forget_email_edit);
		sendBtn = (Button) findViewById(R.id.forget_send_btn);
		loginBtn = (Button) findViewById(R.id.forget_login_btn);
	}
	
	private void initViews()
	{
		sendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeKeyboard();
				if(!ValidateUtil.isEditEmpty(emailEdit)) {
					doGetUidByEmail();
				}else {
					Toast.makeText(getApplicationContext(), mApplication.resources.getString(R.string.forget_alert_empty), Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		loginBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
	        	//startActivity(intent);
	        	//overridePendingTransition(R.anim.to_right_in, R.anim.push_left_out);
	        	finish();
			}
		});
		
		findViewById(R.id.content).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeKeyboard();
			}
		});
	}
	
	protected void doGetUidByEmail() {
		String email = emailEdit.getText().toString().trim();
		VxHttpClient.get(Constant.getUserGetUidUrl(), new RequestParams("mail", email), false, new AsyncHttpResponseHandler() {
			
			@Override
			public void onStart() {
				showLoadingDialog();
				super.onStart();
			}
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String result = new String(arg2);
				Log.v("FindPwdActivity", result);
				JSONArray uidArray;
				try {
					uidArray = new JSONArray(result);
					JSONObject uidObj = uidArray.getJSONObject(0);
					String uid = uidObj.getString("uid");
					requestResetEmail(uid);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				if(arg2 != null && arg2.length > 0) {
					DialogUtil.showToast(ctx, new String(arg2));
				} else{
					Toast.makeText(ctx, R.string.failed_connect, Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onFinish() {
				closeLoadingDialog();
				super.onFinish();
			}
		});
	}

	private void closeKeyboard()
	{
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(emailEdit.getWindowToken(), 0);
	}
	
	private void requestResetEmail(String uid)
	{
		Log.d("ResetPassActivity", "uid: " + uid);
		VxHttpClient.get("http://www.vxplo.cn/app/user/" + uid + "/password_reset", null, false, new AsyncHttpResponseHandler() {
			
			@Override
			public void onStart() {
				showLoadingDialog();
				super.onStart();
			}
			
			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String result = new String(arg2);
				Log.v("FindPwdActivity", result);
			}
			
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
				// TODO Auto-generated method stub
				if(arg2 != null && arg2.length > 0) {
					DialogUtil.showToast(ctx, new String(arg2));
				} else{
					Toast.makeText(ctx, R.string.failed_connect, Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onFinish() {
				closeLoadingDialog();
				super.onFinish();
			}
		});
		/*
		VxHttpRequest getUidRequest = new VxHttpRequest(getApplicationContext(), VxHttpMethod.GET, "http://www.vxplo.cn/app/user/" + uid + "/password_reset");
		getUidRequest.setCallback(new VxHttpCallback() {
			
			@Override
			public void success(String result) throws Exception {
				// TODO Auto-generated method stub
				Log.v("FindPwdActivity", result);
			}
			
			@Override
			public void error(ErrorStatus status, Exception... exs) {
				// TODO Auto-generated method stub
				if(exs.length>0) {
					DialogUtil.showToast(ctx, exs[0].getMessage());
				}else{
					DialogUtil.showToast(ctx, "Error, status:"+status.name());
				}
			}
			
			@Override
			public void complete() {
				// TODO Auto-generated method stub
				closeLoadingDialog();
			}
			
			@Override
			public void beforeSend() {
				// TODO Auto-generated method stub
				showLoadingDialog();
			}
		});
		getUidRequest.send();
		*/
	}
	
	
	
}
