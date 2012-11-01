package com.ccvnc.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import com.ccvnc.Pipe;
import com.ccvnc.forward.TCPServerForwarder;

/**
 * ForwarderTest
 * 
 * @author Volodymyr M. Lisivka
 */
public class ForwarderTest extends TestCase {
	private static Logger logger = Logger.getLogger(ForwarderTest.class);
	private byte[] inbuf;

	public ForwarderTest() {
		super("Forwarder test");
	}

	public void setUp() {
		inbuf = new byte[64 * 1024];
		for (int i = 0; i < inbuf.length; i++)
			inbuf[i] = (byte) (' ' + ((i % 32) + 32));

		// inbuf="Hello, world!".getBytes();
	}

	public void runTest() throws Throwable {
		ByteArrayInputStream is = new ByteArrayInputStream(inbuf);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		Server s1 = new Server(7771);
		Server s2 = new Server(7772);
		new Thread(s1).start();
		new Thread(s2).start();

		Thread.sleep(1000);

		TCPServerForwarder forwarder = new TCPServerForwarder("127.0.0.1",
				7771, "127.0.0.1", 7772);
		new Thread(forwarder).start();

		Thread.sleep(1000);

		Pipe pipe1 = new Pipe(is, s1.os);
		new Thread(pipe1).start();
		Pipe pipe2 = new Pipe(s2.is, os);
		new Thread(pipe2).start();

		Thread.sleep(1000);

		pipe1.close();
		pipe2.close();
		s1.close();
		s2.close();
		forwarder.close();

		// Compare buffers
		String inbufStr = new String(inbuf);
		String outbufStr = new String(os.toByteArray());
		assertEquals(inbufStr.length(), outbufStr.length());
		assertEquals(inbufStr, outbufStr);
	}

	class Server implements Runnable {
		public InputStream is;
		public OutputStream os;
		public ServerSocket serverSocket;
		public Socket socket;

		public boolean connectionAlive;
		public int port;

		Server(int port) {
			this.port = port;
		}

		public void run() {

			try {
				serverSocket = new ServerSocket(port);
			} catch (IOException e) {
				logger.debug("Can't bind server socket to port " + port, e);
				return;
			}
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				logger.debug(
						"Error when waiting for incomming connection on port "
								+ port, e);
				return;
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
			}
			try {
				is = socket.getInputStream();
				os = socket.getOutputStream();
			} catch (IOException e) {
				logger.debug("Can't get socket stream" + port, e);
				return;
			}

			try {
				connectionAlive = true;
				while (connectionAlive) {
					synchronized (this) {
						wait();
					}
				}
			} catch (InterruptedException e) {
			} finally {
				close();
			}
		}

		public void close() {
			try {
				connectionAlive = false;
				synchronized (this) {
					notify();
				}

				if (socket != null)
					socket.close();
			} catch (IOException e) {
			}
		}
	}
}
