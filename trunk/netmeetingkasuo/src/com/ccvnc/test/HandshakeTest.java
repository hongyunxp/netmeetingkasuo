package com.ccvnc.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Properties;

import junit.framework.TestCase;

import com.ccvnc.ClientInterface;
import com.ccvnc.Communicator;
import com.ccvnc.RfbConstants;
import com.ccvnc.Screen;
import com.ccvnc.ServerInterface;
import com.ccvnc.SessionManager;

/**
 * HandshakeTest
 * 
 * @author Volodymyr M. Lisivka
 */
public class HandshakeTest extends TestCase implements RfbConstants {
	@SuppressWarnings("unused")
	private static final String fullAccessPassword = "admin",
			viewOnlyAccessPassword = "guest";

	public HandshakeTest(String method) {
		super(method);
	}

	public void testClientAuthentification_none() throws Throwable {
		{
			// Test RFB3.3
			byte[] noAuthorization = { 0, 0, 0, 1 // NO_AUTH
			};
			byte[] input = {};

			testNoAuth(input, noAuthorization, 3);
		}

		{
			// Test RFB3.7
			byte[] noAuthorization = { 1,// One security method
					1 // NO_AUTH
			};
			byte[] input = { 1 // NO_AUTH
			};

			testNoAuth(input, noAuthorization, 7);
		}
	}

	private void testNoAuth(byte[] input, byte[] noAuthorization,
			int minor_version) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(input);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		ClientInterface client = new ClientInterface(new SessionManager("test",
				"test"), new DataInputStream(is), new DataOutputStream(os),
				null, null);
		client.setScreen(new Screen(minor_version));
		client.doAuthentification();

		// Compare arrays
		byte[] out = os.toByteArray();

