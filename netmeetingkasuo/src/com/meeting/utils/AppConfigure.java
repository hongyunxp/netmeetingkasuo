package com.meeting.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.PropertyConfigurator;

import com.sun.jna.WString;

public class AppConfigure {

	// 流媒体常量设置
	public static final String RED5 = "red5";
	public static final String RED5_LIB = "red5/lib";
	public static final String RED5_OFLADEMO_STREAMS = "red5/webapps/oflaDemo/streams";

	// 远程桌面程序常量设置
	public static final String ssp_folder = "ssp";
	public static final String ssp_path = "web/ssp";
	public static final String ssp_dll_name = "ssp.dll";
	public static final String ssp_exe_name = "ssp.exe";

	// 远程桌面 代理 常量设置
	public static final int ccvnc_web_port = 9988;
	public static final String ccvnc_lib_path = "web/WEB-INF/lib";
	public static final String ccvnc_password = "web/WEB-INF/ccvnc-passwords";
	public static final String ccvnc_log4j_path = "web/WEB-INF/ccvnc-log4j.properties";

	// 服务器相关路径常量设置
	public static final String log4j_path = "web/WEB-INF/log4j.properties";
	public static final String db_path = "database/";
	public static final String upload_path = "data";
	public static final String jod_path = "jod";
	public static final String imagemagick_path = "imagemagick";
	public static final String temp_path = "temp";

	// 数据库相关设置
	public static final String db_name = "netmeeting";
	public static final String db_port = "9009";

	// apache相关设置
	public static final String apache_path = "apache";
	public static final String apache_docpath = "apache/htdocs";
	public static final String apache_errlogpath = "log/apache_err.log";
	public static final String apache_access_common_log_path = "log/apache_common_access.log";
	public static final String apache_access_combined_log_path = "log/apache_combined_access.log";
	public static final String apache_httpdpath = "apache/bin/httpd.exe";
	public static final String apache_confpath = "apache/conf/httpd.conf";
	public static final String apache_service = "netmeeting_apache2";
	public static final WString APACHE_SERVICE = new WString(apache_service);

	// apache状态常量
	public static final int SERVICE_AUTO_START = 0x00000002;
	public static final int SERVICE_DEMAND_START = 0x00000003;
	public static final int SERVICE_DISABLED = 0x00000004;
	public static final int APACHE_SERVICE_RUNNING = 0x00000004;
	public static final int APACHE_SERVICE_STOPPED = 0x00000001;
	public static final int APACHE_SERVICE_NONE = 0x00000000;

	// 数据库中配置表 中key值
	public static final String KEY_EXPIRED = "EXPIRED";
	public static final String KEY_PORT = "PORT";
	public static final String KEY_BASEPATH = "BASEPATH";
	public static final String KEY_CLEANINTERVAL = "CLEANINTERVAL";
	public static final String KEY_SECURITY = "SECURITY";
	public static final String KEY_ALLOWHANDUP = "ALLOWHANDUP";
	public static final String KEY_ALLOWDESKTOPCONTROL = "ALLOWDESKTOPCONTROL";
	public static final String KEY_ALLOWWHITEBOARD = "ALLOWWHITEBOARD";

	// //////////////////////////////////////////////////////////////////////////////

	// 会议状态
	public static final int MEETING_NOT_START = 0;
	public static final int MEETING_IN_PROGRESS = 1;
	public static final int MEETING_ENDED = 2;

	// 用户角色
	public static final int USER_ROLE_ADMIN = 0;
	public static final int USER_ROLE_COMMON = 1;

	// 会议中用户的角色
	public static final int MEETING_ROLE_ADMIN = 0;
	public static final int MEETING_ROLE_COMMON = 1;

	// 会议中用户的当前状态
	public static final int USER_MEETING_STATE_INITIAL = 0;
	public static final int USER_MEETING_STATE_IN_PROCESS = 1;
	public static final int USER_MEETING_STATE_END = 2;
	public static final int USER_MEETING_STATE_REFRESH = 3;

	// 存在session的key
	public static final String HOST = "host";
	public static final String USERNAME = "username";
	public static final String CURRENT_USER = "usermodel";
	public static final String CURRENT_FILE = "filemodel";
	public static final String HOST_USER = "hostmodel";
	public static final String MEETING = "meeting";
	public static final String CONFIG_LIST = "configs";

	// 常量
	public static final long PING_TIMEUP = 1800;
	public static final long K_BYTE = 1 * 1024;
	public static final long M_BYTE = K_BYTE * 1024;

	// 读写常量
	public static final int READ = 1;
	public static final int WRITER = 2;
	public static final int READWRITER = 3;

	// 扩展名常量
	public static final String PDF_EXT = "pdf";
	public static final String DOC_EXT = "doc";
	public static final String PPT_EXT = "ppt";
	public static final String XLS_EXT = "xls";
	public static final String TXT_EXT = "txt";
	public static final String JPG_EXT = "jpg";
	public static final String PNG_EXT = "png";
	public static final String GIF_EXT = "gif";
	public static final String BMP_EXT = "bmp";

	/**
	 * 配置日志文件
	 */
	public static void configLog4j() {
		try {
			PropertyConfigurator.configure(log4j_path);
			System.out.println("config the log4j.properties successful.");
		} catch (Exception e) {

			throw new RuntimeException("Could not configure log4j.", e);
		}
	}

	/**
	 * 配置配置文件
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Properties configConfig() throws FileNotFoundException,
			IOException {
		Properties properties = new Properties();
		// properties.load(new BufferedReader(new InputStreamReader(
		// new FileInputStream(AppConfigure.config_path))));
		return properties;
	}

	/**
	 * 获取IP地址
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemoteIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 转换字节数为文件大小，并自动转换单位
	 * 
	 * @param size
	 * @return
	 */
	public static String getFileSize(long size) {
		String filesize = null;
		if (size > K_BYTE && size < M_BYTE) {
			filesize = size / K_BYTE + "KB";
		} else if (size > M_BYTE) {
			filesize = size / M_BYTE + "MB";
		} else {
			filesize = size + "B";
		}
		return filesize;
	}

	/**
	 * 获取没有扩展名的文件名
	 * 
	 * @param srcfile
	 * @return
	 */
	public static String getFilenameWithoutExt(String srcfile) {
		srcfile = srcfile.replaceAll("\\\\", "/");
		String pdffile = srcfile.substring(srcfile.lastIndexOf("/") + 1);
		String pdffilename = pdffile.substring(0, pdffile.lastIndexOf("."));
		return pdffilename;
	}

	/**
	 * 获取有扩展名的文件名
	 * 
	 * @param srcfile
	 * @return
	 */
	public static String getFilenameWithExt(String srcfile) {
		srcfile = srcfile.replaceAll("\\\\", "/");
		String pdffile = srcfile.substring(srcfile.lastIndexOf("/") + 1);
		return pdffile;
	}

	/**
	 * 获取有扩展名的文件名
	 * 
	 * @param srcfile
	 * @return
	 */
	public static String getFileExt(String srcfile) {
		srcfile = srcfile.replaceAll("\\\\", "/");
		String ext = srcfile.substring(srcfile.lastIndexOf("."));
		return ext;
	}
}
