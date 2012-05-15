package net.kiborgov.argo.android.display;

import net.kiborgov.argo.android.display.net.CairoClient;
import net.kiborgov.argo.android.display.net.proto.factory.GenericTextProtocolFactory;
import android.app.Activity;
import android.os.Bundle;

public class DisplayActivity extends Activity {

	private CairoClient cairoClient;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		cairoClient = new CairoClient(new GenericTextProtocolFactory());
		setContentView(new DisplayView(this, cairoClient));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		cairoClient.close();
	}
}
