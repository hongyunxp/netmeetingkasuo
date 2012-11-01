package com.meeting.gui.log;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

public class LogManager {

	private static boolean isConfigured = false;

	public static void configProperties(String path) {
		if (!isConfigured) {
			try {
				PropertyConfigurator.configure(path);
				isConfigured = true;
				System.out.println("config the log4j.properties successful.");
			} catch (Exception e) {

				throw new RuntimeException("Could not configure log4j.", e);
			}
		}
	}

	public static void configurationXML(String path) {
		if (!isConfigured) {
			try {
				DOMConfigurator.configure(path);
				System.out.println("config the log4j.properties successful.");
			} catch (Exception e) {

				throw new RuntimeException("Could not configure log4j.", e);
			}
		}

	}

	public static void setLevel(Level level) {

		org.apache.log4j.LogManager.getRootLogger().setLevel(level);
	}

	public static void update(String level) {
		org.apache.log4j.LogManager.getRootLogger().setLevel(
				Level.toLevel(level));
	}

	public static void main(String args[]) {
		configProperties("web/WEB-INF/config/log4j.properties");
		Logger logger = Logger.getLogger(LogManager.class);
		Logger logger1 = Logger.getLogger("opengwinfo");
		Logger logger2 = Logger.getLogger("atfgui");

		logger.info("info");
		logger.error("error");
		logger.warn("warn");
		logger1.info("B info.log");
		logger2.info("C result.log");
	}
}
