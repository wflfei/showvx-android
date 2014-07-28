package com.vxplo.vxshow.util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

public class MediaUtil {

	/** 
     * ��ȡ��Ƶ������ͼ 
     * ��ͨ��ThumbnailUtils������һ����Ƶ������ͼ��Ȼ��������ThumbnailUtils������ָ����С������ͼ�� 
     * �����Ҫ������ͼ�Ŀ�͸߶�С��MICRO_KIND��������Ҫʹ��MICRO_KIND��Ϊkind��ֵ���������ʡ�ڴ档 
     * @param videoPath ��Ƶ��·�� 
     * @param width ָ�������Ƶ����ͼ�Ŀ�� 
     * @param height ָ�������Ƶ����ͼ�ĸ߶ȶ� 
     * @param kind ����MediaStore.Images.Thumbnails���еĳ���MINI_KIND��MICRO_KIND�� 
     *            ���У�MINI_KIND: 512 x 384��MICRO_KIND: 96 x 96 
     * @return ָ����С����Ƶ����ͼ 
     */  
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height,  
            int kind) {  
        Bitmap bitmap = null;  
        // ��ȡ��Ƶ������ͼ  
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
        System.out.println("w"+bitmap.getWidth());  
        System.out.println("h"+bitmap.getHeight());  
        //bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,  
        //        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
        return bitmap;  
    } 

}
