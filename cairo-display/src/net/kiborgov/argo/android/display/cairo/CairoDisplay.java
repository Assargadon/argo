package net.kiborgov.argo.android.display.cairo;

import java.util.ArrayList;
import java.util.List;

import net.kiborgov.argo.android.display.cairo.jni.CairoScriptInterpreter;
import net.kiborgov.argo.android.display.cairo.jni.CairoScriptInterpreterListener;
import net.kiborgov.argo.android.display.cairo.jni.Surface;
import net.kiborgov.argo.android.display.net.CairoClient;
import net.kiborgov.argo.android.display.net.CairoClientListener;
import net.kiborgov.argo.android.display.net.proto.factory.GenericTextProtocolFactory;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class CairoDisplay implements CairoClientListener, CairoScriptInterpreterListener {

	private CairoClient client;
	CairoScriptInterpreter csi;
	List<CairoDisplayListener> listeners = new ArrayList<CairoDisplayListener>();
	Bitmap bitmap;

	public CairoDisplay() {
		client = new CairoClient(new GenericTextProtocolFactory());
		client.addListener(this);
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	private void onInvalidate() {
		for (CairoDisplayListener listener : listeners)
			listener.onInvalidate();
	}

	public void addListener(CairoDisplayListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CairoDisplayListener listener) {
		listeners.remove(listener);
	}

	public void stop() {
		if (null != client)
			client.close();
	}

	public void onConnect() {
		csi = new CairoScriptInterpreter(this);
		onInvalidate();
	}

	public void onDisconnect(boolean error) {
		if (!error)
		{
			client = null;
			bitmap.eraseColor(Color.BLACK);
		} else {
			Canvas canvas = new Canvas(bitmap);
			Paint paint = new Paint();
			String text = " CONNECTION LOST ";
			Rect rect = new Rect();

			paint.getTextBounds(text, 0, text.length(), rect);
			rect.offset((bitmap.getWidth() - rect.right) / 2, (bitmap.getHeight() - rect.bottom) / 2);

			paint.setColor(Color.BLACK);
			canvas.drawRect(rect, paint);
			paint.setColor(Color.WHITE);
			canvas.drawText(text, rect.left, rect.top, paint);
		}
		onInvalidate();
	}

	public void onDataRead(byte[] buffer, int start, int length) {
		csi.feed(buffer, start, length);
	}

	public void onContextDestroy() {
		// TODO Auto-generated method stub
	}

	public void onCopyPage(Surface surface) {
		bitmap = surface.getBitmap();
		onInvalidate();
	}

	public void onShowPage(Surface surface) {
		// TODO Auto-generated method stub
	}
}
