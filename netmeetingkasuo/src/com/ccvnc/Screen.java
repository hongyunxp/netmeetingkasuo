package com.ccvnc;

import org.apache.log4j.Logger;

import com.ccvnc.packets.Packet;
import com.ccvnc.packets.server.rect.Rectangle;

/**
 * Screen - contains infromation about remote screen.
 * 
 * @author Volodymyr M. Lisivka
 */
public class Screen {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Screen.class);

	// Framebuffer size
	private int framebufferWidth = -1;
	private int framebufferHeight = -1;

	// Server pixel format
	private int bitsPerPixel = -1;
	private int depth = -1;
	private int bigEndianFlag = -1;
	private int trueColorFlag = -1;
	private int redMax = -1;
	private int greenMax = -1;
	private int blueMax = -1;
	private int redShift = -1;
	private int greenShift = -1;
	private int blueShift = -1;

	// Desktop name
	private String desktopName;

	// Calculated data
	private int bytesPerPixel;
	private int redBits;
	private int greenBits;
	private int blueBits;

	private int rfbMinorVersion = -1;

	private int[] supportedEncodings;
	private int[] palette;

	private Object changeNotifier = new Object();

	public Screen() {
	}

	public Screen(int rfbMinorVersion) {
		this.rfbMinorVersion = rfbMinorVersion;
	}

	public Screen(Screen screen, boolean full) {
		copyScreenProperties(screen);
		if (full) {
			supportedEncodings = screen.supportedEncodings;

			if (trueColorFlag == 0)
				// Copy palette
				setPalette(0, 1 << bitsPerPixel, screen.palette);
		}
	}

	public int[] getPalette() {
		return palette;
	}

	private void copyScreenProperties(Screen screen) {
		this.framebufferWidth = screen.framebufferWidth;
		this.framebufferHeight = screen.framebufferHeight;
		this.bitsPerPixel = screen.bitsPerPixel;
		this.depth = screen.depth;
		this.bigEndianFlag = screen.bigEndianFlag;
		this.trueColorFlag = screen.trueColorFlag;
		this.redMax = screen.redMax;
		this.greenMax = screen.greenMax;
		this.blueMax = screen.blueMax;
		this.redShift = screen.redShift;
		this.greenShift = screen.greenShift;
		this.blueShift = screen.blueShift;
		this.desktopName = screen.desktopName;
		this.bytesPerPixel = screen.bytesPerPixel;
		this.redBits = screen.redBits;
		this.greenBits = screen.greenBits;
		this.blueBits = screen.blueBits;

		this.rfbMinorVersion = screen.rfbMinorVersion;

		supportedEncodings = null;// Don't copy capabilities of remote screen
		palette = null;// Don't copy palette of remote screen
		if (trueColorFlag == 0)
			resetPalette();
	}

	public void setPalette(int firstColour, int numberOfColours, int[] buf) {
		if (buf.length != numberOfColours * 3)
			throw new RuntimeException("Wrong length of palette bufer.");

		System.arraycopy(buf, 0, palette, firstColour * 3, numberOfColours * 3);
		notifyAboutChange();
	}

	/**
	 * Create new empty array to store palette colours.
	 */
	public void resetPalette() {
		if (trueColorFlag != 0)
			palette = null;
		else
			palette = new int[1 << bitsPerPixel];
		notifyAboutChange();
	}

	public void setSupportedEncodings(int[] encodings) {
		this.supportedEncodings = encodings;
		notifyAboutChange();
	}

	public int[] getSupportedEncodings() {
		return supportedEncodings;
	}

	public void setRfbMinorVersion(int rfbMinorVersion) {
		this.rfbMinorVersion = rfbMinorVersion;
		notifyAboutChange();
	}

	public int getRfbMinorVersion() {
		return rfbMinorVersion;
	}

	public boolean canAcceptPacket(Packet packet) {
		if (supportedEncodings == null
				&& (packet.getPacketType() != RfbConstants.CLIENT_SET_ENCODINGS && packet
						.getPacketType() != RfbConstants.CLIENT_SET_PIXEL_FORMAT))
			// Discard all packets if they sent before client set list of
			// supported encodings
			throw new RuntimeException("Too early, can't say is "
					+ packet.dumpToString(false) + " can be accepted or not.");

		return true;
	}

	public int getRedBits() {
		return redBits;
	}

	public int getGreenBits() {
		return greenBits;
	}

	public int getBlueBits() {
		return blueBits;
	}

	public boolean canAcceptRectangleFormat(Rectangle rect) {
		int encodingType = rect.getEncodingType();
		if (encodingType == RfbConstants.ENCODING_RAW)
			return true;// Raw encoding always supported by client (see protocol
						// spec.)

		// If list of supported encodings not set by remote side
		if (supportedEncodings == null)
			return false;// can't accept

		for (int i = 0; i < supportedEncodings.length; i++)
			if (supportedEncodings[i] == encodingType)
				return true;

		return false;
	}

	public boolean isPixelFormatCompatible(Screen screen2) {
		return (screen2.bitsPerPixel == bitsPerPixel)
				&& (screen2.depth == depth)
				&& (screen2.bigEndianFlag == bigEndianFlag)
				&& (screen2.trueColorFlag == trueColorFlag)
				&& (screen2.redMax == redMax) && (screen2.greenMax == greenMax)
				&& (screen2.blueMax == blueMax)
				&& (screen2.redShift == redShift)
				&& (screen2.greenShift == greenShift)
				&& (screen2.blueShift == blueShift);
	}

	public void setPixelFormat(int bitsPerPixel, int depth, int bigEndianFlag,
			int trueColorFlag, int redMax, int greenMax, int blueMax,
			int redShift, int greenShift, int blueShift) {
		this.bitsPerPixel = bitsPerPixel;
		this.depth = depth;
		this.bigEndianFlag = bigEndianFlag;
		this.trueColorFlag = trueColorFlag;
		this.redMax = redMax;
		this.greenMax = greenMax;
		this.blueMax = blueMax;
		this.redShift = redShift;
		this.greenShift = greenShift;
		this.blueShift = blueShift;

		bytesPerPixel = (bitsPerPixel + 7) / 8;
		redBits = maxToBits(redMax);
		greenBits = maxToBits(greenMax);
		blueBits = maxToBits(blueMax);

		resetPalette();
		notifyAboutChange();
	}

	public static int maxToBits(int max) {
		int i = 0;
		for (; max > 0; i++)
			max >>>= 1;
		return i;
	}

	public void setFramebufferSize(int framebufferWidth, int framebufferHeight) {
		this.framebufferWidth = framebufferWidth;
		this.framebufferHeight = framebufferHeight;
		notifyAboutChange();
	}

	public void setDesktopName(String desktopName) {
		this.desktopName = desktopName;
		notifyAboutChange();
	}

	public int getBytesPerPixel() {
		return bytesPerPixel;
	}

	public String getDesktopName() {
		return desktopName;
	}

	public int getBlueShift() {
		return blueShift;
	}

	public int getGreenShift() {
		return greenShift;
	}

	public int getRedShift() {
		return redShift;
	}

	public int getBlueMax() {
		return blueMax;
	}

	public int getGreenMax() {
		return greenMax;
	}

	public int getRedMax() {
		return redMax;
	}

	public int getTrueColorFlag() {
		return trueColorFlag;
	}

	public int getBigEndianFlag() {
		return bigEndianFlag;
	}

	public int getDepth() {
		return depth;
	}

	public int getBitsPerPixel() {
		return bitsPerPixel;
	}

	public int getFramebufferHeight() {
		return framebufferHeight;
	}

	public int getFramebufferWidth() {
		return framebufferWidth;
	}

	public void validate() {
		if (!isValid())
			throw new RuntimeException("Screen is not valid");
	}

	public boolean isValid() {
		return !(framebufferWidth <= 0 || framebufferHeight <= 0
				|| bitsPerPixel <= 0 || bytesPerPixel != (bitsPerPixel + 7) / 8
				|| depth <= 0 || depth > bitsPerPixel || redMax <= 0
				|| redBits <= 0 || redShift < 0
				|| (redShift + redBits) > bitsPerPixel || greenMax <= 0
				|| greenBits <= 0 || greenShift < 0
				|| (greenShift + greenBits) > bitsPerPixel || blueMax <= 0
				|| blueBits <= 0 || blueShift < 0
				|| (blueShift + blueBits) > bitsPerPixel
				|| redBits + greenBits + blueBits > bitsPerPixel
				|| desktopName == null
				|| !(rfbMinorVersion == 3 || rfbMinorVersion == 7)
				|| supportedEncodings == null || (trueColorFlag == 0 && palette == null));
	}

	public void waitForChange(int time) {
		try {
			synchronized (changeNotifier) {
				if (time > 0)
					changeNotifier.wait(time);
				else
					changeNotifier.wait();
			}
		} catch (InterruptedException e) {
		}
	}

	private void notifyAboutChange() {
		synchronized (changeNotifier) {
			changeNotifier.notifyAll();
		}
	}
}
