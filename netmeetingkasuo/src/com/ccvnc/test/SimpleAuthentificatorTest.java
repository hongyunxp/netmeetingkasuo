package com.ccvnc.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import junit.framework.TestCase;

import com.ccvnc.SimpleAuthentificator;

/**
 * SimpleAuthentificatorTest
 * 
 * @author Volodymyr M. Lisivka
 */
public class SimpleAuthentificatorTest extends TestCase {
	public SimpleAuthentificatorTest(String method) {
		super(method);
	}

	public void testSimpleAuthentificator() throws Throwable {
		SimpleAuthentificator authentificator = new SimpleAuthentificator();
		Properties props = new Properties();

		Properties password = new Properties();
		password.setProperty("test user", "right pass");

		String tmpFileName = File.createTempFile("satest", ".properties")
				.getName();
		File tmpFile = new File(tmpFileName);
		password.store(new FileOutputStream(tmpFile), "Test file");
		tmpFile.deleteOnExit();

		Properties configuration = new Properties();
		configuration.setProperty("SimpleAuthentificator.passwordsFileName",
				tmpFileName);

		authentificator.configure(configuration);
		authentificator.validatePassword("test user", "right pass", props);

		boolean ok = false;
		try {
			authentificator.validatePassword("test user", "wrong pass", props);
		} catch (SecurityException e) {
			ok = true;
		}
		if (!ok)
			fail("SimpleAuthentificator bypass wrong passwords");
	}
}
