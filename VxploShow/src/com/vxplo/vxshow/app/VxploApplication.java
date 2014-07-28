package com.vxplo.vxshow.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.vxplo.vxshow.activity.MainActivity;
import com.vxplo.vxshow.activity.MainActivity.ListType;
import com.vxplo.vxshow.entity.Idea;
import com.vxplo.vxshow.http.VxHttpCallback;
import com.vxplo.vxshow.http.VxHttpRequest;
import com.vxplo.vxshow.http.VxHttpRequest.VxHttpMethod;
import com.vxplo.vxshow.http.VxHttpTask.ErrorStatus;
import com.vxplo.vxshow.http.constant.Constant;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.NetworkUtil;
import com.vxplo.vxshow.util.NotificationUtil;
import com.vxplo.vxshow.util.fileupload.FileUpload;
import com.vxplo.vxshow.util.fileupload.UploadTask;

public class VxploApplication extends Application {

	private static VxploApplication application;
	public static Resources resources;
	public static SharedPreferences pref;
	public static ImageLoader imageLoader;
	public static int networkState;
	private List<Activity> activities;
	private List<Idea> inspiredIdeas = new ArrayList<Idea>();
	private List<Idea> myIdeas = new ArrayList<Idea>();
	private List<Idea> favouriteIdeas = new ArrayList<Idea>();
	
	public static VxploApplication getInstance() {
		return application;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		activities = new ArrayList<Activity>();
		resources = getResources();
		pref = getSharedPreferences("session", MODE_PRIVATE);

		initImageLoader();

		Log.v("VxploApplication", "Application onCreate End!" + new Date());
		//getToken(null);
	}

	private void initImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
        .displayer(new RoundedBitmapDisplayer(16)) // default
        .cacheInMemory()
        .cacheOnDisc()
        .build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.defaultDisplayImageOptions(options)
			.build();

		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	public void getToken(Runnable callback) {
		final Runnable cb = callback;
		VxHttpRequest request = new VxHttpRequest(getApplicationContext(), VxHttpMethod.POST, Constant.getUserTokenUrl());
		request.setCallback(new VxHttpCallback(){

			@Override
			public void beforeSend() {
				// TODO Auto-generated method stub

			}

			@Override
			public void success(String result) {
				// TODO Auto-generated method stub
				Log.d("token", result+"");
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
					String token = jObj.optString("token");
					pref.edit().putString("token", token).commit();
					if (cb != null)
						cb.run();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void complete() {
				// TODO Auto-generated method stub

			}

			@Override
			public void error(ErrorStatus status, Exception... exs) {
				// TODO Auto-generated method stub
				System.out.println(status.name());
			}

		});
		request.send();
	}

	public void getIdeaList(ListType type, final DataLoadingCallBack callback)
	{
		final ListType listType = (type == null) ? ListType.INSPIRED : type;
		VxHttpRequest request = new VxHttpRequest(this, VxHttpMethod.GET,
				Constant.getIdeaListUrl(listType));
		request.setCallback(new VxHttpCallback(){

			@Override
			public void beforeSend() {
				// TODO Auto-generated method stub
				//DialogUtil.showLoadingDialog(getActivity());
			}

			@Override
			public void success(String result) {
				// TODO Auto-generated method stub
				if(listType == ListType.INSPIRED) {
					if(inspiredIdeas != null && inspiredIdeas.size() > 0) {
						inspiredIdeas.clear();
					}
					Idea.setIdeaListFromJsonArray(inspiredIdeas, result);
				}else if(listType == ListType.PROJECTS) {
					if(myIdeas != null && myIdeas.size() > 0) {
						myIdeas.clear();
					}
					Idea.setIdeaListFromJsonArray(myIdeas, result);
				}else if(listType == ListType.FAVORITES) {
					if(favouriteIdeas != null && favouriteIdeas.size() > 0) {
						favouriteIdeas.clear();
					}
					Idea.setIdeaListFromJsonArray(favouriteIdeas, result);
				}
				Log.v("Appliction", "Project result: " + result);
				callback.onSuccess();
			}

			@Override
			public void error(ErrorStatus status, Exception... exs) {
				// TODO Auto-generated method stub
				if (exs.length > 0) {
					DialogUtil.showToast(VxploApplication.getInstance(), exs[0].getMessage());
					return;
				}
				DialogUtil.showToast(VxploApplication.getInstance(), status.name());
				callback.onFail();
			}

			@Override
			public void complete() {
				
			}

		});
		request.send();
	}
	
	public List<Idea> getIdeasList(ListType type) {
		if(type == ListType.INSPIRED) {
			return inspiredIdeas;
		}else if(type == ListType.PROJECTS) {
			return myIdeas;
		}else if(type == ListType.FAVORITES) {
			return favouriteIdeas;
		}else {
			return inspiredIdeas;
		}
	}
	
	public String getUserToken() {
		return pref.getString("token", "");
	}

	public void setSession(String name,String value) {
		pref.edit().putString(name, value).commit();
	}

	public void removeSession(String name) {
		pref.edit().remove(name);
	}

	public void addActivity(Activity activity) {
		if(null!=activities) {
			activities.add(activity);
		}
	}

	public void removeActivity(Activity activity) {
 		if(null!=activities) {
			activities.remove(activity);
			activity.finish();
		}
	}
	
	public Activity getTopActivity() {
		if(null != activities && activities.size() > 0) {
			return activities.get(activities.size() - 1);
		}
		return null;
	}
	
	public MainActivity getMainActivity() {
		for(Activity activity : activities) {
			if(activity instanceof MainActivity) {
				return (MainActivity) activity;
			}
		}
		return null;
	}

	public void removeAllActivities() {
		if(null!=activities) {
			for(Activity act : activities) {
				act.finish();
			}
			activities.clear();
		}
	}
	
	
	public void appExit() {
		UploadTask.getInstance().removeAllUploadTask();
		removeAllActivities();
		System.exit(0);
	}

	public SharedPreferences getPref(String name) {
		return getSharedPreferences(name, MODE_PRIVATE);
	}
	
	/**
	 * 加载数据的回调
	 * @author lin
	 *
	 */
	public interface DataLoadingCallBack {
		void onSuccess();
		void onFail();
	}

}
