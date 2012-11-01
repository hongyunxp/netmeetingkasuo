package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;

import com.ccvnc.Screen;

/**
 * SoftMouseMove - cursor moved on the server side.
 * 
 * @author Volodymyr M. Lisivka
 */
public class SoftMouseMove extends AbstractRectangle {

	public void writeRectangleData(DataOutputStream os) {
	}

	public void readRectangleData(DataInputStream is) {
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		return this;
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("SoftMouseMove, new mouse xpos=" + x + ", ypos=" + y);
	}

	public void validate() {
		if (screen == null || x < 0 || y < 0
				|| x > screen.getFramebufferWidth()
				|| y > screen.getFramebufferHeight())
			throw new RuntimeException("SoftMouseMove is not valid: "
					+ dumpToString(true));
	}

}
