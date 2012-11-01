package com.meeting.gui;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class MyImageButton extends JButton {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7369392299683559309L;

	public MyImageButton(String title, String iconString) {
		this.setText(title);
		this.setIcon(getIcon(iconString));
	}

	public ImageIcon getIcon(String iconString) {
		return new ImageIcon(this.getClass().getResource(
				"/resources/images/" + iconString));
	}

}
