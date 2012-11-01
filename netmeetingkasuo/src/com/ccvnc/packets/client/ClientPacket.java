package com.ccvnc.packets.client;

import com.ccvnc.packets.AbstractPacket;

/**
 * ClientPacket
 * 
 * @author Volodymyr M. Lisivka
 */
public abstract class ClientPacket extends AbstractPacket {
	/**
	 * Return true - this is client packet.
	 */
	public boolean isClientPacket() {
		return true;
	}
}
