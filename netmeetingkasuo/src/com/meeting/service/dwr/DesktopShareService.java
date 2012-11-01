package com.meeting.service.dwr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.io.FileTransfer;

import com.ccvnc.caller.Caller;
import com.meeting.dao.ConfigDao;
import com.meeting.model.ConfigModel;
import com.meeting.model.DesktopModel;
import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;
import com.meeting.utils.ZipUtils;

public class DesktopShareService extends DWRService {

	private static Logger logger = Logger.getLogger(DesktopShareService.class);

	/**
	 * ������Map <meetingId,<sspId,Timer>>
	 */
	public static Map<String, Map<String, Timer>> timerMap = new HashMap<String, Map<String, Timer>>();
	/**
	 * ��ʱ���������� <meetingId,<sspId,count>>
	 */
	public static Map<String, Map<String, Integer>> timerCountMap = new HashMap<String, Map<String, Integer>>();
	/**
	 * ���湲��Map <meetingId,[sspId,sspId]>
	 */
	public static Map<String, Map<String, DesktopModel>> sspIdMap = new HashMap<String, Map<String, DesktopModel>>();

	public static final int TIME_PERIOD = 5000, TIME_START = 5000,
			TIME_COUNT = 18;

	public void startService() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("�������湲�����...���飺" + meetingId + ", �û���"
					+ curuser.getUsername());
			Map<String, Timer> tMap = timerMap.get(meetingId);
			if (tMap == null) {
				tMap = new HashMap<String, Timer>();
				timerMap.put(meetingId, tMap);
			}
			Map<String, Integer> tcMap = timerCountMap.get(meetingId);
			if (tcMap == null) {
				tcMap = new HashMap<String, Integer>();
				timerCountMap.put(meetingId, tcMap);
			}
			Map<String, DesktopModel> dmMap = sspIdMap.get(meetingId);
			if (dmMap == null) {
				dmMap = new HashMap<String, DesktopModel>();
				sspIdMap.put(meetingId, dmMap);
			}

