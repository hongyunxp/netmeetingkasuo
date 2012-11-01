package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;

import com.ccvnc.RfbIOTools;
import com.ccvnc.Screen;

/**
 * TightRect
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class TightRect extends AbstractRectangle {
	protected static Logger errorLogger = Logger
			.getLogger("com.ccvnc.ErrorLogger");

	// Use one Hashtable to keep _all_ inflaters using thread as key
	// TODO: find a better solution

	private static Hashtable inflaters = new Hashtable();

	private int subencoding;
	private byte[] palette;
	private int numberOfColours = 0;
	private int filterId;
	private byte[] data;

	public void setSubencoding(int subencoding) {
		this.subencoding = subencoding;
	}

	public int getSubencoding() {
		return subencoding;
	}

	public void setPalette(byte[] palette) {
		this.palette = palette;
	}

	public byte[] getPalette() {
		return palette;
	}

	public void setNumberOfColours(int numberOfColours) {
		this.numberOfColours = numberOfColours;
	}

	public int getNumberOfColours() {
		return numberOfColours;
	}

	public void setFilterId(int filterId) {
		this.filterId = filterId;
	}

	public int getFilterId() {
		return filterId;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void validate() {
		super.validate();

		int bpp = getTightBPP(screen);

		// Check correctness of subencoding value.
		if (subencoding > TIGHT_MAX_SUBENCODING)
			throw new RuntimeException("Incorrect tight subencoding: "
					+ subencoding);

		if (subencoding == SUBENCODING_TIGHT_FILL) {
			if (data.length != bpp)
				throw new RuntimeException(
						"Incorrect length of fill colour buffer: "
								+ dumpToString(true));
			return;
		}

		if (subencoding == SUBENCODING_TIGHT_JPEG)
			return;

		if ((subencoding & SUBENCODING_TIGHT_EXPLICIT_FILTER) != 0
				&& (filterId == SUBENCODING_TIGHT_FILTER_PALETTE && (palette == null
						|| numberOfColours <= 0 || palette.length != numberOfColours
						* bpp)))
			throw new RuntimeException("Incorrect palette: "
					+ dumpToString(true));

		if (numberOfColours < 0 || numberOfColours > 256)
			throw new RuntimeException("Incorrect numColours value: "
					+ dumpToString(true));

		if (numberOfColours == 0 && data.length != height * width * bpp)
			throw new RuntimeException(
					"Incorrect rawcolour data size, expects "
							+ (height * width * bpp) + ": "
							+ dumpToString(true));
		else if (numberOfColours == 2
				&& data.length != height * ((width + 7) / 8))
			throw new RuntimeException(
					"Incorrect monocolour data size, expects "
							+ (height * ((width + 7) / 8)) + ": "
							+ dumpToString(true));
		else if (numberOfColours != 0 && numberOfColours != 2
				&& data.length != height * width)
			throw new RuntimeException(
					"Incorrect indexed colour data size, expects "
							+ (height * width) + ": " + dumpToString(true));

	}

	public void dump(PrintStream out, boolean verbose) {
		out.print("TightRect ");
		super.dump(out, verbose);

		if (subencoding == SUBENCODING_TIGHT_FILL) {
			out.print("Subencoding: fill, data="
					+ RfbIOTools.arrayToString(data));
			return;
		}

		if (subencoding == SUBENCODING_TIGHT_JPEG) {
			out.println("Subencoding: jpeg, data.length=" + data.length);
			return;
		}

		if ((subencoding & SUBENCODING_TIGHT_EXPLICIT_FILTER) != 0) {

			out.print("Subencoding: explicit filter: ");
			if (filterId == SUBENCODING_TIGHT_FILTER_PALETTE)
				out.print("pallette, numColours=" + numberOfColours + ". ");
			else if (filterId == SUBENCODING_TIGHT_FILTER_GRADIENT)
				out.print("gradient. ");
			else if (filterId == SUBENCODING_TIGHT_FILTER_COPY)
				out.print("copy. ");
			else
				out.print("UNKNOWN(" + filterId + ")! ");
		}

		if (data.length < TIGHT_MIN_TO_COMPRESS)
			// Data size is small - not compressed with zlib.
			out.println("Raw data, length=" + data.length);
		else
			// Data was compressed with zlib.
			out.println("ZLIB compressed data, length=" + data.length);

		if (verbose)
			out.print("data: " + RfbIOTools.arrayToString(data));
	}

	public void writeRectangleData(DataOutputStream os) throws IOException {
		if (subencoding == SUBENCODING_TIGHT_FILL
				|| subencoding == SUBENCODING_TIGHT_JPEG)
			os.writeByte(subencoding << 4);// Send data exactly as received.
		else
			os.writeByte(subencoding << 4 | 0x0F);// Tell the decoder to flush
		// each of the four zlib
		// streams.

		// Handle solid-color rectangles.
		if (subencoding == SUBENCODING_TIGHT_FILL) {
			os.write(data);
			return;
		}

		if (subencoding == SUBENCODING_TIGHT_JPEG) {
			RfbIOTools.writeCompactLength(os, data.length);
			os.write(data);
			return;
		}

		if ((subencoding & SUBENCODING_TIGHT_EXPLICIT_FILTER) != 0) {
			os.writeByte(filterId);
			if (filterId == SUBENCODING_TIGHT_FILTER_PALETTE) {
				os.writeByte(numberOfColours - 1);
				os.write(palette);
			}
		}

		// Read, optionally uncompress and decode data.
		if (data.length < TIGHT_MIN_TO_COMPRESS)
			// Data size is small - do not compress with zlib.
			os.write(data);
		else
			// Compress with zlib.
			writeCompressedData(os, data);
	}

	public void readRectangleData(DataInputStream is) throws IOException {
		int bpp = getTightBPP(screen);

		subencoding = is.readUnsignedByte();

		// Flush zlib streams if we are told by the server to do so.
		for (int stream_id = 0; stream_id < 4; stream_id++) {
			if ((subencoding & 1) != 0)
				setInflater(stream_id, null);
			subencoding >>= 1;
		}

		// Check correctness of subencoding value.
		if (subencoding > TIGHT_MAX_SUBENCODING)
			throw new RuntimeException("Incorrect tight subencoding: "
					+ subencoding);

		// Handle solid-color rectangles.
		if (subencoding == SUBENCODING_TIGHT_FILL) {
			data = new byte[bpp];
			is.readFully(data);
		} else if (subencoding == SUBENCODING_TIGHT_JPEG) {
			// Read JPEG data.
			data = new byte[RfbIOTools.readCompactLength(is)];
			is.readFully(data);
		} else {
			// Read filter id and parameters.
			numberOfColours = 0;
			if ((subencoding & SUBENCODING_TIGHT_EXPLICIT_FILTER) != 0) {
				filterId = is.readUnsignedByte();
				if (filterId == SUBENCODING_TIGHT_FILTER_PALETTE) {
					numberOfColours = is.readUnsignedByte() + 1;
					palette = new byte[numberOfColours * bpp];
					is.readFully(palette);
				} else if (filterId != SUBENCODING_TIGHT_FILTER_GRADIENT
						&& filterId != SUBENCODING_TIGHT_FILTER_COPY)
					throw new RuntimeException("Incorrect tight filter id: "
							+ filterId);
			}

			int rowSize;
			if (numberOfColours == 0)
				rowSize = width * bpp;
			else if (numberOfColours == 2)
				rowSize = (width + 7) / 8;
			else
				rowSize = width;

			// Read, optionally uncompress and decode data.
			int dataSize = height * rowSize;
			if (dataSize < TIGHT_MIN_TO_COMPRESS) {
				// Data size is small - not compressed with zlib.
				if (numberOfColours != 0)
					data = new byte[dataSize];// Indexed colors.
				else
					data = new byte[width * height * bpp];

				is.readFully(data);
			} else {
				// Data was compressed with zlib.
				byte[] zlibData = new byte[RfbIOTools.readCompactLength(is)];
				is.readFully(zlibData);
				int stream_id = subencoding & 0x03;

				if (getInflater(stream_id) == null)
					setInflater(stream_id, new Inflater());

				Inflater myInflater = getInflater(stream_id);
				myInflater.setInput(zlibData);
				data = new byte[dataSize];
				try {
					myInflater.inflate(data);
				} catch (DataFormatException e) {
					throw new IOException(e.toString());
				}
			}
		}
	}

	/**
	 * TightVNC uses 3 bytes for colours in case of 32 bits per pixel and 24
	 * bits depth.
	 */
	private int getTightBPP(Screen screen) {
		int bpp = screen.getBytesPerPixel();
		if (bpp == 4 && screen.getDepth() == 24)
			bpp = 3;
		return bpp;
	}

	public Rectangle convertPixelFormat(Screen screen2) {
		int bpp = getTightBPP(screen);
		int bpp2 = getTightBPP(screen2);
		TightRect rect2 = new TightRect();
		rect2.setScreen(screen2);
		rect2.setHeaderParameters(this);

		rect2.subencoding = subencoding;

		if (subencoding == SUBENCODING_TIGHT_FILL) {
			// Colour
			rect2.data = convertRawPixelData(data, screen2, bpp, bpp2);
		} else if (subencoding == SUBENCODING_TIGHT_JPEG) {
			// JPEG data. VNC viewer can't handle JPEG data in 8bits/pixel mode
			rect2.data = data;
			if (screen2.getBytesPerPixel() == 1)
				errorLogger
						.warn("TightVNC viewer may not display JPEG data in 8bits/pixel mode.");
		} else {
			rect2.numberOfColours = numberOfColours;

			if ((subencoding & SUBENCODING_TIGHT_EXPLICIT_FILTER) != 0) {
				rect2.filterId = filterId;
				if (filterId == SUBENCODING_TIGHT_FILTER_PALETTE)
					rect2.palette = convertRawPixelData(palette, screen2, bpp,
							bpp2);
			}

			if (numberOfColours != 0)
				// Indexed colours, conversion not needed (palette already
				// converted)
				rect2.data = data;
			else
				// "Gradient"-filtered data or truecolour data
				rect2.data = convertRawPixelData(data, screen2, bpp, bpp2);
		}

		return rect2;
	}

	private static Inflater getInflater(int stream_id) {
		Vector inflatersVector = (Vector) inflaters.get(Thread.currentThread());
		if (inflatersVector == null)
			return null;
		else
			return (Inflater) inflatersVector.elementAt(stream_id);
	}

	private static void setInflater(int stream_id, Inflater inflater) {
		Vector inflatersVector = (Vector) inflaters.get(Thread.currentThread());
		if (inflatersVector == null) {
			inflatersVector = new Vector(4);
			for (int i = 0; i < 4; i++)
				inflatersVector.addElement(null);
			inflaters.put(Thread.currentThread(), inflatersVector);
		}
		inflatersVector.setElementAt(inflater, stream_id);
	}

	/**
	 * Compress and write the data into the recorded session file. This method
	 * assumes the recording is on (rec != null).
	 */
	private void writeCompressedData(DataOutputStream os, byte[] data)
			throws IOException {
		Deflater deflater = new Deflater();
		deflater.setInput(data);
		int bufSize = data.length + data.length / 100 + 12;
		byte[] buf = new byte[bufSize];
		deflater.finish();
		int compressedSize = deflater.deflate(buf);
		RfbIOTools.writeCompactLength(os, compressedSize);
		os.write(buf, 0, compressedSize);
	}

}
