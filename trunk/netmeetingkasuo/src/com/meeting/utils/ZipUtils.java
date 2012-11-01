package com.meeting.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;

public class ZipUtils {

	private static ZipUtils instance = new ZipUtils();

	private static final int BUFFEREDSIZE = 1024;

	private ZipUtils() {
	}

	public static ZipUtils getInstance() {
		return instance;
	}

	/**
	 * ѹ���ļ�
	 * 
	 * @param inputFilename
	 * @param zipFilename
	 * @throws IOException
	 */
	public synchronized void zip(String inputFilename, String zipFilename)
			throws IOException {
		zip(new File(inputFilename), zipFilename);
	}

	/**
	 * ѹ���ļ�
	 * 
	 * @param inputFile
	 * @param zipFilename
	 * @throws IOException
	 */
	public synchronized void zip(File inputFile, String zipFilename)
			throws IOException {
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				zipFilename));

		try {
			zip(inputFile, out, "");
		} catch (IOException e) {
			throw e;
		} finally {
			out.close();
		}
	}

	/**
	 * ѹ���ļ�
	 * 
	 * @param inputFile
	 * @param out
	 * @param base
	 * @throws IOException
	 */
	private synchronized void zip(File inputFile, ZipOutputStream out,
			String base) throws IOException {
		if (inputFile.isDirectory()) {
			File[] inputFiles = inputFile.listFiles();
			out.putNextEntry(new ZipEntry(base + "/"));
			base = base.length() == 0 ? "" : base + "/";
			for (int i = 0; i < inputFiles.length; i++) {
				zip(inputFiles[i], out, base + inputFiles[i].getName());
			}

		} else {
			if (base.length() > 0) {
				out.putNextEntry(new ZipEntry(base));
			} else {
				out.putNextEntry(new ZipEntry(inputFile.getName()));
			}

			FileInputStream in = new FileInputStream(inputFile);
			try {
				int c;
				byte[] by = new byte[BUFFEREDSIZE];
				while ((c = in.read(by)) != -1) {
					out.write(by, 0, c);
				}
			} catch (IOException e) {
				throw e;
			} finally {
				in.close();
			}
		}
	}

	/**
	 * ��ѹ���ļ�
	 * 
	 * @param zipFilename
	 * @param outputDirectory
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public synchronized void unzip(String zipFilename, String outputDirectory)
			throws IOException {
		File outFile = new File(outputDirectory);
		if (!outFile.exists()) {
			outFile.mkdirs();
		}

		ZipFile zipFile = new ZipFile(zipFilename);
		Enumeration en = zipFile.getEntries();
		ZipEntry zipEntry = null;
		while (en.hasMoreElements()) {
			zipEntry = (ZipEntry) en.nextElement();
			if (zipEntry.isDirectory()) {
				// mkdir directory
				String dirName = zipEntry.getName();
				dirName = dirName.substring(0, dirName.length() - 1);

				File f = new File(outFile.getPath() + File.separator + dirName);
				f.mkdirs();

			} else {
				// unzip file
				File f = new File(outFile.getPath() + File.separator
						+ zipEntry.getName());
				f.createNewFile();
				InputStream in = zipFile.getInputStream(zipEntry);
				FileOutputStream out = new FileOutputStream(f);
				try {
					int c;
					byte[] by = new byte[BUFFEREDSIZE];
					while ((c = in.read(by)) != -1) {
						out.write(by, 0, c);
					}
					// out.flush();
				} catch (IOException e) {
					throw e;
				} finally {
					out.close();
					in.close();
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		ZipUtils.getInstance().zip(
				"D:/EapStudio1.2/workspace/openeap/netmeeting/web/ssp",
				"d:/test.zip");
	}
}