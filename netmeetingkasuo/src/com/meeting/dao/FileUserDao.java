package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.model.FileUserModel;
import com.meeting.model.UserModel;
import com.meeting.utils.StackTraceUtil;

public class FileUserDao {

	private static FileUserDao instance = null;

	private static Logger logger = Logger.getLogger(FileUserDao.class);

	private static final String GET_FILE = "SELECT * FROM V_DOCUMENT_USER WHERE FILEID = ?";
	private static final String GET_FILE_LIST = "SELECT * FROM V_DOCUMENT_USER ORDER BY FILECREATE DESC";
	private static final String GET_FILEBYUSER_LIST = "SELECT * FROM V_DOCUMENT_USER WHERE USERID = ?  ORDER BY FILECREATE DESC";

	public static FileUserDao getInstance() {
		if (instance == null) {
			instance = new FileUserDao();
		}
		return instance;
	}

	/**
	 * 获取所有的文档
	 * 
	 * @return
	 */
	public FileUserModel getFileUser(String fileid) {
		FileUserModel model = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_FILE);
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
	 * 获取所有的文档
	 * 
	 * @return
	 */
	public List<FileUserModel> getFileUserList() {
		List<FileUserModel> fileuserList = new ArrayList<FileUserModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_FILE_LIST);
			rs = psmt.executeQuery();
			while (rs.next()) {
				FileUserModel model = read(rs);
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
	 * 获取某个用户的所有的文档
	 * 
	 * @return
	 */
	public List<FileUserModel> getFileUserList(String userid) {
		List<FileUserModel> fileuserList = new ArrayList<FileUserModel>();
		PreparedStatement psmt = null;
		ResultSet rs = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(GET_FILEBYUSER_LIST);
			psmt.setString(1, userid);
			rs = psmt.executeQuery();
			while (rs.next()) {
				FileUserModel model = read(rs);
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
	private FileUserModel read(ResultSet rs) {
		FileUserModel model = null;
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

			String fileid = rs.getString("FILEID");
			String filename = rs.getString("FILENAME");
			String filepath = rs.getString("FILEPATH");
			String filesize = rs.getString("FILESIZE");
			String filecreate = rs.getString("FILECREATE");
			String filepage = rs.getString("FILEPAGE");
			String filecollection = rs.getString("FILECOLLECTION");
			String filext = rs.getString("FILEEXT");

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

			model = new FileUserModel();
			model.setFileId(fileid);
			model.setFileName(filename);
			model.setFilePath(filepath);
			model.setFileSize(filesize);
			model.setFileCreate(filecreate);
			model.setFilePage(filepage);
			model.setFileCollection(filecollection);
			model.setFileExt(filext);
			model.setUserModel(usermodel);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		return model;
	}

}
