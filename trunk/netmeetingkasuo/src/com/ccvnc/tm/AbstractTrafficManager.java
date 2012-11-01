package com.ccvnc.tm;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ccvnc.ClientInterface;
import com.ccvnc.Communicator;
import com.ccvnc.PacketManager;
import com.ccvnc.RfbConstants;
import com.ccvnc.ServerInterface;
import com.ccvnc.SessionManager;
import com.ccvnc.packets.Packet;
import com.ccvnc.packets.client.SetEncodingsPacket;
import com.ccvnc.packets.client.SetPixelFormatPacket;

/**
 * AbstractTrafficManager - contains common code for all traffic managers.
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public abstract class AbstractTrafficManager implements TrafficManager,
		RfbConstants {
	protected Logger logger;

	/**
	 * Parent session which owns this traffic manager.
	 */
	protected SessionManager session;

	/**
	 * Current VNC server.
	 */
	protected ServerInterface server;

	/**
	 * Hashtable to store information about server state.
	 */
	protected Hashtable serverProperties;

	/**
	 * Hashtable with hashtables to store information about each client state.
	 * 
	 * Client used as key.
	 */
	protected Hashtable clients = new Hashtable();

	/**
	 * List of encodings supported by all clients and by proxy.
	 */
	private int[] supportedEncodings;

	/**
	 * Pixel format used to comunicate with server.
	 */
	private SetPixelFormatPacket pixelFormat;

	/**
	 * List of encodings set by user.
	 */
	private int[] fixedEncodingsList;

	/**
	 * Pixel format set by user.
	 */
	protected SetPixelFormatPacket fixedPixelFormat;

	protected Properties properties;

	public AbstractTrafficManager() {
		logger = Logger.getLogger(this.getClass());
		supportedEncodings = getListOfEncodingsSupportedByTrafficManager();
	}

	/**
	 * Get list of encodings allowed to use with this traffic manager.
	 * 
	 * @return list of supported encodings
	 * 
	 */
	protected int[] getListOfEncodingsSupportedByTrafficManager() {
		return SUPPORTED_ENCODINGS_ARRAY;
	}

	/**
	 * Configure this traffic manager.
	 */
	public synchronized void configure(SessionManager session,
			Properties properties) {
		this.session = session;
		this.properties = properties;
	}

	/**
	 * Filter a new client.
	 * 
	 * @return a client interface or null, if client interface filtered out.
	 */
	public synchronized ClientInterface filterClient(ClientInterface client,
			Properties props) {
		clients.put(client, new Hashtable());
		return client;
	}

	/**
	 * Do an action when client close connection. (Eg. disconnect server too).
	 */
	public synchronized void clientOut(ClientInterface client) {
		clients.remove(client);
		resendSupportedEncodings();
	}

	/**
	 * Filter a new server.
	 * 
	 * @return a server interface or null, if server interface filtered out.
	 */
	public synchronized ServerInterface filterServer(ServerInterface server,
			Properties props) {
		this.server = server;
		serverProperties = new Hashtable();

		// Use user list of supported encodings, if available
		setFixedListOfSupportedEncodings(props.getProperty("encodings"));
		resendSupportedEncodings();

		// Use user pixel format, if available
		setFixedPixelFormat(props.getProperty("pixelFormat"));
		resendPixelFormat();

		return server;
	}

	/**
	 * Do an action when server close connection. (Eg. disconnect all clients
	 * too).
	 */
	public synchronized void serverOut(ServerInterface server) {
		this.server = null;
		serverProperties = null;
		supportedEncodings = null;// Resend
		pixelFormat = null;// Resend
	}

	/**
	 * Do an action before client interface will send the packet to the client.
	 * 
	 * @return a packet to send or null, to prevent sending.
	 */
	public synchronized Packet filterClientPacketBeforeWrite(
			ClientInterface client, Packet packet) {
		if (!client.getScreen().canAcceptPacket(packet)) {
			logger.warn("Packet type " + packet + " is unsupported by "
					+ client + ", ignored.");
			return null;
		}

		if (packet.getPacketType() == SERVER_FRAMEBUFFER_UPDATE)
			packet = onClientFBU(client, packet);

		return packet;
	}

	/**
	 * Filter incoming packet from server after reading.
	 * 
	 * @return a packet or null, if packet filtered out.
	 */
	public synchronized Packet filterServerPacketAfterRead(
			ServerInterface server, Packet packet) {
		if (packet.getPacketType() == SERVER_FRAMEBUFFER_UPDATE)
			packet = onServerFBU(server, packet);

		return packet;
	}

	/**
	 * Filter incoming packet from server after reading.
	 * 
	 * @return a packet or null, if packet filtered out.
	 */
	public synchronized Packet filterClientPacketAfterRead(
			ClientInterface client, Packet packet) {
		if ((packet.getPacketType() == CLIENT_SET_ENCODINGS || packet
				.getPacketType() == CLIENT_SET_PIXEL_FORMAT)
				&& client.getScreen().isValid())
			client.setStatus(Communicator.STATUS_NORMAL);

		if (packet.getPacketType() == CLIENT_SET_ENCODINGS) {
			// Remove unsupported encodings and send packet with send encodings
			// to server again, if needed.
			resendSupportedEncodings();
			return null;
		}

		if (packet.getPacketType() == CLIENT_SET_PIXEL_FORMAT) {
			// Choose best pixel format (to reduce traffic or increase quality)
			resendPixelFormat();
			return null;
		}

		if (packet.getPacketType() == CLIENT_FRAMEBUFFER_UPDATE_REQUEST)
			packet = onClientFBUR(client, packet);

		return packet;
	}

	/**
	 * Calculate the best pixel format to use with server and send it, if
	 * necessary.
	 */
	protected synchronized void resendPixelFormat() {
		SetPixelFormatPacket newPixelFormat = calculateBestPixelFormat();
		if (pixelFormat == null || !pixelFormat.equals(newPixelFormat)) {
			logger.debug("Old pixel fromat: "
					+ ((pixelFormat != null) ? pixelFormat.dumpToString(true)
							: "NONE"));
			sendPixelFormat(newPixelFormat);
			logger.debug("New pixel fromat: "
					+ ((pixelFormat != null) ? pixelFormat.dumpToString(true)
							: "NONE"));
		}
	}

	/**
	 * Always use server pixel format.
	 */
	protected synchronized SetPixelFormatPacket calculateBestPixelFormat() {
		// Use user pixel format, if it set.
		if (fixedPixelFormat != null)
			return fixedPixelFormat;

		if (server == null || server.getScreen() == null)
			// Server is not available, just return any SPF
			return new SetPixelFormatPacket(32, 24, 0, 1, 255, 255, 255, 16, 8,
					0);
		else
			// ALways use server pixel format
			return new SetPixelFormatPacket(server.getScreen());
	}

	/**
	 * Send our pixel format to server.
	 */
	protected synchronized void sendPixelFormat(SetPixelFormatPacket packet) {
		if (server != null) {
			pixelFormat = packet;

			// Put packet into server queue
			server.handlePacket(packet);
		}
	}

	/**
	 * Decode list of encodings from string property and use only them.
	 */
	protected synchronized void setFixedPixelFormat(String pixelFormatString) {
		if (pixelFormatString != null) {// Convert string with encoding names
			// into array of codes

			if (pixelFormatString.equals("32"))// RGB888
				fixedPixelFormat = new SetPixelFormatPacket(32, 24, 0, 1, 255,
						255, 255, 16, 8, 0);
			else if (pixelFormatString.equals("16"))// RGB565
				fixedPixelFormat = new SetPixelFormatPacket(16, 16, 0, 1, 31,
						63, 31, 11, 5, 0);
			else if (pixelFormatString.equals("8"))// BGR233
				fixedPixelFormat = new SetPixelFormatPacket(8, 8, 0, 1, 7, 7,
						3, 0, 3, 6);
			else {
				// Calculate number of parameters
				int count = 0;
				for (StringTokenizer st = new StringTokenizer(
						pixelFormatString, ", "); st.hasMoreTokens(); st
						.nextToken())
					count++;
				if (count != 10)
					throw new RuntimeException(
							"Wrong pixelFormat property. Use short format: \"32\"/\"16\"/\"8\", or full format: \"BPP,depth,bigEndian,redMax,greenMax,blueMax,redShift,greenShift,blueShift\".");

				// Parse string
				StringTokenizer st = new StringTokenizer(pixelFormatString,
						", ");
				SetPixelFormatPacket packet = new SetPixelFormatPacket();
				packet.setBitsPerPixel(Integer.parseInt(st.nextToken()));
				packet.setDepth(Integer.parseInt(st.nextToken()));
				packet.setBigEndianFlag(Integer.parseInt(st.nextToken()));
				packet.setTrueColourFlag(Integer.parseInt(st.nextToken()));
				packet.setRedMax(Integer.parseInt(st.nextToken()));
				packet.setGreenMax(Integer.parseInt(st.nextToken()));
				packet.setBlueMax(Integer.parseInt(st.nextToken()));
				packet.setRedShift(Integer.parseInt(st.nextToken()));
				packet.setGreenShift(Integer.parseInt(st.nextToken()));
				packet.setBlueShift(Integer.parseInt(st.nextToken()));
				fixedPixelFormat = packet;
			}
			fixedPixelFormat.validate();
			logger.debug("Using fixed pixel format: "
					+ fixedPixelFormat.dumpToString(true));
		} else {
			fixedPixelFormat = null;
		}
	}

	/**
	 * Calculate list of supported encodings and send it to server, if that is
	 * necessary.
	 */
	protected synchronized void resendSupportedEncodings() {
		if (server == null)
			return;

		// Create new list of encodings
		int[] newEncodings = getSupportedEncodings();

		// Compare current set of encodings with new
		boolean needToResend = false;
		if (supportedEncodings != null) {
			if (newEncodings.length != supportedEncodings.length)
				needToResend = true;
			else
				for (int i = 0; i < supportedEncodings.length; i++)
					if (newEncodings[i] != supportedEncodings[i]) {
						needToResend = true;
						break;
					}
		} else
			// List of supported encodings was
			needToResend = true;

		if (needToResend) {
			logger.debug("Old encodings: "
					+ new SetEncodingsPacket(supportedEncodings)
							.dumpToString(true));
			sendSupportedEncodings(newEncodings);
			logger.debug("New encodings: "
					+ new SetEncodingsPacket(supportedEncodings)
							.dumpToString(true));
		}
	}

	/**
	 * Calculate list of encodings supported by all clients.
	 */
	public synchronized int[] getSupportedEncodings() {
		// If user set a list of supported encodings manually, then use only it
		if (fixedEncodingsList != null)
			return fixedEncodingsList;

		if (clients.size() > 0) {
			// Put all supported encodings into hashtable
			int[] allSupportedEncodings = getListOfEncodingsSupportedByTrafficManager();
			Hashtable encodings = new Hashtable();
			for (int i = 0; i < allSupportedEncodings.length; i++)
				encodings.put("" + allSupportedEncodings[i], Boolean.TRUE);

			// For each client
			for (Enumeration e = clients.keys(); e.hasMoreElements();) {
				int[] clientEncodings = ((ClientInterface) e.nextElement())
						.getScreen().getSupportedEncodings();
				if (clientEncodings != null) {
					// Remove encoding if it unsupported by client
					for (Enumeration en = encodings.keys(); en
							.hasMoreElements();) {
						int encoding = Integer.parseInt((String) en
								.nextElement());
						boolean remove = true;
						for (int i = 0; i < clientEncodings.length; i++)
							if (clientEncodings[i] == encoding
									||
									// Tight options for JPEG and ZLIB
									(encoding >= ENCODING_JPEG_QUALITY_LEVEL_0
											&& encoding <= ENCODING_JPEG_QUALITY_LEVEL_0 + 9
											&& clientEncodings[i] >= ENCODING_JPEG_QUALITY_LEVEL_0 && clientEncodings[i] <= ENCODING_JPEG_QUALITY_LEVEL_0 + 9)
									|| (encoding >= ENCODING_COMPRESS_LEVEL0
											&& encoding <= ENCODING_COMPRESS_LEVEL0 + 9
											&& clientEncodings[i] >= ENCODING_COMPRESS_LEVEL0 && clientEncodings[i] <= ENCODING_COMPRESS_LEVEL0 + 9)) {
								remove = false;
								break;
							}
						if (remove)
							encodings.remove("" + encoding);
					}
				}
			}
			// Create new list of encodings
			int[] newEncodings = new int[encodings.size()];
			int[] prototype = getListOfEncodingsSupportedByTrafficManager();
			for (int i = 0, j = 0; i < prototype.length; i++)
				if (encodings.containsKey("" + prototype[i]))
					newEncodings[j++] = prototype[i];
			return newEncodings;
		} else
			return getListOfEncodingsSupportedByTrafficManager();
	}

	/**
	 * Decode list of encodings from string property and use only them.
	 */
	public synchronized void setFixedListOfSupportedEncodings(
			String supportedEncodingsString) {
		if (supportedEncodingsString != null) {// Convert string with encoding
			// names into array of codes

			// Calculate number of encodings
			int count = 0;
			for (StringTokenizer st = new StringTokenizer(
					supportedEncodingsString, ", "); st.hasMoreTokens(); st
					.nextToken())
				count++;

			// Parse encoding names and fill array
			int[] encodings = new int[count];
			StringTokenizer st = new StringTokenizer(supportedEncodingsString,
					", ");
			for (int i = 0; i < count; i++)
				encodings[i] = PacketManager.getEncodingByName(st.nextToken());
			fixedEncodingsList = encodings;
			logger.info("Using fixed list of supported encodings: "
					+ new SetEncodingsPacket(fixedEncodingsList)
							.dumpToString(true));
		} else {
			fixedEncodingsList = null;
		}
	}

	/**
	 * Send list of supported encodings to server.
	 */
	protected synchronized void sendSupportedEncodings(int[] encodings) {
		if (server == null)
			return;

		if (encodings == null)
			throw new NullPointerException();

		if (encodings.length == 0)
			encodings = new int[RfbConstants.ENCODING_RAW];// Raw encoding
		// always supported

		logger.debug("Sending supported encodings to server.");

		// Create list of supported encodings
		Packet packet = new SetEncodingsPacket(encodings);
		supportedEncodings = encodings;// Remeber encodings in use
		packet.setScreen(server.getScreen());

		// Put packet into server queue
		server.handlePacket(packet);
	}

	/**
	 * Do an action before server interface will send the packet to the server.
	 * 
	 * @return a packet to send or null, to prevent sending.
	 */
	public synchronized Packet filterServerPacketBeforeWrite(
			ServerInterface server, Packet packet) {
		if ((packet.getPacketType() == CLIENT_SET_ENCODINGS || packet
				.getPacketType() == CLIENT_SET_PIXEL_FORMAT)
				&& server.getScreen().isValid())
			if (server.getStatus() != Communicator.STATUS_NORMAL)
				onServerReady(server);

		if (!server.getScreen().canAcceptPacket(packet)) {
			logger.warn("Packet type " + packet + " is unsupported by "
					+ server + ", ignored.");
			return null;
		}

		if (packet.getPacketType() == CLIENT_FRAMEBUFFER_UPDATE_REQUEST)
			packet = onServerFBUR(server, packet);

		return packet;
	}

	/**
	 * Review packet queue after change.
	 */
	public synchronized void serverQueueChanged(ServerInterface server) {
		server.getQueue().squeeze();
	}

	/**
	 * Review packet queue after change.
	 */
	public synchronized void clientQueueChanged(ClientInterface client) {
		client.getQueue().squeeze();
	}

	/**
	 * Review server queue and choose one packet to send at this moment.
	 */
	public synchronized Packet getPrefferedPacketToSendForServer(
			ServerInterface server) {
		return server.getQueue().pop();
	}

	/**
	 * Review client queue and choose one packet to send at this moment.
	 */
	public synchronized Packet getPrefferedPacketToSendForClient(
			ClientInterface client) {
		return client.getQueue().pop();
	}

	/**
	 * Do an action when server send Framebuffer Update packet.
	 */
	protected synchronized Packet onServerFBU(ServerInterface server,
			Packet packet) {
		return packet;
	}

	/**
	 * Do an action when client send Framebuffer Update Request packet.
	 */
	protected synchronized Packet onClientFBUR(ClientInterface client,
			Packet packet) {
		return packet;
	}

	/**
	 * Do an action when client receive Framebuffer Update packet.
	 */
	protected synchronized Packet onClientFBU(ClientInterface client,
			Packet packet) {
		return packet;
	}

	/**
	 * Do an action when server receive Framebuffer Update Request packet.
	 */
	protected synchronized Packet onServerFBUR(ServerInterface server,
			Packet packet) {
		return packet;
	}

	/**
	 * Do an action when server is ready to handle traffic.
	 * 
	 * Server has valid screen (SPF and SE packets sent) and can serve clients
	 * now.
	 */
	protected synchronized void onServerReady(ServerInterface server) {
		server.setStatus(Communicator.STATUS_NORMAL);
	}

	/**
	 * Shutdown this traffic manager.
	 */
	public void shutdown() {
	}

}
