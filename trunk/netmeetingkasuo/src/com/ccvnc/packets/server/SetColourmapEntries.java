package com.ccvnc.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.Screen;
import com.ccvnc.packets.Packet;

/**
 * SetColourmapEntries - new values of palette colours.
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
 * <td>Packet type (1)</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>padding</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U16</td>
 * <td>firstColour</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U16</td>
 * <td>numberOfColours</td>
 * </tr>
 * <tr>
 * <td>numberOfColours*6</td>
 * <td>U16[]</td>
 * <td>palette (R - U16, G - U16, B - U16)*numberOfColours</td>
 * </tr>
 * </table>
 * 
 * @author Volodymyr M. Lisivka
 */
public class SetColourmapEntries extends ServerPacket {
	private int firstColour, numberOfColours;
	private int[] palette;

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet) {
		return null;// Don't change order
	}

	public void setFirstColour(int firstColour) {
		this.firstColour = firstColour;
	}

	public int getFirstColour() {
		return firstColour;
	}

	public void setNumberOfColours(int numberOfColours) {
		this.numberOfColours = numberOfColours;
	}

	public int getNumberOfColours() {
		return numberOfColours;
	}

	public void setPalette(int[] palette) {
		this.palette = palette;
	}

	public int[] getPalette() {
		return palette;
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("SetColourmapEntries, firstColour=" + firstColour
				+ ",numberOfColours=" + numberOfColours);
		if (verbose) {
			out.println("++++++++++++++++++++++++++++++++++++++++++++++");
			for (int i = 0; i < numberOfColours; i++)
				out.println("palette[" + i + "]=R"
						+ Integer.toHexString(palette[i * 3]) + ",G"
						+ Integer.toHexString(palette[i * 3 + 1]) + ",B"
						+ Integer.toHexString(palette[i * 3 + 2]));
			out.println("----------------------------------------------");
		}
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeByte(0);// padding
		os.writeShort(firstColour);
		os.writeShort(numberOfColours);
		for (int i = 0; i < numberOfColours * 3; i++)
			os.writeShort(palette[i]);
	}

	public void readPacketData(DataInputStream is) throws IOException {
		is.skipBytes(1);// Skip padding
		firstColour = is.readUnsignedShort();
		numberOfColours = is.readUnsignedShort();
		palette = new int[numberOfColours * 3];
		for (int i = 0; i < numberOfColours * 3; i++)
			palette[i] = is.readUnsignedShort();
	}

	/**
	 * Do something after reading, eg. change capabilities of the client screen.
	 */
	public Screen postProcessAfterReading(Screen screen) {
		Screen screen2 = new Screen(screen, true);
		screen2.setPalette(firstColour, numberOfColours, palette);
		return screen2;
	}

	/**
	 * Prepare packet before writing to remote side, eg. change capabilities of
	 * the server screen.
	 */
	public Screen prepareToWrite(Screen screen) {
		Screen screen2 = new Screen(screen, true);
		screen2.setPalette(firstColour, numberOfColours, palette);
		return screen2;
	}

	public int getPacketType() {
		return SERVER_SET_COLOURMAP_ENTRIES;
	}
}
