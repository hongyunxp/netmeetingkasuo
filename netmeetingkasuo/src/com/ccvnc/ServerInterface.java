package com.ccvnc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.ccvnc.packets.Packet;
import com.ccvnc.packets.server.ServerPacket;

/**
 * Interface between VNC server and our proxy.
 * 
 * @author Volodymyr M. Lisivka
 */
public class ServerInterface extends Communicator {
	private int authType;
	private byte[] challenge;

	public ServerInterface(SessionManager session) {
		super(session, null, null);
	}

	public ServerInterface(SessionManager session, DataInputStream is,
			DataOutputStream os) {
		super(session, is, os);
	}

	public int getAuthType() {
		return authType;
	}

	/**
	 * Do handshaking with server.
	 * 
	 * Handshake protocol, then do authentification and initialization.
	 */
	public void handshake(String password) throws IOException {
		synchronized (os) {
			handshakeProtocol();
			doAuthentification(password);
			doInitialization();
		}
	}

	/**
	 * Read server protocol version and send our version to it.
	 */
	public void handshakeProtocol() throws IOException {
		setStatus(STATUS_HANDSHAKING_PROTOCOL);
		byte[] buf = new byte[12];
		is.readFully(buf);
		String protocol = new String(buf);
		logger.debug("VNC Server protocol version: " + protocol);

		if (protocol.indexOf(VNC_PROTOCOL_VERSION_MAJOR) != 0)
			throw new RuntimeException("Unsupported protocol version: \""
					+ protocol + "\"");

		int vncProtocolVersionMinor = Integer.parseInt(protocol.substring(
				VNC_PROTOCOL_VERSION_MAJOR.length(), protocol.length() - 1));
		if (vncProtocolVersionMinor < 3)
			throw new RuntimeException("Unsupported protocol version: \""
					+ protocol + "\"");
		if (vncProtocolVersionMinor < 7)
			vncProtocolVersionMinor = 3;
		if (vncProtocolVersionMinor >= 7)
			vncProtocolVersionMinor = 7;

		// If screen already have protocol version set and it lower than our,
		// then use it
		if (screen.getRfbMinorVersion() > 0
				&& vncProtocolVersionMinor > screen.getRfbMinorVersion())
			vncProtocolVersionMinor = screen.getRfbMinorVersion();

		screen.setRfbMinorVersion(vncProtocolVersionMinor);

		String ourProtocolString = VNC_PROTOCOL_VERSION_MAJOR + "00"
				+ vncProtocolVersionMinor + "\n";
		os.write(ourProtocolString.getBytes());
		logger.debug("Our protocol version: " + ourProtocolString);
		os.flush();
	}

	/**
	 * Send our password to server.
	 */
	private void doAuthentification(String password) throws IOException {
		setStatus(STATUS_AUTHENTIFICATION);

		if (screen.getRfbMinorVersion() == 3) {
			// Read auth type
			authType = is.readInt();

			switch (authType) {
			case CONNECTION_FAILED:

				logger.debug("Authorization failed");
				// Read reason and throw exeption
				int length = is.readInt();
				byte[] buf = new byte[length];
				is.readFully(buf);
				String authFailedReason = new String(buf);
				throw new RuntimeException(
						"Connection to VNC server failed, reason: "
								+ authFailedReason);
			case NO_AUTH: // No authorization

				logger.debug("Authorization not needed.");
				break;
			case VNC_AUTH: // VNC authorization
				doVncAuth(password);
				break;
			default:
				throw new RuntimeException("Unknown authorization code: "
						+ authType);
			}
		} else if (screen.getRfbMinorVersion() >= 7) {
			// Read number of security types
			int numberOfSecurityTypes = is.readByte();

			if (numberOfSecurityTypes == 0) {// Connection failed
				int length = is.readInt();
				byte[] reason = new byte[length];
				is.readFully(reason);
				throw new RuntimeException("Connection failed, server reason: "
						+ new String(reason));
			}

			byte[] securityTypes = new byte[numberOfSecurityTypes];
			is.readFully(securityTypes);

			// Search for NO_AUTH or VNC_AUTH
			boolean authorized = false;
			for (int i = 0; i < numberOfSecurityTypes; i++) {
				if (securityTypes[i] == NO_AUTH) {
					// Choose no authorization
					os.writeByte(NO_AUTH);
					os.flush();

					logger.debug("Authorization not needed.");
					authorized = true;
					break;
				} else if (securityTypes[i] == VNC_AUTH) {
					// Choose VNC authorization
					os.writeByte(VNC_AUTH);
					os.flush();

					doVncAuth(password);
					authorized = true;
					break;
				}
			}
			if (!authorized)
				throw new RuntimeException(
						"No known security method listed by server.");
		}
	}

	/**
	 * Send our password to server.
	 */
	private void doVncAuth(String password) throws IOException {
		logger.debug("VNC authentification.");

		// Read challenge
		challenge = new byte[16];
		is.readFully(challenge);

		// Encode chanlenge with password
		byte[] response = encodePassword(challenge, password);

		// Send encoded challenge
		os.write(response);
		os.flush();

		// Read response
		int authResult = is.readInt();
		switch (authResult) {
		case VNC_AUTH_OK:

			logger.debug("VNC authentification - OK.");
			break;
		default:
		case VNC_AUTH_FAILED:
		case VNC_AUTH_TOO_MANY:

			logger.debug("VNC authentification - failed: " + authResult);
			throw new RuntimeException("VNC authentification - failed: "
					+ authResult);
		}
	}

