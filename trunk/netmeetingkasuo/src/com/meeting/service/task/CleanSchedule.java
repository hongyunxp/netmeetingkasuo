package com.meeting.service.task;

import java.util.Date;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import com.meeting.dao.ConfigDao;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

public class CleanSchedule {
	private static CleanSchedule instance = null;
	private static Logger logger = Logger.getLogger(MeetingSchedule.class);
	public static String TRIGER_NAME = "TRIGER_NAME_MEETING";
	public static String TRIGER_GROUP = "TRIGER_GROUP_MEETINIG";
	public static String JOB_NAME = "JOB_NAME_MEETING";
	public static String JOB_GROUP = "JOB_GROUP_GROUP";
	public static String TASK_CYCLE_NAME = "cycle";

	private CleanSchedule() {
	};

	public static CleanSchedule getInstance() {
		if (instance == null) {
			instance = new CleanSchedule();
		}
		return instance;
	}

	/**
	 * ��������
	 * 
	 * @return
	 * */
	public void startJob() {
		Scheduler sched = SchedulerInstance.getInstance().getScheduler();
		String cronString = ConfigDao.getInstance().getConfig(
				AppConfigure.KEY_CLEANINTERVAL).getValue();
		try {
			cronString = "0 0/" + cronString + " * * * ?";
			logger.info("ѭ����ʱ����ʼ: \"" + cronString + "\"");
			CronTrigger trigger = new CronTrigger(TRIGER_NAME, TRIGER_GROUP,
					JOB_NAME, JOB_GROUP, cronString);
			trigger.setStartTime(new Date());
			JobDetail job = new JobDetail(JOB_NAME, JOB_GROUP, CleanJob.class);
			sched.addJob(job, true);
			sched.scheduleJob(trigger);
			sched.start();
		} catch (Exception e) {
			logger.error("��������ʧ�ܣ�" + StackTraceUtil.getStackTrace(e));
		}

	}

	/**
	 * ֹͣ���е�����
	 */
	public void StopAll() {
		Scheduler sd = SchedulerInstance.getInstance().getScheduler();
		String[] jobs = null;
		try {
			String[] groups = sd.getJobGroupNames();
			for (int j = 0; j < groups.length; j++) {
				logger.info(groups[j]);
				jobs = sd.getJobNames(groups[j]);
				for (int i = 0; i < jobs.length; i++) {
					logger.info("�ҵ����� " + jobs[i]);
					sd.unscheduleJob(jobs[i], groups[j]);
					sd.deleteJob(jobs[i], groups[j]);
					logger.info("ֹͣ����ɹ���");
				}

			}
		} catch (SchedulerException e) {

			e.printStackTrace();
		}
	}
}
