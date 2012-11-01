package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.Screen;

/**
 * CopyRect (Copy rectangle) encoding.
 * 
 * Contains coordinates in framebuffer (srcX,srcY) from which client must copy
 * rectangle of pixel data.
 * 
 * @author Volodymyr M. Lisivka
 */
public class CopyRect extends AbstractRectangle {
	private int srcX;
	private int srcY;

	/**
	 * Sets SrcX
	 * 
	 * @param srcX
	 *            an int
	 */
	public void setSrcX(int srcX) {
		this.srcX = srcX;
	}

	/**
	 * Returns SrcX
	 * 
	 * @return an int
	 */
	public int getSrcX() {
		return srcX;
	}

	/**
	 * Sets SrcY
	 * 
	 * @param srcY
	 *            an int
	 */
	public void setSrcY(int srcY) {
		this.srcY = srcY;
	}

	/**
	 * Returns SrcY
	 * 
	 * @return an int
	 */
	public int getSrcY() {
		return srcY;
	}

	/**
	 * Nothing to convert.
	 */
	public Rectangle convertPixelFormat(Screen screen2) {
		return this;
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		srcX = is.readUnsignedShort();
		srcY = is.readUnsignedShort();
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		os.writeShort(srcX);
		os.writeShort(srcY);
	}

	public void validate() {
		super.validate();
		if (srcX < 0 || srcY < 0 || srcX + width > screen.getFramebufferWidth()
				|| srcY + height > screen.getFramebufferHeight())
			throw new RuntimeException("CopyRect is not valid: "
					+ dumpToString(true));
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("CopyRect from " + srcX + "," + srcY + " to " + x + "," + y
				+ "-" + width + "x" + height);
	}
}
