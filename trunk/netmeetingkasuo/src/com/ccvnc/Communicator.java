package com.ccvnc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.ccvnc.packets.Packet;

/**
 * Communicator - contains common code for ServerInterface and ClientInterface.
 * 
 * @author Volodymyr M. Lisivka
 */
public abstract class Communicator implements RfbConstants {
	protected Logger logger = null;
	protected static Logger errorLogger = Logger
			.getLogger("com.ccvnc.ErrorLogger");

	protected DataInputStream is = null;
	protected DataOutputStream os = null;

	protected boolean connectionAlive;

	protected SessionManager session;
	protected Screen screen;
	protected Fifo queue = new Fifo();

	public final static int STATUS_NOT_CONNECTED = 0,
			STATUS_HANDSHAKING_PROTOCOL = 1, STATUS_AUTHENTIFICATION = 2,
			STATUS_INITIALIZATION = 3, STATUS_INITIALIZATION_CONTINUED = 4,
			STATUS_NORMAL = 5;
	protected int status = STATUS_NOT_CONNECTED;
	private Object statusChangeNotifier = new Object();

	// Used to protect from reading and writing at same time
	protected Object readWriteLock = new Object();

	public Communicator(SessionManager session, DataInputStream is,
			DataOutputStream os) {
		createLogger();

		this.is = is;
		this.os = os;
		this.session = session;
		connectionAlive = (is != null || os != null);
	}

	public Communicator(SessionManager session, Screen screen,
			DataInputStream is, DataOutputStream os) {
		createLogger();

		this.screen = screen;
		this.session = session;
		connectionAlive = (is != null || os != null);
	}

	private void createLogger() {
		logger = Logger.getLogger(this.getClass());
	}

	public void setStatus(int status) {
		this.status = status;
		synchronized (statusChangeNotifier) {
			statusChangeNotifier.notifyAll();
		}
	}

	public int getStatus() {
		return status;
	}

	public void waitForStatus(int status) {
		try {
			for (; connectionAlive && this.status < status;) {
				synchronized (statusChangeNotifier) {
					statusChangeNotifier.wait(100);
				}
			}
		} catch (InterruptedException e) {
		}
	}

	public void setOutputStream(DataOutputStream os) {
		if (this.os != null)
			throw new RuntimeException("Output stream already setted.");
		this.os = os;
		connectionAlive = (is != null || os != null);
	}

	public DataOutputStream getOutputStream() {
		return os;
	}

	public void setInputStream(DataInputStream is) {
		if (this.is != null)
			throw new RuntimeException("Input stream already setted.");
		this.is = is;
		connectionAlive = (is != null || os != null);
	}

	public DataInputStream getInputStream() {
		return is;
	}

	public static byte[] encodePassword(byte[] challenge, String password) {
		if (password.length() > 8)
			password = password.substring(0, 8); // Truncate to 8 chars

		// vncEncryptBytes in the UNIX libvncauth truncates password
		// after the first zero byte. We do to.
		int firstZero = password.indexOf(0);
		if (firstZero != -1)
			password = password.substring(0, firstZero);

		byte[] key = { 0, 0, 0, 0, 0, 0, 0, 0 };
		System.arraycopy(password.getBytes(), 0, key, 0, password.length());
		byte[] response = new byte[16];
		System.arraycopy(challenge, 0, response, 0, challenge.length);

		DesCipher des = new DesCipher(key);

		des.encrypt(response, 0, response, 0);
		des.encrypt(response, 8, response, 8);
		return response;
	}

	public static void fillWithRandomData(byte[] challenge) {
		for (int i = 0; i < challenge.length; i++)
			challenge[i] = (byte) (Math.random() * 256);
	}

	public static boolean compareArrays(byte[] array1, byte[] array2) {
		if (array1 == null || array2 == null)
			return false;

		if (array1.length != array2.length)
			return false;

		for (int i = 0; i < array1.length; i++)
			if (array1[i] != array2[i])
				return false;

		return true;
	}

	/**
	 * Start handlers for incomming and outcomming traffic.
	 * 
	 * @param atBackground
	 *            if true, then run both handlers in seprate threads and return
	 *            immediately. If false, then wait until
	 *            handleIncommingTraffic() will finished.
	 * 
	 */
	public void runHandlers(boolean atBackground) {
		setStatus(STATUS_INITIALIZATION_CONTINUED);
		new Thread(new Runnable() {
			public void run() {
				handleOutcommingTraffic();
			}
		}, "" + this + ".handleOutcommingTraffic()").start();

		if (atBackground)
			new Thread(new Runnable() {
				public void run() {
					handleIncommingTraffic();
				}

			}, "" + this + ".handleIncommingTraffic()").start();
		else
			handleIncommingTraffic();

	}

