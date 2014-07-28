package com.vxplo.vxshow.update;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.util.Log;

import com.vxplo.vxshow.entity.Version;

public class VersionCheckAsyncTask extends AsyncTask<String, Integer, Version>{
	private Context context;
	private CheckUpdateCallback callback;
	
	public VersionCheckAsyncTask(Context context, CheckUpdateCallback callback) {
		this.context = context;
		this.callback = callback;
	}

	@Override
	protected Version doInBackground(String... params) {
		String url = params[0];
		Version latestVersion = null;
		try {
			URL targetUrl = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) targetUrl.openConnection();
			connection.setConnectTimeout(800);
			InputStream is = connection.getInputStream();
			latestVersion = parseVersion(toStringBuffer(is).toString());
			is.close();
			connection.disconnect();
		} catch (Exception exp) {
			exp.printStackTrace();
		}
		return latestVersion;
	}

	@Override
	protected void onPostExecute(Version result) {
		super.onPostExecute(result);
		if(result != null) {
			if(comparedWithCurrentPackage(result)) {
				callback.onFoundLatestVersion(result);
			}else {
				callback.onCurrentIsLatest();
			}
		}
	}

	private Version parseVersion(String str) {
		JSONObject jObj;
		try {
			jObj = new JSONObject(str);
			Version latestVerion = new Version();
			latestVerion.setCode(jObj.getInt("code"));
			latestVerion.setName(jObj.getString("name"));
			latestVerion.setUrl(jObj.getString("url"));
			latestVerion.setMessage(jObj.getString("message"));
			return latestVerion;
		}catch(JSONException je) {
			je.printStackTrace();
		}
		return null;
	}
	
	//输入流转换
	StringBuffer toStringBuffer(InputStream is) throws IOException{
	    if( null == is) return null;
	    BufferedReader in = new BufferedReader(new InputStreamReader(is));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = in.readLine()) != null){
		      buffer.append(line).append("\n");
		}
		is.close();
		return buffer;
	}
	
	/**
	 * 将从服务器获取到的版本号与当前应用的比较
	 * @param version
	 * @return true if need to update
	 */
	boolean comparedWithCurrentPackage(Version version){
		if(version == null) return false;
		int currentVersionCode = 0;
		try {
			PackageInfo pkg = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			currentVersionCode = pkg.versionCode;
		} catch (NameNotFoundException exp) {
			exp.printStackTrace();
		}
		return version.getCode() > currentVersionCode;
	}
	
}
