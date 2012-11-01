package com.meeting.model;

import java.io.Serializable;

public class UserMeetingModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4737888348672275320L;

	private int umRole;

	private String umEnterTime;

	private int umState;

	private MeetingModel meetingModel;

	private UserModel userModel = null;

	public MeetingModel getMeetingModel() {
		return meetingModel;
	}

	public void setMeetingModel(MeetingModel meetingModel) {
		this.meetingModel = meetingModel;
	}

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public int getUmRole() {
		return umRole;
	}

	public void setUmRole(int umRole) {
		this.umRole = umRole;
	}

	public String getUmEnterTime() {
		return umEnterTime;
	}

	public void setUmEnterTime(String umEnterTime) {
		this.umEnterTime = umEnterTime;
	}

	public int getUmState() {
		return umState;
	}

	public void setUmState(int umState) {
		this.umState = umState;
	}

	public String toString() {
		return "UserMeetingModel [meetingModel=" + meetingModel
				+ ", umEnterTime=" + umEnterTime + ", umRole=" + umRole
				+ ", umState=" + umState + ", userModel=" + userModel + "]";
	}

}
