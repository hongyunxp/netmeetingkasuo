package com.meeting.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.log4j.Logger;

import com.meeting.model.FileModel;
import com.meeting.utils.StackTraceUtil;

public class FileDao {

	private static FileDao instance = null;

	private static Logger logger = Logger.getLogger(FileDao.class);

	private static final String ADD_FILE = "INSERT INTO E_FILE(FILEID,FILENAME,FILEPATH,FILESIZE,FILECREATE,FILEPAGE,FILECOLLECTION,FILEEXT,USERID) VALUES(?,?,?,?,?,?,?,?,?)";
	private static final String DEL_FILE = "DELETE FROM E_FILE WHERE FILEID = ?";
	private static final String MOD_FILE = "UPDATE E_FILE SET FILENAME=?,FILEPATH=?,FILESIZE=?,FILECREATE=?,FILEPAGE=?,FILECOLLECTION=?,FILEEXT=? WHERE FILEID = ?";

	public static FileDao getInstance() {
		if (instance == null) {
			instance = new FileDao();
		}
		return instance;
	}

	/**
	 * 添加文档
	 * 
	 * @param model
	 * @return
	 */
	public int addFile(FileModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(ADD_FILE);
			psmt.setString(1, model.getFileId());
			psmt.setString(2, model.getFileName());
			psmt.setString(3, model.getFilePath());
			psmt.setString(4, model.getFileSize());
			psmt.setString(5, model.getFileCreate());
			psmt.setString(6, model.getFilePage());
			psmt.setString(7, model.getFileCollection());
			psmt.setString(8, model.getFileExt());
			psmt.setString(9, model.getUserId());
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
	 * 删除文档
	 * 
	 * @param model
	 * @return
	 */
	public int delFile(String fileId) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(DEL_FILE);
			psmt.setString(1, fileId);
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
	 * 修改文档
	 * 
	 * @param model
	 * @return
	 */
	public int modFile(FileModel model) {
		int ret = 0;
		PreparedStatement psmt = null;
		Connection conn = null;
		try {
			conn = HSQLConnection.getConnection();
			psmt = conn.prepareStatement(MOD_FILE);
			psmt.setString(1, model.getFileName());
			psmt.setString(2, model.getFilePath());
			psmt.setString(3, model.getFileSize());
			psmt.setString(4, model.getFileCreate());
			psmt.setString(5, model.getFilePage());
			psmt.setString(6, model.getFileCollection());
			psmt.setString(7, model.getFileExt());
			psmt.setString(8, model.getFileId());
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
