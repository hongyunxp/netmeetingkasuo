package com.meeting.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.meeting.Starter;
import com.meeting.dao.ConfigDao;
import com.meeting.gui.log.JLogList;
import com.meeting.gui.log.LogMonitor;
import com.meeting.jna.WINSERVICEDLL;
import com.meeting.service.ExecuteService;
import com.meeting.service.HSqlService;
import com.meeting.service.HttpdService;
import com.meeting.service.Red5Service;
import com.meeting.utils.AppConfigure;
import com.meeting.utils.StackTraceUtil;
import com.meeting.web.WebServer;

/**
 * 
 * @author zcg
 * @since JDK1.6
 */
public class MainFrame extends JFrame {

	private static final long serialVersionUID = 3227456982592557903L;
	private JMenuBar menubar = new JMenuBar();
	private JMenu mnFile = new JMenu("�ļ�(F)");
	private JMenu mnContainer = new JMenu("Web����(C)");
	private JMenu mnApache = new JMenu("Apache����(A)");
	private JMenu mnHsql = new JMenu("HSQL����(H)");
	private JMenu mnHelp = new JMenu("����(H)");

	private JMenuItem startWeb = new JMenuItem("����");
	private JMenuItem stopWeb = new JMenuItem("ֹͣ");
	private JMenuItem restartWeb = new JMenuItem("����");
	private JMenuItem miExit = new JMenuItem("�˳�");

	private JMenuItem miInspect = new JMenuItem("���80�˿�");
	private JMenuItem miStart = new JMenuItem("����");
	private JMenuItem miStop = new JMenuItem("ֹͣ");
	private JMenuItem miRestart = new JMenuItem("����");
	private JMenuItem miInstall = new JMenuItem("��װ����");
	private JMenuItem miUninstall = new JMenuItem("ж�ط���");
	private JMenuItem miclearLog = new JMenuItem("�����־");

	private JMenuItem startHsql = new JMenuItem("����");
	private JMenuItem stopHsql = new JMenuItem("ֹͣ");

	private JMenuItem mihelp = new JMenuItem("����");

