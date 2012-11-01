package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Vector;

import com.ccvnc.Screen;

/**
 * HextileRect
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class HextileRect extends AbstractRectangle {

	private Vector rectangles;

	public void setRectangles(Vector rectangles) {
		this.rectangles = rectangles;
	}

	public Enumeration rectangles() {
		return rectangles.elements();
	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("HextileRect, numberOfRectangles=" + rectangles.size() + " ");
		super.dump(out, verbose);
		if (verbose) {
			out
					.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			for (Enumeration e = rectangles(); e.hasMoreElements();)
				((Rectangle) e.nextElement()).dump(out, verbose);
			out
					.println("-------------------------------------------------------");
		}
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		for (Enumeration e = rectangles(); e.hasMoreElements();) {
			AbstractRectangle rect = (AbstractRectangle) e.nextElement();
			os.writeByte(rect.getEncodingType());
			rect.writeRectangleData(os);
		}
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		int tilesInRow = (width + 15) / 16;
		int tilesInColumn = (height + 15) / 16;
		rectangles = new Vector(tilesInColumn * tilesInRow);

		for (int subrectY = y; subrectY < y + height; subrectY += 16) {
			int subrectHeight = (y + height - subrectY < 16) ? y + height
					- subrectY : 16;
			for (int subrectX = x; subrectX < x + width; subrectX += 16) {
				int subrectWidth = (x + width - subrectX < 16) ? x + width
						- subrectX : 16;
				int encoding = is.readUnsignedByte();

				AbstractRectangle rect;
				if ((encoding & SUBENCODING_HEXTILE_RAW) != 0)
					rect = new RawRect();
				else
					rect = new HextileTile();

				rect.setScreen(screen);
				rect.setHeaderParameters(encoding, subrectX, subrectY,
						subrectWidth, subrectHeight);
				rect.readRectangleData(is);
				rect.validate();
				rectangles.add(rect);
			}
		}
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		HextileRect rect2 = new HextileRect();
		rect2.setScreen(screen2);
		rect2.setHeaderParameters(this);
		Vector rectangles2 = null;
		if (rectangles != null) {
			rectangles2 = new Vector(rectangles.size());
			for (Enumeration e = rectangles(); e.hasMoreElements();)
				rectangles2.add(((Rectangle) e.nextElement())
						.convertPixelFormat(screen2));
		}
		rect2.rectangles = rectangles2;

		return rect2;
	}

	public void validate() {
		super.validate();
		if (rectangles == null
				|| rectangles.size() != (((width + 15) / 16) * ((height + 15) / 16))) {
			throw new RuntimeException("HextileRect is not valid: "
					+ dumpToString(false));
		}

		for (Enumeration e = rectangles(); e.hasMoreElements();)
			((Rectangle) e.nextElement()).validate();
	}

}
