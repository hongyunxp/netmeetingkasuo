package com.meeting.service;

import java.util.HashMap;

import com.meeting.utils.AppConfigure;

public class DrawLine2ImageService {

	// private static final Logger logger = Logger
	// .getLogger(DrawLine2ImageService.class);

	private static DrawLine2ImageService instance;

	public static final String KEY_DRAW_LINE_2_IMAGE = "DRAW_LINE_2_IMAGE";
	public static final String KEY_DRAW_ARROWLINE_2_IMAGE = "DRAW_ARROWLINE_2_IMAGE";
	public static final String KEY_DRAW_POLYLINE_2_IMAGE = "DRAW_POLYLINE_2_IMAGE";
	public static final String KEY_DRAW_RECTANGLE_2_IMAGE = "DRAW_RECTANGLE_2_IMAGE ";
	public static final String KEY_DRAW_ELLIPSE_2_IMAGE = "DRAW_ELLIPSE_2_IMAGE ";
	public static final String KEY_SET_IMAGE_SIZE = "SET_IMAGE_SIZE";

	private DrawLine2ImageService() {
	}

	public static synchronized DrawLine2ImageService getInstance() {
		if (instance == null) {
			instance = new DrawLine2ImageService();
		}
		return instance;
	}

	/**
	 * 获取图片转换命令
	 * 
	 * @return
	 */
	static String getPathToImageMagic() {
		return AppConfigure.imagemagick_path + "/convert";
	}

	/**
	 * 画直线到图片上
	 * 
	 * @param points
	 * @param absolutePath
	 * @param absolutePath2
	 */
	public HashMap<String, Object> drawLine2Image(String color, String points,
			String srcfile, String destfile) {
		srcfile = srcfile.replaceAll("\\\\", "/");
		destfile = destfile.replaceAll("\\\\", "/");

		String linePoints = "\"line " + points + "\"";
		String[] args = new String[] {
				DrawLine2ImageService.getPathToImageMagic(), "-stroke", color,
				"-strokewidth", "5", "-draw", linePoints, srcfile, destfile };
		return ExecuteService.executeScript(KEY_DRAW_LINE_2_IMAGE, args);
	}

	/**
	 * 画曲线在图片上
	 * 
	 * @param srcfile
	 * @param destfolder
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> drawPolyLine2Image(String color,
			String points, String srcfile, String destfile) throws Exception {
		srcfile = srcfile.replaceAll("\\\\", "/");
		destfile = destfile.replaceAll("\\\\", "/");
		String param = "\"polyline " + points + "\"";
		String[] argv = new String[] {
				DrawLine2ImageService.getPathToImageMagic(), "-fill", "none",
				"-stroke", color, "-strokewidth", "5", "-draw", param, srcfile,
				destfile };
		return ExecuteService.executeScript(KEY_DRAW_POLYLINE_2_IMAGE, argv);
	}

	/**
	 * 画带有箭头的线段
	 * 
	 * @param points
	 * @param srcfile
	 * @param destfile
	 * @return
	 */
	public HashMap<String, Object> drawArrowLine2Image(String color,
			String points, String srcfile, String destfile) {
		srcfile = srcfile.replaceAll("\\\\", "/");
		destfile = destfile.replaceAll("\\\\", "/");

		String startPoint = points.split(" ")[0];
		String endPoint = points.split(" ")[1];
		double startX = Double.parseDouble(startPoint.split(",")[0]);
		double startY = Double.parseDouble(startPoint.split(",")[1]);
		double endX = Double.parseDouble(endPoint.split(",")[0]);
		double endY = Double.parseDouble(endPoint.split(",")[1]);

		double val1 = endY - startY;
		double val2 = endX - startX;
		double val3 = val2 / val1;
		int val4 = (int) Math.toDegrees(Math.atan(val3));
		if (endY < startY && endX >= startX) {
			val4 = Math.abs(val4) - 90;
		} else if (endY < startY && endX < startX) {
			val4 = -Math.abs(val4) - 90;
		} else if (endY > startY && endX <= startX) {
			val4 = Math.abs(val4) + 90;
		} else if (endY > startY && endX > startX) {
			val4 = 90 - Math.abs(val4);
		}

		String linePoints = "\"line " + points + "\"";
		String path = "M 0,0 l -15,-5  +5,+5  -5,+5  +15,-5 z";
		String drawStr = "\"stroke " + color + " fill " + color + " translate "
				+ endPoint + " rotate " + val4 + " path '" + path + "'\"";
		String[] args = new String[] {
				DrawLine2ImageService.getPathToImageMagic(), "-stroke", color,
				"-strokewidth", "5", "-draw", linePoints,// "10,30 80,200",
				"-draw", drawStr, srcfile, destfile };

		return ExecuteService.executeScript(KEY_DRAW_ARROWLINE_2_IMAGE, args);

	}

