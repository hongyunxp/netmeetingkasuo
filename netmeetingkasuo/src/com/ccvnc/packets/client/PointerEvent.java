package com.ccvnc.packets.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.ccvnc.Screen;
import com.ccvnc.packets.Packet;

/**
 * PointerEvent - mouse moved or mouse button clicked.
 * 
 * Format:
 * <table>
 * <tr>
 * <th>Bytes</th>
 * <th>Type</th>
 * <th>Description</th>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>Packet type (5)</td>
 * </tr>
 * <tr>
 * <td>1</td>
 * <td>U8</td>
 * <td>buttonMask</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U16</td>
 * <td>xPosition</td>
 * </tr>
 * <tr>
 * <td>2</td>
 * <td>U16</td>
 * <td>yPosition</td>
 * </tr>
 * </table>
 * 
 * @author Volodymyr M. Lisivka
 */
public class PointerEvent extends ClientPacket {
	private int buttonMask = -1, xPosition = -1, yPosition = -1;

	public PointerEvent() {
	}

	public PointerEvent(Screen screen, int buttonMask, int xpos, int ypos) {
		this.screen = screen;
		this.buttonMask = buttonMask;
		this.xPosition = xpos;
		this.yPosition = ypos;
	}

	/**
	 * Replace two mouse events by new one.
	 * 
	 * Replace old mouse move event by new one. Replace mouse move by mouse
	 * click. Don't replace mouse click.
	 */
	public Packet squeeze(Packet packet2) {
		// Don't replace mouse click.
		if (buttonMask != 0)
			return null;

		return packet2;
	}

	public int getPacketType() {
		return CLIENT_POINTER_EVENT;
	}

	public void writePacketData(DataOutputStream os) throws IOException {
		os.writeByte(buttonMask);
		os.writeShort(xPosition);
		os.writeShort(yPosition);
	}

	public void dump(PrintStream out, boolean verbose) {
		out.println("PointerEvent, buttonMask=0x"
				+ Integer.toHexString(buttonMask) + ",xpos=" + xPosition
				+ ",ypos=" + yPosition);
	}

	public void readPacketData(DataInputStream is) throws IOException {
		buttonMask = is.readUnsignedByte();
		xPosition = is.readUnsignedShort();
		yPosition = is.readUnsignedShort();
	}

	public void validate() {
		super.validate();
		if (buttonMask < 0 || xPosition < 0 || yPosition < 0
				|| xPosition >= screen.getFramebufferWidth()
				|| yPosition >= screen.getFramebufferHeight())
			throw new RuntimeException("Pointer event is not valid.");
	}

}
