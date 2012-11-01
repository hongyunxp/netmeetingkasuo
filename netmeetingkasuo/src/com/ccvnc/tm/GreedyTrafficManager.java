package com.ccvnc.tm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.ccvnc.ClientInterface;
import com.ccvnc.ServerInterface;
import com.ccvnc.packets.Packet;
import com.ccvnc.packets.client.FramebufferUpdateRequestPacket;
import com.ccvnc.packets.client.SetPixelFormatPacket;
import com.ccvnc.packets.server.FramebufferUpdatePacket;

/**
 * GreedyTrafficManager - extended dumb traffic manager, it goal is to minimize
 * traffic usage.
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
 * <li><b>if</b> stored NI FBU is present, then sent it to this client only,
 * <b>or</b></li>
 * <li>store this FBUR and send it later, when FBUR from each client will be
 * received;</li>
 * <li>ignore all I FBU from server until NI FBUR will received.</li>
 * </ul>
 * </li>
 * 
 * <li>I FBUR received from client
 * <ul>
 * <li>mark client as awaiting I FBU (client can accept NI FBU too);</li>
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
 * <li>mark FBU packet as I FBU or NI FBU;</li>
 * <li>store NI FBU;</li>
 * <li>append I FBU to NI FBU, if stored NI FBU exists;</li>
 * <li>drop stored NI FBU, if it size is too big.</li>
 * </ul>
 * </li>
 * 
 * <li>need to send SPF to server
 * <ul>
 * <li>always use 8 bit depth (or pixel format set by user, if it exists).</li>
 * </ul>
 * </li>
 * 
 *</ul>
 * 
 * @author Volodymyr M. Lisivka
 */
public class GreedyTrafficManager extends DumbTrafficManager {

	protected FramebufferUpdatePacket storedNIFBU;
	protected int sizeOfStoredNIFBU = -1, sizeOfUpdatesToStoredNIFBU = -1;

	/**
	 * Client sent a Framebuffer Update Request packet.
	 * 
	 * If stored NI FBU exists, then send it to client and change FBUR packet
	 * type to Incremental, to prevent sending NI FBUR to server.
	 */
	protected void clientFBURReceived(ClientInterface client,
			FramebufferUpdateRequestPacket packet) {
		super.clientFBURReceived(client, packet);

		if (packet.getIncremental() == 0 && storedNIFBU != null)// NonIncremental
																// FBUR packet
																// received and
																// we already
																// have stored
																// FBU
		{// Return stored FBU to this client only

			// Change packet type to Incremental to prevent sending NI FBUR to
			// server
			packet.setIncremental(1);
			// This will cause a delay for other clients (up to one second per
			// FBU),
			// because client will not ready until it receive whole NIFBU
			client.handlePacket(storedNIFBU);
		}
	}

	/**
	 * Framebuffer Update received from server.
	 * 
	 * If packet is sent in response to NI FBUR, then store it and its size. If
	 * packet is sent in response to I FBUR, then compare size of the all
	 * updates with size of the NI FBUR multipled by number of the clients. If
	 * size of the then append it to stored NI FBUR.
	 */
	protected void serverFBUReceived(FramebufferUpdatePacket packet) {
		super.serverFBUReceived(packet);
		if (packet.isNonIncremental()) {
			replaceStoredNIFBU(packet);
		} else {
			if (storedNIFBU != null) {
				// Calculate size of the NI FBU
				sizeOfUpdatesToStoredNIFBU += calculatePacketSize(packet);

				// If we lose NI FBUR, then we will need to transfer N+1 NI FBU
				// packets, where N is number of active clients.
				if (sizeOfUpdatesToStoredNIFBU > sizeOfStoredNIFBU
						* (clients.size() + 1)) {// Size of the NIFBU too big,
													// drop it
					/* LOG */logger
							.debug("Size of updates ("
									+ sizeOfUpdatesToStoredNIFBU
									+ ") is bigger than size of the NI FBU multipled by number of the clients ("
									+ sizeOfStoredNIFBU + "*"
									+ (clients.size() + 1) + "="
									+ sizeOfStoredNIFBU * (clients.size() + 1)
									+ ").");
					dropStoredNIFBU();
				} else
					// Append update to NI FBU
					storedNIFBU = (FramebufferUpdatePacket) storedNIFBU
							.squeeze(packet);
			}
		}
	}

	/**
	 * Replace old stored NI FBU packet with new one.
	 * 
	 * @param packet
	 *            a NonIncreental FramebufferUpdatePacket
	 * 
	 */
	protected void replaceStoredNIFBU(FramebufferUpdatePacket packet) {
		storedNIFBU = packet;
		// Calculate size of the NI FBU
		sizeOfUpdatesToStoredNIFBU = 0;
		sizeOfStoredNIFBU = calculatePacketSize(packet);
		/* LOG */logger.debug("Replacing old NI FBU, packet size: "
				+ sizeOfStoredNIFBU + ".");
	}

	/**
	 * Drop stored Non Incremental Framebuffer Update.
	 */
	protected void dropStoredNIFBU() {
		/* LOG */logger.debug("Dropping old NI FBU (" + sizeOfStoredNIFBU + "*"
				+ (clients.size() + 1) + "=" + sizeOfStoredNIFBU
				* (clients.size() + 1) + " ?? " + sizeOfUpdatesToStoredNIFBU
				+ ").");
		storedNIFBU = null;
		// Calculate size of the NI FBU
		sizeOfUpdatesToStoredNIFBU = -1;
		sizeOfStoredNIFBU = -1;
	}

	/**
	 * Drop stored NI FBU if necessary.
	 */
	public void clientOut(ClientInterface client) {
		super.clientOut(client);

		if (sizeOfUpdatesToStoredNIFBU > sizeOfStoredNIFBU
				* (clients.size() + 1)) {// Size of the NIFBU too big, drop it
			dropStoredNIFBU();
		}
	}

	/**
	 * Just write packet to ByteArrayOutputStream and return it size.
	 * 
	 * @param packet
	 *            a FramebufferUpdatePacket
	 * 
	 * @return size of the packet (in bytes)
	 * 
	 */
	private int calculatePacketSize(FramebufferUpdatePacket packet) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(bos);
			packet.write(dataOutputStream);
			dataOutputStream.flush();
			return bos.size();
		} catch (IOException e) {
			return 0;
		}
	}

	/**
	 * Reset server status veriables to default values.
	 */
	public void serverOut(ServerInterface server) {
		super.serverOut(server);
		dropStoredNIFBU();
	}

	/**
	 * Don't send NonIncremental Framebuffer Update Request if we have stored
	 * NIFBU.
	 */
	public Packet getPrefferedPacketToSendForServer(ServerInterface server) {
		Packet packet = super.getPrefferedPacketToSendForServer(server);

		if (packet != null
				&& packet.getPacketType() == CLIENT_FRAMEBUFFER_UPDATE_REQUEST
				&& storedNIFBU != null)
			// Set Incremental flag
			((FramebufferUpdateRequestPacket) packet).setIncremental(1);

		return packet;
	}

	/**
	 * Always use 8 bit depth.
	 * 
	 */
	protected SetPixelFormatPacket calculateBestPixelFormat() {
		// Use user pixel format, if it set.
		if (fixedPixelFormat != null)
			return fixedPixelFormat;

		// Always use BGR233 to be compatible with TightVNC applet
		SetPixelFormatPacket packet = new SetPixelFormatPacket(8, 8, 0, 1, 7,
				7, 3, 0, 3, 6);
		return packet;
	}

}
