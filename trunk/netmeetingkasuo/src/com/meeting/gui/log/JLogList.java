package com.meeting.gui.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import com.meeting.gui.MainFrame;
import com.meeting.utils.DateUtils;

@SuppressWarnings("serial")
public class JLogList extends JPanel {
	private long maxRows = 1000;

	private JList logList;

	private SimpleAttributeSet requestAttributes;

	private SimpleAttributeSet responseAttributes;

	private LogListModel model;

	private List<Logger> loggers = new ArrayList<Logger>();

	private InternalLogAppender internalLogAppender = new InternalLogAppender();

	private boolean tailing = true;

	private Stack<Object> linesToAdd = new Stack<Object>();

	private EnableAction enableAction;

	private JCheckBoxMenuItem enableMenuItem;

	private Thread modelThread;

	public JLogList(final MainFrame instance, String title) {
		super(new BorderLayout());
		// this.instance = instance;
		model = new LogListModel();
		logList = new JList(model);
		logList.setBackground(Color.white);
		logList.setToolTipText(title);
		logList.setCellRenderer(new LogAreaCellRenderer());
		// logList.addListSelectionListener(new ListSelectionListener() {
		// public void valueChanged(ListSelectionEvent e) {
		// if (e.getValueIsAdjusting()) {
		// Object object = logList.getSelectedValue();
		// if (object != null) {
		// instance.getDetailLogJTA().setText(object.toString());
		// }
		// }
		// }
		// });

		JPopupMenu listPopup = new JPopupMenu();
		listPopup.add(new ClearAction());
		enableAction = new EnableAction();
		enableMenuItem = new JCheckBoxMenuItem(enableAction);
		enableMenuItem.setSelected(true);
		listPopup.add(enableMenuItem);
		listPopup.addSeparator();
		listPopup.add(new CopyAction());

		logList.setComponentPopupMenu(listPopup);

		setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		add(new JScrollPane(logList), BorderLayout.CENTER);

		requestAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(requestAttributes, Color.GRAY);

		responseAttributes = new SimpleAttributeSet();
		StyleConstants.setForeground(responseAttributes, Color.LIGHT_GRAY);
	}

	public JList getLogList() {
		return logList;
	}

