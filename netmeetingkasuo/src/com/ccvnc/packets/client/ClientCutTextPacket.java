package com.ccvnc.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.RfbConstants;
import com.ccvnc.Screen;
import com.ccvnc.packets.Packet;

/**
 * ClientCutText packet - content of the client cut buffer.
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
 * <td>Packet type (6)</td>
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
public class ClientCutTextPacket extends ClientPacket {
	private byte[] content = null;

	public ClientCutTextPacket() {
	}

	public ClientCutTextPacket(Screen screen, byte[] content) {
		this.screen = screen;
		this.content = content;
	}

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet2) {
		return packet2;// Drop older cut buffer in favor of newer one
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("ClientCutText, content=[\"" + new String(content)
				+ "\"]\n");
	}

	public int getPacketType() {
		return RfbConstants.CLIENT_CUT_TEXT;
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeShort(0);
		os.writeByte(0);// padding
		os.writeInt(content.length);
		os.write(content);
	}

	public void readPacketData(DataInputStream is) throws IOException {
		is.skipBytes(3);// skip padding
		int length = is.readInt();
		content = new byte[length];
		is.readFully(content);
	}

	public void validate() {
		super.validate();
		if (content == null)
			throw new RuntimeException("ClientCutTextPacket is not valid.");
	}

}
