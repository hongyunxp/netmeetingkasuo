package com.meeting.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hsqldb.Server;

import com.meeting.utils.AppConfigure;

public class HSqlService {

	private static Logger logger = Logger.getLogger(HSqlService.class);
	
	public static boolean RUN = false;

	public static void start() {
		logger.info(" ============= 数据库初始化...");

		// 获得数据库文件访问路径
		String dbPath = AppConfigure.db_path;
		if (!dbPath.endsWith("/"))
			dbPath = dbPath + "/";
		if (StringUtils.isEmpty(dbPath)) {
			logger.error(" ============= 无法获得 hsqldb.path路径");
			return;
		}
		logger.info(" ============= 数据库路径是:" + dbPath);

		// 数据库文件名
		String dbName = AppConfigure.db_name;
		if (StringUtils.isEmpty(dbName)) {
			logger.error(" ============= 无法获得hsqldb.dbName数据库名称");
			return;
		}
		logger.info(" ============= 数据库名称是:" + dbName);

		// 数据库访问端口
		int dbPort = -1;
		try {
			dbPort = Integer.parseInt(AppConfigure.db_port);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		logger.info(" ============= 数据库端口是:" + dbPort);

		// 启动数据库
		startServer(dbPath, dbName, dbPort);
	}

	/**
	 * 
	 * 启动数据库
	 * 
	 * @param dbPath
	 * @param dbName
	 * @param port
	 */
	private static void startServer(String dbPath, String dbName, int port) {
		Server server = new Server();
		server.setDatabaseName(0, dbName);
		server.setDatabasePath(0, dbPath + dbName);
		if (port != -1)
			server.setPort(port);
		server.setSilent(true);
		server.setTrace(true);
		server.start();
		RUN = true;
		logger.info(" ============= 数据库启动成功...");

		// 等待Server启动
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		logger.info(" ============= 数据库正在停止...");
		Connection conn = null;
		String dbName = AppConfigure.db_name;
		String dbPort = AppConfigure.db_port;
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost:"
					+ dbPort + "/" + dbName, "sa", "");
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("SHUTDOWN;");
			RUN = false;
			logger.info(" ============= 数据库已经停止...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) throws InterruptedException {
		start();

		Thread.sleep(10000);

		//stop();
	}
}
