$(function() {
	swfuploadInit();
});

// *********************************视频上传***********************************
var swfu;
var fileId = "";
var imageObj = null;
function swfuploadInit() {
	var settings = {
		flash_url : "../js/upload/swfupload.swf",
		// Relative to the SWF file
		upload_url : "../manage/video?oper=uploadVideo",
		post_params : {},
		file_size_limit : "100MB",
		file_types : "*.flv",
		file_types_description : "视频",
		file_upload_limit : 200,
		file_queue_limit : 0,
		debug : false,
		// Button settings
		// Relative to the Flash file
		button_image_url : "../images/netmeeting/upload.png",
		button_placeholder_id : "spanButtonPlaceHolder",
		button_width : "50",
		button_height : "22",
		button_cursor : SWFUpload.CURSOR.HAND,
		// The event handler
		file_queued_handler : fileQueued,
		file_queue_error_handler : fileQueueError,
		upload_progress_handler : uploadProcessHandler,
		upload_success_handler : uploadSuccessEventHandler
	};
	swfu = new SWFUpload(settings);
};

/**
 * 触发上传
 * 
 * @param file
 * @return
 */
var fileQueued = function(file) {
	fileId = file.id;
	swfu.startUpload(fileId);
	$("#processing").show();
	showTxtMsg("视频格式正确，开始上传.");
	var html = "<img alt='关闭' src='../images/netmeeting/upload_cancel.gif' onclick='fileCancel(\""
			+ file + "\")' style='cursor:hand;'/>"
	$("#closeUpload").attr("innerHTML", html);
};

/**
 * 文件加入上传队列错误
 * 
 * @param file
 * @param errorCode
 * @param message
 * @return
 */
function fileQueueError(file, errorCode, message) {
	try {
		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			var filesize = parseInt(file.size / 1000000) + "M";
			showTxtMsg("【" + file.name + "-" + filesize + "】文件过大.最大上传"
					+ swfu.settings.file_size_limit);
			break;
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			showTxtMsg("不能上传0byte的文件.")
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			showTxtMsg("上传文件类型不符合.");
			break;
		default:
			if (file !== null) {
				showTxtMsg("其他错误");
			}
			break;
		}
	} catch (ex) {

	}
}

/**
 * 处理上传过程中的事件
 * 
 * @param file
 * @param bytesLoaded
 * @param bytesTotal
 * @return
 */
var uploadProcessHandler = function(file, bytesLoaded, bytesTotal) {
	var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);
	showTxtMsg("视频正在上传中...");
	$("#uploadpic").progressBar(percent);
}

/**
 * 上传成功事件
 * 
 * @param file
 * @param server_data
 * @return
 */
var uploadSuccessEventHandler = function(file, server_data) {
	var json = JSON.parse(server_data);
	var videoName = decodeURIComponent(json["videoName"]);
	var videoSize = json["videoSize"];
	var videoId = json["videoId"];
	var html = "<table width='100%' border='1' cellspacing='0' cellpadding='0' bordercolor='#006699' bordercolordark='#FFFFFF' style='font-size:12px;'>"
			+ "<tr style='background-image:url(../images/netmeeting/bg.gif);font-size:13px;font-weight:bold;color:#006699;'>"
			+ "<td height='22px'>视频名称</td>"
			+ "<td>视频大小</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ videoName
			+ "</td>"
			+ "<td>"
			+ videoSize
			+ "</td>" + "</tr>" + "</table>";
	$("#uploadTable").attr("innerHTML", html);
	$("#closeUpload").attr("innerHTML", "");
	showTxtMsg("视频上传成功！");
	videoPlayClose();
};

/**
 * 取消上传
 * 
 * @return
 */
function fileCancel(file) {
	swfu.stopUpload();
	if (confirm('是否确认取消？')) {
		swfu.cancelUpload();
		return true;
	} else {
		swfu.startUpload(file.id);
		return false;
	}
}

/**
 * 显示上传提示信息
 * 
 * @param msg
 * @return
 */
function showTxtMsg(msg) {
	$("#uploadtxt").attr("innerHTML", msg);
}

/**
 * 关闭页面
 * 
 * @return
 */
function videoUploadClose() {
	parent.videoPlayUploadClose();
}

/**
 * 关闭页面
 * 
 * @return
 */
function videoPlayClose() {
	parent.videoPlayDiagClose();
}
