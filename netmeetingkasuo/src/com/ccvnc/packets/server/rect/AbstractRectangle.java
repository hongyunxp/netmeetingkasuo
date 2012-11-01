package com.ccvnc.packets.server.rect;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.PacketManager;
import com.ccvnc.RfbConstants;
import com.ccvnc.Screen;

/**
 * Contains common code for all rectangles.
 * 
 * @author Volodymyr M. Lisivka
 */
public abstract class AbstractRectangle implements Rectangle, RfbConstants {
	protected int encodingType, x, y, width, height;

	/**
	 * Screen asociated with this rectangle.
	 * 
	 * Data in this rectangle use pixel format of this screen.
	 */
	protected Screen screen;

	/**
	 * Sets EncodingType
	 * 
	 * @param encodingType
	 *            rectangle encoding type
	 */
	public void setEncodingType(int encodingType) {
		this.encodingType = encodingType;
	}

	/**
	 * Returns EncodingType
	 * 
	 * @return rectangle encoding type
	 */
	public int getEncodingType() {
		return encodingType;
	}

	/**
	 * Sets X
	 * 
	 * @param x
	 *            an int
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Returns X
	 * 
	 * @return an int
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets Y
	 * 
	 * @param y
	 *            an int
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Returns Y
	 * 
	 * @return an int
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets Width
	 * 
	 * @param width
	 *            an int
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns Width
	 * 
	 * @return an int
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets Height
	 * 
	 * @param height
	 *            an int
	 */
	public void setHeight(int height) {
		this.height = height;
	}

