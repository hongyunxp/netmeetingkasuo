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
		logger.info(" ============= ���ݿ��ʼ��...");

		// ������ݿ��ļ�����·��
		String dbPath = AppConfigure.db_path;
		if (!dbPath.endsWith("/"))
			dbPath = dbPath + "/";
		if (StringUtils.isEmpty(dbPath)) {
			logger.error(" ============= �޷���� hsqldb.path·��");
			return;
		}
		logger.info(" ============= ���ݿ�·����:" + dbPath);

		// ���ݿ��ļ���
		String dbName = AppConfigure.db_name;
		if (StringUtils.isEmpty(dbName)) {
			logger.error(" ============= �޷����hsqldb.dbName���ݿ�����");
			return;
		}
		logger.info(" ============= ���ݿ�������:" + dbName);

		// ���ݿ���ʶ˿�
		int dbPort = -1;
		try {
			dbPort = Integer.parseInt(AppConfigure.db_port);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		logger.info(" ============= ���ݿ�˿���:" + dbPort);

		// �������ݿ�
		startServer(dbPath, dbName, dbPort);
	}

	/**
	 * 
	 * �������ݿ�
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
		logger.info(" ============= ���ݿ������ɹ�...");

		// �ȴ�Server����
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void stop() {
		logger.info(" ============= ���ݿ�����ֹͣ...");
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
			logger.info(" ============= ���ݿ��Ѿ�ֹͣ...");
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
