package com.meeting.service.dwr;

public class SessionWrapper {

	private String sessionId = null;

	private String className = null;

	public SessionWrapper() {
	}

	public SessionWrapper(String s, String c) {
		sessionId = s;
		className = c;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean equals(Object anObject) {
		if (anObject instanceof SessionWrapper) {
			SessionWrapper wrapper = (SessionWrapper) anObject;
			if (wrapper.getClassName().equals(this.getClassName())
					&& wrapper.getSessionId().equals(this.getSessionId())) {
				return true;
			}
		}
		return false;
	}
}
