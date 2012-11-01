package com.ccvnc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * SimpleAuthetificator - uses list of properties to store user names and
 * passwords.
 * 
 * @author Volodymyr M. Lisivka
 */
public class SimpleAuthentificator implements Authentificator {
	private Properties passwords = new Properties();

	private boolean configured;

	public SimpleAuthentificator() {
		configured = false;
	}

	/**
	 * Configure this authentificator.
	 */
	public void configure(Properties configuration) throws IOException {
		if (!configured) {
			// Get file name with passwords from configuration
			String passwordsFileName = configuration
					.getProperty("SimpleAuthentificator.passwordsFileName");
			if (passwordsFileName == null)
				throw new RuntimeException(
						"Propperty SimpleAuthentificator.passwordsFileName not found in application configuration.");

			// Load passwords
			passwords.load(new FileInputStream(passwordsFileName));
			configured = true;
		}
	}

	/**
	 * Method validatePassword must throw an security exception in case of
	 * illegal access.
	 * 
	 * @param userName
	 *            name of the user
	 * @param password
	 *            user password
	 * @param props
	 *            additional properties from client
	 * 
	 */
	public void validatePassword(String userName, String password,
			Properties props) throws SecurityException {
		if (!configured)
			throw new SecurityException("Authentificator is not configured.");

		if (!(passwords.getProperty(userName) != null && passwords.getProperty(
				userName).equals(password)))
			throw new SecurityException("Invalid password");
	}

}
