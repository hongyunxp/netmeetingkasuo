package com.ccvnc;

import java.util.Enumeration;

import org.apache.log4j.Logger;

import com.ccvnc.packets.Packet;
import com.ccvnc.packets.client.ClientCutTextPacket;
import com.ccvnc.packets.client.FixColoumapEntriesPacket;
import com.ccvnc.packets.client.FramebufferUpdateRequestPacket;
import com.ccvnc.packets.client.KeyboardEventPacket;
import com.ccvnc.packets.client.PointerEvent;
import com.ccvnc.packets.client.SetEncodingsPacket;
import com.ccvnc.packets.client.SetPixelFormatPacket;
import com.ccvnc.packets.server.Bell;
import com.ccvnc.packets.server.FramebufferUpdatePacket;
import com.ccvnc.packets.server.ServerCutText;
import com.ccvnc.packets.server.SetColourmapEntries;
import com.ccvnc.packets.server.rect.CoRRERect;
import com.ccvnc.packets.server.rect.CopyRect;
import com.ccvnc.packets.server.rect.FramebufferSizeChange;
import com.ccvnc.packets.server.rect.HextileRect;
import com.ccvnc.packets.server.rect.RRERect;
import com.ccvnc.packets.server.rect.RawRect;
import com.ccvnc.packets.server.rect.Rectangle;
import com.ccvnc.packets.server.rect.RichCursorShapeUpdate;
import com.ccvnc.packets.server.rect.SoftMouseMove;
import com.ccvnc.packets.server.rect.TightRect;
import com.ccvnc.packets.server.rect.XCursorShapeUpdate;
import com.ccvnc.packets.server.rect.ZRLERect;
import com.ccvnc.packets.server.rect.ZlibRect;

