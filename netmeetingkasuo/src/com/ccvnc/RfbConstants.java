package com.ccvnc;

/**
 * RfbConstants - contains common constants used in RFB protocol.
 * 
 * @author Volodymyr M. Lisivka
 */
public interface RfbConstants {

	public static final String VNC_PROTOCOL_VERSION_MAJOR = "RFB 003.";

	/**
	 * Server message types
	 */
	final static int SERVER_FRAMEBUFFER_UPDATE = 0,
			SERVER_SET_COLOURMAP_ENTRIES = 1, SERVER_BELL = 2,
			SERVER_CUT_TEXT = 3;

	/**
	 * Client message types
	 */
	public static final int CLIENT_SET_PIXEL_FORMAT = 0,
			CLIENT_FIX_COLOURMAP_ENTRIES = 1, CLIENT_SET_ENCODINGS = 2,
			CLIENT_FRAMEBUFFER_UPDATE_REQUEST = 3, CLIENT_KEYBOARD_EVENT = 4,
			CLIENT_POINTER_EVENT = 5, CLIENT_CUT_TEXT = 6;

	/**
	 * Server authorization type
	 */
	public final static int CONNECTION_FAILED = 0, NO_AUTH = 1, VNC_AUTH = 2;

	/**
	 * Server authorization reply
	 */
	public final static int VNC_AUTH_OK = 0, VNC_AUTH_FAILED = 1,
			VNC_AUTH_TOO_MANY = 2;

	/**
	 * Encodings
	 */
	public final static int ENCODING_RAW = 0, ENCODING_COPY_RECT = 1,
			ENCODING_RRE = 2, ENCODING_CO_RRE = 4, ENCODING_HEXTILE = 5,
			ENCODING_ZLIB = 6, ENCODING_TIGHT = 7, ENCODING_ZLIBHEX = 8,
			ENCODING_ZRLE = 16;

	/**
	 * Hextile subencodings
	 */
	public final static int SUBENCODING_HEXTILE_RAW = (1 << 0),
			SUBENCODING_HEXTILE_BACKGROUND_SPECIFIED = (1 << 1),
			SUBENCODING_HEXTILE_FOREGROUND_SPECIFIED = (1 << 2),
			SUBENCODING_HEXTILE_ANY_SUBRECTS = (1 << 3),
			SUBENCODING_HEXTILE_SUBRECTS_COLOURED = (1 << 4);

	/**
	 * Pseudoencodings
	 */
	public final static int ENCODING_COMPRESS_LEVEL0 = 0xFFFFFF00,
			ENCODING_JPEG_QUALITY_LEVEL_0 = 0xFFFFFFE0,
			ENCODING_X_CURSOR = 0xFFFFFF10, ENCODING_RICH_CURSOR = 0xFFFFFF11,
			ENCODING_POINTER_POS = 0xFFFFFF18, ENCODING_LAST_RECT = 0xFFFFFF20,
			ENCODING_DESKTOP_SIZE = 0xFFFFFF21;

	/**
	 * Tight subencodings
	 */
	public final static int SUBENCODING_TIGHT_EXPLICIT_FILTER = 4,
			SUBENCODING_TIGHT_FILL = 8, SUBENCODING_TIGHT_JPEG = 9;
	public final static int TIGHT_MAX_SUBENCODING = 9;

	public final static int SUBENCODING_TIGHT_FILTER_COPY = 0x00,
			SUBENCODING_TIGHT_FILTER_PALETTE = 0x01,
			SUBENCODING_TIGHT_FILTER_GRADIENT = 0x02;
	public final static int TIGHT_MIN_TO_COMPRESS = 12;

	public final static int[] SUPPORTED_ENCODINGS_ARRAY = {
	// Normal encodings
			ENCODING_COPY_RECT,

			// Cursor handling
			ENCODING_X_CURSOR, ENCODING_RICH_CURSOR, ENCODING_POINTER_POS,

			// Misc
			ENCODING_LAST_RECT,// TightVNC extension
			ENCODING_DESKTOP_SIZE,// May cause problems with TightVNC

			// Encodings with compression
			ENCODING_TIGHT,// TightVNC extension
			// ENCODING_QUALITY_LEVEL_0 + 3,//JPEG quality (0..9)
			ENCODING_COMPRESS_LEVEL0 + 3,// Compression ratio (0..9)

			// Common encdoings
			ENCODING_HEXTILE, ENCODING_CO_RRE,// Deprecated. Hextile is better
			// choice
			ENCODING_RRE, ENCODING_RAW,

	};

	public static final int[] SUPPORTED_ENCODINGS_FOR_ONE_TO_ONE_CONNECTION_ARRAY = {
	// Normal encodings
			ENCODING_COPY_RECT,

			// Cursor handling
			ENCODING_X_CURSOR, ENCODING_RICH_CURSOR, ENCODING_POINTER_POS,

			// Misc
			ENCODING_LAST_RECT,// TightVNC extension
			ENCODING_DESKTOP_SIZE,// May cause problems with TightVNC

			// Encodings with compression
			ENCODING_ZRLE,// RealVNC4.0
			ENCODING_TIGHT,// TightVNC extension
			ENCODING_JPEG_QUALITY_LEVEL_0 + 6,// JPEG quality (0..9)
			ENCODING_ZLIB,// TightVNC extension
			ENCODING_COMPRESS_LEVEL0 + 3,// Compression ratio (0..9)

			// Common encdoings
			ENCODING_HEXTILE, ENCODING_CO_RRE,// Deprecated. Hextile is better
			// choice
			ENCODING_RRE, ENCODING_RAW,

	};
}
