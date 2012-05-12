package net.kiborgov.argo.android.display.net.proto.factory;

import java.io.InputStream;
import java.io.OutputStream;

import net.kiborgov.argo.android.display.net.proto.CairoProtocol;
import net.kiborgov.argo.android.display.net.proto.GenericTextProtocol;

public class GenericTextProtocolFactory extends CairoProtocolFactory {

	@Override
	public CairoProtocol create(InputStream in, OutputStream out) {
		return new GenericTextProtocol(in, out, null, false);
	}

}
