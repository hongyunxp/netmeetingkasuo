package com.meeting.service;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.meeting.utils.AppConfigure;

public class DrawText2ImageService {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger
			.getLogger(DrawText2ImageService.class);

	private static DrawText2ImageService instance;

	public static final String KEY_PROCESSDRAW_TEXT_2_IMAGE = "DRAW_TEXT_2_IMAGE";

	private DrawText2ImageService() {
	}

	public static synchronized DrawText2ImageService getInstance() {
		if (instance == null) {
			instance = new DrawText2ImageService();
		}
		return instance;
	}

	/**
	 * »ñÈ¡Í¼Æ¬×ª»»ÃüÁî
	 * 
	 * @return
	 */
	static String getPathToImageMagic() {
		return AppConfigure.imagemagick_path + "/convert";
	}

	/**
	 * ×ª»»Í¼Æ¬
	 * 
	 * @param srcfile
	 * @param destfolder
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> drawText2Image(String points,
			String srcfile, String destfile) throws Exception {
		destfile = destfile.replaceAll("\\\\", "/");
		String textStr = new String("×Þ´º¸Õ".getBytes("UTF-8"), "GBK");
		String param = "text 100,100 '" + textStr + "'";
		String[] argv2 = new String[] {
				DrawText2ImageService.getPathToImageMagic(), "-font",
				"C:/upload/gkai00mp.ttf", "-draw", param, srcfile, destfile };
		return ExecuteService
				.executeScript(KEY_PROCESSDRAW_TEXT_2_IMAGE, argv2);
	}

	public byte[] gbk2utf8(String chenese) {
		char c[] = chenese.toCharArray();
		byte[] fullByte = new byte[3 * c.length];
		for (int i = 0; i < c.length; i++) {
			int m = (int) c[i];
			String word = Integer.toBinaryString(m);
			// System.out.println(word);

			StringBuffer sb = new StringBuffer();
			int len = 16 - word.length();
			// ²¹Áã
			for (int j = 0; j < len; j++) {
				sb.append("0");
			}
			sb.append(word);
			sb.insert(0, "1110");
			sb.insert(8, "10");
			sb.insert(16, "10");

			// System.out.println(sb.toString());

			String s1 = sb.substring(0, 8);
			String s2 = sb.substring(8, 16);
			String s3 = sb.substring(16);

			byte b0 = Integer.valueOf(s1, 2).byteValue();
			byte b1 = Integer.valueOf(s2, 2).byteValue();
			byte b2 = Integer.valueOf(s3, 2).byteValue();
			byte[] bf = new byte[3];
			bf[0] = b0;
			fullByte[i * 3] = bf[0];
			bf[1] = b1;
			fullByte[i * 3 + 1] = bf[1];
			bf[2] = b2;
			fullByte[i * 3 + 2] = bf[2];

		}
		return fullByte;
	}

	public static String getEncoding(String str) {
		String encode = "GB2312 ";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s = encode;
				return s;
			}
		} catch (Exception exception) {
		}
		encode = "ISO-8859-1 ";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s1 = encode;
				return s1;
			}
		} catch (Exception exception1) {
		}
		encode = "UTF-8 ";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s2 = encode;
				return s2;
			}
		} catch (Exception exception2) {
		}
		encode = "GBK ";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s3 = encode;
				return s3;
			}
		} catch (Exception exception3) {
		}
		encode = "BIG5 ";
		try {
			if (str.equals(new String(str.getBytes(encode), encode))) {
				String s4 = encode;
				return s4;
			}
		} catch (Exception exception3) {
		}
		return " ";
	}

}
