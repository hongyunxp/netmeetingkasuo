Ext.onReady(function() {
	var menutoolbar = new Ext.Toolbar( {
		border : false
	});
	menutoolbar.render('detailBar');
	menutoolbar.add( {
		text : '刷新',
		iconCls : 'netmeeting_refresh',
		listeners : {
			'click' : function() {
				parent.toPage("../manage/meeting?oper=meetingDetail&usercode="
						+ userUid + "&meetingid=" + meetingId);
			}
		}
	});
	if (hostUid == userUid) {
		if (meetingState == '0') {
			menutoolbar.add( {
				text : '开启会议',
				iconCls : 'netmeeting_refresh',
				listeners : {
					'click' : function() {
						startMeeting(meetingId);
					}
				}
			});
		} else if (meetingState == '1') {
			menutoolbar.add( {
				text : '结束会议',
				iconCls : 'meeting_exit',
				listeners : {
					'click' : function() {
						stopMeeting(meetingId);
					}
				}
			});
		}
	}
	if (hostUid != userUid && meetingState == '1') {
		menutoolbar.add( {
			text : '加入会议',
			iconCls : 'netmeeting_refresh',
			listeners : {
				'click' : function() {
					$.post("../meeting?oper=attend", {
						usercode : userUid,
						password : password,
						meetingId : meetingId
					}, function(data) {
						var json = JSON.parse(data);
						var ret = json["ret"];
						var text = json["text"];
						if (ret == '1') {
							Dialog.alert(text);
							return;
						} else {
							window.open("../meeting/index.jsp?time="
									+ getTime());
						}
					});
				}
			}
		});
	}
	menutoolbar.add( {
		text : '返回',
		iconCls : 'netmeeting_refresh',
		listeners : {
			'click' : function() {
				parent.toPage("../manage/meeting?oper=meetingList");
			}
		}
	});
	menutoolbar.doLayout();
});

/**
 * 开启会议
 * 
 * @param meetingid
 * @return
 */
function startMeeting(meetingid) {
	window.open("../meeting?oper=start&meetingid=" + meetingid + "&time="
			+ getTime());
	parent.toPage("../manage/meeting?oper=meetingDetail&usercode=" + userUid
			+ "&meetingid=" + meetingId);
}

/**
 * 结束会议
 * 
 * @param meetingid
 * @return
 */
function stopMeeting(meetingid) {
	Dialog.alert("结束会议！");
}

// //////////////////////////////////////用户列表////////////////////////////////////////////

/**
 * 点击标题栏切换
 * 
 * @return
 */
function userlistChange() {
	if ($("#userlistPlus").is(":hidden")) {
		userlistMinus();
	} else {
		userlistPlus();
	}
}

/**
 * 
 * @return
 */
function userlistMinus() {
	$("#userlistPlus").show();
	$("#userlistMinus").hide();
	$("#userlistBody").hide();
	$("#userlistBody").html("");
}

/**
 * 
 * @return
 */
function userlistPlus() {
	$("#userlistPlus").hide();
	$("#userlistMinus").show();
	$("#userlistBody").show();

	var url = "../monitor?oper=userlistMonitor&time=" + new Date().getTime();
	$.post(url, {
		meetingId : meetingId
	}, function(data) {
		var json = JSON.parse(data);
		var html = new stringBuilder();
		for ( var i = 0; i < json.length; i++) {
			var username = json[i]["username"];
			var usercode = json[i]["usercode"];
			var umrole = json[i]["umrole"];
			var umstate = json[i]["umstate"];
			var umenter = json[i]["umenter"];

			var seq = i + 1;
			if (i % 2 == 0) {
				html.append('<tr>');
			} else {
				html.append('<tr style="background:#efffff;">');
			}
			html.append('<td>' + seq + ".&nbsp;" + username + '</td>');
			if (umrole == 0) {
				html.append('<td>主持人</td>');
			} else {
				html.append('<td>参会者</td>');
			}
			if (umstate == 0) {
				html.append('<td>未在会中</td>');
			} else if (umstate == 1) {
				html.append('<td>在会中</td>');
			} else if (umstate == 2) {
				html.append('<td>已退出</td>');
			} else if (umstate == 3) {
				html.append('<td>已刷新</td>');
			}
			html.append('<td>' + umenter + '</td>');
			html.append('</tr>');
		}
		$("#userlistBody").html(html.toString());
	});

}

// //////////////////////////////////////聊天////////////////////////////////////////////

/**
 * 点击标题栏切换
 * 
 * @return
 */
function chatChange() {
	if ($("#chatPlus").is(":hidden")) {
		chatMinus();
	} else {
		chatPlus();
	}
}

/**
 * 
 * @return
 */
function chatMinus() {
	$("#chatPlus").show();
	$("#chatMinus").hide();
	$("#chatBody").hide();
	$("#chatBody").html("");
}

/**
 * 
 * @return
 */
