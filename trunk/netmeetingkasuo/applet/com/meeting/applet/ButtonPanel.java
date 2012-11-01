package com.meeting.applet;
//
//  Copyright (C) 2001,2002 HorizonLive.com, Inc.  All Rights Reserved.
//  Copyright (C) 1999 AT&T Laboratories Cambridge.  All Rights Reserved.
//
//  This is free software; you can redistribute it and/or modify
//  it under the terms of the GNU General Public License as published by
//  the Free Software Foundation; either version 2 of the License, or
//  (at your option) any later version.
//
//  This software is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU General Public License for more details.
//
//  You should have received a copy of the GNU General Public License
//  along with this software; if not, write to the Free Software
//  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307,
//  USA.
//

//
// ButtonPanel class implements panel with four buttons in the
// VNCViewer desktop window.
//

//Rewrites to this section are spread widely throughout the original base code
//The general purpose of these changes is to provide a better user interface themed by the
//viewer.(varName) parameters passed via applet parameters.  These are to provide
//horde themeing capacity to the applet.  List of parameters and their usage follows 
/*
viewer variabled user within:

  variable		usage
 setQuality	        sets the initial image quality variable and forces a change if set (0-9) 
 setCompression		sets the initial image quality variable and forces a change if set (0-9)
 panelColor		color object to be used for the base panel color
 panelFontColor	color for the panels text
 panelSliderLow		the color for the low value of the sliders for quality and compression
 panelSliderHigh	the color for the high value of the sliders for quality and compression
 panelSliderUnselected	the color for the quality slider blocks to the right of the current
 panelHideQuality	control key to hide quality setting adjuster
 panelHideCompression	control key to hide compression setting adjuster
 panelHideCtrlAltDelete	control key to hide CtrlAltDelete input simulator key
 panelHideRefresh	control key to hide Force Refresh button
 panelHideUser		control key to hide UserName
 panelHideAll		control key to hude all

//the panel "look"
note:  things above the top ----- row are for descriptions not display

+------------------------------------------------------------------------------------------------+
|  Force  |     Send	  | Image   -------|----- | Image       -------|--- |  User  |	 	 |	
| Refresh | CtrlAltDelete | Quality -------|----- | Compression -------|--- | SSmith |	 	 |
+------------------------------------------------------------------------------------------------+
|												 |
|				The Desktop Canvas						 |
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;

class ButtonPanel extends Panel implements ActionListener {

  /**
	 * 
	 */
	private static final long serialVersionUID = 2799136815974704738L;
TightVncViewer viewer;
  Button ctrlAltDelButton;
  Button refreshButton;
  Label qualityLabel;
  Label compressionLabel;
  Button[][] slider = new Button[2][10]; //button arrays for quality and comression slider setting state
    final int qualityIndex = 0;
	 final int compressionIndex = 1;
  Label userLabel;
  
  //
  // The actual data which other classes look at:  -- ripped from options and set static (mostly) in constructor
  //

  int[] encodings = new int[20];
  int nEncodings;

  int compressLevel;
  int jpegQuality;

  boolean eightBitColors;

  boolean requestCursorUpdates;
  boolean ignoreCursorUpdates;

  boolean reverseMouseButtons2And3;
  boolean shareDesktop;
  boolean viewOnly;

