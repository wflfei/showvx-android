package com.vxplo.vxshow.http;

import org.apache.http.client.methods.HttpGet;

import com.vxplo.vxshow.configure.Config;
import com.vxplo.vxshow.util.StringUtil;

public class VxHttpGet extends HttpGet {

	private String url;
	private String params;
	
	public VxHttpGet(String path) {
		super(path);
		this.url = Config.getBaseUrl() + path;
	}
	
	public VxHttpGet(String path,String[] paramNames,String[] paramValues) {
		this(path);
		this.params = makeParams(paramNames,paramValues);
	}
	
	public void setParams(String params) {
		this.params = params;
	}
	
	public void setParams(String[] paramNames,String[] paramValues) {
		this.params = makeParams(paramNames,paramValues);
	}

	private String makeParams(String[] paramNames, String[] paramValues) {
		// TODO Auto-generated method stub
		if(null==paramNames||null==paramValues) {
			return this.url;
		}
		String temp = "";
		int len = paramNames.length;
		for(int i=0;i<len;i++) {
			temp += "&" + paramNames[i] + "=" + paramValues[i];
		}
		if(!StringUtil.isStringEmptyOrNull(temp)) {
			temp = "?" + temp.substring(1);
		}
		return this.url + temp;
	}
	
}
