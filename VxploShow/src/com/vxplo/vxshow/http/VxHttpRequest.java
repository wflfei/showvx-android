package com.vxplo.vxshow.http;

import org.apache.http.client.methods.HttpRequestBase;

import android.content.Context;

import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.util.StringUtil;

public class VxHttpRequest {

	public static enum VxHttpMethod {GET, POST}

	private HttpRequestBase request;
	private VxHttpCallback callback;
	private VxHttpMethod method;
	private VxHttpTask task;
	private final Context context;

	public VxHttpRequest(Context context) {
		this.context = context;
	}

	public VxHttpRequest(Context context,VxHttpMethod method,String url) {
		this(context);
		this.method = method;
		switch(method) {
		case GET:
			request = new VxHttpGet(url);
			break;
		case POST:
			request = new VxHttpPost(url);
			break;
		}
	}

	public VxHttpRequest(Context context,VxHttpMethod method,String url,String[] paramNames,String[] paramValues) {
		this(context);
		this.method = method;
		switch(method) {
		case GET:
			request = new VxHttpGet(url, paramNames, paramValues);
			break;
		case POST:
			request = new VxHttpPost(url, paramNames, paramValues);
			break;
		}
	}

	public void setParams(String[] paramNames,String[] paramValues) {
		switch(this.method) {
		case GET:
			((VxHttpGet)request).setParams(paramNames, paramValues);
			break;
		case POST:
			((VxHttpPost)request).setParams(paramNames, paramValues);
			break;
		}
	}

	public void setCallback(VxHttpCallback callback) {
		this.callback = callback;
	}

	private void addToken() {
		String token = VxploApplication.pref.getString("token", "");
		String sessname = VxploApplication.pref.getString("sessname", "");
		String sessid = VxploApplication.pref.getString("sessid", "");
		if(StringUtil.isStringEmptyOrNull(token)) {
			return;
		}
		request.addHeader("X-CSRF-Token", token);
		if(StringUtil.isStringEmptyOrNull(sessname)||StringUtil.isStringEmptyOrNull(sessid)) {
			return;
		}
		request.addHeader("Cookie", sessname + "=" + sessid);
	}

	public void send() {
		addToken();
		task = new VxHttpTask(context,request);
		task.setCallback(callback);
		task.execute();
	}
}
