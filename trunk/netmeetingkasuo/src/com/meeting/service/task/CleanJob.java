package com.meeting.service.task;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class CleanJob implements Job{
	
	private static Logger logger = Logger.getLogger(CleanJob.class);
	
	/**
	 * 清理内存中
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// 从数据库中查找已经结束的会议，清理此会议的内存，
		// 从数据库中查找未结束句的会议，但是时间已经超时，清理此会议的内存
		logger.info("清理会议！");
	}
}
