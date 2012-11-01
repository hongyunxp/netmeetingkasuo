package com.meeting.service.dwr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptBuffer;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.service.dwr.comparator.UserTimeComparator;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

public abstract class DWRService {

	private static Logger logger = Logger.getLogger(DWRService.class);

	/**
	 * 上下文对象
	 */
	public WebContext context = WebContextFactory.get();
	/**
	 * HttpSession对象
	 */
	public HttpSession hsession = context.getSession();
	/**
	 * 主持人
	 */
	public UserModel hostuser = (UserModel) hsession
			.getAttribute(AppConfigure.HOST_USER);
	/**
	 * 当前人
	 */
	public UserModel curuser = (UserModel) hsession
			.getAttribute(AppConfigure.CURRENT_USER);
	/**
	 * 当前会议
	 */
	public MeetingModel meeting = (MeetingModel) hsession
			.getAttribute(AppConfigure.MEETING);

	/**
	 * 某会议对应的用户列表<meetingid,<sessionid,UserModel>>
	 */
	public static Map<String, Map<String, UserModel>> usersMap = new HashMap<String, Map<String, UserModel>>();

	/**
	 * 某会议对应的ScriptSession的Map，<meetingid,<sessionid,ScriptSession>>
	 */
	public static Map<String, Map<String, ScriptSession>> sessionsMap = new HashMap<String, Map<String, ScriptSession>>();
	/**
	 * 用户登录顺序的Map，<meeting,<sessionId,time>
	 */
	public static Map<String, Map<String, SessionIdTime>> userLogonMap = new HashMap<String, Map<String, SessionIdTime>>();

	/**
	 * 用户的颜色
	 */
	public static Map<String, String> colorMap = new HashMap<String, String>();

	/**
	 * 初始化子类
	 * 
	 * @throws Exception
	 */
	public abstract void startService() throws Exception;

	/**
	 * 初始化userMap
	 */
	protected void initUsersMap() throws Exception {
		String meetingId = meeting.getMeetingId();
		String sessionId = curuser.getSessionid();
		// 插入用户列表
		Map<String, UserModel> userMap = usersMap.get(meetingId);
		if (userMap == null) {
			userMap = new HashMap<String, UserModel>();
			usersMap.put(meetingId, userMap);
		}
		Map<String, SessionIdTime> logonMap = userLogonMap.get(meetingId);
		if (logonMap == null) {
			logonMap = new HashMap<String, SessionIdTime>();
			userLogonMap.put(meetingId, logonMap);
		}
		if (!logonMap.containsKey(curuser.getSessionid()))
			logonMap.put(curuser.getSessionid(), new SessionIdTime(curuser
					.getSessionid(), new Date().getTime()));
		userMap.put(sessionId, curuser);
		usersMap.put(meetingId, userMap);
		userLogonMap.put(meetingId, logonMap);
		logger.info("初始化 usersMap完毕...");
	}

	/**
	 * 初始化SessionMap
	 */
	protected void initSessionMap() throws Exception {
		try {
			String meetingid = meeting.getMeetingId();
			String uSid = curuser.getSessionid();
			Map<String, ScriptSession> wrapperMap = sessionsMap.get(meetingid);
			if (wrapperMap == null) {
				wrapperMap = new HashMap<String, ScriptSession>();
			}
			ScriptSession ssession = context.getScriptSession();
			wrapperMap.put(uSid, ssession);
			sessionsMap.put(meetingid, wrapperMap);
			logger.info("初始化 sessionsMap完毕...");
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("初始化sessionsMap异常： " + e);
		}
	}

	/**
	 * 会议状态
	 * 
	 * @param meetingId
	 */
	public void meetingState(String meetingId) throws Exception {
		try {
			StringBuffer buf1 = new StringBuffer();
			buf1.append("会议【").append(meeting.getMeetingId()).append(
					"】的系统用户Map列表");
			Map<String, UserModel> tempMap = usersMap.get(meetingId);
			for (String sessionId : tempMap.keySet()) {
				UserModel usermodel = tempMap.get(sessionId);
				buf1.append("\n\t").append("userName: ").append(
						usermodel.getUsername());
				buf1.append("，sessionId: ").append(usermodel.getSessionid());
			}
			logger.info(buf1.toString());

			buf1 = new StringBuffer();
			buf1.append("会议【").append(meeting.getMeetingId()).append(
					"】的系统会话Map列表");
			Map<String, ScriptSession> wrapperMap = sessionsMap.get(meetingId);
			for (String sessionid : wrapperMap.keySet()) {
				ScriptSession session = wrapperMap.get(sessionid);
				buf1.append("\n\t").append("sessionId: ").append(sessionid);
				buf1.append("，scriptSessionId: ").append(session.getId());
			}
			logger.info(buf1.toString());
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("获取会议状态异常： " + e);
		}
	}

