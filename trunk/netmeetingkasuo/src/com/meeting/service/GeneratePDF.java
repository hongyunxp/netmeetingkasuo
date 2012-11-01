package com.meeting.service;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.meeting.utils.AppConfigure;

/**
 * 文档转换成PDF
 * 
 * @author zcg
 * 
 */
public class GeneratePDF {

	private static final Logger log = Logger.getLogger(GeneratePDF.class);

	public static final String KEY_PROCESS_OFFICE = "processOpenOffice";
	public static final String KEY_JOD_CONVERT = "doJodConvert";

	private static GeneratePDF instance;

	private GeneratePDF() {
	}

	public static synchronized GeneratePDF getInstance() {
		if (instance == null) {
			instance = new GeneratePDF();
		}
		return instance;
	}

	/**
	 * 转换成PDF文档
	 * 
	 * @param srcfile
	 * @param destfile
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, HashMap<String, Object>> convertPDF(String srcfile,
			String destfile) throws Exception {
		log.info("装换PDF，源文件：" + srcfile + "，目标文件：" + destfile);
		HashMap<String, HashMap<String, Object>> returnError = new HashMap<String, HashMap<String, Object>>();
		// doJodConvert 将文档转换为PDF
		HashMap<String, Object> processOpenOffice = doJodConvert(srcfile,
				destfile);
		log.info("装换PDF完毕！" + srcfile);
		returnError.put(KEY_PROCESS_OFFICE, processOpenOffice);
		return returnError;
	}

	/**
	 * 调用JOD，将office文档转换为PDF
	 * 
	 * @param current_dir
	 * @param fileFullPath
	 * @param destinationFolder
	 * @param outputfile
	 * @return
	 */
	public HashMap<String, Object> doJodConvert(String srcfilepath,
			String destfilepath) {
		// Path to all JARs of JOD
		String jodClassPathFolder = AppConfigure.jod_path;

		StringBuffer sBuffer = new StringBuffer();
		sBuffer.append(jodClassPathFolder + "/commons-cli-1.1.jar").append(";");
		sBuffer.append(jodClassPathFolder + "/commons-io-1.4.jar").append(";");
		sBuffer.append(jodClassPathFolder + "/json-20080701.jar").append(";");
		sBuffer.append(jodClassPathFolder + "/juh-3.1.0.jar").append(";");
		sBuffer.append(jodClassPathFolder + "/jurt-3.1.0.jar").append(";");
		sBuffer.append(jodClassPathFolder + "/ridl-3.1.0.jar").append(";");

		// Create the Content of the Converter Script (.bat or .sh File)
		String[] argv = new String[] { "java", "-classpath",
				sBuffer.toString(), "-jar",
				jodClassPathFolder + "/jodconverter-core-3.0-beta-3.jar",
				srcfilepath, destfilepath };

		return ExecuteService.executeScript(KEY_JOD_CONVERT, argv);

	}

}
