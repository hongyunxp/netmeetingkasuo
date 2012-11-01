package com.ccvnc;

import java.util.Properties;

/**
 * NullAuthentificator.java
 * 
 * @author Volodymyr M. Lisivka
 */
public class NullAuthentificator implements Authentificator {

	public void validatePassword(String userName, String password,
			Properties props) {
		// Nothing to do
	}

	public void configure(Properties configuration) {
		// Nothing to do
	}

}
