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
	 * ���ñ���׶ν��þ�����ʾ ��ʼ�������������
	 */
	private void initComponents() {
		pop = new PopupMenu();
		open = new MenuItem("���ش���");
		restartApache = new MenuItem("����Apache����");
		restartHsql = new MenuItem("����HSQL����");
		restartweb = new MenuItem("����WEB����");
		close = new MenuItem("�˳�");
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
		 * ���ƽ̨�Ƿ���֧��ϵͳ����
		 */
		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			ImageIcon imageIcon = new ImageIcon(this.getClass()
					.getClassLoader().getResource("resources/images/app.gif")); // ���������ʾͼ��
			Image icon = imageIcon.getImage(); // ���Image����
			trayicon = new TrayIcon(icon, "Svn����ϵͳ", pop);
			/*
			 * Ϊ�����������¼�,˫����򿪳������
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
				logger.info("ϵͳ���̼��سɹ���");
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
	 * Ϊ�һ����̲˵�����¼�
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
							"�Ƿ�ȷ������web����");
					if (ret == JOptionPane.YES_OPTION) {
						logger.info("ȷ������WEB����");
						WebServer.getInstance().restart(jframe);
					} else {
						logger.info("ȡ������WEB����");
					}
				}
			}).start();
		} else if (e.getSource() == restartApache) {
			new Thread(new Runnable() {
				public void run() {
					int ret = JOptionPane.showConfirmDialog(null,
							"�Ƿ�ȷ������Apache����");
					if (ret == JOptionPane.YES_OPTION) {
						logger.info("ȷ������Apache����");
						HashMap<String, Object> map = HttpdService.restart();
						jframe
								.handleApacheStatus(map,
										MainFrame.STATUS_RESTART);
					} else {
						logger.info("ȡ������Apache����");
					}
				}
			}).start();
		} else if (e.getSource() == restartHsql) {
			new Thread(new Runnable() {
				public void run() {
					int ret = JOptionPane.showConfirmDialog(null,
							"�Ƿ�ȷ������HSQL����");
					if (ret == JOptionPane.YES_OPTION) {
						logger.info("ȷ������HSQL����");
						jframe.restartHsql();
					} else {
						logger.info("ȡ������HSQL����");
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
		open.setLabel("��ʾ����");
	}

	/**
	 * ��ʾ������溯��
	 */
	public void openFrame() {
		show = true;
		jframe.setVisible(true);
		open.setLabel("���ش���");
	}

}