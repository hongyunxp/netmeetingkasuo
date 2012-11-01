<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<link type="text/css" rel="stylesheet" href="<%=basePath %>css/style-all.css">
<script type="text/javascript" src="<%=basePath %>js/swfupload.js"></script>
<script type="text/javascript" src="<%=basePath %>js/swfupload.swfobject.js"></script>
<style>
.progressBar{
	width:80%;
}
</style>
<script type="text/javascript">
var basepath = "<%=basePath%>";

$(function(){
	swfuploadInit();
});

//*********************************�ĵ��ϴ�***********************************
var swfu;
var fileId="";
var imageObj = null;
function swfuploadInit() {
	var settings = {
		flash_url : basepath+"js/swfupload.swf",
		upload_url : basepath + "servlet/attachment.do?oper=uploadfile", // Relative to the SWF
		// file
		post_params : {},
		file_size_limit : "5MB",
		file_types : "*.jpg;*.gif;*.png",
		file_types_description : "ͼƬ",
		file_upload_limit : 200,
		file_queue_limit : 0,
		debug : false,
		// Button settings
		button_image_url : basepath+"images/server/upload.png", // Relative to the Flash file
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
 * �����ϴ������е��¼�
 */
var uploadProcessHandler = function(file, bytesLoaded, bytesTotal) {
	var percent = Math.ceil((bytesLoaded / bytesTotal) * 100);
	if(percent>90){
		percent=percent-1;
	}
	$("#uploadPb").progressBar(percent);
}

/**
 * �ļ������ϴ����д���
 */
function fileQueueError(file, errorCode, message) {
	try {
		switch (errorCode) {
		case SWFUpload.QUEUE_ERROR.FILE_EXCEEDS_SIZE_LIMIT:
			var filesize = parseInt(file.size / 1000000) + "M";
			alert("��" + file.name + "-" + filesize + "���ļ�����.����ϴ�"
					+ swfu.settings.file_size_limit);
			break;
		case SWFUpload.QUEUE_ERROR.ZERO_BYTE_FILE:
			alert("�����ϴ�0byte���ļ�.");
			break;
		case SWFUpload.QUEUE_ERROR.INVALID_FILETYPE:
			alert("�ϴ��ļ����Ͳ�����.");
			break;
		default:
			if (file !== null) {
				alert("��������");
			}
			break;
		}
	} catch (ex) {

	}
}

/**
 *  �����ϴ�
 */
var fileQueued = function(file) {
	fileId=file.id;
	swfu.startUpload(fileId);

	var blockmessage = "<div id='showMsg'><span class='progressBar' id='uploadPb' style='font-size:12px;width:150px;'>0%</span>" +
					"<a href='#' onclick='cancelUI()' style='font-size:12px'>" +
					"<img src='"+basepath+"images/server/cancelbutton_2.gif' border='0'/></a></div>";
	$.blockUI( {
			message : blockmessage,
			css : {
				'cursor' : 'default',
				backgroundColor : '#FFFFFF',
				'-webkit-border-radius' : '10px',
				'-moz-border-radius' : '10px',
				opacity : .9,
				width : '350px',
				height : '30',
				left : '150px',
				textAlign: 'center',
				padding : '5px'
			}
		});
};


/**
 * �ϴ��ɹ��¼�
 */
var uploadSuccessEventHandler = function(file, server_data) {
	$.unblockUI();
	var json = JSON.parse(server_data);
	imageObj = new Object();
	imageObj.attachId = json["attachId"];
	imageObj.attachName = decodeURI(json["attachName"]);
	imageObj.attachSize = json["attachSize"];
	imageObj.attachPath = json["attachPath"];
	var img = "<img src='"+basepath+"servlet/attachment.do?oper=handleImg&filename="+imageObj.attachPath+"' border='0'/>";
	$("#picholder").attr("innerHTML",img);
};

/**
 * ȡ���ϴ�
 */
function cancelUI() {
	swfu.stopUpload();
	if (confirm('�Ƿ�ȷ��ȡ����')) {		
		$.unblockUI();
		swfu.cancelUpload();
		return true;
	} else {
		swfu.startUpload(fileId);
		return false;
	}
}
</script>
<input type="hidden" name="attachments" id="attachments" value="${attachmentId}"/>
<div style="float:left;">
	<span id="spanButtonPlaceHolder" title="�ϴ��ĵ�">&nbsp;</span>
</div>
<div class="img-priview" id="picholder">ͼƬԤ��</div>
