package com.meeting.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.meeting.model.UserModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

@SuppressWarnings("unchecked")
public abstract class BaseServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4098425118747983442L;

	private static Logger logger = Logger.getLogger(BaseServlet.class);

	public HttpSession session = null;

	public static final String SUCCESS = "0";
	public static final String FAILURE = "1";

	/**
	 * ת��
	 * 
	 * @param request
	 * @param response
	 * @param url
	 * @throws ServletException
	 * @throws IOException
	 */
	public void forward(HttpServletRequest request,
			HttpServletResponse response, String url) throws ServletException,
			IOException {
		request.getRequestDispatcher(url).forward(request, response);
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		try {
			handleRequest(request, response);
		} catch (Exception e) {
			logger.error(StackTraceUtil.getStackTrace(e));
			// String emsg =
			// StackTraceUtil.getStackTrace(e).replaceAll("\n","<br/>");
			request.setAttribute("error", "ϵͳ�ڲ��쳣��");
			forward(request, response, "/error.jsp");
		}
	}

	/**
	 * ��������,���䵽����Ĵ�����ĺ���������Ӧ������
	 * 
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		request.setCharacterEncoding("GBK");
		response.setContentType("GBK");
		response.setHeader("Pragma", "No-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		String oper = request.getParameter("oper");
		session = request.getSession();
		Map map = request.getParameterMap();
		Set<String> set = map.keySet();
		Iterator<String> iterator = set.iterator();
		StringBuffer parambuf = new StringBuffer();
		while (iterator.hasNext()) {
			String key = iterator.next();
			parambuf.append("[").append(key).append("=");
			Object object = map.get(key);
			String[] values = null;
			if (object != null) {
				values = (String[]) map.get(key);
				parambuf.append(values[0]);
			}
			parambuf.append("]");
		}
		logger.info(getMsg(request) + "  ����URI:" + request.getRequestURI()
				+ "  �������:" + parambuf.toString() + "");
		if (oper != null) {
			Method method = null;
			method = this.getClass().getDeclaredMethod(oper,
					HttpServletRequest.class, HttpServletResponse.class);
			method.invoke(this, request, response);
		} else {
			throw new Exception("oper����Ϊ�գ�");
		}
	}

	/**
	 * ��ȡ��ǰ�û�
	 * @param request
	 * @return
	 */
	public UserModel getSessionUserModel(HttpServletRequest request) {
		Object object = session.getAttribute(AppConfigure.CURRENT_USER);
		if (object != null) {
			return (UserModel) object;
		}
		return null;
	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @param request
	 * @return
	 */
	public String getMsg(HttpServletRequest request) {
		Object object = request.getSession().getAttribute("username");
		String username = "NULL";
		if (object != null) {
			username = String.valueOf(object);
		}
		String msg = " �����û�:" + username + "  �����ַ:" + request.getRemoteAddr()
				+ " ";
		return msg;
	}

	/**
	 * �����������json����
	 * 
	 * @param response
	 * @param jsonObject
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void writeJSONObject(HttpServletResponse response,
			JSONObject jsonObject) throws ServletException, IOException {
		response.setContentType("type=application/json; charset=GBK");
		PrintWriter pw = response.getWriter();
		pw.write(jsonObject.toString());
		pw.flush();
		pw.close();
	}

	/**
	 * �����������json����
	 * 
	 * @param response
	 * @param jsonObject
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void writeJSONArray(HttpServletResponse response,
			JSONArray jsonArray) throws ServletException, IOException {
		response.setContentType("type=application/json; charset=GBK");
		PrintWriter pw = response.getWriter();
		pw.write(jsonArray.toString());
		pw.flush();
		pw.close();
	}

}
