package net.kiborgov.argo.android.display.net;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

import net.kiborgov.argo.android.display.net.proto.CairoProtocol;
import net.kiborgov.argo.android.display.net.proto.factory.CairoProtocolFactory;
import android.util.Log;

public class CairoClient implements Runnable {
	private static String TAG = "cairo-display";

	/*
	protected static String PARAM_CAIRO_SERVER_HOST = "net.kiborgov.argo.server.cairo.Host";
	protected static String PARAM_CAIRO_SERVER_PORT = "net.kiborgov.argo.server.cairo.Port";
	protected static String PARAM_CAIRO_SERVER_CONNECT_TIMEOUT = "net.kiborgov.argo.server.cairo.ConnectTimeout";
	*/

	protected String host = "cyborgize.me";
	protected int port = 49778;
	protected int timeout = 20000;
	private boolean connected;

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
		Log.i(TAG, "CairoClient.run()");
		ServerSocket client = null;
		try {
			while (!thread.isInterrupted()) {
				SocketAddress address = null;
				try {
					client = new ServerSocket();
					address = new InetSocketAddress(port);
					client.bind(address);
					Log.i(TAG, "Successfully bound to " + address);
					break;
				} catch (BindException e) {
					if (thread.isInterrupted())
						throw e;
					Log.e(TAG, "Cannot bind to " + address + ": " + e.getMessage());
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						return;
					}
				}
			}
		} catch (IOException e) {
			// TODO Handle errors
			e.printStackTrace();
			return;
		}
		while (!thread.isInterrupted()) {
			try {
				handleConnection(client.accept());
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
		boolean error = false;

		connected = true;
		onConnect();
		CairoProtocol proto = protocolFactory.create(client.getInputStream(), client.getOutputStream());
		try {
			while (!thread.isInterrupted()) {
				byte[] buffer = proto.read();
				if (null != buffer) {
					onDataRead(buffer, 0, buffer.length);
				} else {
					proto.ack();
				}
			}
		} catch (IOException e) {
			error = true;
		} finally {
			proto.close();
			connected = false;
			onDisconnect(error);
		}
	}

	private void onConnect() {
		for (CairoClientListener listener : listeners)
			listener.onConnect();
	}

	private void onDisconnect(boolean error) {
		for (CairoClientListener listener : listeners)
			listener.onDisconnect(error);
	}

	private void onDataRead(byte[] buffer, int start, int length) {
		for (CairoClientListener listener : listeners)
			listener.onDataRead(buffer, start, length);
	}

	public boolean isConnected() {
		return connected;
	}

	public void addListener(CairoClientListener listener) {
		listeners.add(listener);
	}

	public void removeListener(CairoClientListener listener) {
		listeners.remove(listener);
	}
}
