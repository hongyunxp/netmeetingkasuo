package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.Screen;

/**
 * Subrectangle of the CoRRE rectangle.
 * 
 * @author Volodymyr M. Lisivka
 */
public class CoRRESubrect extends AbstractRectangle {

	protected byte[] pixelBuf;

	public void setPixelBuf(byte[] pixelBuf) {
		this.pixelBuf = pixelBuf;
	}

	public byte[] getPixelBuf() {
		return pixelBuf;
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		os.write(pixelBuf);

		os.writeByte(x);
		os.writeByte(y);
		os.writeByte(width);
		os.writeByte(height);
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		pixelBuf = new byte[screen.getBytesPerPixel()];
		is.readFully(pixelBuf);

		x = is.readUnsignedByte();
		y = is.readUnsignedByte();
		width = is.readUnsignedByte();
		height = is.readUnsignedByte();
	}

	public void validate() {
		if (screen == null || pixelBuf == null
				|| pixelBuf.length != screen.getBytesPerPixel() || x < 0
				|| y < 0 || x + width > screen.getFramebufferWidth()
				|| y + height > screen.getFramebufferHeight() || x > 255
				|| y > 255)
			throw new RuntimeException("CoRRESubrect is not valid: "
					+ dumpToString(true));
	}

	public void dump(PrintStream out, boolean versbose) {
		out.println("CoRRESubrect x=" + x + ",y=" + y + ",width=" + width
				+ ",height=" + height);
	}

	public void read(int encodingType, int xpos, int ypos, int width,
			int height, DataInputStream is) throws IOException {
		throw new RuntimeException(
				"Not implemented, use readRectangleData() instead.");
	}

	public void write(DataOutputStream os) throws IOException {
		throw new RuntimeException(
				"Not implemented, use writeRectangleData() instead.");
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		CoRRESubrect rect2 = new CoRRESubrect();
		rect2.setScreen(screen2);
		rect2.setHeaderParameters(this);

		rect2.pixelBuf = convertRawPixelData(pixelBuf, screen2);
		return rect2;
	}

}
