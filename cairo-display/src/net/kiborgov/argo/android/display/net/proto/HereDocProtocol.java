package net.kiborgov.argo.android.display.net.proto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HereDocProtocol extends GenericTextProtocol {

	protected String documentEnd;

	public HereDocProtocol(InputStream in, OutputStream out, String documentEnd) {
		super(in, out, "OK", false);
		this.documentEnd = documentEnd;
	}

	@Override
	protected String readLine() throws IOException {
		String line = super.readLine();
		if (documentEnd.equals(line))
			return null;
		return line;
	}

}
