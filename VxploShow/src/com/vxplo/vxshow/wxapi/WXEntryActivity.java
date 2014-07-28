package com.vxplo.vxshow.wxapi;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.R.layout;
import com.vxplo.vxshow.activity.VxBaseActivity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class WXEntryActivity extends VxBaseActivity implements IWXAPIEventHandler{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wxentry);
		finish();
	}

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResp(BaseResp arg0) {
		// TODO Auto-generated method stub
		Log.v("WXEntryActivity", "receive onResp");
		Toast.makeText(this, "receive onResp", Toast.LENGTH_SHORT).show();
		finish();
	}
}
