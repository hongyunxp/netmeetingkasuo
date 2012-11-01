package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;

/**
 * XCursorShapeUpdate
 * 
 * @author Volodymyr M. Lisivka
 */
public class XCursorShapeUpdate extends AbstractRectangle {
	private byte[] maskData;

	public void setMaskData(byte[] buf) {
		this.maskData = buf;
	}

	public byte[] getMaskData() {
		return maskData;
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		os.write(maskData);
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		int bytesPerRow = (width + 7) / 8;
		int bytesMaskData = bytesPerRow * height;

		if (width * height == 0)
			return;

		// Read cursor shape update data
		int length = 6 + bytesMaskData * 2;

		maskData = new byte[length];

		is.readFully(maskData);
	}

	/**
	 * Nothing to convert.
	 */
	public Rectangle convertPixelFormat(Screen screen2) {
		return this;
	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("XCursorShapeUpdate ");
		super.dump(out, verbose);
		if (verbose)
			out.print("maskData: " + RfbIOTools.arrayToString(maskData));
	}

	public void validate() {
		if (screen == null
				|| x < 0
				|| y < 0
				|| width < 0
				|| height < 0
				|| (width * height != 0 && (maskData == null || maskData.length != 6
						+ ((width + 7) / 8) * height * 2)))
			throw new RuntimeException("XCursorShapeUpdate is not valid: "
					+ dumpToString(false));
	}

}
