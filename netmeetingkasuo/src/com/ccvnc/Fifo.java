package com.ccvnc;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ccvnc.packets.Packet;

/**
 * Simple FIFO.
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class Fifo {
	private static Logger logger = Logger.getLogger(Fifo.class);

	private Vector queue = new Vector();

	/**
	 * Return true if queue contains packet of given type.
	 * 
	 * @param packetType
	 *            a packet type
	 * 
	 * @return true if queue contains packet of given type, false otherwise.
	 */
	public boolean isContainsPacket(int packetType) {
		for (Enumeration e = queue.elements(); e.hasMoreElements();)
			if (((Packet) e.nextElement()).getPacketType() == packetType)
				return true;

		return false;
	}

	/**
	 * Get first packet in queue.
	 */
	public Packet getTopPacket() {
		if (size() < 1)
			return null;

		return (Packet) queue.elementAt(0);
	}

	/**
	 * Walk through queue and try to replace each two packets of same type with
	 * new one.
	 */
	public synchronized void squeeze() {
		/* (n^2)*n , may cause problems when n is big */
		for (int i = 0; i < queue.size() - 1; i++) {
			Packet packet = (Packet) queue.elementAt(i);
			int packetType = packet.getPacketType();

			for (int j = i + 1; j < queue.size(); j++) {
				Packet packet2 = (Packet) queue.elementAt(j);
				int packetType2 = packet2.getPacketType();
				if (packetType == packetType2) {
					Packet packet3 = packet.squeeze(packet2);
					if (packet3 != null) {
						logger.debug("Replacing " + packet + " and " + packet2
								+ " by " + packet3 + ".");
						queue.remove(packet2);
						j--;
						queue.setElementAt(packet3, i);
					}
				}
			}
		}
	}

	/**
	 * Clear queue
	 * 
	 * Remove all packets from queue.
	 */
	public synchronized void clear() {
		logger.warn("Clearing queue.");
		queue.clear();
	}

	/**
	 * Add packet to FIFO.
	 * 
	 * @param packetToSend
	 *            a Packet to add
	 * 
	 */
	public synchronized void add(Packet packetToSend) {
		logger.debug("Adding " + packetToSend + " to queue.");
		queue.add(packetToSend);
	}

	/**
	 * Pop packet from FIFO.
	 * 
	 * @return next packet or null, if queue is empty
	 * 
	 */
	public synchronized Packet pop() {
		if (queue.size() > 0) {
			Object obj = queue.remove(0);
			logger.debug("Popping " + obj + " from queue.");
			return (Packet) obj;
		} else
			return null;
	}

	/**
	 * Pop packet of requested type from FIFO and leave untouched all others
	 * packets.
	 * 
	 * @param enabledPacketTypes
	 *            list of packet types
	 * @param enabled
	 *            if true, then return packet with type in list, if false,
	 *            thenreturn packet with type not in list.
	 * 
	 * @return next packet or null, if queue is empty or pascket not avialable
	 */
	public synchronized Packet pop(int[] enabledPacketTypes, boolean enabled) {
		if (queue.size() > 0) {
			for (Enumeration e = queue.elements(); e.hasMoreElements();) {
				Packet packet = (Packet) e.nextElement();
				int packetType = packet.getPacketType();

				if (enabled) {// If packet type in list, then pop it
					for (int i = 0; i < enabledPacketTypes.length; i++)
						if (packetType == enabledPacketTypes[i]) {
							logger.debug("Popping " + packet + " from queue.");
							queue.remove(packet);
							return packet;
						}
				} else {// If packet type not in list, then pop it
					boolean pop = true;
					for (int i = 0; i < enabledPacketTypes.length; i++)
						if (packetType == enabledPacketTypes[i]) {
							pop = false;
							break;
						}
					if (pop) {
						queue.remove(packet);
						return packet;
					}
				}

			}
		}
		return null;
	}

	/**
	 * Return queue size.
	 * 
	 * @return number of elemenst in the queue
	 * 
	 */
	public int size() {
		return queue.size();
	}

}
