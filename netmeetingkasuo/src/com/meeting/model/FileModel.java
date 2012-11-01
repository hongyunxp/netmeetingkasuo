package com.meeting.model;

import java.io.Serializable;

public class FileModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2912805164775521576L;

	private String fileId = null;

	private String fileName = null;

	private String filePath = null;

	private String fileSize = null;

	private String fileCreate = null;

	private String filePage = null;

	private String fileCollection = null;

	private String fileExt = null;

	private String userId = null;

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileCreate() {
		return fileCreate;
	}

	public void setFileCreate(String fileCreate) {
		this.fileCreate = fileCreate;
	}

	public String getFilePage() {
		return filePage;
	}

	public void setFilePage(String filePage) {
		this.filePage = filePage;
	}

	public String getFileCollection() {
		return fileCollection;
	}

	public void setFileCollection(String fileCollection) {
		this.fileCollection = fileCollection;
	}

	public String getFileExt() {
		return fileExt;
	}

	public void setFileExt(String fileExt) {
		this.fileExt = fileExt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String toString() {
		return "FileModel [fileCollection=" + fileCollection + ", fileCreate="
				+ fileCreate + ", fileExt=" + fileExt + ", fileId=" + fileId
				+ ", fileName=" + fileName + ", filePage=" + filePage
				+ ", filePath=" + filePath + ", fileSize=" + fileSize
				+ ", userId=" + userId + "]";
	}

}
