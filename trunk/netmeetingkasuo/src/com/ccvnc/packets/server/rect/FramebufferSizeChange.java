package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.Screen;

/**
 * Framebuffer size changed.
 * 
 * Width and height of the rectangle represents new width and height of the
 * framebuffer.
 * 
 * @author Volodymyr M. Lisivka
 */
public class FramebufferSizeChange extends AbstractRectangle {

	public void dump(PrintStream out, boolean verbose) {
		out.println("FramebufferSizeChange, new width=" + width + ", height="
				+ height);
	}

	/**
	 * Change screen size.
	 */
	public void writeRectangleData(DataOutputStream os) throws IOException {
		screen.setFramebufferSize(width, height);
	}

	/**
	 * Change screen size.
	 */
	public void readRectangleData(DataInputStream is) throws IOException {
		screen.setFramebufferSize(width, height);
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		FramebufferSizeChange rect = new FramebufferSizeChange();
		rect.setHeaderParameters(this);
		rect.setScreen(screen2);

		return rect;
	}

	/**
	 * Check the rectangle size and screen presence.
	 */
	public void validate() {
		if (screen == null || width <= 0 || height <= 0)
			throw new RuntimeException("FramebufferSizeChange is not valid.");
	}

	public int getPacketType() {
		return ENCODING_DESKTOP_SIZE;
	}

}
