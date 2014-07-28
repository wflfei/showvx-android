package com.vxplo.vxshow.util;

import android.widget.EditText;

public class ValidateUtil {
	public static boolean isEditEmpty(EditText ...edits) {
		String value = "";
		for(EditText edit : edits) {
			value = edit.getText().toString().trim();
			if(StringUtil.isStringEmptyOrNull(value)) {
				return true;
			}
		}
		return false;
	}
}
