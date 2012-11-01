package com.ccvnc.test;

import junit.framework.TestCase;

import com.ccvnc.PacketManager;
import com.ccvnc.RfbConstants;
import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;
import com.ccvnc.packets.server.rect.RawRect;

/**
 * ScreenTest
 * 
 * @author Volodymyr M. Lisivka
 */
public class ScreenTest extends TestCase implements RfbConstants {
	public ScreenTest(String method) {
		super(method);
	}

	public void testPixel16to24() {
		Screen screen24LTRGB = getScreen24LTRGB();
		Screen screen16LTRGB = getScreen16LTRGB();
		RawRect rect = new RawRect();
		rect.setScreen(screen16LTRGB);
		assertEquals("Incorrect converions of pixel to same depth", RfbIOTools
				.arrayToString(new byte[] { 0x00, (byte) 0xff }), RfbIOTools
				.arrayToString(rect.convertRawPixelData(new byte[] { 0x00,
						(byte) 0xff }, screen16LTRGB)));
		assertEquals("Incorrect converions of pixel to same depth", RfbIOTools
				.arrayToString(new byte[] { (byte) 0xf0, (byte) 0x0f }),
				RfbIOTools.arrayToString(rect.convertRawPixelData(new byte[] {
						(byte) 0xf0, 0x0f }, screen16LTRGB)));

		assertEquals("Incorrect converions of pixel to higher depth",
				RfbIOTools.arrayToString(new byte[] { (byte) 0xf8, (byte) 0xfc,
						(byte) 0xf8 }), RfbIOTools.arrayToString(rect
						.convertRawPixelData(new byte[] { (byte) 0xff,
								(byte) 0xff }, screen24LTRGB)));
	}

	public void testPixel24to16() {
		Screen screen24LTRGB = getScreen24LTRGB();
		Screen screen16LTRGB = getScreen16LTRGB();
		RawRect rect = new RawRect();
		rect.setScreen(screen24LTRGB);
		assertEquals("Incorrect converions of pixel to same depth", RfbIOTools
				.arrayToString(new byte[] { 0x00, 0x00, (byte) 0xff }),
				RfbIOTools.arrayToString(rect.convertRawPixelData(new byte[] {
						0x00, 0x00, (byte) 0xff }, screen24LTRGB)));
		assertEquals("Incorrect converions of pixel to same depth", RfbIOTools
				.arrayToString(new byte[] { (byte) 0xf0, 0x0f, (byte) 0xf0 }),
				RfbIOTools.arrayToString(rect.convertRawPixelData(new byte[] {
						(byte) 0xf0, 0x0f, (byte) 0xf0 }, screen24LTRGB)));

		assertEquals("Incorrect converions of pixel to lower depth", RfbIOTools
				.arrayToString(new byte[] { (byte) 0xff, (byte) 0xff }),
				RfbIOTools
						.arrayToString(rect.convertRawPixelData(new byte[] {
								(byte) 0xff, (byte) 0xff, (byte) 0xff },
								screen16LTRGB)));
	}

	public void testMaxToBits() {
		assertEquals("Broken conversion of max value to number of bits", 1,
				Screen.maxToBits(1));
		assertEquals("Broken conversion of max value to number of bits", 2,
				Screen.maxToBits(2));
		assertEquals("Broken conversion of max value to number of bits", 5,
				Screen.maxToBits(31));
		assertEquals("Broken conversion of max value to number of bits", 6,
				Screen.maxToBits(32));
	}

	public void testRawRectDepthConversion24to16() throws Throwable {
		byte[] buf1 = new byte[] { -1, -1, -1, 0, 0, -1,

		0, 0, 0, 0, 0, 0, };
		byte[] buf2 = new byte[] { -1, -1, 0, (byte) 0xf8,

		0, 0, 0, 0, };

		checkConvertedRectangle(convert(buf1, getScreen24LTRGB(),
				getScreen16LTRGB()), buf2);
	}

	public void testRawRectDepthConversion16to24() throws Throwable {
		byte[] buf1 = new byte[] { -1, -1, 0, (byte) 0xf8,

		0, 0, 0, 0, };

		byte[] buf2 = new byte[] { (byte) 0xf8, (byte) 0xfc, (byte) 0xf8, 0, 0,
				(byte) 0xf8,

				0, 0, 0, 0, 0, 0, };
		checkConvertedRectangle(convert(buf1, getScreen16LTRGB(),
				getScreen24LTRGB()), buf2);
	}

	private void checkConvertedRectangle(RawRect rect, byte[] buf2) {
		assertNotNull("Rectangle not converted", rect);
		assertEquals("Rectangle converted incorrectly", buf2.length, rect
				.getBuf().length);
		String s1 = arrayToString(buf2);
		String s2 = arrayToString(rect.getBuf());
		assertEquals("Rectangle converted incorrectly", s1, s2);
	}

	private String arrayToString(byte[] buf2) {
		StringBuffer sb = new StringBuffer(buf2.length * 3);
		for (int i = 0; i < buf2.length; i++) {
			if ((buf2[i] & 0xff) < 0x10)
				sb.append('0');

			sb.append(Integer.toHexString((buf2[i] & 0xff)));
			sb.append(' ');
		}
		return sb.toString();
	}

	private RawRect convert(byte[] buf1, Screen screen1, Screen screen2) {
		RawRect rect = new RawRect();
		rect.setScreen(screen1);
		rect.setHeaderParameters(ENCODING_RAW, 0, 0, 2, 2);
		rect.setBuf(buf1);

		RawRect rect2 = (RawRect) PacketManager.convertRectangle(rect, screen2);
		return rect2;
	}

	private Screen getScreen24LTRGB() {
		Screen screen = new Screen(7);
		screen.setFramebufferSize(640, 480);
		screen.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW });
		screen.setPixelFormat(24, 24, 0, 1, 255, 255, 255, 16, 8, 0);
		return screen;
	}

	private Screen getScreen16LTRGB() {
		Screen screen = new Screen(7);
		screen.setFramebufferSize(640, 480);
		screen.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW });
		screen.setPixelFormat(16, 16, 0, 1, 31, 63, 31, 11, 5, 0);
		return screen;
	}
}
