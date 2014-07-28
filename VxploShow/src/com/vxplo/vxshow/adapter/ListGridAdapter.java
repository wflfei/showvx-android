package com.vxplo.vxshow.adapter;

import java.util.List;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.entity.Idea;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListGridAdapter extends BaseAdapter{
	private Context context;
	private List<Idea> ideas;
	public ListGridAdapter(Context context, List<Idea> ideas) {
		this.context = context;
		this.ideas = ideas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ideas.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ideas.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Idea idea = ideas.get(position);
		RelativeLayout relativeLayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.list_item_layout, null);
		ImageView image = (ImageView) relativeLayout.findViewById(R.id.item_image);
		TextView text = (TextView) relativeLayout.findViewById(R.id.item_text);
		VxploApplication.imageLoader.displayImage(idea.getImageUrl(), image);
		text.setText(idea.getTitle());
		return relativeLayout;
	}

}
