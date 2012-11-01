package com.meeting.web.servlet.manage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.meeting.dao.ConfigDao;
import com.meeting.dao.UserDao;
import com.meeting.model.ConfigModel;
import com.meeting.model.UserModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.DateUtils;
import com.meeting.web.servlet.BaseServlet;

public class UserServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4569817119536761373L;

	public static final int USERROLE_ADMIN = 0;
	public static final int USERROLE_COMMON = 1;

	private static Logger logger = Logger.getLogger(UserServlet.class);

	/**
	 * ��ȡ�û�
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userGet(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String usercode = request.getParameter("usercode");
		UserModel userModel = UserDao.getInstance().getUser(usercode);
		if (userModel != null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("usercode", userModel.getUsercode());
			jsonObject.put("password", userModel.getPassword());
			jsonObject.put("username", userModel.getUsername());
			jsonObject.put("userrole", userModel.getUserrole());
			jsonObject.put("useremail", userModel.getUseremail());
			jsonObject.put("userpic", userModel.getUserpic());
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * �û�ע��/�û����
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userAdd(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String usercode = request.getParameter("usercode");
		String password = request.getParameter("password");
		String verifypwd = request.getParameter("verifypwd");
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String verifycode = request.getParameter("verifycode");
		String realverifycode = (String) session.getAttribute("rand");
		if (existUser(usercode)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "�û��Ѿ����ڣ�");
			writeJSONObject(response, jsonObject);
			return;
		} else if (!password.equals(verifypwd)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "���벻ƥ�䣡");
			writeJSONObject(response, jsonObject);
			return;
		} else if (!verifycode.equals(realverifycode)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "��֤�벻ƥ�䣡");
			writeJSONObject(response, jsonObject);
			return;
		}
		UserModel model = new UserModel();
		model = new UserModel();
		model.setUsercode(usercode);
		model.setPassword(password);
		model.setSessionid("");
		model.setUsername(username);
		model.setUserrole(USERROLE_COMMON);
		model.setUseremail(email);
		model.setUserpic("");
		model.setCreatetime(DateUtils.getCurrentTime());
		model.setUpdatetime(DateUtils.getCurrentTime());
		int ret = UserDao.getInstance().addUser(model);
		if (ret == 1) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "�����ɹ���");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * ɾ���û�
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userDel(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String usercode = request.getParameter("usercode");
		int ret = UserDao.getInstance().delUser(usercode);
		if (ret == 1) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "ɾ���û������ɹ���");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * �����û���Ϣ
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userMod(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String usercode = request.getParameter("usercode");
		String password = request.getParameter("password");
		String verifypwd = request.getParameter("verifypwd");
		String username = request.getParameter("username");
		String email = request.getParameter("email");
		String userrole = request.getParameter("userrole");
		String picpath = request.getParameter("picpath");
		if (picpath == null || picpath.equals("")) {
			picpath = "";
		}
		if (!existUser(usercode)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "�û������ڣ�");
			writeJSONObject(response, jsonObject);
			return;
		} else if (!password.equals(verifypwd)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "���벻ƥ�䣡");
			writeJSONObject(response, jsonObject);
			return;
		}
		UserModel model = new UserModel();
		model = new UserModel();
		model.setUsercode(usercode);
		model.setPassword(password);
		model.setSessionid("");
		model.setUsername(username);
		model.setUserrole(Integer.valueOf(userrole));
		model.setUseremail(email);
		model.setUserpic(picpath);
		model.setUpdatetime(DateUtils.getCurrentTime());
		int ret = UserDao.getInstance().modUser(model);
		if (ret == 1) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "�����ɹ���");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * ��ȡ�û��б�
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<UserModel> userlist = UserDao.getInstance().getUserList();
		handleUserList(request, userlist);
		forward(request, response, "user_manage.jsp");
	}

	/**
	 * �����û��б�
	 * 
	 * @param request
	 * @param userlist
	 */
	public void handleUserList(HttpServletRequest request,
			List<UserModel> userlist) {
		List<Map<String, String>> userMaps = new ArrayList<Map<String, String>>();
		for (UserModel user : userlist) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("username", user.getUsername());
			map.put("usercode", user.getUsercode());
			map.put("password", user.getPassword());
			map.put("userrole", user.getUserrole() == USERROLE_ADMIN ? "����Ա"
					: "��ͨ�û�");
			map.put("useremail", user.getUseremail());
			map.put("createtime", user.getCreatetime());
			map.put("updatetime", user.getUpdatetime());

			StringBuffer buffer = new StringBuffer();
			buffer
					.append("<a href='#' onclick='userEdit(\"")
					.append(user.getUsercode())
					.append(
							"\")'><img src='../images/netmeeting/user_edit.gif' border='0' align='absmiddle'/>&nbsp;�༭</a>")
					.append("&nbsp;&nbsp;&nbsp;<a href='#' onclick='userDel(\"")
					.append(user.getUsercode())
					.append(
							"\")'><img src='../images/netmeeting/user_del.gif' border='0' align='absmiddle'/>&nbsp;ɾ��</a>");
			map.put("operate", buffer.toString());
			userMaps.add(map);
		}
		request.setAttribute("userlist", userMaps);
	}

	/**
	 * �����û���Ϣ
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userSearch(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String usercode = request.getParameter("usercode");
		if (usercode == null || usercode.equals("")) {
			usercode = "";
		}
		String username = request.getParameter("username");
		if (username == null || username.equals("")) {
			username = "";
		}
		String email = request.getParameter("email");
		if (email == null || email.equals("")) {
			email = "";
		}
		String userrole = request.getParameter("userrole");
		int userroleInt = -1;
		if (userrole != null && !userrole.equals("")
				&& !userrole.equals("null")) {
			userroleInt = Integer.parseInt(userrole);
		}
		String regtime_begin = request.getParameter("regtime_begin");
		if (regtime_begin == null || regtime_begin.equals("")) {
			regtime_begin = "";
		}
		String regtime_end = request.getParameter("regtime_end");
		if (regtime_end == null || regtime_end.equals("")) {
			regtime_end = "";
		}

		List<UserModel> userlist = UserDao.getInstance().searchUserList(
				usercode, username, email, userroleInt, regtime_begin,
				regtime_end);
		handleUserList(request, userlist);
		forward(request, response, "user_manage.jsp");
	}

	/**
	 * �û���¼
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userLogin(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String usercode = request.getParameter("usercode");
		String password = request.getParameter("password");
		String verifycode = request.getParameter("verifycode");
		String realverifycode = (String) session.getAttribute("rand");
		if (!verifycode.equals(realverifycode)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "��֤�벻ƥ�䣡");
			writeJSONObject(response, jsonObject);
			return;
		} else {
			UserModel model = UserDao.getInstance().getUserPwd(usercode,
					password);
			if (model == null) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ret", FAILURE);
				jsonObject.put("text", "��¼ʧ�ܣ��˺Ż��������");
				writeJSONObject(response, jsonObject);
			} else {
				session.setAttribute(AppConfigure.CURRENT_USER, model);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ret", SUCCESS);
				jsonObject.put("text", "�û���¼�ɹ���");
				writeJSONObject(response, jsonObject);
			}
		}
	}

	/**
	 * �û��˳�
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userLogout(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Object obj = (Object) session.getAttribute(AppConfigure.CURRENT_USER);
		UserModel usermodel = null;
		if (obj != null) {
			usermodel = (UserModel) obj;
			session.invalidate();
			logger.info("�û�[" + usermodel.getUsername() + "]�˳�");
		}
		response.sendRedirect("../login.jsp");
	}

	/**
	 * �ж��û��Ƿ����
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void checkUser(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String usercode = request.getParameter("usercode");
		if (existUser(usercode)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "�û��˻��Ѿ����ڣ���ʹ�������˻�����");
			writeJSONObject(response, jsonObject);
		} else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "�û��˻�����ʹ�ã�");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * �ϴ�ͼƬ
	 * 
	 * @param request
	 * @param respose
	 * @throws ServletException
	 * @throws IOException
	 */
	public void uploadPhoto(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		response.setContentType("type=text/json; charset=GBK");
		UploadUtil uploadUtil = new UploadUtil();
		UserModel usermodel = uploadUtil.uploadUserAvatar(request);
		if (usermodel != null) {
			// ���µ����ݿ�
			int ret = UserDao.getInstance().modUser(usermodel);
			if (ret == 1) {
				// ����JSON����
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("imgPath", usermodel.getUserpic());
				logger.info("JSON�� " + jsonObject.toString());
				PrintWriter writer = response.getWriter();
				writer.write(jsonObject.toString());
			}
			session.setAttribute(AppConfigure.CURRENT_USER, usermodel);
		}
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
		String imgPath = request.getParameter("imgPath");
		response.setHeader("Cache-Control", "no-cache");
		response.setContentType("image/gif");
		File file = new File(imgPath);
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
	 * �û����û�ȡ
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userSettings(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserModel userModel = (UserModel) session
				.getAttribute(AppConfigure.CURRENT_USER);
		ConfigModel handupConfig = ConfigDao.getInstance().getUserConfig(
				AppConfigure.KEY_ALLOWHANDUP, userModel.getUsercode());
		if (handupConfig == null) {
			handupConfig = ConfigDao.getInstance().getConfig(
					AppConfigure.KEY_ALLOWHANDUP);
		}
		ConfigModel desktopControlConfig = ConfigDao.getInstance()
				.getUserConfig(AppConfigure.KEY_ALLOWDESKTOPCONTROL,
						userModel.getUsercode());
		if (desktopControlConfig == null) {
			desktopControlConfig = ConfigDao.getInstance().getConfig(
					AppConfigure.KEY_ALLOWDESKTOPCONTROL);
		}
		ConfigModel whiteBoardConfig = ConfigDao.getInstance().getUserConfig(
				AppConfigure.KEY_ALLOWWHITEBOARD, userModel.getUsercode());
		if (whiteBoardConfig == null) {
			whiteBoardConfig = ConfigDao.getInstance().getConfig(
					AppConfigure.KEY_ALLOWWHITEBOARD);
		}
		JSONObject json = new JSONObject();
		json.put(AppConfigure.KEY_ALLOWHANDUP, handupConfig.getValue());
		json.put(AppConfigure.KEY_ALLOWDESKTOPCONTROL, desktopControlConfig
				.getValue());
		json.put(AppConfigure.KEY_ALLOWWHITEBOARD, whiteBoardConfig.getValue());
		writeJSONObject(response, json);
	}

	/**
	 * �����û��ĸ��Ի�������
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userSettingUpdate(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserModel userModel = (UserModel) session
				.getAttribute(AppConfigure.CURRENT_USER);
		String handup = request.getParameter(AppConfigure.KEY_ALLOWHANDUP);
		String dscontrol = request
				.getParameter(AppConfigure.KEY_ALLOWDESKTOPCONTROL);
		String wboard = request.getParameter(AppConfigure.KEY_ALLOWWHITEBOARD);
		if (handup == null) {
			handup = "0";
		}
		if (dscontrol == null) {
			dscontrol = "0";
		}
		if (wboard == null) {
			wboard = "0";
		}

		// �����Ƿ��������
		ConfigModel handupConfig = new ConfigModel(userModel.getUsercode(),
				AppConfigure.KEY_ALLOWHANDUP, handup, DateUtils
						.getCurrentTime());
		if (ConfigDao.getInstance().existUserConfig(
				AppConfigure.KEY_ALLOWHANDUP, userModel.getUsercode())) {
			ConfigDao.getInstance().modUserConfig(handupConfig);
		} else {
			ConfigDao.getInstance().addUserConfig(handupConfig);
		}

		// �����Ƿ�����Զ��Э��
		ConfigModel dscontrolConfig = new ConfigModel(userModel.getUsercode(),
				AppConfigure.KEY_ALLOWDESKTOPCONTROL, dscontrol, DateUtils
						.getCurrentTime());
		if (ConfigDao.getInstance().existUserConfig(
				AppConfigure.KEY_ALLOWDESKTOPCONTROL, userModel.getUsercode())) {
			ConfigDao.getInstance().modUserConfig(dscontrolConfig);
		} else {
			ConfigDao.getInstance().addUserConfig(dscontrolConfig);
		}

		// �����Ƿ�����λ���ʹ�ðװ�
		ConfigModel wboardConfig = new ConfigModel(userModel.getUsercode(),
				AppConfigure.KEY_ALLOWWHITEBOARD, wboard, DateUtils
						.getCurrentTime());
		if (ConfigDao.getInstance().existUserConfig(
				AppConfigure.KEY_ALLOWWHITEBOARD, userModel.getUsercode())) {
			ConfigDao.getInstance().modUserConfig(wboardConfig);
		} else {
			ConfigDao.getInstance().addUserConfig(wboardConfig);
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ret", SUCCESS);
		jsonObject.put("text", "�޸����ò����ɹ���");
		writeJSONObject(response, jsonObject);
	}

	/**
	 * ��ȡ�û��б���ѡ��
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void userSelList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<UserModel> userlist = UserDao.getInstance().getUserList();
		request.setAttribute("userlist", userlist);
		forward(request, response, "user_select.jsp");
	}

	/**
	 * �����û�
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void findUsers(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String username = request.getParameter("username");
		username = URLDecoder.decode(username, "UTF-8");
		logger.info("username: " + username);
		List<UserModel> userlist = UserDao.getInstance().getUserListByUsername(
				username);
		request.setAttribute("userlist", userlist);
		forward(request, response, "user_select.jsp");
	}

	/**
	 * �ж��û��Ƿ����
	 * 
	 * @param usercode
	 * @return
	 */
	private boolean existUser(String usercode) {
		return UserDao.getInstance().existUser(usercode);
	}
}
