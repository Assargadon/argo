package net.kiborgov.argo.android.display.cairo.jni;

import android.graphics.Bitmap;

public class CairoScriptInterpreter extends Pointer {
	@Override
	protected native void free();

	@Override
	protected native long init();

	public native int getLineNumber();

	public native void feed(byte[] data);

	public native void feed(byte[] data, int start, int length);

	public native void feed(String line);

	public native int getSurfaceCount();

	public native void getSurfaceSize(int index, double[] size);

	public native void getSurface(int index, Bitmap bitmap);

	public native void clearSurfaces();
}
