package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.model.FileMeetingModel;
import com.meeting.model.MeetingModel;
import com.meeting.utils.StackTraceUtil;

public class FileMeetingDao {

	private static Logger logger = Logger.getLogger(FileMeetingDao.class);

	private static FileMeetingDao instance = null;

	private static final String ADD_FILEMEETING = "INSERT INTO E_MEETINGFILE(MEETINGID,FILEID) VALUES(?,?)";
	private static final String DEL_FILEMEETING = "DELETE FROM E_MEETINGFILE WHERE FILEID=?";
	private static final String GET_MEETING_FILES = "SELECT * FROM V_DOCUMENT_MEETING WHERE MEETINGID=?";
	private static final String GET_MEETING_FILE = "SELECT * FROM V_DOCUMENT_MEETING WHERE FILEID=?";

	public static FileMeetingDao getInstance() {
		if (instance == null) {
			instance = new FileMeetingDao();
		}
		return instance;
	}

	/**
	 * 添加文档与会议的关联
	 * 
	 * @param model
	 * @return
	 */
	public int addFileMeeting(String meetingid, String fileid) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_FILEMEETING);
			psmt.setString(1, meetingid);
			psmt.setString(2, fileid);
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
	 * 删除文档与会议的关联
	 * 
	 * @param model
	 * @return
	 */
	public int delFileMeeting(String fileid) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_FILEMEETING);
			psmt.setString(1, fileid);
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
	 * 获取会议中所有已经共享的文档
	 * 
	 * @param key
	 * @return
	 */
	public List<FileMeetingModel> getMeetingFiles(String meetingid) {
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		List<FileMeetingModel> list = new ArrayList<FileMeetingModel>();
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETING_FILES);
			psmt.setString(1, meetingid);
			rs = psmt.executeQuery();
			while (rs.next()) {
				FileMeetingModel model = read(rs);
				list.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return list;
	}

	/**
	 * 获取会议中所有已经共享的文档
	 * 
	 * @param key
	 * @return
	 */
	public FileMeetingModel getMeetingFile(String fileid) {
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		FileMeetingModel model = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETING_FILE);
			psmt.setString(1, fileid);
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
	 * 读取数据库返回的会议结果集
	 * 
	 * @param rs
	 * @return
	 */
	private FileMeetingModel read(ResultSet rs) {
		FileMeetingModel model = null;
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

			String fileid = rs.getString("FILEID");
			String filename = rs.getString("FILENAME");
			String filepath = rs.getString("FILEPATH");
			String filesize = rs.getString("FILESIZE");
			String filecreate = rs.getString("FILECREATE");
			String filepage = rs.getString("FILEPAGE");
			String filecollection = rs.getString("FILECOLLECTION");
			String filext = rs.getString("FILEEXT");

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

			model = new FileMeetingModel();
			model.setFileId(fileid);
			model.setFileName(filename);
			model.setFilePath(filepath);
			model.setFileSize(filesize);
			model.setFileCreate(filecreate);
			model.setFilePage(filepage);
			model.setFileCollection(filecollection);
			model.setFileExt(filext);
			model.setMeetingModel(meetingModel);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		return model;
	}

}
