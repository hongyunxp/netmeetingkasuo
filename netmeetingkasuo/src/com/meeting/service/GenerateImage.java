package com.meeting.service;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.meeting.utils.AppConfigure;

public class GenerateImage {

	private static final Logger logger = Logger.getLogger(GenerateImage.class);

	private static GenerateImage instance;

	public static final String KEY_PROCESS_THUMB = "processThumb";

	public static final String KEY_GENERATE_BATCH_THUMB = "generateBatchThumb";

	public static final String KEY_CONVERT_SINGLEJPG = "convertSingleJpg";

	public static final String KEY_CONVERT_IMAGE_SIZE = "convertImageByTypeAndSize";

	public static final String KEY_CONVERT_IMAGE_SIZE_DEPTH = "convertImageByTypeAndSizeAndDepth";

	private GenerateImage() {
	}

	public static synchronized GenerateImage getInstance() {
		if (instance == null) {
			instance = new GenerateImage();
		}
		return instance;
	}

	/**
	 * 获取图片转换命令
	 * 
	 * @return
	 */
	static String getPathToImageMagic() {
		// String pathToImageMagic = AppConfigure
		// .getProperty(AppConfigure.KEY_IMAGEMAGICK);
		// if (!pathToImageMagic.equals("")
		// && !pathToImageMagic.endsWith(File.separator)) {
		// pathToImageMagic += File.separator;
		// }
		// pathToImageMagic += "convert";
		return AppConfigure.imagemagick_path + "/convert";
	}

	/**
	 * 转换图片
	 * 
	 * @param srcfile
	 * @param destfolder
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, HashMap<String, Object>> convertImage(
			String srcfile, String destfolder) throws Exception {
		logger.info("装换IMG，源文件：" + srcfile + "，目标目录：" + destfolder);
		HashMap<String, HashMap<String, Object>> returnMap = new HashMap<String, HashMap<String, Object>>();

		HashMap<String, Object> processThumb = generateBatchThumb(srcfile,
				destfolder);
		logger.info("装换IMG完毕！" + srcfile);
		returnMap.put(KEY_PROCESS_THUMB, processThumb);

		return returnMap;
	}

	/**
	 * 生成批量图片
	 * 
	 * @param inputfile
	 * @param outputpath
	 * @return
	 */
	public HashMap<String, Object> generateBatchThumb(String inputfile,
			String outputpath) {

		String srcfile = inputfile.replaceAll("\\\\", "/");
		String fileid = srcfile.substring(srcfile.lastIndexOf("/") + 1);
		fileid = fileid.substring(0, fileid.lastIndexOf("."));
		String destfile = outputpath.replaceAll("\\\\", "/");
		String[] argv = new String[] { GenerateImage.getPathToImageMagic(),
				"-verbose", "-sample", "100%", "-density", "150", "-quality",
				"100", srcfile,
				destfile + "/" + fileid + "_%03d." + AppConfigure.PNG_EXT };

		return ExecuteService.executeScript(KEY_GENERATE_BATCH_THUMB, argv);
	}

	/**
	 * 转换单个JPG格式图片
	 * 
	 * @param inputFile
	 * @param outputfile
	 * @return
	 */
	public HashMap<String, Object> convertSingleJpg(String inputFile,
			String outputfile) {
		String[] argv = new String[] { getPathToImageMagic(), inputFile,
				outputfile + ".jpg" };
		return ExecuteService.executeScript(KEY_CONVERT_SINGLEJPG, argv);
	}

	/**
	 * 转换指定类型，指定大小的图片
	 * 
	 * @param inputFile
	 * @param outputfile
	 * @param width
	 * @param height
	 * @return
	 */
	public HashMap<String, Object> convertImageByTypeAndSize(String inputFile,
			String outputfile, int width, int height) {
		String[] argv = new String[] { getPathToImageMagic(), "-size",
				width + "x" + height, inputFile, outputfile };
		return ExecuteService.executeScript(KEY_CONVERT_IMAGE_SIZE, argv);
	}

	/**
	 * 转换指定类型，指定大小，指定深度的图片
	 * 
	 * @param inputFile
	 * @param outputfile
	 * @param width
	 * @param height
	 * @param depth
	 * @return
	 */
	public HashMap<String, Object> convertImageByTypeAndSizeAndDepth(
			String inputFile, String outputfile, int width, int height,
			int depth) {
		String[] argv = new String[] { getPathToImageMagic(), "-size",
				width + "x" + height, "-depth", Integer.toString(depth),
				inputFile, outputfile };
		return ExecuteService.executeScript(KEY_CONVERT_IMAGE_SIZE_DEPTH, argv);
	}

}
