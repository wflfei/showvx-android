package com.vxplo.vxshow.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.adapter.AlbumGridViewAdapter;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.db.VxshowSqliteOpenHelper;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.fileupload.FileUpload;
import com.vxplo.vxshow.util.fileupload.UploadTask;
import com.vxplo.vxshow.util.imageloader.ImageManager;

import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ToggleButton;

public class AlbumActivity extends VxBaseActivity{
	
	private GridView gridView;
	private ArrayList<HashMap<String, String>> dataList = new ArrayList<HashMap<String, String>>();
	private HashMap<String,ImageView> hashMap = new HashMap<String,ImageView>();
	private ArrayList<HashMap<String, String>> selectedDataList = new ArrayList<HashMap<String, String>>();
	private List<String> paths;
	private ProgressBar progressBar;
	private AlbumGridViewAdapter gridImageAdapter;
	private LinearLayout selectedImageLayout;
	private HorizontalScrollView scrollview;
	private int size = 6 ;
	private VxshowSqliteOpenHelper dbHelper;
	private List<String> uploadedPath;
	private boolean hasUploaded = false;
	
	public static ImageLoader albumLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);
		dbHelper = new VxshowSqliteOpenHelper(this);
		initAlbumImageLoader();
		initActionBar();
		selectedDataList = new ArrayList<HashMap<String, String>>();
		init();
		initListener();
	}

	private void initActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		Button okBtn = new Button(ctx);
		//okBtn.setBackgroundColor(VxploApplication.resources.getColor(R.color.yellow));
		okBtn.setText(R.string.done);
		okBtn.setId(13244);
		okBtn.setTextColor(VxploApplication.resources.getColor(R.color.white));
		okBtn.setOnClickListener(new AlbumOnClickListener());
		okBtn.setBackgroundResource(R.drawable.selector_vxred);
		okBtn.setEnabled(false);
		LayoutParams layout = new LayoutParams(Gravity.RIGHT);
		layout.setMargins(0, 0, 20, 0);
		actionBar.setCustomView(okBtn, layout);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setBackgroundDrawable(VxploApplication.resources.getDrawable(R.drawable.vxred));
	}

	private void init() {
		progressBar = (ProgressBar)findViewById(R.id.progressbar);
		progressBar.setVisibility(View.GONE);
		gridView = (GridView)findViewById(R.id.myGrid);
		gridImageAdapter = new AlbumGridViewAdapter(this, dataList,selectedDataList);
		gridView.setAdapter(gridImageAdapter);
		refreshData();
		selectedImageLayout = (LinearLayout)findViewById(R.id.selected_image_layout);
		scrollview = (HorizontalScrollView)findViewById(R.id.scrollview);
		initSelectImage();
		paths = new ArrayList<String>();
		initUploadedPath();
	}
	
	
	public void initAlbumImageLoader() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.ic_launcher) // 在ImageView加载过程中显示图片  
        .showImageForEmptyUri(R.drawable.ic_launcher) // image连接地址为空时  
        .showImageOnFail(R.drawable.ic_launcher) // image加载失败  
        .cacheInMemory()
        .cacheOnDisc()
        .displayer(new FadeInBitmapDisplayer(16)) // default
        .build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
			.defaultDisplayImageOptions(options)
			.build();

		albumLoader = ImageLoader.getInstance();
		albumLoader.init(config);
	}
	
	private void initSelectImage() {
		if(selectedDataList==null)
			return;
		for(final HashMap<String, String> map:selectedDataList){
			final String path = map.get("paht");
			ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout,false);
			selectedImageLayout.addView(imageView);			
			hashMap.put(path, imageView);
			albumLoader.displayImage("file:///" + path, imageView);
			imageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					removePath(map);
					gridImageAdapter.notifyDataSetChanged();
				}
			});
		}
	}

	private void initListener() {
		
		gridImageAdapter.setOnItemClickListener(new AlbumGridViewAdapter.OnItemClickListener() {
			
			@Override
			public void onItemClick(final ToggleButton toggleButton, int position, final HashMap<String, String> thum, final boolean isChecked) {
				final String path = thum.get("path");
				
				ViewGroup pGroup = (ViewGroup) toggleButton.getParent() ;
				final ImageView check = (ImageView) pGroup.findViewById(R.id.check) ;
				if(isChecked){
					if(selectedDataList.size()>=size){
						toggleButton.setChecked(false);
						if(!removePath(thum)){
							Toast.makeText(AlbumActivity.this, "只能选择"+size+"张图片", 200).show();
						}
						return;
					}
					if(hasUploaded(path)) {
						DialogUtil.showToast(mApplication, "该图片已经上传");
						return;
					}
					check.setVisibility(View.VISIBLE) ;
					if(!hashMap.containsKey(path)){
						ImageView imageView = (ImageView) LayoutInflater.from(AlbumActivity.this).inflate(R.layout.choose_imageview, selectedImageLayout,false);
						selectedImageLayout.addView(imageView);
						imageView.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								
								int off = selectedImageLayout.getMeasuredWidth() - scrollview.getWidth();  
							    if (off > 0) {  
							    	  scrollview.smoothScrollTo(off, 0); 
							    } 
							}
						}, 100);
						if(selectedDataList.size() == 0) {
							scrollview.setVisibility(View.VISIBLE);
							getActionBar().getCustomView().setEnabled(true);
						}
						/*
						ContentResolver cr = getContentResolver();
						String image_id = thum.get("image_id");  
						String[] projection = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };  
						Cursor cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,  
								MediaStore.Images.Media._ID + "=" + image_id, null, null);  
						if (cursor != null) {  
							cursor.moveToFirst();  
							String imagePath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));  
							paths.add(imagePath);
						} else {  
							Toast.makeText(ctx, "Image doesn't exist!", Toast.LENGTH_SHORT).show();  
						}*/
						paths.add(path);
						hashMap.put(path, imageView);
						selectedDataList.add(thum);
						albumLoader.displayImage("file:///" + path, imageView);
						imageView.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								toggleButton.setChecked(false);
								removePath(thum);
								check.setVisibility(View.GONE) ;
							}
						});
					}
				}else{
					check.setVisibility(View.GONE) ;
					removePath(thum);
				}
			}

		});
		
	}

	
	private boolean removePath(HashMap<String, String> map){
		String path = map.get("path");
		if(hashMap.containsKey(path)){
			selectedImageLayout.removeView(hashMap.get(path));
			hashMap.remove(path);
			removeOneData(selectedDataList,path);
			paths.remove(path);
			if(selectedDataList.size() == 0) {
				scrollview.setVisibility(View.INVISIBLE);
				getActionBar().getCustomView().setEnabled(false);
			}
			return true;
		}else{
			return false;
		}
	}
	
	private void removeOneData(ArrayList<HashMap<String, String>> arrayList,String s){
		for(int i =0;i<arrayList.size();i++){
			if(arrayList.get(i).get("path").equals(s)){
				arrayList.remove(i);
				return;
			}
		}
	}
	
    private void refreshData(){
    	new AsyncTask<Void, Void, ArrayList<HashMap<String, String>>>(){
    		
    		@Override
    		protected void onPreExecute() {
    			progressBar.setVisibility(View.VISIBLE);
    			super.onPreExecute();
    		}

			@Override
			protected ArrayList<HashMap<String, String>> doInBackground(Void... params) {
				ArrayList<HashMap<String, String>> listDirlocal =  listAlldirDirectly();
				return listDirlocal;
			}
			
			protected void onPostExecute(ArrayList<HashMap<String, String>> tmpList) {
				
				if(AlbumActivity.this==null||AlbumActivity.this.isFinishing()){
					return;
				}
				progressBar.setVisibility(View.GONE);
				dataList.clear();
				dataList.addAll(tmpList);
				gridImageAdapter.notifyDataSetChanged();
				return;
			};
    		
    	}.execute();
    	
    }
    
    /**
     * 获取所有图片的缩略图路径
     * @return 缩略图路径和对应的原图片的id
     */
    private ArrayList<HashMap<String, String>>  listAlldir(){
    	Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI);
    	Uri uri = intent.getData();
    	int image_id;
    	String thum_data;
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	String[] proj ={MediaStore.Images.Thumbnails.DATA, MediaStore.Images.Thumbnails.IMAGE_ID};
    	Cursor cursor = managedQuery(uri, proj, null, null, null);
    	while(cursor.moveToNext()){
    		HashMap<String, String> map = new HashMap<String, String>();
    		thum_data =cursor.getString(0);
    		image_id = cursor.getInt(1);
    		Log.v("image_id", image_id + "");
    		map.put("path", thum_data);
    		map.put("image_id", image_id + "");
    		list.add(map);
    	}
		return list;
    }
    
    /**
     * 直接获取所有图片的路径，测试加载效果
     * @return 直接返回原图片的路径
     */
    private ArrayList<HashMap<String, String>>  listAlldirDirectly(){
    	Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    	Uri uri = intent.getData();
    	int image_id;
    	String thum_data;
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
    	String[] proj ={MediaStore.Images.Media.DATA};
    	ContentResolver contentR = getContentResolver();
    	Cursor cursor = managedQuery(uri, proj, null, null, MediaStore.Images.Media.DATE_ADDED + " desc");
    	while(cursor.moveToNext()){
    		HashMap<String, String> map = new HashMap<String, String>();
    		thum_data =cursor.getString(0);
    		image_id = 1;
    		Log.v("image_id", image_id + "");
    		map.put("path", thum_data);
    		map.put("image_id", image_id + "");
    		list.add(map);
    	}
		return list;
    }

   	@Override
   	public boolean onOptionsItemSelected(MenuItem item) {
   		// TODO Auto-generated method stub
   		switch (item.getItemId()) {
   		case android.R.id.home:
   			finish();
   			return true;

   		}
   		return true;
   	}
    
    @Override
    public void onBackPressed() {
    	finish();
//    	super.onBackPressed();
    }
    
    @Override
    public void finish() {
    	// TODO Auto-generated method stub
    	super.finish();
//    	albumLoader.recycle(dataList);
    }
    
    @Override
    protected void onDestroy() {
    	ImageManager.from(this).mMemoryCache.trimToSize(0);
    	super.onDestroy();
    }
    
    class AlbumOnClickListener implements OnClickListener
    {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if(id == 13244) {
				upload();
			}
		}
    	
    }

	public void upload() {
		// TODO Auto-generated method stub
		UploadTask.getInstance().addUploadTask(new FileUpload(mApplication, MediaType.IMAGE).upload(paths));
		finish();
	}
	
	public boolean hasUploaded(String path) {
		/*
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction();
		Cursor cursor = db.rawQuery("select * from uploadfile where path = ?;", new String[] {path});
		if(cursor.moveToNext()) {
			return true;
		}
		db.endTransaction();
		cursor.close();
		db = null;*/
		return uploadedPath.contains(path);
	}
	
	private void initUploadedPath() {
		if(uploadedPath == null) {
			uploadedPath = new ArrayList<String>();
		}
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.beginTransaction();
		Cursor cursor = db.rawQuery("select path from uploadfile", null);
		while(cursor.moveToNext()) {
			Log.v("Uploaded Path", cursor.getString(0));
			uploadedPath.add(cursor.getString(0));
		}
		db.endTransaction();
		cursor.close();
		
	}

}
