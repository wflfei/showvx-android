package com.vxplo.vxshow.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.AlbumActivity;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.util.imageloader.ImageManager;

import android.R.color;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ToggleButton;

@SuppressLint("ResourceAsColor")
public class AlbumGridViewAdapter extends BaseAdapter implements
		OnClickListener {

	private Context mContext;
	private ArrayList<HashMap<String, String>> dataList;
	private ArrayList<HashMap<String, String>> selectedDataList;
	private DisplayMetrics dm;
	ImageLoader imageLoader;
	DisplayImageOptions options;

	public AlbumGridViewAdapter(Context c, ArrayList<HashMap<String, String>> dataList,
			ArrayList<HashMap<String, String>> selectedDataList) {

		mContext = c;
		this.dataList = dataList;
		this.selectedDataList = selectedDataList;
		dm = new DisplayMetrics();
		((Activity) mContext).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_launcher) // 在ImageView加载过程中显示图片  
        .showImageForEmptyUri(R.drawable.ic_launcher) // image连接地址为空时  
        .showImageOnFail(R.drawable.ic_launcher) // image加载失败  
        .cacheInMemory()
        .cacheOnDisc()
        .displayer(new FadeInBitmapDisplayer(16)) // default
        .build();

		imageLoader = ImageLoader.getInstance();
	}

	@Override
	public int getCount() {
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	/**
	 * 瀛樻斁鍒楄〃椤规帶浠跺彞鏌?	 */
	private class ViewHolder {
		public ImageView imageView,check;
		public ToggleButton toggleButton;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.select_imageview, parent, false);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.image_view);
			viewHolder.toggleButton = (ToggleButton) convertView
					.findViewById(R.id.toggle_button);
			viewHolder.check = (ImageView) convertView
					.findViewById(R.id.check);
			convertView.setLayoutParams(new android.widget.AbsListView.LayoutParams((dm.widthPixels-dipToPx(10)) / 3, (dm.widthPixels-dipToPx(10)) / 3));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		HashMap<String, String> map;
		String path;
		if (dataList != null && dataList.size() > position) {
			map = dataList.get(position);
			path = map.get("path");
			Log.v("AlbumGridViewAdapter", "position: " + position + "   path:" + path);
		}
		else
			path = "camera_default";
		if (path.contains("default")) {
			viewHolder.imageView.setImageResource(R.drawable.camera_default);
		} else {
			//imageLoader.displayImage("file:///" + path, viewHolder.imageView, options);
			ImageManager.from(mContext).displayImage(viewHolder.imageView,
					path, R.drawable.camera_default , (dm.widthPixels-dipToPx(10)) / 3, (dm.widthPixels-dipToPx(10)) / 3);
		}
		viewHolder.toggleButton.setTag(position);
		viewHolder.toggleButton.setOnClickListener(this);
		if (isInSelectedDataList(path)) {
			viewHolder.toggleButton.setChecked(true);
			viewHolder.toggleButton.setBackgroundColor(Color.TRANSPARENT) ;
			viewHolder.check.setVisibility(View.VISIBLE) ;
		} else {
			viewHolder.toggleButton.setChecked(false);
			viewHolder.toggleButton.setBackgroundColor(Color.TRANSPARENT) ;
			viewHolder.check.setVisibility(View.GONE) ;
		}

		return convertView;
	}

	private boolean isInSelectedDataList(String selectedString) {
		for (int i = 0; i < selectedDataList.size(); i++) {
			if (selectedDataList.get(i).get("path").equals(selectedString)) {
				return true;
			}
		}
		return false;
	}

	public int dipToPx(int dip) {
		return (int) (dip * dm.density + 0.5f);
	}

	@Override
	public void onClick(View view) {
		if (view instanceof ToggleButton) {
			ToggleButton toggleButton = (ToggleButton) view;
			int position = (Integer) toggleButton.getTag();
			if (dataList != null && mOnItemClickListener != null
					&& position < dataList.size()) {
				mOnItemClickListener.onItemClick(toggleButton, position,
						dataList.get(position), toggleButton.isChecked());
			}
		}
	}

	private OnItemClickListener mOnItemClickListener;

	public void setOnItemClickListener(OnItemClickListener l) {
		mOnItemClickListener = l;
	}

	public interface OnItemClickListener {
		public void onItemClick(ToggleButton view, int position, HashMap<String, String> thum,
				boolean isChecked);
	}

}
