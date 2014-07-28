package com.vxplo.vxshow.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AvatarView extends ImageView {
	
	private Bitmap mImage;
	private Bitmap mMask;  // png mask with transparency
	private int mPosX = 0;
	private int mPosY = 0;

	public AvatarView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public AvatarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public AvatarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setmImage(Bitmap mImage) {
		this.mImage = mImage;
	}

	public void setmMask(Bitmap mMask) {
		this.mMask = mMask;
	}

	@Override
	public void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    System.out.println("in");
	    canvas.save();
	    Paint maskPaint = new Paint();
	    maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

	    Paint imagePaint = new Paint();
	    imagePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));

	    canvas.drawBitmap(mMask, 0, 0, maskPaint);
	    canvas.drawBitmap(mImage, mPosX, mPosY, imagePaint);
	    canvas.restore();
	}
}