		assertEquals(noAuthorization.length, out.length);
		assertEquals(new String(noAuthorization), new String(out));
	}

	public void testClientAuthentification_vnc() throws Throwable {
		assertEquals(testClientPassword("foo", "boo", "foo"), VNC_AUTH_OK);
		assertEquals(testClientPassword("foo", "boo", "boo"), VNC_AUTH_OK);
		assertEquals(testClientPassword("foo", "boo", "bla"), VNC_AUTH_FAILED);

		assertEquals(testClientPassword("foo", "boo", "foo"), VNC_AUTH_OK, 7);
		assertEquals(testClientPassword("foo", "boo", "boo"), VNC_AUTH_OK, 7);
		assertEquals(testClientPassword("foo", "boo", "bla"), VNC_AUTH_FAILED,
				7);

		assertEquals(testClientPassword("foo", null, "foo"), VNC_AUTH_OK);
		assertEquals(testClientPassword("foo", null, "boo"), VNC_AUTH_FAILED);
		assertEquals(testClientPassword("foo", null, "bla"), VNC_AUTH_FAILED);

		assertEquals(testClientPassword(null, "boo", "foo"), VNC_AUTH_FAILED);
		assertEquals(testClientPassword(null, "boo", "boo"), VNC_AUTH_OK);
		assertEquals(testClientPassword(null, "boo", "bla"), VNC_AUTH_FAILED);

		assertEquals(testClientPassword("foo", "", "foo"), VNC_AUTH_OK);
		assertEquals(testClientPassword("foo", "", "boo"), VNC_AUTH_FAILED);
		assertEquals(testClientPassword("foo", "", "bla"), VNC_AUTH_FAILED);

		assertEquals(testClientPassword("", "boo", "foo"), VNC_AUTH_FAILED);
		assertEquals(testClientPassword("", "boo", "boo"), VNC_AUTH_OK);
		assertEquals(testClientPassword("", "boo", "bla"), VNC_AUTH_FAILED);

		assertEquals(testClientPassword("foo", "boo", ""), VNC_AUTH_FAILED);
		assertEquals(testClientPassword("", "boo", ""), VNC_AUTH_FAILED);
		assertEquals(testClientPassword("foo", "", ""), VNC_AUTH_FAILED);
	}

	private int testClientPassword(String fullAccessPassword,
			String viewOnlyAccessPassword, String clientPassword)
			throws IOException {
		return testClientPassword(fullAccessPassword, viewOnlyAccessPassword,
				clientPassword, 3);
	}

	private int testClientPassword(String fullAccessPassword,
			String viewOnlyAccessPassword, String clientPassword,
			int minor_version) throws IOException {
		PipedInputStream ppis = new PipedInputStream();
		PipedOutputStream os = new PipedOutputStream(ppis);
		DataInputStream dis = new DataInputStream(ppis);

		PipedOutputStream ppos = new PipedOutputStream();
		PipedInputStream is = new PipedInputStream(ppos);
		DataOutputStream dos = new DataOutputStream(ppos);

		final ClientInterface client = new ClientInterface(new SessionManager(
				"test", "test"), new DataInputStream(is), new DataOutputStream(
				os), fullAccessPassword, viewOnlyAccessPassword);
		client.setScreen(new Screen(minor_version));
		new Thread(new Runnable() {
			public void run() {
				try {
					client.doAuthentification();
				} catch (Exception e) {
				}
			}
		}).start();

		// Read authentification type
		if (minor_version == 3) {
			int authType = dis.readInt();
			assertEquals(authType, VNC_AUTH);
		} else // minor_version==7
		{
			int numberOfSecurityTypes = dis.readByte();
			byte[] securityTypes = new byte[numberOfSecurityTypes];
			dis.readFully(securityTypes);
			assertEquals(securityTypes[0], VNC_AUTH);
			dos.writeByte(VNC_AUTH);
		}

		byte[] challenge = new byte[16];
		dis.readFully(challenge);
		byte[] encpasswd = Communicator.encodePassword(challenge,
				clientPassword);
		dos.write(encpasswd);

		int response = dis.readInt();

		return response;
	}

	private String getServerInterfaceHandshakedProtocol(String protocol,
			int maxMinorVersion) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(protocol.getBytes());
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		ServerInterface server = new ServerInterface(new SessionManager("test",
				"test"), new DataInputStream(is), new DataOutputStream(os));
		server.setScreen(new Screen(maxMinorVersion));
		server.handshakeProtocol();

		return new String(os.toByteArray());
	}

	public void testServerPtotocolHandshake() throws Throwable {
		assertEquals("RFB 003.007\n", getServerInterfaceHandshakedProtocol(
				"RFB 003.007\n", 7));
		assertEquals("RFB 003.003\n", getServerInterfaceHandshakedProtocol(
				"RFB 003.003\n", 7));
		assertEquals("RFB 003.007\n", getServerInterfaceHandshakedProtocol(
				"RFB 003.008\n", 7));
		assertEquals("RFB 003.003\n", getServerInterfaceHandshakedProtocol(
				"RFB 003.004\n", 7));

		assertEquals("RFB 003.003\n", getServerInterfaceHandshakedProtocol(
				"RFB 003.007\n", 3));
		assertEquals("RFB 003.003\n", getServerInterfaceHandshakedProtocol(
				"RFB 003.003\n", 3));
		assertEquals("RFB 003.003\n", getServerInterfaceHandshakedProtocol(
				"RFB 003.008\n", 3));
		assertEquals("RFB 003.003\n", getServerInterfaceHandshakedProtocol(
				"RFB 003.004\n", 3));
	}

	public void testInitialization() throws Throwable {
		SessionManager session = new SessionManager("test", "test");

		{
			byte[] init = new byte[] { (800 >>> 8) & 0xff,
					800 & 0xff, // Framebuffer width
					(600 >>> 8) & 0xff,
					600 & 0xff, // Framebuffer height

					// Server pixel format
					24,// Bits per pixel
					24,// Depth
					0,// Big endian flag
					1,// True color flag
					0,
					(byte) 255,// Red max
					0,
					(byte) 255,// Green max
					0,
					(byte) 255,// Blue max
					16,// Red shift
					8,// Green shift
					0,// Blue shift

					0, 0,
					0,// Padding

					// Desktop name
					0, 0, 0, 4, "Test".getBytes()[0], "Test".getBytes()[1],
					"Test".getBytes()[2], "Test".getBytes()[3] };

			ByteArrayInputStream is = new ByteArrayInputStream(init);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			ServerInterface server = new ServerInterface(session,
					new DataInputStream(is), new DataOutputStream(os));
			Screen screen = new Screen(7);
			screen
					.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW });
			server.setScreen(screen);
			server.doInitialization();

			Screen ss = server.getScreen();
			ss.validate();

			assertEquals(800, ss.getFramebufferWidth());
			assertEquals(600, ss.getFramebufferHeight());

			assertEquals(24, ss.getBitsPerPixel());
			assertEquals(24, ss.getDepth());
			assertEquals(3, ss.getBytesPerPixel());

			assertEquals(0, ss.getBigEndianFlag());
			assertEquals(1, ss.getTrueColorFlag());

			assertEquals(255, ss.getRedMax());
			assertEquals(255, ss.getGreenMax());
			assertEquals(255, ss.getBlueMax());

			assertEquals(16, ss.getRedShift());
			assertEquals(8, ss.getGreenShift());
			assertEquals(0, ss.getBlueShift());

			assertEquals("Test", ss.getDesktopName());

			session.setServer(server);
		}

		// Now test client initialization
		{

			byte[] init = new byte[] { 1 // Shared flag
			};

			ByteArrayInputStream is = new ByteArrayInputStream(init);
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			ClientInterface client = new ClientInterface(session,
					new DataInputStream(is), new DataOutputStream(os));
			Screen clientScreen = new Screen(session.getServer().getScreen(),
					false);
			clientScreen
					.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW });
			client.setViewOnlyAccess(false);
			client.setScreen(clientScreen);

			client.doInitialization();

			// Validate screen and settings
			Screen cs = client.getScreen();
			cs.validate();

			assertEquals(800, cs.getFramebufferWidth());
			assertEquals(600, cs.getFramebufferHeight());

			assertEquals(24, cs.getBitsPerPixel());
			assertEquals(24, cs.getDepth());
			assertEquals(3, cs.getBytesPerPixel());

			assertEquals(0, cs.getBigEndianFlag());
			assertEquals(1, cs.getTrueColorFlag());

			assertEquals(255, cs.getRedMax());
			assertEquals(255, cs.getGreenMax());
			assertEquals(255, cs.getBlueMax());

			assertEquals(16, cs.getRedShift());
			assertEquals(8, cs.getGreenShift());
			assertEquals(0, cs.getBlueShift());

			assertEquals("Test", cs.getDesktopName());

			session.addClient(session.getTrafficManager().filterClient(client,
					new Properties()));

			// Valiadate output of client with ServerInterface
			ServerInterface server = new ServerInterface(session,
					new DataInputStream(new ByteArrayInputStream(os
							.toByteArray())), new DataOutputStream(
							new ByteArrayOutputStream()));
			Screen ss = new Screen(7);
			ss.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW });
			server.setScreen(ss);

			server.doInitialization();

			ss.validate();

			assertEquals(800, ss.getFramebufferWidth());
			assertEquals(600, ss.getFramebufferHeight());

			assertEquals(24, ss.getBitsPerPixel());
			assertEquals(24, ss.getDepth());
			assertEquals(3, ss.getBytesPerPixel());

			assertEquals(0, ss.getBigEndianFlag());
			assertEquals(1, ss.getTrueColorFlag());

			assertEquals(255, ss.getRedMax());
			assertEquals(255, ss.getGreenMax());
			assertEquals(255, ss.getBlueMax());

			assertEquals(16, ss.getRedShift());
			assertEquals(8, ss.getGreenShift());
			assertEquals(0, ss.getBlueShift());

			assertEquals("Test", ss.getDesktopName());
		}
	}
}
