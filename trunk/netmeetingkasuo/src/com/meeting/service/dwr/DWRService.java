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
	 * �����Ķ���
	 */
	public WebContext context = WebContextFactory.get();
	/**
	 * HttpSession����
	 */
	public HttpSession hsession = context.getSession();
	/**
	 * ������
	 */
	public UserModel hostuser = (UserModel) hsession
			.getAttribute(AppConfigure.HOST_USER);
	/**
	 * ��ǰ��
	 */
	public UserModel curuser = (UserModel) hsession
			.getAttribute(AppConfigure.CURRENT_USER);
	/**
	 * ��ǰ����
	 */
	public MeetingModel meeting = (MeetingModel) hsession
			.getAttribute(AppConfigure.MEETING);

	/**
	 * ĳ�����Ӧ���û��б�<meetingid,<sessionid,UserModel>>
	 */
	public static Map<String, Map<String, UserModel>> usersMap = new HashMap<String, Map<String, UserModel>>();

	/**
	 * ĳ�����Ӧ��ScriptSession��Map��<meetingid,<sessionid,ScriptSession>>
	 */
	public static Map<String, Map<String, ScriptSession>> sessionsMap = new HashMap<String, Map<String, ScriptSession>>();
	/**
	 * �û���¼˳���Map��<meeting,<sessionId,time>
	 */
	public static Map<String, Map<String, SessionIdTime>> userLogonMap = new HashMap<String, Map<String, SessionIdTime>>();

	/**
	 * �û�����ɫ
	 */
	public static Map<String, String> colorMap = new HashMap<String, String>();

	/**
	 * ��ʼ������
	 * 
	 * @throws Exception
	 */
	public abstract void startService() throws Exception;

	/**
	 * ��ʼ��userMap
	 */
	protected void initUsersMap() throws Exception {
		String meetingId = meeting.getMeetingId();
		String sessionId = curuser.getSessionid();
		// �����û��б�
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
		logger.info("��ʼ�� usersMap���...");
	}

	/**
	 * ��ʼ��SessionMap
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
			logger.info("��ʼ�� sessionsMap���...");
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("��ʼ��sessionsMap�쳣�� " + e);
		}
	}

	/**
	 * ����״̬
	 * 
	 * @param meetingId
	 */
	public void meetingState(String meetingId) throws Exception {
		try {
			StringBuffer buf1 = new StringBuffer();
			buf1.append("���顾").append(meeting.getMeetingId()).append(
					"����ϵͳ�û�Map�б�");
			Map<String, UserModel> tempMap = usersMap.get(meetingId);
			for (String sessionId : tempMap.keySet()) {
				UserModel usermodel = tempMap.get(sessionId);
				buf1.append("\n\t").append("userName: ").append(
						usermodel.getUsername());
				buf1.append("��sessionId: ").append(usermodel.getSessionid());
			}
			logger.info(buf1.toString());

			buf1 = new StringBuffer();
			buf1.append("���顾").append(meeting.getMeetingId()).append(
					"����ϵͳ�ỰMap�б�");
			Map<String, ScriptSession> wrapperMap = sessionsMap.get(meetingId);
			for (String sessionid : wrapperMap.keySet()) {
				ScriptSession session = wrapperMap.get(sessionid);
				buf1.append("\n\t").append("sessionId: ").append(sessionid);
				buf1.append("��scriptSessionId: ").append(session.getId());
			}
			logger.info(buf1.toString());
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("��ȡ����״̬�쳣�� " + e);
		}
	}

	/**
	 * ��ʾ��ʾ��Ϣ
	 * 
	 * @param string
	 */
	public static void showMsg(String meetingId, String string)
			throws Exception {
		try {
			SessionsCall(meetingId, "showMsg", string);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("Ⱥ����ʾ��Ϣ�쳣�� " + e);
		}
	}

	/**
	 * ��ʾ��ʾ��Ϣ
	 * 
	 * @param string
	 */
	public static void showMsg(ScriptSession session, String string)
			throws Exception {
		try {
			SessionCall(session, "showMsg", string);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("Ⱥ����ʾ��Ϣ�쳣�� " + e);
		}
	}

	/**
	 * ��ʾ��ʾ��Ϣ
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
			throw new DWRServiceException("Ⱥ����ʾ��Ϣ�쳣�� " + e);
		}
	}

	/**
	 * �����û���sessionid����û�����
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
	 * �����û���usercode����û�����
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
	 * ��ȡ�û��б�JSON
	 * 
	 * @param meetingId
	 *            ����ID
	 * @param sessionId
	 *            ���Ϊnull�����ȡ�����û��������ȡ���˴��û�֮��������û�
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
			if (sessionId == null) {// ��ȡ���л������û�
				JSONObject json = new JSONObject();
				json.put("sessionid", tmpSid);
				json.put("usercode", usercode);
				json.put("username", username);
				json.put("color", colorMap.get(tmpSid));
				array.put(json);
			} else {// ��ȡ����sessionId֮��������û�
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
	 * ĳ���������ScriptSessionִ��ĳjavascript����
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
			logger.error("Ⱥ��DWR��Ϣ�쳣�� " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("Ⱥ��DWR��Ϣ�쳣�� " + e);
		}
	}

	/**
	 * ĳ������ض���ִ��ĳjavascript����
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
				logger.error("ssession is null��ʹ�õ�ǰScriptSession����");
				ScriptSession ssession2 = WebContextFactory.get()
						.getScriptSession();
				ScriptBuffer sb = new ScriptBuffer();
				sb.appendCall(funcName, obj);
				ssession2.addScript(sb);
			}
		} catch (Exception e) {
			logger.error("���͵���DWR��Ϣ�쳣�� " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("���͵���DWR��Ϣ�쳣�� " + e);
		}
	}

	/**
	 * ĳ��������ض���֮���������ִ��ĳjavascript����
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
			logger.error("Ⱥ��DWR��Ϣ�쳣�� " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("Ⱥ��DWR��Ϣ�쳣�� " + e);
		}
	}

	/**
	 * ĳ��������ض���֮���������ִ��ĳjavascript����
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
					logger.info("�������󡣡���" + uSid);
				}
			}
		} catch (Exception e) {
			logger.error("Ⱥ��DWR��Ϣ�쳣�� " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("Ⱥ��DWR��Ϣ�쳣�� " + e);
		}
	}

	/**
	 * ĳ�������һЩ��֮���������ִ��ĳjavascript����
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
			logger.error("Ⱥ��DWR��Ϣ�쳣�� " + StackTraceUtil.getStackTrace(e));
			throw new DWRServiceException("Ⱥ��DWR��Ϣ�쳣�� " + e);
		}
	}

	/**
	 * �ж�ĳ���û���sessionId�Ƿ���sessionIdList��
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
