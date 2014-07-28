package com.vxplo.vxshow.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.db.VxshowDbManager;
import com.vxplo.vxshow.db.VxshowSqliteOpenHelper;
import com.vxplo.vxshow.fragment.HistoryFragment;
import com.vxplo.vxshow.fragment.ImageHistoryFragment;
import com.vxplo.vxshow.fragment.RecordsHistoryFragment;
import com.vxplo.vxshow.fragment.VideoHistoryFragment;
import com.vxplo.vxshow.util.FileUtil;

public class HistoryActivity extends FragmentActivity implements TabListener{
	private VxploApplication mApplication;
	private FragmentManager fragManager;
	private ViewPager viewPager;
	private List<Fragment> mFragments;
	private HistoryFragment videoFragment, imageFragment, recordsFragment;
	protected VxshowDbManager dbManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		mApplication = VxploApplication.getInstance();
		
		init();
		initActionBar();
		mApplication.addActivity(this);
		dbManager = new VxshowDbManager(mApplication);
	}

	private void init() {
		// TODO Auto-generated method stub
		mFragments = new ArrayList<Fragment>();
		videoFragment = VideoHistoryFragment.newInstance();
		mFragments.add(videoFragment);
		imageFragment = ImageHistoryFragment.newInstance();
		mFragments.add(imageFragment);
		recordsFragment = RecordsHistoryFragment.newInstance();
		mFragments.add(recordsFragment);
		viewPager = (ViewPager) findViewById(R.id.history_viewpaer);
		fragManager = getSupportFragmentManager();
		viewPager.setAdapter(new HistoryPagerAdapter(fragManager));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				getActionBar().setSelectedNavigationItem(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	private void initActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayShowCustomEnabled(true);
		Button cleanBtn = new Button(this);
		//cleanBtn.setBackgroundColor(VxploApplication.resources.getColor(R.color.yellow));
		cleanBtn.setText(R.string.clean_videos);
		cleanBtn.setId(13244);
		cleanBtn.setTextColor(VxploApplication.resources.getColor(R.color.white));
		cleanBtn.setOnClickListener(cleanClick);
		cleanBtn.setBackgroundResource(R.drawable.selector_vxred);
		android.app.ActionBar.LayoutParams layout = new android.app.ActionBar.LayoutParams(Gravity.RIGHT);
		layout.setMargins(0, 0, 20, 0);
		actionBar.setCustomView(cleanBtn, layout);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		ActionBar.Tab tab1 = actionBar.newTab();
        tab1.setText(R.string.videos);
        tab1.setTabListener(this);
        actionBar.addTab(tab1);
        ActionBar.Tab tab2 = actionBar.newTab();
        tab2.setText(R.string.images);
        tab2.setTabListener(this);
        actionBar.addTab(tab2);
        ActionBar.Tab tab3 = actionBar.newTab();
        tab3.setText(R.string.audios);
        tab3.setTabListener(this);
        actionBar.addTab(tab3);
        
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home) {
			mApplication.removeActivity(this);
		} else if(id == 13244) {
			Log.v("13244", "13244");
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}
	
	class HistoryPagerAdapter extends FragmentPagerAdapter {

		public HistoryPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			// TODO Auto-generated method stub
			return mFragments.get(arg0);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mFragments.size();
		}
		
	}
	
	OnClickListener cleanClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			int selection = getActionBar().getSelectedNavigationIndex();
			switch(selection) {
				case 0:
					FileUtil.cleanDirecory(FileUtil.getVxVideosDir());
					dbManager.cleanUploadHistory(MediaType.VIDEO);
					videoFragment.refresh();
					break;
				case 1:
					FileUtil.cleanDirecory(FileUtil.getVxImagesDir());
					dbManager.cleanUploadHistory(MediaType.IMAGE);
					imageFragment.refresh();
					break;
				case 2:
					FileUtil.cleanDirecory(FileUtil.getVxRecordsDir());
					dbManager.cleanUploadHistory(MediaType.AUDIO);
					recordsFragment.refresh();
					break;
				default:
					break;
			}
		}
	};
	
}
