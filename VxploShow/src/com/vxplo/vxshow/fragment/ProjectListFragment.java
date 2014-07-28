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

public class ProjectListFragment extends Fragment {
	private static final int ONE_PAGE = 16;
	private VxploApplication mApplicaiton;
	private MainActivity mActivity;
	private List<Idea> ideas;
	private int pages;
	private ListType type;
	
	private ViewPager viewPager;
	private CirclePageIndicator pageIndicator;
	private ProjectPagerAdapter projectPagerAdapter;
	private List<GridView> grids;

	public ProjectListFragment() {
		super();
	}
	
	public static ProjectListFragment newInstance(ListType type) {
		ProjectListFragment fragment = new ProjectListFragment();
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
		
		projectPagerAdapter = new ProjectPagerAdapter(grids);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		RelativeLayout content = (RelativeLayout) inflater.inflate(R.layout.fragment_project_list, null);
		viewPager = (ViewPager) content.findViewById(R.id.project_list_viewpaer);
		viewPager.setOffscreenPageLimit(5);
		viewPager.setAdapter(projectPagerAdapter);
		pageIndicator = (CirclePageIndicator) content.findViewById(R.id.project_list_indictor);
		pageIndicator.setViewPager(viewPager);
		return content;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onActivityCreated(savedInstanceState);
	}
	
	private void initIdeasList() {
		ideas = mApplicaiton.getIdeasList(type);
		if(ideas == null) {
			if(NetworkUtil.getNetworkState(mActivity) == NetworkUtil.NETWORN_NONE) {
				grids = new ArrayList<GridView>();
			}else {
				refreshIdeasList();
			}
		}else {
			createGrids();
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
				createGrids();
				projectPagerAdapter.notifyDataSetChanged();
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
	
	protected void createGrids() {
		int size = ideas.size();
		pages = (size % ONE_PAGE == 0) ? size / ONE_PAGE : size / ONE_PAGE + 1;
		if(grids == null) {
			grids = new ArrayList<GridView>();
		}
		if(grids != null && grids.size() > 0) {
			grids.clear();
		}
		for(int i=0; i<pages; i++) {
			List<Idea> onePageIdeas = new ArrayList<Idea>();
			for(int j=i*ONE_PAGE; j<(i+1)*ONE_PAGE && j<ideas.size(); j++) {
				onePageIdeas.add(ideas.get(j));
			}
			GridView gridView = (GridView) LayoutInflater.from(mActivity).inflate(R.layout.list_gridview_layout, null);
			final int start = i;
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent intent = new Intent(mActivity, ProjectShowActivity.class);
					intent.putExtra("idea", ideas.get(start*ONE_PAGE + position));
					mActivity.startActivity(intent);
				}
			});
			ListGridAdapter gridAdapter = new ListGridAdapter(mActivity, onePageIdeas);
			gridView.setAdapter(gridAdapter);
			grids.add(gridView);
		}
	}

	/**
	 * ViewPager的适配器</br>
	 * ((ViewPager)container).removeView((View)object);</br>
	 * 重写getItemPosition返回 POSITION_NONE;
	 * @author lin
	 *
	 */
	class ProjectPagerAdapter extends PagerAdapter
	{
		private List<GridView> gridViews;
		
		public ProjectPagerAdapter(List<GridView> gridViews) {
			this.gridViews = gridViews;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(grids != null) {
				//Log.e("gridViews size", gridViews.size() + "");
				return grids.size();
			}
				
			else {
				//Log.e("gridViews size", "0");
				return 0;
			}
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager)container).removeView((View)object);        //防止元素重叠
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(grids.get(position), 0);
			return grids.get(position);
		}
		
		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return POSITION_NONE;         //使notifyDataSerChanged有效
		}
		
	}
}
