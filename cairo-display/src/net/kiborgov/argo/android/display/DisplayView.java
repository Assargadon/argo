package net.kiborgov.argo.android.display;

import net.kiborgov.argo.android.display.cairo.CairoDisplay;
import net.kiborgov.argo.android.display.cairo.CairoDisplayListener;
import net.kiborgov.argo.android.display.net.CairoClient;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;

public class DisplayView extends View implements CairoDisplayListener {

	CairoDisplay display;

	public DisplayView(Context context, CairoClient client) {
		super(context);

		setKeepScreenOn(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

		display = new CairoDisplay(client);
		display.addListener(this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		display.setSize(w, h);
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Bitmap bitmap = display.getBitmap();
		if (null == bitmap) {
			canvas.drawColor(Color.BLACK);
		} else {
			canvas.drawBitmap(bitmap, 0, 0, null);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				((Activity)getContext()).finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void onInvalidate() {
		postInvalidate();
	}
	
}