	/**
	 * 显示提示消息
	 * 
	 * @param string
	 */
	public static void showMsg(String meetingId, String string)
			throws Exception {
		try {
			SessionsCall(meetingId, "showMsg", string);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("群发提示消息异常： " + e);
		}
	}

	/**
	 * 显示提示消息
	 * 
	 * @param string
	 */
	public static void showMsg(ScriptSession session, String string)
			throws Exception {
		try {
			SessionCall(session, "showMsg", string);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("群发提示消息异常： " + e);
		}
	}

	/**
	 * 显示提示消息
	 * 
	 * @param string
	 */
	public static void showMsg(String meetingId, String sessionid, String string)
			throws Exception {
		try {
			ScriptSession session = sessionsMap.get(meetingId).get(sessionid);
			SessionCall(session, "showMsg", string);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("群发提示消息异常： " + e);
		}
	}

	/**
	 * 根据用户的sessionid获得用户对象
	 * 
	 * @param sessionid
	 * @return
	 */
	public UserModel getUserModelBySid(String meetingid, String sessionid) {
		Map<String, UserModel> userMap = usersMap.get(meetingid);
		for (String sid : userMap.keySet()) {
			if (sid.equals(sessionid)) {
				return userMap.get(sid);
			}
		}
		return null;
	}

	/**
	 * 根据用户的usercode获得用户对象
	 * 
	 * @param userCode
	 * @param userid
	 * @return
	 */
	public UserModel getUserModelByUid(String meetingid, String userCode) {
		Map<String, UserModel> userMap = usersMap.get(meetingid);
		for (String sid : userMap.keySet()) {
			UserModel userModel = userMap.get(sid);
			if (userModel.getUsercode().equals(userCode)) {
				return userModel;
			}
		}
		return null;
	}

