package com.ccvnc.test;

import java.io.ByteArrayOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import junit.framework.TestCase;

/**
 * ZlibTest.java
 * 
 * @author Volodymyr M. Lisivka
 */
public class ZlibTest extends TestCase {
	public ZlibTest(String method) {
		super(method);
	}

	public void testFlush() throws Throwable {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		Deflater deflater = new Deflater(3, true);
		DeflaterOutputStream zos = new DeflaterOutputStream(bos, deflater);

		for (int i = 0; i < 10; i++) {
			// Discard previous data
			zos.flush();
			bos.reset();

			// Write new data
			zos.write(1);
			zos.flush();

			// Verify, if new data received
			assertTrue(bos.size() > 0);
		}

	}

	public void testDeflater() throws Throwable {
		Deflater deflater = new Deflater(3, true);
		deflater.setInput(new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3,
				4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4,
				5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5,
				6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6,
				7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7,
				8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8,
				9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
				0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0,
				1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1,
				2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2,
				3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3,
				4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, });
		byte[] buf = new byte[1024];
		int len = deflater.deflate(buf);
		assertTrue("no output", len > 0);
		assertFalse("needs more input", deflater.needsInput());

	}
}
