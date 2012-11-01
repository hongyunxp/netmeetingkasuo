package com.ccvnc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.ccvnc.packets.Packet;
import com.ccvnc.packets.client.ClientPacket;

/**
 * Interface between VNC client and our proxy.
 * 
 * @author Volodymyr M. Lisivka
 */
public class ClientInterface extends Communicator {
	private boolean viewOnlyAccess = true;// access type depends on password
	private String fullAccessPassword;
	private String viewOnlyAccessPassword;

	public ClientInterface(SessionManager session) {
		super(session, null, null);
		this.session = session;
	}

	public ClientInterface(SessionManager session, DataInputStream is,
			DataOutputStream os) {
		super(session, is, os);
		this.session = session;
	}

	public ClientInterface(SessionManager session, String fullAccessPassword,
			String viewOnlyAccessPassword) {
		super(session, null, null);
		this.fullAccessPassword = fullAccessPassword;
		this.viewOnlyAccessPassword = viewOnlyAccessPassword;
		this.session = session;
	}

	public ClientInterface(SessionManager session, DataInputStream is,
			DataOutputStream os, String fullAccessPassword,
			String viewOnlyAccessPassword) {
		super(session, is, os);
		this.fullAccessPassword = fullAccessPassword;
		this.viewOnlyAccessPassword = viewOnlyAccessPassword;
		this.session = session;
	}

	public void setViewOnlyAccess(boolean viewOnlyAccess) {
		this.viewOnlyAccess = viewOnlyAccess;
	}

	public boolean isViewOnlyAccess() {
		return viewOnlyAccess;
	}

	public void handlePolicy() throws IOException {
		byte[] buf = new byte[22];
		is.readFully(buf);
		String policy = new String(buf);
		if (policy.equals("<policy-file-request/>")) {
			logger.info("处理Flash策略认证");
			os.writeChars("<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>\0");
			os.flush();
		}
	}

	/**
	 * Do handshaking with client.
	 * 
	 * Handshake protocol, then do authentification and initialization.
	 */
	public void handshake() throws IOException {
		handshakeProtocol();
		if (!doAuthentification())
			return;
		doInitialization();
	}

	/**
	 * Read shared desktop flag:
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
	 * Send framebuffer geometry:
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
	 * Send pixel format:
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
	 * Send desktop name:
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
	public void doInitialization() throws IOException {
		setStatus(STATUS_INITIALIZATION);
		// Read client initialization message
		{
			int sharedDesktop = is.readUnsignedByte();
			logger.debug("Client "
					+ ((sharedDesktop == 0) ? "do not " : "")
					+ "want shared desktop");
		}

		logger.debug("Sending to client:");
		// Send server initialization message to client
		{
			// Send framebuffer size
			os.writeShort(screen.getFramebufferWidth());
			os.writeShort(screen.getFramebufferHeight());
			logger.debug("Framebuffer geometry: "
					+ screen.getFramebufferWidth() + "x"
					+ screen.getFramebufferHeight());
		}
		{// Send pixel format
			os.write(screen.getBitsPerPixel());
			logger.debug("BPP: " + screen.getBitsPerPixel());
			os.write(screen.getDepth());
			logger.debug("Depth: " + screen.getDepth());
			os.write(screen.getBigEndianFlag());
			logger
					.debug("BigEndianFlag: " + screen.getBigEndianFlag());
			os.write(screen.getTrueColorFlag());
			logger
					.debug("TrueColorFlag: " + screen.getTrueColorFlag());
			os.writeShort(screen.getRedMax());
			logger.debug("redMax: " + screen.getRedMax());
			os.writeShort(screen.getGreenMax());
			logger.debug("greenMax: " + screen.getGreenMax());
			os.writeShort(screen.getBlueMax());
			logger.debug("blueMax: " + screen.getBlueMax());
			os.write(screen.getRedShift());
			logger.debug("redShift: " + screen.getRedShift());
			os.write(screen.getGreenShift());
			logger.debug("greenShift: " + screen.getGreenShift());
			os.write(screen.getBlueShift());
			logger.debug("blueShift: " + screen.getBlueShift());
			os.write(0);
			os.write(0);
			os.write(0);// Padding
		}
		{// Send desktop name
			String desktopName = screen.getDesktopName();
			if (viewOnlyAccess)
				desktopName += "(viewonly)";
			os.writeInt(desktopName.length());
			os.write(desktopName.getBytes());
			logger.debug("Desktop name: " + desktopName);
		}
		os.flush();
	}

	/**
	 * Authentificate client.
	 * 
	 * If password set, then ask client for password and set it status (full
	 * access or view only access) if password is correct. If password is not
	 * set, then client will have view only access without asking it for
	 * password.
	 */
	public boolean doAuthentification() throws IOException {
		setStatus(STATUS_AUTHENTIFICATION);
		if (screen.getRfbMinorVersion() >= 7)
			os.writeByte(1);// Number of security types supported by server (RFB
		// 3.7)

		// Send auth information to client
		if ((fullAccessPassword == null || fullAccessPassword.length() == 0)
				&& (viewOnlyAccessPassword == null || viewOnlyAccessPassword
						.length() == 0)) {
			if (screen.getRfbMinorVersion() >= 7)
				os.writeByte(NO_AUTH);// Security type
			else
				os.writeInt(NO_AUTH);// Security type

			viewOnlyAccess = true;// Only viewonly access when password is not
			// set
		} else {
			if (screen.getRfbMinorVersion() >= 7) {
				os.writeByte(VNC_AUTH);
				os.flush();
				int clientSecurityType = is.readByte();
				if (clientSecurityType != VNC_AUTH)
					throw new RuntimeException(
							"Wrong reply from client: expect VNC_AUTH (code: "
									+ VNC_AUTH + "), but receive: \""
									+ clientSecurityType + "\".");
			} else
				os.writeInt(VNC_AUTH);

			byte[] challenge = new byte[16];
			fillWithRandomData(challenge);
			os.write(challenge);
			os.flush();

			// Create encoded version of the fullAccessPassword and
			// viewOnlyAccessPassword
			byte[] fullAccessPasswordArray = null;
			byte[] viewOnlyAccessPasswordArray = null;
			if (fullAccessPassword != null && fullAccessPassword.length() > 0)
				fullAccessPasswordArray = encodePassword(challenge,
						fullAccessPassword);
			if (viewOnlyAccessPassword != null
					&& viewOnlyAccessPassword.length() > 0)
				viewOnlyAccessPasswordArray = encodePassword(challenge,
						viewOnlyAccessPassword);

			// Read response
			byte[] response = new byte[16];
			is.readFully(response);

			// Compare passwords
			if (compareArrays(response, fullAccessPasswordArray)) {
				os.writeInt(VNC_AUTH_OK);
				viewOnlyAccess = false;
			} else if (compareArrays(response, viewOnlyAccessPasswordArray)) {
				os.writeInt(VNC_AUTH_OK);
				viewOnlyAccess = true;
			} else {
				os.writeInt(VNC_AUTH_FAILED);
				os.flush();
				closeConnection();
				logger.debug("Authorization FAILED.");
				return false;
			}
		}
		os.flush();
		logger.debug("Authorization done.");
		return true;
	}

