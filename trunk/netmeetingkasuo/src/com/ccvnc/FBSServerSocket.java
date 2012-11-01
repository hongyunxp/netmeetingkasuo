package com.ccvnc;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

/**
 * Returns instance of FBSSocket.
 * 
 * @author Volodymyr M. Lisivka
 */
public class FBSServerSocket extends ServerSocket {
	private String url;

	public FBSServerSocket(String url) throws IOException {
		this.url = url;
	}

	public String rotateUrlList() {
		int commaIndex = url.indexOf(',');
		if (commaIndex > 0) {// List of URL's
			String car = url.substring(0, commaIndex);
			String cdr = url.substring(commaIndex + 1);
			url = cdr + "," + car;// Rotate list of URL's

			return car;// Use first URL from list
		} else
			return url;
	}

	/**
	 * Create new instance of FBSSocket.
	 * 
	 * @return a new FBSSocket
	 * 
	 * @exception IOException
	 * 
	 */
	public Socket accept() throws IOException {
		return new FBSSocket(new URL(rotateUrlList()).openStream());
	}

}
