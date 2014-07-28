package com.vxplo.vxshow.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

public class Option {

	private String oId;
	private String oName;
	
	public String getoId() {
		return oId;
	}
	public void setoId(String oId) {
		this.oId = oId;
	}
	public String getoName() {
		return oName;
	}
	public void setoName(String oName) {
		this.oName = oName;
	}
	
	public static List<Option> getOptionsFromJsonObj(JSONObject obj) {
		List<Option> opts = new ArrayList<Option>();
		Option opt = null;
		Iterator<String> it = obj.keys();
		while(it.hasNext()) {
			opt = new Option();
			String oId = it.next();
			opt.oId = oId;
			opt.oName = obj.optString(oId);
			opts.add(opt);
		}
		return opts;
	}
	
}
