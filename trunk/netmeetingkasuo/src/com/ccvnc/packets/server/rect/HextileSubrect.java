package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;

/**
 * HextileSubrect
 * 
 * @author Volodymyr M. Lisivka
 */
public class HextileSubrect extends AbstractRectangle {
	private byte[] foregroundColourBuf;
	private boolean coloured = false;

	public void setEncodingType(int encodingType) {
		super.setEncodingType(encodingType);
		coloured = ((encodingType & SUBENCODING_HEXTILE_SUBRECTS_COLOURED) != 0);
	}

	public void setForegroundColourBuf(byte[] foregroundColourBuf) {
		this.foregroundColourBuf = foregroundColourBuf;
	}

	public byte[] getForegroundColourBuf() {
		return foregroundColourBuf;
	}

	public void setColoured(boolean coloured) {
		this.coloured = coloured;
	}

	public boolean isColoured() {
		return coloured;
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		if (coloured)
			os.write(foregroundColourBuf);

		int xy = ((x & 0xf) << 4) | (y & 0xf);
		int widthHeight = (((width - 1) & 0xf) << 4) | ((height - 1) & 0xf);

		os.writeByte(xy);
		os.writeByte(widthHeight);

	}

	public void readRectangleData(DataInputStream is) throws IOException {
		coloured = ((encodingType & SUBENCODING_HEXTILE_SUBRECTS_COLOURED) != 0);
		if (coloured) {
			foregroundColourBuf = new byte[screen.getBytesPerPixel()];
			is.readFully(foregroundColourBuf);
		}

		int xy = is.readUnsignedByte();
		int widthHeight = is.readUnsignedByte();

		x = (xy >>> 4) & 0xf;
		y = (xy) & 0xf;

		width = ((widthHeight >>> 4) & 0xf) + 1;
		height = ((widthHeight) & 0xf) + 1;
	}

	public void read(int encodingType, int xpos, int ypos, int width,
			int height, DataInputStream is) throws IOException {
		throw new RuntimeException(
				"Not implemented. User readRectangleData() instead.");
	}

	public void write(DataOutputStream os) throws IOException {
		throw new RuntimeException(
				"Not implemented. User writeRectangleData() instead.");
	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("HextileSubrect ");
		super.dump(out, verbose);
		if (coloured)
			out.println("foregroundColourBuf="
					+ RfbIOTools.arrayToString(foregroundColourBuf));
	}

	public void validate() {
		super.validate();
		if (x > 15
				|| y > 15
				|| width > 16
				|| height > 16
				|| (coloured && (foregroundColourBuf == null || foregroundColourBuf.length != screen
						.getBytesPerPixel())))
			throw new RuntimeException("HextileSubrect is not valid: "
					+ dumpToString(true));
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		if (!coloured)
			return this;
		else {
			HextileSubrect subrect2 = new HextileSubrect();
			subrect2.setScreen(screen2);
			subrect2.setHeaderParameters(this);

			subrect2.coloured = coloured;

			if (foregroundColourBuf != null)
				subrect2.foregroundColourBuf = convertRawPixelData(
						foregroundColourBuf, screen2);
			else
				subrect2.foregroundColourBuf = null;

			return subrect2;
		}
	}

}
