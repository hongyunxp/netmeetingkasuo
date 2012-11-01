package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.model.MeetingModel;
import com.meeting.model.VideoMeetingModel;
import com.meeting.utils.StackTraceUtil;

public class VideoMeetingDao {

	private static Logger logger = Logger.getLogger(VideoMeetingDao.class);

	private static VideoMeetingDao instance = null;

	private static final String ADD_VIDEOMEETING = "INSERT INTO E_MEETINGVIDEO(MEETINGID,VIDEOID) VALUES(?,?)";
	private static final String DEL_VIDEOMEETING = "DELETE FROM E_MEETINGVIDEO WHERE MEETINGID=? AND VIDEOID=?";
	private static final String GET_MEETING_VIDEO = "SELECT * FROM V_MEETING_VIDEO WHERE MEETINGID=? AND VIDEOID=?";
	private static final String GET_MEETING_VIDEOS = "SELECT * FROM V_MEETING_VIDEO WHERE MEETINGID=?";

	public static VideoMeetingDao getInstance() {
		if (instance == null) {
			instance = new VideoMeetingDao();
		}
		return instance;
	}

	/**
	 * 添加视频与会议的关联
	 * 
	 * @param model
	 * @return
	 */
	public int addVideoMeeting(String meetingid, String videoid) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_VIDEOMEETING);
			psmt.setString(1, meetingid);
			psmt.setString(2, videoid);
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
	 * 删除视频与会议的关联
	 * 
	 * @param model
	 * @return
	 */
	public int delVideoMeeting(String meetingid, String videoid) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_VIDEOMEETING);
			psmt.setString(1, meetingid);
			psmt.setString(2, videoid);
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
	 * 获取会议某一个视频
	 * 
	 * @param key
	 * @return
	 */
	public VideoMeetingModel getMeetingVideo(String meetingid, String videoid) {
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		VideoMeetingModel model = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETING_VIDEO);
			psmt.setString(1, meetingid);
			psmt.setString(2, videoid);
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
	 * 获取会议中所有已经共享的视频
	 * 
	 * @param key
	 * @return
	 */
	public List<VideoMeetingModel> getMeetingVideos(String meetingid) {
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		List<VideoMeetingModel> list = new ArrayList<VideoMeetingModel>();
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_MEETING_VIDEOS);
			psmt.setString(1, meetingid);
			rs = psmt.executeQuery();
			while (rs.next()) {
				VideoMeetingModel model = read(rs);
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
	 * 读取数据库返回的会议结果集
	 * 
	 * @param rs
	 * @return
	 */
	private VideoMeetingModel read(ResultSet rs) {
		VideoMeetingModel model = null;
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

			String videoid = rs.getString("VIDEOID");
			String videoname = rs.getString("VIDEONAME");
			String videopath = rs.getString("VIDEOPATH");
			String videosize = rs.getString("VIDEOSIZE");
			String videocreate = rs.getString("VIDEOCREATE");
			String videoext = rs.getString("VIDEOEXT");

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

			model = new VideoMeetingModel();
			model.setVideoId(videoid);
			model.setVideoName(videoname);
			model.setVideoPath(videopath);
			model.setVideoSize(videosize);
			model.setVideoCreate(videocreate);
			model.setVideoExt(videoext);
			model.setMeetingModel(meetingModel);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		return model;
	}

}
