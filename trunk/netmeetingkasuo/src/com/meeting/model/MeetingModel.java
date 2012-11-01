package com.meeting.model;

import java.io.Serializable;

import com.meeting.utils.AppConfigure;

public class MeetingModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5546166624750207063L;

	private String meetingId;
	private String verifyCode;
	private String subject;
	private String agenda;
	private String begintime;
	private String duration;
	private int state;
	private String createtime;
	private String updatetime;

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getAgenda() {
		return agenda;
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public String getBegintime() {
		return begintime;
	}

	public void setBegintime(String begintime) {
		this.begintime = begintime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCreatetime() {
		return createtime;
	}

	public void setCreatetime(String createtime) {
		this.createtime = createtime;
	}

	public String getUpdatetime() {
		return updatetime;
	}

	public void setUpdatetime(String updatetime) {
		this.updatetime = updatetime;
	}

	/**
	 * 获得会议状态字符串
	 * 
	 * @param state
	 * @return
	 */
	public static String getStateString(int state) {
		String stateString = "";
		switch (state) {
		case AppConfigure.MEETING_NOT_START:
			stateString = "<span style='color:orange;'>未开始</span>";
			break;
		case AppConfigure.MEETING_IN_PROGRESS:
			stateString = "<span style='color:green;'>进行中</span>";
			break;
		case AppConfigure.MEETING_ENDED:
			stateString = "<span style='color:gray;'>已结束</span>";
			break;
		default:
			break;
		}
		return stateString;
	}

	public String toString() {
		return "MeetingModel [agenda=" + agenda + ", begintime=" + begintime
				+ ", createtime=" + createtime + ", duration=" + duration
				+ ", meetingId=" + meetingId + ", state=" + state
				+ ", subject=" + subject + ", updatetime=" + updatetime
				+ ", verifyCode=" + verifyCode + "]";
	}

}
