package com.ccvnc;

import java.util.Properties;

/**
 * Authentificator - interface for authetification system.
 * 
 * @author Volodymyr M. Lisivka
 */
public interface Authentificator {
	/**
	 * Configure this authentificator
	 * 
	 * @param configuration
	 *            application confiuration
	 */
	public void configure(Properties configuration) throws Exception;

	/**
	 * Method validatePassword must throw an security exception if user name or
	 * password is incorrect or access denied.
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
			Properties props) throws SecurityException;
}
