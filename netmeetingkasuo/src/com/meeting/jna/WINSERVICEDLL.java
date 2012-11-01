package com.meeting.jna;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;

public interface WINSERVICEDLL extends Library {
	/**
	 * 
	 * 当前路径是在项目下，而不是 bin 输出目录下。
	 */
	public static WINSERVICEDLL INSTANCE = (WINSERVICEDLL) Native.loadLibrary(
			"winservice", WINSERVICEDLL.class);

	public int GetServiceStatus(WString serName);

	public int SetServiceStartType(WString serName, int startType);
}
