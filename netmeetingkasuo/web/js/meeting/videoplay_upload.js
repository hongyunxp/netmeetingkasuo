$(function() {
	swfuploadInit();
});

// *********************************��Ƶ�ϴ�***********************************
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
		file_types_description : "��Ƶ",
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
 * �����ϴ�
 * 
 * @param file
 * @return
 */
var fileQueued = function(file) {
	fileId = file.id;
	swfu.startUpload(fileId);
	$("#processing").show();
	showTxtMsg("��Ƶ��ʽ��ȷ����ʼ�ϴ�.");
	var html = "<img alt='�ر�' src='../images/netmeeting/upload_cancel.gif' onclick='fileCancel(\""
			+ file + "\")' style='cursor:hand;'/>"
	$("#closeUpload").attr("innerHTML", html);
};

/**
 * �ļ������ϴ����д���
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
			showTxtMsg("��" + file.name + "-" + filesize + "���ļ�����.����ϴ�"
					+ swfu.settings.file_size_limit);
			break;
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			showTxtMsg("�����ϴ�0byte���ļ�.")
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			showTxtMsg("�ϴ��ļ����Ͳ�����.");
			break;
		default:
			if (file !== null) {
				showTxtMsg("��������");
			}
			break;
		}
	} catch (ex) {

	}
}

/**
 * �����ϴ������е��¼�
 * 
 * @param file
 * @param bytesLoaded
 * @param bytesTotal
 * @return
 */
var uploadProcessHandler = function(file, bytesLoaded, bytesTotal) {
	var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);
	showTxtMsg("��Ƶ�����ϴ���...");
	$("#uploadpic").progressBar(percent);
}

/**
 * �ϴ��ɹ��¼�
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
			+ "<td height='22px'>��Ƶ����</td>"
			+ "<td>��Ƶ��С</td>"
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
	showTxtMsg("��Ƶ�ϴ��ɹ���");
	videoPlayClose();
};

/**
 * ȡ���ϴ�
 * 
 * @return
 */
function fileCancel(file) {
	swfu.stopUpload();
	if (confirm('�Ƿ�ȷ��ȡ����')) {
		swfu.cancelUpload();
		return true;
	} else {
		swfu.startUpload(file.id);
		return false;
	}
}

/**
 * ��ʾ�ϴ���ʾ��Ϣ
 * 
 * @param msg
 * @return
 */
function showTxtMsg(msg) {
	$("#uploadtxt").attr("innerHTML", msg);
}

/**
 * �ر�ҳ��
 * 
 * @return
 */
function videoUploadClose() {
	parent.videoPlayUploadClose();
}

/**
 * �ر�ҳ��
 * 
 * @return
 */
function videoPlayClose() {
	parent.videoPlayDiagClose();
}
