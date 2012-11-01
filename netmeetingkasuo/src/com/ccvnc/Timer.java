package com.ccvnc;

import java.util.Enumeration;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Timer class used to execute a few repated tasks in one thread.
 * 
 * @author Volodymyr M. Lisivka
 */
@SuppressWarnings("unchecked")
public class Timer implements Runnable {
	private static Logger logger = Logger.getLogger(Timer.class);
	private static Logger errorLogger = Logger
			.getLogger("com.ccvnc.ErrorLogger");

	private boolean alive = false;

	private Vector tasks = new Vector();
	private Vector newTasks = new Vector();

	/**
	 * Move task from list of new tasks to list of active tasks.
	 * 
	 * @param task
	 *            a task
	 * 
	 */
	private void addTaskToListOfActiveTasks(Runnable task) {
		if (!tasks.contains(task)) {
			tasks.add(task);
			logger.debug("Task " + task + " added to list.");
		}
	}

	/**
	 * Move tasks from list of new tasks to list of active tasks.
	 */
	private void addNewTasks() {
		for (Enumeration e = newTasks.elements(); e.hasMoreElements();) {
			Runnable task = (Runnable) e.nextElement();
			addTaskToListOfActiveTasks(task);
			newTasks.remove(task);
		}
	}

	/**
	 * Add task to list of new tasks.
	 * 
	 * @param task
	 *            a task
	 * 
	 */
	public void addTask(Runnable task) {
		if (!tasks.contains(task) && !newTasks.contains(task)) {
			newTasks.add(task);
			logger.debug("Task " + task + " added to list.");
		}
	}

	/**
	 * Remove task from list of tasks.
	 * 
	 * @param task
	 *            a task
	 * 
	 */
	public void removeTask(Runnable task) {
		tasks.remove(task);
		newTasks.remove(task);
		logger.debug("Task " + task + " removed from list.");
	}

	/**
	 * Start a new timer.
	 * 
	 * @param name
	 *            thread name
	 * 
	 * @return a Timer
	 * 
	 */
	public static Timer startNewTimer(String name) {
		Timer timer = new Timer();
		Thread thread = new Thread(timer, name);
		thread.setDaemon(true);
		thread.start();
		return timer;
	}

	/**
	 * Execute all tasks, move new tasks to list of active tasks and sleep up to
	 * one second.
	 */
	public void run() {
		try {
			alive = true;
			for (; alive;) {
				long start = System.currentTimeMillis();
				for (Enumeration e = tasks.elements(); e.hasMoreElements();) {
					Runnable task = (Runnable) e.nextElement();
					try {
						task.run();
					} catch (Throwable ex) {
						errorLogger.error("Error in task " + task, ex);
					}
				}
				// Add new tasks to list of active tasks
				addNewTasks();

				long delay = 1000 - (System.currentTimeMillis() - start);

				if (delay > 0)
					Thread.sleep(delay);// Make a delay up to one second
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Shutdown this timer.
	 */
	public void shutdown() {
		alive = false;
	}
}
