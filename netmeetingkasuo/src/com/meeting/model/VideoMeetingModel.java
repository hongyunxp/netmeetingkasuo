package com.meeting.model;

public class VideoMeetingModel extends VideoModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -155748493131643807L;

	private MeetingModel meetingModel = null;

	public MeetingModel getMeetingModel() {
		return meetingModel;
	}

	public void setMeetingModel(MeetingModel meetingModel) {
		this.meetingModel = meetingModel;
	}

}
