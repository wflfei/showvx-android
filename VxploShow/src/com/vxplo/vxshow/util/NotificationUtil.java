package com.vxplo.vxshow.util;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.widget.RemoteViews;

public class NotificationUtil {
	public static final int NOTIFY_UPLOADING = 2;
	public final static int NOTIFY_OK = 3;
	public final static int NOTIFY_NORMAL = 4;
	public final static int NOTIFY_PROGRESS = 5;
	public static final int NOTIFY_UP_WAITING = 435;
	private static Context context = VxploApplication.getInstance();
	public static NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	private static Notification mNotification;
	
	@SuppressWarnings("deprecation")
	public static void notifyUploading() {
		Context context = VxploApplication.getInstance().getApplicationContext();
		//mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.ic_launcher, VxploApplication.resources.getString(R.string.file_upload_start), System.currentTimeMillis());
        notification.setLatestEventInfo(context, "VXPLO", VxploApplication.resources.getString(R.string.file_uploading), null);
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        mNotificationManager.notify(NOTIFY_UPLOADING, notification);  
	}
	
	@SuppressWarnings("deprecation")
	public static void notifyUploadingPro() {
		mNotification = new Notification(R.drawable.ic_launcher, VxploApplication.resources.getString(R.string.file_uploading), System.currentTimeMillis());
		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.upload_notification_layout);
		contentView.setTextViewText(R.id.update_notify_progresstext, "0%");
		contentView.setProgressBar(R.id.update_notify_progressbar, 100, 0, false);
		mNotification.contentView = contentView;
		mNotificationManager.notify(NOTIFY_PROGRESS, mNotification);
	}
	
	@SuppressWarnings("deprecation")
	public static void notifyUploadingPro(int total) {
		mNotification = new Notification(R.drawable.ic_launcher, VxploApplication.resources.getString(R.string.file_uploading), System.currentTimeMillis());
		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.upload_notification_layout);
		contentView.setTextViewText(R.id.update_notify_progresstext, "0/" + total);
		contentView.setProgressBar(R.id.update_notify_progressbar, total, 0, false);
		mNotification.contentView = contentView;
		mNotificationManager.notify(NOTIFY_PROGRESS, mNotification);
	}
	
	public static void setProgress(int percent) {
		mNotification.contentView.setTextViewText(R.id.update_notify_progresstext, percent + "%");
		mNotification.contentView.setProgressBar(R.id.update_notify_progressbar, 100, percent, false);
		mNotificationManager.notify(NOTIFY_PROGRESS, mNotification);
	}
	
	public static void setProgress(int complete, int total) {
		mNotification.contentView.setTextViewText(R.id.update_notify_progresstext, complete + "/" + total);
		mNotification.contentView.setProgressBar(R.id.update_notify_progressbar, total, complete, false);
		mNotificationManager.notify(NOTIFY_PROGRESS, mNotification);
	}
	
	@SuppressWarnings("deprecation")
	public static void notifyUploadingOk() {
		Context context = VxploApplication.getInstance().getApplicationContext();
		//mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.ic_launcher, VxploApplication.resources.getString(R.string.file_upload_finished), System.currentTimeMillis());
        notification.setLatestEventInfo(context, "VXPLO", VxploApplication.resources.getString(R.string.file_upload_finished), null);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.cancel(NOTIFY_UPLOADING);
        mNotificationManager.cancel(NOTIFY_PROGRESS);
        mNotificationManager.notify(NOTIFY_OK, notification);  
	}
	
	@SuppressWarnings("deprecation")
	public static void notifyUploadingFailed() {
		Context context = VxploApplication.getInstance().getApplicationContext();
		//mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.ic_launcher, VxploApplication.resources.getString(R.string.file_upload_failed), System.currentTimeMillis());
        notification.setLatestEventInfo(context, "VXPLO", VxploApplication.resources.getString(R.string.file_upload_failed), null);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.cancel(NOTIFY_UPLOADING);
        mNotificationManager.cancel(NOTIFY_PROGRESS);
        mNotificationManager.notify(NOTIFY_OK, notification);  
	}
	
	@SuppressWarnings("deprecation")
	public static void notify(String ticker, String Title, String message, int tag) {
		Context context = VxploApplication.getInstance().getApplicationContext();
		//mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		@SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.ic_launcher, ticker, System.currentTimeMillis());
        notification.setLatestEventInfo(context, Title, message, null);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotificationManager.notify(tag, notification); 
	}
	
	public static void notify(int ticker, int Title, String message, int tag) {
		notify(VxploApplication.resources.getString(ticker), VxploApplication.resources.getString(Title), message, tag);
	}
	
	public static void cancleNotify(int tag) {
		Context context = VxploApplication.getInstance().getApplicationContext();
		//mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(tag);
	}
	
	

}
