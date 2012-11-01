package com.meeting.web;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import com.meeting.Starter;
import com.meeting.dao.ConfigDao;
import com.meeting.gui.MainFrame;
import com.meeting.model.ConfigModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

public class WebServer {

	public static final String MODULE_NAME = "manage";

	public int port = 8408;

	private static final Logger logger = Logger.getLogger(WebServer.class);

	private static Server server = null;

	private static WebServer instance = null;

	public static boolean run = false;

	public static WebServer getInstance() {
		if (instance == null) {
			instance = new WebServer();
		}
		return instance;
	}

	/**
	 * 启动web服务器
	 * 
	 * @param port
	 */
	public void start(int port) {
		this.port = port;
		logger.info("Web服务器正在启动...端口：" + port);
		server = new Server();
		Connector conn = new SelectChannelConnector();
		conn.setPort(port);
		server.setConnectors(new Connector[] { conn });
		WebAppContext webapp = new WebAppContext();
		webapp.setContextPath("/");
		webapp.setWar("./web");
		server.setHandler(webapp);
		server.setSendServerVersion(false);
		try {
			server.start();
			run = true;
		} catch (Exception e) {
			run = false;
			e.printStackTrace();
		}
		logger.info("Web服务器启动成功，监听： " + port);
	}

	/**
	 * 停止web服务器
	 */
	public void stop() {
		logger.info("Web服务器停止");
		try {
			server.stop();
			run = false;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(StackTraceUtil.getStackTrace(e));
		}
		logger.info("Web服务器停止完成...");
	}

	/**
	 * 重启服务器
	 */
	public void restart(MainFrame instance) {
		if (run) {
			instance.updateJettyStatusOff();
			stop();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ConfigModel model = ConfigDao.getInstance().getConfig(
				AppConfigure.KEY_PORT, "5520");
		int port = Integer.valueOf(model.getValue());
		if (Starter.isPortExist(port)) {
			String msgString = "端口：" + port + " 已经被占用，请修改手动Web服务器端口!";
			logger.error(msgString);
			JOptionPane.showMessageDialog(null, msgString);
			logger.info("服务器启动失败");
		} else {
			instance.updateJettyStatusOn();
			start(port);
		}
	}

}
