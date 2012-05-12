package net.kiborgov.argo.android.display.cairo.jni;

public abstract class Pointer {
	protected final long pointer;

	protected static native void initClass();
	protected abstract long init();
	protected abstract long init(long pointer);
	protected abstract void free();

	static {
		System.loadLibrary("cairo-native");
		initClass();
	}

	public Pointer() {
		this.pointer = init();
	}

	public Pointer(long pointer) {
		this.pointer = init(pointer);
	}

	protected void finalize() {
		free();
	}
}
