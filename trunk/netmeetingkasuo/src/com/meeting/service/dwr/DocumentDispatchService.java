package com.meeting.service.dwr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.io.FileTransfer;

import com.meeting.dao.FileUserDao;
import com.meeting.model.FileUserModel;
import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.utils.StackTraceUtil;

public class DocumentDispatchService extends DWRService {

	private static Logger logger = Logger
			.getLogger(DocumentDispatchService.class);

	/**
	 * 文档分发Map <meetingId,<fileid,filemodel>>
	 */
	private static Map<String, Map<String, FileUserModel>> dispatchMap = new HashMap<String, Map<String, FileUserModel>>();

	public void startService() throws Exception {
		String meetingId = meeting.getMeetingId();
		Map<String, FileUserModel> fileMap = dispatchMap.get(meetingId);
		if (fileMap == null) {
			fileMap = new HashMap<String, FileUserModel>();
			dispatchMap.put(meetingId, fileMap);
		}
		Set<String> fileIdSet = fileMap.keySet();
		for (String fileid : fileIdSet) {
			FileUserModel fileModel = fileMap.get(fileid);
			if (fileModel != null) {
				ScriptSession userSession = sessionsMap.get(meetingId).get(
						curuser.getSessionid());
				SessionCall(userSession, "documentDispatchCallback", curuser
						.getUsername(), fileModel.getFileId(), fileModel
						.getFileName());
			}
		}
	}

	/**
	 * 销毁文档分发内存
	 * 
	 * @param meeting
	 * @param user
	 * @throws Exception
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("卸载文档分发面板...会议：" + meeting.getMeetingId() + ", 用户："
				+ user.getUsername());
		dispatchMap.remove(meeting.getMeetingId());
	}

	/**
	 * 分发文档
	 * 
	 * @param fileid
	 * @throws Exception
	 */
	public void documentDispatch(String fileid) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, FileUserModel> fileMap = dispatchMap.get(meetingId);
			FileUserModel file = FileUserDao.getInstance().getFileUser(fileid);
			logger.info("用户【" + file.getUserModel().getUsername() + "】分发文档【"
					+ file.getFileName() + "】");
			fileMap.put(fileid, file);
			dispatchMap.put(meetingId, fileMap);
			SessionsExceptCall(meetingId, curuser.getSessionid(),
					"documentDispatchCallback", curuser.getUsername(), file
							.getFileId(), file.getFileName());
		} catch (Exception e) {
			logger.error("分发文档异常: " + StackTraceUtil.getStackTrace(e));
			throw new DocumentDispatchServiceException("分发文档异常: " + e);
		}
	}

	/**
	 * 同意接受文档
	 * 
	 * @param fileid
	 * @throws Exception
	 */
	public FileTransfer documentDispatchAccept(String fileid) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			ScriptSession hostSession = sessionsMap.get(meetingId).get(
					hostuser.getSessionid());
			SessionCall(hostSession, "documentDispatchStatusCallback", curuser
					.getSessionid(), true);

			FileUserModel fileModel = FileUserDao.getInstance().getFileUser(
					fileid);
			String filePath = fileModel.getFilePath();
			File docFile = new File(filePath);
			long len = docFile.length();
			byte[] bytes = new byte[(int) len];
			BufferedInputStream bufferedInputStream = new BufferedInputStream(
					new FileInputStream(docFile));
			int r = bufferedInputStream.read(bytes);
			if (r != len)
				throw new IOException("读取文件不正确");
			bufferedInputStream.close();

			return new FileTransfer(java.net.URLEncoder.encode(fileModel
					.getFileName(), "UTF-8"), "application/x-download", bytes);
		} catch (Exception e) {
			logger.error("同意接受文档异常: " + StackTraceUtil.getStackTrace(e));
			throw new DocumentDispatchServiceException("同意接受文档异常: " + e);
		}
	}

	/**
	 * 同意接受文档
	 * 
	 * @param fileid
	 * @throws Exception
	 */
	public void documentDispatchReject(String fileid) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			ScriptSession hostSession = sessionsMap.get(meetingId).get(
					hostuser.getSessionid());
			SessionCall(hostSession, "documentDispatchStatusCallback", curuser
					.getSessionid(), false);
		} catch (Exception e) {
			logger.error("拒绝接受文档异常: " + StackTraceUtil.getStackTrace(e));
			throw new DocumentDispatchServiceException("拒绝接受文档异常: " + e);
		}
	}

	/**
	 * 关闭分发标签页
	 * 
	 * @param id
	 * @throws Exception
	 */
	public void documentDispatchDelete(String id) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, FileUserModel> fileMap = dispatchMap.get(meetingId);
			if(fileMap != null){
				fileMap.clear();
				dispatchMap.put(meetingId, fileMap);
			}
			SessionsExceptCall(meetingId, hostuser.getSessionid(),
					"documentDispatchDeleteCallback", id, hostuser
							.getUsername());
		} catch (Exception e) {
			logger.error("关闭分发标签页异常: " + StackTraceUtil.getStackTrace(e));
			throw new DocumentDispatchServiceException("关闭分发标签页异常: " + e);
		}
	}

}
