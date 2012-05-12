package net.kiborgov.argo.android.display.cairo.jni;

import android.graphics.Bitmap;

public class CairoScriptInterpreter extends Pointer {

	static {
		initClass();
	}

	public CairoScriptInterpreter(CairoScriptInterpreterListener listener)
	{
		setup(new Listener(listener));
	}

	protected class Listener {

		private final CairoScriptInterpreterListener listener;

		public Listener(CairoScriptInterpreterListener listener) {
			this.listener = listener;
		}

		public void onContextDestroy() {
			listener.onContextDestroy();
		}

		public void onCopyPage(long surface) {
			listener.onCopyPage(new Surface(surface));
		}

		public void onShowPage(long surface) {
			listener.onShowPage(new Surface(surface));
		}

	}

	protected static native void initClass();

	@Override
	protected native long init();

	@Override
	protected long init(long pointer)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	protected native void free();

	private native void setup(Listener listener);

	public native int getLineNumber();

	public native void feed(byte[] data);

	public native void feed(byte[] data, int start, int length);

	public native void feed(String line);

	public native int getSurfaceCount();

	public native void getSurfaceSize(int index, double[] size);

	public native void getSurface(int index, Bitmap bitmap);

	public native void clearSurfaces();
}
