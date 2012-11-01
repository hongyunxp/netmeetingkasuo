package com.meeting.service.dwr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;

import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.utils.CommonUtils;
import com.meeting.utils.StackTraceUtil;

public class UserListService extends DWRService {

	private static Logger logger = Logger.getLogger(UserListService.class);

	/**
	 * �û���sessionid�Լ��Ƿ���ֵı�־��trueΪ���֣�falseΪ����
	 */
	public static Map<String, Boolean> handupMap = new HashMap<String, Boolean>();
	public static final int STATE_HANDUP = 1;
	public static final int STATE_AUDIO = 2;
	public static final int STATE_VIDEO = 3;

	/**
	 * ��ʼ��ҳ��
	 */
	public void startService() throws Exception {
		String meetingId = meeting.getMeetingId();
		String userName = curuser.getUsername();
		logger.info("�����û��б����...���飺" + meetingId + ", �û���" + userName);
		try {
			if (!colorMap.containsKey(curuser.getSessionid()))
				colorMap.put(curuser.getSessionid(), getColor());
			SessionsCall(meetingId, "initUserListCallback", getUserListJson(
					meetingId, null));
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("�����û��б�����쳣: " + e);
		}

		initListState();
	}

	/**
	 * 
	 * @return
	 */
	private String getColor() {
		Set<String> sessionidSet = colorMap.keySet();
		String newColor = CommonUtils.randomColor();
		boolean flag = false;
		for (String key : sessionidSet) {
			String userColor = colorMap.get(key);
			if (newColor.equals(userColor)) {
				flag = true;
				break;
			}
		}
		if (flag) {
			return getColor();
		} else {
			return newColor;
		}
	}

	/**
	 * �˳�ҳ�����
	 * 
	 * @throws Exception
	 */
	public static void destroyUserListService(MeetingModel meeting,
			UserModel user) throws Exception {
		try {
			logger.info("ж���û��б����...���飺" + meeting.getMeetingId() + ", �û���"
					+ user.getUsername());
			String sessionId = user.getSessionid();
			String meetingId = meeting.getMeetingId();
			SessionsCall(meetingId, "initUserListCallback", getUserListJson(
					meetingId, sessionId));

			// �������
			handupMap.remove(sessionId);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("�����û��б��ڴ��쳣: " + e);
		}
	}

	/**
	 * ��ʼ���û��б�״̬
	 * 
	 * @throws Exception
	 */
	private void initListState() throws Exception {
		try {
			Map<String, UserModel> userMap = usersMap.get(meeting
					.getMeetingId());
			for (String tmpSid : userMap.keySet()) {
				UserModel usermodel = userMap.get(tmpSid);
				String sessionid = usermodel.getSessionid();
				// ����״̬
				boolean handupF = false;
				if (handupMap.containsKey(sessionid)) {
					handupF = handupMap.get(sessionid);
				}
				SessionsCall(meeting.getMeetingId(), "userListStateCallback",
						sessionid, sessionid, STATE_HANDUP, handupF);
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("��ʼ���û��б�״̬�쳣: " + e);
		}
	}

	/**
	 * �����û��б�״̬
	 * 
	 * @param sessionid
	 * @throws Exception
	 */
	public void userListState(String sessionid, int type) throws Exception {
		try {
			String meetingid = meeting.getMeetingId();
			boolean flag = false;
			switch (type) {
			case STATE_HANDUP:
				if (handupMap.containsKey(sessionid)) {
					if (handupMap.get(sessionid))
						flag = false;
					else
						flag = true;
				} else {
					flag = true;
				}
				if (flag) {
					logger
							.info("���飺" + meetingid + "���û���" + sessionid
									+ " ���֣�");
				} else {
					logger
							.info("���飺" + meetingid + "���û���" + sessionid
									+ " ���֣�");
				}
				handupMap.put(sessionid, flag);
				break;
			case STATE_AUDIO:

				break;
			case STATE_VIDEO:

				break;
			default:
				break;
			}
			SessionsCall(meetingid, "userListStateCallback", sessionid,
					sessionid, STATE_HANDUP, flag);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("�����û��б�״̬�쳣��" + e);
		}
	}

	/**
	 * ����ĳ�˵ľ��ֹ���
	 * 
	 * @param tid
	 * @throws Exception
	 */
	public void disableHandup(String tid, String flag) throws Exception {
		try {
			String sessionid = tid.substring(tid.indexOf('_') + 1);
			ScriptSession ssession = sessionsMap.get(meeting.getMeetingId())
					.get(sessionid);
			SessionCall(ssession, "disableHandupCallback", flag);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("���þ����쳣��" + e);
		}
	}

}
