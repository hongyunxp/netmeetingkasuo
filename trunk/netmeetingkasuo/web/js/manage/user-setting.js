$(function() {
	
	//初始化上传头像
	swfuploadInit();

	//初始化会议配置
	$.post("../manage/user?oper=userSettings", {
		usercode : usercode
	}, function(data) {
		var json = JSON.parse(data);
		var handup = json[allowhandup];
		var dscontrol = json[allowdesktopcontrol];
		var wboard = json[allowwhiteboard];
		if (handup == '1') {
			$("#"+allowhandup).attr("checked",true);
		} else {
			$("#"+allowhandup).attr("checked","");
		}
		if (dscontrol == '1') {
			$("#"+allowdesktopcontrol).attr("checked",true);
		} else {
			$("#"+allowdesktopcontrol).attr("checked","");
		}
		if (wboard == '1') {
			$("#"+allowwhiteboard).attr("checked",true);
		} else {
			$("#"+allowwhiteboard).attr("checked","");
		}
	});

	//提交修改用户信息
	$('#user_frm').ajaxForm(function(data) {
		var json = JSON.parse(data);
		var ret = json["ret"];
		var text = json["text"];
		if (ret == '1') {
			Dialog.alert(text, function() {
				parent.toPage("user_setting.jsp?usercode=" + usercode);
			});
		} else {
			Dialog.alert(text);
		}
	});
	
	//提交修改会议配置
	$('#setting_frm').ajaxForm(function(data) {
		var json = JSON.parse(data);
		var ret = json["ret"];
		var text = json["text"];
		if (ret == '1') {
			Dialog.alert(text, function() {
				parent.toPage("user_setting.jsp?usercode=" + usercode);
			});
		} else {
			Dialog.alert(text);
		}
	});
});

// *********************************头像上传***********************************
var swfu;
var fileId = "";
var imageObj = null;
function swfuploadInit() {
	var settings = {
		flash_url : "../js/upload/swfupload.swf",
		// Relative to the SWF file
		upload_url : "../manage/user?oper=uploadPhoto",
		post_params : {},
		file_size_limit : "100MB",
		file_types : "*.jpg;*.png;*.gif",
		file_types_description : "图片",
		file_upload_limit : 200,
		file_queue_limit : 0,
		debug : false,
		button_image_url : "../images/netmeeting/upload.png",
		button_placeholder_id : "spanButtonPlaceHolder",
		button_width : "50",
		button_height : "22",
		button_cursor : SWFUpload.CURSOR.HAND,
		file_queued_handler : fileQueued,
		file_queue_error_handler : fileQueueError,
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
 * 上传成功事件
 * 
 * @param file
 * @param server_data
 * @return
 */
var uploadSuccessEventHandler = function(file, server_data) {
	var json = JSON.parse(server_data);
	var imgPath = json["imgPath"];
	var url = "../manage/user?oper=imgPreview&imgPath=" + imgPath + "&time"
			+ new Date().getTime();
	$("#userpic").attr("src", url);
	$("#picpath").val(imgPath);
};

/**
 * 点击标题栏切换
 * 
 * @return
 */
function userChange() {
	if ($("#userMinus").is(":hidden")) {
		userExpand();
	} else {
		userCollpase();
	}
}

/**
 * 
 * @return
 */
function userCollpase() {
	$("#userPlus").show();
	$("#userMinus").hide();
	$("#userBody").hide();
}

/**
 * 
 * @return
 */
function userExpand() {
	$("#userPlus").hide();
	$("#userMinus").show();
	$("#userBody").show();

	$.post("../manage/user?oper=userGet", {
		usercode : usercode
	}, function(data) {
		var json = JSON.parse(data);
		var usercode = json["usercode"];
		var password = json["password"];
		var username = json["username"];
		var userrole = json["userrole"];
		var useremail = json["useremail"];
		var userpic = json["userpic"];
		$("#usercode").val(usercode);
		$("#password").val(password);
		$("#verifypwd").val(password);
		$("#username").val(username);
		$("#email").val(useremail);
		var url = "../manage/user?oper=imgPreview&imgPath=" + userpic + "&time"
				+ new Date().getTime();
		$("#userpic").attr("src", url);
		$("#picpath").val(userpic);
		$("#userrole").val(userrole);
	});
}