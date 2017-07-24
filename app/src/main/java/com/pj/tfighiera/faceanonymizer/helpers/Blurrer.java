package com.pj.tfighiera.faceanonymizer.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;

/**
 * created on 21/07/2017
 *
 * @author tfi
 */
public class Blurrer
{
	private static final int BLUR_RADIUS = 25;
	private final int mWidth;
	private final int mHeight;
	@NonNull
	private final Point mPosition;
	@NonNull
	private final Bitmap mBitMap;

	private Blurrer(@NonNull BlurrerBuilder builder)
	{
		mBitMap = builder.mBitmap;
		mWidth = builder.mWidth;
		mHeight = builder.mHeight;
		mPosition = builder.mPosition;
	}

	@NonNull
	public Bitmap createBlurMask(Context context)
	{
		Bitmap outputBitmap = Bitmap.createBitmap(mBitMap, mPosition.x, mPosition.y, mWidth, mHeight);
		final RenderScript renderScript = RenderScript.create(context);
		Allocation tmpIn = Allocation.createFromBitmap(renderScript, outputBitmap);
		Allocation tmpOut = Allocation.createFromBitmap(renderScript, outputBitmap);

		//Intrinsic Gausian createBlurMask filter
		ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
		intrinsicBlur.setRadius(BLUR_RADIUS);
		intrinsicBlur.setInput(tmpIn);
		intrinsicBlur.forEach(tmpOut);
		tmpOut.copyTo(outputBitmap);
		return outputBitmap;
	}

	public static class BlurrerBuilder
	{
		@NonNull
		private final Bitmap mBitmap;
		private Point mPosition;
		private int mWidth;
		private int mHeight;

		public BlurrerBuilder(@NonNull Bitmap bitmap)
		{
			mBitmap = bitmap;
			mPosition = new Point();
		}

		@NonNull
		public BlurrerBuilder setPosition(@NonNull PointF position)
		{
			mPosition.set((int) position.x, (int) position.y);
			return this;
		}

		@NonNull
		public BlurrerBuilder setWidth(float width)
		{
			mWidth = (int) width;
			return this;
		}

		@NonNull
		public BlurrerBuilder setHeight(float height)
		{
			mHeight = (int) height;
			return this;
		}

		@NonNull
		public Blurrer build()
		{
			mPosition.x = normalizePosition(mPosition.x);
			mPosition.y = normalizePosition(mPosition.y);
			mWidth = normalizeSize(mWidth, mPosition.x, mBitmap.getWidth());
			mHeight = normalizeSize(mHeight, mPosition.y, mBitmap.getHeight());
			return new Blurrer(this);
		}

		private int normalizePosition(int position)
		{
			return position < 0
			       ? 0
			       : position;
		}

		private int normalizeSize(int size, int position, int maxPosition)
		{
			return size + position > maxPosition
			       ? maxPosition - position
			       : size;
		}
	}
}