	/**
	 * Read packet from input stream and send to SessionManager.
	 */
	protected void handleIncommingTraffic() {
		try {
			while (connectionAlive) {
				Packet packet = readPacket();
				if (packet != null) {
					handleIncommingPacket(packet);
					notifyAboutUpdate();// Packet is readed and handled, we may
					// want to send data to remote side
				}
			}
		} catch (Exception e) {
			if (connectionAlive)
				errorLogger.debug("Exception in incoming loop in " + this, e);
		} finally {
			closeConnection();
		}
	}

	public abstract void handleIncommingPacket(Packet packet);

	/**
	 * Get packets from queue and send to remote host.
	 */
	protected void handleOutcommingTraffic() {
		try {
			while (connectionAlive) {
				waitForUpdate();
				writePendingPackets();
			}
		} catch (Exception e) {
			if (connectionAlive)
				errorLogger.debug("Exception in outcoming loop in " + this, e);
		} finally {
			closeConnection();
		}
	}

	public void writePendingPackets() throws IOException {
		while (queue.size() > 0 && connectionAlive) {
			Packet packet = getNextPacketToSend();
			if (packet == null)
				break;

			// Don't send and receive data at same time
			synchronized (readWriteLock) {
				Packet packetToSend;

				synchronized (screen)// Don't allow to change screen settings
				{
					packet = filterPacketBeforeWrite(packet);
					if (packet == null)
						return;

					// Convert packets before sending
					packetToSend = PacketManager.convertPacket(packet, screen);

					if (packetToSend == null)
						throw new RuntimeException(
								"Can't convert packet to another format.");

					screen = packetToSend.prepareToWrite(screen);
				}

				if (os != null) {
					logger.debug("Writing packet " + packet
							+ " to remote side.");
					logPacket(packet, true, false);
					packetToSend.write(os);
					os.flush();
				}
			}
		}
	}

	protected abstract Packet filterPacketBeforeWrite(Packet packet);

	protected abstract Packet filterPacketAfterRead(Packet packet);

	protected abstract Packet getNextPacketToSend();

	public void logPacket(Packet packet, boolean writing, boolean verbose) {
		// if (false) {
		// ByteArrayOutputStream bos = new ByteArrayOutputStream();
		// PrintStream out = new PrintStream(bos);
		// packet.dump(out, verbose);
		// out.flush();
		// logger.debug("" + this + ": " + ((writing) ? "writing" : "readed")
		// + " " + packet + "\n" + new String(bos.toByteArray()));
		// }
	}

	private void waitForUpdate() {
		synchronized (queue) {
			try {
				queue.wait(100);
			} catch (InterruptedException e) {
			}
		}
	}

	public Fifo getQueue() {
		return queue;
	}

	/**
	 * Put new packet to send into outgoing queue.
	 * 
	 * @param packet
	 *            a Packet to send
	 * 
	 */
	public void handlePacket(Packet packet) {
		addToQueue(packet);
	}

	public void addToQueue(Packet packetToSend) {
		try {
			queue.add(packetToSend);
			notifyAboutUpdate();
		} catch (Exception e) {
			errorLogger.warn("Queue overloaded, clearing queue.");
			queue.clear();// TODO: what better - clear queue or close
			// connection?
			queue.add(packetToSend);
		}
	}

	private void notifyAboutUpdate() {
		synchronized (queue) {
			queue.notifyAll();
		}
	}

	public int getQueueSize() {
		return queue.size();
	}

	public void setScreen(Screen screen) {
		this.screen = screen;
	}

	public SessionManager getSession() {
		return session;
	}

	public Screen getScreen() {
		return screen;
	}

	public Packet readPacket() throws IOException {
		Packet packet;
		int messageType = is.readUnsignedByte();
		synchronized (readWriteLock) {
			packet = getPacketHandler(messageType);
			packet.setScreen(screen);
			packet.readPacketData(is);
			logger.debug("This " + this + " readed " + packet
					+ " and doing postprocessing for " + screen);
			logPacket(packet, false, false);

			synchronized (screen) {
				screen = packet.postProcessAfterReading(screen);
			}
		}
		packet.validate();
		return filterPacketAfterRead(packet);
	}

	public abstract Packet getPacketHandler(int messageType) throws IOException;

	public void closeConnection() {
		connectionAlive = false;
		setStatus(STATUS_NOT_CONNECTED);
		try {
			if (is != null)
				is.close();
		} catch (Exception e) {
		}
		is = null;
		try {
			if (os != null)
				os.close();
		} catch (Exception e) {
		}
		os = null;

	}

}
