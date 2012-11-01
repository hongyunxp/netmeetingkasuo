package com.ccvnc.test;

/**
 * TimerTest
 *
 * @author Volodymyr M. Lisivka
 */
import junit.framework.TestCase;

import com.ccvnc.Timer;

public class TimerTest extends TestCase {

	public TimerTest(String method) {
		super(method);
	}

	private int counter1, counter2;
	private Runnable task1, task2;

	public void testTimer() throws Throwable {
		final Timer timer = Timer.startNewTimer("Test timer");

		counter1 = 0;
		counter2 = 0;
		task1 = new Runnable() {
			public void run() {
				counter1++;
				if (counter1 >= 5)
					timer.removeTask(task1);
			}
		};
		timer.addTask(task1);

		task2 = new Runnable() {
			public void run() {
				counter2++;
				if (counter2 >= 5)
					timer.removeTask(task2);
			}
		};
		timer.addTask(task2);

		Thread.sleep(500);
		assertTrue("Timer too fast", counter1 == 0 && counter2 == 0);

		Thread.sleep(2 * 1000);
		assertTrue("Timer too fast", counter1 < 5 && counter2 < 5);

		Thread.sleep(5 * 1000);
		assertTrue("Timer too slow", counter1 == 5 && counter2 == 5);

		// timer.shutdown();
	}
}
