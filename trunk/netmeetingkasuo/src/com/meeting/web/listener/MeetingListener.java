package com.meeting.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.meeting.service.task.CleanSchedule;
import com.meeting.service.task.SchedulerInstance;
import com.meeting.utils.StackTraceUtil;

public class MeetingListener implements ServletContextListener {

	private static Logger logger = Logger.getLogger(MeetingListener.class);

	/**
	 * 启动
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("定时器启动。。。");
		SchedulerInstance sdi = SchedulerInstance.getInstance();
		sdi.startSchedule();
		CleanSchedule.getInstance().startJob();
	}

	/**
	 * 销毁
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("定时器关闭。。。");
		try {
			CleanSchedule.getInstance().StopAll();
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		SchedulerInstance sdi = SchedulerInstance.getInstance();
		sdi.shutdownSchedule();
	}

}
