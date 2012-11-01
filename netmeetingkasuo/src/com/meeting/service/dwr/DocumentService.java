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
	 * 某会议中的所有文档<meetingId,<fileid,FileModel>>
	 */
	public static Map<String, Map<String, FileUserModel>> mFileMap = new HashMap<String, Map<String, FileUserModel>>();

	/**
	 * 某文档的页数<meetingId,<fileId,curFilePage>>
	 */
	public static Map<String, Map<String, Integer>> mFilePageMap = new HashMap<String, Map<String, Integer>>();

	/**
	 * 某文档的自动播放时间间隔<meetingId,<fileId,time>>
	 */
	public static Map<String, Map<String, Integer>> mFileAutoTimeMap = new HashMap<String, Map<String, Integer>>();

	/**
	 * 某个timer的map
	 */
	public static Map<String, Timer> timerMap = new HashMap<String, Timer>();

	public static String DOCUMENT_FOLDER = null;

	/**
	 * 开启文档共享
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

			// 重新加载文档
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
			logger.error("开启文档共享失败！" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("开启文档共享异常！" + e);
		}
	}

	/**
	 * 销毁文档共享内存
	 * 
	 * @param meeting
	 * @param user
	 * @throws DocumentServiceException
	 */
	public static void destroyService(MeetingModel meeting, UserModel user,
			UserModel hostuser) throws Exception {
		logger.info("销毁文档共享面板...会议：" + meeting.getMeetingId() + ", 用户："
				+ user.getUsername());

	}

	/**
	 * 播放文档
	 * 
	 * @param fileid
	 * @param seq
	 */
	public void documentPlay(String fileid, int seq) throws Exception {
		try {
			String meetingId = meeting.getMeetingId();
			FileUserModel fileModel = FileUserDao.getInstance().getFileUser(
					fileid);

			// 添加到文档Map
			Map<String, FileUserModel> fileMap = mFileMap.get(meetingId);
			fileMap.put(fileid, fileModel);
			mFileMap.put(meetingId, fileMap);

			// 文档页数加入Map
			Map<String, Integer> filePageMap = mFilePageMap.get(meetingId);
			filePageMap.put(fileid, seq);
			mFilePageMap.put(meetingId, filePageMap);

			//			
//			SessionsExceptCall(meetingId, hostuser.getSessionid(),
//					"documentInit", fileid, fileModel.getFileName());

			// 显示文档页数下拉框
			String filePageSelect = documentPageList(fileid);
			SessionsCall(meetingId, "documentPageSelect", fileid,
					filePageSelect);

			// 在数据库中，将文档加入到会议中
			if (FileMeetingDao.getInstance().getMeetingFile(fileid) == null) {
				FileMeetingDao.getInstance().addFileMeeting(meetingId, fileid);
			}

			// 拷贝文档中某一页
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

			// 创建画图面板
			DocumentWhiteBoardService.whiteBoardDrawHandler(meetingId, fileid,
					filePageNameWithExt,
					DocumentWhiteBoardService.DRAW_TYPE_BLANK);

			SessionsCall(meetingId, "documentPlayCallback", fileid, seq,
					fileModel.getFilePage(), filePageNameWithExt);
		} catch (Exception e) {
			logger.error("播放文档失败！" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("播放文档异常！" + e);
		}
	}

	/**
	 * 翻到下一页
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
			logger.error("翻到下一页异常！" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("翻到下一页异常！" + e);
		}
	}

	/**
	 * 翻到上一页
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
			logger.error("翻到上一页异常！" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("翻到上一页异常！" + e);
		}
	}

	/**
	 * 显示所有文档
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
			logger.error("显示所有文档异常！" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("显示所有文档异常！" + e);
		}
	}

	/**
	 * 设置自动播放
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
						logger.error("自动播放文档异常！"
								+ StackTraceUtil.getStackTrace(e));
					}
				}
			}, periodInt * 1000, periodInt * 1000);
		} catch (Exception e) {
			logger.error("自动播放文档异常！" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("自动播放文档异常！" + e);
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
			logger.error("设置文档当前页数异常！" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("设置文档当前页数异常！" + e);
		}
	}

	/**
	 * 重新设置定时时间
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
			logger.error("停止自动播放异常！" + StackTraceUtil.getStackTrace(e));
			throw new DocumentServiceException("停止自动播放异常！" + e);
		}
	}

	/**
	 * 删除文档
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
			logger.error("删除文档异常：" + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("删除文档异常: " + e);
		}
	}

	/**
	 * 下载文档
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
				throw new IOException("读取文件不正确");
			bufferedInputStream.close();

			return new FileTransfer(java.net.URLEncoder.encode(fileModel
					.getFileName(), "UTF-8"), "application/x-download", bytes);
		} catch (Exception e) {
			logger.error("下载文档异常：" + StackTraceUtil.getStackTrace(e));
			throw new ChatServiceException("下载文档异常: " + e);
		}
	}

	/**
	 * 获取某文档的当前页/全部页
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
	 * 获取文件图片路径
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
