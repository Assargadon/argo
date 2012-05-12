package net.kiborgov.argo.android.display.cairo.jni;

public interface CairoScriptInterpreterListener {

	void onContextDestroy();

	void onCopyPage(Surface surface);

	void onShowPage(Surface surface);

}
