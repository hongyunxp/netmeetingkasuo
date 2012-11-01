package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;

/**
 * RawRect (Raw rectangle) encoding.
 * 
 * Just raw array of pixels. Array size is
 * <code>width*height*bytesPerPixel</code>.
 * 
 * @author Volodymyr M. Lisivka
 */
public class RawRect extends AbstractRectangle {
	/* LOG */@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(RawRect.class);

	private byte[] buf;

	public void validate() {
		super.validate();
		if (buf == null
				|| buf.length != width * height * screen.getBytesPerPixel()) {
			throw new RuntimeException("Raw rectangle is not valid: "
					+ dumpToString(false));
		}
	}

	/**
	 * Sets Buf
	 * 
	 * @param buf
	 *            a byte[]
	 */
	public void setBuf(byte[] buf) {
		if (buf == null)
			throw new NullPointerException();
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
		os.write(buf);
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		// *LOG*/logger.debug("RawRect "+x+","+y+"-"+width+"x"+height+"@"+(screen.getBytesPerPixel()*8));
		buf = new byte[width * height * screen.getBytesPerPixel()];
		is.readFully(buf);
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		RawRect rect2 = new RawRect();
		rect2.setScreen(screen2);
		rect2.setHeaderParameters(this);

		// Converting pixels
		byte buf2[] = convertRawPixelData(buf, screen2);
		rect2.setBuf(buf2);

		return rect2;
	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("Raw Rect bufLength=" + buf.length + " ");
		super.dump(out, verbose);
		if (verbose) {
			out.println("=====================");
			out.print(RfbIOTools.arrayToString(buf));
			out.println("=====================");
		}
	}

}
