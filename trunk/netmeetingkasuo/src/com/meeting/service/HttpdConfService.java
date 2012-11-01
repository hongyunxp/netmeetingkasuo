package com.meeting.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.meeting.dao.ConfigDao;
import com.meeting.model.ConfigModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

/**
 * Apache httpd���÷��� �ļ���httpd.conf
 * 
 * @author zcg
 * 
 */
public class HttpdConfService {

	private static Logger logger = Logger.getLogger(HttpdConfService.class);

	public static String ServerRoot = "ServerRoot";
	public static String DocumentRoot = "DocumentRoot";
	public static String Directory = "<Directory";
	public static String Directory_EX = "\"cgi-bin\"";
	public static String CustomLog = "CustomLog";
	public static String access_Common = "common";
	public static String access_Combined = "combined";
	public static String ErrorLog = "ErrorLog";
	public static String SVNParentPath = "SVNParentPath";
	public static String AuthzSVNAccessFile = "AuthzSVNAccessFile";
	public static String AuthUserFile = "AuthUserFile";

	private static String bPath = null;

	/**
	 * ��ȡ��·��
	 * 
	 * @return
	 */
	public static String getBasePath() {
		if (bPath == null) {
			ConfigModel model = ConfigDao.getInstance().getConfig(
					AppConfigure.KEY_BASEPATH);
			if (model != null)
				bPath = model.getValue();
			else {
				bPath = null;
			}
		}
		return bPath;
	}

	/**
	 * ��ʼ��
	 */
	public static void init() {
		logger.info("��ʼ��httpd.conf�����ļ�");
		String serverRootValue = "\"" + getBasePath() + "/"
				+ AppConfigure.apache_path + "\"";
		String DocumentRootValue = "\"" + getBasePath() + "/"
				+ AppConfigure.apache_docpath + "\"";
		String ErrorLogValue = "\"" + getBasePath() + "/"
				+ AppConfigure.apache_errlogpath + "\"";
		String[] keys = { ServerRoot, DocumentRoot, ErrorLog };

		String[] values = { serverRootValue, DocumentRootValue, ErrorLogValue };

		setValue(keys, values);

		initAccessLog();

		initDirectory();

	}

	/**
	 * ����ServerRoot
	 * 
	 * @param value
	 */
	public static void initServerPort(String value) {
		setValue(ServerRoot, value);
	}

	/**
	 * ����DocumentRoot
	 * 
	 * @param value
	 */
	public static void initDocumentRoot(String value) {
		setValue(DocumentRoot, value);
	}

	/**
	 * ����ErrorLog
	 * 
	 * @param value
	 */
	public static void initErrorLog(String value) {
		setValue(ErrorLog, value);
	}

	/**
	 * ����SVNParentPath
	 * 
	 * @param value
	 */
	public static void initSVNParentPath(String value) {
		setValue(SVNParentPath, value);
	}

	/**
	 * ����AuthzSVNAccessFile
	 * 
	 * @param value
	 */
	public static void initAuthzSVNAccessFile(String value) {
		setValue(AuthzSVNAccessFile, value);
	}

	/**
	 * ����AuthUserFile
	 * 
	 * @param value
	 */
	public static void initAuthUserFile(String value) {
		setValue(AuthUserFile, value);
	}