	/**
	 * Returns Height
	 * 
	 * @return an int
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Set rectangle header parameters.
	 * 
	 * @param encodingType
	 *            encoding type of this rectangle
	 * @param x
	 *            coordinate of left side
	 * @param y
	 *            coordinate of top side
	 * @param width
	 *            width of the rectangle
	 * @param height
	 *            height of the rectangle
	 */
	public void setHeaderParameters(int encodingType, int x, int y, int width,
			int height) {
		setEncodingType(encodingType);
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	/**
	 * Write rectangle header to stream.
	 */
	public void writeHeaderParameters(DataOutputStream os) throws IOException {
		os.writeShort(x);
		os.writeShort(y);
		os.writeShort(width);
		os.writeShort(height);
		os.writeInt(encodingType);
	}

	/**
	 * Write rectangle data (with header) to stream.
	 */
	public void write(DataOutputStream os) throws IOException {
		writeHeaderParameters(os);
		writeRectangleData(os);
	}

	/**
	 * Write rectangle data (without header) to stream.
	 */
	public abstract void writeRectangleData(DataOutputStream os)
			throws IOException;

	public void setScreen(Screen screen) {
		if (screen == null)
			throw new NullPointerException();

		this.screen = screen;
	}

	public Screen getScreen() {
		return screen;
	}

	/**
	 * Set rectangle header parameters using another rectangle as source.
	 * 
	 * @param rect
	 *            an AbstractRectangle
	 * 
	 */
	public void setHeaderParameters(AbstractRectangle rect) {
		setHeaderParameters(rect.encodingType, rect.x, rect.y, rect.width,
				rect.height);
	}

	/**
	 * Read rectangle data (without rectangle header) from stream.
	 * 
	 * @param encodingType
	 *            encoding type of this rectangle
	 * @param xpos
	 *            coordinate of left side
	 * @param ypos
	 *            coordinate of top side
	 * @param width
	 *            width of the rectangle
	 * @param height
	 *            height of the rectangle
	 * @param is
	 *            a DataInputStream
	 */
	public void read(int encodingType, int xpos, int ypos, int width,
			int height, DataInputStream is) throws IOException {
		setHeaderParameters(encodingType, xpos, ypos, width, height);
		readRectangleData(is);
	}

	/**
	 * Read rectangle data (without rectangle header) from stream.
	 */
	public abstract void readRectangleData(DataInputStream is)
			throws IOException;;

	/**
	 * Convert buffer with true color pixels to another depth.
	 * 
	 * @param buf
	 *            buffer
	 * @param screen2
	 *            a screen to convert to
	 * 
	 * @return new buffer with converted pixels
	 */
	public byte[] convertRawPixelData(byte[] buf, Screen screen2) {
		if (screen.getTrueColorFlag() == 0 || screen2.getTrueColorFlag() == 0)
			throw new RuntimeException(
					"Conversion to/from screen with palette is not supported now. TODO: use 8bit per pixel format and plain palette to emulate.");
		else
			return convertRawPixelData(buf, screen2, screen.getBytesPerPixel(),
					screen2.getBytesPerPixel());
	}

	/**
	 * Convert buffer with true color pixels to another depth.
	 * 
	 * @param buf
	 *            buffer
	 * @param screen2
	 *            a screen to convert to
	 * @param bpp
	 *            number of bytes per pixel on source screen
	 * @param bpp2
	 *            number of bytes per pixel on target screen
	 * 
	 * @return new buffer with converted pixels
	 */
	public byte[] convertRawPixelData(byte[] buf, Screen screen2, int bpp,
			int bpp2) {
		// Calculate number of pixels
		int numberOfPixels = buf.length / bpp;

		if (numberOfPixels * bpp != buf.length)
			throw new RuntimeException("Incorrect buffer size.");

		// Create buffer
		byte[] buf2 = new byte[numberOfPixels * bpp2];

		int bigEndianFlag = screen.getBigEndianFlag();
		int bigEndianFlag2 = screen2.getBigEndianFlag();

		// Source mask - using Max value as mask (may be a wrong way?)
		int srm = screen.getRedMax();
		int sgm = screen.getGreenMax();
		int sbm = screen.getBlueMax();

		// Number of bits for each colour
		int srb = screen.getRedBits();
		int sgb = screen.getGreenBits();
		int sbb = screen.getBlueBits();

		int srb2 = screen2.getRedBits();
		int sgb2 = screen2.getGreenBits();
		int sbb2 = screen2.getBlueBits();

		// Shifts of the colour parts
		int redShift = screen.getRedShift();
		int greenShift = screen.getGreenShift();
		int blueShift = screen.getBlueShift();

		int redShift2 = screen2.getRedShift();
		int greenShift2 = screen2.getGreenShift();
		int blueShift2 = screen2.getBlueShift();

		// For each pixel - get pixel, convert pixel, put pixel
		for (int i = 0; i < numberOfPixels; i++) {
			// Get pixel from byte array
			int pixel = getPixel(bpp, bigEndianFlag, buf, i);

			// Extract R,G and B parts from pixel value
			int r = (pixel >>> redShift) & srm;
			int g = (pixel >>> greenShift) & sgm;
			int b = (pixel >>> blueShift) & sbm;

			{// Convert depth
				if (srb > srb2)
					r >>>= (srb - srb2);
				else if (srb < srb2)
					r <<= (srb2 - srb);

				if (sgb > sgb2)
					g >>>= (sgb - sgb2);
				else if (sgb < sgb2)
					g <<= (sgb2 - sgb);

				if (sbb > sbb2)
					b >>>= (sbb - sbb2);
				else if (sbb < sbb2)
					b <<= (sbb2 - sbb);
			}

			// Compose parts into pixel value
			int pixel2 = (r << redShift2) | (g << greenShift2)
					| (b << blueShift2);

			// Put pixel into byte array
			putPixel(bpp2, bigEndianFlag2, buf2, i, pixel2);
		}

		return buf2;
	}

	/**
	 * Put pixel into byte array with raw data.
	 * 
	 * @param bpp
	 *            bytes per pixel
	 * @param bigEndianFlag
	 *            big endian (!=0) or little endian (==0) format
	 * @param buf
	 *            byte array with pixels
	 * @param index
	 *            index of the pixel in the array
	 * @param pix
	 *            pixel
	 * 
	 */
	public static void putPixel(int bpp, int bigEndianFlag, byte[] buf,
			int index, int pix) {
		switch (bpp) {
		case 1:
			buf[index] = (byte) (pix);
			break;

		case 2:
			if (bigEndianFlag == 0) {
				buf[index * 2] = (byte) (pix);
				buf[index * 2 + 1] = (byte) (pix >>> 8);
			} else {
				buf[index * 2 + 1] = (byte) (pix);
				buf[index * 2] = (byte) (pix >>> 8);
			}
			break;

		case 3:
			if (bigEndianFlag == 0) {
				buf[index * 3] = (byte) (pix);
				buf[index * 3 + 1] = (byte) (pix >>> 8);
				buf[index * 3 + 2] = (byte) (pix >>> 16);
			} else {
				buf[index * 3 + 2] = (byte) (pix);
				buf[index * 3 + 1] = (byte) (pix >>> 8);
				buf[index * 3] = (byte) (pix >>> 16);
			}
			break;

		case 4:
			if (bigEndianFlag == 0) {
				buf[index * 4] = (byte) (pix);
				buf[index * 4 + 1] = (byte) (pix >>> 8);
				buf[index * 4 + 2] = (byte) (pix >>> 16);
				buf[index * 4 + 3] = (byte) (pix >>> 24);
			} else {
				buf[index * 4 + 3] = (byte) (pix);
				buf[index * 4 + 2] = (byte) (pix >>> 8);
				buf[index * 4 + 1] = (byte) (pix >>> 16);
				buf[index * 4] = (byte) (pix >>> 24);
			}
			break;

		default:
			throw new RuntimeException("Unsupported bytes per pixle value");
		}
	}

	/**
	 * Get pixel from byte array with raw data.
	 * 
	 * @param bpp
	 *            bytes per pixel
	 * @param bigEndianFlag
	 *            big endian (!=0) or little endian (==0) format
	 * @param buf
	 *            byte array with pixels
	 * @param index
	 *            index of the pixel in the array
	 * 
	 * @return pixel value
	 * 
	 */
	public static int getPixel(int bpp, int bigEndianFlag, byte[] buf, int index) {
		switch (bpp) {
		case 1:
			return buf[index];

		case 2:
			if (bigEndianFlag == 0)
				return (buf[index * 2] & 0xff)
						| ((buf[index * 2 + 1] & 0xff) << 8);
			else
				return (buf[index * 2 + 1] & 0xff)
						| ((buf[index * 2] & 0xff) << 8);

		case 3:
			if (bigEndianFlag == 0)
				return (buf[index * 3] & 0xff)
						| ((buf[index * 3 + 1] & 0xff) << 8)
						| ((buf[index * 3 + 2] & 0xff) << 16);
			else
				return (buf[index * 3 + 2] & 0xff)
						| ((buf[index * 3 + 1] & 0xff) << 8)
						| ((buf[index * 3] & 0xff) << 16);

		case 4:
			if (bigEndianFlag == 0)
				return (buf[index * 4] & 0xff)
						| ((buf[index * 4 + 1] & 0xff) << 8)
						| ((buf[index * 4 + 2] & 0xff) << 16)
						| ((buf[index * 4 + 3] & 0xff) << 24);
			else
				return (buf[index * 4 + 3] & 0xff)
						| ((buf[index * 4 + 2] & 0xff) << 8)
						| ((buf[index * 4 + 1] & 0xff) << 16)
						| ((buf[index * 4] & 0xff) << 24);

		default:
			throw new RuntimeException("Unsupported bytes per pixel value");
		}
	}

	/**
	 * Validate this rectangle.
	 * 
	 * Check the rectangle size and screen presence.
	 * 
	 */
	public void validate() {
		if (screen == null || x < 0 || y < 0 || width <= 0 || height <= 0
				|| (x + width) > screen.getFramebufferWidth()
				|| (y + height) > screen.getFramebufferHeight())
			throw new RuntimeException("Rectangle is not valid.\n"
					+ dumpToString(true));
	}

	/**
	 * Dump content of this rectangle to output,
	 * 
	 * Print encoding type and rectangle coordinates and size.
	 */
	public void dump(PrintStream out, boolean verbose) {
		out.println("encodingType="
				+ PacketManager.getEncodingName(encodingType) + ", x=" + x
				+ ", y=" + y + ", width=" + width + ", height=" + height);
	}

	/**
	 * Dump content of this rectangle to string.
	 * 
	 * @return a dump
	 * 
	 */
	public String dumpToString(boolean verbose) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bos);
		dump(out, verbose);
		out.flush();
		return new String(bos.toByteArray());
	}

}
