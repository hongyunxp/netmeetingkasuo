package com.ccvnc.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.log4j.Logger;

import com.ccvnc.PacketManager;
import com.ccvnc.Screen;
import com.ccvnc.packets.Packet;

/**
 * SetEncodingsPacket - notify server about new list of supported encodings.
 * 
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
 * <td>Packet type (2)</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>padding</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U16</td>
 * <td>numberOfEncodings</td>
 * </tr>
 * <tr>
 * <td>numberOfEncodings*4</td>
 * <td>U32[]</td>
 * <td>encodings</td>
 * </tr>
 * </table>
 * 
 * @author Volodymyr M. Lisivka
 */
public class SetEncodingsPacket extends ClientPacket {
	/* LOG */private static Logger logger = Logger
			.getLogger(SetEncodingsPacket.class);

	private int[] encodings;

	public SetEncodingsPacket() {
	}

	public SetEncodingsPacket(int[] encodings) {
		this.encodings = encodings;
	}

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet2) {
		return packet2;// Use newest set of encdoings
	}

	public void setEncodings(int[] encodings) {
		this.encodings = encodings;
	}

	public int[] getEncodings() {
		return encodings;
	}

	public void dump(PrintStream out, boolean verbose) {
		if (encodings == null)
			out.println("SetEncodingsPacket, NO ENCODINGS SET!");
		else {
			out.println("SetEncodingsPacket, numberOfEncodings="
					+ encodings.length);
			out.println("+++++++++++++++++++++++++++++++SE");
			for (int i = 0; i < encodings.length; i++)
				out.println("Encoding[" + i + "]="
						+ PacketManager.getEncodingName(encodings[i]));
			out.println("-------------------------------SE");
		}
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeByte(0);// padding
		os.writeShort(encodings.length);
		for (int i = 0; i < encodings.length; i++)
			os.writeInt(encodings[i]);
	}

	public void readPacketData(DataInputStream is) throws IOException {
		is.skipBytes(1);// Skip padding
		int numberOfEncodings = is.readUnsignedShort();
		encodings = new int[numberOfEncodings];

		for (int i = 0; i < numberOfEncodings; i++)
			encodings[i] = is.readInt();
	}

	public int getPacketType() {
		return CLIENT_SET_ENCODINGS;
	}

	/**
	 * Prepare packet before writing to remote side. (Eg. change capabilities of
	 * the server screen).
	 */
	public Screen prepareToWrite(Screen screen) {
		super.prepareToWrite(screen);

		/* LOG */logger
				.debug("Changing supported encodings of the associated screen before writing.");
		/* LOG */logger.debug("New list of supported encodings:"
				+ dumpToString(true));
		screen.setSupportedEncodings(encodings);
		return screen;
	}

	/**
	 * Do something after reading. (Eg. change capabilities of the client
	 * screen).
	 */
	public Screen postProcessAfterReading(Screen screen) {
		super.postProcessAfterReading(screen);

		/* LOG */logger
				.debug("Changing list of supported encodings of the associated screen after reading.");
		/* LOG */logger.debug("New list of supported encodings:"
				+ dumpToString(true));
		screen.setSupportedEncodings(encodings);
		return screen;
	}

	public void filterEncodings(int[] supportedEncodings) {
		int delta = 0;
		for (int i = 0; i < encodings.length; i++) {
			int encoding = encodings[i];
			boolean accept = false;
			// Don't accept unsupported encodings
			for (int j = 0; j < supportedEncodings.length; j++) {
				if (encoding == supportedEncodings[j])
					accept = true;
			}
			if (accept)
				encodings[i - delta] = encoding;
			else {
				/* LOG */logger.debug("Encoding "
						+ PacketManager.getEncodingName(encoding)
						+ " was not accepted.");
				delta++;
			}
		}

		// Cleanup array
		if (delta > 0) {
			int[] encodings2 = new int[encodings.length - delta];
			System.arraycopy(encodings, 0, encodings2, 0, encodings2.length);
			encodings = encodings2;
		}
	}

}
