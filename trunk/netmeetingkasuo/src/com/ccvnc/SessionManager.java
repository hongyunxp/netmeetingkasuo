package com.ccvnc;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ccvnc.packets.client.ClientPacket;
import com.ccvnc.packets.server.ServerPacket;
import com.ccvnc.tm.DumbTrafficManager;
import com.ccvnc.tm.GreedyTrafficManager;
import com.ccvnc.tm.OneToOneTrafficManager;
import com.ccvnc.tm.PlayerTrafficManager;
import com.ccvnc.tm.SmartTrafficManager;
import com.ccvnc.tm.TrafficManager;

/**
 * SessionManager - manage server and its clients.
 * 
 * Recognized properties in constructor:<br>
 * clientSocketFactoryClass - socket factory used to create server socket for
 * client listener<br>
 * serverSocketFactoryClass - socket factory used to create normal socket to
 * connect to VNC server and server socket for Server listener
 * 
 * oneToOne - one to one connection
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class SessionManager {
	private static Logger logger = Logger.getLogger(SessionManager.class);
	private static Logger activityLogger = Logger
			.getLogger("com.ccvnc.ActivityLogger");
	private static Logger errorLogger = Logger
			.getLogger("com.ccvnc.ErrorLogger");

	private static int RFB_MINOR_VERSION_TO_USE = 7;

	private Vector clients = new Vector();

	private SocketFactory serverSocketFactory;
	private SocketFactory clientSocketFactory;

	private boolean serverListenerActive = false;
	private boolean serverConnectedActive = false;
	private ServerSocket serverListenerServerSocket = null;
	private Socket serverConnectionSocket = null;
	private ServerInterface server = null;

	private boolean clientListenerActive = false;
	private ServerSocket clientListenerServerSocket;

	private String displayName;
	private String ownerName;

	// Used to notify about server change
	private Object serverChangeNotifier = new Object();

	private TrafficManager trafficManager;

	// Timestampt of last communication between server and clients
	private long timeOfLastChange = System.currentTimeMillis();

	public SessionManager(String displayName, String ownerName) {
		this.displayName = displayName;
		this.ownerName = ownerName;

		try {
			Properties properties = new Properties();
			createTrafficManager(properties);
			createClientSocketFactory(properties);
			createServerSocketFactory(properties);
		} catch (Exception e) {
			errorLogger.error("Can't create socket factory", e);
		}

	}

	public SessionManager(String displayName, String ownerName,
			Properties properties) throws ClassNotFoundException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		this.displayName = displayName;
		this.ownerName = ownerName;

		createTrafficManager(properties);
		createClientSocketFactory(properties);
		createServerSocketFactory(properties);
	}

	public void setTrafficManager(TrafficManager trafficManager) {
		this.trafficManager = trafficManager;
	}

	public TrafficManager getTrafficManager() {
		return trafficManager;
	}

	/**
	 * Update time of last change.
	 * 
	 */
	private void updateTimestamp() {
		timeOfLastChange = System.currentTimeMillis();
	}

	/**
	 * Return time of inactivity (no one packet was received or sent).
	 */
	public long getTimeOfInactivity() {
		return System.currentTimeMillis() - timeOfLastChange;
	}

	/**
	 * Create instance of traffic manager for this session
	 * 
	 * Properties:
	 * <ul>
	 * <li>"trafficManagerClass" name of the class
	 * <li>"trafficManager" name of the standard traffic manager (use "help" to
	 * receive list of valid names).</li>
	 * </ul>
	 * 
	 * @param properties
	 *            a Properties
	 */
	private void createTrafficManager(Properties properties)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException {
		if (properties.getProperty("trafficManagerClass") != null) {
			String trafficManagerClassName = properties
					.getProperty("trafficManagerClass");
			trafficManager = (TrafficManager) Class.forName(
					trafficManagerClassName).newInstance();
		} else if (properties.getProperty("trafficManager") != null) {
			String trafficManagerName = properties
					.getProperty("trafficManager");
			if (trafficManagerName.equalsIgnoreCase("oneToOne"))
				trafficManager = new OneToOneTrafficManager();
			else if (trafficManagerName.equalsIgnoreCase("dumb"))
				trafficManager = new DumbTrafficManager();
			else if (trafficManagerName.equalsIgnoreCase("greedy"))
				trafficManager = new GreedyTrafficManager();
			else if (trafficManagerName.equalsIgnoreCase("smart"))
				trafficManager = new SmartTrafficManager();
			else if (trafficManagerName.equalsIgnoreCase("player"))
				trafficManager = new PlayerTrafficManager();
			else
				throw new RuntimeException(
						"Unknown traffic manager name. Possible names: oneToOne dumb(default) greedy smart player");
		} else
			trafficManager = new DumbTrafficManager();

		logger.debug("" + trafficManager + " set as traffic manager for "
				+ this);

		trafficManager.configure(this, properties);
	}

	public void setClientListenerActive(boolean clientListenerActive) {
		this.clientListenerActive = clientListenerActive;
	}

	public boolean isClientListenerActive() {
		return clientListenerActive;
	}

	/**
	 * Send client packet to server.
	 * 
	 * Wait until server will be present and ready to work.
	 * 
	 * @param packet
	 *            a Packet
	 * 
	 */
	public void handleClientPacket(ClientPacket packet) {
		if (packet != null) {
			while (server == null && clientListenerActive)
				waitForServer(100);

			if (server == null || !clientListenerActive)
				return;

			server.waitForStatus(Communicator.STATUS_INITIALIZATION_CONTINUED);

			server.handlePacket(packet);
			trafficManager.serverQueueChanged(server);
			updateTimestamp();
		}
	}

	/**
	 * Send server packet to each active client.
	 * 
	 * @param packet
	 *            a Packet
	 * 
	 */
	public void handleServerPacket(ServerPacket packet) {
		if (packet != null)
			for (Enumeration e = clients.elements(); e.hasMoreElements();) {
				ClientInterface client = (ClientInterface) e.nextElement();
				if (client.getStatus() == Communicator.STATUS_NORMAL) {
					client.handlePacket(packet);
					trafficManager.clientQueueChanged(client);
				}
			}
		updateTimestamp();
	}

	public Enumeration clients() {
		return clients.elements();
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setServer(ServerInterface server) {
		this.server = server;
		notifyAboutServerStateChange();
	}

	public ServerInterface getServer() {
		return server;
	}

	/**
	 * Remove client from list of active clients.
	 * 
	 * @param client
	 *            a ClientInterface
	 * 
	 */
	public void removeClient(ClientInterface client) {
		clients.remove(client);
		trafficManager.clientOut(client);
	}

	/**
	 * Add new client to list of active clients.
	 * 
	 * @param client
	 *            a ClientInterface
	 * 
	 */
	public void addClient(ClientInterface client) {
		if (client != null) {
			logger.debug("New client (" + client + ") added to list.");
			clients.add(client);
		}
	}

	/**
	 * Listen for connections from clients.
	 * 
	 * @param clientPort
	 *            a port to listen for
	 * @param clientFullAccessPassword
	 *            password for clients with full access to VNC server
	 * @param clientViewOnlyPassword
	 *            password for clients with viewonly access to VNC server
	 * @param props
	 *            a Properties
	 */
	public void listenForClients(final int clientPort,
			final String clientFullAccessPassword,
			final String clientViewOnlyPassword, final Properties props)
			throws IOException {
		shutdownClientInterfaces(props);

		clientListenerServerSocket = clientSocketFactory.createServerSocket(
				clientPort, props);
		clientListenerActive = true;

		if (props.getProperty("dump_to") != null)
			attachDumper(props.getProperty("dump_to"), props);

		if (props.getProperty("record_to") != null)
			attachRecorded(props.getProperty("record_to"), props);

		new Thread(new Runnable() {
			public void run() {
				Socket socket;
				for (; clientListenerActive;) {
					try {
						socket = clientListenerServerSocket.accept();
					} catch (IOException e) {
						if (clientListenerActive)
							errorLogger.error("Can't accept client connection",
									e);
						break;
					}
					try {
						activityLogger.info("CLIENT_CONNECTION_ACCEPTED:"
								+ displayName
								+ ":New client connection accepted from "
								+ socket.getRemoteSocketAddress());
						handleClientConnection(new ClientInterface(
								SessionManager.this, new DataInputStream(socket
										.getInputStream()),
								new DataOutputStream(new BufferedOutputStream(
										socket.getOutputStream())),
								clientFullAccessPassword,
								clientViewOnlyPassword), props);
					} catch (IOException e) {
						if (clientListenerActive)
							errorLogger
									.error("Can't get stream from socket", e);
					}
				}
			}
		}, "Client listener " + displayName).start();
	}

	/**
	 * Handle connection with client
	 * 
	 * Do handshake and authentification and then start handlers for incomming
	 * and outcomming traffic.
	 */
	public void handleClientConnection(final ClientInterface client,
			final Properties props) {
		if (clientListenerActive)
			new Thread(new Runnable() {
				public void run() {
					try {
						while (server == null && clientListenerActive)
							waitForServer(100);

						if (!clientListenerActive)
							return;

						server
								.waitForStatus(Communicator.STATUS_INITIALIZATION);

						client.setScreen(new Screen(server.getScreen(), false));
						//client.handlePolicy();
						client.handshake();
					} catch (IOException e) {
						if (clientListenerActive)
							errorLogger.error(
									"Exception in handshaking with client", e);
						return;
					}
					if (clientListenerActive) {
						// TODO: maybe better to move out interaction with
						// traffic manager from this method?
						ClientInterface client2 = trafficManager.filterClient(
								client, props);
						if (client2 != null) {
							addClient(client2);
							client2.runHandlers(false);
						}
					}
				}
			}, "Client handler " + displayName).start();
	}

	/**
	 * Wait for server change
	 * 
	 * Used by client handlers because they need server to begin handshaking.
	 */
	public void waitForServer(int miliseconds) {
		synchronized (serverChangeNotifier) {
			try {
				if (miliseconds > 0)
					serverChangeNotifier.wait(miliseconds);
				else
					serverChangeNotifier.wait();
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Notify about server state change.
	 * 
	 * Used in setServer() to notify clients about new server.
	 */
	private void notifyAboutServerStateChange() {
		synchronized (serverChangeNotifier) {
			serverChangeNotifier.notifyAll();
		}
	}

	/**
	 * Atach recorder client interface to session.
	 * 
	 * Used in listenForClients() to add recorder client interface to sesion.
	 */
	private void attachRecorded(String fileName, Properties props)
			throws IOException {
		fileName = getFreeFileName(fileName);
		if (fileName != null) {
			ClientInterface client = new FBSRecorderClientInterface(this,
					new DataOutputStream(new FileOutputStream(fileName)), props);
			client.setScreen(new Screen(server.getScreen(), false));
			client.handshake();
			addClient(client);
			client.runHandlers(true);
		}
	}

	/**
	 * Atach dumper client interface to session.
	 * 
	 * Used in listenForClients() to add recorder client interface to sesion.
	 */
	private void attachDumper(String fileName, Properties props)
			throws IOException {
		fileName = getFreeFileName(fileName);
		if (fileName != null) {
			ClientInterface client = new DumperClientInterface(this,
					new PrintStream(new FileOutputStream(fileName)), props);
			client.setScreen(new Screen(server.getScreen(), false));
			client.handshake();
			addClient(client);
			client.runHandlers(true);
		}
	}

	/**
	 * Get file name for new session recording or dumping stream.
	 * 
	 * replace special symbols '/' and '\\' by '_' and append three digit number
	 * to end of file name.
	 * 
	 * @param fileName
	 *            a file name template
	 * 
	 * @return a new file name or null, if can't create.
	 * 
	 */
	private String getFreeFileName(String fileName) {
		fileName = fileName.replace('/', '_');
		fileName = fileName.replace('\\', '_');

		boolean freeFilenameFound = false;
		File file = new File(fileName);

		if (!file.exists())
			freeFilenameFound = true;
		else {
			for (int i = 0; i < 100; i++) {

				if (i < 10)
					file = new File(fileName + ".00" + i);
				else if (i < 100)
					file = new File(fileName + ".0" + i);
				else
					file = new File(fileName + "." + i);

				if (!file.exists()) {
					freeFilenameFound = true;
					break;
				}
			}
		}

		if (freeFilenameFound)
			return file.getAbsolutePath();

		logger.warn("Can't create new file with name \""
				+ file.getAbsolutePath() + "\".");
		return null;
	}

	/**
	 * 判断服务端是否连接上
	 * @return
	 */
	public synchronized boolean isServerConnectedActive() {
		return serverConnectedActive;
	}

	/**
	 * Listen for connection from server.
	 * 
	 * @param serverPort
	 *            port to listen
	 * @param serverPassword
	 *            password to use
	 * @param props
	 *            a bunch of additional properties
	 */
	public synchronized void listenForServer(int serverPort,
			final String serverPassword, final Properties props)
			throws IOException {
		shutdownServerInterface(props);

		serverListenerServerSocket = serverSocketFactory.createServerSocket(
				serverPort, props);
		serverListenerActive = true;

		new Thread(new Runnable() {
			public void run() {
				for (; serverListenerActive;) {
					try {
						serverConnectionSocket = serverListenerServerSocket
								.accept();
						serverConnectedActive = true;
					} catch (IOException e) {
						if (serverListenerActive)
							errorLogger.error("Can't accept server connection",
									e);
						break;
					}
					ServerInterface server;
					try {
						activityLogger.info("SERVER_CONNECTION_ACCEPTED:"
								+ displayName
								+ ":New server connection accepted from "
								+ serverConnectionSocket
										.getRemoteSocketAddress());
						server = new ServerInterface(SessionManager.this,
								new DataInputStream(serverConnectionSocket
										.getInputStream()),
								new DataOutputStream(new BufferedOutputStream(
										serverConnectionSocket
												.getOutputStream())));
						Screen serverScreen = new Screen(
								RFB_MINOR_VERSION_TO_USE);
						server.setScreen(serverScreen);
						server.handshake(serverPassword);
						server = trafficManager.filterServer(server, props);
					} catch (IOException e) {
						if (serverListenerActive)
							errorLogger.error(
									"Exception in handshaking with server", e);
						continue;
					}
					if (server != null) {
						setServer(server);

						try {
							server.runHandlers(false);
						} finally {
							trafficManager.serverOut(server);
							setServer(null);
							server.closeConnection();
						}
					}
				}
			}
		}, "Server listener " + displayName).start();
	}

	/**
	 * Connect to server.
	 * 
	 * @param hostName
	 *            host name of VNC server to connect to
	 * @param serverPort
	 *            port number of VNC server
	 * @param props
	 *            a Properties
	 * @param serverPassword
	 *            password to use
	 * 
	 * @return always true
	 */
	public synchronized boolean connectToServer(String hostName,
			int serverPort, String serverPassword, Properties props) {
		shutdownServerInterface(props);

		boolean autoreconnect = false;
		serverListenerActive = true;

		if (props.getProperty("autoreconnect") != null)
			autoreconnect = Boolean.valueOf(props.getProperty("autoreconnect"))
					.booleanValue();

		int delay = 5;// Wait 5 seconds before reconnecting by default

		if (props.getProperty("autoreconnect.delay") != null)
			delay = Integer.parseInt(props.getProperty("autoreconnect.delay"));

		if (delay <= 0)
			delay = 1;// At least one second to prevent network overloading in
		// bad case

		handleConnectToServerConnection(autoreconnect, delay, hostName,
				serverPort, props, serverPassword);

		return true;
	}

	/**
	 * Handle server conection for connectToServer() call.
	 * 
	 * @param autoreconnect
	 *            automatically reconnect when connection lost
	 * @param delay
	 *            number of seconds to wait before reconnect (at least 1 sec).
	 * @param hostName
	 *            host name of VNC server to connect to
	 * @param serverPort
	 *            port number of VNC server
	 * @param props
	 *            a Properties
	 * @param serverPassword
	 *            password to use
	 * 
	 */
	private synchronized void handleConnectToServerConnection(
			final boolean autoreconnect, final int delay,
			final String hostName, final int serverPort,
			final Properties props, final String serverPassword) {
		new Thread(new Runnable() {
			public void run() {
				try {
					for (; serverListenerActive;) {
						ServerInterface server;
						try {
							serverConnectionSocket = serverSocketFactory
									.createSocket(hostName, serverPort, props);
							activityLogger.info("SERVER_CONNECTION_ACCEPTED:"
									+ displayName
									+ ":New server connection created to "
									+ serverConnectionSocket
											.getRemoteSocketAddress());
							server = new ServerInterface(
									SessionManager.this,
									new DataInputStream(serverConnectionSocket
											.getInputStream()),
									new DataOutputStream(
											new BufferedOutputStream(
													serverConnectionSocket
															.getOutputStream())));
							Screen serverScreen = new Screen(
									RFB_MINOR_VERSION_TO_USE);
							server.setScreen(serverScreen);
							server.handshake(serverPassword);
						} catch (IOException e) {
							if (serverListenerActive)
								errorLogger
										.warn("Server disconnected when handshaking or can't connect.");
							break;
						}
						server = trafficManager.filterServer(server, props);
						if (server == null)
							break;

						setServer(server);
						try {
							server.runHandlers(false);
						} finally {
							trafficManager.serverOut(server);
							setServer(null);
							server.closeConnection();
						}
						if (autoreconnect && serverListenerActive)
							Thread.sleep(1000 * delay);// Make a delay before
						// reconnecting
						else
							break;
					}
				} catch (InterruptedException e) {
				}
			}
		}, "Connect to server " + displayName).start();
	}

	/**
	 * Shutdown client listener and all client interfaces.
	 */
	public synchronized boolean shutdownClientInterfaces(Properties props) {
		boolean res = clientListenerActive;
		clientListenerActive = false;
		try {
			if (clientListenerServerSocket != null)
				clientSocketFactory.shutdownServerSocket(
						clientListenerServerSocket, props);
		} catch (IOException e) {
			res = false;
		}
		clientListenerServerSocket = null;

		// Shutdown all clients
		for (int i = clients.size() - 1; i >= 0; i--)
			// Client will remove itself from session
			((ClientInterface) clients.elementAt(i)).closeConnection();

		clients.clear();

		return res;
	}

	/**
	 * Shutdown server listener and server interface.
	 */
	public synchronized boolean shutdownServerInterface(Properties props) {
		boolean res = serverListenerActive;
		serverListenerActive = false;
		try {
			serverSocketFactory.shutdownServerSocket(
					serverListenerServerSocket, props);
		} catch (Exception e) {
			res = false;
		}
		serverListenerServerSocket = null;

		// Close active streams
		if (server != null)
			server.closeConnection();

		setServer(null);

		return res;
	}

	/**
	 * Shutdown this session.
	 * 
	 * Shutdown traffic manager and all interfaces.
	 */
	public void shutdown(Properties props) {
		trafficManager.shutdown();
		shutdownServerInterface(props);
		shutdownClientInterfaces(props);
	}

	/**
	 * Create socket factory to use for VNC server connections.
	 * 
	 * Socket factory will be used to create sockets to connect to VNC server or
	 * to listen connection from VNC server.
	 */
	private void createServerSocketFactory(Properties properties)
			throws ClassNotFoundException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		String propertyName = "serverSocketFactoryClass";
		String className = properties.getProperty(propertyName);
		if (className != null && className.length() > 0) {
			serverSocketFactory = (SocketFactory) Class.forName(className)
					.newInstance();
			serverSocketFactory.initialize(properties);
		} else
			serverSocketFactory = new PlainSocketFactory();
	}

	/**
	 * Create socket factory to use for VNC client connections.
	 * 
	 * Socket factory will be used to create sockets to listen connection from
	 * client.
	 */
	private void createClientSocketFactory(Properties properties)
			throws ClassNotFoundException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		String propertyName = "clientSocketFactoryClass";
		String className = properties.getProperty(propertyName);
		if (className != null && className.length() > 0) {
			clientSocketFactory = (SocketFactory) Class.forName(className)
					.newInstance();
			clientSocketFactory.initialize(properties);
		} else
			clientSocketFactory = new PlainSocketFactory();
	}
}
