package com.vxplo.vxshow.service;

import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.util.NetworkUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class NetCheckReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
        	Log.v("NetCheckReceiver", "network state changed");
        	//true 代表网络断开   false 代表网络没有断开
        	/*
            if(intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)){
                if(VxploApplication.getInstance().getMainActivity() != null) {
                	VxploApplication.getInstance().getMainActivity().networkDisable();
                }
            }else{
            	if(VxploApplication.getInstance().getMainActivity() != null) {
                	VxploApplication.getInstance().getMainActivity().networkEnable();
                }
            }*/
        	if(NetworkUtil.getNetworkState(VxploApplication.getInstance()) == NetworkUtil.NETWORN_NONE && VxploApplication.networkState != NetworkUtil.NETWORN_NONE) {
        		if(VxploApplication.getInstance().getMainActivity() != null) {
                	VxploApplication.getInstance().getMainActivity().networkDisable();
                }
        	}else if(VxploApplication.networkState == NetworkUtil.NETWORN_NONE){
        		if(VxploApplication.getInstance().getMainActivity() != null) {
                	VxploApplication.getInstance().getMainActivity().networkEnable();
                }
        	}
        }
    }
}