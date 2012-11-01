package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;

/**
 * RRESubrect - subrectangle of the RRE encoded rectangle.
 * 
 * @author Volodymyr M. Lisivka
 */
public class RRESubrect extends AbstractRectangle {
	private byte[] pixelBuf;

	public void setPixelBuf(byte[] pixelBuf) {
		this.pixelBuf = pixelBuf;
	}

	public byte[] getPixelBuf() {
		return pixelBuf;
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		os.write(pixelBuf);

		os.writeShort(x);
		os.writeShort(y);
		os.writeShort(width);
		os.writeShort(height);
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		pixelBuf = new byte[screen.getBytesPerPixel()];
		is.readFully(pixelBuf);

		x = is.readUnsignedShort();
		y = is.readUnsignedShort();
		width = is.readUnsignedShort();
		height = is.readUnsignedShort();
	}

	public void validate() {
		if (screen == null || pixelBuf == null
				|| pixelBuf.length != screen.getBytesPerPixel() || x < 0
				|| y < 0 || x + width > screen.getFramebufferWidth()
				|| y + height > screen.getFramebufferHeight() || x > 65535
				|| y > 65535)
			throw new RuntimeException("RRESubrect is not valid: "
					+ dumpToString(false));
	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("RRESubrect x=" + x + ",y=" + y + ",width=" + width
				+ ",height=" + height + ", pixel="
				+ RfbIOTools.arrayToString(pixelBuf));
	}

	public void read(int encodingType, int xpos, int ypos, int width,
			int height, DataInputStream is) {
		throw new RuntimeException(
				"Not implemented, use readRectangleData() instead.");
	}

	public void write(DataOutputStream os) {
		throw new RuntimeException(
				"Not implemented, use writeRectangleData() instead.");
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		RRESubrect rect2 = new RRESubrect();
		rect2.setScreen(screen2);
		rect2.setHeaderParameters(this);

		rect2.pixelBuf = convertRawPixelData(pixelBuf, screen2);
		return rect2;
	}

}
