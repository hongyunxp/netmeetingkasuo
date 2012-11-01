package com.meeting.service.task;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.meeting.dao.ConfigDao;
import com.meeting.model.MeetingModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

public class ChatSaveSchedule {

	private static ChatSaveSchedule instance = null;
	private static Logger logger = Logger.getLogger(ChatSaveSchedule.class);
	public static String TRIGER_NAME = "TRIGER_NAME_SAVE";
	public static String TRIGER_GROUP = "TRIGER_GROUP_SAVE";
	public static String JOB_NAME = "JOB_NAME_SAVE";
	public static String JOB_GROUP = "JOB_GROUP_SAVE";
	public static String TASK_CYCLE_NAME = "cycle";

	private ChatSaveSchedule() {
	};

	public static ChatSaveSchedule getInstance() {
		if (instance == null) {
			instance = new ChatSaveSchedule();
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
		String cronString = ConfigDao.getInstance().getConfig(
				AppConfigure.KEY_CLEANINTERVAL).getValue();
		try {
			cronString = "0 0/2 * * * ?";
			String meetingId = meeting.getMeetingId();
			CronTrigger trigger = new CronTrigger(meetingId, TRIGER_GROUP,
					meetingId, JOB_GROUP, cronString);
			trigger.setStartTime(new Date());
			JobDetail job = new JobDetail(meetingId, JOB_GROUP,
					ChatSaveJob.class);
			if (meeting != null) {
				job.getJobDataMap().put(AppConfigure.MEETING, meeting);
			}
			sched.addJob(job, true);
			sched.scheduleJob(trigger);
			sched.start();
			logger.info("��ʱ����������Ϣ��ѭ����ʱ����ʼ: \"" + cronString + "\"��"
					+ job.getFullName());
		} catch (Exception e) {
			logger.error("��ʱ����������Ϣ����������ʧ�ܣ�" + StackTraceUtil.getStackTrace(e));
		}

	}

	/**
	 * ֹͣ���е�����
	 */
	public void StopJob(MeetingModel meeting) {
		Scheduler sd = SchedulerInstance.getInstance().getScheduler();
		String[] jobs = null;
		try {
			jobs = sd.getJobNames(JOB_GROUP);
			for (int i = 0; i < jobs.length; i++) {
				if (jobs[i].equals(meeting.getMeetingId())) {
					sd.unscheduleJob(jobs[i], TRIGER_GROUP);
					sd.deleteJob(jobs[i], TRIGER_GROUP);
					logger.info("��ʱ����������Ϣ����ֹͣ�ɹ���" + TRIGER_GROUP + "."
							+ jobs[i]);
				}
			}
		} catch (SchedulerException e) {
			logger.error("��ʱ����������Ϣ����ֹͣʧ�ܣ�" + StackTraceUtil.getStackTrace(e));
		}
	}

}
