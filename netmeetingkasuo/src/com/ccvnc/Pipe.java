package com.ccvnc;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

/**
 * Simple pipe to forward traffic from input stream to output stream.
 * 
 * @author Volodymyr M. Lisivka
 */
public class Pipe implements Runnable {
	private static Logger logger = Logger.getLogger(Pipe.class);

	private InputStream is;
	private OutputStream os;
	private Pipe neighbour;

	public Pipe(InputStream is, OutputStream os) {
		if (is == null)
			throw new NullPointerException();
		this.is = is;
		this.os = os;
	}

	public Pipe(InputStream is, OutputStream os, Pipe neighbour) {
		if (is == null)
			throw new NullPointerException();

		this.is = is;
		this.os = os;
		this.neighbour = neighbour;
		neighbour.setNeighbour(this);
	}

	public void setNeighbour(Pipe neighbour) {
		this.neighbour = neighbour;
	}

	public Pipe getNeighbour() {
		return neighbour;
	}

	/**
	 * Forwards packets from input stream to output stream.
	 */
	public void run() {
		try {
			byte[] buf = new byte[16 * 1024];
			// available() must throw exception if connection was closed
			for (; is != null && is.available() >= 0;) {
				int length = is.read(buf, 0, buf.length);
				if (length > 0 && os != null)
					os.write(buf, 0, length);
			}
		} catch (Exception e) {
			logger.debug("IOException in Pipe: ", e);
		} finally {
			close();
			// Close opposite connection too
			if (neighbour != null) {
				neighbour.close();
				neighbour = null;
			}
		}
	}

	/**
	 * Close this pipe and it neighbour.
	 */
	public void close() {
		if (is != null)
			try {
				is.close();
				is = null;
			} catch (Exception e) {
			}

		if (os != null)
			try {
				os.close();
				os = null;
			} catch (Exception e) {
			}
	}

}
