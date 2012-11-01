package com.meeting.service.task;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;

import com.meeting.model.MeetingModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.DateUtils;
import com.meeting.utils.StackTraceUtil;

public class MeetingSchedule {

	private static MeetingSchedule instance = null;
	private static Logger logger = Logger.getLogger(MeetingSchedule.class);
	public static String TASK_GROUP = "meetingGrp";

	private MeetingSchedule() {
	};

	public static MeetingSchedule getInstance() {
		if (instance == null) {
			instance = new MeetingSchedule();
		}
		return instance;
	}

	/**
	 * ��������
	 * 
	 * @return
	 * */
	public void startJob(MeetingModel meeting) {
		Scheduler sched = SchedulerInstance.getInstance().getScheduler();
		try {
			long duration = Long.valueOf(meeting.getDuration()) * 60 * 1000;
			Trigger trigger = new SimpleTrigger(meeting.getMeetingId(),
					TASK_GROUP, DateUtils.getSpecficDate(duration));
			JobDetail job = new JobDetail(meeting.getMeetingId(), TASK_GROUP,
					MeetingJob.class);
			if (meeting != null) {
				job.getJobDataMap().put(AppConfigure.MEETING, meeting);
			}
			sched.scheduleJob(job, trigger);
			sched.start();
			logger.info("�����ʱ����ʼ������" + job.getFullName());
		} catch (Exception e) {
			logger.error("�����ʱ��������ʧ�ܣ�" + StackTraceUtil.getStackTrace(e));
		}
	}

	/**
	 * ֹͣ����
	 * 
	 * @param meeting
	 */
	public void stopJob(MeetingModel meeting) {
		Scheduler sd = SchedulerInstance.getInstance().getScheduler();
		String[] jobs = null;
		try {
			jobs = sd.getJobNames(TASK_GROUP);
			for (int i = 0; i < jobs.length; i++) {
				if (jobs[i].equals(meeting.getMeetingId())) {
					sd.unscheduleJob(jobs[i], TASK_GROUP);
					sd.deleteJob(jobs[i], TASK_GROUP);
					logger.info("�����ʱ����ֹͣ�ɹ���" + TASK_GROUP + "." + jobs[i]);
				}
			}
		} catch (SchedulerException e) {
			logger.error("�����ʱ����ֹͣʧ�ܣ�" + StackTraceUtil.getStackTrace(e));
		}
	}
}