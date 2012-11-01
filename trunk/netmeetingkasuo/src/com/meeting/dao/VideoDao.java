package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.meeting.model.VideoModel;
import com.meeting.utils.StackTraceUtil;

public class VideoDao {

	private static VideoDao instance = null;

	private static Logger logger = Logger.getLogger(VideoDao.class);

	private static final String ADD_VIDEO = "INSERT INTO E_VIDEO(VIDEOID,VIDEONAME,VIDEOPATH,VIDEOSIZE,VIDEOCREATE,VIDEOEXT,USERID) VALUES(?,?,?,?,?,?,?)";
	private static final String DEL_VIDEO = "DELETE FROM E_VIDEO WHERE VIDEOID = ?";
	private static final String MOD_VIDEO = "UPDATE E_VIDEO SET VIDEONAME=?,VIDEOPATH=?,VIDEOSIZE=?,VIDEOEXT=?,USERID=? WHERE VIDEOID = ?";

	public static VideoDao getInstance() {
		if (instance == null) {
			instance = new VideoDao();
		}
		return instance;
	}

	/**
	 * 添加视频
	 * 
	 * @param model
	 * @return
	 */
	public int addVideo(VideoModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_VIDEO);
			psmt.setString(1, model.getVideoId());
			psmt.setString(2, model.getVideoName());
			psmt.setString(3, model.getVideoPath());
			psmt.setString(4, model.getVideoSize());
			psmt.setString(5, model.getVideoCreate());
			psmt.setString(6, model.getVideoExt());
			psmt.setString(7, model.getUserId());
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
	 * 删除视频
	 * 
	 * @param model
	 * @return
	 */
	public int delVideo(String videoId) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_VIDEO);
			psmt.setString(1, videoId);
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
	 * 修改视频
	 * 
	 * @param model
	 * @return
	 */
	public int modVideo(VideoModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(MOD_VIDEO);
			psmt.setString(1, model.getVideoName());
			psmt.setString(2, model.getVideoPath());
			psmt.setString(3, model.getVideoSize());
			psmt.setString(4, model.getVideoExt());
			psmt.setString(5, model.getUserId());
			psmt.setString(6, model.getVideoId());
			ret = psmt.executeUpdate();
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(conn);
		}
		return ret;
	}

}
