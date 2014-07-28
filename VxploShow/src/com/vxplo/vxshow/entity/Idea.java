package com.vxplo.vxshow.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Idea implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1274763098528595382L;
	
	private String nid;
	private String title;
	private String imageUrl;
	private String playUrl;
	private boolean editable;
	private String currentPriviledge;
	private boolean canSetToPrivate;
	private String currentCategory;
	private transient List<Option> opts;
	private String shortUrl;
	private boolean likeFlag;
	
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	public void setLikeFlag(String likeFlag) {
		this.likeFlag = likeFlag.equals("like");
	}
	public boolean isLikeFlag() {
		return likeFlag;
	}

	public String getCurrentPriviledge() {
		return currentPriviledge;
	}
	public void setCurrentPriviledge(String currentPriviledge) {
		this.currentPriviledge = currentPriviledge;
	}
	public boolean isCanSetToPrivate() {
		return canSetToPrivate;
	}
	public void setCanSetToPrivate(boolean canSetToPrivate) {
		this.canSetToPrivate = canSetToPrivate;
	}
	public String getCurrentCategory() {
		return currentCategory;
	}
	public void setCurrentCategory(String currentCategory) {
		this.currentCategory = currentCategory;
	}
	public List<Option> getOpts() {
		return opts;
	}
	public void setOpts(List<Option> opts) {
		this.opts = opts;
	}
	public String getShortUrl() {
		return shortUrl;
	}
	public void setShortUrl(String shortUrl) {
		this.shortUrl = shortUrl;
	}
	public String getNid() {
		return nid;
	}
	public void setNid(String nid) {
		this.nid = nid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getPlayUrl() {
		return playUrl;
	}
	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}
	
	public static List<Idea> getIdeaListFromJsonArray(String jsonArr) {
		List<Idea> list = new ArrayList<Idea>();
		JSONArray array;
		Idea idea;
		JSONObject obj;
		try {
			array = new JSONArray(jsonArr);
			int len = array.length();
			for(int i=0;i<len;i++) {
				idea = new Idea();
				obj = array.optJSONObject(i);
				idea.setNid(obj.optString("nid"));
				idea.setTitle(obj.optString("title"));
				idea.setImageUrl(obj.optString("image"));
				idea.setPlayUrl(obj.optString("play_url"));
				list.add(idea);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}
	
	public static void setIdeaListFromJsonArray(List<Idea> ideas, String jsonArr) {
		if(ideas.size() > 0) {
			ideas.clear();
		}
		JSONArray array;
		Idea idea;
		JSONObject obj;
		try {
			array = new JSONArray(jsonArr);
			int len = array.length();
			for(int i=0;i<len;i++) {
				idea = new Idea();
				obj = array.optJSONObject(i);
				idea.setNid(obj.optString("nid"));
				idea.setTitle(obj.optString("title"));
				idea.setImageUrl(obj.optString("image"));
				idea.setPlayUrl(obj.optString("play_url"));
				ideas.add(idea);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String[] getOptValues() {
		if(null==this.opts) {
			return null;
		}
		int len = opts.size();
		String[] strs = new String[len];
		for(int i=0;i<len;i++) {
			strs[i] = opts.get(i).getoName();
		}
		return strs;
	}
}
