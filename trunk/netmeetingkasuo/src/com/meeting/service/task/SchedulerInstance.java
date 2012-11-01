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
	 * ��ȡ����
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
	 * ����
	 */
	public void startSchedule() {
		try {
			SchedulerMetaData md = sd.getMetaData();
			logger.info("�̳߳ش�С�� " + md.getThreadPoolSize());
			logger.info("��ȡ��ܰ汾��" + md.getVersion());
			logger.info("���ƣ�" + md.getSchedulerName());
			sd.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ֹͣ
	 */
	public void shutdownSchedule() {
		try {
			sd.shutdown();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
}
