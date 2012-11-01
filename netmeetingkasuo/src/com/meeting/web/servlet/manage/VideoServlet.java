package com.meeting.web.servlet.manage;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.transaction.util.FileHelper;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.meeting.dao.VideoDao;
import com.meeting.dao.VideoUserDao;
import com.meeting.model.UserModel;
import com.meeting.model.VideoModel;
import com.meeting.model.VideoUserModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.CommonUtils;
import com.meeting.web.servlet.BaseServlet;

public class VideoServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3353865063333040898L;
	private static Logger logger = Logger.getLogger(VideoServlet.class);

	/**
	 * ��ȡ��Ƶ�б�
	 * 
	 * @param request
	 * @param respose
	 * @throws ServletException
	 * @throws IOException
	 */
	public void videoList(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		UserModel userModel = (UserModel) session
				.getAttribute(AppConfigure.CURRENT_USER);
		List<VideoUserModel> fileList = null;
		if (userModel.getUserrole() == AppConfigure.USER_ROLE_ADMIN) {
			fileList = VideoUserDao.getInstance().getVideoUserList();
		} else {
			fileList = VideoUserDao.getInstance().getVideoUserList(
					userModel.getUsercode());
		}
		handleVideoUserList(request, fileList);
		forward(request, response, "videoplay_manage.jsp");
	}

	/**
	 * �ϴ��ļ�
	 * 
	 * @param request
	 * @param respose
	 * @throws ServletException
	 * @throws IOException
	 */
	public void uploadVideo(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("type=text/json; charset=GBK");
		UploadUtil uploadUtil = new UploadUtil();
		VideoModel videomodel = uploadUtil.uploadVideo(request);
		if (videomodel != null) {
			// �浽���ݿ�
			int ret = VideoDao.getInstance().addVideo(videomodel);
			if (ret == 1) {
				// �洢��SESSION��
				// session.setAttribute(AppConfigure.CURRENT_FILE, filemodel);

				// ����JSON����
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("videoId", videomodel.getVideoId());
				jsonObject.put("videoName", URLEncoder.encode(videomodel
						.getVideoName(), "UTF-8"));
				jsonObject.put("videoSize", videomodel.getVideoSize());
				jsonObject.put("videoPath", videomodel.getVideoPath());
				logger.info("JSON�� " + jsonObject.toString());
				PrintWriter writer = response.getWriter();
				writer.write(jsonObject.toString());
			}
		}
	}

	/**
	 * ɾ����Ƶ
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void videoDelete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String videoid = request.getParameter("videoid");

		VideoUserModel vUserModel = VideoUserDao.getInstance().getVideoUser(
				videoid);
		int ret = VideoDao.getInstance().delVideo(videoid);
		if (ret == 1) {
			CommonUtils.deleteParentFolder(vUserModel.getVideoPath());

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "ɾ����Ƶ�����ɹ���");
			writeJSONObject(response, jsonObject);
		} else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "����ʧ�ܣ�");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * ��ƵԤ��
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void videoPreview(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String videoid = request.getParameter("videoid");
		VideoUserModel vUserModel = VideoUserDao.getInstance().getVideoUser(
				videoid);

		FileHelper.copy(new File(vUserModel.getVideoPath()), new File(
				AppConfigure.RED5_OFLADEMO_STREAMS + "/"
						+ vUserModel.getVideoId() + "."
						+ vUserModel.getVideoExt()));

		JSONObject jsonObject = new JSONObject();
		String filename = vUserModel.getVideoId() + "."
				+ vUserModel.getVideoExt();
		jsonObject.put("videoName", filename);
		writeJSONObject(response, jsonObject);
	}

	/**
	 * �����û������б�
	 * 
	 * @param request
	 * @param umeetingList
	 */
	private void handleVideoUserList(HttpServletRequest request,
			List<VideoUserModel> filelist) {
		List<Map<String, String>> fileListMaps = new ArrayList<Map<String, String>>();
		for (VideoUserModel vuserModel : filelist) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("videoName", vuserModel.getVideoName());
			map.put("videoUser", vuserModel.getUserModel().getUsername());
			map.put("videoSize", vuserModel.getVideoSize());
			map.put("videoExt", vuserModel.getVideoExt());
			map.put("videoCreate", vuserModel.getVideoCreate());

			StringBuffer buffer = new StringBuffer();

			buffer
					.append("<a href='#' onclick='videoPreview(\"")
					.append(vuserModel.getVideoId())
					.append(
							"\")'><img src='../images/netmeeting/doc_preview.png' border='0' align='absmiddle'/>&nbsp;Ԥ��</a>")
					.append("&nbsp;&nbsp;&nbsp;");

			buffer
					.append("<a href='#' onclick='videoDelete(\"")
					.append(vuserModel.getVideoId())
					.append(
							"\")'><img src='../images/netmeeting/doc_delete.png' border='0' align='absmiddle'/>&nbsp;ɾ��</a>");

			map.put("operate", buffer.toString());
			fileListMaps.add(map);
		}
		request.setAttribute("videolist", fileListMaps);
	}

}
