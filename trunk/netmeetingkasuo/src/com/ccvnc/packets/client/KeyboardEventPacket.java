package com.ccvnc.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.Screen;
import com.ccvnc.packets.Packet;

/**
 * KeyboardEvent - a key pressed or released.
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
 * <td>Packet type (4)</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>downFlag</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U8</td>
 * <td>padding</td>
 * </tr>
 * <tr>
 * <td>4</td>
 * <td>U32</td>
 * <td>key</td>
 * </tr>
 * </table>
 * 
 * @author Volodymyr M. Lisivka
 */
public class KeyboardEventPacket extends ClientPacket {

	private int downFlag = -1, key = -1;

	public KeyboardEventPacket() {
	}

	public KeyboardEventPacket(Screen screen, int downFlag, int key) {
		this.downFlag = downFlag;
		this.key = key;
		this.screen = screen;
	}

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet2) {
		return null;// Don't squeeze key events
	}

	public int getPacketType() {
		return CLIENT_KEYBOARD_EVENT;
	}

	/**
	 * Sets DownFlag
	 * 
	 * @param downFlag
	 *            an int
	 */
	public void setDownFlag(int downFlag) {
		this.downFlag = downFlag;
	}

	/**
	 * Returns DownFlag
	 * 
	 * @return an int
	 */
	public int getDownFlag() {
		return downFlag;
	}

	/**
	 * Sets Key
	 * 
	 * @param key
	 *            an int
	 */
	public void setKey(int key) {
		this.key = key;
	}

	/**
	 * Returns Key
	 * 
	 * @return an int
	 */
	public int getKey() {
		return key;
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeByte(downFlag);
		os.writeShort(0);// padding
		os.writeInt(key);
	}

	public void readPacketData(DataInputStream is) throws IOException {
		downFlag = is.readByte();
		is.skipBytes(2);// Skip padding
		key = is.readInt();
	}

	public void validate() {
		if (screen == null)
			throw new RuntimeException("Keyboard event is not valid.");
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("KeyboardEvent, key=0x" + Integer.toHexString(key) + "('"
				+ ((char) key) + "), downFlag=" + downFlag);
	}

}
