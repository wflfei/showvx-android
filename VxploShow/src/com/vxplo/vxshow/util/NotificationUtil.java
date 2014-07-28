package com.vxplo.vxshow.util;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

public class NotificationUtil {
	public static final int NOTIFY_UPLOADING = 2;
	public final static int NOTIFY_OK = 3;
	public final static int NOTIFY_NORMAL = 4;
	public static final int NOTIFY_UP_WAITING = 435;
	public static NotificationManager mNotificationManager;
	
	public static void notifyUploading() {
		Context context = VxploApplication.getInstance().getApplicationContext();
		mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, VxploApplication.resources.getString(R.string.file_upload_start), System.currentTimeMillis());
        notification.setLatestEventInfo(context, "VXPLO", VxploApplication.resources.getString(R.string.file_uploading), null);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(NOTIFY_UPLOADING, notification);  
	}
	
	public static void notifyUploadingOk() {
		Context context = VxploApplication.getInstance().getApplicationContext();
		mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, VxploApplication.resources.getString(R.string.file_upload_finished), System.currentTimeMillis());
        notification.setLatestEventInfo(context, "VXPLO", VxploApplication.resources.getString(R.string.file_upload_finished), null);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.cancel(NOTIFY_UPLOADING);
        mNotificationManager.notify(NOTIFY_OK, notification);  
	}
	
	public static void notify(String ticker, String Title, String message, int tag) {
		Context context = VxploApplication.getInstance().getApplicationContext();
		mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, ticker, System.currentTimeMillis());
        notification.setLatestEventInfo(context, Title, message, null);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(tag, notification); 
	}
	
	public static void cancleNotify(int tag) {
		Context context = VxploApplication.getInstance().getApplicationContext();
		mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(tag);
	}

}