	/**
	 * 获取用户列表JSON
	 * 
	 * @param meetingId
	 *            会议ID
	 * @param sessionId
	 *            如果为null，则获取所有用户，否则获取除了此用户之外的所有用户
	 * @return
	 * @throws JSONException
	 */
	public static String getUserListJson(String meetingId, String sessionId)
			throws JSONException {
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
			UserModel userModel = userMap.get(tmpSid);
			String usercode = userModel.getUsercode();
			String username = userModel.getUsername();
			if (sessionId == null) {// 获取所有会议中用户
				JSONObject json = new JSONObject();
				json.put("sessionid", tmpSid);
				json.put("usercode", usercode);
				json.put("username", username);
				json.put("color", colorMap.get(tmpSid));
				array.put(json);
			} else {// 获取除了sessionId之外的所有用户
				if (!tmpSid.equals(sessionId)) {
					JSONObject json = new JSONObject();
					json.put("sessionid", tmpSid);
					json.put("usercode", usercode);
					json.put("username", username);
					json.put("color", colorMap.get(tmpSid));
					array.put(json);
				}
			}
		}
		return array.toString();
	}

	/**
	 * 某会议的所有ScriptSession执行某javascript函数
	 * 
	 * @param funcName
	 * @param obj
	 */
	public static void SessionsCall(String meetingId, String funcName,
			Object... obj) throws Exception {
		try {
			Map<String, ScriptSession> wrapperMap = sessionsMap.get(meetingId);
			for (String uSid : wrapperMap.keySet()) {
				ScriptSession session = wrapperMap.get(uSid);
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendCall(funcName, obj);
				session.addScript(sb);
			}
		} catch (Exception e) {
			logger.error("群发DWR消息异常： " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("群发DWR消息异常： " + e);
		}
	}

	/**
	 * 某会议的特定人执行某javascript函数
	 * 
	 * @param funcName
	 * @param obj
	 */
	public static void SessionCall(ScriptSession ssession, String funcName,
			Object... obj) throws Exception {
		try {
			if (ssession != null) {
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendCall(funcName, obj);
				ssession.addScript(sb);
			} else {
				logger.error("ssession is null，使用当前ScriptSession对象");
				ScriptSession ssession2 = WebContextFactory.get()
						.getScriptSession();
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendCall(funcName, obj);
				ssession2.addScript(sb);
			}
		} catch (Exception e) {
			logger.error("发送单个DWR消息异常： " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("发送单个DWR消息异常： " + e);
		}
	}

	/**
	 * 某会议除了特定人之外的所有人执行某javascript函数
	 * 
	 * @param funcName
	 * @param obj
	 */
	protected static void SessionsExceptCall(String meetingId,
			String sessionId, String funcName, Object... obj) throws Exception {
		try {
			Map<String, ScriptSession> wrapperMap = sessionsMap.get(meetingId);
			Set<String> sessionIdsSet = wrapperMap.keySet();
			for (String uSid : sessionIdsSet) {
				if (!uSid.equals(sessionId)) {
					ScriptSession session = wrapperMap.get(uSid);
					ScriptBuffer sb = new ScriptBuffer();
					sb.appendCall(funcName, obj);
					session.addScript(sb);
				}
			}
		} catch (Exception e) {
			logger.error("群发DWR消息异常： " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("群发DWR消息异常： " + e);
		}
	}

	/**
	 * 某会议除了特定人之外的所有人执行某javascript函数
	 * 
	 * @param funcName
	 * @param obj
	 * @deprecated
	 */
	protected static void SessionsExceptCall(String meetingId,
			String sessionId, int time, String funcName, Object... obj)
			throws Exception {
		try {
			Map<String, ScriptSession> wrapperMap = sessionsMap.get(meetingId);
			Set<String> sessionIdsSet = wrapperMap.keySet();
			for (String uSid : sessionIdsSet) {
				if (!uSid.equals(sessionId)) {
					ScriptSession session = wrapperMap.get(uSid);
					ScriptBuffer sb = new ScriptBuffer();
					sb.appendCall(funcName, obj);
					session.addScript(sb);
					Thread.sleep(time);
					logger.info("发送请求。。。" + uSid);
				}
			}
		} catch (Exception e) {
			logger.error("群发DWR消息异常： " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("群发DWR消息异常： " + e);
		}
	}

	/**
	 * 某会议除了一些人之外的所有人执行某javascript函数
	 * 
	 * @param funcName
	 * @param obj
	 */
	protected static void SessionsExceptCall(String meetingId,
			List<String> sessionIdList, String funcName, Object... obj)
			throws Exception {
		try {
			Map<String, ScriptSession> wrapperMap = sessionsMap.get(meetingId);
			Set<String> sessionIdsSet = wrapperMap.keySet();
			for (String uSid : sessionIdsSet) {
				if (!inSessionIdList(sessionIdList, uSid)) {
					ScriptSession session = wrapperMap.get(uSid);
					ScriptBuffer sb = new ScriptBuffer();
					sb.appendCall(funcName, obj);
					session.addScript(sb);
				}
			}
		} catch (Exception e) {
			logger.error("群发DWR消息异常： " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("群发DWR消息异常： " + e);
		}
	}

	/**
	 * 判断某个用户的sessionId是否在sessionIdList中
	 * 
	 * @param sessionIdList
	 * @param uSid
	 * @return
	 */
	private static boolean inSessionIdList(List<String> sessionIdList,
			String uSid) {
		for (String sessionid : sessionIdList) {
			if (sessionid.equals(uSid))
				return true;
		}
		return false;
	}

	public class SessionIdTime {
		private String sessionId;
		private Long logonTime;

		public SessionIdTime() {
		}

		public SessionIdTime(String sessionId, Long logonTime) {
			this.sessionId = sessionId;
			this.logonTime = logonTime;
		}

		public String getSessionId() {
			return sessionId;
		}

		public void setSessionId(String sessionId) {
			this.sessionId = sessionId;
		}

		public Long getLogonTime() {
			return logonTime;
		}

		public void setLogonTime(Long logonTime) {
			this.logonTime = logonTime;
		}

	}
}
