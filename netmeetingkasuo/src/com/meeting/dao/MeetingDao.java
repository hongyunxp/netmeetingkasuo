package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.model.MeetingModel;
import com.meeting.utils.StackTraceUtil;

public class MeetingDao {
	private static MeetingDao instance = null;

	private static Logger logger = Logger.getLogger(MeetingDao.class);

	private static final String ADD_MEETING = "INSERT INTO E_MEETING(MEETINGID,VERIFYCODE,SUBJECT,AGENDA,BEGINTIME,DURATION,STATE,CREATETIME,UPDATETIME) VALUES(?,?,?,?,?,?,?,?,?)";
	private static final String DEL_MEETING = "DELETE FROM E_MEETING WHERE MEETINGID = ?";
	private static final String MOD_MEETING = "UPDATE E_MEETING SET VERIFYCODE=?,SUBJECT=?,AGENDA=?,BEGINTIME=?,DURATION=?,STATE=?,UPDATETIME=? WHERE MEETINGID = ?";

	private static final String GET_MEETINGBYMID = "SELECT * FROM E_MEETING WHERE MEETINGID = ?";
	private static final String GET_MEETINGLIST = "SELECT * FROM E_MEETING";

	public static MeetingDao getInstance() {
		if (instance == null) {
			instance = new MeetingDao();
		}
		return instance;
	}

	/**
	 * 添加会议
	 * 
	 * @param model
	 * @return
	 */
	public int addMeeting(MeetingModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_MEETING);
			psmt.setString(1, model.getMeetingId());
			psmt.setString(2, model.getVerifyCode());
			psmt.setString(3, model.getSubject());
			psmt.setString(4, model.getAgenda());
			psmt.setString(5, model.getBegintime());
			psmt.setString(6, model.getDuration());
			psmt.setInt(7, model.getState());
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
	 * 删除会议
	 * 
	 * @param model
	 * @return
	 */
	public int delMeeting(String meetingid) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_MEETING);
			psmt.setString(1, meetingid);
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
	 * 修改会议
	 * 
	 * @param model
	 * @return
	 */
	public int modMeeting(MeetingModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(MOD_MEETING);
			psmt.setString(1, model.getVerifyCode());
			psmt.setString(2, model.getSubject());
			psmt.setString(3, model.getAgenda());
			psmt.setString(4, model.getBegintime());
			psmt.setString(5, model.getDuration());
			psmt.setInt(6, model.getState());
			psmt.setString(7, model.getUpdatetime());
			psmt.setString(8, model.getMeetingId());
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
	public MeetingModel getMeeting(String meetingid) {
		MeetingModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGBYMID);
			psmt.setString(1, meetingid);
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
	 * 获取会议列表
	 * 
	 * @param key
	 * @return
	 */
	public List<MeetingModel> getMeetingList() {
		List<MeetingModel> modelList = new ArrayList<MeetingModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETINGLIST);
			rs = psmt.executeQuery();
			while (rs.next()) {
				MeetingModel model = read(rs);
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
	private MeetingModel read(ResultSet rs) {
		MeetingModel model = null;
		try {
			String meetingId = rs.getString("MEETINGID");
			String verifyCode = rs.getString("VERIFYCODE");
			String subject = rs.getString("SUBJECT");
			String agenda = rs.getString("AGENDA");
			String begintime = rs.getString("BEGINTIME");
			String duration = rs.getString("DURATION");
			int state = rs.getInt("STATE");
			String mcreatetime = rs.getString("CREATETIME");
			String mupdatetime = rs.getString("UPDATETIME");

			model = new MeetingModel();
			model.setMeetingId(meetingId);
			model.setVerifyCode(verifyCode);
			model.setSubject(subject);
			model.setAgenda(agenda);
			model.setBegintime(begintime);
			model.setDuration(duration);
			model.setState(state);
			model.setCreatetime(mcreatetime);
			model.setUpdatetime(mupdatetime);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		return model;
	}
}
