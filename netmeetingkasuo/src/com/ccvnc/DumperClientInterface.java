package com.ccvnc;

import java.io.PrintStream;
import java.util.Properties;

import com.ccvnc.packets.Packet;

/**
 * DumperClientInterface
 * 
 * @author Volodymyr M. Lisivka
 */
public class DumperClientInterface extends ClientInterface {
	private PrintStream out = System.out;

	private boolean verbose;

	public DumperClientInterface(SessionManager session) {
		super(session);
		connectionAlive = true;
	}

	public DumperClientInterface(SessionManager session, PrintStream out,
			boolean verbose) {
		super(session);
		this.out = out;
		this.verbose = verbose;
		connectionAlive = true;
	}

	public DumperClientInterface(SessionManager session, PrintStream out,
			Properties props) {
		super(session);
		this.out = out;
		if (props.getProperty("dumper.verbose") != null)
			verbose = Boolean.valueOf(props.getProperty("dumper.verbose"))
					.booleanValue();

		connectionAlive = true;
	}

	public void handlePacket(Packet packet) {
		// Just dump packet to output
		packet.dump(out, verbose);
	}

	public void runHandlers(boolean atBackground) {
	}

	public void handshake() {
		out.println("Framebuffer geometry: " + screen.getFramebufferWidth()
				+ "x" + screen.getFramebufferHeight());
		out.println("BPP: " + screen.getBitsPerPixel());
		out.println("Depth: " + screen.getDepth());
		out.println("BigEndianFlag: " + screen.getBigEndianFlag());
		out.println("TrueColorFlag: " + screen.getTrueColorFlag());
		out.println("redMax: " + screen.getRedMax());
		out.println("greenMax: " + screen.getGreenMax());
		out.println("blueMax: " + screen.getBlueMax());
		out.println("redShift: " + screen.getRedShift());
		out.println("greenShift: " + screen.getGreenShift());
		out.println("blueShift: " + screen.getBlueShift());
		out.println("Desktop name: " + screen.getDesktopName());

		// Set list of supported encodings - support everything
		screen.setSupportedEncodings(new int[] {
				RfbConstants.ENCODING_COPY_RECT, RfbConstants.ENCODING_TIGHT,
				RfbConstants.ENCODING_ZLIB, RfbConstants.ENCODING_RICH_CURSOR,
				RfbConstants.ENCODING_X_CURSOR, RfbConstants.ENCODING_HEXTILE,
				RfbConstants.ENCODING_CO_RRE, RfbConstants.ENCODING_RRE,
				RfbConstants.ENCODING_RAW });
		setStatus(STATUS_NORMAL);
	}

}
