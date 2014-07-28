package com.vxplo.vxshow.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

public class VxHttpTask extends AsyncTask<VxHttpPost, Void, String> {
	
	public static enum ErrorStatus {RequestError,NetworkError,ResultHandleError,Canceled} 
	
	private VxHttpCallback callback;
	private HttpRequestBase request;
	private Context context;
	
	public VxHttpTask(Context context) {
		this.context = context;
	}
	
	public VxHttpTask(Context context, HttpRequestBase request) {
		this(context);
		this.request = request;
	}

//	public VxHttpTask(Context context, String path,String[] paramNames,String[] paramValues) {
//		this(context);
//		this.post = new VxHttpPost(path, paramNames, paramValues);
//	}
	
	@Override
	protected String doInBackground(VxHttpPost... params) {
		// TODO Auto-generated method stub
		try {
			HttpResponse res = VxHttpClient.getHttpClient().execute(request);
			int resCode = res.getStatusLine().getStatusCode();
			System.out.println("resCode:===="+resCode);
			if(resCode!=200) {
				return res.getStatusLine().getReasonPhrase();
			}
			return EntityUtils.toString(res.getEntity());
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		if(null!=callback) {
			callback.error(ErrorStatus.Canceled);
		}
	}

	@Override
	protected void onCancelled(String result) {
		// TODO Auto-generated method stub
		this.onCancelled();
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		if(null!=callback) {
			if(null!=result) {
				try{
					callback.success(result);
				}catch(Exception e) {
					callback.error(ErrorStatus.ResultHandleError, new Exception(result));
				}
			} else {
				callback.error(ErrorStatus.NetworkError);
			}
			callback.complete();
		}
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		if(null!=callback) {
			callback.beforeSend();
		}
	}

	@Override
	protected void onProgressUpdate(Void... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	public void setCallback(VxHttpCallback callback) {
		this.callback = callback;
	}

}