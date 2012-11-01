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
	 * 启动任务
	 * 
	 * @return
	 * */
	public void startJob() {
		Scheduler sched = SchedulerInstance.getInstance().getScheduler();
		String cronString = ConfigDao.getInstance().getConfig(
				AppConfigure.KEY_CLEANINTERVAL).getValue();
		try {
			cronString = "0 0/" + cronString + " * * * ?";
			logger.info("循环定时任务开始: \"" + cronString + "\"");
			CronTrigger trigger = new CronTrigger(TRIGER_NAME, TRIGER_GROUP,
					JOB_NAME, JOB_GROUP, cronString);
			trigger.setStartTime(new Date());
			JobDetail job = new JobDetail(JOB_NAME, JOB_GROUP, CleanJob.class);
			sched.addJob(job, true);
			sched.scheduleJob(trigger);
			sched.start();
		} catch (Exception e) {
			logger.error("启动任务失败：" + StackTraceUtil.getStackTrace(e));
		}

	}

	/**
	 * 停止所有的任务
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
					logger.info("找到任务 " + jobs[i]);
					sd.unscheduleJob(jobs[i], groups[j]);
					sd.deleteJob(jobs[i], groups[j]);
					logger.info("停止任务成功！");
				}

			}
		} catch (SchedulerException e) {

			e.printStackTrace();
		}
	}
}
