package com.ccvnc.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import com.ccvnc.PacketManager;
import com.ccvnc.RfbConstants;
import com.ccvnc.Screen;
import com.ccvnc.packets.Packet;
import com.ccvnc.packets.server.rect.Rectangle;

/**
 * FramebufferUpdatePacket
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class FramebufferUpdatePacket extends ServerPacket {
	private Vector rectangles = new Vector();

	/**
	 * NonIncremental packet.
	 * 
	 * If true, then packet received in response of the NonIncremental
	 * FramebufferRequest and must contain update for whole framebuffer.
	 */
	private boolean nonIncremental = false;

	public FramebufferUpdatePacket(Screen screen) {
		this.screen = screen;
	}

	public FramebufferUpdatePacket() {
	}

	public void setNonIncremental(boolean nonIncremental) {
		this.nonIncremental = nonIncremental;
	}

	public boolean isNonIncremental() {
		return nonIncremental;
	}

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet) {
		if (screen.getTrueColorFlag() != 0 && screen == packet.getScreen()) {
			// Join two framebuffer updates
			FramebufferUpdatePacket packet3 = new FramebufferUpdatePacket(
					screen);

			// Add rectangles from this packet
			for (Enumeration e = rectangles(); e.hasMoreElements();)
				packet3.addRect((Rectangle) e.nextElement());

			// Add rectangles from next packet
			FramebufferUpdatePacket packet2 = (FramebufferUpdatePacket) packet;
			for (Enumeration e = packet2.rectangles(); e.hasMoreElements();)
				packet3.addRect((Rectangle) e.nextElement());

			return packet3;
		} else
			return null;// Don't change order of FBU packets when palette in use
	}

	public void addRect(Rectangle rect) {
		rect.validate();
		rectangles.add(rect);
	}

	public Enumeration rectangles() {
		return rectangles.elements();
	}

	public int getNumberOfRectangles() {
		return rectangles.size();
	}

	public Rectangle getRectangle(int i) {
		return (Rectangle) rectangles.elementAt(i);
	}

	public int getPacketType() {
		return RfbConstants.SERVER_FRAMEBUFFER_UPDATE;
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeByte(0);// padding

		os.writeShort(rectangles.size());
		for (Enumeration e = rectangles(); e.hasMoreElements();)
			((Rectangle) e.nextElement()).write(os);
	}

	public void readPacketData(DataInputStream is) throws IOException {
		is.skipBytes(1);// Skip padding

		int numberOfRectangles = is.readUnsignedShort();

		for (int i = 0; i < numberOfRectangles; i++) {
			int xpos = is.readUnsignedShort();
			int ypos = is.readUnsignedShort();
			int width = is.readUnsignedShort();
			int height = is.readUnsignedShort();
			int encodingType = is.readInt();

			Rectangle rect;

			// TightVNC extension
			if (encodingType == ENCODING_LAST_RECT)
				break;

			// if (false) {// Handle Framebuffer size change as in TigtVNC
			// applet
			// if (encodingType == ENCODING_DESKTOP_SIZE) {
			// rect = new FramebufferSizeChange();
			// rect.setScreen(screen);
			// rect.validate();
			// addRect(rect);
			//
			// break;
			// }
			// }

			rect = PacketManager.getRectangeHandler(encodingType);
			rect.setScreen(screen);
			rect.read(encodingType, xpos, ypos, width, height, is);
			rect.validate();
			addRect(rect);
		}
	}

	public void validate() {
		super.validate();
		// if(rectangles.size()==0)
		// throw new
		// RuntimeException("FramebufferUpdate packet is not valid - no rectangles.");

		for (Enumeration e = rectangles(); e.hasMoreElements();)
			((Rectangle) e.nextElement()).validate();
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("FramebufferUpdate, number of rectangles="
				+ rectangles.size());

		out.println("+++++++++++++++++++++++++++++++++++++++FBU");
		for (Enumeration e = rectangles.elements(); e.hasMoreElements();)
			((Rectangle) e.nextElement()).dump(out, verbose);
		out.println("---------------------------------------FBU");
	}

}
