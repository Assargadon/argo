package net.kiborgov.argo.android.display.cairo;

import java.util.ArrayList;
import java.util.List;

import net.kiborgov.argo.android.display.cairo.jni.CairoScriptInterpreter;
import net.kiborgov.argo.android.display.net.CairoClientListener;
import android.graphics.Bitmap;

public class CairoDisplay implements CairoClientListener {

	CairoScriptInterpreter csi;
	List<CairoDisplayListener> listeners = new ArrayList<CairoDisplayListener>();
	Bitmap bitmap;

	public CairoDisplay() {
		csi = new CairoScriptInterpreter();
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

	public void onDataComplete() {
		int count = csi.getSurfaceCount();
		// FIXME: should more than one surface be supported?
		if (count >= 1) {
			double[] size = new double[2];
			csi.getSurfaceSize(0, size);
			Bitmap bmp = Bitmap.createBitmap((int)size[0], (int)size[1], Bitmap.Config.RGB_565);
			csi.getSurface(0, bmp);
			bitmap = bmp;
			doInvalidate();
			csi.clearSurfaces();
		} else {
			// Temporary workaround for stalled interpreter
			csi = new CairoScriptInterpreter();
		}
	}
}
