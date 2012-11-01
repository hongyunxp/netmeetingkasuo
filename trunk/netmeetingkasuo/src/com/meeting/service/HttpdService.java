package com.meeting.service;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.meeting.utils.AppConfigure;

public class HttpdService {

	private static Logger logger = Logger.getLogger(HttpdService.class);

	private static String HTTPD_INSTALL = "install";
	private static String HTTPD_UNINSTALL = "uninstall";
	private static String HTTPD_START = "start";
	private static String HTTPD_STOP = "stop";
	private static String HTTPD_RESTART = "restart";

	/**
	 * ��װhttpd����
	 * 
	 * @return
	 */
	public static HashMap<String, Object> install() {
		logger.info("��װhttpd����");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"install", "-n", AppConfigure.apache_service };
		return ExecuteService.executeScript(HTTPD_INSTALL, argv);
	}

	/**
	 * ж��httpd����
	 * 
	 * @return
	 */
	public static HashMap<String, Object> uninstall() {
		logger.info("ж��httpd����");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"uninstall", "-n", AppConfigure.apache_service };
		return ExecuteService.executeScript(HTTPD_UNINSTALL, argv);
	}

	/**
	 * ����httpd����
	 * 
	 * @return
	 */
	public static HashMap<String, Object> start() {
		logger.info("����httpd����");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"start", "-n", AppConfigure.apache_service };
		return ExecuteService.executeScript(HTTPD_START, argv);
	}

	/**
	 * ֹͣhttpd����
	 * 
	 * @return
	 */
	public static HashMap<String, Object> stop() {
		logger.info("Apache����������ֹͣ...");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"stop", "-n", AppConfigure.apache_service };
		HashMap<String, Object> map = ExecuteService.executeScript(HTTPD_STOP,
				argv);
		logger.info("Apache������ֹͣ���...");
		return map;
	}

	/**
	 * ����httpd����
	 * 
	 * @return
	 */
	public static HashMap<String, Object> restart() {
		logger.info("����httpd����");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"restart", "-n", AppConfigure.apache_service };
		return ExecuteService.executeScript(HTTPD_RESTART, argv);
	}

	/**
	 * ���80�˿��Ƿ����
	 * 
	 * @return
	 */
	public boolean existPort() {
		return false;
	}

}
