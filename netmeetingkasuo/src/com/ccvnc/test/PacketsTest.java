package com.ccvnc.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import junit.framework.TestCase;

import com.ccvnc.PacketManager;
import com.ccvnc.RfbConstants;
import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;
import com.ccvnc.packets.Packet;
import com.ccvnc.packets.client.SetEncodingsPacket;
import com.ccvnc.packets.client.SetPixelFormatPacket;
import com.ccvnc.packets.server.FramebufferUpdatePacket;
import com.ccvnc.packets.server.rect.CoRRERect;
import com.ccvnc.packets.server.rect.CoRRESubrect;
import com.ccvnc.packets.server.rect.HextileRect;
import com.ccvnc.packets.server.rect.HextileSubrect;
import com.ccvnc.packets.server.rect.HextileTile;
import com.ccvnc.packets.server.rect.RRERect;
import com.ccvnc.packets.server.rect.RRESubrect;
import com.ccvnc.packets.server.rect.RawRect;
import com.ccvnc.packets.server.rect.TightRect;

/**
 * PacketTest
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class PacketsTest extends TestCase {
	public PacketsTest(String method) {
		super(method);
	}

	public void testSetPixelFormatPacket() throws Throwable {
		SetPixelFormatPacket pf = new SetPixelFormatPacket(16, 16, 0, 1, 31,
				63, 31, 11, 5, 0);
		pf.setScreen(new Screen(7));

		writeAndReadPacket(pf);
		pf.validate();
		assertEquals(16, pf.getBitsPerPixel());
		assertEquals(16, pf.getDepth());
		assertEquals(0, pf.getBigEndianFlag());
		assertEquals(1, pf.getTrueColourFlag());
		assertEquals(31, pf.getRedMax());
		assertEquals(63, pf.getGreenMax());
		assertEquals(31, pf.getBlueMax());
		assertEquals(11, pf.getRedShift());
		assertEquals(5, pf.getGreenShift());
		assertEquals(0, pf.getBlueShift());

	}

	private void writeAndReadPacket(Packet packet) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(bos);
		packet.write(os);
		os.flush();
		os.close();

		ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		DataInputStream is = new DataInputStream(bis);
		assertEquals(packet.getPacketType(), is.readByte());
		packet.readPacketData(is);
	}

	public void testSetEncodingPacket() throws Throwable {
		SetEncodingsPacket enc = new SetEncodingsPacket(new int[] {
				RfbConstants.ENCODING_RAW, RfbConstants.ENCODING_HEXTILE,
				RfbConstants.ENCODING_RICH_CURSOR });
		enc.setScreen(new Screen(7));

		writeAndReadPacket(enc);
		enc.validate();
		assertEquals(RfbConstants.ENCODING_RAW, enc.getEncodings()[0]);
		assertEquals(RfbConstants.ENCODING_HEXTILE, enc.getEncodings()[1]);
		assertEquals(RfbConstants.ENCODING_RICH_CURSOR, enc.getEncodings()[2]);
	}

	public void testFramebufferUpdatePacketWithRawEncoding() throws Throwable {
		Screen screen = getScreen16LTRGB();
		FramebufferUpdatePacket packet = new FramebufferUpdatePacket(screen);
		RawRect rect = new RawRect();
		rect.setScreen(screen);
		rect.setHeaderParameters(RfbConstants.ENCODING_RAW, 0, 0, 2, 2);
		rect.setBuf(new byte[] { 0, 0, (byte) 0x2f, (byte) 0x3f, 0,
				(byte) 0x10, (byte) 0x20, 0 });
		rect.validate();
		packet.addRect(rect);
		packet.addRect(rect);

		FramebufferUpdatePacket packet2 = convertTo24AndThenTo16(packet);

		// Compare old and new packets
		assertEquals(packet.getPacketType(), packet2.getPacketType());
		assertEquals(packet.getNumberOfRectangles(), packet2
				.getNumberOfRectangles());
		RawRect rect2 = (RawRect) packet2.getRectangle(0);
		assertEquals(rect.getX(), rect2.getX());
		assertEquals(rect.getY(), rect2.getY());
		assertEquals(rect.getWidth(), rect2.getWidth());
		assertEquals(rect.getHeight(), rect2.getHeight());
		assertEquals(RfbIOTools.arrayToString(rect.getBuf()), RfbIOTools
				.arrayToString(rect2.getBuf()));

	}

	public void testFramebufferUpdatePacketWithRREEncoding() throws Throwable {
		Screen screen = getScreen16LTRGB();
		FramebufferUpdatePacket packet = new FramebufferUpdatePacket(screen);

		{
			RRERect rect = new RRERect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_RRE, 0, 0, 2, 2);
			rect.setBackgroundColorBuf(new byte[] { 0, 0 });
			rect.setRectangles(new Vector(0));
			rect.validate();
			packet.addRect(rect);
		}

		{
			RRERect rect = new RRERect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_RRE, 0, 0, 2, 2);
			rect.setBackgroundColorBuf(new byte[] { 0, 0 });
			Vector rectangles = new Vector(2);
			{
				RRESubrect subrect = new RRESubrect();
				subrect.setScreen(screen);
				subrect.setHeaderParameters(RfbConstants.ENCODING_RRE, 0, 0, 2,
						2);
				subrect.setPixelBuf(new byte[] { (byte) 0xff, 0 });
				subrect.validate();
				rectangles.add(subrect);
			}
			{
				RRESubrect subrect = new RRESubrect();
				subrect.setScreen(screen);
				subrect.setHeaderParameters(RfbConstants.ENCODING_RRE, 1, 1, 1,
						1);
				subrect.setPixelBuf(new byte[] { 0, (byte) 0xff });
				subrect.validate();
				rectangles.add(subrect);
			}
			rect.setRectangles(rectangles);
			rect.validate();
			packet.addRect(rect);
		}

		FramebufferUpdatePacket packet2 = convertTo24AndThenTo16(packet);

		// Compare old and new packets
		assertEquals(packet.getPacketType(), packet2.getPacketType());
		assertEquals(packet.getNumberOfRectangles(), packet2
				.getNumberOfRectangles());
		for (int i = 0; i < packet.getNumberOfRectangles(); i++) {
			RRERect rect1 = (RRERect) packet.getRectangle(i);
			RRERect rect2 = (RRERect) packet2.getRectangle(i);
			assertEquals(rect1.getX(), rect2.getX());
			assertEquals(rect1.getY(), rect2.getY());
			assertEquals(rect1.getWidth(), rect2.getWidth());
			assertEquals(rect1.getHeight(), rect2.getHeight());
			assertEquals(rect1.getNumberOfSubrects(), rect2
					.getNumberOfSubrects());

			for (int j = 0; j < rect1.getNumberOfSubrects(); j++) {
				RRESubrect subrect1 = (RRESubrect) rect1.getSubrectangle(i);
				RRESubrect subrect2 = (RRESubrect) rect2.getSubrectangle(i);

				assertEquals(subrect1.getX(), subrect2.getX());
				assertEquals(subrect1.getY(), subrect2.getY());
				assertEquals(subrect1.getWidth(), subrect2.getWidth());
				assertEquals(subrect1.getHeight(), subrect2.getHeight());
				assertEquals(RfbIOTools.arrayToString(subrect1.getPixelBuf()),
						RfbIOTools.arrayToString(subrect2.getPixelBuf()));
			}
		}

	}

	public void testFramebufferUpdatePacketWithCoRREEncoding() throws Throwable {
		Screen screen = getScreen16LTRGB();
		FramebufferUpdatePacket packet = new FramebufferUpdatePacket(screen);

		{
			CoRRERect rect = new CoRRERect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_CO_RRE, 0, 0, 2, 2);
			rect.setBackgroundColorBuf(new byte[] { 0, 0 });
			rect.setRectangles(new Vector(0));
			rect.validate();
			packet.addRect(rect);
		}

		{
			CoRRERect rect = new CoRRERect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_CO_RRE, 0, 0, 2, 2);
			rect.setBackgroundColorBuf(new byte[] { 0, 0 });
			Vector rectangles = new Vector(2);
			{
				CoRRESubrect subrect = new CoRRESubrect();
				subrect.setScreen(screen);
				subrect.setHeaderParameters(RfbConstants.ENCODING_CO_RRE, 0, 0,
						2, 2);
				subrect.setPixelBuf(new byte[] { (byte) 0xff, 0 });
				subrect.validate();
				rectangles.add(subrect);
			}
			{
				CoRRESubrect subrect = new CoRRESubrect();
				subrect.setScreen(screen);
				subrect.setHeaderParameters(RfbConstants.ENCODING_CO_RRE, 1, 1,
						1, 1);
				subrect.setPixelBuf(new byte[] { 0, (byte) 0xff });
				subrect.validate();
				rectangles.add(subrect);
			}
			rect.setRectangles(rectangles);
			rect.validate();
			packet.addRect(rect);
		}

		FramebufferUpdatePacket packet2 = convertTo24AndThenTo16(packet);

		// Compare old and new packets
		assertEquals(packet.getPacketType(), packet2.getPacketType());
		assertEquals(packet.getNumberOfRectangles(), packet2
				.getNumberOfRectangles());
		for (int i = 0; i < packet.getNumberOfRectangles(); i++) {
			CoRRERect rect1 = (CoRRERect) packet.getRectangle(i);
			CoRRERect rect2 = (CoRRERect) packet2.getRectangle(i);
			assertEquals(rect1.getX(), rect2.getX());
			assertEquals(rect1.getY(), rect2.getY());
			assertEquals(rect1.getWidth(), rect2.getWidth());
			assertEquals(rect1.getHeight(), rect2.getHeight());
			assertEquals(rect1.getNumberOfSubrects(), rect2
					.getNumberOfSubrects());

			for (int j = 0; j < rect1.getNumberOfSubrects(); j++) {
				CoRRESubrect subrect1 = (CoRRESubrect) rect1.getSubrectangle(i);
				CoRRESubrect subrect2 = (CoRRESubrect) rect2.getSubrectangle(i);

				assertEquals(subrect1.getX(), subrect2.getX());
				assertEquals(subrect1.getY(), subrect2.getY());
				assertEquals(subrect1.getWidth(), subrect2.getWidth());
				assertEquals(subrect1.getHeight(), subrect2.getHeight());
				assertEquals(RfbIOTools.arrayToString(subrect1.getPixelBuf()),
						RfbIOTools.arrayToString(subrect2.getPixelBuf()));
			}
		}

	}

	public void testFramebufferUpdatePacketWithHextileEncoding()
			throws Throwable {
		byte[] rawBuf256x16 = new byte[] { 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12,
				0x21, 0x21, 0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21,
				0x22, 0x22, 0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22,
				0x11, 0x11, 0x12, 0x12, 0x21, 0x21, 0x22, 0x22, 0x11, 0x11,
				0x12, 0x12, 0x21, 0x21, 0x22, 0x22 };

		Screen screen = getScreen16LTRGB();
		FramebufferUpdatePacket packet = new FramebufferUpdatePacket(screen);

		{
			HextileRect rect = new HextileRect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_HEXTILE, 0, 0, 48,
					32);
			Vector rectangles = new Vector(6);
			{
				RawRect rawRect = new RawRect();
				rawRect.setScreen(screen);
				rawRect.setHeaderParameters(
						RfbConstants.SUBENCODING_HEXTILE_RAW, 0, 0, 16, 16);
				rawRect.setBuf(rawBuf256x16);
				rawRect.validate();
				rectangles.add(rawRect);
			}
			{
				HextileTile tile = new HextileTile();
				tile.setScreen(screen);
				tile.setHeaderParameters(
						RfbConstants.SUBENCODING_HEXTILE_BACKGROUND_SPECIFIED,
						16, 0, 16, 16);
				tile.setBackgroundColourBuf(new byte[] { 0x01, 0x10 });
				tile.validate();
				rectangles.add(tile);
			}
			{
				HextileTile tile = new HextileTile();
				tile.setScreen(screen);
				tile.setHeaderParameters(
						RfbConstants.SUBENCODING_HEXTILE_FOREGROUND_SPECIFIED,
						32, 0, 16, 16);
				tile.setForegroundColourBuf(new byte[] { 0x02, 0x20 });
				tile.validate();
				rectangles.add(tile);
			}
			{
				HextileTile tile = new HextileTile();
				tile.setScreen(screen);
				tile
						.setHeaderParameters(
								RfbConstants.SUBENCODING_HEXTILE_BACKGROUND_SPECIFIED
										| RfbConstants.SUBENCODING_HEXTILE_FOREGROUND_SPECIFIED,
								0, 16, 16, 16);
				tile.setBackgroundColourBuf(new byte[] { 0x01, 0x10 });
				tile.setForegroundColourBuf(new byte[] { 0x02, 0x20 });
				tile.validate();
				rectangles.add(tile);
			}
			{
				HextileTile tile = new HextileTile();
				tile.setScreen(screen);
				tile
						.setHeaderParameters(
								RfbConstants.SUBENCODING_HEXTILE_BACKGROUND_SPECIFIED
										| RfbConstants.SUBENCODING_HEXTILE_FOREGROUND_SPECIFIED
										| RfbConstants.SUBENCODING_HEXTILE_ANY_SUBRECTS,
								16, 16, 16, 16);
				tile.setBackgroundColourBuf(new byte[] { 0x01, 0x10 });
				tile.setForegroundColourBuf(new byte[] { 0x02, 0x20 });
				Vector subrectangles = new Vector(2);
				{
					HextileSubrect subrect = new HextileSubrect();
					subrect.setScreen(screen);
					subrect.setHeaderParameters(tile.getEncodingType(), 0, 0,
							2, 2);
					subrect.validate();
					subrectangles.add(subrect);
				}
				{
					HextileSubrect subrect = new HextileSubrect();
					subrect.setScreen(screen);
					subrect.setEncodingType(tile.getEncodingType());
					subrect.setX(2);
					subrect.setY(0);
					subrect.setWidth(2);
					subrect.setHeight(2);
					subrect.validate();
					subrectangles.add(subrect);
				}
				tile.setRectangles(subrectangles);
				tile.validate();
				rectangles.add(tile);
			}
			{
				HextileTile tile = new HextileTile();
				tile.setScreen(screen);
				tile
						.setHeaderParameters(
								RfbConstants.SUBENCODING_HEXTILE_BACKGROUND_SPECIFIED
										| RfbConstants.SUBENCODING_HEXTILE_ANY_SUBRECTS
										| RfbConstants.SUBENCODING_HEXTILE_SUBRECTS_COLOURED,
								32, 16, 16, 16);
				tile.setBackgroundColourBuf(new byte[] { 0x01, 0x10 });
				Vector subrectangles = new Vector(2);
				{
					HextileSubrect subrect = new HextileSubrect();
					subrect.setScreen(screen);
					subrect.setHeaderParameters(tile.getEncodingType(), 0, 0,
							2, 2);
					subrect.setForegroundColourBuf(new byte[] { 0x03, 0x30 });
					subrect.validate();
					subrectangles.add(subrect);
				}
				{
					HextileSubrect subrect = new HextileSubrect();
					subrect.setScreen(screen);
					subrect.setHeaderParameters(tile.getEncodingType(), 2, 0,
							2, 2);
					subrect.setForegroundColourBuf(new byte[] { 0x04, 0x40 });
					subrect.validate();
					subrectangles.add(subrect);
				}
				tile.setRectangles(subrectangles);
				tile.validate();
				rectangles.add(tile);
			}
			rect.setRectangles(rectangles);
			rect.validate();
			packet.addRect(rect);
		}

		packet.validate();

		FramebufferUpdatePacket packet2 = convertTo24AndThenTo16(packet);

		// Compare old and new packets
		assertEquals(packet.getPacketType(), packet2.getPacketType());
		assertEquals(packet.getNumberOfRectangles(), packet2
				.getNumberOfRectangles());

		HextileRect hextileRect = (HextileRect) packet2.rectangles()
				.nextElement();

		Enumeration e = hextileRect.rectangles();
		{
			RawRect rect = (RawRect) e.nextElement();
			assertEquals(0, rect.getX());
			assertEquals(0, rect.getY());
			assertEquals(16, rect.getWidth());
			assertEquals(16, rect.getHeight());
			assertEquals(RfbIOTools.arrayToString(rawBuf256x16), RfbIOTools
					.arrayToString(rect.getBuf()));
		}
		{
			HextileTile tile = (HextileTile) e.nextElement();
			assertEquals(16, tile.getX());
			assertEquals(0, tile.getY());
			assertEquals(16, tile.getWidth());
			assertEquals(16, tile.getHeight());
			assertEquals(true, tile.isBackgroundSpecified());
			assertEquals(false, tile.isForegroundSpecified());
			assertEquals(RfbIOTools.arrayToString(new byte[] { 0x01, 0x10 }),
					RfbIOTools.arrayToString(tile.getBackgroundColourBuf()));
		}
		{
			HextileTile tile = (HextileTile) e.nextElement();
			assertEquals(32, tile.getX());
			assertEquals(0, tile.getY());
			assertEquals(16, tile.getWidth());
			assertEquals(16, tile.getHeight());
			assertEquals(false, tile.isBackgroundSpecified());
			assertEquals(true, tile.isForegroundSpecified());
			assertEquals(RfbIOTools.arrayToString(new byte[] { 0x02, 0x20 }),
					RfbIOTools.arrayToString(tile.getForegroundColourBuf()));
		}
		{
			HextileTile tile = (HextileTile) e.nextElement();
			assertEquals(0, tile.getX());
			assertEquals(16, tile.getY());
			assertEquals(16, tile.getWidth());
			assertEquals(16, tile.getHeight());
			assertEquals(true, tile.isBackgroundSpecified());
			assertEquals(RfbIOTools.arrayToString(new byte[] { 0x01, 0x10 }),
					RfbIOTools.arrayToString(tile.getBackgroundColourBuf()));
			assertEquals(true, tile.isForegroundSpecified());
			assertEquals(RfbIOTools.arrayToString(new byte[] { 0x02, 0x20 }),
					RfbIOTools.arrayToString(tile.getForegroundColourBuf()));
		}
		{
			HextileTile tile = (HextileTile) e.nextElement();
			assertEquals(16, tile.getX());
			assertEquals(16, tile.getY());
			assertEquals(16, tile.getWidth());
			assertEquals(16, tile.getHeight());
			assertEquals(true, tile.isBackgroundSpecified());
			assertEquals(RfbIOTools.arrayToString(new byte[] { 0x01, 0x10 }),
					RfbIOTools.arrayToString(tile.getBackgroundColourBuf()));
			assertEquals(true, tile.isForegroundSpecified());
			assertEquals(RfbIOTools.arrayToString(new byte[] { 0x02, 0x20 }),
					RfbIOTools.arrayToString(tile.getForegroundColourBuf()));

			Enumeration subrectangles = tile.rectangles();
			{
				HextileSubrect subrect = (HextileSubrect) subrectangles
						.nextElement();
				assertEquals(0, subrect.getX());
				assertEquals(0, subrect.getY());
				assertEquals(2, subrect.getWidth());
				assertEquals(2, subrect.getHeight());
				assertFalse(subrect.isColoured());
			}
			{
				HextileSubrect subrect = (HextileSubrect) subrectangles
						.nextElement();
				assertEquals(2, subrect.getX());
				assertEquals(0, subrect.getY());
				assertEquals(2, subrect.getWidth());
				assertEquals(2, subrect.getHeight());
				assertFalse(subrect.isColoured());
			}
		}
		{
			HextileTile tile = (HextileTile) e.nextElement();
			assertEquals(32, tile.getX());
			assertEquals(16, tile.getY());
			assertEquals(16, tile.getWidth());
			assertEquals(16, tile.getHeight());
			assertEquals(true, tile.isBackgroundSpecified());
			assertEquals(RfbIOTools.arrayToString(new byte[] { 0x01, 0x10 }),
					RfbIOTools.arrayToString(tile.getBackgroundColourBuf()));
			assertEquals(false, tile.isForegroundSpecified());

			Enumeration subrectangles = tile.rectangles();
			{
				HextileSubrect subrect = (HextileSubrect) subrectangles
						.nextElement();
				assertEquals(0, subrect.getX());
				assertEquals(0, subrect.getY());
				assertEquals(2, subrect.getWidth());
				assertEquals(2, subrect.getHeight());
				assertTrue(subrect.isColoured());
				assertEquals(RfbIOTools
						.arrayToString(new byte[] { 0x03, 0x30 }), RfbIOTools
						.arrayToString(subrect.getForegroundColourBuf()));
			}
			{
				HextileSubrect subrect = (HextileSubrect) subrectangles
						.nextElement();
				assertEquals(2, subrect.getX());
				assertEquals(0, subrect.getY());
				assertEquals(2, subrect.getWidth());
				assertEquals(2, subrect.getHeight());
				assertTrue(subrect.isColoured());
				assertEquals(RfbIOTools
						.arrayToString(new byte[] { 0x04, 0x40 }), RfbIOTools
						.arrayToString(subrect.getForegroundColourBuf()));
			}
		}
	}

	public void testFramebufferUpdatePacketWithTightEncoding() throws Throwable {
		Screen screen = getScreen16LTRGB();
		FramebufferUpdatePacket packet = new FramebufferUpdatePacket(screen);
		{
			TightRect rect = new TightRect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_TIGHT, 0, 0, 2, 2);
			rect.setSubencoding(RfbConstants.SUBENCODING_TIGHT_FILL);
			rect.setData(new byte[] { 0x1e, 0x32 });
			rect.validate();
			packet.addRect(rect);
		}

		{
			TightRect rect = new TightRect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_TIGHT, 0, 0, 2, 2);
			rect.setSubencoding(RfbConstants.SUBENCODING_TIGHT_JPEG);
			rect.setData(new byte[] { 0x1e, 0x32 });
			rect.validate();
			packet.addRect(rect);
		}

		{
			TightRect rect = new TightRect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_TIGHT, 0, 0, 2, 2);
			rect.setSubencoding(RfbConstants.SUBENCODING_TIGHT_EXPLICIT_FILTER);
			rect.setFilterId(RfbConstants.SUBENCODING_TIGHT_FILTER_COPY);
			rect.setData(new byte[] { 0x1e, 0x32, 0x2c, 0x31, 0x2f, 0x30, 0x30,
					0x2f });// Four pixels
			rect.validate();
			packet.addRect(rect);
		}

		{
			TightRect rect = new TightRect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_TIGHT, 0, 0, 2, 2);
			rect.setSubencoding(RfbConstants.SUBENCODING_TIGHT_EXPLICIT_FILTER);
			rect.setFilterId(RfbConstants.SUBENCODING_TIGHT_FILTER_PALETTE);
			rect.setNumberOfColours(2);
			rect.setPalette(new byte[] { 0x1e, 0x32, 0x2c, 0x31 });
			rect.setData(new byte[] { 0x1, 0x2 });// Four pixels, 1 bit per
													// pixel, 1 byte per row
			rect.validate();
			packet.addRect(rect);
		}

		{
			TightRect rect = new TightRect();
			rect.setScreen(screen);
			rect.setHeaderParameters(RfbConstants.ENCODING_TIGHT, 0, 0, 2, 2);
			rect.setSubencoding(RfbConstants.SUBENCODING_TIGHT_EXPLICIT_FILTER);
			rect.setFilterId(RfbConstants.SUBENCODING_TIGHT_FILTER_PALETTE);
			rect.setNumberOfColours(4);
			rect.setPalette(new byte[] { 0x1e, 0x32, 0x2c, 0x31, 0x2f, 0x30,
					0x30, 0x2f });
			rect.setData(new byte[] { 0x0, 0x1, 0x2, 0x3 });// Four pixels, 8
															// bit per pixel, 2
															// bytes per row
			rect.validate();
			packet.addRect(rect);
		}

		FramebufferUpdatePacket packet2 = convertTo24AndThenTo16(packet);

		// Compare old and new packets
		assertEquals("Incorrect conversion of TightRect(s)", packet
				.dumpToString(true), packet2.dumpToString(true));
	}

	private FramebufferUpdatePacket convertTo24AndThenTo16(
			FramebufferUpdatePacket packet) throws IOException {
		Screen screen24 = getScreen24LTRGB();
		Screen screen16 = getScreen16LTRGB();

		// System.out.println("================Scren16================");
		// packet.dump(System.out);
		packet.validate();

		FramebufferUpdatePacket rpacket;
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream os = new DataOutputStream(bos);
			packet.writePacketData(os);

			ByteArrayInputStream bis = new ByteArrayInputStream(bos
					.toByteArray());
			DataInputStream is = new DataInputStream(bis);
			rpacket = new FramebufferUpdatePacket(packet.getScreen());
			rpacket.readPacketData(is);
			rpacket.validate();
		}

		FramebufferUpdatePacket packet1 = (FramebufferUpdatePacket) PacketManager
				.convertPacket(rpacket, screen24);
		// System.out.println("================Scren24================");
		// packet1.dump(System.out);
		packet1.validate();

		FramebufferUpdatePacket rpacket1;
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream os = new DataOutputStream(bos);
			packet1.writePacketData(os);

			ByteArrayInputStream bis = new ByteArrayInputStream(bos
					.toByteArray());
			DataInputStream is = new DataInputStream(bis);
			rpacket1 = new FramebufferUpdatePacket(packet1.getScreen());
			rpacket1.readPacketData(is);
			rpacket1.validate();
		}

		FramebufferUpdatePacket packet2 = (FramebufferUpdatePacket) PacketManager
				.convertPacket(rpacket1, screen16);
		// System.out.println("================Scren16 again================");
		// packet2.dump(System.out);
		packet2.validate();

		return packet2;
	}

	private Screen getScreen24LTRGB() {
		Screen screen = new Screen(7);
		screen.setFramebufferSize(640, 480);
		screen.setDesktopName("Test");
		screen.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW,
				RfbConstants.ENCODING_RRE, RfbConstants.ENCODING_CO_RRE,
				RfbConstants.ENCODING_HEXTILE, RfbConstants.ENCODING_TIGHT,
				RfbConstants.ENCODING_COMPRESS_LEVEL0 + 3 });
		screen.setPixelFormat(24, 24, 0, 1, 255, 255, 255, 16, 8, 0);
		screen.validate();
		return screen;
	}

	private Screen getScreen16LTRGB() {
		Screen screen = new Screen(7);
		screen.setFramebufferSize(640, 480);
		screen.setDesktopName("Test");
		screen.setSupportedEncodings(new int[] { RfbConstants.ENCODING_RAW,
				RfbConstants.ENCODING_RRE, RfbConstants.ENCODING_CO_RRE,
				RfbConstants.ENCODING_HEXTILE, RfbConstants.ENCODING_TIGHT,
				RfbConstants.ENCODING_COMPRESS_LEVEL0 + 3 });
		screen.setPixelFormat(16, 16, 0, 1, 31, 63, 31, 11, 5, 0);
		screen.validate();
		return screen;
	}

}
