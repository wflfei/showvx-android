package com.vxplo.vxshow.util;

public class StringUtil {
	
	public static boolean isStringEmptyOrNull(String str) {
		if(null==str) {
			return true;
		}
		if("".equals(str.trim())) {
			return true;
		}
		return false;
	}
	
}
