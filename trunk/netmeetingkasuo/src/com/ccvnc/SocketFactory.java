package com.ccvnc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

/**
 * Interface for custom socket factories.
 * 
 * @author Volodymyr M. Lisivka
 */
public interface SocketFactory {
	/**
	 * Initalize this socket factory.
	 */
	public void initialize(Properties properties);

	/**
	 * Create a custom plain socket to connect to host:port.
	 */
	public Socket createSocket(String host, int port, Properties props)
			throws IOException;

	/**
	 * Create a custom server socket to listen for incomming connection.
	 */
	public ServerSocket createServerSocket(int port, Properties props)
			throws IOException;

	/**
	 * Shutdown custom server socket.
	 */
	public void shutdownServerSocket(ServerSocket socket, Properties props)
			throws IOException;
}
