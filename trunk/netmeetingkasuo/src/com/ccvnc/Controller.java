package com.ccvnc;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.xmlrpc.WebServer;

import com.meeting.utils.AppConfigure;

/**
 * Controller - handler of remote XMLRPC calls.
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class Controller {
	private static Logger logger = Logger.getLogger(Controller.class);
	private static Logger activityLogger = Logger
			.getLogger("com.ccvnc.ActivityLogger");
	private static Logger errorLogger = Logger
			.getLogger("com.ccvnc.ErrorLogger");

	private static Hashtable interfaces = new Hashtable();
	private Authentificator authentificator;
	private Properties applicationProperties;
	private static Timer timer = Timer.startNewTimer("Session remover thread");

	private Runnable cleaningTask = new Runnable() {
		public void run() {
			closeOldSessions();
		}
	};

	/**
	 * Read application parametres, start new XMLRPC web server, add this class
	 * as default handler.
	 */
	public static void main(String[] args) {

		if (args.length == 0 || (args.length & 1) == 1) {
			System.out
					.println("Usage: java "
							+ Controller.class.getName()
							+ " key value ...\n"
							+ "Keys:\n"
							+ " logger.conf  - name of configuration file for logger\n"
							+ " webserver.port - port number to listen by web server\n"
							+ " Controller.authentificatorClass - use instance of this class to validate user passwords\n"
							+ "\n" + "\n");
		}

		Properties applicationProperties = new Properties();
		for (int i = 0; i < args.length; i += 2)
			applicationProperties.setProperty(args[i], args[i + 1]);

		if (applicationProperties.getProperty("logger.conf") != null)
			PropertyConfigurator.configure(applicationProperties
					.getProperty("logger.conf"));
		else
			PropertyConfigurator.configure(AppConfigure.ccvnc_log4j_path);

		int port = 8080;
		if (applicationProperties.getProperty("webserver.port") != null)
			port = Integer.parseInt(applicationProperties
					.getProperty("webserver.port"));

		try {
			WebServer webserver = new WebServer(port);
			webserver.addHandler("$default", new Controller(
					applicationProperties));
			webserver.start();
		} catch (Exception e) {
			logger.error("Can't start server:", e);
		}
	}

	/**
	 * 启动控制器
	 * 
	 * @param port
	 */
	public static void startController(int port, Properties p) {
		// PropertyConfigurator.configure(AppConfigure.ccvnc_log4j_path);
		try {
			WebServer webserver = new WebServer(port);
			webserver.addHandler("$default", new Controller(p));
			webserver.start();
		} catch (Exception e) {
			logger.error("Can't start server:", e);
		}
	}

	/**
	 * Create new controller.
	 * 
	 * Store application properties and create instance of the authentificator.
	 * 
	 * @param applicationProperties
	 *            global application properties
	 * 
	 */
	public Controller(Properties applicationProperties) {
		this.applicationProperties = applicationProperties;
		createAuthetificatorInstance(applicationProperties);
		timer.addTask(cleaningTask);
	}

	/**
	 * Create new authentificator.
	 * 
	 * Create instance of Authentificator, using value of property
	 * "Controller.authentificatorClass" as class name.
	 */
	private void createAuthetificatorInstance(Properties applicationProperties) {
		String className = SimpleAuthentificator.class.getName();
		if (applicationProperties
				.getProperty("Controller.authentificatorClass") != null)
			className = applicationProperties
					.getProperty("Controller.authentificatorClass");

		try {
			authentificator = (Authentificator) Class.forName(className)
					.newInstance();
			authentificator.configure(applicationProperties);
		} catch (Exception e) {
			authentificator = null;
			throw new RuntimeException("Can't create authentificator - " + e);
		}
	}

	/**
	 * Close and remove each session when it timeout excided.
	 */
	private void closeOldSessions() {
		// close session, if it server inactive more than 5 minutes.
		for (Enumeration e = interfaces.keys(); e.hasMoreElements();) {
			String display = (String) e.nextElement();
			SessionManager session = (SessionManager) interfaces.get(display);

			if (session.getTimeOfInactivity() > 5 * 60 * 1000)// More that 5
			// minutes of
			// inactivity
			{
				logger.debug("Timeout of " + session + " for " + display + ".");

				// Remove session from list of active sessions
				interfaces.remove(display);
				// And shutdown it
				session.shutdown(applicationProperties);
			}
		}
	}

	/**
	 * Get session manager for given display name
	 * 
	 * @param displayName
	 *            display name
	 * @param required
	 *            if true, then throw an exception, if session manager is not
	 *            found
	 * 
	 * @return a SessionManager
	 * 
	 */
	private static SessionManager getSessionManager(String displayName,
			boolean required) {
		if (required && !interfaces.containsKey(displayName))
			throw new RuntimeException("Required display \"" + displayName
					+ "\" not found");
		return (SessionManager) interfaces.get(displayName);
	}

	/**
	 * Create new meeting space
	 * 
	 * Create new display name for user and host name, create new session
	 * manager to handle this display.
	 * 
	 * @param userName
	 *            user name
	 * @param hostName
	 *            a host name
	 * @param props
	 *            a Properties
	 * 
	 * @return new display name
	 * 
	 */
	private String createNewMeetingSpace(String userName, String hostName,
			Properties props) throws Exception {
		String displayName = userName + ":" + hostName + "#" + Math.random();
		SessionManager session = new SessionManager(displayName, userName,
				props);

		interfaces.put(displayName, session);
		return displayName;
	}

	/**
	 * 判断服务端是否连接上
	 * 
	 * @param displayName
	 * @param userName
	 * @param password
	 * @param props
	 * @return
	 */
	public boolean isServerConnectedActive(String displayName, String userName,
			String password, final Vector propsVector) {
		Properties props = copyProperties(propsVector, applicationProperties);
		final SessionManager session = getSessionManagerWithValidation(
				displayName, userName, password, props);
		return session.isServerConnectedActive();
	}

	/**
	 * Create new meeting space.
	 * 
	 * Create a session manager and assign a display name to it.
	 * 
	 * @param userName
	 *            a user name of the owner of this meeting
	 * @param password
	 *            his password
	 * @param hostName
	 *            a name of the session server
	 * @param propsVector
	 *            additional properties
	 * 
	 * @return a display name for that session
	 */
	public String createMeeting(String userName, String password,
			String hostName, Vector propsVector) throws Exception {
		Properties props = copyProperties(propsVector, applicationProperties);
		authentificator.validatePassword(userName, password, props);

		String displayName = createNewMeetingSpace(userName, hostName, props);
		activityLogger.info("CREATE_MEETING:" + displayName
				+ ":Request to create new meeting space for user \"" + userName
				+ "\" and host \"" + hostName + "\"");
		return displayName;
	}

	/**
	 * Make connection to VNC server.
	 * 
	 * @param userName
	 *            user login name
	 * @param password
	 *            user password
	 * @param displayName
	 *            display name
	 * @param hostName
	 *            host of the VNC server to connect
	 * @param serverPort
	 *            port to connect
	 * @param serverPassword
	 *            password to use
	 * @param propsVector
	 *            a Vector with addtional properties
	 * 
	 * @return true
	 */
	public boolean connectToServer(String userName, String password,
			String displayName, final String hostName, final int serverPort,
			final String serverPassword, final Vector propsVector)
			throws IOException {
		activityLogger.info("CONNECT_TO_SERVER:" + displayName
				+ ":Request to connect to VNC server \"" + hostName
				+ "\", port " + serverPort);
		logger.debug("Request to connect to server \"" + hostName + ":"
				+ serverPort + "\"  \"" + displayName + "\".");
		try {
			Properties props = copyProperties(propsVector,
					applicationProperties);
			SessionManager session = getSessionManagerWithValidation(
					displayName, userName, password, props);

			session.shutdownServerInterface(props);

			return session.connectToServer(hostName, serverPort,
					serverPassword, props);
		} catch (RuntimeException e) {
			errorLogger.error("Exception in connectToServer()", e);
			throw e;
		}
	}

	/**
	 * Listen for connections from VNC clients
	 * 
	 * @param userName
	 *            user login name
	 * @param password
	 *            user password
	 * @param displayName
	 *            display name
	 * @param clientFullAccessPassword
	 *            password for VNC clients with full access
	 * @param clientViewOnlyPassword
	 *            password for VNC clients with viewonly access
	 * @param propsVector
	 *            a Vector with addtional properties
	 * 
	 * @return assigned port number
	 */
	public int listenForClients(String userName, String password,
			String displayName, final String clientFullAccessPassword,
			final String clientViewOnlyPassword, final Vector propsVector)
			throws IOException {
		logger.debug("Request to create clients listener for \"" + displayName
				+ "\".");
		try {
			final Properties props = copyProperties(propsVector,
					applicationProperties);
			final SessionManager session = getSessionManagerWithValidation(
					displayName, userName, password, props);

			session.shutdownClientInterfaces(props);

			final int clientPort = PortManager
					.getFreePortForClientConnection(displayName);

			activityLogger
					.info("LISTEN_FOR_CLIENTS:"
							+ displayName
							+ ":Request to listen for connections from VNC clients. Chosen port: "
							+ clientPort + ".");

			session.listenForClients(clientPort, clientFullAccessPassword,
					clientViewOnlyPassword, props);
			return clientPort;
		} catch (RuntimeException e) {
			errorLogger.error("Exception in listenForClients()", e);
			throw e;
		} catch (IOException e) {
			errorLogger.error("Exception in listenForClients()", e);
			throw e;
		}
	}

	/**
	 * Listen for server connection(s)
	 * 
	 * @param userName
	 *            user name
	 * @param password
	 *            use password
	 * @param displayName
	 *            display name
	 * @param serverPassword
	 *            password to use
	 * @param propsVector
	 *            a Vector with additional properties
	 * 
	 * @return assigned port to connect
	 */
	public int listenForServer(String userName, String password,
			String displayName, final String serverPassword,
			final Vector propsVector) throws IOException {
		logger.debug("Request to create server listener for \"" + displayName
				+ "\".");
		try {
			final Properties props = copyProperties(propsVector,
					applicationProperties);
			final SessionManager session = getSessionManagerWithValidation(
					displayName, userName, password, props);

			session.shutdownServerInterface(props);

			final int serverPort = PortManager
					.getFreePortForServerConnection(displayName);

			activityLogger
					.info("LISTEN_FOR_SERVER:"
							+ displayName
							+ ":Request to listen for connection from VNC server. Chosen port: "
							+ serverPort + ".");

			session.listenForServer(serverPort, serverPassword, props);

			return serverPort;
		} catch (RuntimeException e) {
			errorLogger.error("Exception in listenForServer()", e);
			throw e;
		} catch (IOException e) {
			errorLogger.error("Exception in listenForServer()", e);
			throw e;
		}
	}

	/**
	 * Return session manager for given display name
	 * 
	 * Validate user rights - only meeting creator can manage meeting.
	 */
	private SessionManager getSessionManagerWithValidation(String displayName,
			String userName, String password, final Properties props)
			throws RuntimeException {
		final SessionManager session = getSessionManager(displayName, true);

		authentificator.validatePassword(userName, password, props);

		if (!userName.equals(session.getOwnerName()))
			throw new RuntimeException(
					"User name and meeting owner name do not match!");

		return session;
	}

	/**
	 * Shutdown meeting
	 * 
	 * Stop server and client interfaces, remove meeting from list of active
	 * meetings.
	 */
	public boolean shutdownMeeting(String userName, String password,
			String displayName, Vector propsVector) {
		activityLogger.info("SHUTDOWN_MEETING:" + displayName
				+ ":Request to shutdown meeting.");
		logger
				.debug("Request to shutdown meeting for \"" + displayName
						+ "\".");
		try {
			Properties props = copyProperties(propsVector,
					applicationProperties);
			final SessionManager session = getSessionManagerWithValidation(
					displayName, userName, password, props);

			session.shutdown(props);

			interfaces.remove(displayName);

			return true;
		} catch (RuntimeException e) {
			errorLogger.error("Exception in shutdownMeeting()", e);
			throw e;
		}
	}

	/**
	 * Shutdown only server interface of the meeting
	 */
	public boolean shutdownServerInterface(String userName, String password,
			String displayName, Vector propsVector) {
		activityLogger.info("SHUTDOWN_SERVER_INTERFACE:" + displayName
				+ ":Request to shutdown server interface of the meeting.");
		logger.debug("Request to shutdown server interface for \""
				+ displayName + "\".");
		try {
			Properties props = copyProperties(propsVector,
					applicationProperties);
			final SessionManager session = getSessionManagerWithValidation(
					displayName, userName, password, props);

			return session.shutdownServerInterface(props);
		} catch (RuntimeException e) {
			errorLogger.error("Exception in shutdownServerInterface()", e);
			throw e;
		}
	}

	/**
	 * Shutdown only client interfaces of the meeting
	 */
	public boolean shutdownClientInterface(String userName, String password,
			String displayName, Vector propsVector) {
		activityLogger.info("SHUTDOWN_CLIENT_INTERFACE:" + displayName
				+ ":Request to shutdown client interface of the meeting.");
		logger.debug("Request to shutdown client interface for \""
				+ displayName + "\".");
		try {
			Properties props = copyProperties(propsVector,
					applicationProperties);
			final SessionManager session = getSessionManagerWithValidation(
					displayName, userName, password, props);

			return session.shutdownClientInterfaces(props);
		} catch (RuntimeException e) {
			errorLogger.error("Exception in shutdownClientInterface()", e);
			throw e;
		}
	}

	/**
	 * Create list of properties from vector with pairs.
	 * 
	 * Use application properties as parent properties list.
	 * 
	 * @param propsVector
	 *            vector with pairs KEY VALUE
	 * @param parentProperties
	 *            parent properties list
	 * 
	 * @return a new properties list
	 * 
	 */
	private Properties copyProperties(Vector propsVector,
			Properties parentProperties) {
		Properties props = new Properties(parentProperties);
		if ((propsVector.size() & 1) == 1)
			throw new RuntimeException(
					"Odd number of elements in vector of properties");

		for (int i = 0; i < propsVector.size(); i += 2)
			props.put(propsVector.elementAt(i), propsVector.elementAt(i + 1));

		return props;
	}

	/**
	 * Shutdown whole server (via System.exit())
	 * 
	 * @param userName
	 *            user name (must be "admin").
	 * @param password
	 *            user password
	 * @param propsVector
	 *            a Vector with additional properties
	 * 
	 * @return true
	 * 
	 */
	public boolean shutdownServer(String userName, String password,
			Vector propsVector) {
		activityLogger.info("SHUTDOWN_SERVER:" + userName
				+ "::Request to shutdown server.");
		logger.debug("Request to shutdown server from \"" + userName + "\".");
		if (!userName.equals("admin"))
			throw new RuntimeException(
					"Only admin can shutdown this VNC proxy server.");
		Properties props = copyProperties(propsVector, applicationProperties);
		authentificator.validatePassword(userName, password, props);

		new Thread(new Runnable() {
			public void run() {
				try {
					System.out.println("Server will shutdowned in 2 seconds.");
					Thread.sleep(2000);
				} catch (InterruptedException e) {
				}

				shutdownServer();

			}
		}).start();
		return true;
	}

	/**
	 * Execute System.exit()
	 */
	private void shutdownServer() {
		logger.info("Shutdown.");
		System.exit(0);
	}

}
