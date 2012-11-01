package com.meeting.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @Description: 通用工具类
 * @author zcg
 * @date 2010-7-8 下午04:39:22
 * 
 */
public class CommonUtils {
	/**
	 * 通过java API获得一个UUID
	 * 
	 * @return String UUID
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return s;
	}

	/**
	 * 从字母数字组合当中，获取一个随机数
	 * 
	 * @return 随机数串
	 */
	public static String getRandomId() {
		String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		StringBuffer sb = new StringBuffer();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			sb.append(allChar.charAt(random.nextInt(allChar.length())));
		}
		return sb.toString();
	}

	/**
	 * 生成随即密码/
	 * 
	 * @param pwd_len
	 *            生成的密码的总长度
	 * @return 密码的字符串
	 */
	public static String genRandomNum(int pwd_len) {
		// 35是因为数组是从0开始的，26个字母+10个数字
		final int maxNum = 36;
		int i; // 生成的随机数;
		int count = 0; // 生成的密码的长度?
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < pwd_len) {
			// 生成随机数，取绝对值，防止生成负数，
			i = Math.abs(r.nextInt(maxNum)); // 生成的数最大为36-1
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}

	/**
	 * 转换字节数为文件大小，并自动转换单位
	 * 
	 * @param size
	 * @return 返回文件的大小，例如 1.22M,2.50K
	 */
	public static String getFileSize(long size) {
		String filesize = null;
		DecimalFormat df = new DecimalFormat("########.00");
		if (size > Constants.K_BYTE && size < Constants.M_BYTE) {
			filesize = Double.parseDouble(df.format((double) size
					/ Constants.K_BYTE))
					+ "K";
		} else if (size > Constants.M_BYTE) {
			filesize = Double.parseDouble(df.format((double) size
					/ Constants.M_BYTE))
					+ "M";
		} else {
			filesize = Double.parseDouble(df.format((double) size
					/ Constants.K_BYTE))
					+ "K";
		}
		return filesize;
	}

	/**
	 * 判断字符串是不是数字
	 * 
	 * @param str
	 * @return true 是数字，false不是数字
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
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
	 * 根据文件路径获取文件大小
	 * 
	 * @param path
	 * @return
	 */
	public static String getFileSizeByPath(String path) {
		long size = 0;
		try {
			FileInputStream inputStream = new FileInputStream(path);
			byte buffer[] = new byte[1024];
			while (inputStream.read(buffer) != -1) {
				size++;
			}
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return getFileSize(size);
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

	/**
	 * 获取文件的文件夹路径
	 * 
	 * @param srcfile
	 * @return
	 */
	public static String getFileFolder(String srcfile) {
		srcfile = srcfile.replaceAll("\\\\", "/");
		String ext = srcfile.substring(0, srcfile.lastIndexOf("/"));
		return ext;
	}

	/**
	 * 删除文件所在目录
	 * 
	 * @param path
	 */
	public static void deleteFolder(String path) {
		File file1 = new File(path);
		// 文件路径是文件夹
		if (file1.isDirectory()) {
			if (file1.list().length == 0) {
				file1.delete();
			} else {
				File[] files = file1.listFiles();
				for (File temp : files) {
					deleteFolder(temp.getAbsolutePath());
				}
				file1.delete();
			}
		} else {
			file1.delete();
		}
	}

	/**
	 * 删除上一级文件夹
	 * 
	 * @param path
	 */
	public static void deleteParentFolder(String path) {
		String folderPath = getFileFolder(path);
		deleteFolder(folderPath);
	}

	/**
	 * 返回整形
	 * 
	 * @param str
	 * @return
	 */
	public static int toInt(String str) {
		int ret = 0;
		try {
			ret = Integer.parseInt(str);
		} catch (Exception e) {
			ret = 0;
		}
		return ret;
	}

	/**
	 * 获取随机颜色
	 */
	public static String randomColor() {
		String str = "0123456789abcdef";
		String color = "#";
		for (int i = 0; i < 6; i++) {
			color = color + str.charAt(Math.abs(new Random().nextInt()) % 16);
		}
		return color;
	}

	public static void getLocalAddress() {
		Enumeration<NetworkInterface> netInterfaces = null;
		try {
			netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> ips = ni.getInetAddresses();
				while (ips.hasMoreElements()) {
					String ip = ips.nextElement().getHostAddress();
					if (!ip.equals("localhost") && !ip.equals("127.0.0.1")) {
						System.out.println("IP:" + ip);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		getLocalAddress();
	}

}
