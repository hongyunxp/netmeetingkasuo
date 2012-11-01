package com.meeting.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**
	 * ������ ʱ���ʽ
	 */
	public static final String FORMAT1 = "yyyy-MM-dd";

	/**
	 * ������ʱ���� ʱ���ʽ
	 */
	public static final String FORMAT2 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * ʱ���� ʱ���ʽ
	 */
	public static final String FORMAT3 = "HH:mm:ss";

	/**
	 * ��ȡ��ǰ����
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT1);
		return dateFormat.format(new Date());
	}

	/**
	 * ��ȡ��ǰʱ��
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT2);
		return dateFormat.format(new Date());
	}

	/**
	 * ��ȡ��ǰʱ��
	 * 
	 * @return
	 */
	public static String getCurrentTime3() {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT3);
		return dateFormat.format(new Date());
	}

	/**
	 * ��ȡFORMAT1��ʽ���ַ���
	 * 
	 * @param date
	 * @return
	 */
	public static String getDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT1);
		return dateFormat.format(date);
	}

	/**
	 * ��ȡFORMAT2��ʽ���ַ���
	 * 
	 * @param date
	 * @return
	 */
	public static String getTime(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT2);
		return dateFormat.format(date);
	}

	/**
	 * ��ȡ��ǰʱ��֮��durationʱ���Date
	 * 
	 * @param duration
	 *            ������
	 * @return
	 */
	public static java.util.Date getSpecficDate(long duration) {
		long nowTime = new Date().getTime();
		long nextTime = nowTime + duration;
		return new Date(nextTime);
	}

	/**
	 * ���������ַ�����ȡjava.util.Date����
	 * 
	 * @param format
	 *            ���ڸ�ʽ
	 * @param dateStr
	 *            �ַ�������
	 * @return java.util.Date����
	 */
	public static java.util.Date toUtilDate(String format, String dateStr) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		java.util.Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * ���������ַ�����ȡjava.sql.Date����
	 * 
	 * @param format
	 *            ���ڸ�ʽ
	 * @param dateStr
	 *            �ַ�������
	 * @return java.sql.Date����
	 */
	public static java.sql.Date toSqlDate(String format, String dateStr) {
		java.sql.Date date = new java.sql.Date(toUtilDate(format, dateStr)
				.getTime());
		return date;
	}

	/**
	 * ����java.util.Date����õ�java.sql.Date����
	 * 
	 * @param date
	 * @return
	 */
	public static java.sql.Date toSqlDate(java.util.Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String time = sdf.format(date);
		return toSqlDate("yyyy-MM-dd", time);
	}

	/**
	 * ��������ת��Ϊ ��ʱʱ��
	 * 
	 * @param minsec
	 * @return
	 */
	public static String handleTime(long minsec) {
		int totalSec = (int) (minsec / 1000);
		int hours = totalSec / 3600;
		if (hours > 0) {
			totalSec = totalSec % 3600;
		}
		int minutes = totalSec / 60;
		if (minutes > 0) {
			totalSec = totalSec % 60;
		}
		String timeString = hours + "ʱ," + minutes + "��," + totalSec + "��";
		return timeString;
	}

	public static long toUtilTime(String format, String dateStr) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		java.util.Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date.getTime();
	}
}
