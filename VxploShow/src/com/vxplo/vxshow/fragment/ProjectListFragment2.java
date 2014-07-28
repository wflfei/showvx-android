package com.vxplo.vxshow.fragment;

import java.util.ArrayList;
import java.util.List;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity;
import com.vxplo.vxshow.activity.ProjectShowActivity;
import com.vxplo.vxshow.activity.MainActivity.ListType;
import com.vxplo.vxshow.adapter.ListGridAdapter;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.app.VxploApplication.DataLoadingCallBack;
import com.vxplo.vxshow.entity.Idea;
import com.vxplo.vxshow.http.VxHttpCallback;
import com.vxplo.vxshow.http.VxHttpRequest;
import com.vxplo.vxshow.http.VxHttpRequest.VxHttpMethod;
import com.vxplo.vxshow.http.VxHttpTask.ErrorStatus;
import com.vxplo.vxshow.http.constant.Constant;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.NetworkUtil;
import com.vxplo.vxshow.widget.CirclePageIndicator;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.RelativeLayout;

public class ProjectListFragment2 extends Fragment {
	private VxploApplication mApplicaiton;
	private MainActivity mActivity;
	private List<Idea> ideas;
	private ListType type;
	
	private GridView gridView;
	private ListGridAdapter gridAdapter;

	public ProjectListFragment2() {
		super();
	}
	
	public static ProjectListFragment2 newInstance(ListType type) {
		ProjectListFragment2 fragment = new ProjectListFragment2();
		fragment.setListType(type);
		return fragment;
	}
	
	public void setListType(ListType type) {
		this.type = type;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		mApplicaiton = VxploApplication.getInstance();
		mActivity = (MainActivity) getActivity();
		initIdeasList();
		gridAdapter = new ListGridAdapter(mActivity, ideas);
		
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		gridView = (GridView) inflater.inflate(R.layout.list_gridview_layout, null);
		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(mActivity, ProjectShowActivity.class);
				intent.putExtra("idea", ideas.get(position));
				mActivity.startActivity(intent);
			}
		});
		return gridView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private void initIdeasList() {
		ideas = mApplicaiton.getIdeasList(type);
		if(ideas.size() == 0) {
			if(NetworkUtil.getNetworkState(mActivity) != NetworkUtil.NETWORN_NONE) {
				refreshIdeasList();
			}
		}
	}

	/**
	 * 在ideas为null的情况下，调用VxploApplication的getIdeasList（）方法从网络获取对应的数据再赋引用给ideas
	 */
	public void refreshIdeasList()
	{
		DialogUtil.showLoadingDialog(getActivity());
		mApplicaiton.getIdeaList(type, new DataLoadingCallBack() {
			
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				//mActivity.networkEnable();
				ideas = mApplicaiton.getIdeasList(type);
				gridAdapter.notifyDataSetChanged();
				DialogUtil.closeLoadingDialog();
			}
			
			@Override
			public void onFail() {
				// TODO Auto-generated method stub
				Log.v("Fragment", "refreshData Failed!");
				DialogUtil.closeLoadingDialog();
			}
		});
	}
}
