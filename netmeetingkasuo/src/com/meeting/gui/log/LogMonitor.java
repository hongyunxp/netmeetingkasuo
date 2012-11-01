package com.meeting.gui.log;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.meeting.gui.MainFrame;

public class LogMonitor extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8353943006918276175L;

	private JLogList logArea;

	private MainFrame instance = null;

	public LogMonitor(MainFrame instance) {
		this.setBorder(BorderFactory.createTitledBorder(""));
		this.setLayout(new BorderLayout());
		this.instance = instance;
	}

	public JLogList addLogArea(String title, String loggerName,
			boolean isDefault) {
		logArea = new JLogList(instance, title);
		logArea.setLevel(Level.INFO);
		logArea.addLogger(loggerName, !isDefault);
		this.add(logArea, BorderLayout.CENTER);
		return logArea;
	}

	public void logEvent(Object msg) {
		if (msg instanceof LoggingEvent) {
			logArea.addLine(msg);
		}
	}

	public boolean hasLogArea(String loggerName) {
		if (logArea.monitors(loggerName)) {
			return true;
		}
		return false;
	}
}