function chatPlus() {
	$("#chatPlus").hide();
	$("#chatMinus").show();
	$("#chatBody").show();

	var url = "../monitor?oper=chatMonitor&time=" + new Date().getTime();
	$.post(url, {
		meetingId : meetingId
	}, function(data) {
		var json = JSON.parse(data);
		var html = new stringBuilder();
		for ( var i = 0; i < json.length; i++) {
			var username = json[i]["username"];
			var chatpath = json[i]["chatpath"];

			var seq = i + 1;
			var viewChatLink = "<a href='#' onclick='chatView(\"" + chatpath
					+ "\")'>查看</a>";
			if (i % 2 == 0) {
				html.append('<tr>');
			} else {
				html.append('<tr style="background:#efffff;">');
			}
			html.append('<td>' + seq + ".&nbsp;" + username + '</td>');
			html.append('<td colspan="3">' + viewChatLink + '</td>');
			html.append('</tr>');
		}
		$("#chatBody").html(html.toString());
	});

}

/**
 * 查看文档共享信息
 * 
 * @param path
 * @return
 */
function chatView(chatpath) {
	var chatDlg = new Dialog();
	chatDlg.Width = 550;
	chatDlg.Height = 250;
	chatDlg.Title = "文档共享信息";
	chatDlg.URL = "../manage/meeting_detail_chat.jsp?chatpath=" + chatpath;
	chatDlg.show();
}

// //////////////////////////////////////文档共享////////////////////////////////////////////

/**
 * 点击标题栏切换
 * 
 * @return
 */
function docChange() {
	if ($("#docPlus").is(":hidden")) {
		docMinus();
	} else {
		docPlus();
	}
}

/**
 * 
 * @return
 */
function docMinus() {
	$("#docPlus").show();
	$("#docMinus").hide();
	$("#docBody").hide();
	$("#docBody").html("");
}

/**
 * 
 * @return
 */
function docPlus() {
	$("#docPlus").hide();
	$("#docMinus").show();
	$("#docBody").show();

	var url = "../monitor?oper=docMonitor&time=" + new Date().getTime();
	$.post(url, {
		meetingId : meetingId
	}, function(data) {
		var json = JSON.parse(data);
		var html = new stringBuilder();
		for ( var i = 0; i < json.length; i++) {
			var filename = json[i]["filename"];
			var filepage = json[i]["filepage"];
			var fileid = json[i]["fileid"];

			var seq = i + 1;
			var viewChatLink = "<a href='#' onclick='docView(\"" + fileid
					+ "\")'>查看</a>";
			if (i % 2 == 0) {
				html.append('<tr>');
			} else {
				html.append('<tr style="background:#efffff;">');
			}
			html.append('<td>' + seq + ".&nbsp;" + filename + '</td>');
			html.append('<td>' + filepage + '页</td>');
			html.append('<td colspan="2">' + viewChatLink + '</td>');
			html.append('</tr>');
		}
		$("#docBody").html(html.toString());
	});

}

/**
 * 查看文档共享信息
 * 
 * @param path
 * @return
 */
function docView(fileid) {
	var docDlg = new Dialog();
	docDlg.Width = 900;
	docDlg.Height = 600;
	docDlg.Title = "文档共享信息";
	docDlg.URL = "../manage/document?oper=docPreview&fileid=" + fileid;
	docDlg.show();
}

// //////////////////////////////////////视频播放////////////////////////////////////////////

/**
 * 点击标题栏切换
 * 
 * @return
 */
function videoPlayChange() {
	if ($("#videoPlayPlus").is(":hidden")) {
		videoPlayMinus();
	} else {
		videoPlayPlus();
	}
}

/**
 * 
 * @return
 */
function videoPlayMinus() {
	$("#videoPlayPlus").show();
	$("#videoPlayMinus").hide();
	$("#videoPlayBody").hide();
	$("#videoPlayBody").html("");
}

/**
 * 
 * @return
 */
function videoPlayPlus() {
	$("#videoPlayPlus").hide();
	$("#videoPlayMinus").show();
	$("#videoPlayBody").show();

	var url = "../monitor?oper=videoPlayMonitor&time=" + new Date().getTime();
	$.post(url, {
		meetingId : meetingId
	}, function(data) {
		var json = JSON.parse(data);
		var html = new stringBuilder();
		for ( var i = 0; i < json.length; i++) {
			var videoname = json[i]["videoname"];
			var videosize = json[i]["videosize"];
			var videoid = json[i]["videoid"];

			var seq = i + 1;
			var viewVideoLink = "<a href='#' onclick='videoView(\"" + videoid
					+ "\")'>查看</a>";
			if (i % 2 == 0) {
				html.append('<tr>');
			} else {
				html.append('<tr style="background:#efffff;">');
			}
			html.append('<td>' + seq + ".&nbsp;" + videoname + '</td>');
			html.append('<td>' + videosize + '</td>');
			html.append('<td colspan="2">' + viewVideoLink + '</td>');
			html.append('</tr>');
		}
		$("#videoPlayBody").html(html.toString());
	});

}

/**
 * 查看文档共享信息
 * 
 * @param path
 * @return
 */
function videoView(videoid) {
	var videoDiag = new Dialog();
	videoDiag.Width = 950;
	videoDiag.Height = 600;
	videoDiag.Title = "预览音视频";
	videoDiag.URL = "../manage/videoplay_preview.jsp?videoid=" + videoid;
	videoDiag.show();
}
