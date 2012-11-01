package com.meeting.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

public interface WINSERVICEDLL extends Library {
	/**
	 * 
	 * ��ǰ·��������Ŀ�£������� bin ���Ŀ¼�¡�
	 */
	public static WINSERVICEDLL INSTANCE = (WINSERVICEDLL) Native.loadLibrary(
			"winservice", WINSERVICEDLL.class);

	public int GetServiceStatus(WString serName);

	public int SetServiceStartType(WString serName, int startType);
}
