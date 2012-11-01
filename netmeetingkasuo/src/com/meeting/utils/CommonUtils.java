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
 * @Description: ͨ�ù�����
 * @author zcg
 * @date 2010-7-8 ����04:39:22
 * 
 */
public class CommonUtils {
	/**
	 * ͨ��java API���һ��UUID
	 * 
	 * @return String UUID
	 */
	public static String getUUID() {
		String s = UUID.randomUUID().toString();
		return s;
	}

	/**
	 * ����ĸ������ϵ��У���ȡһ�������
	 * 
	 * @return �������
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
	 * �����漴����/
	 * 
	 * @param pwd_len
	 *            ���ɵ�������ܳ���
	 * @return ������ַ���
	 */
	public static String genRandomNum(int pwd_len) {
		// 35����Ϊ�����Ǵ�0��ʼ�ģ�26����ĸ+10������
		final int maxNum = 36;
		int i; // ���ɵ������;
		int count = 0; // ���ɵ�����ĳ���?
		char[] str = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
				'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
				'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		StringBuffer pwd = new StringBuffer("");
		Random r = new Random();
		while (count < pwd_len) {
			// �����������ȡ����ֵ����ֹ���ɸ�����
			i = Math.abs(r.nextInt(maxNum)); // ���ɵ������Ϊ36-1
			if (i >= 0 && i < str.length) {
				pwd.append(str[i]);
				count++;
			}
		}
		return pwd.toString();
	}

	/**
	 * ת���ֽ���Ϊ�ļ���С�����Զ�ת����λ
	 * 
	 * @param size
	 * @return �����ļ��Ĵ�С������ 1.22M,2.50K
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
	 * �ж��ַ����ǲ�������
	 * 
	 * @param str
	 * @return true �����֣�false��������
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * ��ȡIP��ַ
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
	 * �����ļ�·����ȡ�ļ���С
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
	 * ��ȡû����չ�����ļ���
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
	 * ��ȡ����չ�����ļ���
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
	 * ��ȡ����չ�����ļ���
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
	 * ��ȡ�ļ����ļ���·��
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
	 * ɾ���ļ�����Ŀ¼
	 * 
	 * @param path
	 */
	public static void deleteFolder(String path) {
		File file1 = new File(path);
		// �ļ�·�����ļ���
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
	 * ɾ����һ���ļ���
	 * 
	 * @param path
	 */
	public static void deleteParentFolder(String path) {
		String folderPath = getFileFolder(path);
		deleteFolder(folderPath);
	}

	/**
	 * ��������
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
	 * ��ȡ�����ɫ
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
