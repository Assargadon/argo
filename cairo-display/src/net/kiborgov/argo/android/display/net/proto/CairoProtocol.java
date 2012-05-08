package net.kiborgov.argo.android.display.net.proto;

import java.io.IOException;

public abstract class CairoProtocol {

	public abstract byte[] read() throws IOException;
	
	public abstract void ack() throws IOException;

	public abstract void close();
}
