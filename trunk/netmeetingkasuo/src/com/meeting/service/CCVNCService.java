package com.meeting.service;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ccvnc.Controller;
import com.meeting.utils.AppConfigure;

public class CCVNCService {

	private static CCVNCService instance;

	public static final String KEY_START_CCVNC = "KEY_START_CCVNC";

	private static Logger logger = Logger.getLogger(CCVNCService.class);

	private CCVNCService() {
	}

	/**
	 * 单例
	 * 
	 * @return
	 */
	public static synchronized CCVNCService getInstance() {
		if (instance == null) {
			instance = new CCVNCService();
		}
		return instance;
	}

	/**
	 * 启动CCVNC服务
	 * 
	 * @param port
	 * @return
	 */
	public HashMap<String, Object> startCcvncService(String port) {
		String ccvncClassPathFolder = AppConfigure.ccvnc_lib_path;

		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(ccvncClassPathFolder + "/xmlrpc-1.2-b1.jar").append(";");
		sBuffer.append(ccvncClassPathFolder + "/log4j-1.2.15.jar").append(";");
		sBuffer.append(ccvncClassPathFolder + "/junit-4.6.jar").append(";");

		String[] argv = new String[] { "java", "-classpath",
				sBuffer.toString(), "com.ccvnc.Controller",
				"SimpleAuthentificator.passwordsFileName",
				AppConfigure.ccvnc_password, "webserver.port", port };

		return ExecuteService.executeScript(KEY_START_CCVNC, argv);

	}

	/**
	 * 启动CCVNC服务
	 * 
	 * @param port
	 */
	public void startCcvncService(int port) {
		logger.info("XML RPC服务正在启动...");
		Properties properties = new Properties();
		properties.setProperty("SimpleAuthentificator.passwordsFileName",
				AppConfigure.ccvnc_password);
		Controller.startController(port, properties);
		logger.info("XML RPC服务启动成功，监听端口：" + port);
	}

}
