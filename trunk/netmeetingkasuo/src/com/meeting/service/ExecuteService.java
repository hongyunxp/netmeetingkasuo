package com.meeting.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;

import com.meeting.dao.ConfigDao;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;

/**
 * 外部文件执行服务类
 * 
 * @author zcg
 * 
 */
@SuppressWarnings("unused")
public class ExecuteService {

	private static final Logger log = Logger.getLogger(ExecuteService.class);

	public static final String KEY_PROCESS = "process";

	public static final String KEY_EXIT_VALUE = "exitValue";

	public static final String KEY_EXCEPTION = "exception";

	public static final String KEY_ERROR = "error";

	public static HashMap<String, Object> executeScript(String process,
			String[] argv) {
		HashMap<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put(KEY_PROCESS, process);
		log.info("进程: " + process);
		log.info("参数: " + Arrays.toString(argv));

		try {
			Runtime rt = Runtime.getRuntime();
			returnMap.put("command", Arrays.toString(argv));

			// 使用ProcessBuilder来处理参数，组合参数
			ProcessBuilder pb = new ProcessBuilder(argv);

			Map<String, String> env = pb.environment();

			Process proc = pb.start();

			String expired = ConfigDao.getInstance().getConfig(
					AppConfigure.KEY_EXPIRED, "5").getValue();
			long hour = Long.parseLong(expired);
			long timeout = hour * 3600 * 1000;

			ErrorStreamWatcher errorWatcher = new ErrorStreamWatcher(proc);
			Worker worker = new Worker(proc);
			InputStreamWatcher inputWatcher = new InputStreamWatcher(proc);
			errorWatcher.start();
			worker.start();
			inputWatcher.start();
			try {
				worker.join(timeout);
				if (worker.exit != null) {
					returnMap.put(KEY_EXIT_VALUE, worker.exit);
					int extval = worker.exit;
					if (extval != 0) {
						log.error("操作进程【" + process + "】失败，exitVal: " + extval);
					} else {
						log.info("操作进程【" + process + "】成功，exitVal: " + extval);
					}
					returnMap.put(KEY_ERROR, errorWatcher.error);
				} else {
					returnMap.put(KEY_EXCEPTION, "timeOut");
					returnMap.put(KEY_ERROR, errorWatcher.error);
					returnMap.put(KEY_EXIT_VALUE, -1);

					throw new TimeoutException();
				}
			} catch (InterruptedException ex) {
				worker.interrupt();
				errorWatcher.interrupt();
				inputWatcher.interrupt();
				Thread.currentThread().interrupt();

				returnMap.put(KEY_ERROR, ex.getMessage());
				returnMap.put(KEY_EXIT_VALUE, -1);

				throw ex;
			} finally {
				proc.destroy();
			}
		} catch (TimeoutException e) {
			// Timeout exception is processed above
			log.error("脚本执行超时：" + e);
		} catch (Throwable t) {
			// Any other exception is shown in debug window
			t.printStackTrace();
			log.error(StackTraceUtil.getStackTrace(t));
			returnMap.put(KEY_ERROR, t.getMessage());
			returnMap.put(KEY_EXIT_VALUE, -1);
		}
		return returnMap;
	}

	/**
	 * 
	 * @author zcg
	 * 
	 */
	private static class Worker extends Thread {
		private final Process process;
		private Integer exit;

		private Worker(Process process) {
			this.process = process;
		}

		public void run() {
			try {
				exit = process.waitFor();
			} catch (InterruptedException ignore) {
				return;
			}
		}
	}

	/**
	 * 收集从脚本执行过程中的错误信息
	 * 
	 * @author zcg
	 * 
	 */
	private static class ErrorStreamWatcher extends Thread {
		private String error;
		private InputStream stderr;
		private InputStreamReader isr;
		private BufferedReader br;

		private ErrorStreamWatcher(Process process) {
			error = "";
			stderr = process.getErrorStream();
			isr = new InputStreamReader(stderr);
			br = new BufferedReader(isr);
		}

		public void run() {
			try {
				String line = br.readLine();
				while (line != null) {
					error += line;
					log.debug("ErrorStream控制台: " + line);
					line = br.readLine();
				}
			} catch (IOException ioexception) {
				return;
			}
		}
	}

	/**
	 * 读取脚本执行的输出流以使他正常关闭
	 * 
	 * @author zcg
	 * 
	 */
	private static class InputStreamWatcher extends Thread {
		private InputStream stderr;
		private InputStreamReader isr;
		private BufferedReader br;

		private InputStreamWatcher(Process process) {
			stderr = process.getInputStream();
			isr = new InputStreamReader(stderr);
			br = new BufferedReader(isr);
		}

		public void run() {
			try {
				String line = br.readLine();
				while (line != null) {
					line = br.readLine();
					log.debug("InputStream控制台:" + line);
				}
			} catch (IOException ioexception) {
				return;
			}
		}
	}
}
