package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;

/**
 * HextileSubrect
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class HextileTile extends AbstractRectangle {

	private Vector rectangles;
	private byte[] backgroundColourBuf;
	private byte[] foregroundColourBuf;

	private boolean backgroundSpecified = false;
	private boolean foregroundSpecified = false;
	private boolean anySubrects = false;
	private boolean subrectsColoured = false;

	public void setSubrectsColoured(boolean subrectsColoured) {
		this.subrectsColoured = subrectsColoured;
	}

	public boolean isSubrectsColoured() {
		return subrectsColoured;
	}

	public void setAnySubrects(boolean anySubrects) {
		this.anySubrects = anySubrects;
	}

	public boolean isAnySubrects() {
		return anySubrects;
	}

	public void setBackgroundSpecified(boolean backgroundSpecified) {
		this.backgroundSpecified = backgroundSpecified;
	}

	public boolean isBackgroundSpecified() {
		return backgroundSpecified;
	}

	public void setForegroundSpecified(boolean foregroundSpecified) {
		this.foregroundSpecified = foregroundSpecified;
	}

	public boolean isForegroundSpecified() {
		return foregroundSpecified;
	}

	public void setRectangles(Vector rectangles) {
		this.rectangles = rectangles;
	}

	public Enumeration rectangles() {
		return rectangles.elements();
	}

	public void setBackgroundColourBuf(byte[] backgroundColourBuf) {
		this.backgroundColourBuf = backgroundColourBuf;
	}

	public byte[] getBackgroundColourBuf() {
		return backgroundColourBuf;
	}

	public void setForegroundColourBuf(byte[] foregroundColourBuf) {
		this.foregroundColourBuf = foregroundColourBuf;
	}

	public byte[] getForegroundColourBuf() {
		return foregroundColourBuf;
	}

	public void setEncodingType(int encodingType) {
		backgroundSpecified = (encodingType & SUBENCODING_HEXTILE_BACKGROUND_SPECIFIED) != 0;
		foregroundSpecified = (encodingType & SUBENCODING_HEXTILE_FOREGROUND_SPECIFIED) != 0;
		anySubrects = (encodingType & SUBENCODING_HEXTILE_ANY_SUBRECTS) != 0;
		subrectsColoured = (encodingType & SUBENCODING_HEXTILE_SUBRECTS_COLOURED) != 0;

		// TightVNC generates wrong Hextile rectangles
		// if(foregroundSpecified && subrectsColoured)
		// throw new
		// RuntimeException("Wrong settings in the encoding byte: foregroundSpecified and subrectsColoured must not be set both at one time (see VNC spec).");

		super.setEncodingType(encodingType);
	}

	public void write(DataOutputStream os) throws IOException {
		throw new RuntimeException(
				"Not implemented. Use writeRectangleData() instead.");
	}

	public void read(int encodingType, int xpos, int ypos, int width,
			int height, DataInputStream is) throws IOException {
		throw new RuntimeException(
				"Not implemented. Use readRectangleData() instead.");
	}

	public void validate() {
		super.validate();
		if (width > 16
				|| height > 16
				|| (backgroundSpecified && (backgroundColourBuf == null || backgroundColourBuf.length != screen
						.getBytesPerPixel()))
				|| (foregroundSpecified && (foregroundColourBuf == null || foregroundColourBuf.length != screen
						.getBytesPerPixel())))
			throw new RuntimeException("HextileTile is not valid: "
					+ dumpToString(true));

		if (anySubrects)
			for (Enumeration e = rectangles(); e.hasMoreElements();)
				((Rectangle) e.nextElement()).validate();
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		// Write the background colour, if specified.
		if (backgroundSpecified)
			os.write(backgroundColourBuf);

		// Write the foreground colour, if specified.
		if (foregroundSpecified)
			os.write(foregroundColourBuf);

		// Stop processing, if there no any subrectangles in this hextile tile
		if (!anySubrects)
			return;

		os.writeByte(rectangles.size());
		for (Enumeration e = rectangles(); e.hasMoreElements();)
			((HextileSubrect) e.nextElement()).writeRectangleData(os);
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		// Read the background colour, if specified.
		if (backgroundSpecified) {
			backgroundColourBuf = new byte[screen.getBytesPerPixel()];
			is.readFully(backgroundColourBuf);
		}

		// Read the foreground colour, if specified.
		if (foregroundSpecified) {
			foregroundColourBuf = new byte[screen.getBytesPerPixel()];
			is.readFully(foregroundColourBuf);
		}

		// Stop processing, if there no any subrectangles in this hextile tile
		if (!anySubrects)
			return;

		int numberOfSubrects = is.readUnsignedByte();
		rectangles = new Vector(numberOfSubrects);
		for (int i = 0; i < numberOfSubrects; i++) {
			HextileSubrect hextileSubrect = new HextileSubrect();
			hextileSubrect.setScreen(screen);
			hextileSubrect.setHeaderParameters(encodingType, -1, -1, -1, -1);
			hextileSubrect.readRectangleData(is);
			hextileSubrect.validate();
			rectangles.add(hextileSubrect);
		}

	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("HextileTile ");
		super.dump(out, verbose);
		if (backgroundSpecified)
			out.println("backgroundColourBuf="
					+ RfbIOTools.arrayToString(backgroundColourBuf));
		if (foregroundSpecified)
			out.println("foregroundColourBuf="
					+ RfbIOTools.arrayToString(foregroundColourBuf));
		if (anySubrects) {
			out.println("Number of subrectangles: " + rectangles.size());
			if (verbose) {
				out.println("++++++++++++++++++++++++++++++++++++++++");
				for (Enumeration e = rectangles(); e.hasMoreElements();)
					((HextileSubrect) e.nextElement()).dump(out, verbose);
				out.println("----------------------------------------");
			}
		}
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		HextileTile tile2 = new HextileTile();
		tile2.setScreen(screen2);
		tile2.setHeaderParameters(this);

		tile2.backgroundSpecified = backgroundSpecified;
		tile2.foregroundSpecified = foregroundSpecified;
		tile2.anySubrects = anySubrects;
		if (backgroundColourBuf != null)
			tile2.backgroundColourBuf = convertRawPixelData(
					backgroundColourBuf, screen2);
		else
			tile2.backgroundColourBuf = null;
		if (foregroundColourBuf != null)
			tile2.foregroundColourBuf = convertRawPixelData(
					foregroundColourBuf, screen2);
		else
			tile2.foregroundColourBuf = null;

		Vector rectangles2 = null;
		if (rectangles != null) {
			rectangles2 = new Vector(rectangles.size());
			for (Enumeration e = rectangles(); e.hasMoreElements();)
				rectangles2.add(((Rectangle) e.nextElement())
						.convertPixelFormat(screen2));
		}
		tile2.rectangles = rectangles2;

		return tile2;
	}

}
