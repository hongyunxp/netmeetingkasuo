package com.meeting.model;

import java.io.Serializable;

public class VideoModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7875621726695088780L;

	private String videoId;

	private String videoName;

	private String videoPath;

	private String videoSize;

	private String videoCreate;

	private String videoExt;

	private String userId;

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public String getVideoName() {
		return videoName;
	}

	public void setVideoName(String videoName) {
		this.videoName = videoName;
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(String videoSize) {
		this.videoSize = videoSize;
	}

	public String getVideoCreate() {
		return videoCreate;
	}

	public void setVideoCreate(String videoCreate) {
		this.videoCreate = videoCreate;
	}

	public String getVideoExt() {
		return videoExt;
	}

	public void setVideoExt(String videoExt) {
		this.videoExt = videoExt;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

}
