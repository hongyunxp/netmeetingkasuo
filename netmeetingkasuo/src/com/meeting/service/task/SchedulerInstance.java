package com.meeting.service.task;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.StdSchedulerFactory;

public class SchedulerInstance {

	private static Scheduler sd = null;
	private static SchedulerInstance instance = null;
	private static Logger logger = Logger.getLogger(SchedulerInstance.class);

	private SchedulerInstance() {
	};

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static SchedulerInstance getInstance() {
		if (instance == null) {
			instance = new SchedulerInstance();
			try {
				sd = StdSchedulerFactory.getDefaultScheduler();
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	/**
	 * 
	 * @return
	 */
	public Scheduler getScheduler() {
		return sd;
	}

	/**
	 * 启动
	 */
	public void startSchedule() {
		try {
			SchedulerMetaData md = sd.getMetaData();
			logger.info("线程池大小： " + md.getThreadPoolSize());
			logger.info("调取框架版本：" + md.getVersion());
			logger.info("名称：" + md.getSchedulerName());
			sd.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 停止
	 */
	public void shutdownSchedule() {
		try {
			sd.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
