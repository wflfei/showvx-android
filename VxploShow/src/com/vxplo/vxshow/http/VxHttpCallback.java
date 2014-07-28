package com.vxplo.vxshow.http;

import com.vxplo.vxshow.http.VxHttpTask.ErrorStatus;

public interface VxHttpCallback {
	void beforeSend();
	void success(String result) throws Exception;
	void error(ErrorStatus status,Exception ...exs);
	void complete();
}
