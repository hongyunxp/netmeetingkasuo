package com.meeting.service.task;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;


public class CleanJob implements Job{
	
	private static Logger logger = Logger.getLogger(CleanJob.class);
	
	/**
	 * �����ڴ���
	 */
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		// �����ݿ��в����Ѿ������Ļ��飬����˻�����ڴ棬
		// �����ݿ��в���δ������Ļ��飬����ʱ���Ѿ���ʱ������˻�����ڴ�
		logger.info("������飡");
	}
}
