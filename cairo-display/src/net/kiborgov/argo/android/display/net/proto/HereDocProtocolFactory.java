package net.kiborgov.argo.android.display.net.proto;

import java.io.InputStream;
import java.io.OutputStream;

public class HereDocProtocolFactory extends CairoProtocolFactory {
	String documentEnd;

	public HereDocProtocolFactory(String documentEnd) {
		this.documentEnd = documentEnd;
	}

	@Override
	public CairoProtocol create(InputStream in, OutputStream out) {
		return new HereDocProtocol(in, out, documentEnd);
	}

}
