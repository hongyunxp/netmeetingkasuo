package com.meeting.model;

import java.io.Serializable;

public class ConfigModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7461695214113467830L;

	private String userid;
	private String name;
	private String value;
	private String time;

	public ConfigModel() {

	}

	public ConfigModel(String userid, String name, String value, String time) {
		this.userid = userid;
		this.name = name;
		this.value = value;
		this.time = time;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String toString() {
		return "ConfigModel [name=" + name + ", time=" + time + ", userid="
				+ userid + ", value=" + value + "]";
	}

}
