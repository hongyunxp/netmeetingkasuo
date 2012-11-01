package com.ccvnc.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.packets.Packet;

/**
 * FramebufferUpdateRequestPacket
 * 
 * @author Volodymyr M. Lisivka
 */
public class FramebufferUpdateRequestPacket extends ClientPacket {

	private int incremental = -1, xpos = -1, ypos = -1, width = -1,
			height = -1;

	public FramebufferUpdateRequestPacket() {
	}

	public FramebufferUpdateRequestPacket(int incremental, int xpos, int ypos,
			int width, int height) {
		this.incremental = incremental;
		this.xpos = xpos;
		this.ypos = ypos;
		this.width = width;
		this.height = height;
	}

	/**
	 * Replace two FBUR packets with new one to reduce traffic.
	 * 
	 * Must never return null because DumbTrafficManager relies on that.
	 */
	public Packet squeeze(Packet packet) {
		FramebufferUpdateRequestPacket packet2 = (FramebufferUpdateRequestPacket) packet;

		if (incremental > packet2.incremental)// Always replace incremental
												// packet with non-incremental
			return packet2;
		else if (incremental < packet2.incremental)// Always replace incremental
													// packet with
													// non-incremental
			return this;
		else // Same type (NI or I)
		if (xpos <= packet2.xpos && ypos <= packet2.xpos
				&& width >= packet2.xpos && height >= packet2.xpos)
			return packet;
		else if (xpos >= packet2.xpos && ypos >= packet2.xpos
				&& width <= packet2.xpos && height <= packet2.xpos)
			return packet2;
		else
			return new FramebufferUpdateRequestPacket(Math.min(incremental,
					packet2.incremental), Math.min(xpos, packet2.xpos), Math
					.min(ypos, packet2.ypos), Math.max(width, packet2.width),
					Math.max(height, packet2.height));
	}

	/**
	 * Sets Incremental
	 * 
	 * @param incremental
	 *            an int
	 */
	public void setIncremental(int incremental) {
		this.incremental = incremental;
	}

	/**
	 * Returns Incremental
	 * 
	 * @return an int
	 */
	public int getIncremental() {
		return incremental;
	}

	/**
	 * Sets Xpos
	 * 
	 * @param xpos
	 *            an int
	 */
	public void setXpos(int xpos) {
		this.xpos = xpos;
	}

	/**
	 * Returns Xpos
	 * 
	 * @return an int
	 */
	public int getXpos() {
		return xpos;
	}

	/**
	 * Sets Ypos
	 * 
	 * @param ypos
	 *            an int
	 */
	public void setYpos(int ypos) {
		this.ypos = ypos;
	}

	/**
	 * Returns Ypos
	 * 
	 * @return an int
	 */
	public int getYpos() {
		return ypos;
	}

	/**
	 * Sets Width
	 * 
	 * @param width
	 *            an int
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns Width
	 * 
	 * @return an int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets Height
	 * 
	 * @param height
	 *            an int
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Returns Height
	 * 
	 * @return an int
	 */
	public int getHeight() {
		return height;
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("FramebufferUpdateRequestPacket, incremental="
				+ incremental + ",xpos=" + xpos + ",ypos=" + ypos + ",width="
				+ width + ",height=" + height);
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeByte(incremental);
		os.writeShort(xpos);
		os.writeShort(ypos);
		os.writeShort(width);
		os.writeShort(height);
	}

	public void readPacketData(DataInputStream is) throws IOException {
		incremental = is.readByte();
		xpos = is.readUnsignedShort();
		ypos = is.readUnsignedShort();
		width = is.readUnsignedShort();
		height = is.readUnsignedShort();
	}

	public int getPacketType() {
		return CLIENT_FRAMEBUFFER_UPDATE_REQUEST;
	}

	public void validate() {
		super.validate();
		if (incremental < 0 || xpos < 0 || ypos < 0 || width < 0 || height < 0
				|| xpos + width > screen.getFramebufferWidth()
				|| ypos + height > screen.getFramebufferHeight())
			throw new RuntimeException(
					"FramebufferUpdateRequestPacket is not valid.");
	}

}
