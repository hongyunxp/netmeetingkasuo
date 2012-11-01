package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.model.MeetingModel;
import com.meeting.model.UserMeetingModel;
import com.meeting.model.UserModel;
import com.meeting.utils.StackTraceUtil;

public class UserMeetingDao {

	private static UserMeetingDao instance = null;

	private static Logger logger = Logger.getLogger(UserMeetingDao.class);

	private static final String ADD_USERMEETING = "INSERT INTO E_USERMEETING(USERID,MEETINGID,USERMEETINGROLE,USERENTERTIME,MEETINGUSERSTATE) VALUES(?,?,?,?,?)";
	private static final String DEL_USERMEETING = "DELETE FROM E_USERMEETING WHERE USERID=? AND MEETINGID=?";
	private static final String MOD_USERMEETING = "UPDATE E_USERMEETING SET USERMEETINGROLE = ?, MEETINGUSERSTATE=? WHERE USERID = ? AND MEETINGID = ?";

	private static final String GET_MEETING = "SELECT * FROM V_USERMEETING_VIEW WHERE MEETINGID = ? AND USERID = ? AND USERMEETINGROLE=?";
	private static final String GET_MEETINGLIST = "SELECT * FROM V_USERMEETING_VIEW WHERE USERMEETINGROLE = 0 ORDER BY CREATETIME DESC";
	private static final String GET_MEETINGLISTBYUSER = "SELECT DISTINCT * FROM V_USERMEETING_VIEW WHERE USERMEETINGROLE = 0 AND USERID = ? ORDER BY CREATETIME DESC";
	private static final String GET_MEETINGLIST_NUM_BYUSER = "SELECT TOP ? DISTINCT * FROM V_USERMEETING_VIEW WHERE USERID = ? ORDER BY CREATETIME DESC";
	private static final String GET_MEETINGLIST_NUM_BYSTATE = "SELECT TOP ? DISTINCT * FROM V_USERMEETING_VIEW WHERE STATE = 1 ORDER BY CREATETIME DESC";
	private static final String GET_MEETINGLIST_NUM_BYUMSTATE = "SELECT TOP ? DISTINCT * FROM V_USERMEETING_VIEW WHERE USERID = ? AND MEETINGUSERSTATE = 0 AND USERMEETINGROLE = 1 ORDER BY CREATETIME DESC";

	private static final String GET_MEETINGHOST = "SELECT * FROM V_USERMEETING_VIEW WHERE USERMEETINGROLE = 0 AND MEETINGID = ?";
	private static final String GET_MEETINGUSERS = "SELECT * FROM V_USERMEETING_VIEW WHERE MEETINGID = ?";

	// public static final int ROLE_ADMIN_ID = 0;
	// public static final int ROLE_COMMON_ID = 1;

	public static UserMeetingDao getInstance() {
		if (instance == null) {
			instance = new UserMeetingDao();
		}
		return instance;
	}