	/**
	 * Send shared desktop flag:
	 * <table>
	 * <tr>
	 * <th>Bytes</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8</td>
	 * <td>sharedDesktop (ignored)</td>
	 * </tr>
	 * </table>
	 *<br />
	 * Read framebuffer geometry:
	 * <table>
	 * <tr>
	 * <th>Bytes</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>U16</td>
	 * <td>framebufferWidth</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>U16</td>
	 * <td>framebufferHeight</td>
	 * </tr>
	 * </table>
	 *<br />
	 * Read pixel format:
	 * <table>
	 * <tr>
	 * <th>Bytes</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8</td>
	 * <td>bitsPerPixel</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8</td>
	 * <td>depth</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8</td>
	 * <td>bigEndianFlag</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8</td>
	 * <td>trueColourFlag</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>U16</td>
	 * <td>redMax</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>U16</td>
	 * <td>grenMax</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>U16</td>
	 * <td>blueMax</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8</td>
	 * <td>redShift</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8</td>
	 * <td>greenShift</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8</td>
	 * <td>blueShift</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>U8</td>
	 * <td>padding</td>
	 * </tr>
	 * </table>
	 *<br />
	 * Read desktop name:
	 * <table>
	 * <tr>
	 * <th>Bytes</th>
	 * <th>Type</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>U16</td>
	 * <td>desktopNameLength</td>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>U8[]</td>
	 * <td>desktopName</td>
	 * </tr>
	 * </table>
	 * 
	 */
	public void doInitialization() throws IOException, RuntimeException {
		setStatus(STATUS_INITIALIZATION);
		synchronized (screen) {
			{// Send client initialization message
				os.writeByte(1);// Send shared flag
				os.flush();
			}

			// Read server initialization message
			{// Framebuffer size
				int framebufferWidth = is.readUnsignedShort();
				int framebufferHeight = is.readUnsignedShort();
				screen.setFramebufferSize(framebufferWidth, framebufferHeight);
				logger.debug("framebufferWidth=" + framebufferWidth
						+ ", framebufferHeight=" + framebufferHeight);
			}

			{// Pixel format
				int bitsPerPixel = is.readUnsignedByte();
				int depth = is.readUnsignedByte();
				int bigEndianFlag = is.readUnsignedByte();
				int trueColorFlag = is.readUnsignedByte();
				int redMax = is.readUnsignedShort();
				int greenMax = is.readUnsignedShort();
				int blueMax = is.readUnsignedShort();
				int redShift = is.readUnsignedByte();
				int greenShift = is.readUnsignedByte();
				int blueShift = is.readUnsignedByte();
				is.skipBytes(3);// Skip padding

				screen.setPixelFormat(bitsPerPixel, depth, bigEndianFlag,
						trueColorFlag, redMax, greenMax, blueMax, redShift,
						greenShift, blueShift);

				logger.debug("bitsPerPixel=" + bitsPerPixel + "("
						+ screen.getBytesPerPixel() + ")" + ", depth=" + depth
						+ ", bigEndianFlag=" + bigEndianFlag
						+ ", trueColorFlag=" + trueColorFlag + ", redMax="
						+ redMax + ", greenMax=" + greenMax + ", blueMax="
						+ blueMax + ", redShift=" + redShift + ", greenShift="
						+ greenShift + ", blueShift=" + blueShift + "");
			}

			{// Read desktop name
				int desktopNameLength = is.readInt();
				byte[] desktopNameBuf = new byte[desktopNameLength];
				is.readFully(desktopNameBuf);
				String desktopName = new String(desktopNameBuf);
				screen.setDesktopName(desktopName);

				logger.debug("desktopName=" + desktopName);
			}
		}
	}

	/**
	 * Get a class which can handle this packet type.
	 * 
	 * @param messageType
	 *            an packet type
	 * 
	 * @return a Packet object to handle this packet
	 * 
	 */
	public Packet getPacketHandler(int messageType) {
		Packet packet = PacketManager.getServerPacketHandler(messageType);
		return packet;
	}

	/**
	 * Incomming packet received.
	 * 
	 * Pass it to session manager.
	 * 
	 */
	public void handleIncommingPacket(Packet packet) {
		session.handleServerPacket((ServerPacket) packet);
	}

	/**
	 * Use traffic manager to choose the best packet from packet queue to send.
	 */
	protected Packet getNextPacketToSend() {
		return session.getTrafficManager().getPrefferedPacketToSendForServer(
				this);
	}

	/**
	 * Notify traffic manager about packet which we will send to client.
	 */
	protected Packet filterPacketBeforeWrite(Packet packet) {
		return session.getTrafficManager().filterServerPacketBeforeWrite(this,
				packet);
	}

	/**
	 * Notify traffic manager about packet which we was readed from client.
	 */
	protected Packet filterPacketAfterRead(Packet packet) {
		return session.getTrafficManager().filterServerPacketAfterRead(this,
				packet);
	}

	/**
	 * Remove itself from session.
	 */
	public void closeConnection() {
		if (session != null)
			session.setServer(null);

		// Close connection with server
		super.closeConnection();
	}

}