			ScriptSession ssession = sessionsMap.get(meetingId).get(
					curuser.getSessionid());
			Set<String> sspIdSet = dmMap.keySet();
			for (String key : sspIdSet) {
				DesktopModel dm = dmMap.get(key);
				if (dm != null) {
					ConfigModel cModel = ConfigDao.getInstance().getConfig(
							AppConfigure.KEY_SECURITY);
					int security = Integer.parseInt(cModel.getValue());
					SessionCall(ssession, "screenShareNotifyViewersCallback",
							key, dm.getClientPort(), dm
									.getVncclientFullaccessPassword(), security);
				}
			}
		} catch (Exception e) {
			logger.error("�������湲������쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopShareServiceException("�������湲������쳣: " + e);
		}
	}

	/**
	 * �������湲��
	 * 
	 * @param meeting
	 * @param user
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("ж�����湲�����...���飺" + meeting.getMeetingId() + ", �û���"
				+ user.getUsername());
		if (user.getSessionid().equals(hostuser.getSessionid())) {
			String meetingId = meeting.getMeetingId();
			if (timerMap.get(meeting) != null) {
				Map<String, Timer> tMap = timerMap.get(meetingId);
				Set<String> keysSet = tMap.keySet();
				for (String key : keysSet) {
					Timer timer = tMap.get(key);
					timer.cancel();
				}
				timerMap.remove(meetingId);
			}
			if (timerCountMap.get(meeting) != null) {
				timerCountMap.remove(meeting);
			}
			if (sspIdMap.get(meeting) != null) {
				Map<String, DesktopModel> dmMap = sspIdMap.remove(meeting);
				Set<String> sspSet = dmMap.keySet();
				for (String key : sspSet) {
					DesktopModel dm = dmMap.remove(key);
					sspIdMap.remove(meeting);
					boolean isShutdown = Caller.shutdownMeeting(dm
							.getUsername(), dm.getPassword(), dm.getDisplay(),
							dm.getProperties());
					if (isShutdown) {
						screenShareTabClose(meetingId, hostuser.getSessionid(),
								key);
					}
				}
			}
		}
	}

	/**
	 * ����VNC��������ʾ����vnc����Ӧ��ZIP�ļ�
	 * 
	 * @param sspId
	 * @param serverName
	 * @param password
	 * @throws Exception
	 */
	public FileTransfer screenShareStartNewVncProxy(String sspId,
			String serverName, String password) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, DesktopModel> dmMap = sspIdMap.get(meetingId);
			Map<String, Timer> tMap = timerMap.get(meetingId);
			Map<String, Integer> tcMap = timerCountMap.get(meetingId);

			// ����proxy���򿪷���˺Ϳͻ��˶˿�
			Properties p = getProperties(serverName, password);
			Properties p2 = copyProperties(p);

			String[] results = Caller.startVncProxy(p);

			int serverPort = Integer.parseInt(results[0]);
			int clientPort = Integer.parseInt(results[1]);
			String displayName = results[2];
			p2.setProperty("serverport", String.valueOf(serverPort));
			p2.setProperty("clientport", String.valueOf(clientPort));
			p2.setProperty("display", displayName);
			DesktopModel dm = new DesktopModel(p2);
			dmMap.put(sspId, dm);
			sspIdMap.put(meetingId, dmMap);
			ConfigModel cModel = ConfigDao.getInstance().getConfig(
					AppConfigure.KEY_SECURITY);
			logger.info("VNC�ͻ������� server[" + serverName + "]��port["
					+ clientPort + "]��password[" + password + "]��security["
					+ cModel.getValue() + "]");

			// ���zip�ļ�������ʾ�û�����
			String path = packZip(sspId, serverName, serverPort);

			File docFile = new File(path);
			long len = docFile.length();
			byte[] bytes = new byte[(int) len];
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream(docFile));
			int r = bufferedInputStream.read(bytes);
			if (r != len)
				throw new IOException("��ȡ�ļ�����ȷ");
			bufferedInputStream.close();

			// ������ʱ�����ظ�10�Σ������򽫹رմ�proxy
			Properties pro = getProperties(serverName, password);
			pro.setProperty("display", displayName);
			Timer timer = new Timer();
			timer.schedule(new VncServerStatusTimerTask(pro, sspId),
					TIME_PERIOD, TIME_PERIOD);
			tMap.put(sspId, timer);
			timerMap.put(meetingId, tMap);
			tcMap.put(sspId, 0);
			timerCountMap.put(meetingId, tcMap);

			return new FileTransfer(java.net.URLEncoder.encode("���湲�������.zip",
					"UTF-8"), "application/x-download", bytes);
		} catch (Exception e) {
			logger.error("����������ƴ����쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopShareServiceException("����������ƴ����쳣: " + e);
		}
	}

	/**
	 * ֪ͨ�ͻ��˹ۿ���������
	 * 
	 * @param sspId
	 * @throws Exception
	 */
	public void screenShareNotifyViewers(String sspId) throws Exception {
		try {
			ConfigModel cModel = ConfigDao.getInstance().getConfig(
					AppConfigure.KEY_SECURITY);
			if (cModel != null) {
				String meetingId = meeting.getMeetingId();
				Map<String, DesktopModel> dmMap = sspIdMap.get(meetingId);
				DesktopModel dm = dmMap.get(sspId);
				int clientPort = dm.getClientPort();
				String password = dm.getVncclientFullaccessPassword();
				int security = Integer.parseInt(cModel.getValue());
				SessionsExceptCall(meetingId, curuser.getSessionid(),
						"screenShareNotifyViewersCallback", sspId, clientPort,
						password, security);
				logger.info("֪ͨ�ͻ��˹ۿ����湲��port[" + clientPort + "], password["
						+ password + "], security[" + security + "]");
			} else {
				String emsg = "�������ð�ȫ�˿ڣ�";
				logger.error(emsg);
				throw new DesktopShareServiceException(emsg);
			}
		} catch (Exception e) {
			logger.error("֪ͨviewer�����쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopShareServiceException("֪ͨviewer�����쳣: " + e);
		}
	}

	/**
	 * �����˹ر����湲��
	 * 
	 * @param sspId
	 * @throws Exception
	 */
	public void screenShareShutdown(String sspId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, DesktopModel> dmMap = sspIdMap.get(meetingId);
			Map<String, Timer> tMap = timerMap.get(meetingId);
			Map<String, Integer> tcMap = timerCountMap.get(meetingId);
			if (tMap.get(sspId) != null) {
				tMap.get(sspId).cancel();
				tMap.remove(sspId);
				timerMap.put(meetingId, tMap);
			}
			if (tcMap.get(sspId) != null) {
				tcMap.remove(sspId);
				timerCountMap.put(meetingId, tcMap);
			}
			if (dmMap.get(sspId) != null) {
				DesktopModel dm = dmMap.remove(sspId);
				sspIdMap.put(meetingId, dmMap);
				try {
					boolean isShutdown = Caller.shutdownMeeting(dm
							.getUsername(), dm.getPassword(), dm.getDisplay(),
							dm.getProperties());
					if (isShutdown) {
						screenShareTabClose(meetingId, hostuser.getSessionid(),
								sspId);
					}
				} catch (Exception e) {
					logger.error("�ر����湲���쳣��" + StackTraceUtil.getStackTrace(e));
				}
			}

		} catch (Exception e) {
			logger.error("֪ͨviewer�����쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopShareServiceException("֪ͨviewer�����쳣: " + e);
		}
	}

	/**
	 * 
	 * @param meetingId
	 * @param sspId
	 */
	private static void screenShareTabClose(String meetingId, String hostSid,
			String sspId) throws Exception {
		SessionsExceptCall(meetingId, hostSid, "screenShareShutdownCallback",
				sspId);
	}

	/**
	 * ���zip
	 * 
	 * @param sspId
	 * @param host
	 * @param port
	 * @return ���ش�����ļ���·��
	 * @throws Exception
	 */
	private String packZip(String sspId, String host, int port)
			throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			String destFolderPath = AppConfigure.upload_path + "/" + meetingId
					+ "/" + AppConfigure.ssp_folder + "/" + sspId;
			File destFolderFile = new File(destFolderPath);
			if (!destFolderFile.exists()) {
				destFolderFile.mkdirs();
			}

			String srcDllPath = AppConfigure.ssp_path + "/"
					+ AppConfigure.ssp_dll_name;
			String srcExePath = AppConfigure.ssp_path + "/"
					+ AppConfigure.ssp_exe_name;

			String destExeName = "ssp_" + host + "_" + port + ".exe";

			FileHelper.copy(new File(srcDllPath), new File(destFolderFile
					+ "/ssp.dll"));
			FileHelper.copy(new File(srcExePath), new File(destFolderPath + "/"
					+ destExeName));

			String path = AppConfigure.upload_path + "/" + meetingId + "/"
					+ AppConfigure.ssp_folder + "/" + sspId + ".zip";
			ZipUtils.getInstance().zip(destFolderPath, path);

			return path;
		} catch (Exception e) {
			logger.error("���zip�ļ��쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopShareServiceException("���zip�ļ��쳣: " + e);
		}
	}

	/**
	 * ��ȡ����
	 * 
	 * @param serverName
	 * @param password
	 * @return
	 */
	private Properties getProperties(String serverName, String password) {
		Properties p = new Properties();
		String url = "http://" + serverName + ":" + AppConfigure.ccvnc_web_port;
		p.setProperty("action", "new_meeting");
		p.setProperty("url", url);
		p.setProperty("username", "admin");
		p.setProperty("password", "adminpass");
		p.setProperty("host", serverName);
		p.setProperty("vncserver_password", password);
		p.setProperty("vncclient_fullaccess_password", password);
		p.setProperty("vncclient_viewonly_password", password);
		return p;
	}

	/**
	 * 
	 * @param pro
	 * @return
	 */
	private Properties copyProperties(Properties pro) {
		Properties properties = new Properties();
		Enumeration<Object> enums = pro.keys();
		while (enums.hasMoreElements()) {
			String key = (String) enums.nextElement();
			properties.setProperty(key, pro.getProperty(key));
		}
		return properties;
	}

	/**
	 * 
	 * @author Administrator
	 * 
	 */
	class VncServerStatusTimerTask extends TimerTask {

		private Properties properties;
		private String sspId;

		public VncServerStatusTimerTask(Properties properties, String sspId) {
			this.properties = properties;
			this.sspId = sspId;
		}

		public void run() {
			try {
				Properties pro = copyProperties(properties);
				String meetingId = meeting.getMeetingId();
				ScriptSession ssession = sessionsMap.get(meetingId).get(
						curuser.getSessionid());
				Map<String, Integer> tcMap = timerCountMap.get(meetingId);
				int timerCount = tcMap.get(sspId);
				boolean flag = Caller.listenToServerStatusAction(pro);
				if (flag || timerCount >= TIME_COUNT) {
					Map<String, Timer> tMap = timerMap.get(meetingId);
					Timer timer = tMap.get(sspId);
					if (timer != null) {
						timer.cancel();
						tMap.remove(sspId);
						timerMap.put(meetingId, tMap);
						SessionCall(ssession,
								"screenShareServerConnectedCallback", sspId,
								flag);

					}
				}
				int leftTime = (TIME_COUNT * TIME_PERIOD - timerCount
						* TIME_PERIOD) / 1000;
				timerCount++;
				tcMap.put(sspId, timerCount);
				timerCountMap.put(meetingId, tcMap);
				SessionCall(ssession, "screenShareServerWaitStatusCallback",
						sspId, leftTime);
				logger.info("����[" + meetingId + "] �����湲��ʣ��ʱ��: " + leftTime
						+ "��");
			} catch (Exception e) {
				logger.error("��ȡVNC����״̬XMLRPC�쳣��"
						+ StackTraceUtil.getStackTrace(e));
			}
		}
	}
}
