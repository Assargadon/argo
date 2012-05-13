package net.kiborgov.argo.android.display;

import net.kiborgov.argo.android.display.cairo.CairoDisplay;
import net.kiborgov.argo.android.display.cairo.CairoDisplayListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.View;

public class DisplayView extends View implements CairoDisplayListener {

	CairoDisplay display;

	public DisplayView(Context context) {
		super(context);

		setKeepScreenOn(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();

		display = new CairoDisplay();
		display.addListener(this);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Bitmap bitmap = display.getBitmap();
		if (null == bitmap)
			return;
		canvas.drawBitmap(bitmap, 0, 0, null);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				display.stop();
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
