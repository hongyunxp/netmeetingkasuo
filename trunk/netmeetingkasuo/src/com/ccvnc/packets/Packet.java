package com.ccvnc.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.Screen;

/**
 * Packet - interface to RFB protocol packets.
 * 
 * @author Volodymyr M. Lisivka
 */
public interface Packet {

	/**
	 * Get associated screen.
	 * 
	 * @return a Screen
	 * 
	 */
	public Screen getScreen();

	/**
	 * Set associated screen.
	 * 
	 * @param screen
	 *            a Screen
	 * 
	 */
	public void setScreen(Screen screen);

	/**
	 * Get packet type (it code, as in VNC protocol specification).
	 * 
	 * @return code of the packet type
	 * 
	 */
	public int getPacketType();

	/**
	 * Read packet data (without header) from input stream.
	 * 
	 * @param is
	 *            a DataInputStream
	 */
	public void readPacketData(DataInputStream is) throws IOException;

	/**
	 * Write packet data (without header) to output stream.
	 * 
	 * @param os
	 *            a DataOutputStream
	 * 
	 * @exception IOException
	 * 
	 */
	public void writePacketData(DataOutputStream os) throws IOException;

	/**
	 * Write content of this packet (with header) to output stream.
	 * 
	 * @param os
	 *            a DataOutputStream
	 */
	public void write(DataOutputStream os) throws IOException;

	/**
	 * Prepare packet before writing to remote side. (Eg. change capabilities of
	 * the screen).
	 * 
	 * If, after writing to remote side, remote side will change it options, the
	 * options of local screen need to be changed too.
	 */
	public Screen prepareToWrite(Screen screen);

	/**
	 * Do something after reading, eg. change capabilities of the client screen.
	 * 
	 * We need to update screen options when remote side told about that.
	 */
	public Screen postProcessAfterReading(Screen screen);

	/**
	 * Validate this packet.
	 */
	public void validate();

	/**
	 * Dump content if this packet to output stream.
	 * 
	 * @param out
	 *            a PrintStream to output to
	 * @param verbose
	 *            if true, then dump all available data
	 * 
	 */
	public void dump(PrintStream out, boolean verbose);

	/**
	 * Dump content if this packet to string.
	 * 
	 * @param verbose
	 *            if true, then dump all available data
	 * 
	 */
	public String dumpToString(boolean verbose);

	/**
	 * Replace two similar packets with new one to reduce traffic.
	 */
	public Packet squeeze(Packet packet2);

	/**
	 * Return true, if this packet may come from client side only, false
	 * otherwise.
	 */
	public boolean isClientPacket();

}
