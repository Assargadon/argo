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

	public native Bitmap getBitmap();
}
