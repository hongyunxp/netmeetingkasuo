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
	 * ����ʱ��������
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// ����ʱ�䵽����
		Object object = context.getJobDetail().getJobDataMap().get(
				AppConfigure.MEETING);
		if (object != null) {
			MeetingModel meeting = (MeetingModel) object;
			meeting.setState(AppConfigure.MEETING_ENDED);
			MeetingDao.getInstance().modMeeting(meeting);
			try {
				DWRService.SessionsCall(meeting.getMeetingId(), "meetingTimeup", "����ʱ�䵽��");
			} catch (Exception e) {
				logger.error("�����ʱ��ص���DWRʧ�ܣ�"+e);				
			}
		}
	}
}
