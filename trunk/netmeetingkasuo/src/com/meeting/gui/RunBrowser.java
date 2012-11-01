package com.meeting.gui;

import java.awt.Cursor;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JLabel;

/**
 * 
 * @author ���ฺ����ϵͳ��Ĭ��������ȳ��򣬲�������������
 * @netSite ָ��Ҫ��ʾ����ַ
 */
public class RunBrowser {
	private Desktop desktop;
	private URI uri;
	private String netSite;
	private Cursor hander;

	/** Creates a new instance of DesktopRuner */
	public RunBrowser(String site) {
		this.netSite = site;
		this.desktop = Desktop.getDesktop();
	}

	/**
	 * ���ϵͳ�Ƿ�֧�������
	 */
	public boolean checkBroswer() {
		if (Desktop.isDesktopSupported()
				&& desktop.isSupported(Desktop.Action.BROWSE)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 * @param site
	 */
	public void setNetSite(String site) {
		this.netSite = site;
	}

	/**
	 * ����Ĭ�������������������ʾָ����ַ
	 */
	public void runBroswer() {
		try {
			uri = new URI(netSite);
		} catch (URISyntaxException ex) {
			ex.printStackTrace();
		}
		try {
			desktop.browse(uri);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * �ı������״
	 */
	public void changeMouse(JLabel label) {
		hander = new Cursor(Cursor.HAND_CURSOR);
		label.setCursor(hander);
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		RunBrowser browser = new RunBrowser("http://www.baidu.com");
		browser.runBroswer();
	}
}
