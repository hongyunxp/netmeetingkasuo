package com.ccvnc.caller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import com.ccvnc.Controller;

/**
 * Caller - a wrapper class to call remote controller methods via XMLRPC
 * protocol.
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class Caller {
	private static Logger logger = Logger.getLogger(Controller.class);
	private static XmlRpcClient xmlrpc;

	/**
	 * Just parse parameters and call method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		BasicConfigurator.configure();

		Properties props = initializeProperties(args);
		String action = getProperty(props, "action");

		if (action == null || args.length < 2) {
			System.out
					.println("Usage: java "
							+ Caller.class.getName()
							+ " PROPERTY_NAME PROPERTY_VALUE\n"
							+ " action      action to do"
							+ " url         URL to middlet controller\n"
							+ " username    your username on middlet\n"
							+ " password    your password on middlet\n"
							+ "\n"
							+ "\n"
							+ "    action new_meeting\n"
							+ " host                          VNC server host name (eg. \"homecomputer\")\n"
							+ " vncserver_password            password to authentificate middlet on VNC server\n"
							+ " vncclient_fullaccess_password password to allow VNC client full access to VNC server\n"
							+ " vncclient_viewonly_password   password to allow VNC client view only access to VNC server\n"
							+ "\n"
							+ "    action shutdown_meeting\n"
							+ " display     display name (to shutdown)\n"
							+ "\n"
							+ "\n"
							+ "    action create_meeting\n"
							+ " host        VNC server host name (eg. \"homecomputer\")\n"
							+ "\n"
							+ "    action listen_for_server\n"
							+ " vncserver_password            password to authentificate middlet on VNC server\n"
							+ "\n"
							+ "    action connect_to_server\n"
							+ " vncserver_host                hostname of the VNC server to connect\n"
							+ " vncserver_display             display number of the VNC server to connect\n"
							+ " vncserver_password            password to authentificate middlet on VNC server\n"
							+ "\n"
							+ "    action listen_for_clients\n"
							+ " vncclient_fullaccess_password password to allow VNC client full access to VNC server\n"
							+ " vncclient_viewonly_password   password to allow VNC client view only access to VNC server\n"
							+ "\n" + "    action shutdown_server_interface\n"
							+ " display     display name (to shutdown)\n"
							+ "\n" + "    action shutdown_client_interface\n"
							+ " display     display name (to shutdown)\n"
							+ "\n" + "");
			System.exit(1);
		}

		try {
			initialize(getProperty(props, "url", true));
		} catch (MalformedURLException e) {
			logger.fatal("Bad URL", e);
			return;
		}

		try {
			if (action.equalsIgnoreCase("listen_to_server_status"))
				listenToServerStatusAction(props);
			else if (action.equalsIgnoreCase("new_meeting"))
				newMeetingAction(props);
			else if (action.equalsIgnoreCase("shutdown_meeting"))
				shutdownMeetingAction(props);
			else if (action.equalsIgnoreCase("create_meeting"))
				createMeetingAction(props);
			else if (action.equalsIgnoreCase("listen_for_server"))
				listenForServerAction(props);
			else if (action.equalsIgnoreCase("connect_to_server"))
				connectToServerAction(props);
			else if (action.equalsIgnoreCase("listen_for_clients"))
				listentForClientsAction(props);
			else if (action.equalsIgnoreCase("shutdown_server_interface"))
				shutdownServerInterfaceAction(props);
			else if (action.equalsIgnoreCase("shutdown_client_interface"))
				shutdownClientInterfaceAction(props);
			else if (action.equalsIgnoreCase("shutdown_server"))
				shutdownServerAction(props);
			else
				logger.error("Unknown action: \"" + action + "\"!");

		} catch (IOException e) {
			logger.fatal("Problem with connection", e);
		} catch (XmlRpcException e) {
			logger.fatal("Problem on middlet side", e);
		}

	}

	/**
	 * Æô¶¯VNC´úÀí
	 * 
	 * @param p
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	public static String[] startVncProxy(Properties p) throws IOException,
			XmlRpcException {
		initialize(p.getProperty("url"));
		return newMeetingAction(p);
	}

	/**
	 * 
	 * @param p
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	public static void stopVncProxy(Properties p) throws IOException,
			XmlRpcException {
		initialize(p.getProperty("url"));
		shutdownMeetingAction(p);
	}

	/**
	 * ¼àÌý·þÎñ¶Ë¼àÌý×´Ì¬
	 * 
	 * @param props
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public static boolean listenToServerStatusAction(Properties props)
			throws IOException, XmlRpcException {
		String display = getProperty(props, "display", true);
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		return listenToServerStatus(display, username, password, props);
	}

	/**
	 * Convert array of string contains pairs KEY VALUE into list of properties
	 * 
	 * @param args
	 * @return
	 */
	private static Properties initializeProperties(String[] args) {
		Properties props = new Properties();
		for (int i = 0; i < (args.length & (~1)); i += 2)
			props.setProperty(args[i], args[i + 1]);
		return props;
	}

	/**
	 * Create new meeting and start server listener and client listener.
	 * 
	 * @param props
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static String[] newMeetingAction(Properties props)
			throws IOException, XmlRpcException {
		String userName = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		String host = getProperty(props, "host", true);

		String vncserver_password = getProperty(props, "vncserver_password");
		String vncclient_fullaccess_password = getProperty(props,
				"vncclient_fullaccess_password");
		String vncclient_viewonly_password = getProperty(props,
				"vncclient_viewonly_password");

		String displayName = createMeeting(userName, password, host, props);
		logger.info("DISPLAY_NAME=\"" + displayName + "\"");

		int serverPort = listenForServer(userName, password, displayName,
				vncserver_password, props);
		logger.info("SERVER_PORT=\"" + serverPort + "\"");

		int clientPort = listenForClients(userName, password, displayName,
				vncclient_fullaccess_password, vncclient_viewonly_password,
				props);
		logger.info("CLIENT_PORT=\"" + clientPort + "\"");
		return new String[] { serverPort + "", clientPort + "", displayName };
	}

	/**
	 * Setup a new meeting and tell it display name.
	 * 
	 * @param props
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static void createMeetingAction(Properties props)
			throws IOException, XmlRpcException {
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		String host = getProperty(props, "host", true);

		String displayName = createMeeting(username, password, host, props);
		if (displayName != null && displayName.length() != 0)
			logger.info("New meeting space created, display name: \""
					+ displayName + "\".");
		else
			logger.info("New meeting space was not created.");
	}

	/**
	 * Shutdown client interfaces
	 * 
	 * @param props
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static void shutdownClientInterfaceAction(Properties props)
			throws IOException, XmlRpcException {
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		String display = getProperty(props, "display", true);
		if (shutdownClientInterface(username, password, display, props))
			logger.info("Client interface shutdowned");
		else
			logger.info("Client interface was not shutdowned");
	}

	/**
	 * Shutdown server interface
	 * 
	 * @param props
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static void shutdownServerInterfaceAction(Properties props)
			throws IOException, XmlRpcException {
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		String display = getProperty(props, "display", true);
		if (shutdownServerInterface(username, password, display, props))
			logger.info("Server interface shutdowned");
		else
			logger.info("Server interface was not shutdowned");
	}

	/**
	 * Create listener of VNC viewer connections and tell it port number.
	 * 
	 * @param props
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static void listentForClientsAction(Properties props)
			throws IOException, XmlRpcException {
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		String display = getProperty(props, "display", true);
		String vncclient_fullaccess_password = getProperty(props,
				"vncclient_fullaccess_password");
		String vncclient_viewonly_password = getProperty(props,
				"vncclient_viewonly_password");

		int clientPort = listenForClients(username, password, display,
				vncclient_fullaccess_password, vncclient_viewonly_password,
				props);
		if (clientPort > 0)
			logger.info("Listening for client connections on port \""
					+ clientPort + "\"");
		else
			logger.info("Can't setup listener for client connection.");
	}

	/**
	 * Create listener for VNC server connection and tell it port number.
	 * 
	 * @param props
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static void listenForServerAction(Properties props)
			throws IOException, XmlRpcException {
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		String display = getProperty(props, "display", true);
		String vncserver_password = getProperty(props, "vncserver_password");

		int serverPort = listenForServer(username, password, display,
				vncserver_password, props);
		if (serverPort > 0)
			logger.info("Listening for server connection on \"" + serverPort
					+ "\".");
		else
			logger.info("Can't setup listener for server connection.");
	}

	/**
	 * 
	 * @param props
	 * @throws IOException
	 * @throws NumberFormatException
	 * @throws XmlRpcException
	 */
	private static void connectToServerAction(Properties props)
			throws IOException, NumberFormatException, XmlRpcException {
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		String display = getProperty(props, "display", true);
		String vncserver_host = getProperty(props, "vncserver_host", true);
		String vncserver_display = getProperty(props, "vncserver_display", true);
		String vncserver_password = getProperty(props, "vncserver_password");

		if (connectToServer(username, password, display, vncserver_host,
				Integer.parseInt(vncserver_display), vncserver_password, props))
			logger.info("Connected to VNC server.");
		else
			logger.info("Can't connect to VNC server.");
	}

	/**
	 * Connect to VNC server from proxy side.
	 * 
	 * @param userName
	 * @param password
	 * @param displayName
	 * @param vncserver_host
	 * @param vncserver_display
	 * @param vncserver_password
	 * @param props
	 * @return
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static boolean connectToServer(String userName, String password,
			String displayName, String vncserver_host, int vncserver_display,
			String vncserver_password, Properties props) throws IOException,
			XmlRpcException {
		Vector params = new Vector();
		params.addElement(userName);
		params.addElement(password);
		params.addElement(displayName);
		params.addElement(vncserver_host);
		params.addElement(new Integer(vncserver_display));
		params.addElement(vncserver_password);
		params.addElement(toVector(props));

		// this method returns an integer - port number
		Boolean result = (Boolean) xmlrpc.execute("connectToServer", params);
		return result.booleanValue();
	}

	/**
	 * Shutdown meeting.
	 * 
	 * @param props
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static void shutdownMeetingAction(Properties props)
			throws IOException, XmlRpcException {
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		String display = getProperty(props, "display", true);
		if (shutdownMeeting(username, password, display, props))
			logger.info("Meeting shutdowned.");
		else
			logger.info("Meeting was not shutdowned.");
	}

	/**
	 * Shutdown whole server.
	 * 
	 * @param props
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static void shutdownServerAction(Properties props)
			throws IOException, XmlRpcException {
		String username = getProperty(props, "username", true);
		String password = getProperty(props, "password", true);
		if (shutdownServer(username, password, props))
			logger.info("Server  shutdowned.");
		else
			logger.info("Server was not shutdowned.");
	}

	/**
	 * Shutdown whole server.
	 * 
	 * @param userName
	 * @param password
	 * @param props
	 * @return
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static boolean shutdownServer(String userName, String password,
			Properties props) throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.addElement(userName);
		params.addElement(password);
		params.addElement(toVector(props));

		// this method returns a boolean
		Boolean result = (Boolean) xmlrpc.execute("shutdownServer", params);
		return result.booleanValue();
	}

	/**
	 * Shutdown client interface.
	 * 
	 * @param userName
	 * @param password
	 * @param displayName
	 * @param props
	 * @return
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static boolean shutdownClientInterface(String userName,
			String password, String displayName, Properties props)
			throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.addElement(userName);
		params.addElement(password);
		params.addElement(displayName);
		params.addElement(toVector(props));

		// this method returns a boolean
		Boolean result = (Boolean) xmlrpc.execute("shutdownClientInterface",
				params);
		return result.booleanValue();
	}

	/**
	 * ÅÐ¶Ï·þÎñ¶ËÊÇ·ñ¼àÌý
	 * 
	 * @param userName
	 * @param password
	 * @param displayName
	 * @param props
	 * @return
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static boolean listenToServerStatus(String displayName,
			String userName, String password, Properties props)
			throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.addElement(displayName);
		params.addElement(userName);
		params.addElement(password);
		params.addElement(toVector(props));

		// this method returns a boolean
		Boolean result = (Boolean) xmlrpc.execute("isServerConnectedActive",
				params);
		return result.booleanValue();
	}

	/**
	 * Shutdown server interface.
	 * 
	 * @param userName
	 * @param password
	 * @param displayName
	 * @param props
	 * @return
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	private static boolean shutdownServerInterface(String userName,
			String password, String displayName, Properties props)
			throws IOException, XmlRpcException {
		Vector params = new Vector();
		params.addElement(userName);
		params.addElement(password);
		params.addElement(displayName);
		params.addElement(toVector(props));

		// this method returns a boolean
		Boolean result = (Boolean) xmlrpc.execute("shutdownServerInterface",
				params);
		return result.booleanValue();
	}

	/**
	 * Return property value.
	 * 
	 * @param props
	 * @param propName
	 * @return property value or empty string, if property not exists
	 */
	private static String getProperty(Properties props, String propName) {
		String prop = (String) props.remove(propName);
		if (prop == null)
			return "";
		return prop;
	}

	/**
	 * Return property value or throw exception, if property not exists
	 * 
	 * @param props
	 * @param propName
	 * @param required
	 * @return
	 */
	private static String getProperty(Properties props, String propName,
			boolean required) {
		String prop = (String) props.remove(propName);
		if (required && (prop == null || prop.length() == 0))
			throw new RuntimeException("Property \"" + propName
					+ "\" is required!");
		return prop;
	}

	/**
	 * Convert list of properties into Vector
	 * 
	 * @return vector with pairs KEY VALUE
	 */
	private static Vector toVector(Properties props) {
		Vector vector = new Vector(props.size() * 2);
		for (Enumeration e = props.keys(); e.hasMoreElements();) {
			String key = (String) e.nextElement();
			String value = props.getProperty(key);
			vector.addElement(key);
			vector.addElement(value);
		}

		return vector;
	}

	/**
	 * Create a new XmlRpcClient
	 * 
	 * @param url
	 * @throws MalformedURLException
	 */
	public static void initialize(String url) throws MalformedURLException {
		xmlrpc = new XmlRpcClient(url);
	}

	/**
	 * Shutdown meeting
	 * 
	 * @param userName
	 * @param password
	 * @param displayName
	 * @param props
	 * @return
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public static boolean shutdownMeeting(String userName, String password,
			String displayName, Properties props) throws XmlRpcException,
			IOException {
		Vector params = new Vector();
		params.addElement(userName);
		params.addElement(password);
		params.addElement(displayName);
		params.addElement(toVector(props));

		// this method returns a boolean
		Boolean result = (Boolean) xmlrpc.execute("shutdownMeeting", params);
		return result.booleanValue();
	}

	/**
	 * Start listener of server connection
	 * 
	 * @param userName
	 * @param password
	 * @param displayName
	 * @param vncserver_password
	 * @param props
	 * @return
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public static int listenForServer(String userName, String password,
			String displayName, String vncserver_password, Properties props)
			throws XmlRpcException, IOException {
		Vector params = new Vector();
		params.addElement(userName);
		params.addElement(password);
		params.addElement(displayName);
		params.addElement(vncserver_password);
		params.addElement(toVector(props));

		// this method returns a boolean
		Integer result = (Integer) xmlrpc.execute("listenForServer", params);
		return result.intValue();
	}

	/**
	 * Start listener of client connection
	 * 
	 * @param userName
	 * @param password
	 * @param displayName
	 * @param fullAccessPassword
	 * @param viewOnlyAccessPassword
	 * @param props
	 * @return
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public static int listenForClients(String userName, String password,
			String displayName, String fullAccessPassword,
			String viewOnlyAccessPassword, Properties props)
			throws XmlRpcException, IOException {
		Vector params = new Vector();
		params.addElement(userName);
		params.addElement(password);
		params.addElement(displayName);
		params.addElement(fullAccessPassword);
		params.addElement(viewOnlyAccessPassword);
		params.addElement(toVector(props));

		// this method returns a boolean
		Integer result = (Integer) xmlrpc.execute("listenForClients", params);
		return result.intValue();
	}

	/**
	 * Create new meetig and return it display name
	 * 
	 * @param userName
	 * @param password
	 * @param host
	 * @param props
	 * @return
	 * @throws XmlRpcException
	 * @throws IOException
	 */
	public static String createMeeting(String userName, String password,
			String host, Properties props) throws XmlRpcException, IOException {
		Vector params = new Vector();
		params.addElement(userName);
		params.addElement(password);
		params.addElement(host);
		params.addElement(toVector(props));

		// this method returns a string
		String displayName = (String) xmlrpc.execute("createMeeting", params);
		return displayName;
	}

}
