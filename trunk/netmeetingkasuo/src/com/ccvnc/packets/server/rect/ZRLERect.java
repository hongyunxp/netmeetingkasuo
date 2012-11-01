package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;

/**
 * ZRLERect
 * 
 * @author Volodymyr M. Lisivka
 */
public class ZRLERect extends AbstractRectangle {
	private byte[] buf;

	/**
	 * Sets Buf
	 * 
	 * @param buf
	 *            a byte[]
	 */
	public void setBuf(byte[] buf) {
		this.buf = buf;
	}

	/**
	 * Returns Buf
	 * 
	 * @return a byte[]
	 */
	public byte[] getBuf() {
		return buf;
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		os.writeInt(buf.length);
		os.write(buf);
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		int length = is.readInt();
		buf = new byte[length];
		is.readFully(buf);
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		// Impossible?
		throw new RuntimeException(
				"Can't convert pixel format for data in ZLIB stream.");
	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("ZRLERect, chunk length=" + buf.length + " ");
		super.dump(out, verbose);
		if (verbose)
			RfbIOTools.arrayToString(buf);
	}

}
