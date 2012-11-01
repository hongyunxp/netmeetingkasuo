package com.ccvnc.tm;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

import com.ccvnc.ClientInterface;
import com.ccvnc.Fifo;
import com.ccvnc.RfbConstants;
import com.ccvnc.Screen;
import com.ccvnc.ServerInterface;
import com.ccvnc.Timer;
import com.ccvnc.packets.Packet;
import com.ccvnc.packets.client.FramebufferUpdateRequestPacket;
import com.ccvnc.packets.server.FramebufferUpdatePacket;
import com.ccvnc.packets.server.rect.FramebufferSizeChange;

/**
 * DumbTrafficManager - minimalistic traffic manager which allows to share one
 * server connection by few clients.
 * 
 * What it should do in case of:
 * <ul>
 * <li>server connected
 * <ul>
 * <li>send list of supported encodings to server (use user list or calculated
 * list);</li>
 * <li>send pixel format to server (use user pixel format or server pixel
 * format);</li>
 * <li>send new desktop size to clients or disconnect client, if it can't change
 * desktop size;</li>
 * <li>send full screen framebuffer update request to server (if at least one
 * client present).</li>
 * </ul>
 * </li>
 * 
 * <li>client connected
 * <ul>
 * <li>wait for SPF and SE packets from client.</li>
 * </ul>
 * 
 * <li>NI FBUR received from client
 * <ul>
 * <li>mark client as awaiting NI FBU;</li>
 * <li>ignore all I FBU from server until NI FBUR will received;</li>
 * <li>store FBUR and send it later, when FBUR from each client will be
 * received.</li>
 * </ul>
 * </li>
 * 
 * <li>I FBUR received from client
 * <ul>
 * <li>mark client as awaiting I FBU (client can accept NI FBU too);</li>
 * <li>store FBUR and send it later, when FBUR from each client will be
 * received.</li>
 * </ul>
 * </li>
 * 
 * <li>SPF received from client
 * <ul>
 * <li>ignore.</li>
 * </ul>
 * </li>
 * 
 * <li>SE received from client
 * <ul>
 * <li>recalculate list of supported encodings and set it to server, if needed.</li>
 * </ul>
 * </li>
 * 
 * <li>NI FBU sent to client
 * <ul>
 * <li>remove mark about awaiting NI FBU from this client.</li>
 * </ul>
 * </li>
 * 
 * <li>I FBU sent to client
 * <ul>
 * <li>remove mark about awaiting I FBU from this client.</li>
 * </ul>
 * </li>
 * 
 * <li>NI FBUR sent to server
 * <ul>
 * <li>mark next FBU packet from server as NI FBU.</li>
 * </ul>
 * </li>
 * 
 * <li>I FBUR sent to server
 * <ul>
 * <li>mark next FBU packet from server as I FBU.</li>
 * </ul>
 * </li>
 * 
 * <li>FBU received from server
 * <ul>
 * <li>mark FBU packet as I FBU or NI FBU.</li>
 * </ul>
 * </li>
 * 
 * <li>need to send SPF to server
 * <ul>
 * <li>always use server pixel format (or pixel format set by user, if it
 * exists).</li>
 * </ul>
 * </li>
 * 
 *</ul>
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class DumbTrafficManager extends AbstractTrafficManager {
	private static Timer timer = Timer.startNewTimer("TrafficManager timer");

	/**
	 * If true, then FBUR to server is sent and no one FBUR's from cliens was
	 * received.
	 */
	protected boolean fburSent = false;

	private int[] listOfSupportedEncodings = SUPPORTED_ENCODINGS_ARRAY;

	private static final int[] prioritizedPackets = new int[] {
			CLIENT_KEYBOARD_EVENT, CLIENT_POINTER_EVENT, CLIENT_CUT_TEXT,
			CLIENT_SET_PIXEL_FORMAT, CLIENT_SET_ENCODINGS };

	private Runnable sendStoredFburTask = new Runnable() {
		public void run() {
			// Send FBUR to server after receiving FBUR from at least one
			// client,
			// but wait one second for requests from other clients.
			fburSent = true;
			logger
					.debug("FBUR NOT received from all clients. Force sending of FBUR to server "
							+ server + ".");
			sendFBUR(false);
		}
	};

	/**
	 * Type of FBUR sent to server: true - NI FBUR, false - I FBUR.
	 */
	private boolean nifbur;

	/**
	 * Stored cursor shape.
	 */
	private Packet cursorShape;

	// Stored server framebuffer size.
	private int storedFramebufferWidth = -1;
	private int storedFramebufferHeight = -1;

	/**
	 * Refuse some events types. (Eg. change encodings or change pixel format).
	 */
	public synchronized Packet filterClientPacketAfterRead(
			ClientInterface client, Packet packet) {
		packet = super.filterClientPacketAfterRead(client, packet);
		if (packet == null)
			return null;

		if (packet.getPacketType() == RfbConstants.CLIENT_SET_PIXEL_FORMAT
				|| packet.getPacketType() == RfbConstants.CLIENT_SET_ENCODINGS) {
			logger.debug("Packet filtered out: " + packet);
			return null;
		}

		return packet;
	}

	/**
	 * Send stored cursor shape.
	 */
	public synchronized ClientInterface filterClient(ClientInterface client,
			Properties props) {
		client = super.filterClient(client, props);
		if (cursorShape != null)
			client.handlePacket(cursorShape);
		return client;
	}

	/**
	 * Store cursor shape update.
	 */
	public Packet filterServerPacketAfterRead(ServerInterface server,
			Packet packet) {
		if (packet.getPacketType() == ENCODING_X_CURSOR
				|| packet.getPacketType() == ENCODING_RICH_CURSOR)
			cursorShape = packet;

		return super.filterServerPacketAfterRead(server, packet);
	}

	/**
	 * If desktop size changed, then send new desktop size to all clients.
	 */
	public ServerInterface filterServer(ServerInterface server, Properties props) {
		server = super.filterServer(server, props);

		if ((storedFramebufferWidth >= 0 && storedFramebufferWidth != server
				.getScreen().getFramebufferWidth())
				|| (storedFramebufferHeight >= 0 && storedFramebufferHeight != server
						.getScreen().getFramebufferHeight())) {
			// Send new framebuffer size to all clients
			framebufferSizeChanged(server.getScreen());
		}

		// Store current framebuffer width and height
		storedFramebufferWidth = server.getScreen().getFramebufferWidth();
		storedFramebufferHeight = server.getScreen().getFramebufferHeight();

		return server;
	}

	/**
	 * Send new framebuffer size to all clients.
	 */
	protected synchronized void framebufferSizeChanged(Screen screen) {
		FramebufferUpdatePacket fbu = new FramebufferUpdatePacket();
		fbu.setScreen(screen);

		FramebufferSizeChange fbsc = new FramebufferSizeChange();
		fbsc.setScreen(screen);
		fbsc.setHeaderParameters(ENCODING_DESKTOP_SIZE, 0, 0, screen
				.getFramebufferWidth(), screen.getFramebufferHeight());
		fbsc.validate();

		fbu.addRect(fbsc);

		logger.debug("Sending new framebuffer size ("
				+ screen.getFramebufferWidth() + "x"
				+ screen.getFramebufferHeight() + ") to clients.");
		session.handleServerPacket(fbu);
	}

	/**
	 * Client sent a Framebuffer Update Request or receive Framebuffer Update.
	 * 
	 * @param client
	 *            a ClientInterface
	 * @param status
	 *            true if client sent FBUR, false if client receive FBU
	 * @param ni
	 *            true, if clients wants (or still wants, if status==false)
	 *            NonIcremental FBU
	 * 
	 */
	protected synchronized void clientWaitsForFBU(ClientInterface client,
			boolean status, boolean ni) {
		Hashtable props = (Hashtable) clients.get(client);
		props.put("waitingForFBU", Boolean.valueOf(status));

		props.put("waitingForNIFBU", Boolean.valueOf(ni));

		logger.debug("Client " + client + " is "
				+ ((status) ? "waits" : "not waits") + " for "
				+ ((ni) ? "NI " : "") + "FBU.");
	}

	/**
	 * Client receive a Framebuffer Update packet.
	 */
	protected synchronized void clientFBUSent(ClientInterface client,
			FramebufferUpdatePacket packet) {
		clientWaitsForFBU(client, false, !packet.isNonIncremental()
				&& isClientWaitsForNIFBU(client)/*
												 * If packet is I FBU, but
												 * client waits NI FBU, then
												 * still wait NI FBU
												 */);

		logger.debug("Client " + client + " received "
				+ ((packet.isNonIncremental()) ? "NI " : "") + "FBU.");

		if (!isClientWaitsForNIFBU(client))// If client not still waits NI FBU,
			// then remove stored FBUR
			((Hashtable) clients.get(client)).remove("FBUR");
		else
			logger.debug("Client " + client + " still waits NI FBU.");

	}

	/**
	 * Client sent a Framebuffer Update Request packet.
	 * 
	 * Store client FBUR for later processing.
	 */
	protected synchronized void clientFBURReceived(ClientInterface client,
			FramebufferUpdateRequestPacket packet) {
		if (clients == null)
			return;

		clientWaitsForFBU(client, true, packet.getIncremental() == 0);

		// Store FBUR
		Hashtable props = ((Hashtable) clients.get(client));
		if (props.get("FBUR") == null)
			props.put("FBUR", packet);
		else
			// Client still waits for NI FBU (previous received FBU was I FBU).
			props.put("FBUR", ((FramebufferUpdateRequestPacket) props
					.get("FBUR")).squeeze(packet));
	}

	/**
	 * Get stored Framebuffer Update Request.
	 */
	private synchronized FramebufferUpdateRequestPacket getClientFbur(
			ClientInterface client) {
		return (FramebufferUpdateRequestPacket) ((Hashtable) clients
				.get(client)).get("FBUR");
	}

	/**
	 * Return true if client waits Framebuffer Update.
	 * 
	 * @return true if client waits Framebuffer Update, false otherwise.
	 */
	protected synchronized boolean isClientWaitsForFBU(ClientInterface client) {
		if (clients == null)
			return false;

		Hashtable props = (Hashtable) clients.get(client);
		return props.get("waitingForFBU") == Boolean.TRUE;
	}

	/**
	 * Return true if client waits NonIcremental Framebuffer Update.
	 * 
	 * @return true if client waits NonIcremental Framebuffer Update, false
	 *         otherwise.
	 */
	protected synchronized boolean isClientWaitsForNIFBU(ClientInterface client) {
		if (clients == null)
			return false;

		Hashtable props = (Hashtable) clients.get(client);
		return props.get("waitingForNIFBU") == Boolean.TRUE;
	}

	/**
	 * Server sent a Framebuffer Update or receive Framebuffer Update Request.
	 * 
	 * @param status
	 *            true if server sent FBU, false if server receive FBUR
	 */
	protected synchronized void serverWaitsForFBUR(boolean status) {
		if (serverProperties != null)
			serverProperties.put("waitingForFBUR", Boolean.valueOf(status));
		logger.debug("Server " + server + " is "
				+ ((status) ? "waits" : "not waits") + " for FBUR.");
	}

	/**
	 * Return true if server waits Framebuffer Update Request.
	 * 
	 * @return true if server waits Framebuffer Update Request, false otherwise.
	 */
	protected synchronized boolean isServerWaitsForFBUR() {
		if (serverProperties == null)
			return false;
		return serverProperties.get("waitingForFBUR") == Boolean.TRUE;
	}

	/**
	 * Framebuffer Update Request sent to server.
	 * 
	 * Remember the FBUR incremental flag.
	 */
	protected synchronized void serverFBURSent(
			FramebufferUpdateRequestPacket packet) {
		serverWaitsForFBUR(false);
		// Mark next FBU packet from server as NI FBU if this packet is NI FBUR.
		nifbur = (packet.getIncremental() == 0);
		logger.debug(((nifbur) ? "NI " : "") + "FBUR sent to server "
				+ server + ".");
	}

	/**
	 * Framebuffer Update received from server.
	 * 
	 * Mark FBU packet as I FBU or NI FBU using stored incrmental flag from
	 * previous FBUR.
	 */
	protected synchronized void serverFBUReceived(FramebufferUpdatePacket packet) {
		serverWaitsForFBUR(true);
		// Mark this FBU packet from server as NI FBU if previous FBUR packet
		// was NI FBUR.
		packet.setNonIncremental(nifbur);
		nifbur = false;// Clear NI flag
		logger.debug(((nifbur) ? "NI " : "")
				+ "FBU received from server " + server + ".");
	}

	/**
	 * If FBU received from server.
	 * 
	 * <ul>
	 * <li>mark FBU packet as I FBU or NI FBU.</li>
	 * </ul>
	 */
	protected synchronized Packet onServerFBU(ServerInterface server,
			Packet packet) {
		packet = super.onServerFBU(server, packet);
		serverFBUReceived((FramebufferUpdatePacket) packet);
		return packet;
	}

	/**
	 * If NI FBUR received from client.
	 * 
	 * <ul>
	 * <li>mark client as awaiting NI FBU;</li>
	 * <li>ignore all I FBU from server until NI FBUR will received;</li>
	 * <li>store FBUR and send it later, when FBUR from each client will be
	 * received.</li>
	 * </ul>
	 * 
	 * If I FBUR received from client
	 * <ul>
	 * <li>mark client as awaiting I FBU (client can accept NI FBU too);</li>
	 * <li>store FBUR and send it later, when FBUR from each client will be
	 * received.</li>
	 * </ul>
	 */
	protected synchronized Packet onClientFBUR(ClientInterface client,
			Packet packet) {
		packet = super.onClientFBUR(client, packet);
		clientFBURReceived(client, (FramebufferUpdateRequestPacket) packet);

		resendFBUR();

		return null;
	}

	/**
	 * Send FBUR to server, if necessary.
	 */
	protected synchronized void resendFBUR() {
		// Calculate number of clients awaiting FBU
		int fburCount = getNumberOfClientAwaitingFBU();

		if (fburCount < clients.size()) {
			logger.debug("Filter out FBUR #" + fburCount
					+ " (awaiting FBUR from " + (clients.size() - fburCount)
					+ " clients)");

			// send FBUR after receiving FBUR from at least one client,
			// but wait up to one second for request from other clients.
			if (fburSent) {
				fburSent = false;
				timer.addTask(sendStoredFburTask);
			}
		} else if (!fburSent)
			;
		{// If we receive FBUR's from all clients and FBUR still not send, then
			// send it now
			logger
					.debug("FBUR received from all clients. Sending FBUR to server "
							+ server + ".");
			sendFBUR(false);
		}
	}

	/**
	 * Calculate number of clients awaiting FBU.
	 */
	private synchronized int getNumberOfClientAwaitingFBU() {
		int fburCount = 0;
		synchronized (clients) {
			for (Enumeration e = clients.keys(); e.hasMoreElements();) {
				ClientInterface client = (ClientInterface) e.nextElement();
				if (isClientWaitsForFBU(client)
						&& !client.getQueue().isContainsPacket(
								SERVER_FRAMEBUFFER_UPDATE))
					fburCount++;
			}
		}
		return fburCount;
	}

	/**
	 * Client receive Framebuffer Update packet.
	 */
	protected synchronized Packet onClientFBU(ClientInterface client,
			Packet packet) {
		packet = super.onClientFBU(client, packet);
		clientFBUSent(client, (FramebufferUpdatePacket) packet);
		return packet;
	}

	/**
	 * Server receive Framebuffer Update packet.
	 */
	protected synchronized Packet onServerFBUR(ServerInterface server,
			Packet packet) {
		packet = super.onServerFBUR(server, packet);
		serverFBURSent((FramebufferUpdateRequestPacket) packet);
		return packet;
	}

	/**
	 * Don't send Framebuffer Update if client do not wait it.
	 */
	public synchronized Packet getPrefferedPacketToSendForClient(
			ClientInterface client) {
		Screen screen = client.getScreen();
		Fifo queue = client.getQueue();

		// Don't send anything until we wil have a valid screen description
		if (!screen.isValid())
			return null;

		// Don't send FBU's, if we already sent FBU and awaiting for FBUR
		if (!isClientWaitsForFBU(client)
				&& queue.getTopPacket().getPacketType() == SERVER_FRAMEBUFFER_UPDATE) {
			logger.debug("Waiting for FBUR from " + client);
			return null;
		}

		return queue.pop();
	}

	/**
	 * Don't send Framebuffer Update Request if server do not wait it.
	 * 
	 * Send another packet from list of prioritized packets instead.
	 */
	public synchronized Packet getPrefferedPacketToSendForServer(
			ServerInterface server) {
		Screen screen = server.getScreen();
		Fifo queue = server.getQueue();

		Packet packet;

		if (!screen.isValid()) {
			// Send only SPF or SE packets until we will have a valid screen
			// description
			packet = queue.pop(new int[] { CLIENT_SET_PIXEL_FORMAT,
					CLIENT_SET_ENCODINGS }, true);
			logger.debug("Server screen is not valid, sending "
					+ packet);
		} else if (!isServerWaitsForFBUR()
				&& queue.getTopPacket().getPacketType() == CLIENT_FRAMEBUFFER_UPDATE_REQUEST) {
			// Don't send FBUR's, if we already sent FBUR and awaiting for FBU,
			// send another prioritized packet
			packet = queue.pop(prioritizedPackets, true);
			logger.debug("Waiting for FBU, sending " + packet);
		} else {
			packet = queue.pop();
			logger.debug("Sending " + packet);
		}
		return packet;
	}

	/**
	 * Server ready and waits for Framebuffer Update Request.
	 */
	protected synchronized void onServerReady(ServerInterface server) {
		super.onServerReady(server);
		serverWaitsForFBUR(true);

		if (clients.size() > 0)// We have clients, we need to update their
			// screens
			sendFBUR(true);
	}

	/**
	 * Send a new FBUR directly to the server.
	 * 
	 * Use calculated FBUR or create new NonIncremental FBUR.
	 * 
	 * @param forceFSNIFBUR
	 *            force Full Screen NonIncremental Framebuffer Update Request
	 */
	protected synchronized void sendFBUR(boolean forceFSNIFBUR) {
		timer.removeTask(sendStoredFburTask);

		if (server != null) {
			fburSent = true;

			FramebufferUpdateRequestPacket packet = null;

			if (!forceFSNIFBUR)
				packet = calculateFBUR();

			if (packet == null) {// No clients waiting for FBUR but we need to
				// send FBUR. :-/
				if (!forceFSNIFBUR)
					logger
							.warn("No one client awaiting FBUR, but need to send FBUR.");

				// Create NonIncremental FBUR
				Screen screen = server.getScreen();
				packet = new FramebufferUpdateRequestPacket(0, 0, 0, screen
						.getFramebufferWidth(), screen.getFramebufferHeight());
				packet.setScreen(screen);
			}

			logger.debug("Sending FBUR: " + packet.dumpToString(true));
			server.handlePacket(packet);

		} else
			logger.debug("Can't send FBUR: server is not ready.");
	}

	/**
	 * Calculate a common FBUR to cover FBUR's from all clients.
	 * 
	 * @return a FramebufferUpdateRequestPacket or null, if nobody waits FBU
	 * 
	 */
	private synchronized FramebufferUpdateRequestPacket calculateFBUR() {
		FramebufferUpdateRequestPacket packet = null;
		for (Enumeration e = clients.keys(); e.hasMoreElements();) {
			FramebufferUpdateRequestPacket clientFbur = getClientFbur((ClientInterface) e
					.nextElement());
			if (clientFbur == null)
				continue;

			if (packet == null)
				packet = clientFbur;
			else
				packet = (FramebufferUpdateRequestPacket) packet
						.squeeze(clientFbur);
		}
		return packet;
	}

	/**
	 * Reset server status veriables to default values.
	 */
	public synchronized void serverOut(ServerInterface server) {
		fburSent = false;
		serverWaitsForFBUR(false);
		timer.removeTask(sendStoredFburTask);

		super.serverOut(server);
	}

	/**
	 * Send FBUR to server if necessary.
	 */
	public synchronized void clientOut(ClientInterface client) {
		super.clientOut(client);

		resendFBUR();
	}

	/**
	 * Get list of encodings allowed to use with this traffic manager.
	 * 
	 * @return list of supported encodings
	 * 
	 */
	protected synchronized int[] getListOfEncodingsSupportedByTrafficManager() {
		return listOfSupportedEncodings;
	}

	/**
	 * Shutdown this traffic manager.
	 * 
	 * Stop timer.
	 */
	public void shutdown() {
		timer.shutdown();
	}

}
