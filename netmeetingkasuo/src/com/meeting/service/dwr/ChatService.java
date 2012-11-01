package com.meeting.service.dwr;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.io.FileTransfer;

import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.DateUtils;
import com.meeting.utils.StackTraceUtil;

public class ChatService extends DWRService {

	private static Logger logger = Logger.getLogger(ChatService.class);

	/**
	 * 某次会议某个人的文本聊天记录 <meetingId,<sessionId,StringBuffer>>
	 */
	public static Map<String, Map<String, StringBuffer>> userChatMap = new HashMap<String, Map<String, StringBuffer>>();

	/**
	 * 某次会议的文本聊天记录 <meetingId,StringBuffer>
	 */
	public static Map<String, StringBuffer> meetingChatMap = new HashMap<String, StringBuffer>();
	/**
	 * 某次会议某个人的HTML聊天记录 <meetingId,<sessionId,StringBuffer>>
	 */
	public static Map<String, Map<String, StringBuffer>> userChatHtmlMap = new HashMap<String, Map<String, StringBuffer>>();

	/**
	 * 某次会议的HTML聊天记录 <meetingId,StringBuffer>
	 */
	public static Map<String, StringBuffer> meetingChatHtmlMap = new HashMap<String, StringBuffer>();

	/**
	 * 初始化页面
	 * 
	 * @throws Exception
	 */
	public void startService() throws Exception {
		String meetingId = meeting.getMeetingId();
		String userName = curuser.getUsername();
		logger.info("加载聊天面板...会议：" + meetingId + ", 用户：" + userName);
		try {
			//更新用户列表
			Map<String, UserModel> userMap = usersMap.get(meetingId);
			for (String tempSid : userMap.keySet()) {
				String usersString = getUserListJson(meetingId, tempSid);
				ScriptSession session = sessionsMap.get(meetingId).get(tempSid);
				SessionCall(session, "chatInitSelectCallback", usersString);
			}

			// 初始化聊天记录
			initChatMsg(meetingId, curuser);

		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("加载聊天面板异常: " + e);
		}
	}

	/**
	 * 退出页面调用
	 * 
	 * @throws Exception
	 */
	public static void destroyChatService(MeetingModel meeting, UserModel user)
			throws Exception {
		try {
			logger.info("卸载聊天面板...会议：" + meeting.getMeetingId() + ", 用户："
					+ user.getUsername());
			String sessionId = user.getSessionid();
			String meetingId = meeting.getMeetingId();
			SessionsCall(meetingId, "chatInitSelectCallback", getUserListJson(
					meetingId, sessionId));

		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("销毁用户列表内存异常: " + e);
		}
	}

	/**
	 * 初始化聊天信息
	 * 
	 * @return 返回某用户的文本信息
	 * @throws Exception
	 */
	public static void initChatMsg(String meetingId, UserModel curuser)
			throws Exception {
		Set<String> sessionIds = sessionsMap.get(meetingId).keySet();

		// 初始化 userChatMap
		Map<String, StringBuffer> chatMap = userChatMap.get(meetingId);
		if (chatMap == null) {
			chatMap = new HashMap<String, StringBuffer>();
		}
		for (String sessionid : sessionIds) {
			if (chatMap.get(sessionid) == null
					|| chatMap.get(sessionid).equals(""))
				chatMap.put(sessionid, new StringBuffer());
		}
		userChatMap.put(meetingId, chatMap);

		// 初始化 meetingChatMap
		StringBuffer buffer = meetingChatMap.get(meetingId);
		if (buffer == null) {
			buffer = new StringBuffer();
			meetingChatMap.put(meetingId, buffer);
		}

		// 初始化 userChatHtmlMap
		Map<String, StringBuffer> chatHtmlMap = userChatHtmlMap.get(meetingId);
		if (chatHtmlMap == null) {
			chatHtmlMap = new HashMap<String, StringBuffer>();
		}
		for (String sessionid : sessionIds) {
			if (chatHtmlMap.get(sessionid) == null
					|| chatHtmlMap.get(sessionid).equals(""))
				chatHtmlMap.put(sessionid, new StringBuffer());
		}
		userChatHtmlMap.put(meetingId, chatHtmlMap);

		// 初始化 meetingChatHtmlMap
		StringBuffer bufferHtml = meetingChatHtmlMap.get(meetingId);
		if (bufferHtml == null) {
			bufferHtml = new StringBuffer();
			meetingChatHtmlMap.put(meetingId, bufferHtml);
		}

		// 初始化页面的聊天信息
		Map<String, StringBuffer> userHtmlMap = userChatHtmlMap.get(meetingId);
		StringBuffer sBufferHtml = userHtmlMap.get(curuser.getSessionid());
		ScriptSession ssession = sessionsMap.get(meetingId).get(
				curuser.getSessionid());
		if (sBufferHtml.toString().length() < 1) {
			sBufferHtml = meetingChatHtmlMap.get(meetingId);
			StringBuffer sBuffer = meetingChatMap.get(meetingId);
			if (sBufferHtml.toString().length() > 0) {
				userChatMap.get(meetingId).put(curuser.getSessionid(), sBuffer);
				userChatHtmlMap.get(meetingId).put(curuser.getSessionid(),
						sBufferHtml);
			}
		}
		SessionCall(ssession, "chatInitMsgCallback", sBufferHtml.toString());
	}

