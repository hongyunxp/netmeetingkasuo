package com.meeting.service.dwr;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.directwebremoting.ScriptSession;
import org.json.JSONArray;
import org.json.JSONObject;

import com.meeting.model.FileUserModel;
import com.meeting.model.MeetingModel;
import com.meeting.model.UserModel;
import com.meeting.service.DrawLine2ImageService;
import com.meeting.service.dwr.bean.DrawClass;
import com.meeting.utils.CommonUtils;
import com.meeting.utils.StackTraceUtil;

public class DocumentWhiteBoardService extends DWRService {

	private static Logger logger = Logger
			.getLogger(DocumentWhiteBoardService.class);

	/**
	 * 某会议中所有的白板中的线条图片的路径<meetingid,<wbid,wbid>>
	 */
	public static Map<String, Map<String, String>> mWbMap = new HashMap<String, Map<String, String>>();

	/**
	 * 某会议中所有的白板中的带有时间的绘画对象<meetingid,<wbid,DrawClass>>
	 */
	public static Map<String, Map<String, List<DrawClass>>> mWbDrawMap = new HashMap<String, Map<String, List<DrawClass>>>();

	/**
	 * 某会议中所有的白板中撤销的游标位置<meetingid,<wbid,position>>
	 */
	public static Map<String, Map<String, Integer>> mPosMap = new HashMap<String, Map<String, Integer>>();

	/**
	 * 画图类型
	 */
	public static int DRAW_TYPE_BLANK = 0;
	public static int DRAW_TYPE_LINE = 1;
	public static int DRAW_TYPE_TEXT = 2;
	public static int DRAW_TYPE_POLYLINE = 3;
	public static int DRAW_TYPE_ARROWLINE = 4;
	public static int DRAW_TYPE_RECT = 5;
	public static int DRAW_TYPE_ELLIPSE = 6;