//Constructor
  ButtonPanel(TightVncViewer v) {
    viewer = v;

//note regardless of wether gui is built a working configuration set is loaded
  setEncodings();

//GUI Land
  if (viewer.showControls) { //only show controls if the variable is set.  otherwise dont draw gui and just set "options" values
    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
	setBackground(viewer.panelColor);
	setForeground(viewer.panelFontColor);
	if (viewer.panelHideRefresh.equals("No")) {
      refreshButton = new Button("Force Refresh");
    	refreshButton.setEnabled(false);
    	add(refreshButton);
    	refreshButton.addActionListener(this);
    }
	if (viewer.panelHideCtrlAltDelete.equals("No")) {
      ctrlAltDelButton = new Button("Send Ctrl-Alt-Del");
        ctrlAltDelButton.setEnabled(false);
        add(ctrlAltDelButton);
        ctrlAltDelButton.addActionListener(this);
    }
	if (viewer.panelHideQuality.equals("No")) {
	  qualityLabel = new Label("Image Quality");
		add(qualityLabel);
	  for (int x=0 ; x < 10 ; x++) { //draw slider
		slider[qualityIndex][x] = new Button(" ");
		slider[qualityIndex][x].setActionCommand(qualityIndex+""+x);
		add(slider[qualityIndex][x]);
		slider[qualityIndex][x].addActionListener(this);
	  }		
	  colorSlider(qualityIndex);
	}	
	if (viewer.panelHideCompression.equals("No")) {
	  compressionLabel = new Label("Compression Level");
		add(compressionLabel);
	  for (int x=0 ; x < 10 ; x++) { //draw slider
		slider[compressionIndex][x] = new Button(" ");
		slider[compressionIndex][x].setActionCommand(compressionIndex+""+x);
		add(slider[compressionIndex][x]);
		slider[compressionIndex][x].addActionListener(this);
	  }		
	  colorSlider(compressionIndex);
	}
	if 	(viewer.panelHideUser.equals("No") && viewer.autoUsername != null) {
	  userLabel = new Label("Username:  "+viewer.autoUsername);
	  add(userLabel);
	}
  }

  }
  //
  //  Internally called functions
  //


  // redraws the color slider specified by the index to its current state.
  private void colorSlider(int index) {
    for (int x=0; x < 10; x++) {
	  	Color clr;
		int level = (index==qualityIndex)?jpegQuality:compressLevel;  //this line is a limitation and if more sliders where used this could get messy
									      //Alternative:  move the level states of all sliders into a array
		if (x <= level)  //draw gradient
		   clr = mixColors(viewer.panelSliderLow,viewer.panelSliderHigh,(1.0*x)/9);
		else  //draw panelSliderUnselected color
			clr = viewer.panelSliderUnselected;
		slider[index][x].setBackground(clr); slider[index][x].setForeground(clr); //foreground and background set the same
    }
  }

  //merges the two passed colors by the ratio (0-1) and returns the resulting color 
  private Color mixColors(Color color1, Color color2, double ratio) {
    if (ratio <= 0) return color1;
    if (ratio >= 1) return color2;
    return (new Color((int)(color1.getRed()*(1.0-ratio)+color2.getRed()*ratio),
		    (int)(color1.getGreen()*(1.0-ratio)+color2.getGreen()*ratio),
		 (int)(color1.getBlue()*(1.0-ratio)+color2.getBlue()*ratio)));

  }

