package com.meeting.service.dwr;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.log4j.Logger;

import com.meeting.dao.VideoMeetingDao;
import com.meeting.dao.VideoUserDao;
import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.model.VideoMeetingModel;
import com.meeting.model.VideoUserModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

public class VideoPlayService extends DWRService {

	private static Logger logger = Logger.getLogger(VideoPlayService.class);

	/**
	 * ��ƵId Map
	 */
	public static Map<String, VideoUserModel> videoModelMap = new HashMap<String, VideoUserModel>();

	public void startService() throws Exception {
		String meetingId = meeting.getMeetingId();
		if (videoModelMap.containsKey(meetingId)) {
			VideoUserModel vUserModel = videoModelMap.get(meetingId);
			String filename = vUserModel.getVideoId() + "."
					+ vUserModel.getVideoExt();
			String videoname = vUserModel.getVideoName();
			SessionsCall(meetingId, "videoPlayCallback", filename, videoname);
		}
	}

	/**
	 * ������Ƶ�����ڴ�
	 * 
	 * @param meeting
	 * @param user
	 * @throws Exception
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("ж����Ƶ�������...���飺" + meeting.getMeetingId() + ", �û���"
				+ user.getUsername());
	}

	/**
	 * ������Ƶ
	 * 
	 * @param videoId
	 * @throws Exception
	 */
	public void VideoPlay(String videoId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("������Ƶ: " + meetingId);
			VideoUserModel vUserModel = VideoUserDao.getInstance()
					.getVideoUser(videoId);
			VideoMeetingModel model = VideoMeetingDao.getInstance()
					.getMeetingVideo(meetingId, videoId);
			if (model == null) {
				VideoMeetingDao.getInstance().addVideoMeeting(meetingId,
						videoId);
			}
			if (vUserModel != null) {
				videoModelMap.put(meetingId, vUserModel);
				FileHelper.copy(new File(vUserModel.getVideoPath()), new File(
						AppConfigure.RED5_OFLADEMO_STREAMS + "/"
								+ vUserModel.getVideoId() + "."
								+ vUserModel.getVideoExt()));
				String filename = vUserModel.getVideoId() + "."
						+ vUserModel.getVideoExt();
				String videoname = vUserModel.getVideoName();
				SessionsCall(meetingId, "videoPlayCallback", filename,
						videoname);
			}
		} catch (Exception e) {
			logger.error("������Ƶ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("������Ƶ�쳣: " + e);
		}
	}

	/**
	 * �ر���Ƶ
	 * 
	 * @param videoId
	 * @throws Exception
	 */
	public void videoRemove(String videoId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("�ر���Ƶ: " + meetingId);
			videoModelMap.remove(meetingId);
			SessionsExceptCall(meetingId, curuser.getUsercode(),
					"videoRemoveCallback", videoId, curuser.getUsername());
		} catch (Exception e) {
			logger.error("�ر���Ƶ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("�ر���Ƶ�쳣: " + e);
		}
	}
}
