package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.model.VideoUserModel;
import com.meeting.model.UserModel;
import com.meeting.utils.StackTraceUtil;

public class VideoUserDao {

	private static VideoUserDao instance = null;

	private static Logger logger = Logger.getLogger(VideoUserDao.class);

	private static final String GET_VIDEO = "SELECT * FROM V_USER_VIDEO WHERE VIDEOID = ?";
	private static final String GET_VIDEO_LIST = "SELECT * FROM V_USER_VIDEO ORDER BY VIDEOCREATE DESC";
	private static final String GET_VIDEOBYUSER_LIST = "SELECT * FROM V_USER_VIDEO WHERE USERID = ?  ORDER BY VIDEOCREATE DESC";

	public static VideoUserDao getInstance() {
		if (instance == null) {
			instance = new VideoUserDao();
		}
		return instance;
	}

	/**
	 * 获取所有的视频
	 * 
	 * @return
	 */
	public VideoUserModel getVideoUser(String videoid) {
		VideoUserModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_VIDEO);
			psmt.setString(1, videoid);
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
	 * 获取所有的视频
	 * 
	 * @return
	 */
	public List<VideoUserModel> getVideoUserList() {
		List<VideoUserModel> fileuserList = new ArrayList<VideoUserModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_VIDEO_LIST);
			rs = psmt.executeQuery();
			while (rs.next()) {
				VideoUserModel model = read(rs);
				fileuserList.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return fileuserList;
	}

	/**
	 * 获取某个用户的所有的视频
	 * 
	 * @return
	 */
	public List<VideoUserModel> getVideoUserList(String userid) {
		List<VideoUserModel> fileuserList = new ArrayList<VideoUserModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_VIDEOBYUSER_LIST);
			psmt.setString(1, userid);
			rs = psmt.executeQuery();
			while (rs.next()) {
				VideoUserModel model = read(rs);
				fileuserList.add(model);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		} finally {
			HSQLConnection.close(psmt);
			HSQLConnection.close(rs);
			HSQLConnection.close(conn);
		}
		return fileuserList;
	}

	/**
	 * 读取数据库结果集
	 * 
	 * @param rs
	 * @return
	 */
	private VideoUserModel read(ResultSet rs) {
		VideoUserModel model = null;
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

			String videoid = rs.getString("VIDEOID");
			String videoname = rs.getString("VIDEONAME");
			String videopath = rs.getString("VIDEOPATH");
			String videosize = rs.getString("VIDEOSIZE");
			String videocreate = rs.getString("VIDEOCREATE");
			String videoext = rs.getString("VIDEOEXT");

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

			model = new VideoUserModel();
			model.setVideoId(videoid);
			model.setVideoName(videoname);
			model.setVideoPath(videopath);
			model.setVideoSize(videosize);
			model.setVideoCreate(videocreate);
			model.setVideoExt(videoext);
			model.setUserModel(usermodel);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		return model;
	}

}
