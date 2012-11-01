package com.ccvnc.test;

import junit.framework.TestSuite;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Test.java
 * 
 * @author Volodymyr M. Lisivka
 */
public class Test {
	@SuppressWarnings("unused")
	private static Logger logger = Logger.getLogger(Test.class);

	public static void main(String[] args) {
		PropertyConfigurator.configure("controller.conf");

		junit.textui.TestRunner.run(suite());
		junit.textui.TestRunner.run(packetsSuite());

		// junit.textui.TestRunner.run(zlibSuite());
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new PipeTest("testPipe"));
		suite.addTest(new ForwarderTest());

		suite.addTest(new HandshakeTest("testServerPtotocolHandshake"));

		suite.addTest(new HandshakeTest("testClientAuthentification_none"));
		suite.addTest(new HandshakeTest("testClientAuthentification_vnc"));

		suite.addTest(new HandshakeTest("testInitialization"));

		suite.addTest(new ControllerTest("testMeeting"));

		suite
				.addTest(new SimpleAuthentificatorTest(
						"testSimpleAuthentificator"));

		suite.addTest(new PortManagerTest("testAssignManyPorts"));
		suite.addTest(new PortManagerTest("testIsPortFree"));
		suite.addTest(new PortManagerTest("testAssignPort"));
		suite.addTest(new TimerTest("testTimer"));

		return suite;
	}

	public static TestSuite zlibSuite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new ZlibTest("testFlush"));
		suite.addTest(new ZlibTest("testDeflater"));
		return suite;
	}

	public static TestSuite packetsSuite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new ScreenTest("testMaxToBits"));
		suite.addTest(new ScreenTest("testPixel24to16"));
		suite.addTest(new ScreenTest("testPixel16to24"));
		suite.addTest(new ScreenTest("testRawRectDepthConversion24to16"));
		suite.addTest(new ScreenTest("testRawRectDepthConversion16to24"));

		suite.addTest(new SessionManagerTest("testPacketPassing"));

		suite.addTest(new PacketsTest("testSetPixelFormatPacket"));
		suite.addTest(new PacketsTest("testSetEncodingPacket"));
		suite.addTest(new PacketsTest(
				"testFramebufferUpdatePacketWithRawEncoding"));
		suite.addTest(new PacketsTest(
				"testFramebufferUpdatePacketWithRREEncoding"));
		suite.addTest(new PacketsTest(
				"testFramebufferUpdatePacketWithCoRREEncoding"));
		suite.addTest(new PacketsTest(
				"testFramebufferUpdatePacketWithHextileEncoding"));
		suite.addTest(new PacketsTest(
				"testFramebufferUpdatePacketWithTightEncoding"));

		// Long and verbose test
		// suite.addTest(new FbsStreamTest("testFbsPlayerAndDumper"));

		return suite;
	}

}
