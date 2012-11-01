package com.ccvnc.test;

import java.net.MalformedURLException;
import java.net.Socket;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.xmlrpc.WebServer;

import com.ccvnc.Controller;
import com.ccvnc.NullAuthentificator;
import com.ccvnc.caller.Caller;

/**
 * ControllerTest
 * 
 * @author Volodymyr M. Lisivka
 */

public class ControllerTest extends TestCase {
	public ControllerTest(String method) {
		super(method);
	}

	private WebServer webserver;

	public void setUp() throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		Properties properties = new Properties();

		properties.setProperty("Controller.authentificatorClass",
				NullAuthentificator.class.getName());
		// Run webserver
		webserver = new WebServer(7778);
		webserver.addHandler("$default", new Controller(properties));
		webserver.start();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		try {
			Caller.initialize("http://localhost:7778/");
		} catch (MalformedURLException e) {
			fail("Can't initialize caller: " + e);
		}

	}

	public void tearDown() {
		// Stop webserver
		webserver.shutdown();
	}

	public void testMeeting() throws Throwable {

		Properties props = new Properties();

		String userName = "test";
		String password = "pass";

		String displayName = Caller.createMeeting(userName, password,
				"localhost", props);
		assertTrue(displayName.indexOf("test:localhost#") == 0);

		// Make connection to local VNC display (not portable test)
		// Caller.connectToServer(userName,password,displayName,"127.0.0.1",5901,"ticket",props);
		// Thread.sleep(500);
		// SessionManager
		// session=Controller.getSessionManager(displayName,true);
		// assertNotNull(session.getServer().getScreen().getDesktopName());
		// assertEquals("lisa.mystery.lviv.net:1 (lvm)",session.getServer().getScreen().getDesktopName());

		// Start server listener
		int serverListenerPort = Caller.listenForServer(userName, password,
				displayName, "testpass", props);

		// Start client listener
		int clientListenerPort = Caller.listenForClients(userName, password,
				displayName, "testpass", "guest", props);

		Thread.sleep(500);

		{// Validate server connection
			Socket socket = new Socket("localhost", serverListenerPort);
			socket.close();
		}

		{// Validate client connection
			Socket socket = new Socket("localhost", clientListenerPort);
			socket.close();
		}

		// Stop listenners
		Caller.shutdownMeeting("test", "pass", displayName, props);
	}

}
