//  Copyright (C) 2003 Volodymyr M. Lisivka.  All Rights Reserved.
//
//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.
//

package com.ccvnc;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.Socket;

/**
 * This class reads data from FBS stream and sends it to VNCViewer with delays.
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unused")
public class FBSSocket extends Socket implements Runnable {

	private DataInputStream fbsSsis, pipedCsis;
	private DataOutputStream pipedCsos;

	private DataInputStream csis, ssis;
	private DataOutputStream csos, ssos;

	private boolean connectionClosed = false;

	public FBSSocket(InputStream is) {
		// Create reader
		fbsSsis = new DataInputStream(new BufferedInputStream(is));
		final PipedOutputStream piped_Ssos = new PipedOutputStream();
		try {
			ssis = new DataInputStream(new PipedInputStream(piped_Ssos));

			PipedInputStream pcsis = new PipedInputStream();
			pipedCsis = new DataInputStream(pcsis);
			csos = new DataOutputStream(new PipedOutputStream(pcsis));

			PipedInputStream pssis = new PipedInputStream();
			csis = new DataInputStream(pssis);
			pipedCsos = new DataOutputStream(new PipedOutputStream(pssis));

		} catch (IOException e) {
		}

		// Read data from recorded stream
		new Thread(this, "FBSSocket").start();
	}

	public void run() {
		try {
			readFbsData(fbsSsis, csis, csos);
		} catch (IOException e) {
		}
	}

	private static void readFbsData(DataInputStream fbsis,
			DataInputStream csis, DataOutputStream os) throws IOException {
		byte[] buf = new byte[64 * 1024];

		long beginTime = System.currentTimeMillis();

		// Skip file header "FBS 001.000\n"
		byte[] headerBuf = new byte[12];
		fbsis.readFully(headerBuf);
		if (new String(headerBuf).indexOf("FBS 001.") != 0)
			throw new RuntimeException("Bad file header");

		while (fbsis.available() >= 0) {
			// Read block
			int chunkLength = fbsis.readInt();

			int blockLength = (chunkLength + 3) & (~0x3);
			if (buf.length < blockLength)
				buf = new byte[blockLength];

			fbsis.readFully(buf, 0, blockLength);

			int timestamp = fbsis.readInt();

			// *DEBUG*/System.out.println("Chunk readed, chunk length:"+chunkLength+", timestamp:"+timestamp);

			waitToTimestamp(beginTime, timestamp);
			while (csis.available() > 0)
				csis.skipBytes(csis.available());
			os.write(buf, 0, chunkLength);
			os.flush();
		}

	}

	private static void waitToTimestamp(long beginTime, int timestamp) {
		long delay = timestamp - (System.currentTimeMillis() - beginTime);
		try {
			if (delay > 0)
				Thread.sleep(delay);
			else
				beginTime -= delay;
		} catch (InterruptedException e) {
		}

	}

	public InputStream getInputStream() {
		return pipedCsis;
	}

	public OutputStream getOutputStream() {
		return pipedCsos;
	}

	public void close() {
		try {
			pipedCsis.close();
		} catch (IOException e) {
		}
		try {
			pipedCsos.close();
		} catch (IOException e) {
		}
		try {
			fbsSsis.close();
		} catch (IOException e) {
		}
	}
}
