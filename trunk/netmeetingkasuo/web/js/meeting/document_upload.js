$(function() {
	if (FILEID == "") {
		swfuploadInit();
	} else {
		convert();
	}
});

// *********************************�ĵ��ϴ�***********************************
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
		file_types_description : "�ĵ�",
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
	showTxtMsg("�ĵ���ʽ��ȷ����ʼ�ϴ�.");
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
	showTxtMsg("�ĵ������ϴ���...");
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
	var fileName = decodeURIComponent(json["fileName"]);
	var fileSize = json["fileSize"];
	FILEID = json["fileId"];
	FILENAME = fileName;
	var html = "<table width='100%' border='1' cellspacing='0' cellpadding='0' bordercolor='#006699' bordercolordark='#FFFFFF' style='font-size:12px;'>"
			+ "<tr style='background-image:url(../images/netmeeting/bg.gif);font-size:13px;font-weight:bold;color:#006699;'>"
			+ "<td height='22px'>�ĵ�����</td>"
			+ "<td>�ĵ���С</td>"
			+ "</tr>"
			+ "<tr>"
			+ "<td>"
			+ fileName
			+ "</td>"
			+ "<td>"
			+ fileSize
			+ "</td>" + "</tr>" + "</table>";
	$("#uploadTable").attr("innerHTML", html);
	showTxtMsg("�ĵ��ϴ��ɹ���");
	convert();

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
 * ת���ĵ�
 * 
 * @return
 */
function convert() {
	showTxtMsg("�ĵ�����ת���У������ĵȴ�!");
	var html = "ת�����ȣ�<img alt='' src='../images/netmeeting/process_convert.gif' border='0'/>";
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
								+ "<td height='22px'>�ĵ�����</td>"
								+ "<td>�ĵ���С</td>"
								+ "<td>�ĵ�ҳ��</td>"
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
						showTxtMsg("�ĵ�ת����ϣ�");
						$("#btns").hide();
						$("#processing").attr("innerHTML", "");
						$("#uploadTable").attr("innerHTML", table);
						if (DOCUMENTMGR == "") {
							$("#docClose").hide();
							$("#docPlay").show();
						}else{
							Dialog.confirm("�ĵ�ת����ϣ��Ƿ���������?", function() {
								parent.documentManagementUploadClose(FILEID,
										FILENAME, 1);
							});
						}
					});
}

/**
 * �����ĵ�
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
 * �ر�ҳ��
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
