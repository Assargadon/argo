package net.kiborgov.argo.android.display.net.proto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class HereDocProtocol extends CairoProtocol {

	private BufferedReader in;
	private PrintWriter out;
	private String documentEnd;

	HereDocProtocol(InputStream in, OutputStream out, String documentEnd) {
		this.in = new BufferedReader(new InputStreamReader(in));
		this.out = new PrintWriter(new OutputStreamWriter(out)); // TODO: probably not needed
		this.documentEnd = documentEnd;
	}

	@Override
	public byte[] read() throws IOException {
		String line;
		if (null == (line = in.readLine()))
			throw new IOException("No data could be read");
		if (documentEnd.equals(line))
			return null;
		return line.getBytes();
	}

	@Override
	public void ack() {
		out.println("OK");
		out.flush();
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
