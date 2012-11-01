package com.ccvnc.test;

import java.net.ServerSocket;
import java.util.Hashtable;

import junit.framework.TestCase;

import com.ccvnc.PortManager;

/**
 * PortManagerTest
 * 
 * @author Volodymyr M. Lisivka
 */
public class PortManagerTest extends TestCase {
	public PortManagerTest(String method) {
		super(method);
	}

	public void testIsPortFree() throws Throwable {
		ServerSocket socket = null;
		assertTrue(PortManager.isPortFree(7777));

		try {
			socket = new ServerSocket(6666);
			assertFalse(PortManager.isPortFree(6666));
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	public void testAssignPort() throws Throwable {
		ServerSocket socket = null;

		int port = PortManager.getFreePortForServerConnection("test");
		assertTrue(port == PortManager.getFreePortForServerConnection("test"));

		try {
			socket = new ServerSocket(port);
			assertFalse(port == PortManager
					.getFreePortForServerConnection("test"));
		} finally {
			if (socket != null)
				socket.close();
		}
	}

	@SuppressWarnings("unchecked")
	public void testAssignManyPorts() throws Throwable {
		// Assign 4900 ports - all they must be different
		int diapason = PortManager.SERVER_SOCKETS_DIAPASON_END
				- PortManager.SERVER_SOCKETS_DIAPASON_BEGIN;

		Hashtable ports = new Hashtable(diapason);

		for (int i = 0; i < diapason - 100; i++) {
			String key = String.valueOf(PortManager
					.getFreePortForServerConnection("test" + i));
			assertFalse(
					"Duplicate ports at " + i + "/" + diapason + " request",
					ports.containsKey(key));
			ports.put(key, Boolean.TRUE);
		}

	}
}
