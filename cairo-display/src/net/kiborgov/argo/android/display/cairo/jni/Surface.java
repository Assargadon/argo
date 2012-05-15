package net.kiborgov.argo.android.display.cairo.jni;

import android.graphics.Bitmap;

public class Surface extends Pointer {

	static {
		initClass();
	}

	Surface(long pointer) {
		super(pointer);
	}

	protected static native void initClass();

	@Override
	protected long init() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected native long init(long pointer);

	@Override
	protected native void free();

	/**
	 * Create a new bitmap using the surface's data
	 * 
	 * @return The newly created bitmap
	 */
	public native Bitmap getBitmap();

	/**
	 * Copy the surface's data to the provided bitmap
	 * 
	 * @param bitmap	The target bitmap
	 * @param x			X coordinate in the target bitmap
	 * @param y			Y coordinate in the target bitmap
	 * @param scale		Whether to scale the source data or not
	 */
	public native void drawTo(Bitmap bitmap, int x, int y, boolean scale);
}
