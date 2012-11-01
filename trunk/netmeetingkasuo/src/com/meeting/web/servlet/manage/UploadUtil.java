package com.meeting.web.servlet.manage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import com.meeting.model.FileModel;
import com.meeting.model.UserModel;
import com.meeting.model.VideoModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.CommonUtils;
import com.meeting.utils.DateUtils;
import com.meeting.utils.StackTraceUtil;

/**
 * 上传通用类
 * 
 * @author zcg
 * 
 */

@SuppressWarnings("unchecked")
public class UploadUtil {

	private static final Logger logger = Logger.getLogger(UploadUtil.class);

	/**
	 * 上传文档通用处理接口
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public FileModel uploadFile(HttpServletRequest request) throws IOException {
		request.setCharacterEncoding("UTF-8");
		String uploadFolder = AppConfigure.upload_path;
		UserModel usermodel = (UserModel) request.getSession().getAttribute(
				AppConfigure.CURRENT_USER);
		String curtime = String.valueOf(System.currentTimeMillis());
		uploadFolder += "/" + usermodel.getUsercode() + "/" + curtime + "/";
		File foldrFile = new File(uploadFolder);
		if (!foldrFile.exists()) {
			foldrFile.mkdirs();
		}
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			return null;
		}
		String outString = "";
		File file = null;
		FileModel filemodel = null;
		for (Iterator i = items.iterator(); i.hasNext();) {
			FileItem fileItem = (FileItem) i.next();
			if (!fileItem.isFormField()) {
				if (fileItem.getName() != null && fileItem.getSize() > 0) {
					String fileName = fileItem.getName();
					outString = DateUtils.getCurrentTime() + ": 文档【" + fileName
							+ "】开始上传！";
					logger.info(outString);
					String fileext = fileName.substring(fileName
							.lastIndexOf(".") + 1);
					String fileNewName = curtime + "." + fileext;
					String filepath = uploadFolder + fileNewName;
					file = new File(filepath);
					// 文件名
					InputStream in = fileItem.getInputStream();
					FileOutputStream out = new FileOutputStream(file);
					byte[] buffer = new byte[1024];

					int n = -1;
					while ((n = in.read(buffer)) != -1) {
						out.write(buffer, 0, n);
					}
					in.close();
					out.close();
					fileItem.delete();
					outString = DateUtils.getCurrentTime() + ": 文档【" + fileName
							+ "】上传成功！";
					logger.info(outString);
					String fileSize = CommonUtils.getFileSize(file.length());
					filemodel = new FileModel();
					filemodel.setFileId(curtime);
					filemodel.setFileName(fileName);
					filemodel.setFilePath(filepath);
					filemodel.setFileSize(fileSize);
					filemodel.setFileCreate(DateUtils.getCurrentTime());
					filemodel.setFilePage("");
					filemodel.setFileCollection("");
					filemodel.setFileExt(fileext);
					filemodel.setUserId(usermodel.getUsercode());
				}
			}
		}
		return filemodel;
	}

	/**
	 * 上传用户图片通用处理接口
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public UserModel uploadUserAvatar(HttpServletRequest request)
			throws IOException {
		request.setCharacterEncoding("UTF-8");
		UserModel usermodel = (UserModel) request.getSession().getAttribute(
				AppConfigure.CURRENT_USER);
		
		//上传文件夹
		String uploadFolder = AppConfigure.upload_path;
		uploadFolder += "/" + usermodel.getUsercode() + "/";
		File foldrFile = new File(uploadFolder);
		if (!foldrFile.exists()) {
			foldrFile.mkdirs();
		}

		//删除原有的图片
		String oriPath = usermodel.getUserpic();
		if (oriPath != null && oriPath.length() > 0) {
			File oriFile = new File(oriPath);
			if (oriFile.exists()) {
				oriFile.delete();
			}
		}

		//获取上传对象
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			return null;
		}
		
		//上传过程
		File file = null;
		for (Iterator i = items.iterator(); i.hasNext();) {
			FileItem fileItem = (FileItem) i.next();
			if (!fileItem.isFormField()) {
				if (fileItem.getName() != null && fileItem.getSize() > 0) {
					String fileName = fileItem.getName();
					logger.info(DateUtils.getCurrentTime() + ": 图片【" + fileName
							+ "】开始上传！");
					String fileext = fileName.substring(fileName
							.lastIndexOf(".") + 1);
					String filepath = uploadFolder + usermodel.getUsercode()
							+ "." + fileext;
					file = new File(filepath);
					// 文件名
					InputStream in = fileItem.getInputStream();
					FileOutputStream out = new FileOutputStream(file);
					byte[] buffer = new byte[1024];

					int n = -1;
					while ((n = in.read(buffer)) != -1) {
						out.write(buffer, 0, n);
					}
					in.close();
					out.close();
					fileItem.delete();
					logger.info(DateUtils.getCurrentTime() + ": 图片【" + fileName
							+ "】上传成功！");
					usermodel.setUserpic(filepath);
				}
			}
		}
		return usermodel;
	}

	/**
	 * 上传文档通用处理接口
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public VideoModel uploadVideo(HttpServletRequest request)
			throws IOException {
		request.setCharacterEncoding("UTF-8");
		String uploadFolder = AppConfigure.upload_path;
		UserModel usermodel = (UserModel) request.getSession().getAttribute(
				AppConfigure.CURRENT_USER);
		String curtime = String.valueOf(System.currentTimeMillis());
		uploadFolder += "/" + usermodel.getUsercode() + "/" + curtime + "/";
		File foldrFile = new File(uploadFolder);
		if (!foldrFile.exists()) {
			foldrFile.mkdirs();
		}
		FileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		List items = null;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			return null;
		}
		String outString = "";
		File file = null;
		VideoModel videomodel = null;
		for (Iterator i = items.iterator(); i.hasNext();) {
			FileItem fileItem = (FileItem) i.next();
			if (!fileItem.isFormField()) {
				if (fileItem.getName() != null && fileItem.getSize() > 0) {
					String fileName = fileItem.getName();
					outString = DateUtils.getCurrentTime() + ": 文档【" + fileName
							+ "】开始上传！";
					logger.info(outString);
					String fileext = fileName.substring(fileName
							.lastIndexOf(".") + 1);
					String fileNewName = curtime + "." + fileext;
					String filepath = uploadFolder + fileNewName;
					file = new File(filepath);
					// 文件名
					InputStream in = fileItem.getInputStream();
					FileOutputStream out = new FileOutputStream(file);
					byte[] buffer = new byte[1024];

					int n = -1;
					while ((n = in.read(buffer)) != -1) {
						out.write(buffer, 0, n);
					}
					in.close();
					out.close();
					fileItem.delete();
					outString = DateUtils.getCurrentTime() + ": 文档【" + fileName
							+ "】上传成功！";
					logger.info(outString);
					String fileSize = CommonUtils.getFileSize(file.length());
					videomodel = new VideoModel();
					videomodel.setVideoId(curtime);
					videomodel.setVideoName(fileName);
					videomodel.setVideoPath(filepath);
					videomodel.setVideoSize(fileSize);
					videomodel.setVideoCreate(DateUtils.getCurrentTime());
					videomodel.setVideoExt(fileext);
					videomodel.setUserId(usermodel.getUsercode());
				}
			}
		}
		return videomodel;
	}

}
