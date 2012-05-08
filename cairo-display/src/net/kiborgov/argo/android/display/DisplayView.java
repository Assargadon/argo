package net.kiborgov.argo.android.display;

import java.util.Random;

import net.kiborgov.argo.android.display.cairo.jni.CairoScriptInterpreter;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class DisplayView extends View {

	private CairoScriptInterpreter csi;
	private Bitmap bmp;

	private static String Hilbert =
			"%!CairoScript\n"

			+ "/hA { % cr dist lvl *hA* cr dist\n"
			+ "  dup not { pop } {\n"
			+ "      1 sub\n"
			+ "      3 copy hB 0 exch L pop\n"
			+ "      3 copy hA 0 L pop\n"
			+ "      3 copy hA neg 0 exch L pop\n"
			+ "      hC\n"
			+ "  } ifelse\n"
			+ "} bind def\n"

			+ "/hB { % cr dist lvl *hB* cr dist\n"
			+ "  dup not { pop } {\n"
			+ "      1 sub\n"
			+ "      3 copy hA 0 L pop\n"
			+ "      3 copy hB 0 exch L pop\n"
			+ "      3 copy hB neg 0 L pop\n"
			+ "      hD\n"
			+ "  } ifelse\n"
			+ "} bind def\n"

			+ "/hC { % cr dist lvl *hC* cr dist\n"
			+ "  dup not { pop } {\n"
			+ "      1 sub\n"
			+ "      3 copy hD neg 0 L pop\n"
			+ "      3 copy hC neg 0 exch L pop\n"
			+ "      3 copy hC 0 L pop\n"
			+ "      hA\n"
			+ "  } ifelse\n"
			+ "} bind def\n"

			+ "/hD { % cr dist lvl *hD* cr dist\n"
			+ "  dup not { pop } {\n"
			+ "      1 sub\n"
			+ "      3 copy hC neg 0 exch L pop\n"
			+ "      3 copy hD neg 0 L pop\n"
			+ "      3 copy hD 0 exch L pop\n"
			+ "      hB\n"
			+ "  } ifelse\n"
			+ "} bind def\n"

			+ "dict\n"
			+ "  /width  1024 set\n"
			+ "  /height 1024 set\n"
			+ "  surface context\n"

			+ "2 2 m 4 10 hA pop 1 g set-source stroke\n"

			//+ "/target get (out.png) write-to-png pop\n"
			+ "pop\n";

	private static String Dragon =
		      "%!CairoScript\n"
		              + "/pot { % n -- n\n"
		              + "  1 sub\n"
		              + "  dup  -1 bitshift or\n"
		              + "  dup  -2 bitshift or\n"
		              + "  dup  -4 bitshift or\n"
		              + "  dup  -8 bitshift or\n"
		              + "  dup -16 bitshift or\n"
		              + "  1 add\n"
		              + "} bind def\n"

		              + "/direction { % i -- bool\n"
		              + "  dup 2 lt { pop true } {\n"
		              + "      dup 1 add dup pot dup 3 -1 roll eq { pop pop true } {\n"
		              + "       2 div 1 sub 2 mul exch sub direction not\n"
		              + "      } ifelse\n"
		              + "  } ifelse\n"
		              + "} bind def\n"

		              + "/path { % cr dx dy -- cr\n"
		              + "  0 1 2048 {\n"
		              + "    4 1 roll 3 copy L pop 4 -1 roll direction {\n"
		              + "      exch neg\n"
		              + "    } {\n"
		              + "      neg exch\n"
		              + "    } ifelse\n"
		              + "  } for\n"
		              + "  pop pop\n"
		              + "} bind def\n"

		              + "dict\n"
		              + "  /width  512 set\n"
		              + "  /height 512 set\n"
		              + "  surface context\n"

		              + "1 g set-source paint\n"

		              + "//LINE_CAP_ROUND set-line-cap\n"
		              + "//LINE_JOIN_ROUND set-line-join\n"
		              + "4 set-line-width\n"

		              + "256 256 m  12 0 path 0 0 0 rgb set-source stroke\n"
		              + "256 256 m -12 0 path 1 0 0 rgb set-source stroke\n"
		              + "256 256 m 0  12 path 0 1 0 rgb set-source stroke\n"
		              + "256 256 m 0 -12 path 0 0 1 rgb set-source stroke\n"

		              //+ "/target get (out.png) write-to-png pop\n"
		              + "pop\n";

	private static String[] Scripts = { Hilbert, Dragon, };

	private static Random rnd = new Random();

	private void feedRandomScript() {
		csi.feed(Scripts[rnd.nextInt(Scripts.length)].getBytes());
	}

	public DisplayView(Context context) {
		super(context);
		csi = new CairoScriptInterpreter();
		feedRandomScript();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		bmp = null;
		bmp = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (null == bmp)
			return;
		int count = csi.getSurfaceCount();
		for (int i = 0; i < count; ++i) {
			csi.getSurface(i, bmp);
			canvas.drawBitmap(bmp, 0, 0, null);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (keyCode) {
			case KeyEvent.KEYCODE_MENU:
				csi.clearSurfaces();
				feedRandomScript();
				invalidate();
				return true;
			case KeyEvent.KEYCODE_BACK:
				((Activity)getContext()).finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			csi.clearSurfaces();
			feedRandomScript();
			invalidate();
			return true;
		}
		return super.onTouchEvent(event);
	}

	
}
