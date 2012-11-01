package com.meeting.gui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.meeting.service.HttpdService;
import com.meeting.utils.StackTraceUtil;
import com.meeting.web.WebServer;

public class SetTrayIcon implements ActionListener {

	private PopupMenu pop;

	private MenuItem open, restartweb, restartApache, restartHsql, close;

	private TrayIcon trayicon;

	private MainFrame jframe;

	private boolean show = true;

	private static Logger logger = Logger.getLogger(SetTrayIcon.class);

	public SetTrayIcon(MainFrame jframe) {
		this.jframe = jframe;
		initComponents();
	}

	/**
	 * 设置编译阶段禁用警告提示 初始化程序托盘组件
	 */
	private void initComponents() {
		pop = new PopupMenu();
		open = new MenuItem("隐藏窗口");
		restartApache = new MenuItem("重启Apache服务");
		restartHsql = new MenuItem("重启HSQL服务");
		restartweb = new MenuItem("重启WEB服务");
		close = new MenuItem("退出");
		open.addActionListener(this);
		restartApache.addActionListener(this);
		restartHsql.addActionListener(this);
		restartweb.addActionListener(this);
		close.addActionListener(this);

		pop.add(open);
		pop.add(restartHsql);
		pop.add(restartweb);
		pop.add(restartApache);
		pop.add(close);

		/*
		 * 检查平台是否受支持系统托盘
		 */
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			ImageIcon imageIcon = new ImageIcon(this.getClass()
					.getClassLoader().getResource("resources/images/app.gif")); // 获得托盘显示图标
			Image icon = imageIcon.getImage(); // 获得Image对象
			trayicon = new TrayIcon(icon, "Svn管理系统", pop);
			/*
			 * 为托盘添加鼠标事件,双击则打开程序界面
			 */
			trayicon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						if (show) {
							hideFrame();
						} else {
							openFrame();
						}
					}
				}
			});

			try {
				tray.add(trayicon);
				logger.info("系统托盘加载成功！");
			} catch (AWTException e) {
				logger.error(StackTraceUtil.getStackTrace(e));
			}

		}

		jframe.addWindowListener(new WindowAdapter() {

			public void windowClosed(WindowEvent arg0) {
				hideFrame();
				jframe.dispose();
			}

			public void windowClosing(WindowEvent arg0) {
				hideFrame();
			}

			public void windowIconified(WindowEvent arg0) {
				hideFrame();
			}
		});
	}

	public void closingWindow() {

	}

	/**
	 * 为右击托盘菜单添加事件
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == open) {
			if (show) {
				hideFrame();
			} else {
				openFrame();
			}
		} else if (e.getSource() == close) {
			jframe.closeWindow();
		} else if (e.getSource() == restartweb) {
			new Thread(new Runnable() {
				public void run() {
					int ret = JOptionPane.showConfirmDialog(null,
							"是否确定重启web服务？");
					if (ret == JOptionPane.YES_OPTION) {
						logger.info("确认重启WEB服务");
						WebServer.getInstance().restart(jframe);
					} else {
						logger.info("取消重启WEB服务");
					}
				}
			}).start();
		} else if (e.getSource() == restartApache) {
			new Thread(new Runnable() {
				public void run() {
					int ret = JOptionPane.showConfirmDialog(null,
							"是否确定重启Apache服务？");
					if (ret == JOptionPane.YES_OPTION) {
						logger.info("确认重启Apache服务");
						HashMap<String, Object> map = HttpdService.restart();
						jframe
								.handleApacheStatus(map,
										MainFrame.STATUS_RESTART);
					} else {
						logger.info("取消重启Apache服务");
					}
				}
			}).start();
		} else if (e.getSource() == restartHsql) {
			new Thread(new Runnable() {
				public void run() {
					int ret = JOptionPane.showConfirmDialog(null,
							"是否确定重启HSQL服务？");
					if (ret == JOptionPane.YES_OPTION) {
						logger.info("确认重启HSQL服务");
						jframe.restartHsql();
					} else {
						logger.info("取消重启HSQL服务");
					}
				}
			}).start();
		}
	}

	/**
	 * 
	 */
	public void hideFrame() {
		show = false;
		jframe.setAlwaysOnTop(false);
		jframe.setVisible(false);
		open.setLabel("显示窗口");
	}

	/**
	 * 显示程序界面函数
	 */
	public void openFrame() {
		show = true;
		jframe.setVisible(true);
		open.setLabel("隐藏窗口");
	}

}