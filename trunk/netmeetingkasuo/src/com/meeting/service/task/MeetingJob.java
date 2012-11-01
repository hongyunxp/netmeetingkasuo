package com.meeting.service.task;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.meeting.dao.MeetingDao;
import com.meeting.model.MeetingModel;
import com.meeting.service.dwr.DWRService;
import com.meeting.utils.AppConfigure;

public class MeetingJob implements Job {

	private static Logger logger = Logger.getLogger(MeetingJob.class);

	/**
	 * 会议时间监控任务
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// 会议时间到触发
		Object object = context.getJobDetail().getJobDataMap().get(
				AppConfigure.MEETING);
		if (object != null) {
			MeetingModel meeting = (MeetingModel) object;
			meeting.setState(AppConfigure.MEETING_ENDED);
			MeetingDao.getInstance().modMeeting(meeting);
			try {
				DWRService.SessionsCall(meeting.getMeetingId(), "meetingTimeup", "会议时间到了");
			} catch (Exception e) {
				logger.error("会议计时监控调用DWR失败："+e);				
			}
		}
	}
}
