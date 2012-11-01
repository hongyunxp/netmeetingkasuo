package com.meeting.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AboutDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1508530852095399183L;

	private MyImageLabel imageLabel = null;
	private JLabel jLabel2;
	private JButton jButton1;
	private JLabel jLabel3;
	private JLabel jLabel1;

	public AboutDialog() {
		initGUI();
	}

	private void initGUI() {
		try {
			{
				this.setSize(430, 199);
				this.setLayout(null);
				imageLabel = new MyImageLabel("", "filesesrver.png");
				add(imageLabel);
				imageLabel.setBounds(16, 46, 109, 113);
			}
			{
				jLabel1 = new JLabel();
				getContentPane().add(jLabel1);
				jLabel1.setText("作者：邹春刚");
				jLabel1.setBounds(163, 60, 181, 15);
			}
			{
				jLabel1 = new JLabel();
				getContentPane().add(jLabel1);
				jLabel1.setText("时间：2010-12-26");
				jLabel1.setBounds(163, 90, 181, 15);
			}
			{
				jLabel2 = new JLabel();
				getContentPane().add(jLabel2);
				jLabel2.setText("网络控制台");
				jLabel2.setBounds(19, 12, 392, 42);
			}
			{
				jButton1 = new MyImageButton("关闭", "cancel.png");
				getContentPane().add(jButton1);
				jButton1.setBounds(309, 139, 88, 22);
				jButton1.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
			}
			{
				jLabel3 = new JLabel();
				getContentPane().add(jLabel3);
				jLabel3.setText("浏览器打开...");
				jLabel3.setForeground(Color.BLUE);
				jLabel3.setBounds(163, 120, 97, 15);

//				ConfigDao.getInstance().getConfig(
//						AppConfigure.KEY_PORT,"5520");
//				final RunBrowser browser = new RunBrowser(url);
//				browser.changeMouse(jLabel3);
//				jLabel3.addMouseListener(new MouseAdapter() {
//					public void mouseClicked(MouseEvent e) {
//						browser.runBroswer();
//					}
//				});
			}
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			this.setTitle("帮助-版权");
			this.setLocationRelativeTo(null);
			this.setModal(true);
			this.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
