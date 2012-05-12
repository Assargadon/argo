package net.kiborgov.argo.android.display.net.proto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class GenericTextProtocol extends CairoProtocol {

	protected BufferedReader in;
	protected PrintWriter out;
	protected String ackString;
	protected boolean emptyLines;

	public GenericTextProtocol(InputStream in, OutputStream out, String ackString, boolean emptyLines) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new PrintWriter(new OutputStreamWriter(out)); // TODO: probably not needed
		this.ackString = ackString;
		this.emptyLines = emptyLines;
	}

	protected String readLine() throws IOException {
		String line;
		do {
			line = in.readLine();
			if (null == line)
				throw new IOException("No data could be read");
		} while (!emptyLines && "".equals(line));
		return line;
	}

	@Override
	public byte[] read() throws IOException {
		String line = readLine();
		return line.getBytes();
	}

	@Override
	public void ack() {
		if (null != ackString) {
			out.println(ackString);
			out.flush();
		}
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			//
		} finally {
			out.close();
		}
	}

}
