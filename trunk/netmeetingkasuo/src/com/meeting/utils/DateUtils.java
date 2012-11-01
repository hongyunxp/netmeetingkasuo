package com.meeting.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

	/**
	 * 年月日 时间格式
	 */
	public static final String FORMAT1 = "yyyy-MM-dd";

	/**
	 * 年月日时分秒 时间格式
	 */
	public static final String FORMAT2 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 时分秒 时间格式
	 */
	public static final String FORMAT3 = "HH:mm:ss";

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT1);
		return dateFormat.format(new Date());
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT2);
		return dateFormat.format(new Date());
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getCurrentTime3() {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT3);
		return dateFormat.format(new Date());
	}

	/**
	 * 获取FORMAT1格式的字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT1);
		return dateFormat.format(date);
	}

	/**
	 * 获取FORMAT2格式的字符串
	 * 
	 * @param date
	 * @return
	 */
	public static String getTime(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(FORMAT2);
		return dateFormat.format(date);
	}

	/**
	 * 获取当前时间之后duration时间的Date
	 * 
	 * @param duration
	 *            毫秒数
	 * @return
	 */
	public static java.util.Date getSpecficDate(long duration) {
		long nowTime = new Date().getTime();
		long nextTime = nowTime + duration;
		return new Date(nextTime);
	}

	/**
	 * 根据日期字符串获取java.util.Date对象
	 * 
	 * @param format
	 *            日期格式
	 * @param dateStr
	 *            字符串日期
	 * @return java.util.Date对象
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
	 * 根据日期字符串获取java.sql.Date对象
	 * 
	 * @param format
	 *            日期格式
	 * @param dateStr
	 *            字符串日期
	 * @return java.sql.Date对象
	 */
	public static java.sql.Date toSqlDate(String format, String dateStr) {
		java.sql.Date date = new java.sql.Date(toUtilDate(format, dateStr)
				.getTime());
		return date;
	}

	/**
	 * 根据java.util.Date对象得到java.sql.Date对象
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
	 * 将毫秒数转化为 历时时间
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
		String timeString = hours + "时," + minutes + "分," + totalSec + "秒";
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
