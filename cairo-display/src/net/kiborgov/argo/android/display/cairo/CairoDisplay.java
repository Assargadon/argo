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
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class CairoDisplay implements CairoClientListener, CairoScriptInterpreterListener {

	private static final String CONNECTION_LOST = " CONNECTION LOST ";
	private static final String NO_SIGNAL = " NO SIGNAL ";
	private static final String SCREEN_RESET = " SCREEN RESET ";

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

	public void setSize(int width, int height) {
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		bitmap.eraseColor(Color.BLUE);
		drawMessageBox(client.isConnected() ? SCREEN_RESET : NO_SIGNAL);
		onInvalidate();
	}

	public void stop() {
		client.close();
	}

	public void onConnect() {
		csi = new CairoScriptInterpreter(this);
		onInvalidate();
	}

	public void onDisconnect(boolean error) {
		csi = null;
		drawMessageBox(error ? CONNECTION_LOST : NO_SIGNAL);
		onInvalidate();
	}

	private void drawMessageBox(String text) {
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		Rect rect = new Rect();

		paint.setTextSize(20);
		paint.getTextBounds(text, 0, text.length(), rect);
		int dx = -rect.left, dy = -rect.top;
		rect.offsetTo(0, 0);
		rect.offset((bitmap.getWidth() - rect.right) / 2, (bitmap.getHeight() - rect.bottom) / 2);

		Rect box = new Rect(rect);
		box.inset(-10, -10);
		paint.setColor(0x90000000);
		canvas.drawRect(box, paint);
		paint.setColor(Color.WHITE);
		canvas.drawText(text, rect.left + dx, rect.top + dy, paint);
	}

	public void onDataRead(byte[] buffer, int start, int length) {
		csi.feed(buffer, start, length);
	}

	public void onContextDestroy() {
		// TODO Auto-generated method stub
	}

	public void onCopyPage(Surface surface) {
		surface.drawTo(bitmap, 0, 0, false);
		onInvalidate();
	}

	public void onShowPage(Surface surface) {
		// TODO Auto-generated method stub
	}
}
