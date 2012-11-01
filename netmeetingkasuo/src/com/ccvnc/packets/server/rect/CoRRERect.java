package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import com.ccvnc.Screen;

/**
 * CoRRE (Compact RRE) - variant of RRE.
 * 
 * DEPRECATED, use Hextile instead.
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class CoRRERect extends AbstractRectangle {
	private byte[] backgroundColorBuf;
	private Vector rectangles;

	public void setRectangles(Vector rectangles) {
		this.rectangles = rectangles;
	}

	public Enumeration rectangles() {
		return rectangles.elements();
	}

	public int getNumberOfSubrects() {
		return rectangles.size();
	}

	public CoRRESubrect getSubrectangle(int i) {
		return (CoRRESubrect) rectangles.elementAt(i);
	}

	public void setBackgroundColorBuf(byte[] backgroundColorBuf) {
		this.backgroundColorBuf = backgroundColorBuf;
	}

	public byte[] getBackgroundColorBuf() {
		return backgroundColorBuf;
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		os.writeInt(rectangles.size());
		os.write(backgroundColorBuf);

		for (Enumeration e = rectangles(); e.hasMoreElements();)
			((CoRRESubrect) e.nextElement()).writeRectangleData(os);
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		int numberOfSubrects = is.readInt();
		rectangles = new Vector(numberOfSubrects);

		// Backround color
		backgroundColorBuf = new byte[screen.getBytesPerPixel()];
		is.readFully(backgroundColorBuf);

		// Rectangtles
		for (int i = 0; i < numberOfSubrects; i++) {
			CoRRESubrect subrect = new CoRRESubrect();
			subrect.setScreen(screen);
			subrect.readRectangleData(is);
			subrect.validate();
			rectangles.add(subrect);
		}
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("CoRRERect " + x + "," + y + "-" + width + "x" + height
				+ ", numberOfSubrects=" + rectangles.size());
		if (verbose) {
			out.println("++++++++++++++++++++++++++");
			for (Enumeration e = rectangles(); e.hasMoreElements();)
				((CoRRESubrect) e.nextElement()).dump(out, verbose);
			out.println("--------------------------");
		}
	}

	public void validate() {
		super.validate();

		if (backgroundColorBuf == null
				|| backgroundColorBuf.length != screen.getBytesPerPixel()
				|| rectangles == null)
			throw new RuntimeException("CoRRE rectangle is not valid: "
					+ dumpToString(true));

		for (Enumeration e = rectangles(); e.hasMoreElements();)
			((Rectangle) e.nextElement()).validate();
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		CoRRERect rect2 = new CoRRERect();
		rect2.setScreen(screen2);
		rect2.setHeaderParameters(this);

		rect2.backgroundColorBuf = convertRawPixelData(backgroundColorBuf,
				screen2);

		rect2.rectangles = new Vector(rectangles.size());
		for (Enumeration e = rectangles(); e.hasMoreElements();)
			rect2.rectangles.add(((CoRRESubrect) e.nextElement())
					.convertPixelFormat(screen2));

		return rect2;
	}

}
