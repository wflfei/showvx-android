package com.vxplo.vxshow.asynchttp;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.loopj.android.http.*;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.util.StringUtil;

public class VxHttpClient extends AsyncHttpClient {
	private static boolean addedToken = false;
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public static void get(String url, RequestParams params, boolean withToken, AsyncHttpResponseHandler responseHandler) {
		if(withToken) {
			if(!addedToken) {
				addToken();
			}
		} else {
			removeTokens();
		}
		client.get(url, params, responseHandler);
	}

	public static void post(String url, RequestParams params, boolean withToken, AsyncHttpResponseHandler responseHandler) {
		if(withToken) {
			if(!addedToken) {
				addToken();
			}
		} else {
			removeTokens();
		}
		client.post(url, params, responseHandler);
	}
	
	private static void addToken() {
		String token = VxploApplication.pref.getString("token", "");
		String sessname = VxploApplication.pref.getString("sessname", "");
		String sessid = VxploApplication.pref.getString("sessid", "");
		if(StringUtil.isStringEmptyOrNull(token)) {
			return;
		}
		client.addHeader("X-CSRF-Token", token);
		if(StringUtil.isStringEmptyOrNull(sessname)||StringUtil.isStringEmptyOrNull(sessid)) {
			return;
		}
		client.addHeader("Cookie", sessname + "=" + sessid);
		addedToken = true;
	}
	
	private static void removeTokens() {
		if(addedToken) {
			client.removeHeader("X-CSRF-Token");
			client.removeHeader("Cookie");
			addedToken = false;
		}
	}


}
