package com.meeting.service.dwr;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.io.FileTransfer;

import com.meeting.dao.FileMeetingDao;
import com.meeting.dao.FileUserDao;
import com.meeting.model.FileUserModel;
import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.service.dwr.comparator.FileIdComparator;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.CommonUtils;
import com.meeting.utils.StackTraceUtil;

public class DocumentService extends DWRService {

	private static Logger logger = Logger.getLogger(DocumentService.class);

	/**
	 * ĳ�����е������ĵ�<meetingId,<fileid,FileModel>>
	 */
	public static Map<String, Map<String, FileUserModel>> mFileMap = new HashMap<String, Map<String, FileUserModel>>();

	/**
	 * ĳ�ĵ���ҳ��<meetingId,<fileId,curFilePage>>
	 */
	public static Map<String, Map<String, Integer>> mFilePageMap = new HashMap<String, Map<String, Integer>>();

	/**
	 * ĳ�ĵ����Զ�����ʱ����<meetingId,<fileId,time>>
	 */
	public static Map<String, Map<String, Integer>> mFileAutoTimeMap = new HashMap<String, Map<String, Integer>>();

	/**
	 * ĳ��timer��map
	 */
	public static Map<String, Timer> timerMap = new HashMap<String, Timer>();

	public static String DOCUMENT_FOLDER = null;

	/**
	 * �����ĵ�����
	 */
	public void startService() throws Exception {
		try {
			String meetingId = meeting.getMeetingId();

			Map<String, FileUserModel> filesMap = mFileMap.get(meetingId);
			if (filesMap == null) {
				filesMap = new HashMap<String, FileUserModel>();
				mFileMap.put(meetingId, filesMap);
			}

			Map<String, Integer> pageMap = mFilePageMap.get(meetingId);
			if (pageMap == null) {
				pageMap = new HashMap<String, Integer>();
				mFilePageMap.put(meetingId, pageMap);
			}

			Map<String, Integer> autoTimeMap = mFileAutoTimeMap.get(meetingId);
			if (autoTimeMap == null) {
				autoTimeMap = new HashMap<String, Integer>();
				mFileAutoTimeMap.put(meetingId, autoTimeMap);
			}

			Map<String, ScriptSession> sessionMap = sessionsMap.get(meeting
					.getMeetingId());
			ScriptSession session = sessionMap.get(curuser.getSessionid());

			// ���¼����ĵ�
			Set<String> fileids = filesMap.keySet();
			List<String> tFileidList = new ArrayList<String>();
			for (String fileid : fileids) {
				tFileidList.add(fileid);
			}
			Collections.sort(tFileidList, new FileIdComparator());
			List<String> fileidList = new ArrayList<String>();
			List<String> filenameList = new ArrayList<String>();
			List<Integer> filepageList = new ArrayList<Integer>();
			// List<Integer> autoPlayList = new ArrayList<Integer>();
			for (String fileid : tFileidList) {
				FileUserModel fileModel = filesMap.get(fileid);
				fileidList.add(fileid);
				filenameList.add(fileModel.getFileName());
				filepageList.add(pageMap.get(fileid));
				// autoPlayList.add(autoTimeMap.get(fileid));
			}
			SessionCall(session, "documentInitPlayCallback", fileidList
					.toArray(new String[] {}), filenameList
					.toArray(new String[] {}), filepageList
					.toArray(new Integer[] {}));

			DocumentWhiteBoardService.start(meetingId, curuser);
		} catch (Exception e) {
			logger.error("�����ĵ�����ʧ�ܣ�" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("�����ĵ������쳣��" + e);
		}
	}

	/**
	 * �����ĵ������ڴ�
	 * 
	 * @param meeting
	 * @param user
	 * @throws DocumentServiceException
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("�����ĵ��������...���飺" + meeting.getMeetingId() + ", �û���"
				+ user.getUsername());

	}

	/**
	 * �����ĵ�
	 * 
	 * @param fileid
	 * @param seq
	 */
	public void documentPlay(String fileid, int seq) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			FileUserModel fileModel = FileUserDao.getInstance().getFileUser(
					fileid);

			// ��ӵ��ĵ�Map
			Map<String, FileUserModel> fileMap = mFileMap.get(meetingId);
			fileMap.put(fileid, fileModel);
			mFileMap.put(meetingId, fileMap);

			// �ĵ�ҳ������Map
			Map<String, Integer> filePageMap = mFilePageMap.get(meetingId);
			filePageMap.put(fileid, seq);
			mFilePageMap.put(meetingId, filePageMap);

			//			
//			SessionsExceptCall(meetingId, hostuser.getSessionid(),
//					"documentInit", fileid, fileModel.getFileName());

			// ��ʾ�ĵ�ҳ��������
			String filePageSelect = documentPageList(fileid);
			SessionsCall(meetingId, "documentPageSelect", fileid,
					filePageSelect);

			// �����ݿ��У����ĵ����뵽������
			if (FileMeetingDao.getInstance().getMeetingFile(fileid) == null) {
				FileMeetingDao.getInstance().addFileMeeting(meetingId, fileid);
			}

			// �����ĵ���ĳһҳ
			String filePath = fileImagePath(fileModel, String.valueOf(seq));
			String filePageNameWithOutExt = CommonUtils
					.getFilenameWithoutExt(filePath);
			String filePageNameWithExt = CommonUtils
					.getFilenameWithExt(filePath);
			if (DOCUMENT_FOLDER == null || DOCUMENT_FOLDER.equals("")) {
				DOCUMENT_FOLDER = AppConfigure.upload_path + "/" + meetingId
						+ "/document";
			}
			String folderPath = DOCUMENT_FOLDER + "/" + fileid + "/"
					+ filePageNameWithOutExt;
			File folderFile = new File(folderPath);
			if (!folderFile.exists()) {
				folderFile.mkdirs();
			}
			// FileHelper.copy(new File(filePath), new File(folderPath + "/"
			// + filePageNameWithExt));

			// ������ͼ���
			DocumentWhiteBoardService.whiteBoardDrawHandler(meetingId, fileid,
					filePageNameWithExt,
					DocumentWhiteBoardService.DRAW_TYPE_BLANK);

			SessionsCall(meetingId, "documentPlayCallback", fileid, seq,
					fileModel.getFilePage(), filePageNameWithExt);
		} catch (Exception e) {
			logger.error("�����ĵ�ʧ�ܣ�" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("�����ĵ��쳣��" + e);
		}
	}

