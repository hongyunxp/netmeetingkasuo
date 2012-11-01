package com.ccvnc.tm;

/**
 * PlayerTrafficManager
 *
 * @author Volodymyr M. Lisivka
 */
import com.ccvnc.ServerInterface;
import com.ccvnc.packets.client.SetPixelFormatPacket;

public class PlayerTrafficManager extends SmartTrafficManager {

	/**
	 * Server ready and waits for Framebuffer Update Request.
	 */
	protected void onServerReady(ServerInterface server) {
		super.onServerReady(server);

		// DumbTrafficManager sents NI FBUR only when at least one client
		// connected
		// We need to send NI FBUR always
		if (!fburSent)
			sendFBUR(true);
	}

	/**
	 * Always use server pixel format because player can't change it.
	 */
	protected SetPixelFormatPacket calculateBestPixelFormat() {
		SetPixelFormatPacket packet;

		if (server == null || server.getScreen() == null)
			// Server is not available, just return any SPF
			packet = new SetPixelFormatPacket(32, 24, 0, 1, 255, 255, 255, 16,
					8, 0);
		else
			// ALways use server pixel format
			packet = new SetPixelFormatPacket(server.getScreen());

		return packet;
	}

}
