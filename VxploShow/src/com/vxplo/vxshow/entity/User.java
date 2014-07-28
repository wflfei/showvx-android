package com.vxplo.vxshow.entity;

import org.json.JSONObject;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.vxplo.vxshow.app.VxploApplication;

public class User {
	
	private int uid;
	private String userName;
	private String userMail;
	private String userToken;
	private String avatarUrl;
	private String nickName;
	private static User cUser;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getUserMail() {
		return userMail;
	}
	public void setUserMail(String userMail) {
		this.userMail = userMail;
	}
	public String getAvatarUrl() {
		return avatarUrl;
	}
	public void setAvatarUri(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
	public static void setUserInfoByJson(JSONObject obj) {
		if(null==cUser) {
			cUser = new User();
		}
		cUser.uid = obj.optInt("uid");
		cUser.userName = obj.optString("name");
		cUser.userMail = obj.optString("mail");
		cUser.nickName = obj.optJSONObject("field_first_name").optJSONArray("und").optJSONObject(0).optString("value");
		JSONObject picture = obj.optJSONObject("picture");
		if(picture != null)
		cUser.avatarUrl = picture.optString("url");
		else 
			cUser.avatarUrl = obj.optString("picture");
		
		saveUser();
	}
	
	private static void saveUser() {
		SharedPreferences userPref = VxploApplication.getInstance().getPref("user");
		Editor editor = userPref.edit();
		editor.putBoolean("auto", true);
		editor.putInt("uid", cUser.uid);
		editor.putString("userName", cUser.userName);
		editor.putString("userMail", cUser.userMail);
		editor.putString("nickName", cUser.nickName);
		editor.putString("avatarUrl", cUser.avatarUrl);
		editor.commit();
	}
	
	public static User getUserFromPref() {
		SharedPreferences userPref = VxploApplication.getInstance().getPref("user");
		User cUser = new User();
		cUser.avatarUrl = userPref.getString("avatarUrl", "");
		cUser.nickName = userPref.getString("nickName", "");
		cUser.uid = userPref.getInt("uid", 0);
		cUser.userMail = userPref.getString("userMail", "");
		cUser.userName = userPref.getString("userName", "");
		cUser.userToken = VxploApplication.getInstance().getUserToken();
		
		return cUser;
	}
	
	public static void setCurrentUser(User user) {
		cUser = user;
	}
	
	public static User getCurrentUser() {
		if(null==cUser) {
			cUser = getUserFromPref();
		}
		return cUser;
	}
	
	public static void clearUser() {
		cUser = null;
		SharedPreferences userPref = VxploApplication.getInstance().getPref("user");
		Editor editor = userPref.edit();
		editor.clear();
		editor.commit();
	}
}
