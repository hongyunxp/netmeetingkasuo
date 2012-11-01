package com.meeting.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.meeting.utils.AppConfigure;

public class HSQLConnection {

	/**
	 * 获取数据库连接
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = null;
		String dbName = AppConfigure.db_name;
		String dbPort = AppConfigure.db_port;
		String dbUrl = "jdbc:hsqldb:hsql://localhost:" + dbPort + "/" + dbName;
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			conn = DriverManager.getConnection(dbUrl, "sa", "");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return conn;
	}

	// 关闭连接
	public static void close(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 关闭statement
	public static void close(PreparedStatement pStmt) {
		try {
			if (pStmt != null) {
				pStmt.close();
				pStmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 关闭结果集
	public static void close(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
