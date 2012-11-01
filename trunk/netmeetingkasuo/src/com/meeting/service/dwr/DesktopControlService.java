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

public class DesktopControlService extends DWRService {

	private static Logger logger = Logger
			.getLogger(DesktopControlService.class);

	/**
	 * 计数器Map <meetingId,<sspId,Timer>>
	 */
	public static Map<String, Map<String, Timer>> timerMap = new HashMap<String, Map<String, Timer>>();
	/**
	 * 定时器计数次数 <meetingId,<sspId,count>>
	 */
	public static Map<String, Map<String, Integer>> timerCountMap = new HashMap<String, Map<String, Integer>>();
	/**
	 * 远程协助Map <meetingId,[sspId,sspId]>
	 */
	public static Map<String, Map<String, DesktopModel>> sspIdMap = new HashMap<String, Map<String, DesktopModel>>();

	public static final int TIME_PERIOD = 5000, TIME_START = 5000,
			TIME_COUNT = 18;

	public void startService() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("加载远程协助面板...会议：" + meetingId + ", 用户："
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
					if (dm.getViewerId().equals(curuser.getSessionid())) {
						ConfigModel cModel = ConfigDao.getInstance().getConfig(
								AppConfigure.KEY_SECURITY);
						int security = Integer.parseInt(cModel.getValue());
						SessionCall(ssession,
								"screenControlNotifyViewersCallback", key, dm
										.getClientPort(), dm
										.getVncclientFullaccessPassword(),
								security);
					}
				}
			}
		} catch (Exception e) {
			logger.error("加载远程协助面板异常: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopControlServiceException("加载远程协助面板异常: " + e);
		}
	}

	/**
	 * 销毁远程协助
	 * 
	 * @param meeting
	 * @param user
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("卸载远程协助面板...会议：" + meeting.getMeetingId() + ", 用户："
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
						screenControlTabClose(meetingId, hostuser
								.getSessionid(), key);
					}
				}
			}
		}
	}

	/**
	 * 远程控制确认
	 * 
	 * @param serverId
	 * @param viewerId
	 */
	public void screenControlConfrim(String serverId, String viewerId)
			throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			ScriptSession serverSession = sessionsMap.get(meetingId).get(
					serverId);
			ScriptSession viewerSession = sessionsMap.get(meetingId).get(
					viewerId);
			UserModel viewModel = usersMap.get(meetingId).get(viewerId);

			// 判断是否已经打开远程协助
			Map<String, DesktopModel> dmMap = sspIdMap.get(meetingId);
			Set<String> keysSet = dmMap.keySet();
			for (String key : keysSet) {
				DesktopModel dm = dmMap.get(key);
				if (dm.getServerId().equals(serverId)) {
					showMsg(serverSession, "对不起，您只能打开一个远程协助...");
					return;
				}
			}

			SessionCall(viewerSession, "screenControlConfrimCallback",
					viewModel.getUsername(), serverId, viewerId);

		} catch (Exception e) {
			logger.error("远程控制确认异常: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopControlServiceException("远程控制确认异常: " + e);
		}
	}

	/**
	 * 远程控制确认结果
	 * 
	 * @param serverId
	 * @param viewerId
	 * @throws Exception
	 */
	public void screenControlConfrimResult(String serverId, String viewerId,
			boolean flag) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			UserModel viewModel = usersMap.get(meetingId).get(viewerId);
			ScriptSession serverSession = sessionsMap.get(meetingId).get(
					serverId);
			if (flag) {
				SessionCall(serverSession,
						"screenControlConfrimResultOKCallback", serverId,
						viewerId);
				showMsg(serverSession, "【" + viewModel.getUsername()
						+ "】同意远程协助");
			} else {
				SessionCall(serverSession,
						"screenControlConfrimResultNOCallback", viewModel
								.getUsername(), serverId, viewerId);
				showMsg(serverSession, "【" + viewModel.getUsername()
						+ "】不同意远程协助");
			}
		} catch (Exception e) {
			logger.error("远程控制确认异常: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopControlServiceException("远程控制确认异常: " + e);
		}
	}

	/**
	 * 创建VNC代理，并提示下载vnc服务应用ZIP文件
	 * 
	 * @param sspId
	 * @param serverName
	 * @param password
	 * @throws Exception
	 */
	public FileTransfer screenControlStartNewVncProxy(String sspId,
			String serverName, String password, String serverId, String viewerId)
			throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, DesktopModel> dmMap = sspIdMap.get(meetingId);
			Map<String, Timer> tMap = timerMap.get(meetingId);
			Map<String, Integer> tcMap = timerCountMap.get(meetingId);

			// 启动proxy，打开服务端和客户端端口
			Properties p = getProperties(serverName, password);
			Properties p2 = copyProperties(p);

			String[] results = Caller.startVncProxy(p);

			int serverPort = Integer.parseInt(results[0]);
			int clientPort = Integer.parseInt(results[1]);
			String displayName = results[2];
			p2.setProperty("serverport", String.valueOf(serverPort));
			p2.setProperty("clientport", String.valueOf(clientPort));
			p2.setProperty("display", displayName);
			p2.setProperty("serverId", serverId);
			p2.setProperty("viewerId", viewerId);
			DesktopModel dm = new DesktopModel(p2);
			dmMap.put(sspId, dm);
			sspIdMap.put(meetingId, dmMap);
			ConfigModel cModel = ConfigDao.getInstance().getConfig(
					AppConfigure.KEY_SECURITY);
			logger.info("VNC客户端连接 server[" + serverName + "]，port["
					+ clientPort + "]，password[" + password + "]，security["
					+ cModel.getValue() + "], serverId[" + serverId
					+ "], viewerId[" + viewerId + "]");

			// 打包zip文件，并提示用户下载
			String path = packZip(sspId, serverName, serverPort);

			File docFile = new File(path);
			long len = docFile.length();
			byte[] bytes = new byte[(int) len];
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream(docFile));
			int r = bufferedInputStream.read(bytes);
			if (r != len)
				throw new IOException("读取文件不正确");
			bufferedInputStream.close();

			// 启动定时器，重复10次，超过则将关闭此proxy
			Properties pro = getProperties(serverName, password);
			pro.setProperty("display", displayName);
			Timer timer = new Timer();
			timer.schedule(new VncServerStatusTimerTask(pro, sspId, serverId,
					viewerId), TIME_PERIOD, TIME_PERIOD);
			tMap.put(sspId, timer);
			timerMap.put(meetingId, tMap);
			tcMap.put(sspId, 0);
			timerCountMap.put(meetingId, tcMap);

			return new FileTransfer(java.net.URLEncoder.encode("远程协助控制器.zip",
					"UTF-8"), "application/x-download", bytes);
		} catch (Exception e) {
			logger.error("创建桌面控制代理异常: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopControlServiceException("创建桌面控制代理异常: " + e);
		}
	}

	/**
	 * 通知客户端观看共享桌面
	 * 
	 * @param sspId
	 * @throws Exception
	 */
	public void screenControlNotifyViewers(String sspId, String serverId,
			String viewerId) throws Exception {
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
				ScriptSession ssession = sessionsMap.get(meetingId).get(
						viewerId);
				SessionCall(ssession, "screenControlNotifyViewersCallback",
						sspId, clientPort, password, security);

				logger.info("通知客户端观看远程协助，port[" + clientPort + "], password["
						+ password + "], security[" + security + "]");
			} else {
				String emsg = "尚无配置安全端口！";
				logger.error(emsg);
				throw new DesktopControlServiceException(emsg);
			}
		} catch (Exception e) {
			logger.error("通知viewer创建异常: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopControlServiceException("通知viewer创建异常: " + e);
		}
	}

	/**
	 * 主持人关闭远程协助
	 * 
	 * @param sspId
	 * @throws Exception
	 */
	public void screenControlShutdown(String sspId) throws Exception {
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
						screenControlTabClose(meetingId, dm.getViewerId(),
								sspId);
					}
				} catch (Exception e) {
					logger.error("关闭远程协助异常：" + StackTraceUtil.getStackTrace(e));
				}
			}

		} catch (Exception e) {
			logger.error("通知viewer创建异常: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopControlServiceException("通知viewer创建异常: " + e);
		}
	}

	/**
	 * 
	 * @param meetingId
	 * @param sspId
	 */
	private static void screenControlTabClose(String meetingId,
			String sessionId, String sspId) throws Exception {
		ScriptSession session = sessionsMap.get(meetingId).get(sessionId);
		SessionCall(session, "screenControlShutdownCallback", sspId);
	}

	/**
	 * 打包zip
	 * 
	 * @param sspId
	 * @param host
	 * @param port
	 * @return 返回打包后文件的路径
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
			logger.error("打包zip文件异常: " + StackTraceUtil.getStackTrace(e));
			throw new DesktopControlServiceException("打包zip文件异常: " + e);
		}
	}

	/**
	 * 获取属性
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
		private String serverId;
		private String viewerId;

		public VncServerStatusTimerTask(Properties properties, String sspId,
				String serverId, String viewerId) {
			this.properties = properties;
			this.sspId = sspId;
			this.serverId = serverId;
			this.viewerId = viewerId;
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
								"screenControlServerConnectedCallback", sspId,
								flag, serverId, viewerId);

					}
				}
				int leftTime = (TIME_COUNT * TIME_PERIOD - timerCount
						* TIME_PERIOD) / 1000;
				timerCount++;
				tcMap.put(sspId, timerCount);
				timerCountMap.put(meetingId, tcMap);
				SessionCall(ssession, "screenControlServerWaitStatusCallback",
						sspId, leftTime);
				logger.info("会议[" + meetingId + "] 的远程协助剩余时间: " + leftTime
						+ "秒");
			} catch (Exception e) {
				logger.error("获取VNC服务状态XMLRPC异常："
						+ StackTraceUtil.getStackTrace(e));
			}
		}
	}
}
