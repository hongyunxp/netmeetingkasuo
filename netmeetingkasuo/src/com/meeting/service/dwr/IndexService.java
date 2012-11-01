package com.meeting.service.dwr;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;

import com.meeting.dao.MeetingDao;
import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.service.task.ChatSaveSchedule;
import com.meeting.service.task.MeetingSchedule;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

public class IndexService extends DWRService {

	private static Logger logger = Logger.getLogger(IndexService.class);

	/**
	 * 锁定标签Map <meetingId,tabId>
	 */
	public static Map<String, String> tabLockMap = new HashMap<String, String>();
	/**
	 * 是否允许举手
	 */
	public static Map<String, Map<String,Integer>> allowHandupMap = new HashMap<String, Map<String,Integer>>();
	/**
	 * 是否允许远程协助
	 */
	public static Map<String, Map<String,Integer>> allowDtControlMap = new HashMap<String, Map<String,Integer>>();
	/**
	 * 是否允许电子白板
	 */
	public static Map<String, Map<String,Integer>> allowWboardMap = new HashMap<String, Map<String,Integer>>();

	/**
	 * 启动服务
	 * 
	 * @throws Exception
	 */
	public void startService() throws Exception {
		try {
			initSessionMap();
			initUsersMap();
			String meetingId = meeting.getMeetingId();
			String userName = curuser.getUsername();
			meetingState(meetingId);

			// 清除锁定的标签页
			tabLockMap.remove(meetingId);

			//
			String folderPath = AppConfigure.upload_path + "/"
					+ meeting.getMeetingId();
			File folderFile = new File(folderPath);
			if (!folderFile.exists()) {
				folderFile.mkdirs();
			}

			// 启动定时器
			if (curuser.getSessionid().equals(hostuser.getSessionid())) {
				MeetingSchedule.getInstance().startJob(meeting);
				ChatSaveSchedule.getInstance().startJob(meeting);
			}
			String msg = "用户【" + userName + "】进入会议！";
			logger.info("加载首页面...会议：" + meetingId + ", 用户：" + userName);
			logger.info(msg);
			showMsg(meetingId, msg);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new IndexServiceException("加载首页服务异常: " + e);
		}
	}

	/**
	 * 销毁页面
	 */
	public static void destroyInitService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		String meetingId = meeting.getMeetingId();
		String sessionId = user.getSessionid();
		logger.info("卸载首页面...会议：" + meetingId + ", 用户：" + user.getUsername());

		// 清理用户Map
		Map<String, UserModel> userMap = usersMap.get(meetingId);
		userMap.remove(user.getSessionid());
		usersMap.put(meetingId, userMap);

		// 清理会话Map
		Map<String, ScriptSession> sessionMap = sessionsMap.get(meetingId);
		sessionMap.remove(sessionId);
		sessionsMap.put(meetingId, sessionMap);
		showMsg(meetingId, "用户【" + user.getUsername() + "】离开会议！");

		// 停止次会议的定时器
		if (sessionId.equals(hostuser.getSessionid())) {
			MeetingSchedule.getInstance().stopJob(meeting);
			ChatSaveSchedule.getInstance().StopJob(meeting);
		}

		// 如果是最后一个人退出会议
		if (userMap.size() == 0) {
			logger.info("最后一个用户已经退出，结束会议");
			meeting.setState(AppConfigure.MEETING_ENDED);
			MeetingDao.getInstance().modMeeting(meeting);
		}
	}

	/**
	 * 锁定标签
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void contentTabLock(String id) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			tabLockMap.put(meetingId, id);
			SessionsCall(meetingId, "contentTabLockCallback", id);
			showMsg(meetingId, "主持人锁定当前标签页！");
		} catch (Exception e) {
			logger.error("锁定标签异常: " + StackTraceUtil.getStackTrace(e));
			throw new IndexServiceException("锁定标签异常: " + e);
		}
	}

	/**
	 * 解除锁定标签
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void contentTabUnlock() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			tabLockMap.remove(meetingId);
			SessionsCall(meetingId, "contentTabLockCallback", "");
		} catch (Exception e) {
			logger.error("锁定标签异常: " + StackTraceUtil.getStackTrace(e));
			throw new IndexServiceException("锁定标签异常: " + e);
		}
	}

	/**
	 * 激活标签
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void contentTabChanged(String id) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			SessionsCall(meetingId, "contentTabChangeCallback", id);
		} catch (Exception e) {
			logger.error("激活标签: " + id + "异常: "
					+ StackTraceUtil.getStackTrace(e));
			throw new IndexServiceException("激活标签: " + id + "异常: " + e);
		}
	}
}