	/**
	 * ������һҳ
	 * 
	 * @param fileid
	 * @throws Exception
	 */
	public void documentNext(String fileid) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, FileUserModel> fileMap = mFileMap.get(meetingId);
			FileUserModel fileModel = fileMap.get(fileid);
			String filePageNameWithExt = CommonUtils
					.getFilenameWithExt(fileModel.getFilePath());
			int totalPage = Integer.valueOf(fileModel.getFilePage());
			int filepage = mFilePageMap.get(meetingId).get(fileid);
			if (filepage + 1 > totalPage) {
				documentShowAll(fileid);
			} else {
				filepage++;
				mFilePageMap.get(meetingId).put(fileid, filepage);
				SessionsCall(meetingId, "documentPlayCallback", fileid,
						filepage, totalPage, filePageNameWithExt);
			}
		} catch (Exception e) {
			logger.error("������һҳ�쳣��" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("������һҳ�쳣��" + e);
		}
	}

	/**
	 * ������һҳ
	 * 
	 * @param fileid
	 * @throws Exception
	 */
	public void documentPre(String fileid) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, FileUserModel> fileMap = mFileMap.get(meetingId);
			FileUserModel fileModel = fileMap.get(fileid);
			String filePageNameWithExt = CommonUtils
					.getFilenameWithExt(fileModel.getFilePath());
			int totalPage = Integer.valueOf(fileModel.getFilePage());
			int filepage = mFilePageMap.get(meetingId).get(fileid);
			if (filepage - 1 < 1) {
				documentShowAll(fileid);
			} else {
				filepage--;
				mFilePageMap.get(meetingId).put(fileid, filepage);
				SessionsCall(meetingId, "documentPlayCallback", fileid,
						filepage, totalPage, filePageNameWithExt);
			}
		} catch (Exception e) {
			logger.error("������һҳ�쳣��" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("������һҳ�쳣��" + e);
		}
	}

	/**
	 * ��ʾ�����ĵ�
	 * 
	 * @param fileid
	 * @throws Exception
	 */
	public void documentShowAll(String fileid) throws Exception {
		try {
			Map<String, ScriptSession> sessionMap = sessionsMap.get(meeting
					.getMeetingId());
			ScriptSession session = sessionMap.get(curuser.getSessionid());
			String filePageSelect = documentPageList(fileid);
			SessionCall(session, "documentShowAllCallback", fileid,
					filePageSelect);
		} catch (Exception e) {
			logger.error("��ʾ�����ĵ��쳣��" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("��ʾ�����ĵ��쳣��" + e);
		}
	}

	/**
	 * �����Զ�����
	 * 
	 * @param fileid
	 * @param value
	 */
	public void documentAutoPlay(final String fileid, String value)
			throws Exception {
		try {
			final String meetingId = meeting.getMeetingId();
			final Map<String, FileUserModel> fileMap = mFileMap.get(meetingId);
			Integer periodInt = Integer.valueOf(value);
			Map<String, Integer> autoTimeMap = mFileAutoTimeMap.get(meetingId);
			autoTimeMap.put(fileid, periodInt);
			final Timer timer = new Timer();
			timerMap.put(fileid, timer);
			timer.schedule(new TimerTask() {
				public void run() {
					try {
						FileUserModel fileModel = fileMap.get(fileid);
						String filePageNameWithExt = CommonUtils
								.getFilenameWithExt(fileModel.getFilePath());
						int totalPage = Integer
								.valueOf(fileModel.getFilePage());
						int filepage = mFilePageMap.get(meetingId).get(fileid);
						if (filepage + 1 > totalPage) {
							documentShowAll(fileid);
							timer.cancel();
						} else {
							filepage++;
							mFilePageMap.get(meetingId).put(fileid, filepage);
							SessionsCall(meetingId, "documentPlayCallback",
									fileid, filepage, totalPage,
									filePageNameWithExt);
						}
					} catch (Exception e) {
						logger.error("�Զ������ĵ��쳣��"
								+ StackTraceUtil.getStackTrace(e));
					}
				}
			}, periodInt * 1000, periodInt * 1000);
		} catch (Exception e) {
			logger.error("�Զ������ĵ��쳣��" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("�Զ������ĵ��쳣��" + e);
		}
	}

	/**
	 * 
	 * @param fileid
	 * @param seq
	 */
	public void documentSetPage(String fileid, String seq) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			mFilePageMap.get(meetingId).put(fileid, Integer.valueOf(seq));
		} catch (Exception e) {
			logger.error("�����ĵ���ǰҳ���쳣��" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("�����ĵ���ǰҳ���쳣��" + e);
		}
	}

	/**
	 * �������ö�ʱʱ��
	 * 
	 * @param fileid
	 * @param newTime
	 */
	public void documentStopAutoPlay(String fileid, String newTime)
			throws Exception {
		try {
			Object object = timerMap.get(fileid);
			if (object != null) {
				Timer timer = (Timer) object;
				timer.cancel();
				timerMap.remove(fileid);
			}
		} catch (Exception e) {
			logger.error("ֹͣ�Զ������쳣��" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("ֹͣ�Զ������쳣��" + e);
		}
	}

	/**
	 * ɾ���ĵ�
	 * 
	 * @param fileid
	 */
	public void documentDelete(String fileid) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, FileUserModel> fileMap = mFileMap.get(meetingId);
			fileMap.remove(fileid);
			mFileMap.put(fileid, fileMap);
			Map<String, Integer> filePageMap = mFilePageMap.get(meetingId);
			filePageMap.remove(fileid);
			mFilePageMap.put(fileid, filePageMap);

			SessionsExceptCall(meetingId, hostuser.getSessionid(),
					"documentDeleteCallback", fileid);
		} catch (Exception e) {
			logger.error("ɾ���ĵ��쳣��" + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("ɾ���ĵ��쳣: " + e);
		}
	}

	/**
	 * �����ĵ�
	 * 
	 * @throws Exception
	 */
	public FileTransfer documentSave(String fileid) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			Map<String, FileUserModel> fileMap = mFileMap.get(meetingId);
			FileUserModel fileModel = fileMap.get(fileid);
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
			logger.error("�����ĵ��쳣��" + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("�����ĵ��쳣: " + e);
		}
	}

	/**
	 * ��ȡĳ�ĵ��ĵ�ǰҳ/ȫ��ҳ
	 * 
	 * @param docid
	 * @return
	 */
	private String documentPageList(String fileid) {
		FileUserModel fileModel = mFileMap.get(meeting.getMeetingId()).get(
				fileid);
		int total = Integer.valueOf(fileModel.getFilePage());
		StringBuffer buffer = new StringBuffer();
		for (int i = 1; i <= total; i++) {
			String dropDownString = i + "/" + total;
			buffer.append(dropDownString).append(";");
		}
		return buffer.toString();
	}

	/**
	 * ��ȡ�ļ�ͼƬ·��
	 * 
	 * @param filemodel
	 * @param seq
	 * @return
	 */
	public static String fileImagePath(FileUserModel filemodel, String seq) {
		String collection = filemodel.getFileCollection();
		String[] images = collection.split(";");
		String folder = CommonUtils.getFileFolder(filemodel.getFilePath());
		String filename = images[CommonUtils.toInt(seq) - 1];
		File file = new File(folder, filename);
		return file.getAbsolutePath();
	}

}
