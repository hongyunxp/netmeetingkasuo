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
	 * 视频Id Map
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
	 * 销毁视频播放内存
	 * 
	 * @param meeting
	 * @param user
	 * @throws Exception
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("卸载视频播放面板...会议：" + meeting.getMeetingId() + ", 用户："
				+ user.getUsername());
	}

	/**
	 * 播放视频
	 * 
	 * @param videoId
	 * @throws Exception
	 */
	public void VideoPlay(String videoId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("播放视频: " + meetingId);
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
			logger.error("播放视频异常: " + StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("播放视频异常: " + e);
		}
	}

	/**
	 * 关闭视频
	 * 
	 * @param videoId
	 * @throws Exception
	 */
	public void videoRemove(String videoId) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			logger.info("关闭视频: " + meetingId);
			videoModelMap.remove(meetingId);
			SessionsExceptCall(meetingId, curuser.getUsercode(),
					"videoRemoveCallback", videoId, curuser.getUsername());
		} catch (Exception e) {
			logger.error("关闭视频异常: " + StackTraceUtil.getStackTrace(e));
			throw new UserListServiceException("关闭视频异常: " + e);
		}
	}
}