/**
 * PacketManager - resolves packet or rectangle id and returns handler for this
 * packet or rectangle.
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class PacketManager implements RfbConstants {

	private static Logger logger = Logger.getLogger(PacketManager.class);

	public static Packet getClientPacketHandler(int messageType) {
		Packet packet;

		switch (messageType) {
		case CLIENT_SET_PIXEL_FORMAT:

			logger.debug("SET_PIXEL_FORMAT");
			packet = new SetPixelFormatPacket();
			break;
		case CLIENT_FIX_COLOURMAP_ENTRIES:

			logger.debug("FIX_COLOURMAP_ENTRIES");
			packet = new FixColoumapEntriesPacket();
			break;
		case CLIENT_SET_ENCODINGS:

			logger.debug("SET_ENCODINGS");
			packet = new SetEncodingsPacket();
			break;
		case CLIENT_FRAMEBUFFER_UPDATE_REQUEST:

			logger.debug("FRAME_BUFER_UPDATE_REQUEST");
			packet = new FramebufferUpdateRequestPacket();
			break;
		case CLIENT_KEYBOARD_EVENT:

			logger.debug("KEYBOARD_EVENT");
			packet = new KeyboardEventPacket();
			break;
		case CLIENT_POINTER_EVENT:

			logger.debug("POINTER_EVENT");
			packet = new PointerEvent();
			break;
		case CLIENT_CUT_TEXT:

			logger.debug("CLIENT_CUT_TEXT");
			packet = new ClientCutTextPacket();
			break;
		default:
			throw new RuntimeException("Unknown client packet type: "
					+ messageType);
		}
		return packet;
	}

	public static Packet getServerPacketHandler(int messageType) {
		Packet packet;
		switch (messageType) {
		case SERVER_FRAMEBUFFER_UPDATE:

			logger.debug("FRAMEBUFFER_UPDATE");
			packet = new FramebufferUpdatePacket();
			break;
		case SERVER_SET_COLOURMAP_ENTRIES:

			logger.debug("SET_COLOURMAP_ENTRIES");
			packet = new SetColourmapEntries();
			break;
		case SERVER_BELL:

			logger.debug("BELL");
			packet = new Bell();
			break;
		case SERVER_CUT_TEXT:

			logger.debug("SERVER_CUT_TEXT");
			packet = new ServerCutText();
			break;
		default:
			throw new RuntimeException("Unknown server packet type: "
					+ messageType);
		}
		return packet;
	}

	public static Packet convertPacket(Packet packet, Screen screen) {
		if (screen == null)
			throw new NullPointerException(
					"Screen not initialized, can't convert packet.");

		if (packet.isClientPacket()) {
		}// Nothing to do for client packet now
		else {// Convert server packet
			switch (packet.getPacketType()) {
			case SERVER_FRAMEBUFFER_UPDATE:
				// If screens is incompatible
				if (!screen.isPixelFormatCompatible(packet.getScreen())) {
					// then convert packet
					FramebufferUpdatePacket fbu = new FramebufferUpdatePacket(
							screen);
					for (Enumeration e = ((FramebufferUpdatePacket) packet)
							.rectangles(); e.hasMoreElements();) {
						Rectangle rect = (Rectangle) e.nextElement();
						Rectangle rect2 = convertRectangle(rect, screen);
						if (rect2 != null)
							fbu.addRect(rect2);
						else
							logger.debug("Rectangle " + rect
									+ " can't be converted and skipped.");
					}
					packet = fbu;
				}
				break;
			}
		}

		return packet;
	}

	public static Rectangle convertRectangle(Rectangle rect, Screen screen2) {
		boolean convertPF = false;
		boolean convertRF = false;

		// Check capabilities of screen1 and screen2
		Screen screen1 = rect.getScreen();
		if (screen1 == null)
			throw new NullPointerException(
					"Source screen of rectangle not initalized, can't convert.");
		if (screen2 == null)
			throw new NullPointerException(
					"Target screen of rectangle not initalized, can't convert.");

		if (!screen2.isPixelFormatCompatible(screen1))
			convertPF = true;

		if (!screen2.canAcceptRectangleFormat(rect))
			convertRF = true;

		// Convert pixel format
		if (convertPF)
			rect = rect.convertPixelFormat(screen2);

		if (convertRF)
			// throw new
			// RuntimeException("TODO: impement rectangle format converter (eg. zlib to raw).");
			return null;

		return rect;
	}

	public static Rectangle getRectangeHandler(int encodingType)
			throws RuntimeException {
		Rectangle rect;
		switch (encodingType) {
		case ENCODING_X_CURSOR:

			logger.debug("ENCODING_X_CURSOR");
			rect = new XCursorShapeUpdate();
			break;
		case ENCODING_RICH_CURSOR:

			logger.debug("ENCODING_RICH_CURSOR");
			rect = new RichCursorShapeUpdate();
			break;
		case ENCODING_POINTER_POS:

			logger.debug("ENCODING_POINTER_POS");
			rect = new SoftMouseMove();
			break;

		case ENCODING_RAW:

			logger.debug("ENCODING_RAW");
			rect = new RawRect();
			break;
		case ENCODING_COPY_RECT:

			logger.debug("ENCODING_COPY_RECT");
			rect = new CopyRect();
			break;
		case ENCODING_RRE:

			logger.debug("ENCODING_RRE");
			rect = new RRERect();
			break;
		case ENCODING_CO_RRE:

			logger.debug("ENCODING_CO_RRE");
			rect = new CoRRERect();
			break;
		case ENCODING_HEXTILE:

			logger.debug("ENCODING_HEXTILE");
			rect = new HextileRect();
			break;
		case ENCODING_ZLIB:

			logger.debug("ENCODING_ZLIB");
			rect = new ZlibRect();
			break;
		case ENCODING_ZLIBHEX:

			logger.debug("ENCODING_ZLIBHEX");
			throw new RuntimeException("Unsupported ecnoding: ENCODING_ZLIBHEX");
		case ENCODING_TIGHT:

			logger.debug("ENCODING_TIGHT");
			rect = new TightRect();
			break;
		case ENCODING_ZRLE:

			logger.debug("ENCODING_ZRLE");
			rect = new ZRLERect();
			break;
		case ENCODING_DESKTOP_SIZE:

			logger.debug("ENCODING_NEW_FRAMEBUFFER_SIZE");
			rect = new FramebufferSizeChange();
			break;
		default:
			throw new RuntimeException("Unsupported ecnoding: " + encodingType);
		}
		return rect;
	}

	public static String getEncodingName(int encoding) {
		switch (encoding) {
		case ENCODING_RAW:
			return "Raw";
		case ENCODING_COPY_RECT:
			return "CopyRect";
		case ENCODING_RRE:
			return "RRE";
		case ENCODING_CO_RRE:
			return "CoRRE";
		case ENCODING_HEXTILE:
			return "Hextile";
		case ENCODING_ZLIB:
			return "Zlib";
		case ENCODING_ZRLE:
			return "ZRLE";
		case ENCODING_ZLIBHEX:
			return "ZlibHex";
		case ENCODING_TIGHT:
			return "Tight";
		case ENCODING_LAST_RECT:
			return "LastRect";
		case ENCODING_DESKTOP_SIZE:
			return "DesktopSize";
		case ENCODING_POINTER_POS:
			return "PointerPos";
		case ENCODING_RICH_CURSOR:
			return "RichCursor";
		case ENCODING_X_CURSOR:
			return "XCursor";
		case ENCODING_COMPRESS_LEVEL0:
		case ENCODING_COMPRESS_LEVEL0 + 1:
		case ENCODING_COMPRESS_LEVEL0 + 2:
		case ENCODING_COMPRESS_LEVEL0 + 3:
		case ENCODING_COMPRESS_LEVEL0 + 4:
		case ENCODING_COMPRESS_LEVEL0 + 5:
		case ENCODING_COMPRESS_LEVEL0 + 6:
		case ENCODING_COMPRESS_LEVEL0 + 7:
		case ENCODING_COMPRESS_LEVEL0 + 8:
		case ENCODING_COMPRESS_LEVEL0 + 9:
			return "CompressionLevel" + (encoding - ENCODING_COMPRESS_LEVEL0);
		case ENCODING_JPEG_QUALITY_LEVEL_0:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 1:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 2:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 3:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 4:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 5:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 6:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 7:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 8:
		case ENCODING_JPEG_QUALITY_LEVEL_0 + 9:
			return "JpegQualityLevel"
					+ (encoding - ENCODING_JPEG_QUALITY_LEVEL_0);
		}
		return "Unknown encoding (" + encoding + ")";
	}

	public static int getEncodingByName(String encoding) {
		if (encoding.equalsIgnoreCase("Raw"))
			return ENCODING_RAW;
		if (encoding.equalsIgnoreCase("CopyRect"))
			return ENCODING_COPY_RECT;
		if (encoding.equalsIgnoreCase("RRE"))
			return ENCODING_RRE;
		if (encoding.equalsIgnoreCase("CoRRE"))
			return ENCODING_CO_RRE;
		if (encoding.equalsIgnoreCase("Hextile"))
			return ENCODING_HEXTILE;
		if (encoding.equalsIgnoreCase("Zlib"))
			return ENCODING_ZLIB;
		if (encoding.equalsIgnoreCase("ZRLE"))
			return ENCODING_ZRLE;
		if (encoding.equalsIgnoreCase("ZlibHex"))
			return ENCODING_ZLIBHEX;
		if (encoding.equalsIgnoreCase("Tight"))
			return ENCODING_TIGHT;
		if (encoding.equalsIgnoreCase("LastRect"))
			return ENCODING_LAST_RECT;
		if (encoding.equalsIgnoreCase("DesktopSize"))
			return ENCODING_DESKTOP_SIZE;
		if (encoding.equalsIgnoreCase("PointerPos"))
			return ENCODING_POINTER_POS;
		if (encoding.equalsIgnoreCase("RichCursor"))
			return ENCODING_RICH_CURSOR;
		if (encoding.equalsIgnoreCase("XCursor"))
			return ENCODING_X_CURSOR;
		if (encoding.toLowerCase().startsWith("CompressionLevel".toLowerCase())) {
			try {
				return ENCODING_COMPRESS_LEVEL0
						+ Integer.parseInt(encoding
								.substring("CompressionLevel".length()));
			} catch (NumberFormatException e) {
				throw new RuntimeException("Number is wrong: " + e);
			}
		}
		if (encoding.toLowerCase().startsWith("JPEGQualityLevel".toLowerCase())) {
			try {
				return ENCODING_JPEG_QUALITY_LEVEL_0
						+ Integer.parseInt(encoding
								.substring("JPEGQualityLevel".length()));
			} catch (NumberFormatException e) {
				throw new RuntimeException("Number is wrong: " + e);
			}
		}

		try {
			return Integer.parseInt(encoding);
		} catch (NumberFormatException e) {
			throw new RuntimeException(
					"List of supported encoding names: Raw,CopyRect,RRE,CoRRE,Hextile,Zlib,CompressionLevelN,ZRLE,ZlibHex,Tight,JPEGQualityLevelN,LastRect,DesktopSize,PointerPos,RichCursor,XCursor or encoding number.");
		}

	}

}