	/**
	 * 添加用户与会议的关联
	 * 
	 * @param model
	 * @return
	 */
	public int addUserMeeting(UserMeetingModel umModel) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			String userid = umModel.getUserModel().getUsercode();
			String meetingid = umModel.getMeetingModel().getMeetingId();
			int userrole = umModel.getUmRole();
			String umEnter = umModel.getUmEnterTime();
			int umState = umModel.getUmState();
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_USERMEETING);
			psmt.setString(1, userid);
			psmt.setString(2, meetingid);
			psmt.setInt(3, userrole);
			psmt.setString(4, umEnter);
			psmt.setInt(5, umState);
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
	 * 删除用户与会议的关联
	 * 
	 * @param model
	 * @return
	 */
	public int delUserMeeting(String userid, String meetingid) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_USERMEETING);
			psmt.setString(1, userid);
			psmt.setString(2, meetingid);
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
	 * 更新用户与会议的关联
	 * 
	 * @param model
	 * @return
	 */
	public int modUserMeeting(UserMeetingModel umModel) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			String userid = umModel.getUserModel().getUsercode();
			String meetingid = umModel.getMeetingModel().getMeetingId();
			int userrole = umModel.getUmRole();
			int umState = umModel.getUmState();
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(MOD_USERMEETING);
			psmt.setInt(1, userrole);
			psmt.setInt(2, umState);
			psmt.setString(3, userid);
			psmt.setString(4, meetingid);
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
	 * 获取某个会议
	 * 
	 * @param key
	 * @return
	 */
	public UserMeetingModel getMeeting(String meetingid, String usercode,
			int umrole) {
		UserMeetingModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETING);
			psmt.setString(1, meetingid);
			psmt.setString(2, usercode);
			psmt.setInt(3, umrole);
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
	 * 返回所有用户会议列表
	 * 
	 * @return
	 */
	public List<UserMeetingModel> getMeetingList() {
		List<UserMeetingModel> userMeetingList = new ArrayList<UserMeetingModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGLIST);
			rs = psmt.executeQuery();
			while (rs.next()) {
				UserMeetingModel model = read(rs);
				userMeetingList.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return userMeetingList;
	}

	/**
	 * 返回某用户参加过会议
	 * 
	 * @return
	 */
	public List<UserMeetingModel> getMeetingListByUserCode(String userid) {
		List<UserMeetingModel> userMeetingList = new ArrayList<UserMeetingModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGLISTBYUSER);
			psmt.setString(1, userid);
			rs = psmt.executeQuery();
			while (rs.next()) {
				UserMeetingModel model = read(rs);
				userMeetingList.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return userMeetingList;
	}

	/**
	 * 返回某用户参加过会议，指定条数
	 * 
	 * @return
	 */
	public List<UserMeetingModel> getMeetingListByUserCode(String userid,
			int count) {
		List<UserMeetingModel> userMeetingList = new ArrayList<UserMeetingModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGLIST_NUM_BYUSER);
			psmt.setInt(1, count);
			psmt.setString(2, userid);
			rs = psmt.executeQuery();
			while (rs.next()) {
				UserMeetingModel model = read(rs);
				userMeetingList.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return userMeetingList;
	}

	/**
	 * 返回指定条数的正在进行的会议
	 * 
	 * @param count
	 * @return
	 */
	public List<UserMeetingModel> getMeetingListByState(int count) {
		List<UserMeetingModel> userMeetingList = new ArrayList<UserMeetingModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGLIST_NUM_BYSTATE);
			psmt.setInt(1, count);
			rs = psmt.executeQuery();
			while (rs.next()) {
				UserMeetingModel model = read(rs);
				userMeetingList.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return userMeetingList;
	}

	/**
	 * 返回指定条数的已经邀请的人员
	 * 
	 * @param count
	 * @return
	 */
	public List<UserMeetingModel> getMeetingListByUMState(String usercode,
			int count) {
		List<UserMeetingModel> userMeetingList = new ArrayList<UserMeetingModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGLIST_NUM_BYUMSTATE);
			psmt.setInt(1, count);
			psmt.setString(2, usercode);
			rs = psmt.executeQuery();
			while (rs.next()) {
				UserMeetingModel model = read(rs);
				userMeetingList.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return userMeetingList;
	}

	/**
	 * 获取某个会议的主持人
	 * 
	 * @param key
	 * @return
	 */
	public UserModel getMeetingHost(String meetingid) {
		UserModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGHOST);
			psmt.setString(1, meetingid);
			rs = psmt.executeQuery();
			if (rs.next()) {
				UserMeetingModel usermodel = read(rs);
				model = usermodel.getUserModel();
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
	 * 获取某个会议的所有与会者
	 * 
	 * @param key
	 * @return
	 */
	public List<UserMeetingModel> getMeetingUsers(String meetingid) {
		List<UserMeetingModel> modelList = new ArrayList<UserMeetingModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGUSERS);
			psmt.setString(1, meetingid);
			rs = psmt.executeQuery();
			while (rs.next()) {
				UserMeetingModel model = read(rs);
				modelList.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return modelList;
	}

	/**
	 * 读取数据库返回的会议结果集
	 * 
	 * @param rs
	 * @return
	 */
	private UserMeetingModel read(ResultSet rs) {
		UserMeetingModel model = null;
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

			String meetingId = rs.getString("MEETINGID");
			String verifyCode = rs.getString("VERIFYCODE");
			String subject = rs.getString("SUBJECT");
			String agenda = rs.getString("AGENDA");
			String begintime = rs.getString("BEGINTIME");
			String duration = rs.getString("DURATION");
			int state = rs.getInt("STATE");
			String mcreatetime = rs.getString("CREATETIME");
			String mupdatetime = rs.getString("UPDATETIME");

			int umRole = rs.getInt("USERMEETINGROLE");
			String umEnterTime = rs.getString("USERENTERTIME");
			int umState = rs.getInt("MEETINGUSERSTATE");

			UserModel usermodel = new UserModel();
			usermodel.setUsercode(userid);
			usermodel.setPassword(password);
			usermodel.setSessionid(sessionid);
			usermodel.setUsername(username);
			usermodel.setUserrole(userrole);
			usermodel.setUseremail(useremail);
			usermodel.setUserpic(userpic);
			usermodel.setCreatetime(createtime);
			usermodel.setUpdatetime(updatetime);

			MeetingModel meetingModel = new MeetingModel();
			meetingModel.setMeetingId(meetingId);
			meetingModel.setVerifyCode(verifyCode);
			meetingModel.setSubject(subject);
			meetingModel.setAgenda(agenda);
			meetingModel.setBegintime(begintime);
			meetingModel.setDuration(duration);
			meetingModel.setState(state);
			meetingModel.setCreatetime(mcreatetime);
			meetingModel.setUpdatetime(mupdatetime);
			model = new UserMeetingModel();
			model.setUmRole(umRole);
			model.setUmEnterTime(umEnterTime);
			model.setUmState(umState);
			model.setMeetingModel(meetingModel);
			model.setUserModel(usermodel);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		return model;
	}
}
