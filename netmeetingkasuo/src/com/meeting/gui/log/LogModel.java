package com.meeting.gui.log;

import java.util.Observable;

public class LogModel extends Observable {

	private StringBuffer buffer = new StringBuffer();

	public StringBuffer getBuffer() {
		return buffer;
	}

	public void setBuffer(StringBuffer b) {
		this.buffer = b;
		this.notifyObservers(buffer);
		this.setChanged();
	}

	public void appendString(String string) {
		this.buffer.append(string + "\n");
		this.notifyObservers(buffer);
		this.setChanged();
	}

}
