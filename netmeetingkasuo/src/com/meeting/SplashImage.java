package com.meeting;

import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

public class SplashImage extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6024838194769671026L;
	private JLabel splashLabel = null;
	private JWindow splashScreen = null;

	private static SplashImage instance = null;

	public SplashImage() {
	}

	public static SplashImage getInstance() {
		if (instance == null) {
			instance = new SplashImage();
		}
		return instance;
	}

	/**
	 * œ‘ æ
	 */
	public void show() {
		createSplashScreen();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				showSplashScreen();
			}
		});
	}

	/**
	 * Î[≤ÿ
	 */
	public void hide() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				hideSplash();
			}
		});
	}

	/**
	 * ¥¥Ω®…¡∆¡
	 */
	private void createSplashScreen() {
		splashLabel = new JLabel(createImageIcon("Splash.jpg",
				"…¡∆¡Õº∆¨"));

		splashScreen = new JWindow(this);
		splashScreen.getContentPane().add(splashLabel);
		splashScreen.setAlwaysOnTop(true);
		splashScreen.pack();
		Rectangle screenRect = this.getGraphicsConfiguration().getBounds();
		splashScreen.setLocation(screenRect.x + screenRect.width / 2
				- splashScreen.getSize().width / 2, screenRect.y
				+ screenRect.height / 2 - splashScreen.getSize().height / 2);
	}

	/**
	 * “˛≤ÿ…¡∆¡
	 */
	private void hideSplash() {
		splashScreen.setVisible(false);
		splashScreen = null;
		splashLabel = null;
	}

	/**
	 * œ‘ æ…¡∆¡
	 */
	private void showSplashScreen() {
		splashScreen.setVisible(true);
	}

	/**
	 * ¥¥Ω®Õº∆¨
	 * @param filename
	 * @param description
	 * @return
	 */
	private ImageIcon createImageIcon(String filename, String description) {
		String path = "/resources/images/" + filename;
		return new ImageIcon(getClass().getResource(path), description);
	}

}
