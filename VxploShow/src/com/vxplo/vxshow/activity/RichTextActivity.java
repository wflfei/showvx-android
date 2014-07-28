package com.vxplo.vxshow.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.MainActivity.MediaType;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.util.fileupload.FileUpload;
import com.vxplo.vxshow.util.fileupload.UploadTask;

public class RichTextActivity extends VxBaseActivity {
	private final String url = "file:///android_asset/nic/nicEditor.html";
	private WebView webView;
	private Button okBtn, cancelBtn;
	private String html;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rich_text);
		
		initActionBar();
		
		findViews();
		initViews();
		
	}

	private void initActionBar() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	private void initViews() {
		// TODO Auto-generated method stub
		okBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getDataNow();
			}
		});
		cancelBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mApplication.removeActivity(RichTextActivity.this);
			}
		});
		initWebView();
	}

	private void findViews() {
		// TODO Auto-generated method stub
		webView = (WebView) findViewById(R.id.richtext_webview);
		okBtn = (Button) findViewById(R.id.richtext_ok);
		cancelBtn = (Button) findViewById(R.id.richtext_cancel);
	}
	
	@SuppressLint("NewApi")
	private void initWebView() {
		WebSettings webSetting = webView.getSettings();
		webSetting.setJavaScriptEnabled(true);
		//webSetting.setAllowFileAccessFromFileURLs(true);
		//webSetting.setAllowUniversalAccessFromFileURLs(true);
		webSetting.setLoadWithOverviewMode(true);
		webView.addJavascriptInterface(this, "androidactivity");
		webView.loadUrl(url);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home) {
			mApplication.removeActivity(this);
		}
		return super.onOptionsItemSelected(item);
	}
	
	@JavascriptInterface
	public void getHtmlData(String data) {
		html = data;
		//Toast.makeText(this, "^" + data + "$", Toast.LENGTH_SHORT).show();
		if(data == null || data.equals("") || data.matches("^\\s+$") || data.equals("<br>")) {
			new AlertDialog.Builder(this).setTitle(R.string.please_type_something).setMessage(R.string.text_empty_type_some).setNegativeButton(R.string.ok, new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).create().show();
		}else {
			//DialogUtil.showToast(ctx, html);
			write2File();
			uploadData();
		}
	}
	
	private void write2File() {
		// TODO Auto-generated method stub
		try {
			FileOutputStream out = VxploApplication.getInstance().openFileOutput("android.html", Context.MODE_PRIVATE);
			//byte[] bytes = "<head><meta charset=\"utf-8\"><title>Replace Textareas by Class Name &mdash; CKEditor Sample</title></head><body><form action=\"sample_posteddata.php\" method=\"post\"><p><textarea class=\"ckeditor\" cols=\"80\" id=\"editor1\" name=\"editor1\" rows=\"10\"></textarea></p><p><input type=\"submit\" value=\"Submit\"></p></form></body></html>".getBytes();
			byte[] bytes = html.getBytes();
			int b = bytes.length;
			out.write(bytes, 0, b);
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void uploadData() {
		// TODO Auto-generated method stub
		String filePath = mApplication.getFilesDir() + "/android.html";
		UploadTask.getInstance().addUploadTask(new FileUpload(mApplication, MediaType.TEXT).upload(filePath));
		mApplication.removeActivity(this);
	}


	private void getDataNow() {
		webView.loadUrl("javascript: getData()");
	}
}
