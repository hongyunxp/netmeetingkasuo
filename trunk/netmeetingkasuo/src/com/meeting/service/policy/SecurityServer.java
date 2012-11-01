package com.meeting.service.policy;

import java.io.*;
import java.net.*;

import org.apache.log4j.Logger;

import com.meeting.dao.ConfigDao;
import com.meeting.model.ConfigModel;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.DateUtils;
import com.meeting.utils.StackTraceUtil;

/**
 * 
 * @author Administrator
 * 
 */
public class SecurityServer {

	private static final long serialVersionUID = 0x365850c05199bfa5L;

	private static Logger logger = Logger.getLogger(SecurityServer.class);

	public static void start() {
		try {
			ServerSocket serverSocket = new ServerSocket(0);
			int port = serverSocket.getLocalPort();
			ConfigModel model = new ConfigModel();
			model.setName(AppConfigure.KEY_SECURITY);
			model.setTime(DateUtils.getCurrentTime());
			model.setValue(String.valueOf(port));
			model.setUserid(String.valueOf(AppConfigure.USER_ROLE_ADMIN));
			ConfigDao.getInstance().saveConfig(model);
			logger.info("Flash安全认证服务启动，端口：" + port);
			do {
				Socket socket = serverSocket.accept();
				new SocketThread(socket).start();
			} while (true);
		} catch (SocketException socketexception) {
			logger.error("Socket异常："
					+ StackTraceUtil.getStackTrace(socketexception));
		} catch (IOException e) {
			logger.error("IO异常：" + StackTraceUtil.getStackTrace(e));
		}
	}

	private static class SocketThread extends Thread {

		private Socket socket;

		public SocketThread(Socket socket) {
			this.socket = socket;
		}

		public void run() {
			try {
				DataOutputStream outputStream = new DataOutputStream(
						new BufferedOutputStream(socket.getOutputStream()));
				outputStream
						.writeBytes("<cross-domain-policy><allow-access-from domain=\"*\" to-ports=\"*\" /></cross-domain-policy>\0");
				outputStream.flush();
				String address = ((InetSocketAddress) socket
						.getRemoteSocketAddress()).getAddress()
						.getHostAddress();
				int clientPort = ((InetSocketAddress) socket
						.getRemoteSocketAddress()).getPort();
				outputStream.close();
				socket.close();
				logger.info("客户端 [" + address + ":" + clientPort + "] 请求认证");
			} catch (IOException e) {
				logger.error("IO异常：" + StackTraceUtil.getStackTrace(e));
			}
		}
	}
}
