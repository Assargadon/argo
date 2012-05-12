package net.kiborgov.argo.android.display.net.proto;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GenericProtocol extends CairoProtocol {

	private BufferedInputStream in;
	private BufferedOutputStream out;
	private final byte[] buffer;

	public GenericProtocol(InputStream in, OutputStream out, int bufferSize) {
		this.in = new BufferedInputStream(in);
		this.out = new BufferedOutputStream(out); // TODO: probably not needed
		this.buffer = new byte[bufferSize];
	}

	@Override
	public byte[] read() throws IOException {
		int read = in.read(buffer);
		if (read < buffer.length) {
			byte[] result = new byte[read];
			System.arraycopy(buffer, 0, result, 0, read);
			return result;
		}
		return buffer;
	}

	@Override
	public void ack() {
		// Nothing to do here
	}

	@Override
	public void close() {
		try {
			in.close();
		} catch (IOException e) {
			//
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				//
			}
		}
	}
}