	/***
	 * 画矩形
	 * 
	 * @param points
	 * @param srcfile
	 * @param destfile
	 * @return
	 */
	public HashMap<String, Object> drawRectangle2Image(String color,
			String points, String srcfile, String destfile) {
		srcfile = srcfile.replaceAll("\\\\", "/");
		destfile = destfile.replaceAll("\\\\", "/");
		String param = "\"rectangle " + points + "\"";
		String[] argv = new String[] {
				DrawLine2ImageService.getPathToImageMagic(), "-fill", "none",
				"-stroke", color, "-strokewidth", "5", "-draw", param, srcfile,
				destfile };
		return ExecuteService.executeScript(KEY_DRAW_RECTANGLE_2_IMAGE, argv);
	}

	/***
	 * 画椭圆
	 * 
	 * @param points
	 * @param srcfile
	 * @param destfile
	 * @return
	 */
	public HashMap<String, Object> drawEllipse2Image(String color,
			String points, String width, String height, String srcfile,
			String destfile) {
		srcfile = srcfile.replaceAll("\\\\", "/");
		destfile = destfile.replaceAll("\\\\", "/");

		String startPoint = points.split(" ")[0];
		String endPoint = points.split(" ")[1];
		double startX = Double.parseDouble(startPoint.split(",")[0]);
		double startY = Double.parseDouble(startPoint.split(",")[1]);
		double endX = Double.parseDouble(endPoint.split(",")[0]);
		double endY = Double.parseDouble(endPoint.split(",")[1]);
		startX = startX + Double.parseDouble(width);
		startY = startY + Double.parseDouble(height);

		double val1 = endY - startY;
		double val2 = endX - startX;
		double val3 = val2 / val1;
		int val4 = (int) Math.toDegrees(Math.atan(val3));
		if (endY < startY && endX >= startX) {
			val4 = Math.abs(val4) - 90;
		} else if (endY < startY && endX < startX) {
			val4 = -Math.abs(val4) - 90;
		} else if (endY > startY && endX <= startX) {
			val4 = Math.abs(val4) + 90;
		} else if (endY > startY && endX > startX) {
			val4 = 90 - Math.abs(val4);
		}

		String param = "\"ellipse " + startX + "," + startY + " " + width + ","
				+ height + " 0,360\"";
		String[] argv = new String[] {
				DrawLine2ImageService.getPathToImageMagic(), "-fill", "none",
				"-stroke", color, "-strokewidth", "5", "-draw", param, srcfile,
				destfile };
		return ExecuteService.executeScript(KEY_DRAW_ELLIPSE_2_IMAGE, argv);
	}

	/**
	 * 设置图片大小
	 * 
	 * @param string
	 * @param srcImagePath
	 * @param imagePath
	 */
	public HashMap<String, Object> setImageSize(String size,
			String srcImagePath, String imagePath) {
		srcImagePath = srcImagePath.replaceAll("\\\\", "/");
		imagePath = imagePath.replaceAll("\\\\", "/");
		String[] argv = new String[] {
				DrawLine2ImageService.getPathToImageMagic(), "-resize",
				size + "!", srcImagePath, imagePath };
		return ExecuteService.executeScript(KEY_DRAW_POLYLINE_2_IMAGE, argv);
	}

}
