package com.vxplo.vxshow.fragment;

import java.util.List;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.db.VxshowDbManager;
import com.vxplo.vxshow.entity.History;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RecordsHistoryFragment extends HistoryFragment {

	private ListView listView;
	private List<History> recordHistorys;
	
	public static RecordsHistoryFragment newInstance() {
		RecordsHistoryFragment instance = new RecordsHistoryFragment();
		return instance;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		recordHistorys = dbManager.getRecordUploadPath();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		listView = new ListView(getActivity());
		listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		adapter = new RecordHistoryAdapter(inflater); 
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Uri uri = Uri.parse("file://" + recordHistorys.get(position).getPath());   
				//调用系统自带的播放器  
			    Intent intent = new Intent(Intent.ACTION_VIEW);  
			    Log.v("URI:::::::::", uri.toString());  
			    intent.setDataAndType(uri, "audio/mpeg");  
			    startActivity(intent);  
			}
		});
		return listView;
	}
	
	class RecordHistoryAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		public RecordHistoryAdapter(LayoutInflater inflater) {
			this.inflater = inflater;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return recordHistorys.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return recordHistorys.get(position);
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
			viewHolder.imageView.setImageResource(R.drawable.music);
			viewHolder.textView.setText(recordHistorys.get(position).getPath());
			return convertView;
		}
		
		class ViewHolder {
			public ImageView imageView;
			public TextView textView;
		}
		
	}
	
	@Override
	public void refresh() {
		recordHistorys = dbManager.getRecordUploadPath();
		super.refresh();
	}

}
