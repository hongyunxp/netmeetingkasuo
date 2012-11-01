package com.meeting.utils;

import java.io.*;

/**
 * 
 * @Description: �쳣ջ��־���ַ���ת����
 * @author zcg
 * @date 2010-7-8 ����04:50:21
 *
 */
public final class StackTraceUtil {

	/**
	 * ��ȡĬ�ϵ��쳣��־
	 * @param aThrowable
	 * @return
	 */
	public static String getStackTrace(Throwable aThrowable) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}
	
	
	/**
	 * ����ָ����ʽ���쳣ջ��־
	 * @param aThrowable
	 * @return
	 */
	public static String getCustomStackTrace(Throwable aThrowable) {
		// add the class name and any message passed to constructor
		final StringBuilder result = new StringBuilder("NETMEETING-EXCEPTION: ");
		result.append(aThrowable.toString());
		final String NEW_LINE = System.getProperty("line.separator");
		result.append(NEW_LINE);

		// add each element of the stack trace
		for (StackTraceElement element : aThrowable.getStackTrace()) {
			result.append(element);
			result.append(NEW_LINE);
		}
		return result.toString();
	}

	/** Demonstrate output. */
	public static void main(String... aArguments) {
		final Throwable throwable = new IllegalArgumentException("Blah");
		System.out.println(getStackTrace(throwable));
		System.out.println(getCustomStackTrace(throwable));
	}
}
