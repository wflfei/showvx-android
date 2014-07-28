package com.vxplo.vxshow.fragment;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.HistoryActivity;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.entity.History;

public class ImageHistoryFragment extends HistoryFragment {
	private List<History> imageHistorys;
	private ImageLoader albumLoader;
	private HistoryActivity mActivity = (HistoryActivity)getActivity();
	
	public static ImageHistoryFragment newInstance() {
		ImageHistoryFragment instance = new ImageHistoryFragment();
		return instance;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_launcher) // 在ImageView加载过程中显示图片  
        .showImageForEmptyUri(R.drawable.ic_launcher) // image连接地址为空时  
        .showImageOnFail(R.drawable.ic_launcher) // image加载失败  
        .cacheInMemory()
        .cacheOnDisc()
        .displayer(new FadeInBitmapDisplayer(16)) // default
        .build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(VxploApplication.getInstance())
			.defaultDisplayImageOptions(options)
			.build();

		albumLoader = ImageLoader.getInstance();
		albumLoader.init(config);
		imageHistorys = dbManager.getImageUploadPath();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listView = new ListView(getActivity());
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		adapter = new ImageHistoryAdapter(inflater);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Uri uri = Uri.parse("file://" + imageHistorys.get(position).getPath());   
				//调用系统图库或其他
			    Intent intent = new Intent(Intent.ACTION_VIEW);  
			    Log.v("URI:::::::::", uri.toString());  
			    intent.setDataAndType(uri, "image/*");  
			    startActivity(intent);  
			}
		});
		return listView;
	}
	
	
	class ImageHistoryAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		public ImageHistoryAdapter(LayoutInflater inflater) {
			this.inflater = inflater;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return imageHistorys.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return imageHistorys.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if(convertView == null) {
				convertView  = inflater.inflate(R.layout.image_history_item, null);
				viewHolder = new ViewHolder();
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_history_item_img);
				viewHolder.textView = (TextView) convertView.findViewById(R.id.image_history_item_text);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			albumLoader.displayImage("file://" + imageHistorys.get(position).getPath(), viewHolder.imageView);
			viewHolder.textView.setText(imageHistorys.get(position).getPath());
			return convertView;
		}
		
		class ViewHolder {
			public ImageView imageView;
			public TextView textView;
		}
		
	}
	
	@Override
	public void refresh() {
		imageHistorys = dbManager.getImageUploadPath();
		super.refresh();
	}

}


