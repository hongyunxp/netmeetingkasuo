package com.meeting.web.servlet.manage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.meeting.dao.MeetingDao;
import com.meeting.dao.UserDao;
import com.meeting.dao.UserMeetingDao;
import com.meeting.model.MeetingModel;
import com.meeting.model.UserMeetingModel;
import com.meeting.model.UserModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.DateUtils;
import com.meeting.utils.MeetingIDUtil;
import com.meeting.web.servlet.BaseServlet;

public class MeetingServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2394540070794451023L;

	/**
	 * 获取会议
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void meetingGet(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String meetingid = request.getParameter("meetingid");
		String usercode = request.getParameter("usercode");
		UserMeetingModel userModel = UserMeetingDao.getInstance().getMeeting(
				meetingid, usercode, AppConfigure.MEETING_ROLE_ADMIN);
		if (userModel != null) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("subject", userModel.getMeetingModel().getSubject());
			jsonObject.put("begintime", userModel.getMeetingModel()
					.getBegintime());
			jsonObject.put("agenda", userModel.getMeetingModel().getAgenda());
			jsonObject.put("verifycode", userModel.getMeetingModel()
					.getVerifyCode());
			int duration = Integer.valueOf(userModel.getMeetingModel()
					.getDuration());
			int hour = duration / 60;
			int minute = duration % 60;
			jsonObject.put("hour", hour + "");
			jsonObject.put("minute", minute + "");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * 返回会议详细信息
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void meetingDetail(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String usercode = request.getParameter("usercode");
		String meetingid = request.getParameter("meetingid");
		UserMeetingModel userMeetingModel = UserMeetingDao.getInstance()
				.getMeeting(meetingid, usercode,
						AppConfigure.MEETING_ROLE_ADMIN);
		if (userMeetingModel == null) {
			userMeetingModel = UserMeetingDao.getInstance().getMeeting(
					meetingid, usercode, AppConfigure.MEETING_ROLE_COMMON);
		}
		UserModel hostModel = UserMeetingDao.getInstance().getMeetingHost(
				meetingid);
		request.setAttribute("userMeetingModel", userMeetingModel);
		request.setAttribute(AppConfigure.HOST_USER, hostModel);
		request
				.setAttribute("meetingModel", userMeetingModel
						.getMeetingModel());
		String folderPath = AppConfigure.upload_path + "/" + meetingid
				+ "/chat";
		File file = new File(folderPath);
		if (!file.exists()) {
			file.mkdirs();
		}

		forward(request, response, "meeting_detail.jsp");
	}

	/**
	 * 预约会议
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void meetingAdd(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String subject = request.getParameter("subject");
		String begintime = request.getParameter("begintime");
		String hour = request.getParameter("hour");
		String minute = request.getParameter("minute");
		String agenda = request.getParameter("agenda");
		String verifycode = request.getParameter("verifycode");
		String verifycode2 = request.getParameter("verifycode2");
		String invites = request.getParameter("invites");
		if (!verifycode.equals(verifycode2)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "会议验证码不匹配！");
			writeJSONObject(response, jsonObject);
			return;
		}
		String duration = String.valueOf(Integer.parseInt(hour) * 60
				+ Integer.parseInt(minute));
		String meetingId = MeetingIDUtil.getMeetingId();
		MeetingModel model = new MeetingModel();
		model.setMeetingId(meetingId);
		model.setSubject(subject);
		model.setAgenda(agenda);
		model.setVerifyCode(verifycode);
		model.setBegintime(begintime);
		model.setDuration(duration);
		model.setState(AppConfigure.MEETING_NOT_START);
		model.setCreatetime(DateUtils.getCurrentTime());
		model.setUpdatetime(DateUtils.getCurrentTime());

		// 加入邀请的用户
		String[] usercodeArr = invites.split(";");
		for (String uc : usercodeArr) {
			if (uc != null && uc.length() > 0) {
				UserModel tmpUserModel = UserDao.getInstance().getUser(uc);
				UserMeetingModel umModel = new UserMeetingModel();
				umModel.setUmEnterTime(DateUtils.getCurrentTime());
				umModel.setUmRole(AppConfigure.MEETING_ROLE_COMMON);
				umModel.setUmState(AppConfigure.USER_MEETING_STATE_INITIAL);
				umModel.setUserModel(tmpUserModel);
				umModel.setMeetingModel(model);
				UserMeetingDao.getInstance().addUserMeeting(umModel);
			}
		}

		// 加入主持人到会议关联表中
		UserModel userModel = getSessionUserModel(request);
		int ret = MeetingDao.getInstance().addMeeting(model);
		UserMeetingModel umModel = new UserMeetingModel();
		umModel.setUmEnterTime(DateUtils.getCurrentTime());
		umModel.setUmRole(AppConfigure.MEETING_ROLE_ADMIN);
		umModel.setUmState(AppConfigure.USER_MEETING_STATE_INITIAL);
		umModel.setUserModel(userModel);
		umModel.setMeetingModel(model);
		int ret2 = UserMeetingDao.getInstance().addUserMeeting(umModel);
		if (ret == 1 && ret2 == 1) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "操作成功！");
			writeJSONObject(response, jsonObject);
		} else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "操作失败！");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * 删除会议
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void meetingDel(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String usercode = request.getParameter("usercode");
		String meetingid = request.getParameter("meetingid");
		int ret = MeetingDao.getInstance().delMeeting(meetingid);
		int ret2 = UserMeetingDao.getInstance().delUserMeeting(usercode,
				meetingid);
		if (ret == 1 && ret2 == 1) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "删除用户操作成功！");
			writeJSONObject(response, jsonObject);
		} else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "操作失败！");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * 修改会议
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void meetingMod(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String meetingid = request.getParameter("meetingid");
		String subject = request.getParameter("subject");
		String begintime = request.getParameter("begintime");
		String hour = request.getParameter("hour");
		String minute = request.getParameter("minute");
		String agenda = request.getParameter("agenda");
		String verifycode = request.getParameter("verifycode");
		String verifycode2 = request.getParameter("verifycode2");
		if (!verifycode.equals(verifycode2)) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "会议验证码不匹配！");
			writeJSONObject(response, jsonObject);
			return;
		}
		String duration = String.valueOf(Integer.parseInt(hour) * 60
				+ Integer.parseInt(minute));
		MeetingModel model = new MeetingModel();
		model.setMeetingId(meetingid);
		model.setSubject(subject);
		model.setAgenda(agenda);
		model.setVerifyCode(verifycode);
		model.setBegintime(begintime);
		model.setDuration(duration);
		model.setState(AppConfigure.MEETING_NOT_START);
		model.setCreatetime(DateUtils.getCurrentTime());
		model.setUpdatetime(DateUtils.getCurrentTime());
		int ret = MeetingDao.getInstance().modMeeting(model);
		if (ret == 1) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", SUCCESS);
			jsonObject.put("text", "操作成功！");
			writeJSONObject(response, jsonObject);
		} else {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("ret", FAILURE);
			jsonObject.put("text", "操作失败！");
			writeJSONObject(response, jsonObject);
		}
	}

	/**
	 * 会议列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void meetingList(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserModel userModel = (UserModel) session
				.getAttribute(AppConfigure.CURRENT_USER);
		List<UserMeetingModel> umeetingList = null;
		if (userModel.getUserrole() == AppConfigure.USER_ROLE_ADMIN) {
			umeetingList = UserMeetingDao.getInstance().getMeetingList();
		} else {
			umeetingList = UserMeetingDao.getInstance()
					.getMeetingListByUserCode(userModel.getUsercode());
		}
		handleUserMeetingList(request, umeetingList);
		forward(request, response, "meeting_manage.jsp");
	}

	/**
	 * 返回后台首页，查询相关列表
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void meetingWelcome(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		UserModel userModel = (UserModel) session
				.getAttribute(AppConfigure.CURRENT_USER);

		// 已经参加过的会议列表
		List<UserMeetingModel> umHisList = UserMeetingDao.getInstance()
				.getMeetingListByUserCode(userModel.getUsercode(), 9);
		request.setAttribute("umHisList", umHisList);

		// 正在进行的会议列表
		List<UserMeetingModel> umOnlineList = UserMeetingDao.getInstance()
				.getMeetingListByState(10);
		request.setAttribute("umOnlineList", umOnlineList);

		// 被邀请的会议
		List<UserMeetingModel> umInvitedList = UserMeetingDao.getInstance()
				.getMeetingListByUMState(userModel.getUsercode(), 9);
		request.setAttribute("umInvitedList", umInvitedList);

		String url = "welcome.jsp";
		forward(request, response, url);
	}

	/**
	 * 处理用户会议列表
	 * 
	 * @param request
	 * @param umeetingList
	 */
	private void handleUserMeetingList(HttpServletRequest request,
			List<UserMeetingModel> umeetingList) {
		List<Map<String, String>> userMeetingMaps = new ArrayList<Map<String, String>>();
		for (UserMeetingModel usermeeting : umeetingList) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("subject", usermeeting.getMeetingModel().getSubject());
			map.put("agenda", usermeeting.getMeetingModel().getAgenda());
			map.put("begintime", usermeeting.getMeetingModel().getBegintime());
			map.put("duration", usermeeting.getMeetingModel().getDuration()
					+ "分钟");
			map.put("state", MeetingModel.getStateString(usermeeting
					.getMeetingModel().getState()));
			map.put("username", usermeeting.getUserModel().getUsername());
			map.put("usercode", usermeeting.getUserModel().getUsercode());
			map.put("password", usermeeting.getUserModel().getPassword());
			String userrole = usermeeting.getUserModel().getUserrole() == 0 ? "管理员"
					: "普通用户";
			map.put("userrole", userrole);
			map.put("useremail", usermeeting.getUserModel().getUseremail());
			map.put("createtime", usermeeting.getUserModel().getCreatetime());
			map.put("updatetime", usermeeting.getUserModel().getUpdatetime());

			StringBuffer buffer = new StringBuffer();

			buffer
					.append("<a href='#' onclick='meetingView(\"")
					.append(usermeeting.getUserModel().getUsercode())
					.append("\",\"")
					.append(usermeeting.getMeetingModel().getMeetingId())
					.append(
							"\")'><img src='../images/netmeeting/user_edit.gif' border='0' align='absmiddle'/>&nbsp;详细</a>")
					.append("&nbsp;&nbsp;&nbsp;");
			if (usermeeting.getMeetingModel().getState() == AppConfigure.MEETING_NOT_START) {
				buffer
						.append("<a href='#' onclick='meetingEdit(\"")
						.append(usermeeting.getUserModel().getUsercode())
						.append("\",\"")
						.append(usermeeting.getMeetingModel().getMeetingId())
						.append(
								"\")'><img src='../images/netmeeting/user_edit.gif' border='0' align='absmiddle'/>&nbsp;编辑</a>")
						.append("&nbsp;&nbsp;&nbsp;");
				buffer
						.append("<a href='#' onclick='meetingDel(\"")
						.append(usermeeting.getUserModel().getUsercode())
						.append("\",\"")
						.append(usermeeting.getMeetingModel().getMeetingId())
						.append(
								"\")'><img src='../images/netmeeting/user_del.gif' border='0' align='absmiddle'/>&nbsp;取消</a>");
			}

			map.put("operate", buffer.toString());
			userMeetingMaps.add(map);
		}
		request.setAttribute("usermeetinglist", userMeetingMaps);
	}

}
