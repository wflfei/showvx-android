package com.vxplo.vxshow.util;

import com.vxplo.vxshow.R;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class PopMenuUtil {
	public static PopupWindow popupWindow;
	
	public static void popBottomIphoneDialog(Context context, OnClickListener listener) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout popupWindowView = (RelativeLayout) inflater.inflate(R.layout.bottom_pop_layout, null);
		popupWindow = new PopupWindow(popupWindowView,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT,true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//设置P opupWindow的弹出和消失效果
		popupWindow.setAnimationStyle(R.style.popupAnimation);
		popupWindowView.setOnClickListener(new CancelClickListener());
		Button takeVideoButton = (Button) popupWindowView.findViewById(R.id.take_video_button);
		takeVideoButton.setOnClickListener(listener);
		popupWindowView.findViewById(R.id.take_picture_button).setOnClickListener(listener);
		popupWindowView.findViewById(R.id.sel_video_button).setOnClickListener(listener);
		popupWindowView.findViewById(R.id.sel_picture_button).setOnClickListener(listener);
		Button cancleButton = (Button) popupWindowView.findViewById(R.id.cancleButton);
		cancleButton.setOnClickListener(new CancelClickListener());
		popupWindow.showAtLocation(takeVideoButton, Gravity.CENTER, 0, 0);
	} 
	
	static class CancelClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v) {
			popupWindow.dismiss();
		}
		
	}

}
