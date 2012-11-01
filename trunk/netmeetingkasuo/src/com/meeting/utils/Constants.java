package com.meeting.utils;

public class Constants {

	public static final long K_BYTE = 1 * 1024;

	public static final long M_BYTE = K_BYTE * 1024;

	/**
	 * ת���ֽ���Ϊ�ļ���С�����Զ�ת����λ
	 * 
	 * @param size
	 * @return
	 */
	public static String getFileSize(long size) {
		String filesize = null;
		if (size > Constants.K_BYTE && size < Constants.M_BYTE) {
			filesize = size / Constants.K_BYTE + "KB";
		} else if (size > Constants.M_BYTE) {
			filesize = size / Constants.M_BYTE + "MB";
		} else {
			filesize = size + "B";
		}
		return filesize;
	}

}
