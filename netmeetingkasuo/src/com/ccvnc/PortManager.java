package com.ccvnc;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Hashtable;

/**
 * PortManager
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class PortManager {
	public static final int CLIENT_SOCKETS_DIAPASON_BEGIN = 5900,
			CLIENT_SOCKETS_DIAPASON_END = 9999;
	public static final int SERVER_SOCKETS_DIAPASON_BEGIN = 10000,
			SERVER_SOCKETS_DIAPASON_END = 14999;

	private static Hashtable clientPorts = new Hashtable(
			CLIENT_SOCKETS_DIAPASON_END - CLIENT_SOCKETS_DIAPASON_BEGIN);
	private static Hashtable serverPorts = new Hashtable(
			SERVER_SOCKETS_DIAPASON_END - SERVER_SOCKETS_DIAPASON_BEGIN);

	public static int getFreePortForClientConnection(String displayName) {
		return getFreePort(clientPorts, displayName,
				CLIENT_SOCKETS_DIAPASON_BEGIN, CLIENT_SOCKETS_DIAPASON_END);
	}

	public static int getFreePortForServerConnection(String displayName) {
		return getFreePort(serverPorts, displayName,
				SERVER_SOCKETS_DIAPASON_BEGIN, SERVER_SOCKETS_DIAPASON_END);
	}

	private synchronized static int getFreePort(Hashtable map,
			String displayName, int dbegin, int dend) {
		if (map.containsKey(displayName)) {
			// Attempt to reuse early assigned port
			int port = ((Integer) serverPorts.get(displayName)).intValue();
			if (isPortFree(port))
				return port;
		}

		int port;
		do {
			// Randomly assign port from diapason
			port = assignPort(dbegin, dend);
			Integer portObj = new Integer(port);
			if (map.containsKey(portObj)) {
				for (int i = 0; i < (dend - dbegin); i++) {
					port++;
					if (port >= dend)
						port = dbegin;
					portObj = new Integer(port);

					if (!map.containsKey(portObj))
						break;

					int time = (int) (System.currentTimeMillis() - ((Long) map
							.get(portObj)).longValue());
					if (time > 1000 * 25 && isPortFree(port))// More than a 25
					// seconds old
					// and free
					{
						map.remove(portObj);
						break;
					}
				}
			}

			// Store assigned port
			map.put(displayName, portObj);
			map.put(portObj, new Long(System.currentTimeMillis()));
		} while (!isPortFree(port));

		return port;
	}

	private static int assignPort(int dend, int dstart) {
		for (int i = 0; i < 100; i++) {
			int port = (int) (Math.random() * (dend - dstart) + dstart);
			if (isPortFree(port))
				return port;
		}
		throw new RuntimeException("Can't assign new port from pool");
	}

	public static boolean isPortFree(int port) {
		try {
			new ServerSocket(port).close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
}
