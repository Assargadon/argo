package net.kiborgov.argo.android.display.net;

public interface CairoClientListener {

	void onConnect();

	void onDisconnect(boolean error);

	void onDataRead(byte[] buffer, int start, int length);

}
