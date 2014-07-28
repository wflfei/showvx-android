package com.vxplo.vxshow.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import com.vxplo.vxshow.configure.Config;

public class VxHttpPost extends HttpPost {
	
	private String url;
	private List<NameValuePair> params;
	
	public VxHttpPost(String path) {
		super(path);
		this.url = Config.getBaseUrl() + path;
	}
	
	public VxHttpPost(String path,String[] paramNames,String[] paramValues) {
		this(path);
		setParams(paramNames, paramValues);
	}
	
	public void setParams(String[] paramNames,String[] paramValues) {
		this.params = makeParams(paramNames,paramValues);
		try {
			this.setEntity(new UrlEncodedFormEntity(params));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private List<NameValuePair> makeParams(String[] paramNames, String[] paramValues) {
		// TODO Auto-generated method stub
		List<NameValuePair> temp = new ArrayList<NameValuePair>();
		if(null==paramNames||null==paramValues) {
			return temp;
		}
		int len = paramNames.length;
		for(int i=0;i<len;i++) {
			temp.add(new BasicNameValuePair(paramNames[i], paramValues[i]));
		}
		return temp;
	}

}
