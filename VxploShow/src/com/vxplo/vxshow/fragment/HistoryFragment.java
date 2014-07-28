package com.vxplo.vxshow.fragment;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.HistoryActivity;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.db.VxshowDbManager;
import com.vxplo.vxshow.util.FileUtil;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;


public class HistoryFragment extends Fragment {
	protected ListView listView;
	protected VxshowDbManager dbManager;
	protected HistoryActivity mActivity = (HistoryActivity)getActivity();
	protected BaseAdapter adapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbManager = new VxshowDbManager(getActivity());
	}
	
	public void refresh() {
		adapter.notifyDataSetChanged();
	}
	
}


