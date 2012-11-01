package com.meeting;

import java.io.File;
import java.net.ServerSocket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import com.meeting.dao.ConfigDao;
import com.meeting.gui.MainFrame;
import com.meeting.gui.SetTrayIcon;
import com.meeting.model.ConfigModel;
import com.meeting.service.CCVNCService;
import com.meeting.service.HSqlService;
import com.meeting.service.Red5Service;
import com.meeting.service.policy.SecurityServer;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.DateUtils;
import com.meeting.web.WebServer;

public class Starter {

	private static Logger logger = Logger.getLogger(Starter.class);

	private static MainFrame instance = null;

	public static void start(String workdir) {
		SplashImage.getInstance().show();
		AppConfigure.configLog4j();
		logger.info("当前目录：" + workdir);

		// 1. 启动数据库
		HSqlService.start();

		// 2. 将工作目录更新到数据库中
		if (!workdir.equals("")) {
			ConfigModel model = new ConfigModel();
			model.setName(AppConfigure.KEY_BASEPATH);
			model.setTime(DateUtils.getCurrentTime());
			model.setValue(workdir);
			ConfigDao.getInstance().addConfig(model);
		}

		// 3. 启动UI组件
		instance = new MainFrame();
		try {
			UIManager
					.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		instance.setVisible(true);
		instance.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		new SetTrayIcon(instance);// 调用SetTrayIcon(JFrame)类,设置程序系统托盘功能

		// 4. 启动Web容器
		int port = Integer.valueOf(ConfigDao.getInstance().getConfig(
				AppConfigure.KEY_PORT, "5520").getValue());
		if (isPortExist(port)) {
			String msgString = "端口：" + port + " 已经被占用，请修改手动Web服务器端口!";
			logger.error(msgString);
			JOptionPane.showMessageDialog(null, msgString);
			logger.error("服务器启动失败");
		} else {
			WebServer.getInstance().start(port);
			instance.updateJettyStatusOn();
		}

		// 5. 启动Apache服务器
		// instance.initApacheServer();
		// HttpdConfService.init();

		// 6. 启动XMLRPC服务
		CCVNCService.getInstance().startCcvncService(
				AppConfigure.ccvnc_web_port);

		// 7. 启动Flash security 服务
		new Thread(new Runnable() {
			public void run() {
				SecurityServer.start();
			}
		}).start();
		
		SplashImage.getInstance().hide();

		// 8. 启动Red5fuwu
		Red5Service.startRed5Service();

	}

	/**
	 * 判断端口是否存在
	 * 
	 * @param port
	 * @return
	 */
	public static boolean isPortExist(int port) {
		try {
			ServerSocket ss = new ServerSocket(port);
			ss.close();
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File file = new File("winservice.dll");
		String filePath = file.getAbsolutePath();
		filePath = filePath.replaceAll("\\\\", "/");
		filePath = filePath.substring(0, filePath.lastIndexOf("/"));
		start(filePath);
	}

}
