package com.ccvnc.packets.server.rect;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.Screen;

/**
 * Rectangle - interface for rectangles with coordinates of top left corner
 * (x,y) and with width and height.
 * 
 * @author Volodymyr M. Lisivka
 */
public interface Rectangle {
	/**
	 * Set associated screen.
	 */
	public void setScreen(Screen screen);

	/**
	 * Returns associated screen.
	 */
	public Screen getScreen();

	/**
	 * Read rectangle data(without header) from input stream.
	 * 
	 * @param encodingType
	 *            encoding type of this rectangle
	 * @param xpos
	 *            coordinate of left side
	 * @param ypos
	 *            coordinate of top side
	 * @param width
	 *            width
	 * @param height
	 *            height
	 * @param is
	 *            a DataInputStream
	 */
	public void read(int encodingType, int xpos, int ypos, int width,
			int height, DataInputStream is) throws IOException;

	/**
	 * Write rectangle data (with header) to output stream,
	 * 
	 */
	public void write(DataOutputStream os) throws IOException;

	/**
	 * Convert rectangle data to pixel format of another screen and return a new
	 * rectangle.
	 */
	public Rectangle convertPixelFormat(Screen screen2);

	/**
	 * Validate content of this rectangle.
	 */
	public void validate();

	/**
	 * Dump this rectangle to output stream.
	 * 
	 * @param out
	 *            a PrintStream
	 * @param verbose
	 *            be a bit verbose
	 */
	public void dump(PrintStream out, boolean verbose);

	/**
	 * Sets EncodingType
	 * 
	 * @param encodingType
	 *            an int
	 */
	public void setEncodingType(int encodingType);

	/**
	 * Returns EncodingType
	 * 
	 * @return an int
	 */
	public int getEncodingType();

	/**
	 * Sets X
	 * 
	 * @param x
	 *            an int
	 */
	public void setX(int x);

	/**
	 * Returns X
	 * 
	 * @return an int
	 */
	public int getX();

	/**
	 * Sets Y
	 * 
	 * @param y
	 *            an int
	 */
	public void setY(int y);

	/**
	 * Returns Y
	 * 
	 * @return an int
	 */
	public int getY();

	/**
	 * Sets Width
	 * 
	 * @param width
	 *            an int
	 */
	public void setWidth(int width);

	/**
	 * Returns Width
	 * 
	 * @return an int
	 */
	public int getWidth();

	/**
	 * Sets Height
	 * 
	 * @param height
	 *            an int
	 */
	public void setHeight(int height);

	/**
	 * Returns Height
	 * 
	 * @return an int
	 */
	public int getHeight();

}
