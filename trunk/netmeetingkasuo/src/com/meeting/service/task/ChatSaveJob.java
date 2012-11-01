package com.meeting.service.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.service.dwr.ChatService;
import com.meeting.service.dwr.DWRService;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

public class ChatSaveJob implements Job {

	private static Logger logger = Logger.getLogger(ChatSaveJob.class);

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		logger.info("保存聊天信息");
		// 会议时间到触发
		Object object = context.getJobDetail().getJobDataMap().get(
				AppConfigure.MEETING);
		if (object != null) {
			try {
				MeetingModel meeting = (MeetingModel) object;
				String meetingId = meeting.getMeetingId();
				StringBuffer mBuffer = ChatService.meetingChatMap
						.get(meetingId);
				Map<String, StringBuffer> userChatMap = ChatService.userChatMap
						.get(meetingId);
				Map<String, UserModel> userMap = DWRService.usersMap
						.get(meetingId);
				Set<String> usercodes = userMap.keySet();
				for (String usercode : usercodes) {
					UserModel user = userMap.get(usercode);
					StringBuffer uBuffer = userChatMap.get(user.getSessionid());
					saveChatMsg(meetingId, user.getUsercode() + ".html",
							uBuffer.toString());
				}
				saveChatMsg(meetingId, meetingId + ".html", mBuffer.toString());
			} catch (Exception e) {
				logger.error("保存聊天信息失败：" + StackTraceUtil.getStackTrace(e));
			}
		}
	}

	/**
	 * 保存聊天信息
	 * 
	 * @param meeting
	 * @param user
	 * @throws IOException
	 */
	public void saveChatMsg(String meetingId, String fileId, String msg)
			throws IOException {
		String folderPath = AppConfigure.upload_path + "/" + meetingId
				+ "/chat";
		File folderFile = new File(folderPath);
		if (!folderFile.exists()) {
			folderFile.mkdirs();
		}
		File saveFile = new File(folderPath + "/" + fileId);

		FileOutputStream out = new FileOutputStream(saveFile);
		out.write(msg.getBytes());
		out.flush();
	}

}
