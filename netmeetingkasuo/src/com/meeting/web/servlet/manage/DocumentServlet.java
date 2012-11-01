package com.meeting.web.servlet.manage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.meeting.dao.FileDao;
import com.meeting.dao.FileMeetingDao;
import com.meeting.dao.FileUserDao;
import com.meeting.model.FileModel;
import com.meeting.model.FileUserModel;
import com.meeting.model.UserModel;
import com.meeting.service.ExecuteService;
import com.meeting.service.GenerateImage;
import com.meeting.service.GeneratePDF;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.CommonUtils;
import com.meeting.utils.StackTraceUtil;
import com.meeting.web.servlet.BaseServlet;

public class DocumentServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3353865063333040898L;
	private static Logger logger = Logger.getLogger(DocumentServlet.class);

	/**
	 * ��ȡ�ĵ��б�
	 * 
	 * @param request
	 * @param respose
	 * @throws ServletException
	 * @throws IOException
	 */
	public void docList(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		UserModel userModel = (UserModel) session
				.getAttribute(AppConfigure.CURRENT_USER);
		List<FileUserModel> fileList = null;
		if (userModel.getUserrole() == AppConfigure.USER_ROLE_ADMIN) {
			fileList = FileUserDao.getInstance().getFileUserList();
		} else {
			fileList = FileUserDao.getInstance().getFileUserList(
					userModel.getUsercode());
		}
		handleFileUserList(request, fileList);
		forward(request, response, "document_manage.jsp");
	}

	/**
	 * �ϴ��ļ�
	 * 
	 * @param request
	 * @param respose
	 * @throws ServletException
	 * @throws IOException
	 */
	public void uploadFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("type=text/json; charset=GBK");
		UploadUtil uploadUtil = new UploadUtil();
		FileModel filemodel = uploadUtil.uploadFile(request);
		if (filemodel != null) {
			// �浽���ݿ�
			int ret = FileDao.getInstance().addFile(filemodel);
			if (ret == 1) {
				// �洢��SESSION��
				session.setAttribute(AppConfigure.CURRENT_FILE, filemodel);

				// ����JSON����
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("fileId", filemodel.getFileId());
				jsonObject.put("fileName", URLEncoder.encode(filemodel
						.getFileName(), "UTF-8"));
				jsonObject.put("fileSize", filemodel.getFileSize());
				jsonObject.put("filePath", filemodel.getFilePath());
				logger.info("JSON�� " + jsonObject.toString());
				PrintWriter writer = response.getWriter();
				writer.write(jsonObject.toString());
			}
		}
	}

	/**
	 * �ĵ�ת��
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void convertFile(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String fileid = request.getParameter("fileid");
		FileUserModel filemodel = (FileUserModel) FileUserDao.getInstance()
				.getFileUser(fileid);
		UserModel usermodel = (UserModel) session
				.getAttribute(AppConfigure.CURRENT_USER);
		logger.info("===>�յ��ĵ�ת������" + filemodel.getFileName());
		if (filemodel == null) {
			return;
		} else {
			String fileext = filemodel.getFileExt();
			if (!fileext.endsWith(AppConfigure.PDF_EXT)
					&& !fileext.endsWith(AppConfigure.PPT_EXT)
					&& !fileext.endsWith(AppConfigure.DOC_EXT)
					&& !fileext.endsWith(AppConfigure.XLS_EXT)
					&& !fileext.endsWith(AppConfigure.TXT_EXT)) {
				return;
			}
			if (fileext.endsWith(AppConfigure.PNG_EXT)
					|| fileext.endsWith(AppConfigure.JPG_EXT)
					|| fileext.endsWith(AppConfigure.GIF_EXT)
					|| fileext.endsWith(AppConfigure.BMP_EXT)) {
				return;
			}
			FileModel convertModel = convert(usermodel, filemodel);

			int ret = FileDao.getInstance().modFile(convertModel);
			if (ret == 1) {
				response.setContentType("type=text/json; charset=GBK");
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("fileName", convertModel.getFileName());
				jsonObject.put("fileSize", convertModel.getFileSize());
				jsonObject.put("filePage", convertModel.getFilePage());
				logger.info("JSON�� " + jsonObject.toString());
				PrintWriter writer = response.getWriter();
				writer.write(jsonObject.toString());
			}
		}
	}

	/**
	 * ɾ���ĵ�
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void docDelete(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String fileid = request.getParameter("fileid");

		FileMeetingDao.getInstance().delFileMeeting(fileid);
		FileModel fileModel = FileUserDao.getInstance().getFileUser(fileid);
		int ret = FileDao.getInstance().delFile(fileid);
		if (ret == 1) {
			CommonUtils.deleteParentFolder(fileModel.getFilePath());

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "ɾ���ĵ������ɹ���");
			writeJSONObject(response, jsonObject);
		} else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "����ʧ�ܣ�");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * �ĵ�Ԥ��
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void docPreview(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String fileid = request.getParameter("fileid");
		FileUserModel filemodel = FileUserDao.getInstance().getFileUser(fileid);
		request.setAttribute("filemodel", filemodel);
		forward(request, response, "document_preview.jsp");
	}

	/**
	 * �ĵ�ͼƬԤ��
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void imgPreview(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String fileid = request.getParameter("fileid");
		String seq = request.getParameter("seq");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("image/gif");
		FileUserModel filemodel = FileUserDao.getInstance().getFileUser(fileid);
		String filePath = fileImagePath(filemodel, seq);
		File file = new File(filePath);
		OutputStream os = response.getOutputStream();
		InputStream is = new FileInputStream(file);
		byte[] bs = new byte[1024];
		int len;
		while ((len = is.read(bs)) != -1) {
			os.write(bs, 0, len);
		}
		os.flush();
		is.close();
		os.close();
	}

	/**
	 * Ԥ��ָ��ҳ���ĵ�
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void docSpecific(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String fileid = request.getParameter("fileid");
		String seq = request.getParameter("seq");
		FileUserModel fileModel = FileUserDao.getInstance().getFileUser(fileid);
		JSONObject json = null;
		try {
			json = new JSONObject();
			json.put("seq", seq);
			json.put("total", fileModel.getFilePage());
		} catch (Exception e) {
		}
		writeJSONObject(response, json);
	}

	/**
	 * ��ȡ�ļ�ͼƬ·��
	 * 
	 * @param filemodel
	 * @param seq
	 * @return
	 */
	private String fileImagePath(FileUserModel filemodel, String seq) {
		String collection = filemodel.getFileCollection();
		String[] images = collection.split(";");
		String folder = CommonUtils.getFileFolder(filemodel.getFilePath());
		String filename = images[CommonUtils.toInt(seq) - 1];
		File file = new File(folder, filename);
		return file.getAbsolutePath();
	}

	/**
	 * �ĵ�ת��
	 * 
	 * @param srcfile
	 * @param destfolder
	 */
	private FileModel convert(UserModel usermodel, FileModel filemodel) {
		// ת��PDF���
		HashMap<String, HashMap<String, Object>> pdfretMap = null;
		Integer pdfexitVal = 0;
		String pdfexitErr = "";
		boolean pdfret = false;

		// ת��IMG���
		HashMap<String, HashMap<String, Object>> imgretMap = null;
		Integer imgexitVal = 0;
		String imgexitErr = "";

		String srcfile = filemodel.getFilePath().replaceAll("\\\\", "/");
		String destfolder = CommonUtils.getFileFolder(srcfile);
		String srcFileNameWithoutExt = CommonUtils
				.getFilenameWithoutExt(srcfile);
		String destfile = destfolder + "/" + srcFileNameWithoutExt + "."
				+ AppConfigure.PDF_EXT;
		if (!filemodel.getFileExt().toLowerCase()
				.endsWith(AppConfigure.PDF_EXT)) {
			pdfretMap = convertPDF(srcfile, destfile);
			if (pdfretMap != null) {
				HashMap<String, Object> tempMap = pdfretMap
						.get(GeneratePDF.KEY_PROCESS_OFFICE);
				pdfexitVal = Integer.valueOf(String.valueOf(tempMap
						.get(ExecuteService.KEY_EXIT_VALUE)));
				// �ж��˳���ֵ�Ƿ���ȷ�������Ϊ0�������ת���������д���
				if (pdfexitVal != 0) {
					pdfret = false;
					pdfexitErr = String.valueOf(tempMap
							.get(ExecuteService.KEY_ERROR));
					logger.error("ת���ĵ���PDF�����쳣��" + pdfexitErr);
					// onConvertFail(eFile, pdfexitVal, pdfexitErr);
				} else {
					pdfret = true;
				}
			}
		} else {
			pdfret = true;
		}
		if (pdfret) {
			imgretMap = convertIMG(destfile, destfolder);
			if (imgretMap != null) {
				HashMap<String, Object> tempMap = imgretMap
						.get(GenerateImage.KEY_PROCESS_THUMB);
				imgexitVal = Integer.valueOf(String.valueOf(tempMap
						.get(ExecuteService.KEY_EXIT_VALUE)));
				// �ж��˳���ֵ�Ƿ���ȷ�������Ϊ0�������ת���������д���
				if (imgexitVal != 0) {
					imgexitErr = String.valueOf(tempMap
							.get(ExecuteService.KEY_ERROR));
					logger.error("ת���ĵ���IMG�����쳣��" + imgexitErr);
					// onConvertFail(eFile, imgexitVal, imgexitErr);
				} else {
					onConvertSucc(srcfile, destfolder, filemodel);
				}
			}
		}
		return filemodel;
	}

	/**
	 * �ĵ�ת���ɹ�
	 * 
	 * @param user
	 * @param srcfile
	 * @param destfile
	 * @param eFile
	 */
	private void onConvertSucc(String srcfile, String destfolder,
			FileModel eFile) {
		String srcFileNameWithoutExt = CommonUtils
				.getFilenameWithoutExt(srcfile);
		String filepage = "";
		String fileimg = "";
		File destFolder = new File(destfolder);
		File[] files = destFolder.listFiles();
		int count = 0;
		for (File file : files) {
			String filename = file.getName().toLowerCase();
			if (filename.endsWith(AppConfigure.PNG_EXT)
					&& filename.contains(srcFileNameWithoutExt)) {
				count++;
				fileimg += file.getName() + ";";
			}
		}
		filepage = String.valueOf(count);
		eFile.setFilePage(filepage);
		eFile.setFileCollection(fileimg);
	}

	/**
	 * ת��pdf
	 * 
	 * @param srcfile
	 * @param destfolder
	 * @return
	 */
	private HashMap<String, HashMap<String, Object>> convertPDF(String srcfile,
			String destfile) {
		try {
			logger.info("��ʼת���ĵ�Ϊ��" + AppConfigure.PDF_EXT);
			HashMap<String, HashMap<String, Object>> map = GeneratePDF
					.getInstance().convertPDF(srcfile, destfile);
			logger.info("HashMap: " + map);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(StackTraceUtil.getStackTrace(e));
			return null;
		}
	}

	/**
	 * ת��ͼƬ
	 * 
	 * @param srcfile
	 * @param destfolder
	 * @return
	 */
	private HashMap<String, HashMap<String, Object>> convertIMG(String srcfile,
			String destfolder) {
		try {
			logger.info("��ʼת���ĵ�Ϊ��" + AppConfigure.PNG_EXT);
			HashMap<String, HashMap<String, Object>> map = GenerateImage
					.getInstance().convertImage(srcfile, destfolder);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(StackTraceUtil.getStackTrace(e));
			return null;
		}
	}

	/**
	 * �����û������б�
	 * 
	 * @param request
	 * @param umeetingList
	 */
	private void handleFileUserList(HttpServletRequest request,
			List<FileUserModel> filelist) {
		List<Map<String, String>> fileListMaps = new ArrayList<Map<String, String>>();
		for (FileUserModel fileuser : filelist) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("fileName", fileuser.getFileName());
			map.put("fileUser", fileuser.getUserModel().getUsername());
			map.put("fileSize", fileuser.getFileSize());
			map.put("fileExt", fileuser.getFileExt());
			map.put("fileCreate", fileuser.getFileCreate());
			if (fileuser.getFilePage() == null
					|| fileuser.getFilePage().equals("")) {
				map.put("isconverted", "��");
			} else {
				map.put("isconverted", "��ת��");
			}
			if (fileuser.getFilePage() == null
					|| fileuser.getFilePage().equals("")) {
				map.put("filePage", "��");
			} else {
				map.put("filePage", fileuser.getFilePage());
			}

			StringBuffer buffer = new StringBuffer();

			if (fileuser.getFilePage() == null
					|| fileuser.getFilePage().equals("")) {
				buffer
						.append("<a href='#' onclick='docConvert(\"")
						.append(fileuser.getFileId())
						.append(
								"\")'><img src='../images/netmeeting/doc_convert.png' border='0' align='absmiddle'/>&nbsp;ת��</a>")
						.append("&nbsp;&nbsp;&nbsp;");
			} else {
				buffer
						.append("<a href='#' onclick='docPreview(\"")
						.append(fileuser.getFileId())
						.append(
								"\")'><img src='../images/netmeeting/doc_preview.png' border='0' align='absmiddle'/>&nbsp;Ԥ��</a>")
						.append("&nbsp;&nbsp;&nbsp;");
			}

			buffer
					.append("<a href='#' onclick='docDelete(\"")
					.append(fileuser.getFileId())
					.append(
							"\")'><img src='../images/netmeeting/doc_delete.png' border='0' align='absmiddle'/>&nbsp;ɾ��</a>");

			map.put("operate", buffer.toString());
			fileListMaps.add(map);
		}
		request.setAttribute("filelist", fileListMaps);
	}

}
