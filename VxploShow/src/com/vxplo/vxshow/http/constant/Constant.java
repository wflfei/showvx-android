package com.vxplo.vxshow.http.constant;

import com.vxplo.vxshow.activity.MainActivity.ListType;
import com.vxplo.vxshow.configure.Config;

public class Constant {
	
	private static final String[] LIST_TYPES = {"explore","own","fav"}; 
	
	private static final String URL_USER_LOGIN = "/app/user/login";
	private static final String URL_USER_LOGOUT = "/app/user/logout";
	private static final String URL_USER_SIGN_UP = "/app/user/register";
	private static final String URL_USER_GET_UID = "/app/get_uid_by_mail";
	private static final String URL_USER_TOKEN = "/app/user/token";
	private static final String URL_IDEA_LIST = "/app/project";
	private static final String URL_IDEA_LIKE = "/app/project/like";
	private static final String URL_IDEA_SEND = "/app/project/send";
	private static final String URL_IDEA_INFO = "/app/project/getProjectInfo";
	private static final String URL_IDEA_SETTING = "/app/project/projectSettings";
	private static final String URL_IDEA_REPORT = "/app/project/report";
	private static final String URL_FILE = "/app/file";
	
	
	private static String makeUrlByNid(String url, String nid) {
		return url.replace("nid", nid+"");
	}
	
	public static String getUserLoginUrl() {
		return Config.getBaseUrl() + URL_USER_LOGIN;
	}
	
	public static String getUserLogoutUrl() {
		return Config.getBaseUrl() + URL_USER_LOGOUT;
	}

	public static String getUserTokenUrl() {
		return Config.getBaseUrl() + URL_USER_TOKEN;
	}
	
	public static String getIdeaListUrl(ListType type) {
		return Config.getBaseUrl() + URL_IDEA_LIST + "?type=" + LIST_TYPES[type.ordinal()];
	}
	
	public static String getIdeaLikeUrl(String nid) {
		return Config.getBaseUrl() + makeUrlByNid(URL_IDEA_LIKE, nid);
	}
	
	public static String getUserSignUpUrl() {
		return Config.getBaseUrl() + URL_USER_SIGN_UP;
	}
	
	public static String getUserGetUidUrl() {
		return Config.getBaseUrl() + URL_USER_GET_UID;
	}

	public static String getIdeaLikeUrl() {
		return Config.getBaseUrl() + URL_IDEA_LIKE;
	}
	
	public static String getIdeaSendUrl() {
		return Config.getBaseUrl() + URL_IDEA_SEND;
	}
	
	public static String getIdeaInfoUrl() {
		return Config.getBaseUrl() + URL_IDEA_INFO;
	}
	
	public static String getIdeaSettingUrl() {
		return Config.getBaseUrl() + URL_IDEA_SETTING;
	}
	
	public static String getIdeaReportUrl() {
		return Config.getBaseUrl() + URL_IDEA_REPORT;
	}
	
	public static String getFileUploadUrl() {
		return Config.getBaseUrl() + URL_FILE;
	}
}
