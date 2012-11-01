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
	 * 语音会议状态Map
	 */
	public static Map<String, Boolean> audioStateMap = new HashMap<String, Boolean>();
	/**
	 * 语音会议用户Map
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
			logger.error("初始化音频面板异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("初始化音频面板异常: " + e);
		}
	}

	/**
	 * 销毁语音会议内存
	 * @param meeting
	 * @param user
	 * @param hostuser
	 * @throws Exception
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("卸载语音会议面板...会议：" + meeting.getMeetingId() + ", 用户："
				+ user.getUsername());
		audioStateMap.remove(meeting.getMeetingId());
		audioUserMap.remove(meeting.getMeetingId());
	}

	/**
	 * 加载Flash文件
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
			logger.error("加载Flash文件异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("加载Flash文件异常: " + e);
		}
	}

	/**
	 * 发布音频流
	 * 
	 * @param publishId
	 * @throws Exception
	 */
	public void audioSharePublish(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("发布音频流：publishId[" + publishId + "]");
			ScriptSession ssession = sessionsMap.get(meetingId).get(publishId);
			SessionCall(ssession, "audioSharePublishCallback", publishId);
		} catch (Exception e) {
			logger.error("发布音频流异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("发布音频流异常: " + e);
		}
	}

	/**
	 * 同意发布音频流
	 * 
	 * @param publishId
	 * @throws Exception
	 */
	public void audioSharePublishAgree(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("同意发布音频流：publishId[" + publishId + "]");

			Map<String, Integer> userMap = audioUserMap.get(meetingId);
			userMap.put(publishId, AUDIO_SPEAKING);
			audioUserMap.put(meetingId, userMap);

			SessionsCall(meetingId, "audioSharePublishAgreeCallback", publishId);
		} catch (Exception e) {
			logger.error("同意发布音频流异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("同意发布音频流异常: " + e);
		}
	}

	/**
	 * 接收音频流
	 * 
	 * @param publishId
	 */
	public void audioShareComsumeAudio(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("接收音频流：publishId[" + publishId + "]");
			UserModel userModel = usersMap.get(meetingId).get(publishId);
			SessionsCall(meetingId, "audioShareComsumeAudioCallback",
					publishId, userModel.getUsername());
		} catch (Exception e) {
			logger.error("接收音频流异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("接收音频流异常: " + e);
		}
	}

	/**
	 * 初始化接收语音
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
						logger.info("初始化接收语音流：" + sid);
						ScriptSession session = sessionsMap.get(meetingId).get(
								sid);
						UserModel userModel = usersMap.get(meetingId).get(sid);
						SessionCall(session, "audioShareComsumeAudioCallback",
								sid, userModel.getUsername());
					}
				}
			}
		} catch (Exception e) {
			logger.error("初始化接收语音流异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("初始化接收语音流异常: " + e);
		}
	}

	/**
	 * 静音语音会议
	 */
	public void audioShareMute(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, Integer> audioMap = audioUserMap.get(meetingId);
			audioMap.put(publishId, AUDIO_MUTED);
			audioUserMap.put(meetingId, audioMap);
			SessionsCall(meetingId, "audioShareMuteCallback", publishId);
		} catch (Exception e) {
			logger.error("静音语音会议异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("静音语音会议异常: " + e);
		}
	}

	/**
	 * 取消静音语音会议
	 */
	public void audioShareMuteCacel(String publishId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, Integer> audioMap = audioUserMap.get(meetingId);
			audioMap.put(publishId, AUDIO_SPEAKING);
			audioUserMap.put(meetingId, audioMap);
			SessionsCall(meetingId, "audioShareMuteCacelCallback", publishId);
		} catch (Exception e) {
			logger.error("静音语音会议异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("静音语音会议异常: " + e);
		}
	}

	/**
	 * 获取语音共享列表
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
			logger.error("静音语音会议异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("静音语音会议异常: " + e);
		}
	}

	/**
	 * 关闭语音
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
			logger.error("关闭语音异常: " + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("关闭语音异常: " + e);
		}
	}

}
