package com.ccvnc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * RfbIOTools
 * 
 * @author Volodymyr M. Lisivka
 */
public class RfbIOTools {
	/**
	 * Read an integer in compact representation (1..3 bytes).
	 */
	public static int readCompactLength(DataInputStream is) throws IOException {
		int res = is.readUnsignedByte();
		if ((res & (1 << 7)) != 0) {
			res ^= (1 << 7);
			res = res | (is.readUnsignedByte() << 7);
			if ((res & (1 << 14)) != 0) {
				res ^= (1 << 14);
				res = res | (is.readUnsignedByte() << 14);
			}
		}

		return res;
	}

	/**
	 * Write an integer in compact representation (1..3 bytes).
	 * 
	 * @param os
	 * @param length
	 * @throws IOException
	 */
	public static void writeCompactLength(DataOutputStream os, int length)
			throws IOException {
		byte[] buf = new byte[3];
		int count = 1;
		buf[0] = (byte) (length & 0x7f);
		if (buf[0] < length) {
			count++;
			buf[0] |= 0x80;
			length >>>= 7;
			buf[1] = (byte) (length & 0x7f);
			if (buf[1] < length) {
				count++;
				buf[1] |= 0x80;
				length >>>= 7;
				buf[2] = (byte) length;
			}
		}
		os.write(buf, 0, count);
	}

	/**
	 * Convert array of bytes to string with hexedecimal dump.
	 * 
	 * @param buf
	 * @return
	 */
	public static String arrayToString(byte[] buf) {
		StringBuffer sb = new StringBuffer(buf.length * 3 + (buf.length + 15)
				/ 16);
		int i = 0;
		for (; i < buf.length; i++) {
			if ((buf[i] & 0xff) < 0x10)
				sb.append('0');

			sb.append(Integer.toHexString((buf[i] & 0xff)));
			sb.append(' ');
			if ((i + 1) % 16 == 0)
				sb.append('\n');
		}
		if ((i + 1) % 16 != 0)
			sb.append('\n');
		return sb.toString();
	}

}
