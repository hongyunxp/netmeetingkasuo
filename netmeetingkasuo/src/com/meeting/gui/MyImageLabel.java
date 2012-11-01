package com.meeting.gui;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class MyImageLabel extends JLabel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6498510719800430599L;
	
	public MyImageLabel(String title, String iconString) {
		this.setText(title);
		this.setIcon(getIcon(iconString));
	}

	public ImageIcon getIcon(String iconString) {
		return new ImageIcon(this.getClass().getResource(
				"/resources/images/" + iconString));
	}

}
