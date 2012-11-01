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
	 * 安装httpd服务
	 * 
	 * @return
	 */
	public static HashMap<String, Object> install() {
		logger.info("安装httpd服务");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"install", "-n", AppConfigure.apache_service };
		return ExecuteService.executeScript(HTTPD_INSTALL, argv);
	}

	/**
	 * 卸载httpd服务
	 * 
	 * @return
	 */
	public static HashMap<String, Object> uninstall() {
		logger.info("卸载httpd服务");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"uninstall", "-n", AppConfigure.apache_service };
		return ExecuteService.executeScript(HTTPD_UNINSTALL, argv);
	}

	/**
	 * 启动httpd服务
	 * 
	 * @return
	 */
	public static HashMap<String, Object> start() {
		logger.info("启动httpd服务");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"start", "-n", AppConfigure.apache_service };
		return ExecuteService.executeScript(HTTPD_START, argv);
	}

	/**
	 * 停止httpd服务
	 * 
	 * @return
	 */
	public static HashMap<String, Object> stop() {
		logger.info("Apache服务器正在停止...");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"stop", "-n", AppConfigure.apache_service };
		HashMap<String, Object> map = ExecuteService.executeScript(HTTPD_STOP,
				argv);
		logger.info("Apache服务器停止完成...");
		return map;
	}

	/**
	 * 重启httpd服务
	 * 
	 * @return
	 */
	public static HashMap<String, Object> restart() {
		logger.info("重启httpd服务");
		String[] argv = new String[] { AppConfigure.apache_httpdpath, "-k",
				"restart", "-n", AppConfigure.apache_service };
		return ExecuteService.executeScript(HTTPD_RESTART, argv);
	}

	/**
	 * 检测80端口是否存在
	 * 
	 * @return
	 */
	public boolean existPort() {
		return false;
	}

}
