package com.ccvnc.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.ccvnc.Screen;
import com.ccvnc.packets.Packet;

/**
 * SetPixelFormatPacket - client change it pixel format for new Framebuffer
 * Update's.
 * 
 * Format:
 * <table>
 * <tr>
 * <th>Bytes</th>
 * <th>Type</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>Packet type (0)</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>U8</td>
 * <td>padding</td>
 * </tr>
 * <tr>
 * <td>16</td>
 * <td></td>
 * <td>pixel format</td>
 * </tr>
 * </table>
 *<br />
 * Pixel format:
 * <table>
 * <tr>
 * <th>Bytes</th>
 * <th>Type</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>bitsPerPixel</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>depth</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>bigEndianFlag</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>trueColourFlag</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U16</td>
 * <td>redMax</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U16</td>
 * <td>grenMax</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U16</td>
 * <td>blueMax</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>redShift</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>greenShift</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>blueShift</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>U8</td>
 * <td>padding</td>
 * </tr>
 * </table>
 * 
 * @author Volodymyr M. Lisivka
 */
public class SetPixelFormatPacket extends ClientPacket {

	/* LOG */private static Logger logger = Logger
			.getLogger(SetPixelFormatPacket.class);

	private int bitsPerPixel, depth, bigEndianFlag, trueColourFlag, redMax,
			greenMax, blueMax, redShift, greenShift, blueShift;

	public SetPixelFormatPacket() {
	}

	public SetPixelFormatPacket(int bitsPerPixel, int depth, int bigEndianFlag,
			int trueColorFlag, int redMax, int greenMax, int blueMax,
			int redShift, int greenShift, int blueShift) {
		this.bitsPerPixel = bitsPerPixel;
		this.depth = depth;
		this.bigEndianFlag = bigEndianFlag;
		this.trueColourFlag = trueColorFlag;
		this.redMax = redMax;
		this.greenMax = greenMax;
		this.blueMax = blueMax;
		this.redShift = redShift;
		this.greenShift = greenShift;
		this.blueShift = blueShift;
	}

	public SetPixelFormatPacket(Screen screen) {
		this.bitsPerPixel = screen.getBitsPerPixel();
		this.depth = screen.getDepth();
		this.bigEndianFlag = screen.getBigEndianFlag();
		this.trueColourFlag = screen.getTrueColorFlag();
		this.redMax = screen.getRedMax();
		this.greenMax = screen.getGreenMax();
		this.blueMax = screen.getBlueMax();
		this.redShift = screen.getRedShift();
		this.greenShift = screen.getGreenShift();
		this.blueShift = screen.getBlueShift();
		this.screen = screen;
	}

