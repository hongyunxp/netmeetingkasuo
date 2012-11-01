package com.ccvnc.packets;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.RfbConstants;
import com.ccvnc.Screen;

/**
 * Contains code common for all packets.
 * 
 * @author Volodymyr M. Lisivka
 */
public abstract class AbstractPacket implements Packet, RfbConstants {
	/**
	 * Associated screen.
	 */
	protected Screen screen;

	public Screen getScreen() {
		return screen;
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	/**
	 * Write content of this packet (with header) to output stream.
	 * 
	 * @param os
	 *            a DataOutputStream
	 */
	public void write(DataOutputStream os) throws IOException {
		writeHeaderParameters(os);
		writePacketData(os);
	}

	/**
	 * Write packet header to output stream.
	 * 
	 * @param os
	 *            a DataOutputStream
	 */
	public void writeHeaderParameters(DataOutputStream os) throws IOException {
		os.writeByte(getPacketType());
	}

	/**
	 * Validate content of this packet.
	 * 
	 * Just check screen presence.
	 */
	public void validate() {
		if (screen == null)
			throw new RuntimeException("Packet screen not initialized");
	}

	/**
	 * Prepare packet before writing to remote side, eg. change capabilities of
	 * the interface screen.
	 * 
	 * If, after writing of this packet to remote side, remote side will change
	 * it options, the options of local screen need to be changed too.
	 */
	public Screen prepareToWrite(Screen screen) {
		return screen;
	}

	/**
	 * Do something after reading. (Eg. change capabilities of the client
	 * screen.)
	 * 
	 * We need to update screen options when remote side told about that.
	 */
	public Screen postProcessAfterReading(Screen screen) {
		return screen;
	}

	/**
	 * Dump content of this packet to sctring.
	 * 
	 * @param verbose
	 *            be a bit verbose
	 */
	public String dumpToString(boolean verbose) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(bos);
		dump(out, verbose);
		out.flush();
		return new String(bos.toByteArray());
	}

}