	public static void start(String meetingId, UserModel curuser)
			throws Exception {
		try {
			logger.info("加载文档白板面板...会议：" + meetingId + ", 用户："
					+ curuser.getUsername());
			Map<String, String> wbMap = mWbMap.get(meetingId);
			if (wbMap == null) {
				wbMap = new HashMap<String, String>();
				mWbMap.put(meetingId, wbMap);
			}

			Map<String, List<DrawClass>> wbLineMap = mWbDrawMap.get(meetingId);
			if (wbLineMap == null) {
				wbLineMap = new HashMap<String, List<DrawClass>>();
				mWbDrawMap.put(meetingId, wbLineMap);
			}
			Set<String> wbIdsSet = wbMap.keySet();
			for (String wbId : wbIdsSet) {
				List<DrawClass> dcList = wbLineMap.get(wbId);
				if (dcList == null) {
					dcList = new ArrayList<DrawClass>();
				}
				wbLineMap.put(wbId, dcList);
			}
			mWbDrawMap.put(meetingId, wbLineMap);

			Map<String, Integer> posMap = mPosMap.get(meetingId);
			if (posMap == null) {
				posMap = new HashMap<String, Integer>();
				mPosMap.put(meetingId, posMap);
			}

			ScriptSession ssession = sessionsMap.get(meetingId).get(
					curuser.getSessionid());
			Set<String> wbIdSet = wbMap.keySet();
			for (String tmpWdId : wbIdSet) {
				String newImgageName = getImageName(meetingId, tmpWdId);

				// SessionCall(ssession, "whiteBoardInitCallback", tmpWdId,
				// newImgageName, responseText(meetingId, tmpWdId));
			}
		} catch (Exception e) {
			logger.error("启动文档白板异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("启动白板异常：" + e);
		}
	}

	public void startService() throws Exception {

	}

	/**
	 * 卸载文档白板面板
	 * 
	 * @param meeting
	 * @param user
	 */
	public static void destroyWhiteBoardService(MeetingModel meeting,
			UserModel user) throws Exception {
		String meetingId = meeting.getMeetingId();
		logger
				.info("卸载文档白板面板...会议：" + meetingId + ", 用户："
						+ user.getUsername());
		// 清理内存
		Map<String, String> wbMap = mWbMap.remove(meeting);
		mWbDrawMap.remove(meeting);
		mPosMap.remove(meeting);

		// 清理白板文件夹中的图片
		if (wbMap != null) {
			Set<String> wbSet = wbMap.keySet();
			for (String wbId : wbSet) {
				clearWhiteBoardFolder(wbId);
			}
		}
	}

	/**
	 * 初始化白板内容图片
	 * 
	 * @param wbId
	 * @param imageName
	 * @param width
	 * @param height
	 * @throws Exception
	 */
	public void createWhiteBoardContent(String wbId, String imageName,
			String width, String height) throws Exception {
		try {
			logger.info("初始化文档白板内容图片：文件名" + imageName + ", 宽：" + width + ", 高："
					+ height);
			String meetingId = meeting.getMeetingId();
			Map<String, String> wbMap = mWbMap.get(meetingId);
			wbMap.put(wbId, wbId);
			mWbMap.put(meetingId, wbMap);

			String folderPath = getFileFolder(meetingId, wbId);
			File file = new File(folderPath + "/" + imageName);
			if (!file.exists()) {
				String srcImagePath = hsession.getServletContext().getRealPath(
						"/images/blank.png");
				String imagePath = folderPath + "/" + imageName;

				whiteBoardDrawHandler(meetingId, wbId, imageName,
						DRAW_TYPE_BLANK);

				DrawLine2ImageService.getInstance().setImageSize(
						width + "x" + height, srcImagePath, imagePath);
			}
		} catch (Exception e) {
			logger.error("初始化文档白板内容图片异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("初始化白板内容图片异常：" + e);
		}
	}

	/**
	 * 画曲线
	 * 
	 * @param color
	 * @param wbId
	 * @param newImgageName
	 * @param points
	 * @throws Exception
	 */
	public void drawPolyLine(String color, String wbId, String newImgageName,
			String points) throws Exception {
		try {
			// convert -draw "fill none stroke color polyline x1,y1 x2,y2" 原始图
			// 改后图
			logger.info("画曲线：" + points);
			String meetingId = meeting.getMeetingId();
			String folderPath = getFileFolder(meetingId, wbId);

			File srcFile = new File(folderPath + "/"
					+ getImageName(meetingId, wbId));
			File destFile = new File(folderPath + "/" + newImgageName);

			DrawLine2ImageService.getInstance().drawPolyLine2Image(color,
					points, srcFile.getAbsolutePath(),
					destFile.getAbsolutePath());

			whiteBoardDrawHandler(meetingId, wbId, newImgageName,
					DRAW_TYPE_POLYLINE);

			// 返回客户端图片URL
			SessionsCall(meetingId, "docWhiteBoardDrawlineCallback", wbId,
					newImgageName);

			SessionsCall(meetingId, "docWhiteBoardRedrawTextCallback", wbId,
					responseText(meetingId, wbId));
		} catch (Exception e) {
			logger.error("画曲线异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("画曲线异常：" + e);
		}
	}

	/**
	 * 画直线段
	 * 
	 * @param color
	 * @param wbId
	 * @param newImgageName
	 * @param points
	 * @throws Exception
	 */
	public void drawLine(String color, String wbId, String newImgageName,
			String points) throws Exception {
		try {
			logger.info("画直线：" + points);
			String meetingId = meeting.getMeetingId();
			String folderPath = getFileFolder(meetingId, wbId);
			File srcFile = new File(folderPath + "/"
					+ getImageName(meetingId, wbId));
			File destFile = new File(folderPath + "/" + newImgageName);
			DrawLine2ImageService.getInstance().drawLine2Image(color, points,
					srcFile.getAbsolutePath(), destFile.getAbsolutePath());

			whiteBoardDrawHandler(meetingId, wbId, newImgageName,
					DRAW_TYPE_LINE);

			// 返回客户端图片URL
			SessionsCall(meetingId, "docWhiteBoardDrawlineCallback", wbId,
					newImgageName);

			SessionsCall(meetingId, "docWhiteBoardRedrawTextCallback", wbId,
					responseText(meetingId, wbId));
		} catch (Exception e) {
			logger.error("画直线段异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("画直线段异常：" + e);
		}
	}

	/**
	 * 画带有箭头的线段
	 * 
	 * @param color
	 * @param wbId
	 * @param newImgageName
	 * @param points
	 * @throws Exception
	 */
	public void drawArrowLine(String color, String wbId, String newImgageName,
			String points) throws Exception {
		try {
			logger.info("画箭头线段：" + points);
			String meetingId = meeting.getMeetingId();
			String folderPath = getFileFolder(meetingId, wbId);
			File srcFile = new File(folderPath + "/"
					+ getImageName(meetingId, wbId));
			File destFile = new File(folderPath + "/" + newImgageName);
			DrawLine2ImageService.getInstance().drawArrowLine2Image(color,
					points, srcFile.getAbsolutePath(),
					destFile.getAbsolutePath());

			whiteBoardDrawHandler(meetingId, wbId, newImgageName,
					DRAW_TYPE_ARROWLINE);

			// 返回客户端图片URL
			SessionsCall(meetingId, "docWhiteBoardDrawlineCallback", wbId,
					newImgageName);

			SessionsCall(meetingId, "docWhiteBoardRedrawTextCallback", wbId,
					responseText(meetingId, wbId));
		} catch (Exception e) {
			logger.error("画箭头线段异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("画箭头线段异常：" + e);
		}
	}

	/**
	 * 写字
	 * 
	 * @param wbId
	 * @param msg
	 * @param x
	 * @param y
	 * @throws Exception
	 */
	public void drawText(String wbId, String msg, String x, String y,
			String color) throws Exception {
		try {
			logger.info("写字：" + msg);
			String meetingId = meeting.getMeetingId();

			String msgxy = msg + "|" + x + "|" + y + "|" + color;
			whiteBoardDrawHandler(meetingId, wbId, msgxy, DRAW_TYPE_TEXT);

			SessionsCall(meetingId, "docWhiteBoardDrawTextCallback", wbId, msg, x,
					y, color);
		} catch (Exception e) {
			logger.error("写文字异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("写文字异常：" + e);
		}
	}

	/**
	 * 画矩形
	 * 
	 * @param color
	 * @param wbId
	 * @param newImgageName
	 * @param points
	 * @throws Exception
	 */
	public void drawRectangle(String color, String wbId, String newImgageName,
			String points) throws Exception {
		try {
			logger.info("画矩形：" + points);
			String meetingId = meeting.getMeetingId();
			String folderPath = getFileFolder(meetingId, wbId);
			File srcFile = new File(folderPath + "/"
					+ getImageName(meetingId, wbId));
			File destFile = new File(folderPath + "/" + newImgageName);
			DrawLine2ImageService.getInstance().drawRectangle2Image(color,
					points, srcFile.getAbsolutePath(),
					destFile.getAbsolutePath());

			whiteBoardDrawHandler(meetingId, wbId, newImgageName,
					DRAW_TYPE_RECT);

			// 返回客户端图片URL
			SessionsCall(meetingId, "docWhiteBoardDrawlineCallback", wbId,
					newImgageName);

			SessionsCall(meetingId, "docWhiteBoardRedrawTextCallback", wbId,
					responseText(meetingId, wbId));
		} catch (Exception e) {
			logger.error("画矩形异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("画矩形异常：" + e);
		}
	}

	/**
	 * 画椭圆
	 * 
	 * @param color
	 * @param wbId
	 * @param newImgageName
	 * @param points
	 * @param width
	 * @param height
	 * @throws Exception
	 */
	public void drawEllipse(String color, String wbId, String newImgageName,
			String points, String width, String height) throws Exception {
		try {
			logger.info("画椭圆：" + points);
			String meetingId = meeting.getMeetingId();
			String folderPath = getFileFolder(meetingId, wbId);
			File srcFile = new File(folderPath + "/"
					+ getImageName(meetingId, wbId));
			File destFile = new File(folderPath + "/" + newImgageName);
			DrawLine2ImageService.getInstance().drawEllipse2Image(color,
					points, width, height, srcFile.getAbsolutePath(),
					destFile.getAbsolutePath());

			whiteBoardDrawHandler(meetingId, wbId, newImgageName,
					DRAW_TYPE_ELLIPSE);

			// 返回客户端图片URL
			SessionsCall(meetingId, "docWhiteBoardDrawlineCallback", wbId,
					newImgageName);

			SessionsCall(meetingId, "docWhiteBoardRedrawTextCallback", wbId,
					responseText(meetingId, wbId));
		} catch (Exception e) {
			logger.error("画椭圆异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("画椭圆异常：" + e);
		}
	}

	/**
	 * 删除白板
	 * 
	 * @param wbId
	 * @throws Exception
	 */
	public void whiteboardDelete(String wbId) throws Exception {
		try {
			logger.info("删除文档白板: " + wbId);
			String meetingId = meeting.getMeetingId();

			Map<String, String> wbMap = mWbMap.get(meetingId);
			wbMap.remove(wbId);
			mWbMap.put(meetingId, wbMap);

			Map<String, List<DrawClass>> drawCMap = mWbDrawMap.get(meetingId);
			drawCMap.remove(wbId);
			mWbDrawMap.put(meetingId, drawCMap);

			Map<String, Integer> wbPosMap = mPosMap.get(meetingId);
			wbPosMap.remove(wbId);
			mPosMap.put(meetingId, wbPosMap);

			// 清理白板文件夹中的图片
			clearWhiteBoardFolder(wbId);

			// 返回初始化图片
			SessionsExceptCall(meetingId, hostuser.getSessionid(),
					"whiteboardDeleteCallback", wbId);
		} catch (Exception e) {
			logger.error("删除白板异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("删除文档白板异常：" + e);
		}
	}

	/**
	 * 上一步骤
	 * 
	 * @param wbId
	 */
	public void drawUndo(String wbId) throws Exception {
		try {
			logger.error("返回上一步骤白板：" + wbId);
			String meetingId = meeting.getMeetingId();

			Map<String, Integer> wbPosMap = mPosMap.get(meetingId);
			int wbPos = wbPosMap.get(wbId) - 1;
			if (wbPos < 1) {
				return;
			}
			wbPosMap.put(wbId, wbPos);
			mPosMap.put(meetingId, wbPosMap);

			Map<String, List<DrawClass>> drawCMap = mWbDrawMap.get(meetingId);
			DrawClass drawClass = drawCMap.get(wbId).get(wbPos - 1);

			if (drawClass.type == DRAW_TYPE_TEXT) {
				SessionsCall(meetingId, "whiteBoardRedrawTextCallback", wbId,
						responseText(meetingId, wbId));
			} else {
				// 返回客户端图片URL
				SessionsCall(meetingId, "whiteBoardDrawlineCallback", wbId,
						drawClass.draw);

				SessionsCall(meetingId, "whiteBoardRedrawTextCallback", wbId,
						responseText(meetingId, wbId));
			}
		} catch (Exception e) {
			logger.error("返回上一步骤白板异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("返回上一步骤白板异常：" + e);
		}
	}

	/**
	 * 下一步骤
	 * 
	 * @param wbId
	 */
	public void drawRedo(String wbId) throws Exception {
		try {
			logger.error("返回重做步骤白板：" + wbId);
			String meetingId = meeting.getMeetingId();

			Map<String, Integer> wbPosMap = mPosMap.get(meetingId);
			int wbPos = wbPosMap.get(wbId) + 1;
			if (wbPos > getMaxPosition(meetingId, wbId)) {
				return;
			}
			wbPosMap.put(wbId, wbPos);
			mPosMap.put(meetingId, wbPosMap);

			Map<String, List<DrawClass>> drawCMap = mWbDrawMap.get(meetingId);
			DrawClass drawClass = drawCMap.get(wbId).get(wbPos - 1);
			if (drawClass.type == DRAW_TYPE_TEXT) {
				SessionsCall(meetingId, "whiteBoardRedrawTextCallback", wbId,
						responseText(meetingId, wbId));
			} else {
				// 返回客户端图片URL
				SessionsCall(meetingId, "whiteBoardDrawlineCallback", wbId,
						drawClass.draw);

				SessionsCall(meetingId, "whiteBoardRedrawTextCallback", wbId,
						responseText(meetingId, wbId));
			}

		} catch (Exception e) {
			logger.error("返回重做步骤白板异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("返回重做步骤白板异常：" + e);
		}
	}

	/**
	 * 清理页面白板
	 * 
	 * @param wbId
	 */
	public void drawClear(String wbId, String imageName, String width,
			String height) throws Exception {
		try {
			logger.info("清除文档白板：新文件名" + imageName);
			String meetingId = meeting.getMeetingId();
			Map<String, String> wbMap = mWbMap.get(meetingId);
			wbMap.remove(wbId);
			mWbMap.put(meetingId, wbMap);

			Map<String, List<DrawClass>> drawCMap = mWbDrawMap.get(meetingId);
			drawCMap.remove(wbId);
			mWbDrawMap.put(meetingId, drawCMap);

			Map<String, Integer> wbPosMap = mPosMap.get(meetingId);
			wbPosMap.remove(wbId);
			mPosMap.put(meetingId, wbPosMap);

			// 清理白板文件夹中的图片
			clearWhiteBoardFolder(wbId);

			// 创建新页面
			createWhiteBoardContent(wbId, imageName, width, height);

			// 返回初始化图片
			SessionsCall(meetingId, "whiteBoardDrawlineCallback", wbId,
					imageName);

			showMsg(meetingId, "清理白板内容...");
		} catch (Exception e) {
			logger.error("清除文档白板异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("清除白板异常：" + e);
		}
	}

	/**
	 * 設置其他与会者是否可以使用白板
	 * 
	 * @param wbId
	 * @param flag
	 * @throws Exception
	 */
	public void enableWhiteBoard(String wbId, String flag) throws Exception {
		try {
			logger.info("开启/关闭白板： " + wbId + ", " + flag);
			String meetingId = meeting.getMeetingId();
			SessionsExceptCall(meetingId, hostuser.getSessionid(),
					"enableWhiteBoardCallback", wbId, flag);
		} catch (Exception e) {
			logger.error("开启/关闭白板异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("开启/关闭白板异常：" + e);
		}
	}

	/**
	 * 
	 * @param wbId
	 * @throws Exception
	 */
	public static String getImageName(String meetingId, String wbId)
			throws Exception {
		try {
			Map<String, Integer> wbPosMap = mPosMap.get(meetingId);
			int cusWbPos = wbPosMap.get(wbId);
			Map<String, List<DrawClass>> wbDrawMap = mWbDrawMap.get(meetingId);
			List<DrawClass> dcList = wbDrawMap.get(wbId);
			String imageName = "";
			while (true) {
				DrawClass curDclass = dcList.get(cusWbPos - 1);
				int type = curDclass.type;
				if (type == DRAW_TYPE_TEXT) {
					cusWbPos--;
					type = curDclass.type;
				} else {
					imageName = curDclass.draw;
					break;
				}
			}

			logger.info("已有图片名称为：" + imageName);
			return imageName;
		} catch (Exception e) {
			logger.error("获取当前图片异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("获取当前图片异常：" + e);
		}
	}

	/**
	 * 将已有的文本信息重写到浏览器
	 */
	private static String responseText(String meetingId, String wbId)
			throws Exception {
		try {
			int curpos = mPosMap.get(meetingId).get(wbId);

			Map<String, List<DrawClass>> textMap = mWbDrawMap.get(meetingId);
			List<DrawClass> dcList = textMap.get(wbId);
			JSONArray jsonArr = new JSONArray();
			for (DrawClass dc : dcList) {
				int pos = dc.pos;
				int tmpType = dc.type;
				if (pos > curpos)
					break;
				JSONObject jsonObject = new JSONObject();
				if (tmpType == DRAW_TYPE_TEXT) {
					String msg = dc.draw.substring(0, dc.draw.indexOf("|"));
					String xy = dc.draw.substring(dc.draw.indexOf("|") + 1,
							dc.draw.lastIndexOf("|"));
					String x = xy.substring(0, xy.indexOf("|"));
					String y = xy.substring(xy.lastIndexOf("|") + 1);
					String color = dc.draw
							.substring(dc.draw.lastIndexOf("|") + 1);
					jsonObject.put("msg", msg);
					jsonObject.put("x", x);
					jsonObject.put("y", y);
					jsonObject.put("color", color);
					jsonArr.put(jsonObject);
				}
			}
			logger.info("当前文字为：" + jsonArr.toString());
			return jsonArr.toString();
		} catch (Exception e) {
			logger.error("重写文字异常：" + StackTraceUtil.getStackTrace(e));
			throw new DocumentWhiteBoardServiceException("重写文字异常：" + e);
		}
	}

	/**
	 * 清理白板文件夹
	 * 
	 * @param wbId
	 */
	private static void clearWhiteBoardFolder(String wbId) {
		// String folderPath = getFileFolder(meetingId,wbId);
		// File folderFile = new File(folderPath);
		// if (folderFile.exists() && folderFile.isDirectory()) {
		// File[] files = folderFile.listFiles();
		// for (File f : files) {
		// if (f.isFile()) {
		// f.delete();
		// }
		// }
		// }
	}

	/**
	 * 获取最大的位置
	 * 
	 * @return
	 */
	private static int getMaxPosition(String meetingId, String wbId) {
		Map<String, List<DrawClass>> lineMap = mWbDrawMap.get(meetingId);
		List<DrawClass> dcList = lineMap.get(wbId);
		int tmpPos = 0;
		if (dcList != null) {
			for (DrawClass dc : dcList) {
				if (tmpPos < dc.pos) {
					tmpPos = dc.pos;
				}
			}
		}
		return tmpPos;
	}

	/**
	 * 更新白板Map
	 * 
	 * @param meetingId
	 * @param wbId
	 * @param imageName
	 * @param type
	 */
	public static void whiteBoardDrawHandler(String meetingId, String wbId,
			String imageName, int type) {
		Integer tmpPos = getMaxPosition(meetingId, wbId);
		tmpPos++;
		mPosMap.get(meetingId).put(wbId, tmpPos);

		Map<String, List<DrawClass>> lineMap = mWbDrawMap.get(meetingId);
		DrawClass drawObj = new DrawClass(type, imageName, tmpPos);
		List<DrawClass> dcList = lineMap.get(wbId);
		if (dcList == null) {
			dcList = new ArrayList<DrawClass>();
		}
		dcList.add(drawObj);
		lineMap.put(wbId, dcList);
		mWbDrawMap.put(meetingId, lineMap);
	}

	/**
	 * 获取文件文件夹
	 * 
	 * @param meetingId
	 * @param wbId
	 * @return
	 */
	private String getFileFolder(String meetingId, String wbId) {
		FileUserModel filemodel = DocumentService.mFileMap.get(meetingId).get(
				wbId);
		int filepage = DocumentService.mFilePageMap.get(meetingId).get(wbId);
		String fileFolderString = DocumentService.fileImagePath(filemodel,
				String.valueOf(filepage));
		fileFolderString = CommonUtils.getFilenameWithoutExt(fileFolderString);
		String folderPath = DocumentService.DOCUMENT_FOLDER + "/" + wbId + "/"
				+ fileFolderString;
		return folderPath;
	}

}