	/**
	 * 获取某次会议中某人的文本聊天信息
	 * 
	 * @param meetingId
	 * @param curuser
	 * @return
	 */
	public String getUserTxtMsg(String meetingId, UserModel curuser) {
		Map<String, StringBuffer> userMap = userChatMap.get(meetingId);
		StringBuffer sBuffer = userMap.get(curuser.getSessionid());
		if (sBuffer.toString().length() < 1) {
			sBuffer = meetingChatMap.get(meetingId);
			userChatMap.get(meetingId).put(curuser.getSessionid(), sBuffer);
		}
		return sBuffer.toString();
	}

	/**
	 * 发送聊天信息
	 * 
	 * @param receiver
	 * @param msg
	 * @throws Exception
	 */
	public void send(String receiverId, String msg) throws Exception {
		try {
			String smileMsg = handleSmileMsg(msg);
			String htmlMsg = "";
			String textMsg = "";
			String meetingId = meeting.getMeetingId();
			Map<String, StringBuffer> chatMap = userChatMap.get(meetingId);
			StringBuffer mBuffer = meetingChatMap.get(meetingId);
			Map<String, StringBuffer> chatHtmlMap = userChatHtmlMap
					.get(meetingId);
			StringBuffer mBufferHtml = meetingChatHtmlMap.get(meetingId);
			Set<String> sessionIds = sessionsMap.get(meetingId).keySet();
			if (receiverId.equals("1")) {
				logger.info(curuser.getUsername() + "-->大家说：" + msg);

				// 接收人
				UserModel receiver = new UserModel();
				receiver.setSessionid(receiverId);
				receiver.setUsername("大家");

				// HTML信息
				StringBuffer htmlBuffer = new StringBuffer();
				formatMsg(htmlBuffer, curuser, receiver, DateUtils
						.getCurrentTime3(), smileMsg);
				htmlMsg = htmlBuffer.toString();

				// 文本信息
				StringBuffer textBuffer = new StringBuffer();
				formatMsg(textBuffer, curuser, receiver, DateUtils
						.getCurrentTime3(), msg);
				textMsg = textBuffer.toString();

				// 存储至userChatMap
				for (String sessionId : sessionIds) {
					StringBuffer tmpBuffer = chatMap.get(sessionId);
					tmpBuffer.append(textMsg).append("\n");
					chatMap.put(sessionId, tmpBuffer);
				}
				userChatMap.put(meetingId, chatMap);

				// 存储至meetingChatMap
				mBuffer.append(textMsg).append("\n");
				meetingChatMap.put(meetingId, mBuffer);

				// 存储至userChatHtmlMap
				for (String sessionId : sessionIds) {
					StringBuffer tmpBuffer = chatHtmlMap.get(sessionId);
					tmpBuffer.append(htmlMsg);
					chatHtmlMap.put(sessionId, tmpBuffer);
				}
				userChatHtmlMap.put(meetingId, chatHtmlMap);

				// 存储至meetingChatHtmlMap
				mBufferHtml.append(htmlMsg);
				meetingChatHtmlMap.put(meetingId, mBufferHtml);

				SessionsCall(meetingId, "chatUpdateMsgCallback", receiverId,
						curuser.getUsername(), receiver.getUsername(),
						smileMsg, DateUtils.getCurrentTime3());
			} else {
				String sendSid = curuser.getSessionid();

				// 接收人
				UserModel receiverModel = getUserModelBySid(meetingId,
						receiverId);
				String recvUsername = receiverModel.getUsername();
				logger.info(curuser.getUsername() + "-->" + recvUsername + "说："
						+ msg);

				// HTML信息
				StringBuffer htmlBuffer = new StringBuffer();
				formatMsg(htmlBuffer, curuser, receiverModel, DateUtils
						.getCurrentTime3(), smileMsg);
				htmlMsg = htmlBuffer.toString();

				// 文本信息
				StringBuffer textBuffer = new StringBuffer();
				formatMsg(textBuffer, curuser, receiverModel, DateUtils
						.getCurrentTime3(), msg);
				textMsg = textBuffer.toString();

				// 存储至userChatMap
				StringBuffer recvChatBuf = chatMap.get(receiverModel
						.getSessionid());
				StringBuffer sendChatBuf = chatMap.get(sendSid);
				recvChatBuf.append(textMsg).append("\n");
				sendChatBuf.append(textMsg).append("\n");
				chatMap.put(receiverId, recvChatBuf);
				chatMap.put(sendSid, sendChatBuf);
				userChatMap.put(meetingId, chatMap);

				// 存储至userChatHtmlMap
				StringBuffer recvChatHtmlBuf = chatHtmlMap.get(receiverModel
						.getSessionid());
				StringBuffer sendChatHtmlBuf = chatHtmlMap.get(sendSid);
				recvChatHtmlBuf.append(htmlMsg);
				sendChatHtmlBuf.append(htmlMsg);
				chatHtmlMap.put(receiverId, recvChatHtmlBuf);
				chatHtmlMap.put(sendSid, sendChatHtmlBuf);
				userChatHtmlMap.put(meetingId, chatHtmlMap);

				Map<String, ScriptSession> mMap = sessionsMap.get(meetingId);
				ScriptSession senderSession = mMap.get(sendSid);
				ScriptSession receiverSession = mMap.get(receiverId);

				SessionCall(senderSession, "chatUpdateMsgCallback", receiverId,
						curuser.getUsername(), recvUsername, smileMsg,
						DateUtils.getCurrentTime3());
				SessionCall(receiverSession, "chatUpdateMsgCallback",
						receiverId, curuser.getUsername(), recvUsername,
						smileMsg, DateUtils.getCurrentTime3());
			}
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("发送消息异常: " + e);
		}
	}

