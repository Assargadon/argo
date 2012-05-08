package net.kiborgov.argo.android.display.cairo.jni;

public abstract class Pointer {
	protected final long pointer;

	protected static native void initClass();
	protected abstract long init();
	protected abstract void free();

	static {
		System.loadLibrary("cairo-native");
		initClass();
	}

	public Pointer() {
		pointer = init();
	}

	protected void finalize() {
		free();
	}
}
