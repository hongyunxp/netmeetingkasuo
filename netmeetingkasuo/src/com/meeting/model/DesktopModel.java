package com.meeting.model;

import java.io.Serializable;
import java.util.Properties;

public class DesktopModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2493385398399732951L;

	private int serverPort;

	private int clientPort;

	private String username;

	private String password;

	private String url;

	private String host;

	private String vncserverPassword;

	private String vncclientFullaccessPassword;

	private String vncclientViewonlyPassword;

	private String display;

	private Properties properties;

	private String serverId;

	private String viewerId;

	public DesktopModel() {

	}

	public DesktopModel(Properties p) {
		this.properties = p;
		this.serverPort = Integer.parseInt(p.getProperty("serverport"));
		this.clientPort = Integer.parseInt(p.getProperty("clientport"));
		this.display = p.getProperty("display");
		this.url = p.getProperty("url");
		this.username = p.getProperty("username");
		this.password = p.getProperty("password");
		this.host = p.getProperty("host");
		this.vncserverPassword = p.getProperty("vncserver_password");
		this.vncclientFullaccessPassword = p
				.getProperty("vncclient_fullaccess_password");
		this.vncclientViewonlyPassword = p
				.getProperty("vncclient_viewonly_password");
		this.serverId = p.getProperty("serverId");
		this.viewerId = p.getProperty("viewerId");
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public int getClientPort() {
		return clientPort;
	}

	public void setClientPort(int clientPort) {
		this.clientPort = clientPort;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getVncserverPassword() {
		return vncserverPassword;
	}

	public void setVncserverPassword(String vncserverPassword) {
		this.vncserverPassword = vncserverPassword;
	}

	public String getVncclientFullaccessPassword() {
		return vncclientFullaccessPassword;
	}

	public void setVncclientFullaccessPassword(
			String vncclientFullaccessPassword) {
		this.vncclientFullaccessPassword = vncclientFullaccessPassword;
	}

	public String getVncclientViewonlyPassword() {
		return vncclientViewonlyPassword;
	}

	public void setVncclientViewonlyPassword(String vncclientViewonlyPassword) {
		this.vncclientViewonlyPassword = vncclientViewonlyPassword;
	}

	public String getDisplay() {
		return display;
	}

	public void setDisplay(String display) {
		this.display = display;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public String getViewerId() {
		return viewerId;
	}

	public void setViewerId(String viewerId) {
		this.viewerId = viewerId;
	}

}
