package com.ccvnc.tm;

import java.util.Properties;

import com.ccvnc.ClientInterface;
import com.ccvnc.ServerInterface;
import com.ccvnc.SessionManager;
import com.ccvnc.packets.Packet;

/**
 * Interface for traffic managers.
 * 
 * TrafficManager goals:
 * <ul>
 * <li>filter out unneeded, wrong, forbidden or unsupported packets;</li>
 * <li>modify packets when necessary;</li>
 * <li>filter out servers/clients if they do not meet a traffic manager
 * requirements;</li>
 * </ul>
 * 
 * Interfaces should not ask a traffic manager help if they do not want to be
 * managed by traffic manager (eg. recorder or dumper interfaces).
 * 
 * @author Volodymyr M. Lisivka
 */
public interface TrafficManager {
	/**
	 * Calculate list of encodings supported by this traffic mananger.
	 */
	public int[] getSupportedEncodings();

	/**
	 * User may set list of encodings manually.
	 */
	public void setFixedListOfSupportedEncodings(String encodings);

	/**
	 * Configure this traffic manager.
	 */
	public void configure(SessionManager session, Properties properties);

	/**
	 * Filter a new client.
	 * 
	 * @return a client interface or null, if client interface filtered out.
	 */
	public ClientInterface filterClient(ClientInterface client, Properties props);

	/**
	 * Filter a new server.
	 * 
	 * @return a server interface or null, if server interface filtered out.
	 */
	public ServerInterface filterServer(ServerInterface server, Properties props);

	/**
	 * Do an action when client close connection. (Eg. disconnect server too).
	 */
	public void clientOut(ClientInterface client);

	/**
	 * Do an action when server close connection. (Eg. disconnect all clients).
	 */
	public void serverOut(ServerInterface server);

	/**
	 * Filter incoming packet from server after reading.
	 * 
	 * @return a packet or null, if packet filtered out.
	 */
	public Packet filterClientPacketAfterRead(ClientInterface client,
			Packet packet);

	/**
	 * Filter incoming packet from server after reading.
	 * 
	 * @return a packet or null, if packet filtered out.
	 */
	public Packet filterServerPacketAfterRead(ServerInterface server,
			Packet packet);

	/**
	 * Do an action before server interface will send the packet to the server.
	 * 
	 * @return a packet to send or null, to prevent sending.
	 */
	public Packet filterServerPacketBeforeWrite(ServerInterface server,
			Packet packet);

	/**
	 * Do an action before client interface will send the packet to the client.
	 * 
	 * @return a packet to send or null, to prevent sending.
	 */
	public Packet filterClientPacketBeforeWrite(ClientInterface client,
			Packet packet);

	/**
	 * Review packet queue after change.
	 */
	public void clientQueueChanged(ClientInterface client);

	/**
	 * Review packet queue after change.
	 */
	public void serverQueueChanged(ServerInterface server);

	/**
	 * Choose a packet from queue to send.
	 */
	public Packet getPrefferedPacketToSendForServer(ServerInterface server);

	/**
	 * Choose a packet from queue to send.
	 */
	public Packet getPrefferedPacketToSendForClient(ClientInterface client);

	/**
	 * Shutdown this traffic manager.
	 */
	public void shutdown();

}
