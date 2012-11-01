package com.ccvnc.packets.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.packets.Packet;

/**
 * ServerCutText - content of the server cut buffer.
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
 * <td>Packet type (3)</td>
 * </tr>
 * <tr>
 * <td>3</td>
 * <td>U8</td>
 * <td>padding</td>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>U32</td>
 * <td>length</td>
 * </tr>
 * <tr>
 * <td>length</td>
 * <td>U8[]</td>
 * <td>content</td>
 * </tr>
 * </table>
 * 
 * @author Volodymyr M. Lisivka
 */
public class ServerCutText extends ServerPacket {
	private byte[] content;

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet2) {
		return packet2;// Use latest cut buffer
	}

	public void setContent(byte[] buf) {
		this.content = buf;
	}

	public byte[] getContent() {
		return content;
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("ServerCutText, length=" + content.length + ", content=[\""
				+ new String(content) + "\"]");
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeShort(0);
		os.writeByte(0);// Padding
		os.writeInt(content.length);
		os.write(content);
	}

	public void readPacketData(DataInputStream is) throws IOException {
		is.skipBytes(3);// Skip padding
		int length = is.readInt();
		content = new byte[length];
		is.readFully(content);
	}

	public int getPacketType() {
		return SERVER_CUT_TEXT;
	}

}
