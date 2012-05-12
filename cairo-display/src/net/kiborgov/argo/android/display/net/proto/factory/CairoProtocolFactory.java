package net.kiborgov.argo.android.display.net.proto.factory;

import java.io.InputStream;
import java.io.OutputStream;

import net.kiborgov.argo.android.display.net.proto.CairoProtocol;

public abstract class CairoProtocolFactory {

	public abstract CairoProtocol create(InputStream in, OutputStream out);
}
