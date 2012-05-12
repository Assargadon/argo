package net.kiborgov.argo.android.display.net.proto.factory;

import java.io.InputStream;
import java.io.OutputStream;

import net.kiborgov.argo.android.display.net.proto.CairoProtocol;
import net.kiborgov.argo.android.display.net.proto.HereDocProtocol;

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
