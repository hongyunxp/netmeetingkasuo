package com.ccvnc.forward;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.ccvnc.Pipe;

/**
 * TCPServerForwarder
 * 
 * @author Volodymyr M. Lisivka
 */
public class TCPServerForwarder implements Runnable {
	private Socket server, client;
	private InputStream ssis, csis;
	private OutputStream ssos, csos;
	private Pipe p1, p2;

	public TCPServerForwarder(String fromHost, int fromPort, String toHost,
			int toPort) throws IOException {
		server = new Socket(fromHost, fromPort);
		client = new Socket(toHost, toPort);

		ssis = server.getInputStream();
		csis = client.getInputStream();

		ssos = server.getOutputStream();
		csos = client.getOutputStream();
	}

	/**
	 * Method close
	 * 
	 */
	public void close() {
		p1.close();
		p2.close();
		try {
			server.close();
		} catch (IOException e) {
		}
		try {
			client.close();
		} catch (IOException e) {
		}
	}

	public void run() {
		p1 = new Pipe(ssis, csos);
		p2 = new Pipe(csis, ssos, p1);

		new Thread(p1, "Forwarder").start();
		p2.run();
		close();
	}

	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("Usage:\n  java "
					+ TCPServerForwarder.class.getName()
					+ " FROM_HOST FROM_PORT TO_HOST TO_PORT");
			System.exit(0);
		}

		try {
			String fromHost = args[0];
			int fromPort = Integer.parseInt(args[1]);

			String toHost = args[2];
			int toPort = Integer.parseInt(args[3]);

			new TCPServerForwarder(fromHost, fromPort, toHost, toPort).run();
		} catch (NumberFormatException e) {
			System.out.println("Bad port number format: "
					+ e.getLocalizedMessage());
		} catch (IOException e) {
			System.out.println("Unable to connect: " + e.getLocalizedMessage());
		}
	}
}
