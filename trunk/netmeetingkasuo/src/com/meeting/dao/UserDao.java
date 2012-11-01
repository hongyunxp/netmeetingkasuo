package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.model.UserModel;
import com.meeting.utils.StackTraceUtil;

public class UserDao {

	private static UserDao instance = null;

	private static Logger logger = Logger.getLogger(UserDao.class);

	private static final String ADD_USER = "INSERT INTO E_USER(USERID,PASSWORD,SESSIONID,USERNAME,USERROLE,USEREMAIL,USERPIC,USERCREATE,USERUPDATE) VALUES(?,?,?,?,?,?,?,?,?)";
	private static final String DEL_USER = "DELETE FROM E_USER WHERE USERID = ?";
	private static final String MOD_USER = "UPDATE E_USER SET PASSWORD=?,SESSIONID=?,USERNAME=?,USERROLE=?,USEREMAIL=?,USERPIC=?,USERUPDATE=? WHERE USERID = ?";
	private static final String GET_USER = "SELECT * FROM E_USER WHERE USERID = ?";
	private static final String GET_USER_PWD = "SELECT * FROM E_USER WHERE USERID = ? AND PASSWORD= ?";
	private static final String GET_USERLIST = "SELECT * FROM E_USER ORDER BY USERCREATE DESC";
	private static final String GET_USERLIST_USERNAME = "SELECT * FROM E_USER WHERE USERNAME LIKE ? ORDER BY USERCREATE DESC";
	private static final String SEARCH_USERLIST = "SELECT * FROM E_USER WHERE (USERID = ? OR '' = ?) AND (USERNAME = ? OR '' = ?) AND (USEREMAIL = ? OR '' = ?) AND (USERROLE = ? OR -1 = ?) AND ((USERCREATE BETWEEN ? AND ?) OR ('' = ? AND '' = ?)) ORDER BY USERCREATE DESC";

	public static UserDao getInstance() {
		if (instance == null) {
			instance = new UserDao();
		}
		return instance;
	}

	/**
	 * 添加用户
	 * 
	 * @param model
	 * @return
	 */
	public int addUser(UserModel model) {
		int ret = 0;
		if (existUser(model.getUsercode()))
			return ret;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_USER);
			psmt.setString(1, model.getUsercode());
			psmt.setString(2, model.getPassword());
			psmt.setString(3, model.getSessionid());
			psmt.setString(4, model.getUsername());
			psmt.setInt(5, model.getUserrole());
			psmt.setString(6, model.getUseremail());
			psmt.setString(7, model.getUserpic());
			psmt.setString(8, model.getCreatetime());
			psmt.setString(9, model.getUpdatetime());
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
	 * 删除用户
	 * 
	 * @param model
	 * @return
	 */
	public int delUser(String usercode) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_USER);
			psmt.setString(1, usercode);
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
	 * 修改用户
	 * 
	 * @param model
	 * @return
	 */
	public int modUser(UserModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(MOD_USER);
			psmt.setString(1, model.getPassword());
			psmt.setString(2, model.getSessionid());
			psmt.setString(3, model.getUsername());
			psmt.setInt(4, model.getUserrole());
			psmt.setString(5, model.getUseremail());
			psmt.setString(6, model.getUserpic());
			psmt.setString(7, model.getUpdatetime());
			psmt.setString(8, model.getUsercode());
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
	 * 获取用户
	 * 
	 * @param key
	 * @return
	 */
	public UserModel getUser(String usercode) {
		UserModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_USER);
			psmt.setString(1, usercode);
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
	 * 获取用户
	 * 
	 * @param usercode
	 * @param pwd
	 * @return
	 */
	public UserModel getUserPwd(String usercode, String pwd) {
		UserModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_USER_PWD);
			psmt.setString(1, usercode);
			psmt.setString(2, pwd);
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
	 * 返回用户列表
	 * 
	 * @return
	 */
	public List<UserModel> getUserList() {
		List<UserModel> USERlist = new ArrayList<UserModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_USERLIST);
			rs = psmt.executeQuery();
			while (rs.next()) {
				UserModel model = read(rs);
				USERlist.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return USERlist;
	}

	/**
	 * 返回用户列表
	 * 
	 * @return
	 */
	public List<UserModel> getUserListByUsername(String username) {
		List<UserModel> USERlist = new ArrayList<UserModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_USERLIST_USERNAME);
			psmt.setString(1, "%" + username + "%");
			rs = psmt.executeQuery();
			while (rs.next()) {
				UserModel model = read(rs);
				USERlist.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return USERlist;
	}

	/**
	 * 查找用户
	 * 
	 * @param usercode
	 * @param username
	 * @param email
	 * @param userrole
	 * @param regtimeBegin
	 * @param regtimeEnd
	 * @return
	 */
	public List<UserModel> searchUserList(String usercode, String username,
			String email, int userrole, String regtimeBegin, String regtimeEnd) {
		List<UserModel> userlist = new ArrayList<UserModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(SEARCH_USERLIST);
			psmt.setString(1, usercode);
			psmt.setString(2, usercode);
			psmt.setString(3, username);
			psmt.setString(4, username);
			psmt.setString(5, email);
			psmt.setString(6, email);
			psmt.setInt(7, userrole);
			psmt.setInt(8, userrole);
			psmt.setString(9, regtimeBegin);
			psmt.setString(10, regtimeEnd);
			psmt.setString(11, regtimeBegin);
			psmt.setString(12, regtimeEnd);

			rs = psmt.executeQuery();
			while (rs.next()) {
				UserModel model = read(rs);
				userlist.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return userlist;
	}

	/**
	 * 判断是否存在用户
	 * 
	 * @param name
	 * @return
	 */
	public boolean existUser(String usercode) {
		return getUser(usercode) == null ? false : true;
	}

	/**
	 * 读取数据结果集
	 * 
	 * @param rs
	 * @return
	 */
	public UserModel read(ResultSet rs) {
		UserModel model = null;
		try {
			String userid = rs.getString("USERID");
			String password = rs.getString("PASSWORD");
			String sessionid = rs.getString("SESSIONID");
			String username = rs.getString("USERNAME");
			int userrole = rs.getInt("USERROLE");
			String useremail = rs.getString("USEREMAIL");
			String userpic = rs.getString("USERPIC");
			String createtime = rs.getString("USERCREATE");
			String updatetime = rs.getString("USERUPDATE");
			model = new UserModel();
			model.setUsercode(userid);
			model.setPassword(password);
			model.setSessionid(sessionid);
			model.setUsername(username);
			model.setUserrole(userrole);
			model.setUseremail(useremail);
			model.setUserpic(userpic);
			model.setCreatetime(createtime);
			model.setUpdatetime(updatetime);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		return model;
	}

}