	private JPanel pnlStatusbar = new JPanel();
	private JLabel apacheStatus = new JLabel("");
	private JLabel jettyStatus = new JLabel("");
	private JLabel hsqlStatus = new JLabel("");
	private JLabel lblNowTime = new JLabel(new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss").format(new Date()));

	private Timer dateTimer;

	private JDesktopPane desktop = new JDesktopPane();

	// /////////////////////////////////////////////////////////////

	final int DEBUG = 10000;
	final int INFO = 20000;
	final int WARN = 30000;
	final int ERROR = 40000;

	private static Logger log = Logger.getLogger(MainFrame.class);

	public static MainFrame instance = null;

	private JLogList loglist = null;

	private LogMonitor logMonitor = null;

	public static List<Object> logCache = new ArrayList<Object>();

	private JTextArea detailLogJTA = null;

	private static Logger logger = Logger.getLogger(MainFrame.class);

	public static final int STATUS_START = 1;
	public static final int STATUS_STOP = 2;
	public static final int STATUS_RESTART = 3;
	public static final int STATUS_INSTALL = 4;
	public static final int STATUS_UNINSTALL = 5;

	public static boolean CONTAINER_RUN = false;
	public static boolean APACHE_RUN = false;
	public static boolean APACHE_INSTALL = false;

	/**
	 * ���캯��
	 */
	public MainFrame() {
		initComponents();
		if (dateTimer != null)
			dateTimer.start();
		instance = this;
	}

	/**
	 * ��ʼ��
	 */
	private void initComponents() {

		dateTimer = new Timer(1000, new ShowNowTime());
		final MyImageLabel infoLabel = new MyImageLabel("    ", "ie.png");
		infoLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// String username = AppConfigure
				// .getProperty(AppConfigure.KEY_USERNAME);
				// UserDao userDao = new UserDao();
				// String password = userDao.getUserByUserCode(username)
				// .getUserpwd();
				// String url = "http://localhost/"
				// + WebServer.MODULE_NAME
				// + "/login?oper=forward&username=" + username
				// + "&password=" + password;
				// final RunBrowser browser = new RunBrowser(url);
				// browser.changeMouse(infoLabel);
				// browser.runBroswer();
			}
		});
		JPanel jPanel = new JPanel();
		jPanel.setLayout(new BorderLayout());
		jPanel.add(lblNowTime, BorderLayout.EAST);
		jPanel.add(infoLabel, BorderLayout.CENTER);

		JPanel statusJPanel = new JPanel();
		statusJPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
		statusJPanel.add(hsqlStatus);
		statusJPanel.add(jettyStatus);
		statusJPanel.add(apacheStatus);

		pnlStatusbar.setLayout(new BorderLayout());
		pnlStatusbar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlStatusbar.add(statusJPanel, BorderLayout.WEST);
		pnlStatusbar.add(jPanel, BorderLayout.EAST);

		miExit.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/logout.png")));

		startWeb.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/container_start.png")));
		stopWeb.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/container_stop.png")));
		restartWeb.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/container_restart.png")));

		miInspect.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/checkport.png")));
		miStart.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/laptop_start.png")));
		miStop.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/laptop_stop.png")));
		miRestart.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/laptop_go.png")));
		miInstall.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/laptop_add.png")));
		miUninstall.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/laptop_delete.png")));

		startHsql.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/container_start.png")));
		stopHsql.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/container_stop.png")));

		mihelp.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/help.png")));

		mnFile.add(miExit);

		mnContainer.add(startWeb);
		mnContainer.add(stopWeb);
		mnContainer.add(restartWeb);

		mnApache.add(miInspect);
		mnApache.add(miStart);
		mnApache.add(miStop);
		mnApache.add(miRestart);
		mnApache.add(miInstall);
		mnApache.add(miUninstall);
		mnApache.add(miclearLog);

		mnHsql.add(startHsql);
		mnHsql.add(stopHsql);
		if (HSqlService.RUN) {
			updateHsqlStatusOn();
		} else {
			updateHsqlStatusOff();
		}

		mnHelp.add(mihelp);

		menubar.add(mnFile);
		mnFile.setMnemonic('F');
		menubar.add(mnContainer);
		menubar.add(mnApache);
		mnApache.setMnemonic('A');
		menubar.add(mnHsql);
		mnHsql.setMnemonic('H');
		menubar.add(mnHelp);
		mnHelp.setMnemonic('H');

		setJMenuBar(menubar);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(new Dimension(900, 650));
		setTitle("����������̨");
		setLocationRelativeTo(null);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				instance.setVisible(false);
			}
		});

		// ����web������
		startWeb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						logger.info("ȷ������WEB������");
						int port = Integer.valueOf(ConfigDao.getInstance()
								.getConfig(AppConfigure.KEY_PORT, "5520")
								.getValue());
						if (Starter.isPortExist(port)) {
							String msgString = "�˿ڣ�" + port
									+ " �Ѿ���ռ�ã����޸��ֶ�Web�������˿�!";
							logger.error(msgString);
							JOptionPane.showMessageDialog(null, msgString);
							logger.error("����������ʧ��");
							updateJettyStatusOff();
						} else {
							WebServer.getInstance().start(port);
							updateJettyStatusOn();
						}
					}
				}).start();
			}
		});
		// ֹͣweb������
		stopWeb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						logger.info("ȷ��ֹͣWEB������");
						WebServer.getInstance().stop();
						updateJettyStatusOff();
					}
				}).start();
			}
		});
		// ����web������
		restartWeb.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						int ret = JOptionPane.showConfirmDialog(null,
								"�Ƿ�ȷ������web��������");
						if (ret == JOptionPane.YES_OPTION) {
							logger.info("ȷ������WEB������");
							WebServer.getInstance().restart(instance);
						} else {
							logger.info("ȡ������WEB������");
						}
					}
				}).start();
			}
		});

		startHsql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						HSqlService.start();
						updateHsqlStatusOn();
					}
				}).start();
			}
		});

		stopHsql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						int ret = JOptionPane.showConfirmDialog(null,
								"�Ƿ�ȷ��ֹͣHSQL��������");
						if (ret == JOptionPane.YES_OPTION) {
							logger.info("ȷ��ֹͣHSQL������");
							HSqlService.stop();
							updateHsqlStatusOff();
						} else {
							logger.info("ȡ��ֹͣHSQL������");
						}
					}
				}).start();
			}
		});

		// �˳�
		miExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("�رճ���");
				closeWindow();
			}
		});

		// ���80�˿�
		miInspect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						boolean apacheport = Starter.isPortExist(80);
						if (apacheport) {
							JOptionPane.showMessageDialog(null, "80�˿��Ѿ���ռ�ã�");
						} else {
							JOptionPane.showMessageDialog(null, "80�˿�δռ�ã�");
						}
					}
				}).start();
			}
		});

		// ����httpd
		miStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startApache();
			}
		});

		// ֹͣhttpd
		miStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						logger.info("Apache����������ֹͣ...");
						HashMap<String, Object> map = HttpdService.stop();
						handleApacheStatus(map, STATUS_STOP);
					}
				}).start();
			}
		});

		// ����httpd
		miRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						apacheStatus.setToolTipText("Apache������������...");
						HashMap<String, Object> map = HttpdService.restart();
						handleApacheStatus(map, STATUS_RESTART);
					}
				}).start();
			}
		});

		// ���80�˿�
		miInspect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {

					}
				}).start();
			}
		});

		// ��װhttpd����
		miInstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						logger.info("Apache�������ڱ���װϵͳ����...");
						HashMap<String, Object> map = HttpdService.install();
						handleApacheStatus(map, STATUS_INSTALL);
						logger.info("�޸�ApacheĬ���������ã��ֹ�����");
						WINSERVICEDLL.INSTANCE.SetServiceStartType(
								AppConfigure.APACHE_SERVICE,
								AppConfigure.SERVICE_DEMAND_START);
					}
				}).start();
			}
		});

		// ж��httpd����
		miUninstall.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					public void run() {
						logger.info("Apache�������ڱ�ж��...");
						HashMap<String, Object> map = HttpdService.uninstall();
						handleApacheStatus(map, STATUS_UNINSTALL);
					}
				}).start();
			}
		});

		// ����
		mihelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("�򿪰����Ի���");
				AboutDialog diag = new AboutDialog();
				diag.setModal(true);
			}
		});

		// ���������־
		miclearLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("���������־");
				String basePath = ConfigDao.getInstance().getConfig(
						AppConfigure.KEY_BASEPATH).getValue();
				String AccessCommonLog = basePath + "/"
						+ AppConfigure.apache_access_common_log_path;

				String AccessCombinedLog = basePath + "/"
						+ AppConfigure.apache_access_combined_log_path;

				String ApacheErrLog = basePath + "/"
						+ AppConfigure.apache_errlogpath;

				try {
					BufferedWriter writer1 = new BufferedWriter(new FileWriter(
							new File(AccessCommonLog)));
					writer1.write("");
					writer1.flush();
					writer1.close();
					logger.info("�����־��"
							+ AppConfigure.apache_access_common_log_path
							+ " �ɹ���");
				} catch (IOException ex) {
					ex.printStackTrace();
					logger.error("д�ļ�"
							+ AppConfigure.apache_access_common_log_path
							+ "IO�쳣�� " + StackTraceUtil.getStackTrace(ex));
				}

				try {
					BufferedWriter writer2 = new BufferedWriter(new FileWriter(
							new File(AccessCombinedLog)));
					writer2.write("");
					writer2.flush();
					writer2.close();
					logger.info("�����־��"
							+ AppConfigure.apache_access_combined_log_path
							+ " �ɹ���");
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error("д�ļ�"
							+ AppConfigure.apache_access_combined_log_path
							+ "IO�쳣�� " + StackTraceUtil.getStackTrace(ex));
				}

				try {
					BufferedWriter writer2 = new BufferedWriter(new FileWriter(
							new File(ApacheErrLog)));
					writer2.write("");
					writer2.flush();
					writer2.close();
					logger.info("�����־��" + AppConfigure.apache_errlogpath
							+ " �ɹ���");
				} catch (Exception ex) {
					ex.printStackTrace();
					logger.error("д�ļ�" + AppConfigure.apache_errlogpath
							+ "IO�쳣�� " + StackTraceUtil.getStackTrace(ex));
				}
			}
		});

		// ///////////////////////////////
		// guiͼ��
		Image myimage = createImage("app.gif");
		this.setIconImage(myimage);

		getContentPane().add(getLogMonitor(), BorderLayout.CENTER);
		// getContentPane().add(new JToolBar(), BorderLayout.NORTH);
		getContentPane().add(pnlStatusbar, BorderLayout.SOUTH);

		// createMenu();
	}

	/**
	 * ����Apache
	 */
	public void startApache() {
		new Thread(new Runnable() {
			public void run() {
				logger.info("Apache��������������...�˿ڣ�80");
				HashMap<String, Object> map = HttpdService.start();
				handleApacheStatus(map, STATUS_START);
			}
		}).start();
	}

	/**
	 * ��ʼ��Apache������
	 */
	public void initApacheServer() {
		new Thread(new Runnable() {
			public void run() {
				apacheStatus.setText("Apache");
				// ���80�˿�
				boolean apacheport = Starter.isPortExist(80);
				boolean portexist = false;
				if (apacheport) {
					portexist = true;
					JOptionPane.showMessageDialog(null, "80�˿��Ѿ���ռ�ã�");
				}
				int status = WINSERVICEDLL.INSTANCE
						.GetServiceStatus(AppConfigure.APACHE_SERVICE);
				switch (status) {
				case AppConfigure.APACHE_SERVICE_NONE: {
					apacheStatus.setIcon(new ImageIcon(this.getClass()
							.getResource("/resources/images/status_none.png")));
					APACHE_INSTALL = false;
					APACHE_RUN = false;
					miInstall.setEnabled(true);
					miUninstall.setEnabled(false);
					miStart.setEnabled(false);
					miStop.setEnabled(false);
					miRestart.setEnabled(false);
					break;
				}
				case AppConfigure.APACHE_SERVICE_RUNNING: {
					logger.info("Apache��������������...�˿ڣ�80");
					apacheStatus
							.setIcon(new ImageIcon(this.getClass().getResource(
									"/resources/images/status_start.png")));
					APACHE_RUN = true;
					APACHE_INSTALL = true;
					miInstall.setEnabled(false);
					miUninstall.setEnabled(false);
					miStart.setEnabled(false);
					miStop.setEnabled(true);
					miRestart.setEnabled(true);
					logger.info("Apache�����������ɹ�...������80");
					break;
				}
				case AppConfigure.APACHE_SERVICE_STOPPED: {
					apacheStatus.setIcon(new ImageIcon(this.getClass()
							.getResource("/resources/images/status_stop.png")));
					APACHE_RUN = false;
					APACHE_INSTALL = true;
					miInstall.setEnabled(false);
					miUninstall.setEnabled(true);
					miStart.setEnabled(true);
					miStop.setEnabled(false);
					miRestart.setEnabled(false);
					if (!portexist) {
						startApache();
					}
					break;
				}
				default:
					break;
				}
			}
		}).start();
	}

	/**
	 * ����jetty����״̬
	 */
	public void updateJettyStatusOn() {
		CONTAINER_RUN = true;
		jettyStatus.setText("Container");
		jettyStatus.setToolTipText("Web ���������ɹ���");
		jettyStatus.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/status_start.png")));
		restartWeb.setEnabled(true);
		startWeb.setEnabled(false);
		stopWeb.setEnabled(true);
	}

	/**
	 * ����jettytֹͣ״̬
	 */
	public void updateJettyStatusOff() {
		CONTAINER_RUN = false;
		jettyStatus.setText("Container");
		jettyStatus.setToolTipText("Web ����ֹͣ�ɹ���");
		jettyStatus.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/status_stop.png")));
		restartWeb.setEnabled(true);
		startWeb.setEnabled(true);
		stopWeb.setEnabled(false);
	}

	/**
	 * ����HSQL����״̬
	 */
	public void updateHsqlStatusOn() {
		hsqlStatus.setText("HSQL");
		hsqlStatus.setToolTipText("HSQL���������ɹ���");
		hsqlStatus.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/status_start.png")));

		startHsql.setEnabled(false);
		stopHsql.setEnabled(true);
	}

	public void restartHsql() {
		updateHsqlStatusOff();
		HSqlService.stop();
		HSqlService.start();
		updateHsqlStatusOn();
	}

	/**
	 * ����HSQLֹͣ״̬
	 */
	public void updateHsqlStatusOff() {
		hsqlStatus.setText("HSQL");
		hsqlStatus.setToolTipText("HSQL����ֹͣ�ɹ���");
		hsqlStatus.setIcon(new ImageIcon(this.getClass().getResource(
				"/resources/images/status_stop.png")));

		startHsql.setEnabled(true);
		stopHsql.setEnabled(false);
	}

	/**
	 * ���web����״̬
	 * 
	 * @param status
	 */
	public void handleContainerStatus() {
		if (CONTAINER_RUN) {
			updateJettyStatusOn();
		} else {
			updateJettyStatusOff();
		}
	}

	/**
	 * ���Apache״̬
	 * 
	 * @param map
	 */
	public void handleApacheStatus(HashMap<String, Object> map, int status) {
		String exitval = String.valueOf(map.get(ExecuteService.KEY_EXIT_VALUE));
		String exiterr = String.valueOf(map.get(ExecuteService.KEY_ERROR));
		apacheStatus.setText("Apache");
		apacheStatus.setIcon(new ImageIcon(instance.getClass().getResource(
				"/resources/images/status_none.png")));
		if (exitval.equals("0")) {
			switch (status) {
			case STATUS_START: {
				apacheStatus.setToolTipText("Apache�����ɹ���");
				apacheStatus.setIcon(new ImageIcon(instance.getClass()
						.getResource("/resources/images/status_start.png")));
				miInstall.setEnabled(false);
				miUninstall.setEnabled(false);
				miStart.setEnabled(false);
				miStop.setEnabled(true);
				miRestart.setEnabled(true);
				APACHE_RUN = true;
				APACHE_INSTALL = true;
				logger.info("Apache�����������ɹ�...������80");
				break;
			}
			case STATUS_STOP: {
				apacheStatus.setToolTipText("Apacheֹͣ����");
				apacheStatus.setIcon(new ImageIcon(instance.getClass()
						.getResource("/resources/images/status_stop.png")));
				miInstall.setEnabled(false);
				miUninstall.setEnabled(true);
				miStart.setEnabled(true);
				miStop.setEnabled(false);
				miRestart.setEnabled(false);
				APACHE_RUN = false;
				APACHE_INSTALL = true;
				logger.info("Apache����������ֹͣ...");
				break;
			}
			case STATUS_RESTART: {
				apacheStatus.setIcon(new ImageIcon(instance.getClass()
						.getResource("/resources/images/status_start.png")));
				miInstall.setEnabled(false);
				miUninstall.setEnabled(false);
				miStart.setEnabled(false);
				miStop.setEnabled(true);
				miRestart.setEnabled(true);
				APACHE_RUN = true;
				APACHE_INSTALL = true;
				logger.info("Apache�����������ɹ�...������80");
				break;
			}
			case STATUS_INSTALL: {
				apacheStatus.setToolTipText("Apache��װ����ɹ���");
				apacheStatus
						.setIcon(new ImageIcon(instance.getClass().getResource(
								"/resources/images/status_installed.png")));
				miInstall.setEnabled(false);
				miUninstall.setEnabled(true);
				miStart.setEnabled(true);
				miStop.setEnabled(false);
				miRestart.setEnabled(false);
				APACHE_RUN = false;
				APACHE_INSTALL = true;
				logger.info("Apache����װϵͳ����ɹ�...");
				break;
			}
			case STATUS_UNINSTALL: {
				apacheStatus.setToolTipText("Apacheж�ط���ɹ���");
				apacheStatus.setIcon(new ImageIcon(instance.getClass()
						.getResource("/resources/images/status_none.png")));
				miInstall.setEnabled(true);
				miUninstall.setEnabled(false);
				miStart.setEnabled(false);
				miStop.setEnabled(false);
				miRestart.setEnabled(false);
				APACHE_RUN = false;
				APACHE_INSTALL = false;
				logger.info("Apache����ж�سɹ�...");
				break;
			}
			default:
				break;
			}
		} else {
			JOptionPane.showMessageDialog(null, exiterr);
		}
	}

	/**
	 * 
	 * @return
	 */
	public JDesktopPane getDesktopPane() {
		return desktop;
	}

	/**
	 * ��ʾ��ǰʱ��
	 * 
	 * @author Administrator
	 * 
	 */
	private class ShowNowTime implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			lblNowTime.setText("��ǰʱ�䣺" + now);
		}
	}

	// ///////////////////////////////////////////////////////

	public JTextArea getDetailLogJTA() {
		return detailLogJTA;
	}

	/**
	 * ������־����
	 * 
	 * @param level
	 */
	public void setLogLevel(int level) {
		switch (level) {
		case DEBUG: {
			loglist.setLevel(Level.DEBUG);
			log.info("Log level reset to DEBUG");
			break;
		}
		case INFO: {
			loglist.setLevel(Level.INFO);
			log.info("Log level reset to INFO");
			break;
		}
		case WARN: {
			loglist.setLevel(Level.WARN);
			log.warn("Log level reset to WARN");
			break;
		}
		case ERROR: {
			loglist.setLevel(Level.ERROR);
			log.error("Log level reset to ERROR");
			break;
		}
		default: {
			loglist.setLevel(Level.INFO);
			log.info("Log level reset to INFO");
			break;
		}
		}
	}

	/**
	 * ��ӡ��־�б�
	 * 
	 * @return
	 */
	public Component getLogMonitor() {
		logMonitor = new LogMonitor(this);
		loglist = logMonitor.addLogArea("��־���", "com.suntek.efileconverter",
				true);
		for (Object message : logCache) {
			logMonitor.logEvent(message);
		}
		return logMonitor;
	}

	/**
	 * ��ӡ��־
	 * 
	 * @param msg
	 */
	public synchronized static void log(final Object msg) {
		if (instance == null) {
			logCache.add(msg);
			return;
		}

		if (SwingUtilities.isEventDispatchThread()) {
			instance.logMonitor.logEvent(msg);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					instance.logMonitor.logEvent(msg);
				}
			});
		}
	}

	public Image createImage(String filename) {
		String path = "/resources/images/" + filename;
		return Toolkit.getDefaultToolkit().getImage(
				getClass().getResource(path));
	}

	/**
	 * ��ʾ�رմ���
	 */
	public void closeWindow() {
		int option = JOptionPane.showConfirmDialog(instance, "ȷ���˳�ϵͳ?", "ϵͳ��ʾ",
				JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			new Thread(new Runnable() {
				public void run() {
					stop();
				}
			}).start();
		}
	}

	/**
	 * �رշ�����
	 */
	public void stop() {
		// 1. ֹͣ������
		if (MainFrame.APACHE_RUN) {
			HttpdService.stop();
		}

		// 2. ֹͣWeb����
		WebServer.getInstance().stop();
		instance.updateJettyStatusOff();

		// 3. �ر�RED5����
		Red5Service.stopRed5Service();
		
		// 4. ֹͣ���ݿ�
		HSqlService.stop();

		// 5. �ر�GUI
		logger.info("ϵͳ�����˳�...");
		System.exit(0);
	}

}
