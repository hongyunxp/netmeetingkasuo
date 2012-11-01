package com.meeting.service.dwr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.service.dwr.comparator.UserTimeComparator;
import com.meeting.utils.StackTraceUtil;

public class VideoShareService extends DWRService {

	private static Logger logger = Logger.getLogger(VideoShareService.class);

	public static final int VIDEO_ERR = 0;
	public static final int VIDEO_OK = 1;
	public static final int VIDEO_ON = 2;

	/**
	 * ��Ƶ���״̬ <meetingid,state>
	 */
	public static Map<String, Boolean> panelStateMap = new HashMap<String, Boolean>();
	/**
	 * ����ͷ״̬ MAP <meetingid,<sessionid,state>>
	 */
	public static Map<String, Map<String, Integer>> camMicStatusMap = new HashMap<String, Map<String, Integer>>();
	/**
	 * ��Ƶ���Map <meetingId,<panelId,sessionId>>
	 */
	public static Map<String, Map<String, String>> panelIdMap = new HashMap<String, Map<String, String>>();

	public void startService() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			if (!panelStateMap.containsKey(meetingId)) {
				panelStateMap.put(meetingId, false);
			}
			Map<String, Integer> camMicMap = camMicStatusMap.get(meetingId);
			if (camMicMap == null) {
				camMicMap = new HashMap<String, Integer>();
				camMicStatusMap.put(meetingId, camMicMap);
			}
			Map<String, String> pMap = panelIdMap.get(meetingId);
			if (pMap == null) {
				pMap = new HashMap<String, String>();
				panelIdMap.put(meetingId, pMap);
			}
			if (panelStateMap.containsKey(meetingId)) {
				boolean flag = panelStateMap.get(meetingId);
				if (flag) {
					ScriptSession ssession = sessionsMap.get(meetingId).get(
							curuser.getSessionid());
					SessionCall(ssession, "videoShareSwitchCallback");
				}
			}
		} catch (Exception e) {
			logger.error("������Ƶ����쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("������Ƶ����쳣: " + e);
		}
	}

	/**
	 * ������Ƶ�����ڴ�
	 * 
	 * @param meeting
	 * @param user
	 * @param hostuser
	 * @throws Exception
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("ж����Ƶ�������...���飺" + meeting.getMeetingId() + ", �û���"
				+ user.getUsername());
		panelStateMap.remove(meeting.getMeetingId());
		camMicStatusMap.remove(meeting.getMeetingId());
		panelIdMap.remove(meeting.getMeetingId());
	}

	/**
	 * �л���Ƶ�������
	 * 
	 * @throws Exception
	 */
	public void videoShareSwitch() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			if (panelStateMap.get(meetingId)) {
				panelStateMap.put(meetingId, false);
				panelIdMap.get(meetingId).clear();
				camMicStatusMap.get(meetingId).clear();
			} else {
				panelStateMap.put(meetingId, true);
			}
			SessionsCall(meetingId, "videoShareSwitchCallback");
		} catch (Exception e) {
			logger.error("�л���Ƶ��������쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("�л���Ƶ��������쳣: " + e);
		}
	}

	/**
	 * SWF����������ͷ����˷���Ϣ�����ͷ���� ����˸����Ѿ��յ���״̬����ʼ��SWF�û��б�
	 * 
	 * @param status
	 * @throws Exception
	 */
	public void swfInitVideoPanel(int camstatus, int micstatus)
			throws Exception {
		try {
			logger.info(curuser.getUsername() + "����Ƶ״̬: " + camstatus
					+ "����Ƶ״̬��" + micstatus);
			String meetingId = meeting.getMeetingId();
			if (camstatus == 1 && micstatus == 1) {
				Map<String, Integer> camMicMap = camMicStatusMap.get(meetingId);
				camMicMap.put(curuser.getSessionid(), VIDEO_OK);
				camMicStatusMap.put(meetingId, camMicMap);
			} else {
				Map<String, Integer> camMicMap = camMicStatusMap.get(meetingId);
				camMicMap.put(curuser.getSessionid(), VIDEO_ERR);
				camMicStatusMap.put(meetingId, camMicMap);
			}

			// ����Ƿ��յ����е�SWF��ⷴ��
			int statusSize = camMicStatusMap.get(meetingId).size();
			int userSize = usersMap.get(meetingId).size();
			if (statusSize == userSize) {
				logger.info("��ʼ���û��б�" + swfVideoList());
				SessionsCall(meetingId, "swfInitVideoPanelCallback",
						swfVideoList());
			}
		} catch (Exception e) {
			logger.error("�����������ͷ״̬�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("�����������ͷ״̬�쳣: " + e);
		}
	}

	/**
	 * ������Ƶ
	 * 
	 * @param sessionid
	 * @param panelId
	 */
	public void videoShareComsumeVideo(String sessionid, String panelId)
			throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("������Ƶ��sessionid[" + sessionid + "], panelId[" + panelId
					+ "]");
			UserModel userModel = usersMap.get(meetingId).get(sessionid);
			SessionsExceptCall(meetingId, sessionid,
					"videoShareComsumeVideoCallback", sessionid, userModel
							.getUsername(), panelId);
		} catch (Exception e) {
			logger.error("������Ƶ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("������Ƶ�쳣: " + e);
		}
	}

	/**
	 * ������Ƶ
	 * 
	 * @throws Exception
	 */
	public void videoSharePublishVideo(String sessionid, String panelId)
			throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("������Ƶ��sessionid[" + sessionid + "], panelId[" + panelId
					+ "]");
			UserModel userModel = usersMap.get(meetingId).get(sessionid);
			ScriptSession session = sessionsMap.get(meetingId).get(sessionid);
			SessionCall(session, "videoSharePublishVideoCallback", sessionid,
					userModel.getUsername(), panelId);
		} catch (Exception e) {
			logger.error("������Ƶ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("������Ƶ�쳣: " + e);
		}
	}

	/**
	 * ͬ�ⷢ����Ƶ
	 * 
	 * @param sessionid
	 * @param panelId
	 * @throws Exception
	 */
	public void swfVideoAgree(String sessionid, String panelId)
			throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("ͬ�ⷢ����Ƶ��sessionid[" + sessionid + "], panelId["
					+ panelId + "]");
			// ������Ƶ���
			Map<String, String> pMap = panelIdMap.get(meetingId);
			Set<String> panelIdSet = pMap.keySet();
			boolean exist = false;
			String tmpPid = "";
			for (String tpid : panelIdSet) {
				String tsid = pMap.get(tpid);
				if (tsid.equals(sessionid)) {
					exist = true;
					tmpPid = tpid;
					break;
				}
				if (tpid.equals(panelId)) {
					exist = true;
					tmpPid = tpid;
					break;
				}
			}

			if (exist) {
				pMap.remove(tmpPid);
				panelIdMap.put(meetingId, pMap);
				SessionsCall(meetingId, "videoShareCloseBeforePublishCallback",
						tmpPid, sessionid, panelId);
			} else {
				pMap.put(panelId, sessionid);
				panelIdMap.put(meetingId, pMap);
			}

			// ��ӡ��Ƶ���״̬
			videoPanelState(panelIdSet, pMap);
		} catch (Exception e) {
			logger.error("ͬ�ⷢ����Ƶ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("ͬ�ⷢ����Ƶ�쳣: " + e);
		}
	}

	/**
	 * �ر���Ƶ
	 * 
	 * @param panelId
	 * @throws Exception
	 */
	public void videoShareClose(String sessionid, String panelId)
			throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("�ر���Ƶ��panelId[" + panelId + "]");
			SessionsCall(meetingId, "videoShareCloseCallback", sessionid,
					panelId);
		} catch (Exception e) {
			logger.error("�ر���Ƶ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("�ر���Ƶ�쳣: " + e);
		}
	}

	/**
	 * ��ʼ����Ƶ���
	 * 
	 * @throws Exception
	 */
	public void videoShareInitVideoPanel() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			String sessionid = curuser.getSessionid();
			logger.info("��ʼ����Ƶ���");
			Map<String, String> pMap = panelIdMap.get(meetingId);
			UserModel userModel = usersMap.get(meetingId).get(sessionid);
			Set<String> pidSet = pMap.keySet();
			ScriptSession session = sessionsMap.get(meetingId).get(sessionid);
			for (String panelId : pidSet) {
				if (sessionid.equals(pMap.get(panelId))) {
					SessionCall(session, "videoSharePublishVideoCallback",
							sessionid, userModel.getUsername(), panelId);
				} else {
					SessionCall(session, "videoShareComsumeVideoCallback",
							sessionid, userModel.getUsername(), panelId);
				}
			}
		} catch (Exception e) {
			logger.error("��ʼ����Ƶ����쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("��ʼ����Ƶ����쳣: " + e);
		}
	}

	/**
	 * ������Ƶ�����б�
	 * 
	 * @return
	 * @throws Exception
	 */
	private String swfVideoList() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, Integer> camMicMap = camMicStatusMap.get(meetingId);

			Map<String, UserModel> userMap = usersMap.get(meetingId);
			Map<String, SessionIdTime> logonMap = userLogonMap.get(meetingId);
			Set<String> keysSet = userMap.keySet();
			List<SessionIdTime> ssidTimeList = new ArrayList<SessionIdTime>();
			for (String key : keysSet) {
				ssidTimeList.add(logonMap.get(key));
			}
			Collections.sort(ssidTimeList, new UserTimeComparator());
			JSONArray array = new JSONArray();
			for (SessionIdTime sst : ssidTimeList) {
				String tmpSid = sst.getSessionId();
				if (camMicMap.containsKey(tmpSid)
						&& camMicMap.get(tmpSid) == VIDEO_OK) {
					UserModel userModel = userMap.get(tmpSid);
					String username = userModel.getUsername();
					JSONObject json = new JSONObject();
					json.put("sessionid", tmpSid);
					json.put("username", username);
					array.put(json);
				}
			}
			return array.toString();
		} catch (Exception e) {
			logger.error("��Ƶ�����б��쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("��Ƶ�����б��쳣: " + e);
		}
	}

	/**
	 * ��Ƶ���״̬
	 * 
	 * @param panelIdSet
	 * @param pMap
	 * @throws JSONException
	 */
	private void videoPanelState(Set<String> panelIdSet,
			Map<String, String> pMap) throws JSONException {
		JSONArray array = new JSONArray();
		for (String tpid : panelIdSet) {
			JSONObject object = new JSONObject();
			String tsid = pMap.get(tpid);
			object.put("panelId", tpid);
			object.put("panelId", tsid);
			array.put(object);
		}
		logger.info("��Ƶ���Map: " + array.toString());
	}

}
