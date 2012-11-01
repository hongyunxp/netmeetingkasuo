package com.meeting.model;

public class FileUserModel extends FileModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8166226971540773019L;

	private UserModel userModel = null;

	public UserModel getUserModel() {
		return userModel;
	}

	public void setUserModel(UserModel userModel) {
		this.userModel = userModel;
	}

	public String toString() {
		return "UserMeetingModel [userModel=" + userModel + "]";
	}

}
