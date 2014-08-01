package com.vxplo.vxshow.util.fileupload;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

import com.vxplo.vxshow.R;
import com.vxplo.vxshow.activity.RecordActivity;
import com.vxplo.vxshow.app.VxploApplication;
import com.vxplo.vxshow.util.DialogUtil;
import com.vxplo.vxshow.util.NetworkUtil;
import com.vxplo.vxshow.util.NotificationUtil;

public class UploadTask {
	private static UploadTask uploadTask;
	private List<FileUpload> uploadsTask;
	private int networkState;

	private UploadTask() {
		uploadsTask = new ArrayList<FileUpload>();
		
	}
	
	public static UploadTask getInstance() {
		if(uploadTask == null) {
			uploadTask = new UploadTask();
		}
		return uploadTask;
	}
	
	public void addUploadTask(final FileUpload task) {
		if(null != uploadsTask) {
			this.uploadsTask.add(task);
			if(uploadsTask.size() >= 2) {  //多个上传任务等待中
				NotificationUtil.notify(R.string.waiting_for_upload, R.string.waiting_for_upload, VxploApplication.resources.getString(R.string.waiting_upload_last) + (uploadsTask.size() - 1), NotificationUtil.NOTIFY_UP_WAITING);
			}else {    //当前是第一个上传任务
				boolean ignored = VxploApplication.getInstance().getPref("mobile_ignore").getBoolean("ignored", false);
				if(NetworkUtil.getNetworkState(VxploApplication.getInstance()) == NetworkUtil.NETWORN_WIFI) {
					task.execute();
				} else if(NetworkUtil.getNetworkState(VxploApplication.getInstance()) == NetworkUtil.NETWORN_MOBILE) {
					if(ignored) {
						task.execute();
					} else {
						Activity activity;
						if(VxploApplication.getInstance().getTopActivity() instanceof RecordActivity) {
							activity = VxploApplication.getInstance().getTopActivity();
						} else {
							activity = VxploApplication.getInstance().getMainActivity();
						}
						Builder builder = new AlertDialog.Builder(activity);
						//builder.setIcon(R.drawable.ic_launcher);
						builder.setTitle(R.string.mobile_network_tips);
						//builder.setMessage(R.string.mobile_network_tips);
						builder.setSingleChoiceItems(R.array.mobile_network_choice, 0, new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.cancel();
								if(which == 0) {
									removeAllUploadTask();
								} else if(which == 1) {
									task.execute();
								} else if(which == 2) {
									VxploApplication.getInstance().getPref("mobile_ignore").edit().putBoolean("ignored", true).commit();
									task.execute();
								}
							}
						});
						AlertDialog alert = builder.create();
						alert.show();
					}
					
				} else {
					DialogUtil.showAlert(VxploApplication.getInstance().getTopActivity(), R.string.no_network, R.string.tips);
					removeAllUploadTask();
				}
				
			}
		}
		
	}
	
	public void removeUploadTask(FileUpload task) {
		if(null != uploadsTask && uploadsTask.contains(task)) {
			this.uploadsTask.remove(task);
			if(uploadsTask.size() > 0) {
				if(uploadsTask.size() == 1) {
					NotificationUtil.cancleNotify(NotificationUtil.NOTIFY_UP_WAITING);
				}
				uploadsTask.get(0).execute();
			} 
		}
	}
	
	public List<FileUpload> getUploadTasks() {
		return uploadsTask;
	}
	
	public void removeAllUploadTask() {
		uploadsTask.clear();
		NotificationUtil.cancleNotify(NotificationUtil.NOTIFY_UP_WAITING);
		NotificationUtil.cancleNotify(NotificationUtil.NOTIFY_UPLOADING);
		NotificationUtil.cancleNotify(NotificationUtil.NOTIFY_OK);
	}

}
