package com.vxplo.vxshow.util.uihider;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

public class VxShowSystemUiHider {
	/**
	 * Flags for {@link View#setSystemUiVisibility(int)} to use when showing the
	 * system UI.
	 */
	private int mShowFlags;

	/**
	 * Flags for {@link View#setSystemUiVisibility(int)} to use when hiding the
	 * system UI.
	 */
	private int mHideFlags;
	
	private boolean autoHide = false;
	private boolean visible = true;
	private Activity mActivity;
	private View anchorView;

	public VxShowSystemUiHider(Activity mActivity, View anchorView) {
		this.mActivity = mActivity;
		this.anchorView = anchorView;
		mShowFlags = View.SYSTEM_UI_FLAG_VISIBLE;
		mHideFlags = View.SYSTEM_UI_FLAG_FULLSCREEN;
		
	}
	
	public VxShowSystemUiHider(Activity mActivity, View anchorView, boolean autoHide) {
		this.mActivity = mActivity;
		this.anchorView = anchorView;
		this.autoHide = autoHide;
		mShowFlags = View.SYSTEM_UI_FLAG_VISIBLE;
		mHideFlags = View.SYSTEM_UI_FLAG_FULLSCREEN;
	}
	
	public boolean isAutoHide() {
		return autoHide;
	}

	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}
	
	public void toogle() {
		if(visible) {
			hide();
		}else {
			show();
		}
	}

	public void show() {
		anchorView.setSystemUiVisibility(mShowFlags);
		if(autoHide) {
			delayedHide(3000);
		}
		visible = true;
	}
	
	public void hide() {
		anchorView.setSystemUiVisibility(mHideFlags);
		visible = false;
	}
	
	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}

}
