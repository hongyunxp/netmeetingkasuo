package com.ccvnc.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.packets.Packet;

/**
 * Bell - ring.
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
 * </table>
 * 
 * @author Volodymyr M. Lisivka
 */
public class Bell extends ServerPacket {

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet2) {
		return null;// Don't squeeze bell packets
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("+Bell+");
	}

	public void writePacketData(DataOutputStream os) throws IOException {
	}

	public void readPacketData(DataInputStream is) throws IOException {
	}

	public int getPacketType() {
		return SERVER_BELL;
	}

}
