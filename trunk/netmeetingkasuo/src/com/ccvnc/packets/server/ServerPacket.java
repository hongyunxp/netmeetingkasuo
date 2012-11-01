package com.ccvnc.packets.server;

import com.ccvnc.packets.AbstractPacket;

/**
 * ServerPacket
 * 
 * @author Volodymyr M. Lisivka
 */
public abstract class ServerPacket extends AbstractPacket {
	/**
	 * Return false - this is server packet.
	 */
	public boolean isClientPacket() {
		return false;
	}

}
