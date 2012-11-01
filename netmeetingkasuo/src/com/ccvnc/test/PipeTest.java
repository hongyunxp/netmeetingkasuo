package com.ccvnc.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.ccvnc.Pipe;

/**
 * PipeTest
 * 
 * @author Volodymyr M. Lisivka
 */
public class PipeTest extends TestCase {
	private byte[] inbuf;

	public PipeTest(String method) {
		super(method);
	}

	public void setUp() {
		// inbuf=new byte[1024];
		// for(int i=0;i<inbuf.length;i++)
		// inbuf[i]=(byte)(Math.random()*256);
		inbuf = "Hello, world!".getBytes();
	}

	public void testPipe() throws Throwable {
		ByteArrayInputStream is = new ByteArrayInputStream(inbuf);
		ByteArrayOutputStream os = new ByteArrayOutputStream();

		Pipe pipe = new Pipe(is, os);
		new Thread(pipe).start();

		Thread.sleep(2);

		pipe.close();

		// Compare buffers
		String inbufStr = new String(inbuf);
		String outbufStr = new String(os.toByteArray());
		assertEquals(inbufStr, outbufStr);
	}
}
