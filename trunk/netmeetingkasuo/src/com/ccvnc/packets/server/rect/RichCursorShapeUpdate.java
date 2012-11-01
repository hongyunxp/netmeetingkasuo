package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;

/**
 * RichCursorShapeUpdate - new cursor shape to draw locally by client.
 * 
 * Rectangle x and y positions indicate cursor hotspot. Data consists of the
 * width*height pixels of the cursor shape image folowed by cursor bitmask.
 * 
 * @author Volodymyr M. Lisivka
 */
public class RichCursorShapeUpdate extends AbstractRectangle {
	private byte[] maskData;
	private byte[] imageData;

	public void setMaskData(byte[] maskData) {
		this.maskData = maskData;
	}

	public byte[] getMaskData() {
		return maskData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		if (width * height != 0) {
			os.write(imageData);
			os.write(maskData);
		}
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		if (width * height == 0)
			return;

		// Read cursor shape data
		int imageDataLength = width * height * screen.getBytesPerPixel();
		imageData = new byte[imageDataLength];

		is.readFully(imageData);
		// Read cursor mask data
		int bytesPerRow = (width + 7) / 8;
		int maskDataLength = bytesPerRow * height;
		maskData = new byte[maskDataLength];
		is.readFully(maskData);

	}

	/**
	 * Convert cursor image to another pixel format.
	 */
	public Rectangle convertPixelFormat(Screen screen2) {
		RichCursorShapeUpdate cursor2 = new RichCursorShapeUpdate();
		cursor2.setScreen(screen2);
		cursor2.setHeaderParameters(this);

		if (maskData != null)
			cursor2.setMaskData(maskData);

		if (imageData != null)
			cursor2.setImageData(convertRawPixelData(imageData, screen2));

		return cursor2;
	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("RichCursorShapeUpdate ");
		super.dump(out, verbose);
		if (verbose) {
			if (maskData != null)
				out.println("MaskData: length=" + maskData.length + "\n"
						+ RfbIOTools.arrayToString(maskData));
			if (imageData != null)
				out.println("ImageData: length=" + imageData.length + "\n"
						+ RfbIOTools.arrayToString(imageData));
		}
	}

	public void validate() {
		if (screen == null || x < 0 || y < 0 || width < 0 || height < 0)
			throw new RuntimeException("RichCursorShapeUpdate is not valid: "
					+ dumpToString(true));

		if (width * height != 0
				&& (maskData == null
						|| maskData.length != ((width + 7) / 8) * height
						|| imageData == null || imageData.length != width
						* height * screen.getBytesPerPixel()))
			throw new RuntimeException(
					"RichCursorShapeUpdate is not valid, wrong data: "
							+ dumpToString(true));
	}

}
