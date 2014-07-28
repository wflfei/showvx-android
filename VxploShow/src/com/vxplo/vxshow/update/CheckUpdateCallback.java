package com.vxplo.vxshow.update;

import com.vxplo.vxshow.entity.Version;

public interface CheckUpdateCallback {
	
	void onFoundLatestVersion(Version version);
	void onCurrentIsLatest();

}
