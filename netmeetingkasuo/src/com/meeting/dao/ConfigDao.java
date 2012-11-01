package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.model.ConfigModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.DateUtils;
import com.meeting.utils.StackTraceUtil;

public class ConfigDao {

	private static ConfigDao instance = null;

	private static Logger logger = Logger.getLogger(ConfigDao.class);

	private static final String ADD_CONFIG = "INSERT INTO E_CONFIG(USERID,CONFIGNAME,CONFIGVALUE,CONFIGTIME) VALUES(?,?,?,?)";
	private static final String DEL_CONFIG = "DELETE FROM E_CONFIG WHERE USERID = ? AND CONFIGNAME = ?";
	private static final String MOD_CONFIG = "UPDATE E_CONFIG SET CONFIGVALUE = ?,CONFIGTIME = ? WHERE USERID = ? AND CONFIGNAME = ?";
	private static final String GET_CONFIG = "SELECT * FROM E_CONFIG WHERE USERID = ? AND CONFIGNAME = ?";
	private static final String GET_CONFIGLIST = "SELECT * FROM E_CONFIG ORDER BY CONFIGTIME DESC";
	private static final String GET_USERCONFIGLIST = "SELECT * FROM E_CONFIG WHERE USERID = ? ORDER BY CONFIGTIME DESC";

	public static ConfigDao getInstance() {
		if (instance == null) {
			instance = new ConfigDao();
		}
		return instance;
	}

	/**
	 * 添加配置
	 * 
	 * @param model
	 * @return
	 */
	public int addConfig(ConfigModel model) {
		int ret = 0;
		if (existConfig(model.getName()))
			return ret;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_CONFIG);
			psmt.setString(1, String.valueOf(AppConfigure.USER_ROLE_ADMIN));
			psmt.setString(2, model.getName());
			psmt.setString(3, model.getValue());
			psmt.setString(4, model.getTime());
			ret = psmt.executeUpdate();
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(conn);
		}
		return ret;
	}

	/**
	 * 删除配置
	 * 
	 * @param model
	 * @return
	 */
	public int delConfig(String name) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_CONFIG);
			psmt.setString(1, String.valueOf(AppConfigure.USER_ROLE_ADMIN));
			psmt.setString(2, name);
			ret = psmt.executeUpdate();
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(conn);
		}
		return ret;
	}

	/**
	 * 保存配置
	 * 
	 * @param model
	 * @return
	 */
	public int saveConfig(ConfigModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			if (!existConfig(model.getName())) {
				ret = addConfig(model);
			} else {
				conn = HSQLConnection.getConnection();
				psmt = conn.prepareStatement(MOD_CONFIG);
				psmt.setString(1, model.getValue());
				psmt.setString(2, model.getTime());
				psmt.setString(3, model.getUserid());
				psmt.setString(4, model.getName());
				ret = psmt.executeUpdate();
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(conn);
		}
		return ret;
	}

	/**
	 * 获取配置
	 * 
	 * @param key
	 * @return
	 */
	public ConfigModel getConfig(String name) {
		ConfigModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_CONFIG);
			psmt.setString(1, String.valueOf(AppConfigure.USER_ROLE_ADMIN));
			psmt.setString(2, name);
			rs = psmt.executeQuery();
			if (rs.next()) {
				model = read(rs);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return model;
	}

	/**
	 * 获取配置，如果没有则使用默认值
	 * 
	 * @param name
	 * @param defaultval
	 * @return
	 */
	public ConfigModel getConfig(String name, String defaultval) {
		ConfigModel model = getConfig(name);
		if (model == null) {
			model = new ConfigModel();
			model.setUserid(String.valueOf(AppConfigure.USER_ROLE_ADMIN));
			model.setName(name);
			model.setValue(defaultval);
			model.setTime(DateUtils.getCurrentTime());
		}
		return model;
	}

	/**
	 * 返回配置列表
	 * 
	 * @return
	 */
	public List<ConfigModel> getConfigList() {
		List<ConfigModel> configlist = new ArrayList<ConfigModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_CONFIGLIST);
			rs = psmt.executeQuery();
			while (rs.next()) {
				ConfigModel model = read(rs);
				configlist.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return configlist;
	}

	/**
	 * 判断是否存在配置
	 * 
	 * @param name
	 * @return
	 */
	public boolean existConfig(String name) {
		return getConfig(name) == null ? false : true;
	}

	/**
	 * 添加某个用户的配置
	 * 
	 * @param model
	 * @return
	 */
	public int addUserConfig(ConfigModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_CONFIG);
			psmt.setString(1, model.getUserid());
			psmt.setString(2, model.getName());
			psmt.setString(3, model.getValue());
			psmt.setString(4, model.getTime());
			ret = psmt.executeUpdate();
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(conn);
		}
		return ret;
	}

	/**
	 * 删除某个用户的配置
	 * 
	 * @param model
	 * @return
	 */
	public int delUserConfig(String name, String userid) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_CONFIG);
			psmt.setString(1, userid);
			psmt.setString(2, name);
			ret = psmt.executeUpdate();
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(conn);
		}
		return ret;
	}

	/**
	 * 修改某个用户的配置
	 * 
	 * @param model
	 * @return
	 */
	public int modUserConfig(ConfigModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(MOD_CONFIG);
			psmt.setString(1, model.getValue());
			psmt.setString(2, model.getTime());
			psmt.setString(3, model.getUserid());
			psmt.setString(4, model.getName());
			ret = psmt.executeUpdate();
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(conn);
		}
		return ret;
	}

	/**
	 * 获取某个用户的配置
	 * 
	 * @param key
	 * @return
	 */
	public ConfigModel getUserConfig(String name, String userid) {
		ConfigModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_CONFIG);
			psmt.setString(1, userid);
			psmt.setString(2, name);
			rs = psmt.executeQuery();
			if (rs.next()) {
				model = read(rs);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return model;
	}

	/**
	 * 获取某个用户配置，如果没有则使用默认值
	 * 
	 * @param name
	 * @param defaultval
	 * @return
	 */
	public ConfigModel getUserConfig(String name, String userid,
			String defaultval) {
		ConfigModel model = getConfig(name);
		if (model == null) {
			model = new ConfigModel();
			model.setUserid(userid);
			model.setName(name);
			model.setValue(defaultval);
			model.setTime(DateUtils.getCurrentTime());
		}
		return model;
	}

	/**
	 * 返回某个用户配置列表
	 * 
	 * @return
	 */
	public List<ConfigModel> getUserConfigList(String userid) {
		List<ConfigModel> configlist = new ArrayList<ConfigModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_USERCONFIGLIST);
			psmt.setString(1, userid);
			rs = psmt.executeQuery();
			while (rs.next()) {
				ConfigModel model = read(rs);
				configlist.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return configlist;
	}

	/**
	 * 判断是否存在配置
	 * 
	 * @param name
	 * @return
	 */
	public boolean existUserConfig(String name, String userid) {
		return getUserConfig(name, userid) == null ? false : true;
	}

	/**
	 * 读取数据结果集
	 * 
	 * @param rs
	 * @return
	 */
	public ConfigModel read(ResultSet rs) {
		ConfigModel model = null;
		try {
			String userid = rs.getString("USERID");
			String name = rs.getString("CONFIGNAME");
			String value = rs.getString("CONFIGVALUE");
			String time = rs.getString("CONFIGTIME");
			model = new ConfigModel();
			model.setUserid(userid);
			model.setName(name);
			model.setValue(value);
			model.setTime(time);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		return model;
	}

}
