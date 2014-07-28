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
	 * ���ø÷�����ʼ�����߳������ļ�
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
			int down_step = 5;// ��ʾstep
			int totalSize;// �ļ��ܴ�С
			int downloadCount = 0;// �Ѿ����غõĴ�С
			int updateCount = 0;// �Ѿ����µ��ļ���С
	        try {  
	            URL url=new URL(downloadUrl);  
	            HttpURLConnection urlConn=(HttpURLConnection)url.openConnection();
	            totalSize = urlConn.getContentLength();
	            if (urlConn.getResponseCode() == 404) {
	    			throw new Exception("����ʧ��!");
	    		}
	            if(totalSize <= 0) {
	            	throw new RuntimeException("�����ļ���Ч");
	            }
	            int kbSize = totalSize / 1024;     //��KBΪ��λ���������ļ��Ĵ�С
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
	                downloadCount += readsize;          //ʵ���Ѿ����ص��ֽ���
	    			//ÿ������5%
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
		 * @param fileSize �ļ���С ��λΪKB
		 */
		void onDownloadStart(int fileSize);
		/**
		 *  
		 * @param nowSize ��ɵİٷ��� 100Ϊ���
		 */
		void onDownloading(int nowSize);
		/**
		 * 
		 * @param fileName �ļ�����
		 * @param description ��������
		 */
		void onDownloadFail(String fileName, String description);
		/**
		 * 
		 * @param fileName �ļ�����·������
		 */
		void onDownloadSuccess(String fileName);
	}
}
