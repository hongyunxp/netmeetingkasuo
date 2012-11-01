package com.meeting.gui.log;


import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;

import com.meeting.gui.MainFrame;

public class LogAppender extends AppenderSkeleton {

	public static StringBuilder sb = new StringBuilder();

	public static void setSb(StringBuilder sb) {
		LogAppender.sb = sb;
	}

	public LogAppender() {
	}

	protected void append(LoggingEvent event) {
		MainFrame.log(event);
	}

	public void close() {
	}

	public boolean requiresLayout() {
		return false;
	}
}