	/**
	 * ����Common access
	 * 
	 * @param value
	 */
	public static void initAccessLog() {
		StringBuffer buffer = new StringBuffer();
		List<String> lineList = getLineList();
		for (String line : lineList) {
			String temp = line.trim();
			// logger.debug("�����У� " + line);
			if (temp.startsWith(CustomLog)) {
				String[] customLogs = temp.split(" ");
				String AccessCommonLog = "\"" + getBasePath() + "/"
						+ AppConfigure.apache_access_common_log_path + "\"";
				String AccessCombinedLog = "\"" + getBasePath() + "/"
						+ AppConfigure.apache_access_combined_log_path + "\"";
				if (customLogs[2].equals(access_Common)) {
					if (!getMidValue(CustomLog, access_Common).equals(
							AccessCommonLog)) {
						logger.debug("����key��" + CustomLog + "��value��"
								+ AccessCommonLog);
						String val = customLogs[0] + " " + AccessCommonLog
								+ " " + customLogs[2];
						buffer.append(val).append("\n");
					} else {
						buffer.append(line).append("\n");
					}
				} else if (customLogs[2].equals(access_Combined)) {
					if (!getMidValue(CustomLog, access_Combined).equals(
							AccessCombinedLog)) {
						logger.debug("����key��" + CustomLog + "��value��"
								+ AccessCombinedLog);
						String val = customLogs[0] + " " + AccessCombinedLog
								+ " " + customLogs[2];
						buffer.append(val).append("\n");
					} else {
						buffer.append(line).append("\n");
					}
				} else {
					buffer.append(line).append("\n");
				}
			} else {
				buffer.append(line).append("\n");
			}
		}
		String confpath = getBasePath() + "/" + AppConfigure.apache_confpath;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					confpath)));
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("д�ļ�" + AppConfigure.apache_confpath + "IO�쳣�� "
					+ StackTraceUtil.getStackTrace(e));
		}
	}

	/**
	 * ����Directory
	 * 
	 * @param value
	 */
	public static void initDirectory() {
		String value = "\"" + getBasePath() + "/" + AppConfigure.apache_path
				+ "\"";

		StringBuffer buffer = new StringBuffer();
		List<String> lineList = getLineList();
		for (String line : lineList) {
			String temp = line.trim();
			// logger.debug("�����У� " + line);
			if (temp.indexOf(" ") != -1) {
				temp = temp.substring(0, temp.length() - 1);
				String content = temp.substring(temp.indexOf(" ") + 1);
				if (temp.startsWith(Directory) && (!content.equals("/"))
						&& (!content.equals(Directory_EX))) {
					// logger.debug("��ȡkey��" + Directory + "��value��" + content);
					if (!content.equals(value)) {
						logger.debug("����key��" + Directory + "��value��" + value);
						String dirval = Directory + " " + value + ">";
						buffer.append(dirval).append("\n");
					} else {
						buffer.append(line).append("\n");
					}
				} else {
					buffer.append(line).append("\n");
				}
			} else {
				buffer.append(line).append("\n");
			}
		}
		String confpath = getBasePath() + "/" + AppConfigure.apache_confpath;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					confpath)));
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("д�ļ�" + AppConfigure.apache_confpath + "IO�쳣�� "
					+ StackTraceUtil.getStackTrace(e));
		}
	}

	/**
	 * ��ȡֵ
	 * 
	 * @param customLog2
	 * @return
	 */
	public static Object getMidValue(String key, String last) {
		List<String> lineList = getLineList();
		for (String line : lineList) {
			line = line.trim();
			// logger.debug("�����У� " + line);
			if (line.startsWith(key) && line.endsWith(last)) {
				logger.debug("��ȡkey��" + key + "��value��" + line.split(" ")[1]);
				return line.split(" ")[1];
			}
		}
		return "";
	}

	/**
	 * ��ȡkey��Ӧ��ֵ
	 * 
	 * @param key
	 */
	public static String getConfig(String key) {
		List<String> lineList = getLineList();
		for (String line : lineList) {
			line = line.trim();
			// logger.debug("�����У� " + line);
			if (line.startsWith(key)) {
				String value = line.substring(key.length()).trim();
				// logger.debug("��ȡkey��" + key + "��value��" + value);
				return value;
			}
		}
		return "";
	}

	/**
	 * ����key��ֵ
	 * 
	 * @param key
	 * @param value
	 */
	public static void setValue(String[] keys, String[] values) {
		StringBuffer buffer = new StringBuffer();
		List<String> lineList = getLineList();
		for (String line : lineList) {
			String temp = line.trim();
			// logger.debug("�����У� " + line);
			boolean flag = false;
			for (int i = 0; i < keys.length; i++) {
				if (temp.startsWith(keys[i])) {
					if (!getConfig(keys[i]).equals(values[i])) {
						logger
								.debug("����key��" + keys[i] + "��value��"
										+ values[i]);
						String tempKey = line.split(keys[i])[0];
						String val = tempKey + keys[i] + " " + values[i];
						flag = true;
						buffer.append(val).append("\n");
					}
				}
			}
			if (!flag) {
				buffer.append(line).append("\n");
			}
		}
		String confpath = getBasePath() + "/" + AppConfigure.apache_confpath;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					confpath)));
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("д�ļ�" + AppConfigure.apache_confpath + "IO�쳣�� "
					+ StackTraceUtil.getStackTrace(e));
		}
	}

	/**
	 * ����key��ֵ
	 * 
	 * @param key
	 * @param value
	 */
	public static void setValue(String key, String value) {
		List<String> lineList = getLineList();
		StringBuffer buffer = new StringBuffer();
		for (String line : lineList) {
			String temp = line.trim();
			// logger.debug("�����У� " + line);
			if (temp.startsWith(key)) {
				if (!getConfig(key).equals(value)) {
					String val = line.substring(0, line.lastIndexOf(" ")) + " "
							+ value;
					logger.debug("����key��" + key + "��value��" + value);
					buffer.append(val).append("\n");
				}
			} else {
				buffer.append(line).append("\n");
			}
		}
		String confpath = getBasePath() + "/" + AppConfigure.apache_confpath;
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(
					confpath)));
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("д�ļ�" + AppConfigure.apache_confpath + "IO�쳣�� "
					+ StackTraceUtil.getStackTrace(e));
		}
	}

	/**
	 * ��ȡ�ļ���������
	 * 
	 * @return
	 */
	public static List<String> getLineList() {
		List<String> lineList = new ArrayList<String>();
		String confpath = getBasePath() + "/" + AppConfigure.apache_confpath;
		try {
			String line = "";
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					confpath)));
			while ((line = reader.readLine()) != null) {
				// logger.debug("all: " + line);
				if (!line.trim().startsWith("#") && !line.trim().equals("")) {
					lineList.add(line);
				}
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("���ļ�" + AppConfigure.apache_confpath + "û���ҵ��쳣��"
					+ StackTraceUtil.getStackTrace(e));
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("���ļ�" + AppConfigure.apache_confpath + "IO�쳣�� "
					+ StackTraceUtil.getStackTrace(e));
		}
		return lineList;
	}

}
