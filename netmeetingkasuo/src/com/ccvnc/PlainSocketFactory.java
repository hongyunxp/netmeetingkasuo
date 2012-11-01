package com.ccvnc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * PlainSocketFactory
 * 
 * @author Volodymyr M. Lisivka
 */
public class PlainSocketFactory implements SocketFactory {
	public Socket createSocket(String host, int port, Properties props)
			throws IOException {
		return new Socket(host, port);
	}

	public ServerSocket createServerSocket(int port, Properties props)
			throws IOException {
		return new ServerSocket(port);
	}

	public void shutdownServerSocket(ServerSocket socket, Properties props)
			throws IOException {
		socket.close();
	}

	public void initialize(Properties properties) {
	}

}