	public boolean equals(SetPixelFormatPacket spf) {
		return bitsPerPixel == spf.bitsPerPixel && depth == spf.depth
				&& bigEndianFlag == spf.bigEndianFlag
				&& trueColourFlag == spf.trueColourFlag && redMax == spf.redMax
				&& greenMax == spf.greenMax && blueMax == spf.blueMax
				&& redShift == spf.redShift && greenShift == spf.greenShift
				&& blueShift == spf.blueShift;
	}

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet2) {
		return packet2;// Client waits for data in newest format
	}

	public void setBitsPerPixel(int bitsPerPixel) {
		this.bitsPerPixel = bitsPerPixel;
	}

	public int getBitsPerPixel() {
		return bitsPerPixel;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public int getDepth() {
		return depth;
	}

	public void setBigEndianFlag(int bigEndianFlag) {
		this.bigEndianFlag = bigEndianFlag;
	}

	public int getBigEndianFlag() {
		return bigEndianFlag;
	}

	public void setTrueColourFlag(int trueColorFlag) {
		this.trueColourFlag = trueColorFlag;
	}

	public int getTrueColourFlag() {
		return trueColourFlag;
	}

	public void setRedMax(int redMax) {
		this.redMax = redMax;
	}

	public int getRedMax() {
		return redMax;
	}

	public void setGreenMax(int greenMax) {
		this.greenMax = greenMax;
	}

	public int getGreenMax() {
		return greenMax;
	}

	public void setBlueMax(int blueMax) {
		this.blueMax = blueMax;
	}

	public int getBlueMax() {
		return blueMax;
	}

	public void setRedShift(int redShift) {
		this.redShift = redShift;
	}

	public int getRedShift() {
		return redShift;
	}

	public void setGreenShift(int greenShift) {
		this.greenShift = greenShift;
	}

	public int getGreenShift() {
		return greenShift;
	}

	public void setBlueShift(int blueShift) {
		this.blueShift = blueShift;
	}

	public int getBlueShift() {
		return blueShift;
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("SetPixelFormatPacket");
		out.println("++++++++++++++++++++++++++++++++SPF");
		out.println("BPP: " + bitsPerPixel);
		out.println("Depth: " + depth);
		out.println("BigEndianFlag: " + bigEndianFlag);
		out.println("TrueColorFlag: " + trueColourFlag);
		out.println("redMax: " + redMax);
		out.println("greenMax: " + greenMax);
		out.println("blueMax: " + blueMax);
		out.println("redShift: " + redShift);
		out.println("greenShift: " + greenShift);
		out.println("blueShift: " + blueShift);
		out.println("--------------------------------SPF");
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeByte(0);
		os.writeByte(0);
		os.writeByte(0);// Padding
		// Send pixel format
		os.writeByte(bitsPerPixel);
		os.writeByte(depth);
		os.writeByte(bigEndianFlag);
		os.writeByte(trueColourFlag);
		os.writeShort(redMax);
		os.writeShort(greenMax);
		os.writeShort(blueMax);
		os.writeByte(redShift);
		os.writeByte(greenShift);
		os.writeByte(blueShift);
		os.writeByte(0);
		os.writeByte(0);
		os.writeByte(0);// Padding
	}

	public void readPacketData(DataInputStream is) throws IOException {
		is.skipBytes(3);// Skip padding

		// Pixel format
		bitsPerPixel = is.readUnsignedByte();
		depth = is.readUnsignedByte();
		bigEndianFlag = is.readUnsignedByte();
		trueColourFlag = is.readUnsignedByte();
		redMax = is.readUnsignedShort();
		greenMax = is.readUnsignedShort();
		blueMax = is.readUnsignedShort();
		redShift = is.readUnsignedByte();
		greenShift = is.readUnsignedByte();
		blueShift = is.readUnsignedByte();
		is.skipBytes(3);// Skip padding
	}

	public int getPacketType() {
		return CLIENT_SET_PIXEL_FORMAT;
	}

	/**
	 * Prepare packet before writing to remote side. (Eg. change capabilities of
	 * the server screen).
	 */
	public Screen prepareToWrite(Screen screen) {
		super.prepareToWrite(screen);

		// Change settings of associated screen
		/* LOG */logger
				.debug("Changing pixel format of the associated screen before writing.");
		/* LOG */logger.debug("New pixel format:" + dumpToString(true));
		Screen screen2 = new Screen(screen, true);
		screen2.setPixelFormat(bitsPerPixel, depth, bigEndianFlag,
				trueColourFlag, redMax, greenMax, blueMax, redShift,
				greenShift, blueShift);
		return screen2;
	}

	/**
	 * Do something after reading, eg. change capabilities of the client screen.
	 */
	public Screen postProcessAfterReading(Screen screen) {
		super.postProcessAfterReading(screen);

		// Change settings of associated screen
		/* LOG */logger
				.debug("Changing pixel format of the associated screen after reading.");
		/* LOG */logger.debug("New pixel format:" + dumpToString(true));
		Screen screen2 = new Screen(screen, true);
		screen2.setPixelFormat(bitsPerPixel, depth, bigEndianFlag,
				trueColourFlag, redMax, greenMax, blueMax, redShift,
				greenShift, blueShift);
		return screen2;
	}

	public void validate() {
		int redBits = maxToBits(redMax);
		int greenBits = maxToBits(greenMax);
		int blueBits = maxToBits(blueMax);

		if (bitsPerPixel != 8 && bitsPerPixel != 16 && bitsPerPixel != 32)
			throw new RuntimeException(
					"SetPixelFormatPacket is not valid, bad bitsPerPixel value (not 8,16,32): "
							+ dumpToString(true));

		if (depth <= 0 || depth > bitsPerPixel
				|| depth != (redBits + greenBits + blueBits))
			throw new RuntimeException(
					"SetPixelFormatPacket is not valid, bad depth value: "
							+ dumpToString(true));

		if (redMax <= 0 || redShift < 0 || (redShift + redBits) > bitsPerPixel
				|| greenMax <= 0 || greenShift < 0
				|| (greenShift + greenBits) > bitsPerPixel || blueMax <= 0
				|| blueShift < 0 || (blueShift + blueBits) > bitsPerPixel
				|| redBits + greenBits + blueBits > bitsPerPixel)
			throw new RuntimeException("SetPixelFormatPacket is not valid: "
					+ dumpToString(true));

	}

	private static int maxToBits(int max) {
		int i = 0;
		for (; max > 0; i++)
			max >>>= 1;
		return i;
	}

}
