package com.meeting.applet;
import java.awt.event.*;
import java.io.*;

//spawns a thread to automatically log in the user based on the following viewer variables
//this allows the user to log into an account based on a set of passed parametsrs or 
//the same variables as configured by the modified AuthPanel
//List of parameters and their usage follows
/*
viewer variabled user within:

  variable		usage
  
*/

class AutoLogin extends Thread {
	TightVncViewer viewer;	//the lone and only important passed variable

//CONSTRUCTOR
    AutoLogin(TightVncViewer v) {
		  viewer = v;
    }


//Internally used function to send a string against the vnc session
	public void simulateString(String str) throws IOException {
		final int modifiers = 0;
		for (int x=0; x < str.length() ; x++) {
				if (str.charAt(x) == '\\' && (x < (str.length() - 1))) { //special character handler
				  viewer.rfb.writeKeyEvent(new KeyEvent(viewer.buttonPanel, KeyEvent.KEY_PRESSED, 0, modifiers, 0, 
								(char)(str.charAt(x)+str.charAt(x+1))));
				  viewer.rfb.writeKeyEvent(new KeyEvent(viewer.buttonPanel, KeyEvent.KEY_RELEASED, 0, modifiers, 0,
								(char)(str.charAt(x)+str.charAt(x+1))));	
				  x++; //skips the second char of a multicharacter character (ie \t)			
				}
				else {  //normal character handler
				  viewer.rfb.writeKeyEvent(new KeyEvent(viewer.buttonPanel, KeyEvent.KEY_PRESSED, 0, modifiers, 0, str.charAt(x)));
				  viewer.rfb.writeKeyEvent(new KeyEvent(viewer.buttonPanel, KeyEvent.KEY_RELEASED, 0, modifiers, 0, str.charAt(x)));
				}
			 }

	}

//Main thread function - called by start()
//handles all input simulation and listener activation
	@SuppressWarnings("deprecation")
	public void run() {
		try {
		  System.out.println("Autologin thread slawned.  Delaying for "+viewer.autoDelay+" milliseconds");
		  Thread.sleep(viewer.autoDelay); //allows the server to draw the screen
		  System.out.println("Autologin commencing");
		  //enable input systems - they where disabled so the user cant manually put something in during the thread sleep
		  viewer.vc.enableInput(true); 
		  viewer.vc.addKeyListener(viewer.vc);
		  //simulate key inputs
			 final int modifiers = 0;
			 if (viewer.autoCtrlAltDelete.equalsIgnoreCase("Yes")) { //sends a ctrl alt delete as need by windows as a terminal
				final int ctrlAlt = InputEvent.CTRL_MASK | InputEvent.ALT_MASK;
				//Note:  this uses the depricated KeyEvent constructor as was originally in the source.  i cant find the escape code for del
				//If found also replace in buttonPanel actionListener
        			viewer.rfb.writeKeyEvent(new KeyEvent(viewer.buttonPanel, KeyEvent.KEY_PRESSED, 0, ctrlAlt, 127));
				viewer.rfb.writeKeyEvent(new KeyEvent(viewer.buttonPanel, KeyEvent.KEY_RELEASED, 0, ctrlAlt, 127));
			}
			 for (int x=0; x < viewer.autoBackspace ; x++) { //deletes an existing entry in the username field
				//backspaces to clear user - possibly made configurable number to simulate
			        viewer.rfb.writeKeyEvent(new KeyEvent(viewer.buttonPanel, KeyEvent.KEY_PRESSED, 0, modifiers, 0, '\b')); 
			        viewer.rfb.writeKeyEvent(new KeyEvent(viewer.buttonPanel, KeyEvent.KEY_RELEASED, 0, modifiers, 0, '\b'));
			 }
			 //Simulate the input string to log the user in.  the following where consolidated from individual simulateString() Calls
			 //draw the username
			 //tab (by default seperator)
			 //draw the password
			 //hit enter to log the user in!
			 simulateString(viewer.autoUsername+viewer.autoSeperator+viewer.autoPassword+"\n");
		//catches
      		} catch (IOException e) {
        		e.printStackTrace(); //dont know how to make occur but its all good
      		} catch (InterruptedException e) { }  //their is no way for the user to do this anyway
	}
 }
