package com.vxplo.vxshow.service;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.entity.Version;
import com.vxplo.vxshow.update.CheckUpdateCallback;
import com.vxplo.vxshow.update.DownloadFileTask;
import com.vxplo.vxshow.update.VersionCheckAsyncTask;
import com.vxplo.vxshow.util.FileUtil;
import com.vxplo.vxshow.util.NetworkUtil;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class UpdateService extends Service implements CheckUpdateCallback {
	String checkUrl = "http://lin-pc:8080/AppUpdate/servlet/GetAppVersionInfo";
	
	private Context context;
	private Version newVersion;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		context = getApplicationContext();
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(NetworkUtil.getNetworkState(context) == NetworkUtil.NETWORN_WIFI) {
			VersionCheckAsyncTask task = new VersionCheckAsyncTask(context, this);
			task.execute(checkUrl);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onFoundLatestVersion(Version version) {
		this.newVersion = version;
		showWhetherDialog();
	}

	@Override
	public void onCurrentIsLatest() {
		stopSelf();
		
	}
	
	
	
	@Override
	public void onDestroy() {
		Log.d("UpdateService", "UpdateService Destroyed!");
		super.onDestroy();
	}

	public void showWhetherDialog()
	{
		new AlertDialog.Builder(VxploApplication.getInstance().getTopActivity()).setTitle(String.format(context.getResources().getString(R.string.latest_version_title), newVersion.getName()))
		.setMessage(String.format(context.getResources().getString(R.string.update_content), newVersion.getMessage()))
		.setPositiveButton(R.string.update_now, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(NetworkUtil.getNetworkState(context) == NetworkUtil.NETWORN_WIFI) {
					String savePath = FileUtil.getUpdateFile(context).toString();
					DownloadFileTask task = new DownloadFileTask(newVersion.getUrl(), savePath, new AppDownloadListener());
					task.download();
				}else {
					Toast.makeText(context, "请使用WiFi下载", Toast.LENGTH_SHORT).show();
				}
				
			}
		}).setNegativeButton(R.string.do_it_later, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				stopSelf();
			}
		}).show();
	}
	
	class AppDownloadListener implements DownloadFileTask.DownloadListener {
		private NotificationManager mNotificationManager;
		private Notification mNotification;
		private PendingIntent mPendingIntent;
		
		public AppDownloadListener() {
			mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		}

		@Override
		public void onDownloadStart(int fileSize) {
			mNotification = new Notification(R.drawable.ic_launcher, "开始下载安装包", System.currentTimeMillis());
			RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.update_download_notification_layout);
			contentView.setTextViewText(R.id.update_notify_progresstext, "0%");
			contentView.setProgressBar(R.id.update_notify_progressbar, 100, 0, false);
			mNotification.contentView = contentView;
			mNotificationManager.notify(0, mNotification);
		}

		@Override
		public void onDownloading(int nowSize) {
			mNotification.contentView.setTextViewText(R.id.update_notify_progresstext, nowSize + "%");
			mNotification.contentView.setProgressBar(R.id.update_notify_progressbar, 100, nowSize, false);
			mNotificationManager.notify(0, mNotification);
		}

		@Override
		public void onDownloadFail(String fileName, String description) {
			
			
		}

		@Override
		public void onDownloadSuccess(String fileName) {
			mNotificationManager.cancel(0);
			Uri uri = Uri.fromFile(FileUtil.getUpdateFile(context));  
            Intent intent = new Intent(Intent.ACTION_VIEW);  
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(uri, "application/vnd.android.package-archive"); 
            startActivity(intent);
            /*
            mPendingIntent = PendingIntent.getActivity(  
                    context, 0, intent, 0);  
            Notification notification = new Notification(R.drawable.ic_launcher, "下载完成，点击安装", System.currentTimeMillis());
            notification.setLatestEventInfo(context, "VXPLO", "下载更新成功，点击安装", mPendingIntent);
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            /*
            Notification.Builder builder = new Notification.Builder(context);
            builder.setContentTitle("VXPLO");
            builder.setContentText("下载更新成功，点击安装");
            builder.setContentIntent(mPendingIntent);
            */
            //mNotificationManager.notify(1, notification);  
            stopSelf();
		}
	}

}