	/**
	 * 删除聊天信息
	 * 
	 * @throws Exception
	 */
	public void remove() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			String sendSid = curuser.getSessionid();
			Map<String, ScriptSession> mMap = sessionsMap.get(meetingId);
			ScriptSession ssession = mMap.get(sendSid);
			SessionCall(ssession, "chatRemoveCallback");
			showMsg(ssession, "清除聊天信息成功！");
		} catch (Exception e) {
			logger.error("清除聊天信息异常：" + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("清除聊天信息异常: " + e);
		}
	}

	/**
	 * 删除所有的聊天信息
	 * 
	 * @throws ChatServiceException
	 */
	public void removeAll() throws ChatServiceException {
		try {
			String meetingId = meeting.getMeetingId();
			SessionsCall(meetingId, "chatRemoveCallback");
			showMsg(meetingId, "主持人执行清除聊天！");
		} catch (Exception e) {
			logger.error("清除聊天信息异常：" + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("清除聊天信息异常: " + e);
		}
	}

	/**
	 * 全屏
	 * 
	 * @return
	 */
	public void fullScreen() throws ChatServiceException {
		try {
			String meetingId = meeting.getMeetingId();
			String sendSid = curuser.getSessionid();
			Map<String, ScriptSession> mMap = sessionsMap.get(meetingId);
			ScriptSession ssession = mMap.get(sendSid);
			String userChatHtml = userChatHtmlMap.get(meetingId).get(sendSid)
					.toString();
			SessionCall(ssession, "chatInitMsgCallback", userChatHtml);
		} catch (Exception e) {
			logger.error("全屏异常：" + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("全屏异常: " + e);
		}

	}

	/**
	 * 保存
	 * 
	 * @throws Exception
	 */
	public FileTransfer save() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			String filename = "聊天信息_" + curuser.getUsername() + ".html";
			String msgContent = getUserTxtMsg(meetingId, curuser);
			String folderPath = AppConfigure.upload_path + "/" + meetingId
					+ "/chat";
			File folderFile = new File(folderPath);
			if (!folderFile.exists()) {
				folderFile.mkdirs();
			}
			File curuserChatFile = new File(folderPath + "/"
					+ curuser.getUsercode() + ".html");
			File meetingChatFile = new File(folderPath + "/" + meetingId
					+ ".html");

			FileOutputStream out = new FileOutputStream(curuserChatFile);
			out.write(msgContent.getBytes());
			out.flush();

			String meetingChatContent = meetingChatMap.get(meetingId)
					.toString();
			out = new FileOutputStream(meetingChatFile);
			out.write(meetingChatContent.getBytes());
			out.flush();

			return new FileTransfer(java.net.URLEncoder.encode(filename,
					"UTF-8"), "application/x-download", msgContent.getBytes());
		} catch (Exception e) {
			logger.error("保存聊天信息异常：" + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("保存聊天信息异常: " + e);
		}
	}

	/**
	 * 更新StringBuffer的内容的公共函数
	 * 
	 * @param sb
	 * @param sender
	 * @param receiver
	 * @param time
	 * @param msg
	 */
	private void formatMsg(StringBuffer sb, UserModel sender,
			UserModel receiver, String time, String msg) {
		sb
				.append("<table class='chat_table' cellspacing='0' cellpadding='0'><tr class='chat_tr'><td>");
		sb.append("<b>");
		sb.append(sender.getUsername());
		sb.append("</b> -> ");
		if (receiver.getSessionid().equals("1")) {
			sb.append("<b>大家</b>说:");
		} else {
			sb.append("<b>");
			sb.append(receiver.getUsername());
			sb.append("</b>说:");
		}
		sb.append("</td>");
		sb.append("<td align='right' class='chat_td_1'>");
		sb.append(time);
		sb.append("</td></tr>");
		sb
				.append("<tr><td colspan='2' class='chat_td_2'>&nbsp;&nbsp;&nbsp;&nbsp;");
		sb.append(msg);
		sb.append("</td></tr></table>");
	}

	/**
	 * 处理HTML信息
	 * 
	 * @param msg
	 * @return
	 */
	public static String handleSmileMsg(String msg) {
		msg = msg.replace("\n", "<br>&nbsp;&nbsp;&nbsp;&nbsp;");
		msg = msg.replaceAll(":\\)",
				"<img src='../images/smile/smile.gif' border='0'/>");
		msg = msg
				.replaceAll(":D",
						"<img src='../images/smile/open-mouthedSmile.gif' border='0'/>");
		msg = msg.replaceAll(";\\)",
				"<img src='../images/smile/winkingSmile.gif' border='0'/>");
		msg = msg.replaceAll(":-O",
				"<img src='../images/smile/surprisedSmile.gif' border='0'/>");
		msg = msg
				.replaceAll(":P",
						"<img src='../images/smile/smileWithTongueOut.gif' border='0'/>");
		msg = msg.replaceAll(":@",
				"<img src='../images/smile/angrySmile.gif' border='0'/>");
		msg = msg.replaceAll(":S",
				"<img src='../images/smile/confusedSmile.gif' border='0'/>");
		msg = msg.replaceAll(":\\$",
				"<img src='../images/smile/embarrassed-.gif' border='0'/>");
		msg = msg.replaceAll(":\\(",
				"<img src='../images/smile/sad.gif' border='0'/>");
		msg = msg.replaceAll(":&",
				"<img src='../images/smile/crying.gif' border='0'/>");
		msg = msg.replaceAll(":\\|",
				"<img src='../images/smile/Disappointed.gif' border='0'/>");
		msg = msg.replaceAll(":-#",
				"<img src='../images/smile/donotTell.gif' border='0'/>");
		return msg;
	}
}
