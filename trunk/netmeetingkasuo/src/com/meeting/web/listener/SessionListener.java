package com.meeting.web.listener;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.log4j.Logger;

public class SessionListener implements HttpSessionListener {

	private static Logger logger = Logger.getLogger(SessionListener.class);

	public void sessionCreated(HttpSessionEvent arg0) {
		logger.info("Session´´½¨£º" + arg0.getSession().getId());
	}

	public void sessionDestroyed(HttpSessionEvent arg0) {
		logger.info("SessionÏú»Ù£º" + arg0.getSession().getId());
	}

}
