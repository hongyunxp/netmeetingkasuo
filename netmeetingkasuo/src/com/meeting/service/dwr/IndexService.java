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
	 * ������ǩMap <meetingId,tabId>
	 */
	public static Map<String, String> tabLockMap = new HashMap<String, String>();
	/**
	 * �Ƿ��������
	 */
	public static Map<String, Map<String,Integer>> allowHandupMap = new HashMap<String, Map<String,Integer>>();
	/**
	 * �Ƿ�����Զ��Э��
	 */
	public static Map<String, Map<String,Integer>> allowDtControlMap = new HashMap<String, Map<String,Integer>>();
	/**
	 * �Ƿ�������Ӱװ�
	 */
	public static Map<String, Map<String,Integer>> allowWboardMap = new HashMap<String, Map<String,Integer>>();

	/**
	 * ��������
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

			// ��������ı�ǩҳ
			tabLockMap.remove(meetingId);

			//
			String folderPath = AppConfigure.upload_path + "/"
					+ meeting.getMeetingId();
			File folderFile = new File(folderPath);
			if (!folderFile.exists()) {
				folderFile.mkdirs();
			}

			// ������ʱ��
			if (curuser.getSessionid().equals(hostuser.getSessionid())) {
				MeetingSchedule.getInstance().startJob(meeting);
				ChatSaveSchedule.getInstance().startJob(meeting);
			}
			String msg = "�û���" + userName + "��������飡";
			logger.info("������ҳ��...���飺" + meetingId + ", �û���" + userName);
			logger.info(msg);
			showMsg(meetingId, msg);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new IndexServiceException("������ҳ�����쳣: " + e);
		}
	}

	/**
	 * ����ҳ��
	 */
	public static void destroyInitService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		String meetingId = meeting.getMeetingId();
		String sessionId = user.getSessionid();
		logger.info("ж����ҳ��...���飺" + meetingId + ", �û���" + user.getUsername());

		// �����û�Map
		Map<String, UserModel> userMap = usersMap.get(meetingId);
		userMap.remove(user.getSessionid());
		usersMap.put(meetingId, userMap);

		// ����ỰMap
		Map<String, ScriptSession> sessionMap = sessionsMap.get(meetingId);
		sessionMap.remove(sessionId);
		sessionsMap.put(meetingId, sessionMap);
		showMsg(meetingId, "�û���" + user.getUsername() + "���뿪���飡");

		// ֹͣ�λ���Ķ�ʱ��
		if (sessionId.equals(hostuser.getSessionid())) {
			MeetingSchedule.getInstance().stopJob(meeting);
			ChatSaveSchedule.getInstance().StopJob(meeting);
		}

		// ��������һ�����˳�����
		if (userMap.size() == 0) {
			logger.info("���һ���û��Ѿ��˳�����������");
			meeting.setState(AppConfigure.MEETING_ENDED);
			MeetingDao.getInstance().modMeeting(meeting);
		}
	}

	/**
	 * ������ǩ
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void contentTabLock(String id) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			tabLockMap.put(meetingId, id);
			SessionsCall(meetingId, "contentTabLockCallback", id);
			showMsg(meetingId, "������������ǰ��ǩҳ��");
		} catch (Exception e) {
			logger.error("������ǩ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new IndexServiceException("������ǩ�쳣: " + e);
		}
	}

	/**
	 * ���������ǩ
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
			logger.error("������ǩ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new IndexServiceException("������ǩ�쳣: " + e);
		}
	}

	/**
	 * �����ǩ
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void contentTabChanged(String id) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			SessionsCall(meetingId, "contentTabChangeCallback", id);
		} catch (Exception e) {
			logger.error("�����ǩ: " + id + "�쳣: "
					+ StackTraceUtil.getStackTrace(e));
			throw new IndexServiceException("�����ǩ: " + id + "�쳣: " + e);
		}
	}
}
