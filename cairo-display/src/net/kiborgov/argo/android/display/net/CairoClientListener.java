package net.kiborgov.argo.android.display.net;

public interface CairoClientListener {

	void onDataRead(byte[] buffer, int start, int length);

}
