package com.vxplo.vxshow.util;

import java.io.ByteArrayOutputStream;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.vxplo.vxshow.R;
import com.vxplo.vxshow.configure.Config;
import com.vxplo.vxshow.entity.Idea;

public class WXUtil {
	private IWXAPI api;
	private Activity mActivity = null;
	private final int THUMB_SIZE = 100;
	private String filePath = null;
	private Bitmap mBitmap = null;

	public WXUtil(Activity activity) {
		mActivity = activity;
		api = WXAPIFactory.createWXAPI(mActivity, Config.APP_ID, true);
	}
	
	public WXUtil(Activity activity, IWXAPI api) {
		mActivity = activity;
		this.api = api;
	}
	
	private boolean checkWeChatInstalled() {
		if(!api.isWXAppInstalled()) {
			DialogUtil.showAlert(mActivity, "您当前没有安装微信", "温馨提示").show();
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param idea
	 * @param shareToTimeLine 是否是分享到朋友圈
	 */
	public void shareWebPage(final Idea idea, final boolean shareToTimeLine) {
		if(!checkWeChatInstalled()) {
			return;
		}
		new Thread() {
			@Override
			public void run() {
				try {
					WXWebpageObject webpage = new WXWebpageObject();
					webpage.webpageUrl = idea.getPlayUrl();
					WXMediaMessage msg = new WXMediaMessage(webpage);
					msg.title = idea.getTitle();
					msg.description = idea.getTitle();
					
					Bitmap bmp = BitmapFactory.decodeStream(new URL(idea.getImageUrl()).openStream());
					Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE,
							THUMB_SIZE, true);
					bmp.recycle();
					msg.thumbData = bmpToByteArray(thumbBmp, true);
					
					SendMessageToWX.Req req = new SendMessageToWX.Req();
					req.transaction = buildTransaction("webpage");
					req.message = msg;
					req.scene = shareToTimeLine ? SendMessageToWX.Req.WXSceneTimeline : SendMessageToWX.Req.WXSceneSession;
					api.sendReq(req);
				} catch(Exception e) {
					e.printStackTrace();
				}
				super.run();
			}
		}.start();
	}
	
	private String buildTransaction(final String type) {

		return (type == null) ? String.valueOf(System.currentTimeMillis())
				: type + System.currentTimeMillis();

	}

	private byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}

		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
