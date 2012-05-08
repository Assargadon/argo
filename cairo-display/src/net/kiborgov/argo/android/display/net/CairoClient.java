package net.kiborgov.argo.android.display.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import net.kiborgov.argo.android.display.net.proto.CairoProtocol;
import net.kiborgov.argo.android.display.net.proto.CairoProtocolFactory;

public class CairoClient implements Runnable {
	private static String TAG = "cairo-display";

	/*
	protected static String PARAM_CAIRO_SERVER_HOST = "net.kiborgov.argo.server.cairo.Host";
	protected static String PARAM_CAIRO_SERVER_PORT = "net.kiborgov.argo.server.cairo.Port";
	protected static String PARAM_CAIRO_SERVER_CONNECT_TIMEOUT = "net.kiborgov.argo.server.cairo.ConnectTimeout";
	*/

	protected String host = "192.168.1.2";
	protected int port = 7778;
	protected int timeout = 20000;

	protected Thread thread;
	protected List<CairoClientListener> listeners = new ArrayList<CairoClientListener>();

	protected CairoProtocolFactory protocolFactory;

	public CairoClient(CairoProtocolFactory protocolFactory) {
		this.protocolFactory = protocolFactory;
		thread = new Thread(this);
		thread.start();
	}

	public void close() {
		thread.interrupt();
	}

	/*
	private void restore(SharedPreferences config) {
		host = config.getString(PARAM_CAIRO_SERVER_HOST, "192.168.1.2");
		port = config.getInt(PARAM_CAIRO_SERVER_PORT, 7788);
		timeout = config.getInt(PARAM_CAIRO_SERVER_CONNECT_TIMEOUT, 20000);
	}
	*/

	public void run() {
		while (!thread.isInterrupted()) {
			Socket client = null;
			try {
				client = new Socket();
				client.connect(new InetSocketAddress(host, port), timeout);
				handleConnection(client);
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (null != client)
					try {
						client.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		}
	}

	private void handleConnection(Socket client) throws IOException {
		CairoProtocol proto = protocolFactory.create(client.getInputStream(), client.getOutputStream());
		try {
			while (!thread.isInterrupted()) {
				byte[] buffer = proto.read();
				if (null != buffer) {
					onDataRead(buffer, 0, buffer.length);
				} else {
					onDataComplete();
					proto.ack();
				}
			}
		} finally {
			proto.close();
		}
	}

	private void onDataRead(byte[] buffer, int start, int length) {
		for (CairoClientListener listener : listeners)
			listener.onDataRead(buffer, start, length);
	}

	private void onDataComplete() {
		for (CairoClientListener listener : listeners)
			listener.onDataComplete();
	}

	public void addListener(CairoClientListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CairoClientListener listener) {
		listeners.remove(listener);
	}
}
