package com.vxplo.vxshow.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

public class DownloadFileTask{
	private static final int DOWNLOADSTART = 1;
	private static final int DOWNLOADING = 2;
	private static final int DOWNLOADSUCCESS = 3;
	private static final int DOWNLOADFAIL = 4;
	private String downloadUrl; 
	private String savePath;
	private final DownloadListener listener;
	Handler handler;
	
	public DownloadFileTask(String downUrl, String savePath, DownloadListener listener) {
		this.downloadUrl = downUrl;
		this.savePath = savePath;
		this.listener = listener;
		handler = new DownHandler();
	}
	
	/**
	 * 调用该方法开始另起线程下载文件
	 */
	public void download() {
		new Thread(new DownloadRun()).start();
	}
	
	class DownHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case DOWNLOADSTART:
				listener.onDownloadStart(msg.arg1);
				break;
				
			case DOWNLOADING:
				listener.onDownloading(msg.arg1);
				break;
				
			case DOWNLOADSUCCESS:
				listener.onDownloadSuccess(savePath);
				break;
				
			case DOWNLOADFAIL:
				listener.onDownloadFail(downloadUrl, null);
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	public class DownloadRun implements Runnable {

		@SuppressLint("NewApi")
		@Override
		public void run() {;
			InputStream is=null;  
			int down_step = 5;// 提示step
			int totalSize;// 文件总大小
			int downloadCount = 0;// 已经下载好的大小
			int updateCount = 0;// 已经更新的文件大小
	        try {  
	            URL url=new URL(downloadUrl);  
	            HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
	            totalSize = urlConn.getContentLength();
	            if (urlConn.getResponseCode() == 404) {
	    			throw new Exception("下载失败!");
	    		}
	            if(totalSize <= 0) {
	            	throw new RuntimeException("网络文件无效");
	            }
	            int kbSize = totalSize / 1024;     //以KB为单位返回下载文件的大小
	            Message msg = handler.obtainMessage();
	            msg.what = DOWNLOADSTART;
	            msg.arg1 = kbSize;
	            msg.sendToTarget();
	            
	            is=urlConn.getInputStream();
	            File file = new File(savePath);
	        	if(file.exists()) { 
	        		file.delete();
	        	}
	            FileOutputStream os = new FileOutputStream(savePath, false);
	            byte buffer [] = new byte[4 * 1024]; 
	            int readsize;
	            while((readsize = is.read(buffer)) != -1 ){  
	                os.write(buffer, 0, readsize);
	                downloadCount += readsize;          //实际已经下载的字节数
	    			//每次增张5%
	    			if (updateCount == 0 || (downloadCount * 100 / totalSize - down_step) >= updateCount) {
	    				updateCount += down_step;
	    				Message msg1 = handler.obtainMessage();
	    	            msg1.what = DOWNLOADING;
	    	            msg1.arg1 = updateCount;
	    	            msg1.sendToTarget();
	    			}
	            } 
	            if (urlConn != null) {
	            	urlConn.disconnect();
	    		}
	            is.close();
	            os.flush();
	            os.close();
	            changeMode(savePath);
	        	Message msg1 = handler.obtainMessage();
	            msg1.what = DOWNLOADSUCCESS;
	            msg1.sendToTarget();
	        }
	        catch(Exception e) {
	        	e.printStackTrace();
	        	 Message msg1 = handler.obtainMessage();
		            msg1.what = DOWNLOADFAIL;
		            msg1.sendToTarget();
	        	File file = new File(savePath);
	        	if(file.exists()) { 
	        		file.delete();
	        	}
	        }
		}
	}
	
	public void changeMode(String fileName) {
		Process p;
		try {
			p = Runtime.getRuntime().exec("chmod 744 " + fileName);
			int status;
			status = p.waitFor();
	    	if (status == 0) {    
	    	    //chmod succeed    
	    	} else {    
	    	    //chmod failed    
	    	} 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public interface DownloadListener
	{
		/**
		 * 
		 * @param fileSize 文件大小 单位为KB
		 */
		void onDownloadStart(int fileSize);
		/**
		 *  
		 * @param nowSize 完成的百分数 100为完成
		 */
		void onDownloading(int nowSize);
		/**
		 * 
		 * @param fileName 文件名称
		 * @param description 错误描述
		 */
		void onDownloadFail(String fileName, String description);
		/**
		 * 
		 * @param fileName 文件完整路径名称
		 */
		void onDownloadSuccess(String fileName);
	}
}