	/**
	 * Send our version to client and it protocol version.
	 */
	public void handshakeProtocol() throws IOException {
		setStatus(STATUS_HANDSHAKING_PROTOCOL);
		{// Send protocol version to client
			String protocolVersionString = VNC_PROTOCOL_VERSION_MAJOR + "00"
					+ screen.getRfbMinorVersion() + "\n";
			logger.debug("Our protocol version: "
					+ protocolVersionString);
			os.write(protocolVersionString.getBytes());
			os.flush();
		}

		{// Read client reply
			byte[] buf = new byte[12];
			is.readFully(buf);
			String protocol = new String(buf);
			logger.debug("VNC Client protocol version: " + protocol);
			
//			if(protocol.equals("<policy-file")){
//				handlePolicy();
//			}

			if (protocol.indexOf(VNC_PROTOCOL_VERSION_MAJOR) != 0)
				throw new RuntimeException("Incompatible protocol version: \""
						+ protocol + "\"");

			int vncProtocolVersionMinor = Integer
					.parseInt(protocol.substring(VNC_PROTOCOL_VERSION_MAJOR
							.length(), protocol.length() - 1));
			if (!(vncProtocolVersionMinor == 3 || vncProtocolVersionMinor == 7))
				throw new RuntimeException("Unsupported protocol version: \""
						+ protocol + "\"");

			// If screen already have protocol version set and it lower than
			// our, then use it
			if (screen.getRfbMinorVersion() > 0
					&& vncProtocolVersionMinor > screen.getRfbMinorVersion())
				vncProtocolVersionMinor = screen.getRfbMinorVersion();

			screen.setRfbMinorVersion(vncProtocolVersionMinor);
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
		Packet packet = PacketManager.getClientPacketHandler(messageType);
		return packet;
	}

	/**
	 * Incomming packet received.
	 * 
	 * Pass it to session manager.
	 * 
	 */
	public void handleIncommingPacket(Packet packet) {
		session.handleClientPacket((ClientPacket) packet);
	}

	/**
	 * Remove itself from session.
	 */
	public void closeConnection() {
		if (session != null)
			session.removeClient(this);

		super.closeConnection();
	}

	/**
	 * Use traffic manager to choose the best packet from packet queue to send.
	 */
	protected Packet getNextPacketToSend() {
		return session.getTrafficManager().getPrefferedPacketToSendForClient(
				this);
	}

	/**
	 * Notify traffic manager about packet which we will send to client.
	 */
	protected Packet filterPacketBeforeWrite(Packet packet) {
		return session.getTrafficManager().filterClientPacketBeforeWrite(this,
				packet);
	}

	/**
	 * Notify traffic manager about packet which we was readed from client.
	 */
	protected Packet filterPacketAfterRead(Packet packet) {
		return session.getTrafficManager().filterClientPacketAfterRead(this,
				packet);
	}

}
