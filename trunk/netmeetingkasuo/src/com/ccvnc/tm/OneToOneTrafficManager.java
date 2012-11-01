package com.ccvnc.tm;

import com.ccvnc.ClientInterface;
import com.ccvnc.ServerInterface;

/**
 * OneToOneTrafficManager - minimalistic traffic manager which allows to clients
 * to directly control VNC server.
 * 
 * What it should do in case of:
 * <ul>
 * <li>server connected
 * <ul>
 * <li>send list of supported encodings to server (use user list or calculated
 * list);</li>
 * <li>send pixel format to server (use user pixel format or client pixel
 * format);</li>
 * </ul>
 * </li>
 * 
 * <li>client connected
 * <ul>
 * <li>wait for SPF and SE packets from client.</li>
 * </ul>
 * </li>
 * 
 * <li>client disconnected
 * <ul>
 * <li>disconnect server too.</li>
 * </ul>
 * </li>
 * 
 * <li>server disconnected
 * <ul>
 * <li>disconnect clients too.</li>
 * </ul>
 * </li>
 * 
 * <li>SE received from client
 * <ul>
 * <li>recalculate list of supported encodings and set it to server, if needed.</li>
 * </ul>
 * </li>
 * 
 * <li>need to send SPF to server
 * <ul>
 * <li>always use client pixel format (or pixel format set by user, if it
 * exists).</li>
 * </ul>
 * </li>
 * 
 *</ul>
 * 
 * @author Volodymyr M. Lisivka
 */
public class OneToOneTrafficManager extends AbstractTrafficManager {
	public int[] getListOfEncodingsSupportedByTrafficManager() {
		return SUPPORTED_ENCODINGS_FOR_ONE_TO_ONE_CONNECTION_ARRAY;
	}

	/**
	 * Shutdown server interface when client closes connection.
	 */
	public synchronized void clientOut(ClientInterface client) {
		if (clients.contains(client))
			session.shutdownServerInterface(properties);

		super.clientOut(client);
	}

	/**
	 * Shutdown client interface when client closes connection.
	 */
	public synchronized void serverOut(ServerInterface server) {
		super.serverOut(server);
		session.shutdownClientInterfaces(properties);
	}

}
