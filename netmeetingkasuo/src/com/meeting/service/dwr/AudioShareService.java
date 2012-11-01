package com.meeting.service.dwr;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;
import org.json.JSONArray;
import org.json.JSONObject;

import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.utils.StackTraceUtil;

public class AudioShareService extends DWRService {

	private static Logger logger = Logger.getLogger(AudioShareService.class);

	public static final int AUDIO_NONE = 1;
	public static final int AUDIO_SPEAKING = 2;
	public static final int AUDIO_MUTED = 3;
	/**
	 * ��������״̬Map
	 */
	public static Map<String, Boolean> audioStateMap = new HashMap<String, Boolean>();
	/**
	 * ���������û�Map
	 */
	public static Map<String, Map<String, Integer>> audioUserMap = new HashMap<String, Map<String, Integer>>();

	public void startService() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, Integer> userMap = audioUserMap.get(meetingId);
			if (userMap == null) {
				userMap = new HashMap<String, Integer>();
				audioUserMap.put(meetingId, userMap);
			}

			ScriptSession ssession = sessionsMap.get(meetingId).get(
					curuser.getSessionid());
			if (audioStateMap.get(meetingId) != null
					&& audioStateMap.get(meetingId)) {
				SessionCall(ssession, "audioShareOnCallback");
			}
		} catch (Exception e) {
			logger.error("��ʼ����Ƶ����쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("��ʼ����Ƶ����쳣: " + e);
		}
	}

	/**
	 * �������������ڴ�
	 * @param meeting
	 * @param user
	 * @param hostuser
	 * @throws Exception
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("ж�������������...���飺" + meeting.getMeetingId() + ", �û���"
				+ user.getUsername());
		audioStateMap.remove(meeting.getMeetingId());
		audioUserMap.remove(meeting.getMeetingId());
	}

	/**
	 * ����Flash�ļ�
	 * 
	 * @param flag
	 */
	public void audioShareInit(boolean flag) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			if (flag) {
				audioStateMap.put(meetingId, true);
				SessionsCall(meetingId, "audioShareOnCallback");
			} else {
				audioStateMap.put(meetingId, false);
				audioUserMap.clear();
				SessionsCall(meetingId, "audioShareOffCallback");
			}
		} catch (Exception e) {
			logger.error("����Flash�ļ��쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("����Flash�ļ��쳣: " + e);
		}
	}

	/**
	 * ������Ƶ��
	 * 
	 * @param publishId
	 * @throws Exception
	 */
	public void audioSharePublish(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("������Ƶ����publishId[" + publishId + "]");
			ScriptSession ssession = sessionsMap.get(meetingId).get(publishId);
			SessionCall(ssession, "audioSharePublishCallback", publishId);
		} catch (Exception e) {
			logger.error("������Ƶ���쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("������Ƶ���쳣: " + e);
		}
	}

	/**
	 * ͬ�ⷢ����Ƶ��
	 * 
	 * @param publishId
	 * @throws Exception
	 */
	public void audioSharePublishAgree(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("ͬ�ⷢ����Ƶ����publishId[" + publishId + "]");

			Map<String, Integer> userMap = audioUserMap.get(meetingId);
			userMap.put(publishId, AUDIO_SPEAKING);
			audioUserMap.put(meetingId, userMap);

			SessionsCall(meetingId, "audioSharePublishAgreeCallback", publishId);
		} catch (Exception e) {
			logger.error("ͬ�ⷢ����Ƶ���쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("ͬ�ⷢ����Ƶ���쳣: " + e);
		}
	}

	/**
	 * ������Ƶ��
	 * 
	 * @param publishId
	 */
	public void audioShareComsumeAudio(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("������Ƶ����publishId[" + publishId + "]");
			UserModel userModel = usersMap.get(meetingId).get(publishId);
			SessionsCall(meetingId, "audioShareComsumeAudioCallback",
					publishId, userModel.getUsername());
		} catch (Exception e) {
			logger.error("������Ƶ���쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("������Ƶ���쳣: " + e);
		}
	}

	/**
	 * ��ʼ����������
	 * 
	 * @throws Exception
	 */
	public void audioShareInitComsumeAudio() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, Integer> userMap = audioUserMap.get(meetingId);
			Set<String> userSet = userMap.keySet();
			for (String sid : userSet) {
				if (!sid.equals(curuser.getSessionid())) {
					if (userMap.get(sid) != 1) {
						logger.info("��ʼ��������������" + sid);
						ScriptSession session = sessionsMap.get(meetingId).get(
								sid);
						UserModel userModel = usersMap.get(meetingId).get(sid);
						SessionCall(session, "audioShareComsumeAudioCallback",
								sid, userModel.getUsername());
					}
				}
			}
		} catch (Exception e) {
			logger.error("��ʼ�������������쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("��ʼ�������������쳣: " + e);
		}
	}

	/**
	 * ������������
	 */
	public void audioShareMute(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, Integer> audioMap = audioUserMap.get(meetingId);
			audioMap.put(publishId, AUDIO_MUTED);
			audioUserMap.put(meetingId, audioMap);
			SessionsCall(meetingId, "audioShareMuteCallback", publishId);
		} catch (Exception e) {
			logger.error("�������������쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("�������������쳣: " + e);
		}
	}

	/**
	 * ȡ��������������
	 */
	public void audioShareMuteCacel(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, Integer> audioMap = audioUserMap.get(meetingId);
			audioMap.put(publishId, AUDIO_SPEAKING);
			audioUserMap.put(meetingId, audioMap);
			SessionsCall(meetingId, "audioShareMuteCacelCallback", publishId);
		} catch (Exception e) {
			logger.error("�������������쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("�������������쳣: " + e);
		}
	}

	/**
	 * ��ȡ���������б�
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String audioShareList(String meetingId) throws Exception {
		try {
			Map<String, Integer> audioMap = audioUserMap.get(meetingId);
			Map<String, UserModel> uMap = usersMap.get(meetingId);
			Set<String> keySet = uMap.keySet();
			JSONArray array = new JSONArray();
			for (String sid : keySet) {
				JSONObject json = new JSONObject();
				UserModel userModel = uMap.get(sid);
				json.put("sessionid", userModel.getSessionid());
				json.put("username", userModel.getUsername());
				if (audioMap.containsKey(sid)) {
					json.put("audiostate", audioMap.get(sid));
				} else {
					json.put("audiostate", 1);
				}
				array.put(json);
			}
			return array.toString();
		} catch (Exception e) {
			logger.error("�������������쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("�������������쳣: " + e);
		}
	}

	/**
	 * �ر�����
	 * 
	 * @param publishId
	 * @throws Exception
	 */
	public void audioShareClose(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, Integer> audioMap = audioUserMap.get(meetingId);
			audioMap.put(publishId, AUDIO_NONE);
			audioUserMap.put(meetingId, audioMap);
			SessionsCall(meetingId, "audioShareCloseCallback", publishId);
		} catch (Exception e) {
			logger.error("�ر������쳣: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("�ر������쳣: " + e);
		}
	}

}
