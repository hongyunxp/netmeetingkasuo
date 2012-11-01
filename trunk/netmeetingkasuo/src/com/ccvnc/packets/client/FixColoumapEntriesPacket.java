package com.ccvnc.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.packets.Packet;

/**
 * FixColoumapEntriesPacket
 * 
 * @author Volodymyr M. Lisivka
 */
public class FixColoumapEntriesPacket extends ClientPacket {
	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet2) {
		return packet2;// Drop older request in favor of newer one
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("FixColoumapEntriesPacket");
	}

	public void writePacketData(DataOutputStream os) throws IOException {
	}

	public void readPacketData(DataInputStream is) throws IOException {
		screen.resetPalette();
	}

	public int getPacketType() {
		return CLIENT_FIX_COLOURMAP_ENTRIES;
	}

	/**
	 * Method validate
	 * 
	 */
	public void validate() {
		// Deprecated message
	}

}
