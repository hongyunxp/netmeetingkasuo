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
	 * 获取用户
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
	 * 用户注册/用户添加
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
			jsonObject.put("text", "用户已经存在！");
			writeJSONObject(response, jsonObject);
			return;
		} else if (!password.equals(verifypwd)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "密码不匹配！");
			writeJSONObject(response, jsonObject);
			return;
		} else if (!verifycode.equals(realverifycode)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "验证码不匹配！");
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
			jsonObject.put("text", "操作成功！");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * 删除用户
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
			jsonObject.put("text", "删除用户操作成功！");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * 更新用户信息
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
			jsonObject.put("text", "用户不存在！");
			writeJSONObject(response, jsonObject);
			return;
		} else if (!password.equals(verifypwd)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "密码不匹配！");
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
			jsonObject.put("text", "操作成功！");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * 获取用户列表
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
	 * 处理用户列表
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
			map.put("userrole", user.getUserrole() == USERROLE_ADMIN ? "管理员"
					: "普通用户");
			map.put("useremail", user.getUseremail());
			map.put("createtime", user.getCreatetime());
			map.put("updatetime", user.getUpdatetime());

			StringBuffer buffer = new StringBuffer();
			buffer
					.append("<a href='#' onclick='userEdit(\"")
					.append(user.getUsercode())
					.append(
							"\")'><img src='../images/netmeeting/user_edit.gif' border='0' align='absmiddle'/>&nbsp;编辑</a>")
					.append("&nbsp;&nbsp;&nbsp;<a href='#' onclick='userDel(\"")
					.append(user.getUsercode())
					.append(
							"\")'><img src='../images/netmeeting/user_del.gif' border='0' align='absmiddle'/>&nbsp;删除</a>");
			map.put("operate", buffer.toString());
			userMaps.add(map);
		}
		request.setAttribute("userlist", userMaps);
	}

	/**
	 * 查找用户信息
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
	 * 用户登录
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
			jsonObject.put("text", "验证码不匹配！");
			writeJSONObject(response, jsonObject);
			return;
		} else {
			UserModel model = UserDao.getInstance().getUserPwd(usercode,
					password);
			if (model == null) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ret", FAILURE);
				jsonObject.put("text", "登录失败，账号或密码错误！");
				writeJSONObject(response, jsonObject);
			} else {
				session.setAttribute(AppConfigure.CURRENT_USER, model);
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("ret", SUCCESS);
				jsonObject.put("text", "用户登录成功！");
				writeJSONObject(response, jsonObject);
			}
		}
	}

	/**
	 * 用户退出
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
			logger.info("用户[" + usermodel.getUsername() + "]退出");
		}
		response.sendRedirect("../login.jsp");
	}

	/**
	 * 判断用户是否存在
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
			jsonObject.put("text", "用户账户已经存在，请使用其他账户名！");
			writeJSONObject(response, jsonObject);
		} else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "用户账户可以使用！");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * 上传图片
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
			// 更新到数据库
			int ret = UserDao.getInstance().modUser(usermodel);
			if (ret == 1) {
				// 返回JSON对象
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("imgPath", usermodel.getUserpic());
				logger.info("JSON： " + jsonObject.toString());
				PrintWriter writer = response.getWriter();
				writer.write(jsonObject.toString());
			}
			session.setAttribute(AppConfigure.CURRENT_USER, usermodel);
		}
	}

	/**
	 * 文档图片预览
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
	 * 用户配置获取
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
	 * 更新用户的个性会议配置
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

		// 配置是否允许举手
		ConfigModel handupConfig = new ConfigModel(userModel.getUsercode(),
				AppConfigure.KEY_ALLOWHANDUP, handup, DateUtils
						.getCurrentTime());
		if (ConfigDao.getInstance().existUserConfig(
				AppConfigure.KEY_ALLOWHANDUP, userModel.getUsercode())) {
			ConfigDao.getInstance().modUserConfig(handupConfig);
		} else {
			ConfigDao.getInstance().addUserConfig(handupConfig);
		}

		// 配置是否允许远程协助
		ConfigModel dscontrolConfig = new ConfigModel(userModel.getUsercode(),
				AppConfigure.KEY_ALLOWDESKTOPCONTROL, dscontrol, DateUtils
						.getCurrentTime());
		if (ConfigDao.getInstance().existUserConfig(
				AppConfigure.KEY_ALLOWDESKTOPCONTROL, userModel.getUsercode())) {
			ConfigDao.getInstance().modUserConfig(dscontrolConfig);
		} else {
			ConfigDao.getInstance().addUserConfig(dscontrolConfig);
		}

		// 配置是否允许参会者使用白板
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
		jsonObject.put("text", "修改配置操作成功！");
		writeJSONObject(response, jsonObject);
	}

	/**
	 * 获取用户列表，供选择
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
	 * 查找用户
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
	 * 判断用户是否存在
	 * 
	 * @param usercode
	 * @return
	 */
	private boolean existUser(String usercode) {
		return UserDao.getInstance().existUser(usercode);
	}
}
