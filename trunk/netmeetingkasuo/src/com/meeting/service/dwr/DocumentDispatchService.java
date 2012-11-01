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
	 * �ĵ��ַ�Map <meetingId,<fileid,filemodel>>
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
	 * �����ĵ��ַ��ڴ�
	 * 
	 * @param meeting
	 * @param user
	 * @throws Exception
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("ж���ĵ��ַ����...���飺" + meeting.getMeetingId() + ", �û���"
				+ user.getUsername());
		dispatchMap.remove(meeting.getMeetingId());
	}

	/**
	 * �ַ��ĵ�
	 * 
	 * @param fileid
	 * @throws Exception
	 */
	public void documentDispatch(String fileid) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, FileUserModel> fileMap = dispatchMap.get(meetingId);
			FileUserModel file = FileUserDao.getInstance().getFileUser(fileid);
			logger.info("�û���" + file.getUserModel().getUsername() + "���ַ��ĵ���"
					+ file.getFileName() + "��");
			fileMap.put(fileid, file);
			dispatchMap.put(meetingId, fileMap);
			SessionsExceptCall(meetingId, curuser.getSessionid(),
					"documentDispatchCallback", curuser.getUsername(), file
							.getFileId(), file.getFileName());
		} catch (Exception e) {
			logger.error("�ַ��ĵ��쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DocumentDispatchServiceException("�ַ��ĵ��쳣: " + e);
		}
	}

	/**
	 * ͬ������ĵ�
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
				throw new IOException("��ȡ�ļ�����ȷ");
			bufferedInputStream.close();

			return new FileTransfer(java.net.URLEncoder.encode(fileModel
					.getFileName(), "UTF-8"), "application/x-download", bytes);
		} catch (Exception e) {
			logger.error("ͬ������ĵ��쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DocumentDispatchServiceException("ͬ������ĵ��쳣: " + e);
		}
	}

	/**
	 * ͬ������ĵ�
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
			logger.error("�ܾ������ĵ��쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DocumentDispatchServiceException("�ܾ������ĵ��쳣: " + e);
		}
	}

	/**
	 * �رշַ���ǩҳ
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
			logger.error("�رշַ���ǩҳ�쳣: " + StackTraceUtil.getStackTrace(e));
			throw new DocumentDispatchServiceException("�رշַ���ǩҳ�쳣: " + e);
		}
	}

}
