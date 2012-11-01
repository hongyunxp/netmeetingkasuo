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

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

/**
 * This class creates instances of the FBSSocket class.
 * 
 * Recognized properties: fbsurl - full URL to FBS stream.
 * 
 * @author Volodymyr M. Lisivka
 */
public class FBSSocketFactory implements SocketFactory {

	public void initialize(Properties properties) {
	}

	/**
	 * Properties: fbsurl - full URL to FBS stream
	 */
	public Socket createSocket(String host, int port, Properties props)
			throws IOException {
		String url = props.getProperty("fbsurl");

		int commaIndex = url.indexOf(',');
		if (commaIndex > 0) {// List of URL's
			String car = url.substring(0, commaIndex);
			String cdr = url.substring(commaIndex + 1);
			props.setProperty("fbsurl", cdr);// Pop first URL from list

			url = car;// Use first URL from list
		} else
			props.remove("fbsurl");// Remove property

		return new FBSSocket(new URL(url).openStream());
	}

	public void shutdownServerSocket(ServerSocket socket, Properties props)
			throws IOException {
		socket.close();
	}

	/**
	 * Properties: fbsurl - full URL to FBS stream or comma separated list of
	 * the URL's.
	 */
	public ServerSocket createServerSocket(int port, Properties props)
			throws IOException {
		return new FBSServerSocket(props.getProperty("fbsurl"));
	}
}
