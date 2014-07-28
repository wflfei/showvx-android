package com.vxplo.vxshow.util.camera;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

public class VxCameraSize {
	private static final String tag = "yan";
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static VxCameraSize myCamPara = null;
	private VxCameraSize(){
		
	}
	public static VxCameraSize getInstance(){
		if(myCamPara == null){
			myCamPara = new VxCameraSize();
			return myCamPara;
		}
		else{
			return myCamPara;
		}
	}
	
	public  Size getPreviewSize(List<Camera.Size> list, int th){
		Collections.sort(list, sizeComparator);
		
		int i = 0;
		for(Size s:list){
			if((s.width > th) && equalRate(s, 1.33f)){
				Log.i(tag, "◊Ó÷’…Ë÷√‘§¿¿≥ﬂ¥Á:w = " + s.width + "h = " + s.height);
                break;
			}
			i++;
		}

		return list.get(i);
	}
	public Size getPictureSize(List<Camera.Size> list, int th){
		Collections.sort(list, sizeComparator);
		
		int i = 0;
		for(Size s:list){
			if((s.width > th) && equalRate(s, 1.33f)){
				Log.i(tag, "◊Ó÷’…Ë÷√Õº∆¨≥ﬂ¥Á:w = " + s.width + "h = " + s.height);
                break;
			}
			i++;
		}

		return list.get(i);
	}
	
	public boolean equalRate(Size s, float rate){
		float r = (float)(s.width)/(float)(s.height);
		if(Math.abs(r - rate) <= 0.2)
		{
			return true;
		}
		else{
			return false;
		}
	}
	
	public  class CameraSizeComparator implements Comparator<Camera.Size>{
		//∞¥…˝–Ú≈≈¡–
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){
			return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}
		
	}
}