//Sets the encoding and other options.  mostly written statically because the new "User Friendly" Gui removes settings users wont understand
public void setEncodings() {
//Configurations
	//initial option values
    	  nEncodings = 0;
    	//enable rectangle copys
	  encodings[nEncodings++] = RfbProto.EncodingCopyRect;
	//best all purpose encoding
    	  int preferredEncoding = RfbProto.EncodingTight;
    	  @SuppressWarnings("unused")
		boolean enableCompressLevel = true;
    	  encodings[nEncodings++] = preferredEncoding;
	//failback encoding priorities
    	  encodings[nEncodings++] = RfbProto.EncodingHextile;
    	  encodings[nEncodings++] = RfbProto.EncodingZlib;
    	  encodings[nEncodings++] = RfbProto.EncodingCoRRE;
    	  encodings[nEncodings++] = RfbProto.EncodingRRE;
	//default compression level
		compressLevel = viewer.setCompression;
		if (compressLevel != 0) 
		   encodings[nEncodings++] = RfbProto.EncodingCompressLevel0 + compressLevel;
       		else //lowest compression setting disables compression
		  compressLevel = -1;
       //default image quality
	  //note this relies on using tight encoding as prefered (which is currently statically set)
		jpegQuality = viewer.setQuality;
		encodings[nEncodings++] = RfbProto.EncodingQualityLevel0 + jpegQuality;
       // Request cursor shape updates if necessary.
         requestCursorUpdates = true;
         encodings[nEncodings++] = RfbProto.EncodingXCursor;
         encodings[nEncodings++] = RfbProto.EncodingRichCursor;
         ignoreCursorUpdates = false;
	// remaining encoding settings - (im not sure what these do :-( )
	  encodings[nEncodings++] = RfbProto.EncodingPointerPos; //this may be removed if the cursor is hidden but it does no real harm
    	  encodings[nEncodings++] = RfbProto.EncodingLastRect;
    	  encodings[nEncodings++] = RfbProto.EncodingNewFBSize;
	//color level settings
	  eightBitColors = false; //8bit sux
	//reverseMouseButtons2And3 setting
	  reverseMouseButtons2And3 = false;
	//viewOnly off
	  viewOnly = false; //statically disabled - may be added in based on passed parameter
	//shareDesktop on
	  shareDesktop = true;	//their is no good reason to turn this off
	//debuging data dump
	
 /*   System.out.println("encodings array\n------------");
	for (int x=0; x < 20; x++) {
		System.out.println(x+": "+encodings[x]);
	}
	System.out.println("nEncodings: "+nEncodings); 
	System.out.println("compressLevel: "+compressLevel);
	System.out.println("jpegQuality: "+jpegQuality); 
	System.out.println("Flags\n------------");
	System.out.println("eightBitColors: "+(eightBitColors?"True":"False"));
	System.out.println("requestCursorUpdates: "+(requestCursorUpdates?"True":"False"));
	System.out.println("ignoreCursorUpdates: "+(ignoreCursorUpdates?"True":"False"));
	System.out.println("reverseMouseButtons2And3: "+(reverseMouseButtons2And3?"True":"False"));
	System.out.println("shareDesktop: "+(shareDesktop?"True":"False"));
	System.out.println("viewOnly: "+(viewOnly?"True":"False"));
*/
	//end of debug

	  viewer.setEncodings(); //apply encodings array changes to the canvas via the viewer function

}

  //
  // Externally called functions
  //

  public void enableButtons() {
	if (viewer.panelHideRefresh.equals("No"))
    	  refreshButton.setEnabled(true);
  }

  //
  // Disable all buttons on disconnect.
  //

  //rather useless because disconnect is removed but its too integrated to be easily removed
	//sidenote:  Disconnect is automatic and works well.  their is no issue with disabling disconnect controls
  public void disableButtonsOnDisconnect() {
	if (viewer.panelHideCtrlAltDelete.equals("No"))
    	  ctrlAltDelButton.setEnabled(false);
	if (viewer.panelHideRefresh.equals("No"))
    	  refreshButton.setEnabled(false);

    validate();
  }

  //
  // Enable/disable controls that should not be available in view-only
  // mode.
  //

  //viewonly will likely be restored so this remains
  public void enableRemoteAccessControls(boolean enable) {
	if (viewer.panelHideCtrlAltDelete.equals("No"))
    	  ctrlAltDelButton.setEnabled(enable);
  }


  //
  // Event processing.
  //

  @SuppressWarnings("deprecation")
public void actionPerformed(ActionEvent evt) {

    viewer.moveFocusToDesktop();

	//CTRL-ALT-DELETE
    if (evt.getSource() == ctrlAltDelButton) {
      try {
        final int modifiers = InputEvent.CTRL_MASK | InputEvent.ALT_MASK;

        KeyEvent ctrlAltDelEvent =
          new KeyEvent(this, KeyEvent.KEY_PRESSED, 0, modifiers, 127);
        viewer.rfb.writeKeyEvent(ctrlAltDelEvent);

        ctrlAltDelEvent =
          new KeyEvent(this, KeyEvent.KEY_RELEASED, 0, modifiers, 127);
        viewer.rfb.writeKeyEvent(ctrlAltDelEvent);

      } catch (IOException e) {
        e.printStackTrace();
      }
    } else
	//REFRESH 
	if (evt.getSource() == refreshButton) {
      try {
		RfbProto rfb = viewer.rfb;
		rfb.writeFramebufferUpdateRequest(0, 0, rfb.framebufferWidth,
					  							rfb.framebufferHeight, false);
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
	//Slider action handlers
     int index = Integer.parseInt(evt.getActionCommand());
	  int level = index % 10;
	  index = index / 10;
	  if (index == qualityIndex) viewer.setQuality = level;
	  else viewer.setCompression = level;
	  setEncodings(); //loads the encodings array - somewhat overkill here
     colorSlider(index);
System.out.println("jpegQuality: "+jpegQuality+" Compression: "+compressLevel);
   }
  }
}

