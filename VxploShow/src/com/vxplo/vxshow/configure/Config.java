package com.vxplo.vxshow.configure;

public class Config {
	
	public static boolean IS_DEBUG = true;
	
	public static String BASE_URL = "";
	public static String BASE_URL_TEST = "http://www.vxplo.cn";
	
	public static final String APP_ID = "wxe34af3d8156d47f6";
	
	public static String getBaseUrl() {
		if(IS_DEBUG) {
			return BASE_URL_TEST;
		}
		return BASE_URL;
	}

}