	public void addLine(Object line) {
		if (!isEnabled())
			return;

		if (modelThread == null) {
			modelThread = new Thread(model);
			modelThread.start();
		}

		if (line instanceof LoggingEvent) {
			linesToAdd.push(new LoggingEventWrapper((LoggingEvent) line));
		} else {
			linesToAdd.push(line);
		}
	}

	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		logList.setEnabled(enabled);
		enableMenuItem.setSelected(enabled);
	}

	private static class LogAreaCellRenderer extends DefaultListCellRenderer {
		private Map<Level, Color> levelColors = new HashMap<Level, Color>();

		private LogAreaCellRenderer() {
			levelColors.put(Level.ERROR, new Color(192, 0, 0));
			levelColors.put(Level.INFO, new Color(0, 92, 0));
			levelColors.put(Level.WARN, Color.ORANGE.darker().darker());
			levelColors.put(Level.DEBUG, new Color(0, 0, 128));
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			JLabel component = (JLabel) super.getListCellRendererComponent(
					list, value, index, isSelected, cellHasFocus);

			if (value instanceof LoggingEventWrapper) {
				LoggingEventWrapper eventWrapper = (LoggingEventWrapper) value;

				if (levelColors.containsKey(eventWrapper.getLevel()))
					component.setForeground(levelColors.get(eventWrapper
							.getLevel()));
			}

			component.setToolTipText(component.getText());

			return component;
		}
	}

	private final static class LoggingEventWrapper {
		private final LoggingEvent loggingEvent;

		private String str;

		@SuppressWarnings("unused")
		private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

		public LoggingEventWrapper(LoggingEvent loggingEvent) {
			this.loggingEvent = loggingEvent;
		}

		public Level getLevel() {
			return loggingEvent.getLevel();
		}

		public String toString() {
			if (str == null) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateUtils.getTime(new Date(
						loggingEvent.timeStamp)));
				builder.append(" ").append(loggingEvent.getLevel()).append(
						" - ").append(loggingEvent.getMessage());
				str = builder.toString();
			}

			return str;
		}
	}

	public void addLogger(String loggerName, boolean addAppender) {
		Logger logger = Logger.getRootLogger();
		if (addAppender)
			logger.addAppender(internalLogAppender);
		loggers.add(logger);
	}

	public void setLevel(Level level) {
		for (Logger logger : loggers) {
			logger.setLevel(level);
		}
	}

	public Level getLevel() {
		Level level = null;
		for (Logger logger : loggers) {
			level = logger.getLevel();
		}
		return level;
	}

	private class InternalLogAppender extends AppenderSkeleton {
		protected void append(LoggingEvent event) {
			addLine(event);
		}

		public void close() {
		}

		public boolean requiresLayout() {
			return false;
		}
	}

	public boolean monitors(String loggerName) {
		for (Logger logger : loggers) {
			if (loggerName.startsWith(logger.getName())) {
				return true;
			}
		}
		return false;
	}

	public void removeLogger(String loggerName) {
		for (Logger logger : loggers) {
			if (loggerName.equals(logger.getName())) {
				logger.removeAppender(internalLogAppender);
			}
		}
	}

	public boolean isTailing() {
		return tailing;
	}

	public void setTailing(boolean tail) {
		this.tailing = tail;
	}

	private class ClearAction extends AbstractAction {
		public ClearAction() {
			super("清除");
		}

		public void actionPerformed(ActionEvent e) {
			model.clear();
			// instance.getDetailLogJTA().setText("");
			LogAppender.setSb(new StringBuilder());
		}
	}

	private class CopyAction extends AbstractAction {
		public CopyAction() {
			super("拷贝");
		}

		public void actionPerformed(ActionEvent e) {
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();

			StringBuffer buf = new StringBuffer();
			int[] selectedIndices = logList.getSelectedIndices();
			if (selectedIndices.length == 0) {
				for (int c = 0; c < logList.getModel().getSize(); c++) {
					buf.append(logList.getModel().getElementAt(c).toString());
					buf.append("\r\n");
				}
			} else {
				for (int c = 0; c < selectedIndices.length; c++) {
					buf.append(logList.getModel().getElementAt(
							selectedIndices[c]).toString());
					buf.append("\r\n");
				}
			}

			StringSelection selection = new StringSelection(buf.toString());
			clipboard.setContents(selection, selection);
		}
	}

	private class EnableAction extends AbstractAction {
		public EnableAction() {
			super("开启日志");
		}

		public void actionPerformed(ActionEvent e) {
			JLogList.this.setEnabled(enableMenuItem.isSelected());
		}
	}

	/**
	 * Internal list model that for optimized storage and notifications
	 * 
	 * @author Ole.Matzura
	 */

	private final class LogListModel extends AbstractListModel implements
			Runnable {
		private List<Object> lines = new LinkedList<Object>();

		public int getSize() {
			return lines.size();
		}

		public Object getElementAt(int index) {
			return lines.get(index);
		}

		public void clear() {
			int sz = lines.size();
			if (sz == 0)
				return;

			lines.clear();
			fireIntervalRemoved(this, 0, sz - 1);
		}

		public void run() {
			while (true) {
				try {
					if (!linesToAdd.isEmpty()) {
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								while (!linesToAdd.isEmpty()) {
									int sz = lines.size();
									lines.addAll(linesToAdd);
									linesToAdd.clear();
									fireIntervalAdded(this, sz, lines.size()
											- sz);
								}

								int cnt = 0;
								while (lines.size() > maxRows) {
									lines.remove(0);
									cnt++;
								}

								if (cnt > 0)
									fireIntervalRemoved(this, 0, cnt - 1);

								if (tailing) {
									logList
											.ensureIndexIsVisible(lines.size() - 1);
								}
							}
						});
					}

					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.getContentPane().add(new JLogList(null, "test"));
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
