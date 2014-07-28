package com.vxplo.vxshow.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.Bundle;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.util.NetworkUtil;

public class VxBaseActivity extends Activity {
	
	protected Context ctx;
	protected Resources resources;
	protected VxploApplication mApplication;
	private Builder builder;
	private AlertDialog aDialog;
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		super.onCreate(savedInstanceState);
		this.ctx = this;
		this.resources = this.getResources();
		this.mApplication = VxploApplication.getInstance();
		
		mApplication.addActivity(this);
	}
	
	protected void showAlertDialog(String msg) {
		if(null==builder) {
			builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_DARK);
			builder.setTitle(resources.getString(R.string.dialog_title_info));
			builder.setNegativeButton(resources.getString(R.string.OK), null);
		}
		builder.setMessage(msg);
		builder.setInverseBackgroundForced(false);
		builder.show();
	}
	

	protected void showLoadingDialog() {
		if(null==pDialog) {
			pDialog = new ProgressDialog(this,ProgressDialog.THEME_HOLO_DARK);
		}
		pDialog.setMessage(VxploApplication.resources.getString(R.string.loading_str));
		pDialog.show();
	}
	
	protected void closeLoadingDialog() {
		if(null!=pDialog) {
			pDialog.dismiss();
			pDialog = null;
		}
	}
	
	@Override
	protected void onDestroy() {
		mApplication.removeActivity(this);
		super.onDestroy();
	}
}
