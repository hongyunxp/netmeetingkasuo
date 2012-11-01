package com.meeting.service;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.meeting.utils.AppConfigure;

public class Red5Service {

	public static final String KEY_START_RED5 = "KEY_START_RED5";
	public static final String KEY_STOP_RED5 = "KEY_STOP_RED5";

	public static final String RED5_HOME = AppConfigure.RED5;
	public static final String RED5_START_MAINCLASS = "org.red5.server.Bootstrap";
	public static final String JYTHON_OPTS = "-Dpython.home=lib";
	public static final String LOGGING_OPTS = "-Dlogback.ContextSelector=org.red5.logging.LoggingContextSelector -Dcatalina.useNaming=true";
	public static final String SECURITY_OPTS = "-Djava.security.debug=failure";

	public static final String JAVA_OPTS = "-Djavax.net.ssl.keyStore=\""
			+ RED5_HOME
			+ "/conf/keystore.jmx\" -Djavax.net.ssl.keyStorePassword=password";
	public static final String RED5_SHUTDOWN_MAINCLASS = "org.red5.server.Shutdown";
	public static final String RED5_JMX_PORT = "9999";
	public static final String RED5_JMX_USER = "red5user";
	public static final String RED5_JMX_ACTION = "changeme";

	private static Logger logger = Logger.getLogger(Red5Service.class);

	/**
	 * 启动RED5服务
	 * 
	 * @param port
	 * @return
	 */
	public static HashMap<String, Object> startRed5Service() {
		logger.info("启动RED5服务");
		System.setProperty("red5.root", AppConfigure.RED5);
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(AppConfigure.RED5 + "/" + "boot.jar;");
		sBuffer.append(AppConfigure.RED5 + "/" + "conf");

		String[] argv = new String[] { "java", JYTHON_OPTS, LOGGING_OPTS,
				SECURITY_OPTS, "-cp", sBuffer.toString(), RED5_START_MAINCLASS,
				AppConfigure.RED5 };

		return ExecuteService.executeScript(KEY_START_RED5, argv);

	}

	/**
	 * 停止RED5服务
	 */
	public static HashMap<String, Object> stopRed5Service() {
		logger.info("停止RED5服务");
		System.setProperty("red5.root", AppConfigure.RED5);
		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(AppConfigure.RED5 + "/" + "boot.jar;");
		sBuffer.append(AppConfigure.RED5 + "/" + "conf");

		String[] argv = new String[] { "java", JYTHON_OPTS, LOGGING_OPTS,
				SECURITY_OPTS, JAVA_OPTS, "-cp", sBuffer.toString(),
				RED5_SHUTDOWN_MAINCLASS, RED5_JMX_PORT, RED5_JMX_USER,
				RED5_JMX_ACTION };

		return ExecuteService.executeScript(KEY_STOP_RED5, argv);
	}

}
