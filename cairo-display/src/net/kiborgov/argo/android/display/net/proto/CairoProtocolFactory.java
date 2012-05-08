package net.kiborgov.argo.android.display.net.proto;

import java.io.InputStream;
import java.io.OutputStream;

public abstract class CairoProtocolFactory {

	public abstract CairoProtocol create(InputStream in, OutputStream out);
}
