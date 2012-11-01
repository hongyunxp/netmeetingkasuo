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
		logger.info("��ǰĿ¼��" + workdir);

		// 1. �������ݿ�
		HSqlService.start();

		// 2. ������Ŀ¼���µ����ݿ���
		if (!workdir.equals("")) {
			ConfigModel model = new ConfigModel();
			model.setName(AppConfigure.KEY_BASEPATH);
			model.setTime(DateUtils.getCurrentTime());
			model.setValue(workdir);
			ConfigDao.getInstance().addConfig(model);
		}

		// 3. ����UI���
		instance = new MainFrame();
		try {
			UIManager
					.setLookAndFeel("org.fife.plaf.Office2003.Office2003LookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
		instance.setVisible(true);
		instance.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		new SetTrayIcon(instance);// ����SetTrayIcon(JFrame)��,���ó���ϵͳ���̹���

		// 4. ����Web����
		int port = Integer.valueOf(ConfigDao.getInstance().getConfig(
				AppConfigure.KEY_PORT, "5520").getValue());
		if (isPortExist(port)) {
			String msgString = "�˿ڣ�" + port + " �Ѿ���ռ�ã����޸��ֶ�Web�������˿�!";
			logger.error(msgString);
			JOptionPane.showMessageDialog(null, msgString);
			logger.error("����������ʧ��");
		} else {
			WebServer.getInstance().start(port);
			instance.updateJettyStatusOn();
		}

		// 5. ����Apache������
		// instance.initApacheServer();
		// HttpdConfService.init();

		// 6. ����XMLRPC����
		CCVNCService.getInstance().startCcvncService(
				AppConfigure.ccvnc_web_port);

		// 7. ����Flash security ����
		new Thread(new Runnable() {
			public void run() {
				SecurityServer.start();
			}
		}).start();
		
		SplashImage.getInstance().hide();

		// 8. ����Red5fuwu
		Red5Service.startRed5Service();

	}

	/**
	 * �ж϶˿��Ƿ����
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
