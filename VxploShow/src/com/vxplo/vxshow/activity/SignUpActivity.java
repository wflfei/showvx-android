package com.vxplo.vxshow.activity;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
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
import android.widget.CheckBox;
import android.widget.EditText;

public class SignUpActivity extends VxBaseActivity {
	private static final String INVALID_HEAD = "Not Acceptable: ";
	private static final String[] SIGN_UP_FIELDS = { "name", "mail", "pass",
	"field_first_name[und][0][value]" };
	private EditText emailEdit;
	private EditText nameEdit;
	private EditText passwordEdit;
	private CheckBox acceptCheckBox;
	private Button signUpBtn;
	private Button loginBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		findViews();
		initViews();
		
	}
	
	private void findViews()
	{
		emailEdit = (EditText) findViewById(R.id.sign_email_edit);
		nameEdit = (EditText) findViewById(R.id.sign_name_edit);
		passwordEdit = (EditText) findViewById(R.id.sign_password_edit);
		acceptCheckBox = (CheckBox) findViewById(R.id.sign_accept_check);
		signUpBtn = (Button) findViewById(R.id.sign_up_btn);
		loginBtn = (Button) findViewById(R.id.sign_login_btn);
	}
	
	private void initViews()
	{
		signUpBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (ValidateUtil.isEditEmpty(emailEdit, nameEdit, passwordEdit)) {
					showAlertDialog(VxploApplication.resources
							.getString(R.string.signup_alert_empty));
					return;
				}
				if (!acceptCheckBox.isChecked()) {
					showAlertDialog(VxploApplication.resources
							.getString(R.string.signup_alert_empty));
					return;
				}
				doSignUpAsync();
			}
		});
		
		loginBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) { 
				finish();
			}
		});
		
		
		
		findViewById(R.id.content).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeKeyboard();
			}
		});
	}
	
	private void closeKeyboard()
	{
		InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(emailEdit.getWindowToken(), 0);
	}
	
	private void doSignUpAsync()
	{
		String mail = emailEdit.getText().toString().trim();
		String name = nameEdit.getText().toString().trim();
		String pass = passwordEdit.getText().toString().trim();

		
		VxHttpRequest request = new VxHttpRequest(ctx, VxHttpMethod.POST,
				Constant.getUserSignUpUrl(), SIGN_UP_FIELDS, new String[] {
						mail, mail, pass, name });
		request.setCallback(new VxHttpCallback(){

			@Override
			public void beforeSend() {
				// TODO Auto-generated method stub
				//DialogUtil.showLoadingDialog(ctx);
				showLoadingDialog();
			}

			@Override
			public void success(String result) throws Exception {
				// TODO Auto-generated method stub
				if(result.startsWith(INVALID_HEAD)) {
					int start = result.lastIndexOf("The e-mail address");
					String tempMsg = result.substring(start);
					int end = tempMsg.indexOf(".");
					String message = tempMsg.substring(0, end);
					DialogUtil.showToast(ctx, tempMsg);
				}else {
					try {
						Log.d("SignUpResult", result);
						Intent intent = new Intent();
						intent.putExtra("result", result);
						setResult(RESULT_OK, intent);
						finish();
					} catch(Exception e) {
						throw new Exception(result);
					}
				}
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
				//DialogUtil.closeLoadingDialog();
				closeLoadingDialog();
			}});
		request.send();
	}
	
	private void doSignUp()
	{
		String mail = emailEdit.getText().toString().trim();
		String name = nameEdit.getText().toString().trim();
		String pass = passwordEdit.getText().toString().trim();

		VxHttpRequest request = new VxHttpRequest(ctx, VxHttpMethod.POST,
				Constant.getUserSignUpUrl(), SIGN_UP_FIELDS, new String[] {
						mail, mail, pass, name });
		request.setCallback(new VxHttpCallback(){

			@Override
			public void beforeSend() {
				// TODO Auto-generated method stub
				//DialogUtil.showLoadingDialog(ctx);
				showLoadingDialog();
			}

			@Override
			public void success(String result) throws Exception {
				// TODO Auto-generated method stub
				if(result.startsWith(INVALID_HEAD)) {
					int start = result.lastIndexOf("The e-mail address");
					String tempMsg = result.substring(start);
					int end = tempMsg.indexOf(".");
					String message = tempMsg.substring(0, end);
					DialogUtil.showToast(ctx, tempMsg);
				}else {
					try {
						Log.d("SignUpResult", result);
						Intent intent = new Intent();
						intent.putExtra("result", result);
						setResult(RESULT_OK, intent);
						finish();
					} catch(Exception e) {
						throw new Exception(result);
					}
				}
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
				//DialogUtil.closeLoadingDialog();
				closeLoadingDialog();
			}});
		request.send();
	}
	
}
