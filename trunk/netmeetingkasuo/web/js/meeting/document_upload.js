$(function() {
	if (FILEID == "") {
		swfuploadInit();
	} else {
		convert();
	}
});

// *********************************文档上传***********************************
var swfu;
var fileId = "";
var imageObj = null;
function swfuploadInit() {
	var settings = {
		flash_url : "../js/upload/swfupload.swf",
		// Relative to the SWF file
		upload_url : "../manage/document?oper=uploadFile",
		post_params : {},
		file_size_limit : "100MB",
		file_types : "*.pdf;*.doc;*.ppt;*.txt;*.jpg;*.png;*.gif",
		file_types_description : "文档",
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
	showTxtMsg("文档格式正确，开始上传.");
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
	showTxtMsg("文档正在上传中...");
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
	var fileName = decodeURIComponent(json["fileName"]);
	var fileSize = json["fileSize"];
	FILEID = json["fileId"];
	FILENAME = fileName;
	var html = "<table width='100%' border='1' cellspacing='0' cellpadding='0' bordercolor='#006699' bordercolordark='#FFFFFF' style='font-size:12px;'>"
			+ "<tr style='background-image:url(../images/netmeeting/bg.gif);font-size:13px;font-weight:bold;color:#006699;'>"
			+ "<td height='22px'>文档名称</td>"
			+ "<td>文档大小</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ fileName
			+ "</td>"
			+ "<td>"
			+ fileSize
			+ "</td>" + "</tr>" + "</table>";
	$("#uploadTable").attr("innerHTML", html);
	showTxtMsg("文档上传成功！");
	convert();

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
 * 转换文档
 * 
 * @return
 */
function convert() {
	showTxtMsg("文档正在转换中，请耐心等待!");
	var html = "转换进度：<img alt='' src='../images/netmeeting/process_convert.gif' border='0'/>";
	$("#processing").show();
	$("#processing").attr("innerHTML", html);
	$("#spanButtonPlaceHolder").hide();

	var url = "../manage/document?oper=convertFile";
	$
			.post(
					url,
					{
						fileid : FILEID
					},
					function(data) {
						var json = JSON.parse(data);
						var fileName = json["fileName"];
						var fileSize = json["fileSize"];
						var filePage = json["filePage"];

						var table = "<table width='100%' border='1' cellspacing='0' cellpadding='0' bordercolor='#006699' bordercolordark='#FFFFFF' style='font-size:12px;'>"
								+ "<tr style='background-image:url(../images/netmeeting/bg.gif);font-size:13px;font-weight:bold;color:#006699;'>"
								+ "<td height='22px'>文档名称</td>"
								+ "<td>文档大小</td>"
								+ "<td>文档页数</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td>"
								+ fileName
								+ "</td>"
								+ "<td>"
								+ fileSize
								+ "</td>"
								+ "<td>"
								+ filePage
								+ "</td>" + "</tr>" + "</table>";
						showTxtMsg("文档转换完毕！");
						$("#btns").hide();
						$("#processing").attr("innerHTML", "");
						$("#uploadTable").attr("innerHTML", table);
						if (DOCUMENTMGR == "") {
							$("#docClose").hide();
							$("#docPlay").show();
						}else{
							Dialog.confirm("文档转换完毕，是否立即播放?", function() {
								parent.documentManagementUploadClose(FILEID,
										FILENAME, 1);
							});
						}
					});
}

/**
 * 播放文档
 * 
 * @return
 */
function documentPlay() {
	if (DOCUMENTMGR == "") {
		parent.documentPlay(FILEID, FILENAME, 1);
	}else{
		parent.documentManagementUploadClose(FILEID, FILENAME, 1);
	}
}

/**
 * 关闭页面
 * 
 * @return
 */
function documentUploadClose() {
	if (DOCUMENTMGR == "") {
		parent.documentDiagClose();
	}else{
		parent.documentManagementUploadClose(FILEID, FILENAME, 0);
	}
}
