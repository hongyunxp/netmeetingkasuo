package com.ccvnc;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ccvnc.packets.Packet;

/**
 * FBSRecorderSocketFactory
 * 
 * @author Volodymyr M. Lisivka
 */
public class FBSRecorderClientInterface extends ClientInterface {

	private static Logger logger = Logger
			.getLogger(FBSRecorderClientInterface.class);

	private long timestamp;

	public FBSRecorderClientInterface(SessionManager session,
			DataOutputStream os) {
		super(session, null, os);
		connectionAlive = true;
	}

	public FBSRecorderClientInterface(SessionManager session,
			DataOutputStream os, Properties props) {
		super(session, null, os);
		connectionAlive = true;
	}

	private void writePacket(Packet packet) {
		// Just write packet to file with timestamp
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream os = new DataOutputStream(bos);
			packet.write(os);
			os.flush();
			writeChunk(bos.toByteArray());
		} catch (IOException e) {
			logger.fatal("Can't store packet:", e);
			closeConnection();
		}
	}

	public void handlePacket(Packet packet) {
		// Just write packet to output
		Packet packetToSend = PacketManager.convertPacket(packet, screen);
		screen = packetToSend.prepareToWrite(screen);
		logger.debug("Writing packet " + packet + " to remote side of " + this
				+ ".");
		writePacket(packetToSend);
	}

	public void runHandlers(boolean atBackground) {
	}

	private void writeChunk(byte[] chunk) throws IOException {
		synchronized (os) {
			os.writeInt(chunk.length);
			os.write(chunk);
			int blockLength = (chunk.length + 3) & (~0x3);
			for (int i = 0; i < (blockLength - chunk.length); i++)
				os.writeByte(0);// Padding
			long delay = (System.currentTimeMillis() - timestamp);
			os.writeInt((int) delay);
			os.flush();
			logger.debug("Chunk written, chunk length:" + chunk.length + "("
					+ blockLength + "), delay=" + delay);
		}
	}

	public void handshake() throws IOException {
		os.write("FBS 001.001\n".getBytes());
		timestamp = System.currentTimeMillis();
		super.handshake();
	}

	public void handshakeProtocol() throws IOException {
		if (screen.getRfbMinorVersion() == 7)
			writeChunk("RFB 003.007\n".getBytes());
		else
			writeChunk("RFB 003.003\n".getBytes());
	}

	public boolean doAuthentification() throws IOException {
		if (screen.getRfbMinorVersion() == 7)
			writeChunk(new byte[] { 1, NO_AUTH });
		else
			writeChunk(new byte[] { 0, 0, 0, NO_AUTH });
		return true;
	}

	public void doInitialization() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream os = new DataOutputStream(bos);

		os.writeShort(screen.getFramebufferWidth());
		os.writeShort(screen.getFramebufferHeight());
		os.write(screen.getBitsPerPixel());
		os.write(screen.getDepth());
		os.write(screen.getBigEndianFlag());
		os.write(screen.getTrueColorFlag());
		os.writeShort(screen.getRedMax());
		os.writeShort(screen.getGreenMax());
		os.writeShort(screen.getBlueMax());
		os.write(screen.getRedShift());
		os.write(screen.getGreenShift());
		os.write(screen.getBlueShift());
		os.write(0);
		os.write(0);
		os.write(0);// Padding
		String desktopName = screen.getDesktopName();
		os.writeInt(desktopName.length());
		os.write(desktopName.getBytes());

		os.flush();
		writeChunk(bos.toByteArray());
		os.close();

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
