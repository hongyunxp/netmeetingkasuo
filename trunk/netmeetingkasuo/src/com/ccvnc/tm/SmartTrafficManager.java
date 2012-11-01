package com.ccvnc.tm;

import java.util.Enumeration;

import com.ccvnc.ClientInterface;
import com.ccvnc.Screen;
import com.ccvnc.packets.client.SetPixelFormatPacket;

/**
 * SmartTrafficManager - extended gredy traffic manager, it goal is to minimize
 * traffic usage using smart alhorithm.
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
 * <li>recalculate best pixel format and set it to server, if needed;</li>
 * <li>if SPF was send, then drop stored NI FBU and send new NI FBUR to update
 * screen.</li>
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
 * <li>drop older rectangles in stored NI FBU, drop stored NI FBU if it size is
 * too big.</li>
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
@SuppressWarnings("unchecked")
public class SmartTrafficManager extends GreedyTrafficManager {

	/**
	 * Use pixel format used by majority of clients.
	 */
	protected SetPixelFormatPacket calculateBestPixelFormat() {
		// Use user pixel format, if it set.
		if (fixedPixelFormat != null)
			return fixedPixelFormat;

		// Calucalate best pixel format
		SetPixelFormatPacket packet = new SetPixelFormatPacket(32, 24, 0, 1,
				255, 255, 255, 16, 8, 0);

		if (clients.size() == 0 && server != null)
			// No clients, use server settings
			packet = new SetPixelFormatPacket(server.getScreen());
		else if (clients.size() == 1) {// One client, use client settings
			Screen screen = ((ClientInterface) clients.keys().nextElement())
					.getScreen();
			packet = new SetPixelFormatPacket(screen);
		} else if (clients.size() > 1) {// More than one client, calculate the
										// best BPP

			// For each client get the client BPP and increase counter for that
			// value
			int[] bpps = new int[4];// 1,2,3,4 bytes per pixel
			for (Enumeration e = clients.keys(); e.hasMoreElements();) {
				Screen clientScreen = ((ClientInterface) e.nextElement())
						.getScreen();
				bpps[clientScreen.getBytesPerPixel() - 1]++;
			}

			// Choose the best BPP
			int maxClients = Math.max(bpps[0], Math.max(bpps[1], Math.max(
					bpps[2], bpps[3])));
			int bpp = bpps[0];
			for (int i = 0; i < 4; i++)
				if (bpps[i] == maxClients) {
					bpp = i;
					break;
				}

			// Select a random client with chosen bpp
			for (Enumeration e = clients.keys(); e.hasMoreElements();) {
				Screen screen = ((ClientInterface) e.nextElement()).getScreen();
				if (screen.getBytesPerPixel() == bpp) {
					packet = new SetPixelFormatPacket(screen);
					break;
				}
			}

			// And use it screen settings
		} else {// Server is not available, no clients. :-/ Just return any SPF.
		}

		return packet;
	}
}
