package com.ccvnc.test;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Properties;

import junit.framework.TestCase;

import com.ccvnc.ClientInterface;
import com.ccvnc.Communicator;
import com.ccvnc.RfbConstants;
import com.ccvnc.Screen;
import com.ccvnc.ServerInterface;
import com.ccvnc.SessionManager;
import com.ccvnc.packets.client.FramebufferUpdateRequestPacket;
import com.ccvnc.packets.server.FramebufferUpdatePacket;
import com.ccvnc.packets.server.rect.RawRect;
import com.ccvnc.tm.TrafficManager;

/**
 * SessionManagerTest
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unused")
public class SessionManagerTest extends TestCase {

	public SessionManagerTest(String method) {
		super(method);
	}

	public void testPacketPassing() throws Throwable {
		byte[] buf24 = new byte[] { -1, -1, -1, 0, 0, -1, 0, 0, 0, 0, 0, 0 };
		byte[] buf16 = new byte[] { -1, -1, 0, (byte) 0xf8, 0, 0, 0, 0 };

		// Send packet from server to session manager.
		// Packet must be received by clients in proper format
		// Send packet from client to session manager
		// Packet must be received by server

		// Setup session
		SessionManager session = new SessionManager("test", "test");
		Properties props = new Properties();

		ServerInterface server = new ServerInterface(session);
		Screen serverScreen = getScreen24LTRGB();
		serverScreen.setFramebufferSize(640, 480);
		serverScreen
				.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW });
		server.setScreen(serverScreen);
		server.setStatus(Communicator.STATUS_NORMAL);
		server
				.setOutputStream(new DataOutputStream(
						new ByteArrayOutputStream()));

		TrafficManager tm = session.getTrafficManager();
		session.setServer(tm.filterServer(server, props));

		ClientInterface client1 = new ClientInterface(session);
		client1.setScreen(getScreen24LTRGB());
		client1.setOutputStream(new DataOutputStream(
				new ByteArrayOutputStream()));
		client1.setStatus(Communicator.STATUS_NORMAL);
		session.addClient(session.getTrafficManager().filterClient(client1,
				props));

		ClientInterface client2 = new ClientInterface(session);
		client2.setScreen(getScreen16LTRGB());
		client2.setOutputStream(new DataOutputStream(
				new ByteArrayOutputStream()));
		client2.setStatus(Communicator.STATUS_NORMAL);
		session.addClient(session.getTrafficManager().filterClient(client2,
				props));

		ClientInterface client3 = new ClientInterface(session);
		client3.setScreen(getScreen24LTRGB());
		client3.setOutputStream(new DataOutputStream(
				new ByteArrayOutputStream()));
		client3.setStatus(Communicator.STATUS_NORMAL);
		session.addClient(session.getTrafficManager().filterClient(client3,
				props));

		sendFBUR(session, client1, 0);// Send nonincremental request
		sendFBUR(session, client2, 0);// Send nonincremental request
		sendFBUR(session, client3, 0);// Send nonincremental request

		sendFBU(session, server);// Send FBU

		assertEquals(1, client1.getQueueSize());
		assertEquals(1, client2.getQueueSize());
		assertEquals(1, client3.getQueueSize());

	}

	private Screen getScreen24LTRGB() {
		Screen screen = new Screen(7);
		screen.setFramebufferSize(640, 480);
		screen.setDesktopName("test");
		screen.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW });
		screen.setPixelFormat(24, 24, 0, 1, 255, 255, 255, 16, 8, 0);
		return screen;
	}

	private Screen getScreen16LTRGB() {
		Screen screen = new Screen(7);
		screen.setFramebufferSize(640, 480);
		screen.setDesktopName("test");
		screen.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW });
		screen.setPixelFormat(16, 16, 0, 1, 31, 63, 31, 11, 5, 0);
		return screen;
	}

	private String arrayToString(byte[] buf2) {
		StringBuffer sb = new StringBuffer(buf2.length * 3);
		for (int i = 0; i < buf2.length; i++) {
			if ((buf2[i] & 0xff) < 0x10)
				sb.append('0');

			sb.append(Integer.toHexString((buf2[i] & 0xff)));
			sb.append(' ');
		}
		return sb.toString();
	}

	private void sendFBU(SessionManager session, ServerInterface server) {
		RawRect rect = new RawRect();
		rect.setScreen(server.getScreen());
		rect.setHeaderParameters(RfbConstants.ENCODING_RAW, 0, 0, 2, 2);
		rect.setBuf(new byte[] { -1, -1, -1, 0, 0, -1, 0, 0, 0, 0, 0, 0 });
		rect.validate();

		FramebufferUpdatePacket packet = new FramebufferUpdatePacket(server
				.getScreen());
		packet.addRect(rect);
		packet.validate();

		session.handleServerPacket(packet);
	}

	private void sendFBUR(SessionManager session, ClientInterface client,
			int incremental) {
		Screen screen = client.getScreen();
		FramebufferUpdateRequestPacket packet = new FramebufferUpdateRequestPacket(
				incremental, 0, 0, screen.getFramebufferWidth(), screen
						.getFramebufferHeight());
		packet.setScreen(screen);
		packet.validate();

		session.handleClientPacket(packet);
	}

}
