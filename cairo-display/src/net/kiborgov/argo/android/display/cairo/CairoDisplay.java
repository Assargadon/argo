package net.kiborgov.argo.android.display.cairo;

import java.util.ArrayList;
import java.util.List;

import net.kiborgov.argo.android.display.cairo.jni.CairoScriptInterpreter;
import net.kiborgov.argo.android.display.cairo.jni.CairoScriptInterpreterListener;
import net.kiborgov.argo.android.display.cairo.jni.Surface;
import net.kiborgov.argo.android.display.net.CairoClientListener;
import android.graphics.Bitmap;

public class CairoDisplay implements CairoClientListener, CairoScriptInterpreterListener {

	CairoScriptInterpreter csi;
	List<CairoDisplayListener> listeners = new ArrayList<CairoDisplayListener>();
	Bitmap bitmap;

	public CairoDisplay() {
		csi = new CairoScriptInterpreter(this);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	private void doInvalidate() {
		for (CairoDisplayListener listener : listeners)
			listener.onInvalidate();
	}

	public void addListener(CairoDisplayListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CairoDisplayListener listener) {
		listeners.remove(listener);
	}

	public void onDataRead(byte[] buffer, int start, int length) {
		csi.feed(buffer, start, length);
	}

	public void onContextDestroy() {
		// TODO Auto-generated method stub
	}

	public void onCopyPage(Surface surface) {
		bitmap = surface.getBitmap();
		doInvalidate();
	}

	public void onShowPage(Surface surface) {
		// TODO Auto-generated method stub
	}
}
