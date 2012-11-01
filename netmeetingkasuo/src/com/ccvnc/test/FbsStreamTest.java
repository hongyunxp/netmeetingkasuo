package com.ccvnc.test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import junit.framework.TestCase;

import com.ccvnc.DumperClientInterface;
import com.ccvnc.FBSRecorderClientInterface;
import com.ccvnc.SessionManager;

/**
 * FbsStreamTest
 * 
 * @author Volodymyr M. Lisivka
 */
public class FbsStreamTest extends TestCase {
	public FbsStreamTest(String method) {
		super(method);
	}

	public void testFbsPlayerAndDumper() throws Throwable {
		Properties props = new Properties();
		props.setProperty("serverSocketFactoryClass",
				"com.ccvnc.FBSSocketFactory");
		SessionManager session = new SessionManager("test", "test", props);
		session.setClientListenerActive(true);

		DumperClientInterface dumper = new DumperClientInterface(session,
				System.out, false);
		session.handleClientConnection(dumper, props);

		File recorderFile = new File("recorded.fbs");
		recorderFile.delete();
		FileOutputStream recorderFos = new FileOutputStream(recorderFile);
		FBSRecorderClientInterface recorder = new FBSRecorderClientInterface(
				session, new DataOutputStream(recorderFos));
		session.handleClientConnection(recorder, props);

		props.clear();
		// Saved session number, encodings - status
		// 1 - tight - ok (long)
		// 2 - tight - ok (long)
		// 3 - XCursor, raw - bad (at all?)
		// 4 - raw - bad (at all?)
		// 5 - zlib, raw - ok
		// 6 - hextile - ok
		// 7 - corre, raw, copyrect - ok
		// 8 - rre, raw, copyrect - ok (long)
		// 9 - tight - ok
		// 10 - tight - ok
		// 11 - hextile - bad
		props
				.setProperty("fbsurl",
						"file:///c:/vncsession.fbs.010");
		session.connectToServer("unused", 5901, "unused", props);
		session.waitForServer(-1);

		// Shutdown recorder and player
		props.clear();
		session.shutdownServerInterface(props);
		recorder.closeConnection();

		// Validate recorded stream
		recorderFos.flush();
		recorderFos.close();
		assertTrue("File was not recorded", recorderFile.exists());
		assertTrue("File was not recorded", recorderFile.length() != 0);

		// Validate recorded stream
		props.setProperty("fbsurl",
				"file:///c:/recorded.fbs");
		session.connectToServer("unused", 5901, "unused", props);
		session.waitForServer(-1);

		props.clear();
		session.shutdownServerInterface(props);
		session.shutdownClientInterfaces(props);

	}
}
