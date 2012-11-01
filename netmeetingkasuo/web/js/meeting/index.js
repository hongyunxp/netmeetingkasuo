// ---------------------------全局操作函数----------------------------------------
var msghint = '<img border="0" src="../images/netmeeting/info.png" align="absmiddle"/>&nbsp;系统提示';

var userlistArray = [];

/**
 * 锁定的标签ID
 */
var tabLockId = "";

/**
 * 提示消息定义
 */
Ext.extmsg = function() {
	var msgCt;
	function createBox(t, s) {
		return [
				'<div class="msg">',
				'<div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>',
				'<div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc"><h3>',
				t,
				'</h3>',
				s,
				'</div></div></div>',
				'<div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>',
				'</div>' ].join('');
	}
	return {
		msg : function(title, format) {
			if (!msgCt) {
				msgCt = Ext.DomHelper.insertFirst(document.body, {
					id : 'msg-div'
				}, true);
			}
			msgCt.alignTo(document, 't-t');
			var s = String.format.apply(String, Array.prototype.slice.call(
					arguments, 1));
			var m = Ext.DomHelper.append(msgCt, {
				html : createBox(title, s)
			}, true);
			m.slideIn('t').pause(2).ghost("t", {
				remove : true
			});
		},

		init : function() {
			var lb = Ext.get('lib-bar');
			if (lb) {
				lb.show();
			}
		}
	};
}();

/**
 * 右下角弹出提示框
 * 
 * @param msg
 * @return
 */
function showMsg(msgs) {
	new Ext.ux.ToastWindow( {
		title : msghint,
		html : msgs
	}).show(document);
}


/**
 * 判断是否为IE浏览器
 * 
 * @return
 */
function isIE(){
	var ua = navigator.userAgent.toLowerCase();
	if(ua.match(/msie ([\d.]+)/)){
		return true;
	}else{
		return false;
	}
}

/**
 * 获取随机颜色
 */
function randomColor(){
    var str = '0123456789abcdef';
    var color = '#';
    for(i = 0; i < 6; i++){
    	color = color + str.charAt(Math.random() * 16);
    }
    return color;
}

/**
 * 更正PNG格式的图片
 * 
 * @return
 */
function correctPNG() {
	for ( var i = 0; i < document.images.length; i++) {
		var img = document.images[i]
		var imgName = img.src.toUpperCase()
		if (imgName.substring(imgName.length - 3, imgName.length) == "PNG") {
			var imgID = (img.id) ? "id='" + img.id + "' " : ""
			var imgClass = (img.className) ? "class='" + img.className + "' "
					: ""
			var imgTitle = (img.title) ? "title='" + img.title + "' "
					: "title='" + img.alt + "' "
			var imgStyle = "display:inline-block;" + img.style.cssText
			if (img.align == "left")
				imgStyle = "float:left;" + imgStyle
			if (img.align == "right")
				imgStyle = "float:right;" + imgStyle
			if (img.parentElement.href)
				imgStyle = "cursor:hand;" + imgStyle
			var strNewHTML = "<span "
					+ imgID
					+ imgClass
					+ imgTitle
					+ " style=\""
					+ "width:"
					+ img.width
					+ "px; height:"
					+ img.height
					+ "px;"
					+ imgStyle
					+ ";"
					+ "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader"
					+ "(src=\'" + img.src
					+ "\', sizingMethod='scale');\"></span>"
			img.outerHTML = strNewHTML
			i = i - 1
		}
	}
}

var ExtBtnSpacer2 = {
		xtype: 'tbspacer', 
		width: 2
	};

var ExtBtnSpacer5 = {
		xtype: 'tbspacer', 
		width: 5
	};

var ExtBtnSpacer10 = {
		xtype: 'tbspacer', 
		width: 10
	};

/**
 * 生成min和max之间的随机数
 * 
 * @param min
 * @param max
 * @return
 */
function gen_random(min, max){   
	return Math.floor(Math.random() * (max- min) + min);   
} 


// ---------------------------用户列表操作函数----------------------------------------
var isitemclicked = false;
var itemclickArr = new Array();
var selectedUser = "";
var sessionIdArr = new Array();

var USERCOLOR="";
/**
 * 回调-初始化用户列表
 * 
 * @return
 */
function initUserListCallback(jsonArr) {
	// alert(jsonArr);
	userlistArray = [];
	var json = JSON.parse(jsonArr);
	var html = new stringBuilder();
	for ( var i = 0; i < json.length; i++) {
		var sessionid = json[i]["sessionid"];
		var usercode = json[i]["usercode"];
		var username = json[i]["username"];
		var color = json[i]["color"];
		if (userSid == sessionid) {
			USERCOLOR = color;
		}
		
		userlistArray.push( {
			"username" : json[i]["username"],
			"sessionid" : json[i]["sessionid"]
		});
		
		html.append("<table id='userList_").append(sessionid).append("' ");
		html.append("' width='100%' border='0' cellspacing='0'");
		html.append(" style='border-bottom:1px solid #a3bad9;' ");
		html.append("onmouseover=userlistMouseover(this,'" + sessionid + "') ");
		html.append("onmouseout=userlistMouseout(this,'" + sessionid + "') ");
		html.append("onclick=userlistClick(this,'" + sessionid + "') >");
		html.append("<tr style='cursor:hand;'>");
		html.append("<td align='left' width='40%' height='25px'>");
		if (hostSid == sessionid) {
			html.append("<span style='font-size:12px;font-weight:bold;margin-left:5px;'>");
			html
					.append("<img src='../images/netmeeting/netmeeting_admin.png' border='0' style='vertical-align:middle;'/> &nbsp;");
		} else {
			html.append("<span style='font-size:12px;margin-left:5px;'>");
			html
					.append("<img src='../images/netmeeting/netmeeting_user.png' border='0' style='vertical-align:middle;'/> &nbsp;");
		}
		html.append(username).append("</span></td>");
		html.append("<td width='50%'>");
		html.append("<span style='width:18px;' id='handup_").append(sessionid)
				.append("'></span>");
		html.append("<span style='width:18px;' id='audio_").append(sessionid)
				.append("'></span>");
		html.append("<span style='width:18px;' id='video_").append(sessionid)
				.append("'></span>");
		html.append("</td>");
		html.append("<td width='10%' valign='center' align='right'>");
		html.append("<span style='height:5px;width:8px;border:0;margin-right:10px;background:"+color+"'></span>");
		html.append("</td></tr></table>");

		sessionIdArr.push(sessionid);
	}
	document.getElementById("UserListDiv").innerHTML = html.toString();

	// 初始化用户列表菜单
	if (userSid == hostSid) {
		initHostUserListMenu();
	} else {
		initAttendUserListMenu();
	}
}

/**
 * 用户鼠标移入
 */
function userlistMouseover(elid, sessionid) {
	elid.style.background = '#D6E3F2';
}

/**
 * 用户鼠标移出
 */
function userlistMouseout(elid, sessionid) {
	if (!isitemclicked || itemclickArr[0].elid != elid) {
		elid.style.background = '#ffffff';
	}
}

/**
 * 用户鼠标点击
 */
function userlistClick(elid, sessionid) {
	// isitemclicked = true;
	// if (itemclickArr.length > 0) {
	// var popitem = itemclickArr.pop();
	// popitem.elid.style.background = '#ffffff';
	// if (popitem.elid != elid) {
	// itemclickArr.push( {
	// 'elid' : elid,
	// 'sessionid' : sessionid
	// });
	// elid.style.background = '#D6E3F2';
	// selectedUser = sessionid;
	// } else {
	// isitemclicked = false;
	// selectedUser = "";
	// }
	// } else {
	// itemclickArr.push( {
	// 'elid' : elid,
	// 'sessionid' : sessionid
	// });
	// elid.style.background = '#D6E3F2';
	// selectedUser = sessionid;
	// }
}

/**
 * 添加与会者菜单
 * 
 * @return
 */
function initAttendUserListMenu(){
	var userListMenuObj = document.createElement("div");
	userListMenuObj.className = "contextMenu";
	userListMenuObj.id = "userListMenu";
	var menuList = new stringBuilder();
	menuList.append('<ul style="font-size:12px;">');
	menuList.append('<li id="item_1">远程协助</li>');
	menuList.append('</ul>');
	userListMenuObj.innerHTML = menuList.toString();
	document.body.appendChild(userListMenuObj);

	for ( var i = 0; i < sessionIdArr.length; i++) {
		var sessionid = sessionIdArr[i];
		$("#userList_" + sessionid).contextMenu('userListMenu', {
			bindings : {
				'item_1' : function(t) {
					var viewerId = t.id.split("_")[1];
					if(userSid == viewerId){
						Dialog.alert("禁止此操作，您不能邀请自己远程协助！");
					} else {
						screenControlConfrim(userSid,viewerId);
					}
				}
			}
		});
	}
}

/**
 * 添加主持人菜单
 * 
 * @return
 */
function initHostUserListMenu() {
	var userListMenuObj = document.createElement("div");
	userListMenuObj.className = "contextMenu";
	userListMenuObj.id = "userListMenu";
	var menuList = new stringBuilder();
	menuList.append('<ul style="font-size:12px;">');
	menuList.append('<li id="item_1">踢出会议</li>');
	menuList.append('<li id="item_2">远程协助</li>');
	menuList.append('</ul>');
	userListMenuObj.innerHTML = menuList.toString();
	document.body.appendChild(userListMenuObj);

	for ( var i = 0; i < sessionIdArr.length; i++) {
		var sessionid = sessionIdArr[i];
		$("#userList_" + sessionid).contextMenu('userListMenu', {
			bindings : {
				'item_1' : function(t) {
					alert(t.id + '\nAction was Open');
				},
				'item_2' : function(t) {
					var viewerId = t.id.split("_")[1];
					if(userSid == viewerId){
						Dialog.alert("禁止此操作，您不能邀请自己远程协助！");
					} else {
						screenControlConfrim(userSid,viewerId);
					}
				}
			}
		});
	}
}

/**
 * 服务端回调禁用举手功能
 * 
 * @return
 */
function disableHandupCallback(flag){
	if(flag == "true"){
		showMsg("管理员允许你举手");
		Ext.getCmp('meetinghandsupId').enable();
	}else{
		showMsg("管理员禁止你举手");
		Ext.getCmp('meetinghandsupId').disable();
	}
}

// ---------------------------聊天操作函数----------------------------------------
var chatToolbar = null;
var chatDataArray = [ [ '1', '所有人' ] ];
var chatComboDataSource = null;
var chatSelectCombo = null;

var chatToolbar2 = null;
var chatDataArray2 = [ [ '1', '所有人' ] ];
var chatComboDataSource2 = null;
var chatSelectCombo2 = null;

var inputHintMsg = "输入文字，按[回车]或者[发送]按钮发送信息，按住[Shift+回车]换行";
var chatWin = null;
var isChatWinShow = false;

/**
 * 初始化聊天显示面板信息
 * 
 * @return
 */
function initChatShow() {
	var html = '<span style="color:gray;">温馨提示：请在文本框内输入聊天信息~</span>';
	$("#ChatNorthDiv").attr("innerHTML", html);
}

/**
 * 初始化聊天菜单
 */
function initChatMenu() {
	chatToolbar = new Ext.Toolbar( {
		buttonAlign : 'left'
	});
	chatToolbar.render('ChatCenterDiv');
	
	// 下拉框
	chatComboDataSource = new Ext.data.Store( {
		proxy : new Ext.data.MemoryProxy(chatDataArray), // 数据源
		reader : new Ext.data.ArrayReader( {}, [ // 如何解析
				{
					name : 'id'
				}, {
					name : 'name'
				} ])
	});
	chatComboDataSource.load();
	
	chatSelectCombo = new Ext.form.ComboBox( {
		id : 'userSelectList',
		store : chatComboDataSource,
		editable : false,
		valueField : 'id',
		displayField : 'name',
		typeAhead : true,
		mode : 'local',
		selectOnFocus : true,
		triggerAction : 'all',
		width : 110
	});
	chatSelectCombo.setValue('1');
	
	chatToolbar.add( {
		iconCls : 'chat_smile',
		tooltip : '笑脸',
		listeners : {
			'click' : function() {
				chatSmile();
			}
		}
	});
	chatToolbar.add( {
		iconCls : 'chat_remove',
		tooltip : '清除聊天信息',
		listeners : {
			'click' : function() {
				chatRemove();
			}
		}
	});
	if (hostSid == userSid) {
		chatToolbar.add( {
			iconCls : 'chat_removeAll',
			tooltip : '清除所有聊天信息',
			listeners : {
				'click' : function() {
					chatRemoveAll();
				}
			}
		});
	}
	chatToolbar.add( {
		iconCls : 'chat_save',
		tooltip : '保存聊天信息',
		listeners : {
			'click' : function() {
				chatSave();
			}
		}
	});
	chatToolbar.addField(chatSelectCombo);
	chatToolbar.doLayout();
}

/**
 * 初始化聊天输入信息
 * 
 * @return
 */
function initChatInput() {
	var inputHtml = new stringBuilder();
	inputHtml.append('<table width="100%" border="0">');
	inputHtml.append('<tr>');
	inputHtml.append('<td width="100%">');
	inputHtml
			.append('<textarea id="chatInput" onkeyup="chatKeyup(event);" class="inputArea"></textarea>');
	inputHtml.append('</td>');
	inputHtml.append('<td width="48px" valign="bottom">');
	inputHtml
			.append('<img src="../images/elearning/training_sendmsg.gif" style="cursor:hand;border:0;" onClick="chatSend()">');
	inputHtml.append('</td>');
	inputHtml.append('</tr>');
	inputHtml.append('</table>');
	$("#ChatSouthDiv").attr("innerHTML", inputHtml.toString())
}

/**
 * 服务器回调更新用户下拉列表信息
 * 
 * @param jsonArr
 * @return
 */
function chatInitSelectCallback(jsonArr) {
	chatDataArray = [ [ '1', '所有人' ] ];
	chatDataArray2 = [ [ '1', '所有人' ] ];
	var json = JSON.parse(jsonArr);
	var html = new stringBuilder();
	for ( var i = 0; i < json.length; i++) {
		var sessionid = json[i]["sessionid"];
		var usercode = json[i]["usercode"];
		var username = json[i]["username"];
		chatDataArray.push( [ sessionid, username ]);
		chatDataArray2.push( [ sessionid, username ]);
	}
	chatComboDataSource.loadData(chatDataArray);
}

/**
 * 初始化聊天信息
 * 
 * @param msg
 * @return
 */
function chatInitMsgCallback(msg) {
	if (msg != '' && msg.length > 0) {
		var chatShow = document.getElementById("ChatNorthDiv");
		chatShow.innerHTML = msg;
		chatShow.scrollTop = 1000000;
	}
}

/**
 * 聊天时，键盘弹起事件
 * 
 * @param event
 * @return
 */
function chatKeyup(event) {
	var event = window.event || e;
	var ie = navigator.appName == "Microsoft Internet Explorer" ? true : false;
	if (ie) {
		if ((!event.shiftKey) && (event.keyCode == 13)) {
			chatSend();
		}
	} else {
		 if (isKeyTrigger(e, 13, true)) {
			 chatSend();
		 }
	}
}

/**
 * 弹出笑脸
 * 
 * @return
 */
function chatSmile() {
	var html = new stringBuilder();
	html.append('<table border="0" width="100px">');
	html.append('<tr>');
	html
			.append('<td height="16px"><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':)' + '\')"><img border="0" src="../images/smile/smile.gif" alt="微笑 :)"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':D' + '\')"><img border="0" src="../images/smile/open-mouthedSmile.gif" alt="大笑 :D"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ';)' + '\')"><img border="0" src="../images/smile/winkingSmile.gif" alt="眨眼 ;)"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':-O' + '\')"><img border="0" src="../images/smile/surprisedSmile.gif" alt="惊讶 :-O"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':P' + '\')"><img border="0" src="../images/smile/smileWithTongueOut.gif" alt="吐舌笑脸 :P"/></a></td>');
	html.append('</tr>');
	html.append('<tr>');
	html
			.append('<td height="16px"><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':@' + '\')"><img border="0" src="../images/smile/angrySmile.gif" alt="生气 :@"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':S' + '\')"><img border="0" src="../images/smile/confusedSmile.gif" alt="困惑 :S"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':$' + '\')"><img border="0" src="../images/smile/embarrassed-.gif" alt="尴尬 :$"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':(' + '\')"><img border="0" src="../images/smile/sad.gif" alt="悲哀 :("/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':&' + '\')"><img border="0" src="../images/smile/crying.gif" alt="哭泣的脸 :&"/></a></td>');
	html.append('</tr>');
	html.append('<tr>');
	html
			.append('<td height="16px"><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':|' + '\')"><img border="0" src="../images/smile/Disappointed.gif" alt="失望 :|"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':-#' + '\')"><img border="0" src="../images/smile/donotTell.gif" alt="保持秘密 :-#"/></a></td>');
	html.append('<td>&nbsp;</td>');
	html.append('<td>&nbsp;</td>');
	html.append('<td>&nbsp;</td>');
	html.append('</tr>');
	html.append('</table>');

	if(isChatWinShow){
		var x = chatWin.getPosition(true)[0]+10;
		var y = chatWin.getPosition(true)[1]+305;
		$("#laughfaces").attr("innerHTML", html.toString());
		$("#laughfaces").css("left",x);
		$("#laughfaces").css("top",y);
		if ($("#laughfaces").is(":hidden")) {
			$("#laughfaces").show();
		} else {
			$("#laughfaces").hide();
		}
	}else{
		$("#laughfaces").attr("innerHTML", html.toString());
		$("#laughfaces").css("left",3);
		$("#laughfaces").css("top",document.body.clientHeight-75);
		if ($("#laughfaces").is(":hidden")) {
			$("#laughfaces").show();
		} else {
			$("#laughfaces").hide();
		}
	}
}

/**
 * 处理选中笑脸事件
 * 
 * @return
 */
function handleSmiles(smilevalue) {
	if(isChatWinShow){
		var txtareaId = document.getElementById("chatInput2");
		txtareaId.value = txtareaId.value  + " " + smilevalue + " ";
		txtareaId.focus();
		$("#laughfaces").hide();
	} else {
		var txtareaId = document.getElementById("chatInput");
		txtareaId.value = txtareaId.value  + " " + smilevalue + " ";
		txtareaId.focus();
		$("#laughfaces").hide();
	}
}

/**
 * 删除聊天信息
 * 
 * @return
 */
function chatRemove() {
	Dialog.confirm("清除聊天信息？", function() {
		ChatService.remove();
	});
}

/**
 * 删除所有的聊天信息
 * 
 * @return
 */
function chatRemoveAll() {
	Dialog.confirm("清除所有人的聊天信息", function() {
		ChatService.removeAll();
	});
}

/**
 * 服务器回调=清除聊天信息
 */
function chatRemoveCallback() {
	if(isChatWinShow){
		var chatShow = document.getElementById("ChatNorthDiv2");
		chatShow.innerHTML = "";
	} else {
		var chatShow = document.getElementById("ChatNorthDiv");
		chatShow.innerHTML = "";
	}
}

/**
 * 保存聊天信息
 * 
 * @return
 */
function chatSave() {
	dwr.engine.setAsync(false);
	ChatService.save({
		callback : function(data) {
			dwr.engine.openInDownload(data);
			dwr.engine.setAsync(true); 
		},
		async : false
	});
}

/**
 * 发送聊天信息
 * 
 * @return
 */
function chatSend() {
	if(isChatWinShow){
		var txtareaId = document.getElementById("chatInput2");
		var color = $("#chatInput2").css("color");
		if (color != 'gray') {
			var receiver = chatSelectCombo2.getValue();
			var msg = dwr.util.getValue('chatInput2').trim(); // 获得消息内容
			if (msg == "" || msg.length < 1) {
				showMsg("不能输入空消息！");
			} else {
				ChatService.send(receiver, msg, function() {
					$("#chatInput2").val("");
				});
			}
		}
	} else {
		var txtareaId = document.getElementById("chatInput");
		var color = $("#chatInput").css("color");
		if (color != 'gray') {
			var receiver = chatSelectCombo.getValue();
			var msg = dwr.util.getValue('chatInput').trim(); // 获得消息内容
			if (msg == "" || msg.length < 1) {
				showMsg("不能输入空消息！");
			} else {
				ChatService.send(receiver, msg, function() {
					$("#chatInput").val("");
				});
			}
		}
	}
}

/**
 * 更新聊天消息
 * 
 * @param msg
 * @return
 */
function chatUpdateMsgCallback(receiverId, senderName, receiverName, msg, time) {
	var msgHtml = new stringBuilder();
	msgHtml
			.append("<table class='chat_table' cellspacing='0' cellpadding='0'><tr class='chat_tr'><td>");
	msgHtml.append("<b>");
	msgHtml.append(senderName);
	msgHtml.append("</b> -> ");
	if (receiverId == "1") {
		msgHtml.append("<b>大家</b>说:");
	} else {
		msgHtml.append("<b>");
		msgHtml.append(receiverName);
		msgHtml.append("</b>说:");
	}
	msgHtml.append("</td>");
	msgHtml.append("<td align='right' class='chat_td_1'>");
	msgHtml.append(time);
	msgHtml.append("</td></tr>");
	msgHtml
			.append("<tr><td colspan='2' class='chat_td_2'>&nbsp;&nbsp;&nbsp;&nbsp;");
	msgHtml.append(msg);
	msgHtml.append("</td></tr></table>");

	if (isChatWinShow) {
		var chatShow2 = document.getElementById("ChatNorthDiv2");
		var oriHtml2 = chatShow2.innerHTML;
		chatShow2.innerHTML = oriHtml2 + msgHtml.toString();
		chatShow2.scrollTop = 1000000;
	} else {
		var chatShow = document.getElementById("ChatNorthDiv");
		var oriHtml = chatShow.innerHTML;
		chatShow.innerHTML = oriHtml + msgHtml.toString();
		chatShow.scrollTop = 1000000;
	}
}

/**
 * 切换聊天面板
 * 
 * @return
 */
function chatPanelSwitch(){
	var html = new stringBuilder();
	html.append('<div id="chatPanel_Div" style="height:100%;width:100%">');
	html.append('<div id="ChatDiv2">');
	html.append('<div id="ChatNorthDiv2" style="height:250px;"></div>');
	html.append('<div id="ChatCenterDiv2" style="height:24px;"></div>');
	html.append('<div id="ChatSouthDiv2"></div>');
	html.append('</div>');
	html.append('</div>');

	$("#ChatPanelDiv").html(html.toString());
	
	if(chatWin == null){
		chatWin = new Ext.Window({
			title: '<img src="../images/netmeeting/chat_menu.png" border="0" align="absmiddle"/>&nbsp;互动讨论',
			layout : 'fit',
			width : 350,
			height : 365,
			collapsible : true,
			closable : false,
			resizable : false,
			closeAction : 'hide',
			plain : true,
			items : {
				contentEl : 'chatPanel_Div'
			},
			tools : [/*
						 * { id:'maximize', handler: function(event, toolEl,
						 * panel){ chatWin.maximize();
						 * chatWin.getTool('restore').show();
						 * chatWin.getTool('maximize').hide(); var height =
						 * document.body.clientHeight - 115;
						 * $("#ChatNorthDiv2").height(height); } },{
						 * id:'restore', hidden : true, handler: function(event,
						 * toolEl, panel){ chatWin.restore();
						 * chatWin.getTool('maximize').show();
						 * chatWin.getTool('restore').hide();
						 * $("#ChatNorthDiv2").height(250); } },
						 */{
	        	 id:'unpin',
	        	 handler: function(event, toolEl, panel){
					
					if(Ext.getCmp("left_audio_panel").isVisible()){
						Ext.getCmp('leftSouthWrapId').setHeight(455);
					}else{
						Ext.getCmp('leftSouthWrapId').setHeight(250);
					}
					Ext.getCmp('left_south_panel').setVisible(true);
					Ext.getCmp('west_panel').doLayout();
					
					var chatShow = document.getElementById("ChatNorthDiv");
					var chatShow2 = document.getElementById("ChatNorthDiv2");
					chatShow.innerHTML = chatShow2.innerHTML;
					chatShow.scrollTop = 1000000;
					
					isChatWinShow = false;
					
					chatWin.hide();
	 		     }
			}]
		});
		initChatMenu2();
		initChatInput2();
	}
	
	chatWin.show();
	chatWin.doLayout();
	initChatShow2();
	$("#chatInput2").val("");
	isChatWinShow = true;
}

/**
 * 初始化聊天显示面板信息
 * 
 * @return
 */
function initChatShow2() {
	var chatShow = document.getElementById("ChatNorthDiv");
	var chatShow2 = document.getElementById("ChatNorthDiv2");
	chatShow2.innerHTML = chatShow.innerHTML;
	chatShow2.scrollTop = 1000000;
}

/**
 * 初始化聊天菜单
 */
function initChatMenu2() {
	
	chatToolbar2 = new Ext.Toolbar( {
		buttonAlign : 'left'
	});
	chatToolbar2.render('ChatCenterDiv2');
	
	// 下拉框
	chatComboDataSource2 = new Ext.data.Store( {
		proxy : new Ext.data.MemoryProxy(chatDataArray2), // 数据源
		reader : new Ext.data.ArrayReader( {}, [ // 如何解析
				{
					name : 'id'
				}, {
					name : 'name'
				} ])
	});
	chatComboDataSource2.load();
	
	chatSelectCombo2 = new Ext.form.ComboBox( {
		id : 'userSelectList2',
		store : chatComboDataSource2,
		editable : false,
		valueField : 'id',
		displayField : 'name',
		typeAhead : true,
		mode : 'local',
		selectOnFocus : true,
		triggerAction : 'all',
		width : 110
	});
	chatSelectCombo2.setValue('1');
	
	chatToolbar2.add( {
		id : 'smilefaces2',
		iconCls : 'chat_smile',
		tooltip : '笑脸',
		listeners : {
			'click' : function() {
				chatSmile();
			}
		}
	});
	chatToolbar2.add( {
		iconCls : 'chat_remove',
		tooltip : '清除聊天信息',
		listeners : {
			'click' : function() {
				chatRemove();
			}
		}
	});
	if (hostSid == userSid) {
		chatToolbar2.add( {
			iconCls : 'chat_removeAll',
			tooltip : '清除所有聊天信息',
			listeners : {
				'click' : function() {
					chatRemoveAll();
				}
			}
		});
	}
	chatToolbar2.add( {
		iconCls : 'chat_save',
		tooltip : '保存聊天信息',
		listeners : {
			'click' : function() {
				chatSave();
			}
		}
	});
	chatToolbar2.addField(chatSelectCombo2);
	chatToolbar2.doLayout();
}

/**
 * 初始化聊天输入信息
 * 
 * @return
 */
function initChatInput2() {
	var inputHtml = new stringBuilder();
	inputHtml.append('<table width="100%" border="0">');
	inputHtml.append('<tr>');
	inputHtml.append('<td width="100%">');
	inputHtml
			.append('<textarea id="chatInput2" onkeyup="chatKeyup(event);" class="inputArea"></textarea>');
	inputHtml.append('</td>');
	inputHtml.append('<td width="48px" valign="bottom">');
	inputHtml
			.append('<img src="../images/elearning/training_sendmsg.gif" style="cursor:hand;border:0;" onClick="chatSend()">');
	inputHtml.append('</td>');
	inputHtml.append('</tr>');
	inputHtml.append('</table>');
	$("#ChatSouthDiv2").attr("innerHTML", inputHtml.toString());
}


// ---------------------------文档共享操作函数----------------------------------------
var docDiag;

var documentDivPrefix = "documentDiv_";
var documentPrefix = "document_";
var documentMenuPrefix = "documentMenu_";
var documentMenuPagePrefix = "documentMenuPage_";
var docMenuAutoPlayPrefix = "docMenuAutoPlay";
var documentContentPrefix = "documentContent_";
var documentImgPrefix = "documentImg_";

var documentPagePre = "documentPagePre_";
var documentPageNext = "documentPageNext_";

var docUploadDiag;

/**
 * 文档共享白板画图功能
 */
var documentDrawPrefix = "documentDraw_";
var documentDrawImgPrefix = "documentDrawImg_";
var DOC_DRAW_LINE = "docWbLine";
var DOC_DRAW_ARROWLINE = "docWbArrowLine";
var DOC_DRAW_POLYLINE = "docWbPolyLine";
var DOC_DRAW_TEXT = "docWbText";
var DOC_DRAW_RECT = "docWbRect";
var DOC_DRAW_ELLIPSE = "docWbEllipse";
var DOC_DRAW_POINTER = "docWbPoint";
var DOC_DRAW_POINTERIMG = "docWbPointImg";

var DOC_DRAW_TEXT_INPUT = "doc_draw_text_input";

var docWhiteboardMenuMap = {};

var docWbDrawMap = {}; 

var docZoomWidthArray = {};
var docZoomHeightArray = {};
/**
 * 文档上传
 * 
 * @return
 */
function documentUpload() {
	docDiag = new Dialog();
	docDiag.Width = 550;
	docDiag.Height = 210;
	docDiag.Title = "上传文档";
	docDiag.URL = "../meeting/document_upload.jsp";
	docDiag.MessageTitle = "<span style='font-size:13px;'>上传文档</span>";
	docDiag.Message = "<span style='font-size:13px;'>请按照提示，文档上传完会进行转换，请耐心等待！</span>";
	docDiag.show();
}

/**
 * 关闭对话框
 * 
 * @return
 */
function documentDiagClose() {
	docDiag.close();
}

/**
 * 文档初始化
 * 
 * @return
 */
function documentInit(fileid, filename){
	
	var docDivId = documentDivPrefix+fileid;
	var docId = documentPrefix+fileid;
	var docMenuId = documentMenuPrefix+fileid;
	var docContentId = documentContentPrefix+fileid;
	var docHtml = new stringBuilder();
	docHtml.append('<div id="');
	docHtml.append(docMenuId);
	docHtml.append('"></div><div id="');
	docHtml.append(docContentId);
	docHtml.append('" style="overflow:auto;background:#a3bad9;" onmousedown="return false;" onmousemove="return false;" onmouseup="return false;" onscroll="docWhiteBoardOnscroll(\''+fileid+'\')"></div>');
	var divEl = document.createElement("div");
	divEl.id=docDivId;
	divEl.innerHTML=docHtml.toString();
	document.getElementById("DocumentDiv").appendChild(divEl);
	if(userSid == hostSid){
		contentPanel
		.add( {
			id : docId,
			title : filename,
			iconCls : 'meeting-info',
			closable : true,
			contentEl : docDivId
		});
	}else{
		contentPanel
		.add( {
			id : docId,
			title : filename,
			iconCls : 'meeting-info',
			contentEl : docDivId
		});
	}
	contentPanel.setActiveTab(docId);

	documentInitMenu(fileid,filename);
	documentInitContent(fileid,filename);
}

/**
 * 初始化文档菜单栏
 * 
 * @param fileid
 * @param filename
 * @return
 */
function documentInitMenu(fileid,filename){
	var docMenuId = documentMenuPrefix+fileid;
	var docMenuPage = documentMenuPagePrefix+fileid;
	var docMenuAutoPlay = docMenuAutoPlayPrefix+fileid;
	var documentPageNextId = documentPageNext + fileid;
	var documentPagePreId = documentPagePre + fileid;
	// 页数下拉框
	var docDataArray = [ [ '0/0', '全部' ] ];
	var docComboDataSource = new Ext.data.Store( {
		proxy : new Ext.data.MemoryProxy(docDataArray), // 数据源
		reader : new Ext.data.ArrayReader( {}, [ // 如何解析
				{
					name : 'id'
				}, {
					name : 'name'
				} ])
	});
	docComboDataSource.load();
	var docSelectCombo = new Ext.form.ComboBox( {
		id : docMenuPage,
		store : docComboDataSource,
		editable : false,
		valueField : 'id',
		displayField : 'name',
		typeAhead : true,
		mode : 'local',
		selectOnFocus : true,
		triggerAction : 'all',
		width : 50,
		listeners : {
			'select' : function(comboVar, record, index) {
				var value = Ext.getCmp(docMenuPage).getValue(index);
				documentMenuSpecPage(fileid,value);
			}
		}
	});

	// 自动翻页下拉框
	autoPlayStore = new Ext.data.ArrayStore({
        fields: ['id', 'name'],
        data : [
                ['5','5秒'],
                ['10','10秒'],
                ['15','15秒'],
                ['20','20秒'],
                ['25','25秒'],
                ['30','30秒']
        ]
    });
    autoPlayCombo = new Ext.form.ComboBox({
    	id : docMenuAutoPlay,
    	hidden : true,
    	editable : false,
        store: autoPlayStore,
        valueField : 'id',
        displayField:'name',
        typeAhead: true,
        mode: 'local',
        width : 50,
        triggerAction: 'all',
        selectOnFocus:true,
        listeners : {
			'select' : function(comboVar, record, index) {
				var value = Ext.getCmp(docMenuAutoPlay).getValue(index);
				documentAutoPlayTime(fileid,value);
			}
		}
    });
	
	var docToolbar = new Ext.Toolbar( {
		buttonAlign : 'left'
	});
	docToolbar.render(docMenuId);
	
	if(userSid == hostSid){
		docToolbar.add( {
			id : documentPagePreId,
			iconCls : 'doc_pre',
			tooltip : '上一页',
			listeners : {
				'click' : function() {
					documentMenuPrePage(fileid);
				}
			}
		});
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.addField(docSelectCombo);
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add( {
			id : documentPageNextId,
			iconCls : 'doc_next',
			tooltip : '下一页',
			listeners : {
				'click' : function() {
					documentMenuNextPage(fileid);
				}
			}
		});
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.addButton( 
			new Ext.Toolbar.Button( { 
				iconCls : 'doc_autoplay',
				enableToggle : true,
				tooltip:'开始自动翻页', 
				toggleHandler : function(b,state){
					if(state){
						var value = "5";
						Ext.getCmp(docMenuAutoPlay).show();
						Ext.getCmp(docMenuAutoPlay).setValue(value);
						documentMenuAutoplay(fileid,value);
					}else{
						Ext.getCmp(docMenuAutoPlay).hide();
						DocumentService.documentStopAutoPlay(fileid,0);
					}
				}
			}) 
		); 
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.addField(autoPlayCombo);
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add('-');
	}

	docToolbar.add(ExtBtnSpacer2);
	docToolbar.add( {
		iconCls : 'doc_zoomout',
		tooltip : '放大',
		listeners : {
			'click' : function() {
				documentMenuZoomOut(fileid);
			}
		}
	});
	docToolbar.add(ExtBtnSpacer2);
	docToolbar.add( {
		iconCls : 'doc_zoomin',
		tooltip : '缩小',
		listeners : {
			'click' : function() {
				documentMenuZoomIn(fileid);
			}
		}
	});
	docToolbar.add(ExtBtnSpacer2);
	docToolbar.add( {
		iconCls : 'doc_save',
		tooltip : '下载该文档',
		listeners : {
			'click' : function() {
				dwr.engine.setAsync(false);
				DocumentService.documentSave(fileid,{
					callback : function(data) {
						dwr.engine.openInDownload(data);
						dwr.engine.setAsync(true); 
					},
					async : false
				});
			}
		}
	});
	
	
	if(userSid == hostSid){
		var wbPointId = DOC_DRAW_POINTER + fileid;
		var wbLineId = DOC_DRAW_LINE + fileid;
		var wbArrowLineId = DOC_DRAW_ARROWLINE + fileid;
		var wbPolyLineId = DOC_DRAW_POLYLINE + fileid;
		var wbTextId = DOC_DRAW_TEXT + fileid;
		var wbRectId = DOC_DRAW_RECT + fileid;
		var wbEllipseId = DOC_DRAW_ELLIPSE + fileid;
		
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add('-');
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add({
			id : wbLineId,
			iconCls : 'whiteboard_line',
			tooltip : '直线',
			enableToggle : true,
			toggleGroup : fileid,
			toggleHandler : function(b,state){
				docWhiteBoardMenubarHandler(fileid,wbLineId,state);
			}
		});
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add( {
			id : wbArrowLineId,
			iconCls : 'whiteboard_arrow',
			tooltip : '箭头线',
			enableToggle : true,
			toggleGroup : fileid,
			toggleHandler : function(b,state){
				docWhiteBoardMenubarHandler(fileid,wbArrowLineId,state);
			}
		});
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add( {
			id : wbRectId,
			iconCls : 'whiteboard_rect',
			tooltip : '矩形',
			enableToggle : true,
			toggleGroup : fileid,
			toggleHandler : function(b,state){
				docWhiteBoardMenubarHandler(fileid,wbRectId,state);
			}
		});
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add( {
			id : wbEllipseId,
			iconCls : 'whiteboard_ellipse',
			tooltip : '椭圆',
			enableToggle : true,
			toggleGroup : fileid,
			toggleHandler : function(b,state){
				docWhiteBoardMenubarHandler(fileid,wbEllipseId,state);
			}
		});
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add( {
			id : wbPolyLineId,
			iconCls : 'whiteboard_pen',
			tooltip : '钢笔',
			enableToggle : true,
			toggleGroup : fileid,
			toggleHandler : function(b,state){
				docWhiteBoardMenubarHandler(fileid,wbPolyLineId,state);
			}
		});
		docToolbar.add(ExtBtnSpacer2);
		docToolbar.add( {
			id : wbTextId,
			iconCls : 'whiteboard_text',
			tooltip : '文本',
			toggleGroup : fileid,
			enableToggle : true,
			toggleHandler : function(b,state){
				docWhiteBoardMenubarHandler(fileid,wbTextId,state);
			}
		});
		if(userSid == hostSid){
			docToolbar.add('-');
			docToolbar.add(ExtBtnSpacer2);
			docToolbar.add( {
				iconCls : 'whiteboard_undo',
				tooltip : '撤销',
				listeners : {
					'click' : function() {
						whiteBoardMenuUndo(fileid);
					}
				}
			});
			docToolbar.add(ExtBtnSpacer2);
			docToolbar.add( {
				iconCls : 'whiteboard_redo',
				tooltip : '重做',
				listeners : {
					'click' : function() {
						whiteBoardMenuRedo(fileid);
					}
				}
			});
			docToolbar.add(ExtBtnSpacer2);
			docToolbar.add( {
				iconCls : 'whiteboard_trash',
				tooltip : '删除所有',
				listeners : {
					'click' : function() {
						whiteBoardMenuClear(fileid);
					}
				}
			});
		}
	}
	
	docToolbar.doLayout();
}

/**
 * 初始化文档内容
 * 
 * @param fileid
 * @param filename
 * @return
 */
function documentInitContent(fileid,filename){
	var docContentId = documentContentPrefix+fileid;
	var imgHeight = contentPanel.getHeight()-60;
	var imgWidth = contentPanel.getWidth();
	$("#"+docContentId).css("width",contentPanel.getWidth());
	$("#"+docContentId).css("height",imgHeight);
}

/**
 * 播放文档
 * 
 * @return
 */
function documentPlay(fileid, filename, seq) {
	documentInit(fileid, filename);
	DocumentService.documentPlay(fileid, seq, function() {
		if(docDiag){
			documentDiagClose();
		}
		showMsg("【"+filename+"】文档加载成功！");
	});
}

/**
 * 播放文档
 * 
 * @return
 */
function documentPlay2(fileid,seq) {
	DocumentService.documentPlay(fileid, seq);
}

/**
 * 服务器回调页面初始加载
 * 
 * @return
 */
function documentInitPlayCallback(fileids,filenames,filepages){
	for(var i=0;i<fileids.length;i++){
		var fileid = fileids[i];
		var filename = filenames[i];
		var filepage = filepages[i];
		documentPlay(fileid,filename,filepage);
	}
}

/**
 * 服务器回调播放文档
 * 
 * @param json
 * @return
 */
function documentPlayCallback(fileid, seq, total, filename) {
	var docMenuPage = documentMenuPagePrefix+fileid;
	var imgurl = "../manage/document?oper=imgPreview&fileid=" + fileid
			+ "&seq=" + seq;
	var page = seq+"/"+total;
	Ext.getCmp(docMenuPage).setValue(page);
	documentLoadImage(fileid,imgurl,filename);
	
}

/**
 * 服务回调初始化文档下拉页数
 * 
 * @param fileid
 * @param pages
 * @return
 */
function documentPageSelect(fileid,pages){
	var docMenuPage = documentMenuPagePrefix+fileid;
	var docDataArray = [ [ '0/0', '全部' ] ];
	var pageArr = pages.split(";")
	for(var i=0;i<pageArr.length;i++){
		var page = pageArr[i];
		if(page != ""){
			if(i==0){
				Ext.getCmp(docMenuPage).setValue(page);
			}
			docDataArray.push( [ page, page ]);
		}
	}
	Ext.getCmp(docMenuPage).getStore().loadData(docDataArray);
}

/**
 * 文档画图
 * 
 * @param fileid
 * @param imghtml
 * @return
 */
function documentDrawer(fileid,divhtml,width,height,imghtml,filename){
	var wbPointId = DOC_DRAW_POINTER + fileid;
	var wbLineId = DOC_DRAW_LINE + fileid;
	var wbArrowLineId = DOC_DRAW_ARROWLINE + fileid;
	var wbPolyLineId = DOC_DRAW_POLYLINE + fileid;
	var wbTextId = DOC_DRAW_TEXT + fileid;
	var wbRectId = DOC_DRAW_RECT + fileid;
	var wbEllipseId = DOC_DRAW_ELLIPSE + fileid;
	
	var docContentId = documentContentPrefix+fileid;
	$("#"+docContentId).attr("innerHTML", imghtml);
	
	var documentDrawId = documentDrawPrefix+fileid;
	var oridwdiv = document.getElementById("documentWhiteDiv");
	if(oridwdiv){
		document.getElementById(docContentId).removeChild(oridwdiv);
	}
	var divEl = document.createElement("div");
	divEl.id="documentWhiteDiv";
	divEl.className="dwImageDiv";
	divEl.innerHTML=divhtml;
	document.getElementById(docContentId).appendChild(divEl);
	
	docZoomWidthArray[fileid] = 0;
	docZoomHeightArray[fileid] = 0;
	
	var imageName = getTime()+".gif";
	DocumentWhiteBoardService.createWhiteBoardContent(fileid,imageName,width,height,function(){
		var drawO = new whiteBoardDrawer(documentDrawId);
		drawO.init(fileid,filename,width,height);
		drawO.setStroke(5);
		drawO.setFont("14");
		drawO.setDrawType(DOC_DRAW_POLYLINE);
		drawO.setColor(USERCOLOR);
		docWbDrawMap[fileid] = drawO;
		docWhiteboardMenuMap[fileid] = [[wbPolyLineId,true],[wbLineId,false],[wbArrowLineId,false],[wbTextId,false],[wbRectId,false],[wbEllipseId,false],[wbPointId,false]];

	});
}

/**
 * 获取图片
 * 
 * @param url
 * @return
 */
function documentLoadImage(fileid,url,filename) {
	var docContentId = documentContentPrefix+fileid;
	var image = new Image();
	image.src = url;
	if (image.complete) {
		var html = documentImageLoaded(fileid,image, url);
		documentDrawer(fileid,html[0],html[1],html[2],html[3],filename);
		return;
	}
	image.onload = function() {
		var html = documentImageLoaded(fileid,image, url);
		documentDrawer(fileid,html[0],html[1],html[2],html[3],filename);
		return;
	}
	$("#"+docContentId).attr("innerHTML", "<span style='color:red'>文档正在加载中......</span>");
}

/**
 * 图片加载完成
 * 
 * @param image
 * @param path
 * @return
 */
function documentImageLoaded(fileid,image, path) {
	var docImgId = documentImgPrefix+fileid;
	var documentDrawId = documentDrawPrefix+fileid;
	var documentDrawImgId = documentDrawImgPrefix+fileid;
	var widthf = parseFloat(image.width);
	var heightf = parseFloat(image.height);
	var rate = widthf / heightf;
	var imgwidth = contentPanel.getWidth() - 20;
	var imgheight = parseInt(imgwidth / rate);
	var html = "";
	if (isIE()) {
		html = "<v:image id='"+docImgId+"' src='" + path + "' style='width:"
				+ imgwidth + "px;height:" + imgheight + "px;display:block;' onmousedown='return false;' onmousemove='return false;' onmouseup='return false;'/>";
	} else {
		html = "<img class='highqual' alt='图片' id='"+docImgId+"' src='" + path
				+ "' width='" + imgwidth + "' height='" + imgheight + "' onmousedown='return false;' onmousemove='return false;' onmouseup='return false;'>";
	}
	var divHtml = new stringBuilder();
	divHtml.append('<div style="position:absolute;left:0px;top:0px;padding:0;margin:0;" id="'+documentDrawId+'" style="width:'+imgwidth+'px;height:'+imgheight+'px" onmousedown="docWhiteBoardOnmousedown(\''+fileid+'\',event)"');
	divHtml.append(' onmousemove="docWhiteBoardOnmousemove(\''+fileid+'\',event)"');
	divHtml.append(' onmouseup="docWhiteBoardOnmouseup(\''+fileid+'\',event)"');
	divHtml.append(' onmouseover="docWhiteBoardOnmouseover(\''+fileid+'\',event)"');
	divHtml.append(' onmouseout="docWhiteBoardOnmouseout(\''+fileid+'\',event)"');
	divHtml.append(' ondbclick="docWhiteBoardOndblclick(\''+fileid+'\',event)">');
	divHtml.append(' <img src="../images/blank.png" id="'+documentDrawImgId+'" border="0" style="position:absolute;left:0px;top:0px;width:'+imgwidth+'px;height:'+imgheight+'px;margin:0px;"/>');
	divHtml.append('</div>');
	
	return [divHtml.toString(),imgwidth,imgheight,html];
}

/**
 * 翻到上一页
 * 
 * @param fileid
 * @return
 */
function documentMenuPrePage(fileid){
	DocumentService.documentPre(fileid);
}

/**
 * 翻到制定页
 * 
 * @param fileid
 * @return
 */
function documentMenuSpecPage(fileid,value){
	var seq = value.substring(0,value.indexOf("/"));
	if(seq == '0'){
		DocumentService.documentShowAll(fileid);
	}else{
		DocumentService.documentPlay(fileid,seq);
	}
}

/**
 * 翻到下一页
 * 
 * @param fileid
 * @return
 */
function documentMenuNextPage(fileid){
	DocumentService.documentNext(fileid);
}

/**
 * 显示所有文档图片
 * 
 * @return
 */
function documentShowAllCallback(fileid,fileCol){
	// 禁用上下翻页
	var documentPagePreId = documentPagePre+fileid;
	// Ext.getCmp(documentPagePre).disable();
	
	var docContentId = documentContentPrefix+fileid;
	var pageArr = fileCol.split(";")
	document.getElementById(docContentId).innerHTML = "";
	for(var i=0;i<pageArr.length;i++){
		var page = pageArr[i];
		var seq = page.substring(0,page.indexOf("/"));
		if(page != ""){
			var docImgId = documentImgPrefix+fileid+"_"+seq;
			var imgurl = "../manage/document?oper=imgPreview&fileid=" + fileid
				+ "&seq=" + seq;
			var image = new Image();
			image.src = imgurl;
			var html = "";
			if (isIE()) {
				html = "<v:image id='"+docImgId+"' src='" + imgurl + "' style='width:200px;height:200px;display:block;'/>";
			} else {
				html = "<img class='highqual' id='"+docImgId+"' src='" + imgurl
						+ "' style='width:200px;height:200px;display:block;'>";
			}
			documentShowAllImages(fileid,html,seq);
		}
	}
}

/**
 * 显示文档
 * 
 * @param image
 * @return
 */
function documentShowAllImages(fileid,image,seq){
	var docContentId = documentContentPrefix+fileid;
	var html = new stringBuilder();
	var span = document.createElement("span");
	html.append("<ul style='margin:0;'>");
	html.append("<li style='margin:10px;list-style:none;width:210px;float:left;cursor:hand' onclick='documentPlay2(\""+fileid+"\",\""+seq+"\")'>");
	html.append(image);
	html.append("</li>");
	html.append("</ul>");
	span.innerHTML = html.toString();
	document.getElementById(docContentId).appendChild(span);
}

/**
 * 设置自动播放文档的时间
 * 
 * @param fileid
 * @param time
 * @return
 */
function documentAutoPlayTime(fileid,time){
	DocumentService.documentPlay(fileid, 1);
	DocumentService.documentStopAutoPlay(fileid,time,function(){
		documentMenuAutoplay(fileid,time);
	});
}

/**
 * 自动播放文档
 * 
 * @param fileid
 * @return
 */
function documentMenuAutoplay(fileid,value){
	DocumentService.documentPlay(fileid, 1);
	DocumentService.documentAutoPlay(fileid,value);
}

/**
 * 放大文档
 * 
 * @param fileid
 * @return
 */
function documentMenuZoomOut(fileid){
	var docImgId = documentImgPrefix+fileid;
	var oriImgWidth = $("#"+docImgId).css("width");
	var oriImgHeight = $("#"+docImgId).css("height");
	var widthf = parseFloat(oriImgWidth);
	var heightf = parseFloat(oriImgHeight);
	var rate = widthf / heightf;
	var newImgWidth = parseInt(oriImgWidth) + 100;
	var newImgHeight = parseInt(newImgWidth / rate);
	
	docZoomWidthArray[fileid] = newImgWidth - oriImgWidth;
	docZoomHeightArray[fileid] = newImgHeight - oriImgHeight;
	
	var documentDrawId = documentDrawPrefix+fileid;
	var documentDrawImgId = documentDrawImgPrefix+fileid;
	
	$("#"+documentDrawId).css("width",newImgWidth);
	$("#"+documentDrawId).css("height",newImgHeight);
	
	$("#"+documentDrawImgId).css("width",newImgWidth);
	$("#"+documentDrawImgId).css("height",newImgHeight);
	
	$("#"+docImgId).css("width",newImgWidth);
	$("#"+docImgId).css("height",newImgHeight);
}

/**
 * 缩小文档
 * 
 * @param fileid
 * @return
 */
function documentMenuZoomIn(fileid){
	var docImgId = documentImgPrefix+fileid;
	var oriImgWidth = $("#"+docImgId).css("width");
	var oriImgHeight = $("#"+docImgId).css("height");
	var widthf = parseFloat(oriImgWidth);
	var heightf = parseFloat(oriImgHeight);
	var rate = widthf / heightf;
	var newImgWidth = parseInt(oriImgWidth) - 100;
	if(newImgWidth < 0){
		return;
	}
	var newImgHeight = parseInt(newImgWidth / rate);
	
	docZoomWidthArray[fileid] = newImgWidth - widthf;
	docZoomHeightArray[fileid] = newImgHeight - heightf;
	
	var documentDrawId = documentDrawPrefix+fileid;
	var documentDrawImgId = documentDrawImgPrefix+fileid;
	
	$("#"+documentDrawId).css("width",newImgWidth);
	$("#"+documentDrawId).css("height",newImgHeight);
	
	$("#"+documentDrawImgId).css("width",newImgWidth);
	$("#"+documentDrawImgId).css("height",newImgHeight);
	
	$("#"+docImgId).css("width",newImgWidth);
	$("#"+docImgId).css("height",newImgHeight);
}

/**
 * 显示文档列表
 * 
 * @return
 */
function documentManagement(){
	docDiag = new Dialog();
	docDiag.Width = 950;
	docDiag.Height = 350;
	docDiag.Title = "文档列表";
	docDiag.URL = "../meeting?oper=documentList&action=share";
	docDiag.show();
}

/**
 * 在文档列表中文档上传
 * 
 * @return
 */
function documentManagementUpload(){
	docUploadDiag = new Dialog();
	docUploadDiag.Width = 550;
	docUploadDiag.Height = 210;
	docUploadDiag.Title = "上传文档";
	docUploadDiag.URL = "../meeting/document_upload.jsp?oper=documentMgr";
	docUploadDiag.MessageTitle = "<span style='font-size:13px;'>上传文档</span>";
	docUploadDiag.Message = "<span style='font-size:13px;'>请按照提示，文档上传完会进行转换，请耐心等待！</span>";
	docUploadDiag.show();
}

/**
 * 在文档列表中关闭上传文档对话框
 * 
 * @return
 */
function documentManagementUploadClose(FILEID, FILENAME,flag){
	docUploadDiag.close();
	if(flag == 1){
		documentPlay(FILEID, FILENAME, 1);
	}
}

/**
 * 关闭文档
 * 
 * @return
 */
function documentDeleteCallback(fileid){
	var docId = documentPrefix+fileid;
	contentPanel.remove(docId);
}

/**
 * 菜单栏选中画图按钮事件
 * 
 * @param wbId
 * @param selId
 * @param state
 * @return
 */
function docWhiteBoardMenubarHandler(fileid,selId,state){
	var drawObj = docWhiteBoardDrawerObj(fileid);
	//docWhiteBoardInitDrawText(fileid,drawObj.textleft,drawObj.texttop);
	
	var wbPointId = DOC_DRAW_POINTER + fileid;
	var wbLineId = DOC_DRAW_LINE + fileid;
	var wbArrowLineId = DOC_DRAW_ARROWLINE + fileid;
	var wbPolyLineId = DOC_DRAW_POLYLINE + fileid;
	var wbTextId = DOC_DRAW_TEXT + fileid;
	var wbRectId = DOC_DRAW_RECT + fileid;
	var wbEllipseId = DOC_DRAW_ELLIPSE + fileid;
	
	if(state){
		if(userSid == hostSid){
			if(wbPointId == selId){
				drawObj.setDrawType(DOC_DRAW_POINTER);
				Ext.getCmp(wbPointId).toggle(true);
				
			} 
		}
		
		if(wbLineId == selId){
			drawObj.setDrawType(DOC_DRAW_LINE);
			Ext.getCmp(wbLineId).toggle(true);
			
		}else if(wbArrowLineId == selId){
			drawObj.setDrawType(DOC_DRAW_ARROWLINE);
			Ext.getCmp(wbArrowLineId).toggle(true);
			
		}else if(wbPolyLineId == selId){
			drawObj.setDrawType(DOC_DRAW_POLYLINE);
			Ext.getCmp(wbPolyLineId).toggle(true);
			
		}else if(wbTextId == selId){
			drawObj.setDrawType(DRAW_TEXT);
			Ext.getCmp(wbTextId).toggle(true);
			
		}else if(wbRectId == selId){
			drawObj.setDrawType(DOC_DRAW_RECT);
			Ext.getCmp(wbRectId).toggle(true);
			
		}else if(wbEllipseId == selId){
			drawObj.setDrawType(DOC_DRAW_ELLIPSE);
			Ext.getCmp(wbEllipseId).toggle(true);
		}
		docWhiteBoardUpdateMenuState(fileid,selId,true);
	} else {
		var menuToggled = docWhiteBoardMenuToggled(fileid);
		if(menuToggled == selId && menuToggled!=wbPointId){
			drawObj.setDrawType(DOC_DRAW_POINTER);
			if(userSid == hostSid){
				// Ext.getCmp(wbPointId).toggle(true);
			}
		}else{
			if(menuToggled != ""){
				Ext.getCmp(menuToggled).toggle(false);
			}
			docWhiteBoardUpdateMenuState(selId,false);
			
			drawObj.setDrawType("");
			var wbPointId = DOC_DRAW_POINTERIMG+fileid;
			var whiteboardContentId = whiteboardContentPrefix+fileid;
			var imgDiv = document.getElementById(wbPointId);
			if(imgDiv){
				document.getElementById(whiteboardContentId).removeChild(imgDiv);
			}
		}
	}
}

/**
 * 更新菜单状态
 * 
 * @param key
 * @return
 */
function docWhiteBoardUpdateMenuState(wbId,mId,flag){
	var wbMenuArray = docWhiteboardMenuMap[wbId];
	if(wbMenuArray){
		var length = wbMenuArray.length;
		var tmpMenuStateArray = [];
		for(var i=0;i<length;i++){
			var menuStateArr = wbMenuArray[i];
			var menuId = menuStateArr[0];
			if(menuId == mId){
				tmpMenuStateArray.push([menuId,flag]);
			}else{
				tmpMenuStateArray.push([menuId,false]);
			}
		}
		docWhiteboardMenuMap[wbId]=tmpMenuStateArray;
	}
}

/**
 * 获取按下的菜单
 * 
 * @return
 */
function docWhiteBoardMenuToggled(wbId){
	var wbMenuArray = docWhiteboardMenuMap[wbId];
	var length = wbMenuArray.length;
	for(var i=0;i<length;i++){
		var menuStateArr = wbMenuArray[i];
		var menuState = menuStateArr[1];
		if(menuState){
			return menuStateArr[0];
		}
	}
	return "";
}

/**
 * 获取绘图对象
 * 
 * @param wbId
 * @return
 */
function docWhiteBoardDrawerObj(fileid){
	var drawO = docWbDrawMap[fileid];
	return drawO;
}

/**
 * 初始化写字
 * 
 * @param fileid
 * @param x
 * @param y
 * @return
 */
function docWhiteBoardInitDrawText(fileid,x,y){
	var docContentId = documentContentPrefix+fileid;
	var drawTxtInput = DOC_DRAW_TEXT_INPUT + fileid;
	var inputTxt = document.getElementById(drawTxtInput);
	var whiteboardObj = document.getElementById(docContentId);
	if(inputTxt && whiteboardObj){
		var inputVal = inputTxt.value;
		if(inputVal != null && inputVal.length >0){
			DocumentWhiteBoardService.drawText(fileid,inputVal,x,y,USERCOLOR);
		}
		inputTxt.style.display="none";
		whiteboardObj.removeChild(inputTxt);
	}
}

/**
 * 
 * @param fileid
 * @param e
 * @return
 */
function docWhiteBoardOnscroll(fileid){
	var o = docWhiteBoardDrawerObj(fileid);
	if(!o){
		return;
	}
	var docContentId = documentContentPrefix+fileid;
	var toTop = document.getElementById(docContentId).scrollTop;
	o.setToTop(toTop);
}

/**
 * 鼠标经过形状
 * 
 * @param wbId
 * @param e
 * @return
 */
function docWhiteBoardOnmouseover(fileid,e){
	var docContentId = documentContentPrefix+fileid;
	document.getElementById(docContentId).className="dwMouseOver";
}

/**
 * 鼠标移出
 * 
 * @param wbId
 * @param e
 * @return
 */
function docWhiteBoardOnmouseout(fileid,e){
	var docContentId = documentContentPrefix+fileid;
	document.getElementById(docContentId).className="dwMouseOut";
}

/**
 * 绘图面板鼠标按下事件
 * 
 * @param e
 * @return
 */
function docWhiteBoardOnmousedown(wbId,e) {
	e = window.event || e;
	var o = docWhiteBoardDrawerObj(wbId);
	if(!o){
		return;
	}
	var drawtype = o.getDrawType();
	if (drawtype == DOC_DRAW_POLYLINE) {
		// 画曲线
		o.setDraw(true);
		o.drawLineString = new stringBuilder();
	} else if (drawtype == DRAW_TEXT) {
		// 写字
		var docContentId = documentContentPrefix+o.wbId;
		docWhiteBoardInitDrawText(o.wbId,o.textleft,o.texttop);
		e = window.event || e;
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.textleft = x;
		o.texttop = y;
		inputTxt = document.createElement("input");
		inputTxt.id=DOC_DRAW_TEXT_INPUT+wbId;
		inputTxt.className = "drawTextInput";
		inputTxt.style.left=o.textleft;
		inputTxt.style.top=o.texttop;
		inputTxt.style.display="block";
		inputTxt.onkeyup=function(e){
			var event = window.event || e;
			var ie = navigator.appName == "Microsoft Internet Explorer" ? true : false;
			if (ie) {
				if ((!event.shiftKey) && (event.keyCode == 13)) {
					docWhiteBoardInitDrawText(o.wbId,o.textleft,o.texttop);
				}
			} else {
				 if (isKeyTrigger(e, 13, true)) {
					 docWhiteBoardInitDrawText(o.wbId,o.textleft,o.texttop);
				 }
			}
		}
		document.getElementById(docContentId).appendChild(inputTxt);
		inputTxt.focus();
	} else if(drawtype == DOC_DRAW_ARROWLINE || drawtype == DOC_DRAW_LINE || drawtype == DOC_DRAW_RECT || drawtype == DOC_DRAW_ELLIPSE){
		o.setDraw(true);
		o.drawLineString = new stringBuilder();
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.startX = x;
		o.startY = y;
		o.drawLineString.append(o.startX).append(",").append(o.startY).append(" ");
	} else if(drawtype == DOC_DRAW_POINTER){
		o.setDraw(true);
		
		var wbPointId = DOC_DRAW_POINTERIMG+o.wbId;
		var docContentId = documentContentPrefix+o.wbId;
		var imgDiv = document.getElementById(wbPointId);
		if(imgDiv){
			o.setStart(null);
			document.getElementById(docContentId).removeChild(imgDiv);
		}
		
		if(!o.start){
			var docContentId = documentContentPrefix+o.wbId;
			var pointImgId = DOC_DRAW_POINTERIMG+o.wbId;
			var pointerImg = document.createElement("img");
			var x = e.x || e.pageX;
			var y = e.y-24 || e.pageY-24;
			pointerImg.src="../images/netmeeting/whiteboard_pointer.gif";
			pointerImg.id=pointImgId;
			pointerImg.style.left=x;
			pointerImg.style.top=y;
			pointerImg.className="pointerImg";
			document.getElementById(docContentId).appendChild(pointerImg);
		}
	} 
}

/**
 * 绘图面板鼠标移动事件
 * 
 * @param e
 * @return
 */
function docWhiteBoardOnmousemove(wbId,e) {
	e = window.event || e;
	var o = docWhiteBoardDrawerObj(wbId);
	if(!o)
		return;
	var drawtype = o.getDrawType();
	var draw = o.getDraw();
	var start = o.getStart();
	if (!draw)
		return;
	if (drawtype == DOC_DRAW_POLYLINE) {
		if (o.start) {
			var x = e.x || e.pageX;
			var y = e.y-24 || e.pageY-24;
			if(o.getToTop() > 0){
				y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
			}
			                  
			o.endX = x;
			o.endY = y;
			o.drawLineString.append(o.startX).append(",").append(o.startY).append(" ");
			o.drawLineString.append(o.endX).append(",").append(o.endY).append(" ");
			o.startX = o.endX;
			o.startY = o.endY;
			
			o.end = [ x, y ];
			o.gc.drawLine.apply(o.gc, o.start.concat(o.end));
			o.gc.paint();
			o.start = o.end;				
			return;
		}
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.startX = x;
		o.startY = y;
		o.start = [ x, y ];
	} else if(drawtype == DOC_DRAW_ARROWLINE){
		o.gc.clear();
		if (o.start) {
			var x = e.x || e.pageX;
			var y = e.y-24 || e.pageY-24;
			if(o.getToTop() > 0){
				y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
			}
			o.end = [ x, y ];
			o.gc.drawLine.apply(o.gc, o.start.concat(o.end));
			o.gc.paint();
			return;
		}
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.start = [ x, y ];
	} else if(drawtype == DOC_DRAW_LINE){
		o.gc.clear();
		if (o.start) {
			var x = e.x || e.pageX;
			var y = e.y-24 || e.pageY-24;
			if(o.getToTop() > 0){
				y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
			}
			o.end = [ x, y ];
			o.gc.drawLine.apply(o.gc, o.start.concat(o.end));
			o.gc.paint();
			return;
		}
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.start = [ x, y ];
	} else if(drawtype == DOC_DRAW_RECT){
		o.gc.clear();
		if (o.start) {
			var x = e.x || e.pageX;
			var y = e.y-24 || e.pageY-24;
			if(o.getToTop() > 0){
				y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
			}
			o.endX = x;
			o.endY = y;
			o.gc.drawLine.apply(o.gc,[o.startX,o.startY, o.startX, o.endY]);
			o.gc.paint();
			o.gc.drawLine.apply(o.gc,[o.startX,o.startY, o.endX, o.startY]);
			o.gc.paint();
			o.gc.drawLine.apply(o.gc,[o.startX, o.endY, o.endX, o.endY]);
			o.gc.paint();
			o.gc.drawLine.apply(o.gc,[o.endX, o.startY, o.endX, o.endY]);
			o.gc.paint();
			return;
		}
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.startX = x;
		o.startY = y;
		o.start = [ x, y ];
	} else if(drawtype == DOC_DRAW_ELLIPSE){
		o.gc.clear();
		if (o.start) {
			var x = e.x || e.pageX;
			var y = e.y-24 || e.pageY-24;
			if(o.getToTop() > 0){
				y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
			}
			o.endX = x;
			o.endY = y;
			o.gc.drawEllipse.apply(o.gc,[o.startX,o.startY, Math.abs(o.endX-o.startX), Math.abs(o.endY-o.startY)]);
			o.gc.paint();
			return;
		}
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.startX = x;
		o.startY = y;
		o.start = [ x, y ];
	} else if(drawtype == DOC_DRAW_POINTER){
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.start = [ x, y ];
	}
}

/**
 * 绘图面板鼠标弹起时的事件
 * 
 * @param e
 * @return
 */
function docWhiteBoardOnmouseup(wbId,e) {
	e = window.event || e;
	var o = docWhiteBoardDrawerObj(wbId);
	if (o.drawtype == DOC_DRAW_POLYLINE) {
		o.draw = false;
		o.start = null;
		var newImgName = getTime()+".gif";
		DocumentWhiteBoardService.drawPolyLine(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),function(){
			o.setImageName(newImgName);
		});
	} else if (o.drawtype == DOC_DRAW_TEXT) {
		var textObj = document.getElementById(DOC_DRAW_TEXT_INPUT+wbId);
		if(textObj){
			textObj.focus();
		}
	} else if(o.drawtype == DOC_DRAW_ARROWLINE){
		o.draw = false;
		o.start = null;
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.endX = x;
		o.endY = y;
		if(o.startX == o.endX && o.startY == o.endY){
			return;
		}else{
			o.drawLineString.append(o.endX).append(",").append(o.endY);
			var newImgName = getTime()+".gif";
			DocumentWhiteBoardService.drawArrowLine(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),function(){
				o.setImageName(newImgName);
			});
		}
	} else if(o.drawtype == DOC_DRAW_LINE){
		o.draw = false;
		o.start = null;
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.endX = x;
		o.endY = y;
		if(o.startX == o.endX && o.startY == o.endY){
			return;
		}else{
			o.drawLineString.append(o.endX).append(",").append(o.endY);
			var newImgName = getTime()+".gif";
			DocumentWhiteBoardService.drawLine(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),function(){
				o.setImageName(newImgName);
			});
		}
	} else if(o.drawtype == DOC_DRAW_RECT){
		o.draw = false;
		o.start = null;
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.endX = x;
		o.endY = y;
		if(o.startX == o.endX && o.startY == o.endY){
			return;
		}else{
			o.drawLineString.append(o.endX).append(",").append(o.endY);
			var newImgName = getTime()+".gif";
			DocumentWhiteBoardService.drawRectangle(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),function(){
				o.setImageName(newImgName);
			});
		}
	} else if(o.drawtype == DOC_DRAW_ELLIPSE){
		o.draw = false;
		o.start = null;
		var x = e.x || e.pageX;
		var y = e.y-24 || e.pageY-24;
		if(o.getToTop() > 0){
			y = e.y-24+o.getToTop() || e.pageY-24+o.getToTop();
		}
		o.endX = x;
		o.endY = y;
		if(o.startX == o.endX && o.startY == o.endY){
			return;
		}else{
			var tmpX = Math.abs((o.endX - o.startX)/2);
			var tmpY = Math.abs((o.endY - o.startY)/2);
			o.drawLineString.append(o.endX).append(",").append(o.endY);
			var newImgName = getTime()+".gif";
			DocumentWhiteBoardService.drawEllipse(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),tmpX,tmpY,function(){
				o.setImageName(newImgName);
			});
		}
	}
}

/**
 * 绘图面板鼠标双击事件
 * 
 * @param e
 * @return
 */
function docWhiteBoardOndblclick(wbId,e){
	var o = docWhiteBoardDrawerObj(wbId);
	if(o.drawtype == DOC_DRAW_POINTER){
		o.draw = false;	
		o.start = null;
		var wbPointId = DOC_DRAW_POINTERIMG+o.wbId;
		var whiteboardContentId = whiteboardContentPrefix+o.wbId;
		var imgDiv = document.getElementById(wbPointId);
		if(imgDiv)
			document.getElementById(whiteboardContentId).removeChild(imgDiv);
	}
}


/**
 * 服务器回调
 * 
 * @param imagePath
 * @return
 */
function docWhiteBoardDrawlineCallback(wbId,imageName){
	docWhiteBoardDrawerObj(wbId).clear();
	var documentDrawImgId = documentDrawImgPrefix+wbId;
	var imgUrl = "../meeting?oper=docWhiteBoardView&meetingId="+meetingId+"&wbId="+wbId+"&filename="+imageName+"&tt="+getTime();
	$("#"+documentDrawImgId).attr("src",imgUrl);
	
}

/**
 * 服务器回调重写文字
 * 
 * @param wbId
 * @param text
 * @return
 */
function docWhiteBoardRedrawTextCallback(wbId,text){
	var json = JSON.parse(text);
	docWhiteBoardDrawerObj(wbId).clear();
	for(var i=0;i<json.length;i++){
		var msg = json[i]["msg"];
		var x = json[i]["x"];
		var y = json[i]["y"];
		var color = json[i]["color"];
		docWhiteBoardDrawTextCallback(wbId,msg,x,y,color);
	}
}

/**
 * 服务器回调写字
 * 
 * @param wbId
 * @param x
 * @param y
 * @return
 */
function docWhiteBoardDrawTextCallback(wbId,msg,x,y,color){
	var drawO = docWhiteBoardDrawerObj(wbId);
	drawO.setColor(color);
	drawO.drawText(msg,x,y);
	drawO.setColor(USERCOLOR);
}

/**
 * 初始化写字
 * 
 * @param wbId
 * @param x
 * @param y
 * @return
 */
function docWhiteBoardInitDrawText(wbId,x,y){
	var docContentId = documentContentPrefix+wbId;
	var drawTxtInput = DOC_DRAW_TEXT_INPUT + wbId;
	var inputTxt = document.getElementById(drawTxtInput);
	var whiteboardObj = document.getElementById(docContentId);
	if(inputTxt && whiteboardObj){
		var inputVal = inputTxt.value;
		if(inputVal != null && inputVal.length >0){
			DocumentWhiteBoardService.drawText(wbId,inputVal,x,y,USERCOLOR);
		}
		inputTxt.style.display="none";
		whiteboardObj.removeChild(inputTxt);
	}
}

// ---------------------------白板共享函数----------------------------------------
var whiteboardDivPrefix = "whiteboardDiv_";
var whiteboardPrefix = "whiteboard_";
var whiteboardMenuPrefix = "whiteboardMenu_";
var whiteboardMenuFontSizePrefix = "whiteboardMenuFontSize_";
var whiteboardContentPrefix = "whiteboardContent_"
var whiteboardContentImgPrefix = "whiteboardContentImg_";

var whiteboardNamePrefix = "白板_";

var DRAW_LINE = "wbLine";
var DRAW_ARROWLINE = "wbArrowLine";
var DRAW_POLYLINE = "wbPolyLine";
var DRAW_TEXT = "wbText";
var DRAW_RECT = "wbRect";
var DRAW_ELLIPSE = "wbEllipse";
var DRAW_POINTER = "wbPoint";
var DRAW_POINTERIMG = "wbPointImg";
var DRAW_ENABLE = "wbEnable";

var DRAW_TEXT_INPUT = "draw_text_input";

var whiteboardMenuMap = {};

var wbDrawMap = {}; // [wbId:drawO]

/**
 * 白板初始化
 * 
 * @return
 */
function whiteBoardInit(wbId){
	if(wbId == '0'){
		wbId = getTime();
	}
	WhiteBoardService.createWhiteBoard(wbId);
}

/**
 * 服务端回调初始化白板
 * 
 * @param wbId
 * @return
 */
function whiteBoardInitCallback(wbId,imageName,msg){
	var whiteBoardName = whiteboardNamePrefix+wbId;
	var whiteBoardDivId = whiteboardDivPrefix+wbId;
	var whiteBoardId = whiteboardPrefix+wbId;
	var whiteboardMenuId = whiteboardMenuPrefix+wbId;
	var whiteboardContentId = whiteboardContentPrefix+wbId;
	var docHtml = new stringBuilder();
	docHtml.append('<div id="');
	docHtml.append(whiteboardMenuId);
	docHtml.append('"></div><div id="');
	docHtml.append(whiteboardContentId);
	docHtml.append('" style="overflow:hidden;background:#fff;"');
	docHtml.append(' onmousedown="whiteBoardOnmousedown(\''+wbId+'\',event)"');
	docHtml.append(' onmousemove="whiteBoardOnmousemove(\''+wbId+'\',event)"');
	docHtml.append(' onmouseup="whiteBoardOnmouseup(\''+wbId+'\',event)"');
	docHtml.append(' ondbclick="whiteBoardOndblclick(\''+wbId+'\',event)"></div>');
	var divEl = document.createElement("div");
	divEl.id=whiteBoardDivId;
	divEl.innerHTML=docHtml.toString();
	document.getElementById("WhiteBoardDiv").appendChild(divEl);
	if(userSid == hostSid){
		contentPanel
		.add( {
			id : whiteBoardId,
			title : whiteBoardName,
			iconCls : 'whiteboard_new',
			closable : true,
			contentEl : whiteBoardDivId
		});
	} else {
		contentPanel
		.add( {
			id : whiteBoardId,
			title : whiteBoardName,
			iconCls : 'whiteboard_new',
			contentEl : whiteBoardDivId
		});
	}
	
	contentPanel.setActiveTab(whiteBoardId);

	whiteBoardInitMenu(wbId,whiteBoardName);
	whiteBoardInitContent(wbId,imageName,msg);
}

/**
 * 初始化白板菜单栏
 * 
 * @param fileid
 * @param filename
 * @return
 */
function whiteBoardInitMenu(wbId,filename){
	var whiteboardMenuId = whiteboardMenuPrefix+wbId;
	var whiteboardMenuFontSizeId = whiteboardMenuFontSizePrefix+wbId;
	
	var wbPointId = DRAW_POINTER + wbId;
	var wbLineId = DRAW_LINE + wbId;
	var wbArrowLineId = DRAW_ARROWLINE + wbId;
	var wbPolyLineId = DRAW_POLYLINE + wbId;
	var wbTextId = DRAW_TEXT + wbId;
	var wbRectId = DRAW_RECT + wbId;
	var wbEllipseId = DRAW_ELLIPSE + wbId;
	var wbEnable = DRAW_ENABLE + wbId;
	
	var wbToolbar = new Ext.Toolbar( {
		buttonAlign : 'left'
	});
	wbToolbar.render(whiteboardMenuId);
	
	// 字体大小下拉框
	fontSizeStore = new Ext.data.ArrayStore({
        fields: ['id', 'name'],
        data : [
                ['9','9px'],
                ['10','10px'],
                ['11','11px'],
                ['12','12px'],
                ['13','13px'],
                ['14','14px']
        ]
    });
    fontSizeCombo = new Ext.form.ComboBox({
    	id : whiteboardMenuFontSizeId,
    	editable : false,
        store: fontSizeStore,
        valueField : 'id',
        displayField:'name',
        typeAhead: true,
        mode: 'local',
        width : 50,
        triggerAction: 'all',
        selectOnFocus:true,
        listeners : {
			'select' : function(comboVar, record, index) {
				var value = Ext.getCmp(docMenuAutoPlay).getValue(index);
				documentAutoPlayTime(fileid,value);
			}
		}
    });
    fontSizeCombo.setValue('9');
	
    if(userSid == hostSid){
    	wbToolbar.add( {
    		id : wbEnable,
    		iconCls : 'whiteboard_enable',
    		tooltip : '开启其他人白板功能',
    		enableToggle : true,
    		toggleHandler : function(b,state){
    			if(state){
    				WhiteBoardService.enableWhiteBoard(wbId,"true",function(){
    					Ext.getCmp(wbEnable).setIconClass("whiteboard_disable");
    					Ext.getCmp(wbEnable).setTooltip('关闭其他人白板功能'); 
    				});
    			}else{
    				WhiteBoardService.enableWhiteBoard(wbId,"false",function(){
    					Ext.getCmp(wbEnable).setIconClass("whiteboard_enable");
    					Ext.getCmp(wbEnable).setTooltip('开启其他人白板功能');
    				});
    			}
    		}
    	});
    	wbToolbar.add( {
    		id : wbPointId,
    		iconCls : 'whiteboard_hand',
    		tooltip : '指针',
    		enableToggle : true,
    		toggleGroup : wbId,
    		toggleHandler : function(b,state){
    			whiteBoardMenubarHandler(wbId,wbPointId,state);
    		}
    	});
    }
	wbToolbar.add(ExtBtnSpacer2);
	wbToolbar.add({
		id : wbLineId,
		iconCls : 'whiteboard_line',
		tooltip : '直线',
		enableToggle : true,
		toggleGroup : wbId,
		toggleHandler : function(b,state){
			whiteBoardMenubarHandler(wbId,wbLineId,state);
		}
	});
	wbToolbar.add(ExtBtnSpacer2);
	wbToolbar.add( {
		id : wbArrowLineId,
		iconCls : 'whiteboard_arrow',
		tooltip : '箭头线',
		enableToggle : true,
		toggleGroup : wbId,
		toggleHandler : function(b,state){
			whiteBoardMenubarHandler(wbId,wbArrowLineId,state);
		}
	});
	wbToolbar.add(ExtBtnSpacer2);
	wbToolbar.add( {
		id : wbRectId,
		iconCls : 'whiteboard_rect',
		tooltip : '矩形',
		enableToggle : true,
		toggleGroup : wbId,
		toggleHandler : function(b,state){
			whiteBoardMenubarHandler(wbId,wbRectId,state);
		}
	});
	wbToolbar.add(ExtBtnSpacer2);
	wbToolbar.add( {
		id : wbEllipseId,
		iconCls : 'whiteboard_ellipse',
		tooltip : '椭圆',
		enableToggle : true,
		toggleGroup : wbId,
		toggleHandler : function(b,state){
			whiteBoardMenubarHandler(wbId,wbEllipseId,state);
		}
	});
	wbToolbar.add(ExtBtnSpacer2);
	wbToolbar.add( {
		id : wbPolyLineId,
		iconCls : 'whiteboard_pen',
		tooltip : '钢笔',
		enableToggle : true,
		toggleGroup : wbId,
		toggleHandler : function(b,state){
			whiteBoardMenubarHandler(wbId,wbPolyLineId,state);
		}
	});
	wbToolbar.add(ExtBtnSpacer2);
	wbToolbar.add( {
		id : wbTextId,
		iconCls : 'whiteboard_text',
		tooltip : '文本',
		toggleGroup : wbId,
		enableToggle : true,
		toggleHandler : function(b,state){
			whiteBoardMenubarHandler(wbId,wbTextId,state);
		}
	});
	if(userSid == hostSid){
		wbToolbar.add('-');
		wbToolbar.add(ExtBtnSpacer2);
		wbToolbar.add( {
			iconCls : 'whiteboard_undo',
			tooltip : '撤销',
			listeners : {
				'click' : function() {
					whiteBoardMenuUndo(wbId);
				}
			}
		});
		wbToolbar.add(ExtBtnSpacer2);
		wbToolbar.add( {
			iconCls : 'whiteboard_redo',
			tooltip : '重做',
			listeners : {
				'click' : function() {
					whiteBoardMenuRedo(wbId);
				}
			}
		});
		wbToolbar.add(ExtBtnSpacer2);
		wbToolbar.add( {
			iconCls : 'whiteboard_trash',
			tooltip : '删除所有',
			listeners : {
				'click' : function() {
					whiteBoardMenuClear(wbId);
				}
			}
		});
	}
	
	wbToolbar.doLayout();
}

/**
 * 开启/关闭白板
 * 
 * @param wbId
 * @param flag
 * @return
 */
function enableWhiteBoardCallback(wbId,flag){
	var drawObj = whiteBoardDrawerObj(wbId);
	var wbLineId = DRAW_LINE + wbId;
	var wbArrowLineId = DRAW_ARROWLINE + wbId;
	var wbPolyLineId = DRAW_POLYLINE + wbId;
	var wbTextId = DRAW_TEXT + wbId;
	var wbRectId = DRAW_RECT + wbId;
	var wbEllipseId = DRAW_ELLIPSE + wbId;
	
	var inputTxtId = DRAW_TEXT_INPUT+wbId;
	if(flag == "true"){
		drawObj.setDrawType(DRAW_POLYLINE);
		Ext.getCmp(wbPolyLineId).enable();
		Ext.getCmp(wbLineId).enable();
		Ext.getCmp(wbArrowLineId).enable();
		Ext.getCmp(wbTextId).enable();
		Ext.getCmp(wbRectId).enable();
		Ext.getCmp(wbEllipseId).enable();
		showMsg("主持人开启了您的白板画图功能");
	} else{
		drawObj.setDrawType("");
		Ext.getCmp(wbPolyLineId).disable();
		Ext.getCmp(wbLineId).disable();
		Ext.getCmp(wbArrowLineId).disable();
		Ext.getCmp(wbTextId).disable();
		Ext.getCmp(wbRectId).disable();
		Ext.getCmp(wbEllipseId).disable();
		showMsg("主持人禁止了您的白板画图功能");
		
		var whiteboardContentId = whiteboardContentPrefix+wbId;
		var drawTxtInput = DRAW_TEXT_INPUT + wbId;
		var inputTxt = document.getElementById(drawTxtInput);
		var whiteboardObj = document.getElementById(whiteboardContentId);
		if(inputTxt && whiteboardObj){
			inputTxt.style.display="none";
			whiteboardObj.removeChild(inputTxt);
		}
	}
}

/**
 * 菜单栏选中按钮事件
 * 
 * @param wbId
 * @param selId
 * @param state
 * @return
 */
function whiteBoardMenubarHandler(wbId,selId,state){
	var drawObj = whiteBoardDrawerObj(wbId);
	whiteBoardInitDrawText(wbId,drawObj.textleft,drawObj.texttop);
	
	var wbPointId = DRAW_POINTER + wbId;
	var wbLineId = DRAW_LINE + wbId;
	var wbArrowLineId = DRAW_ARROWLINE + wbId;
	var wbPolyLineId = DRAW_POLYLINE + wbId;
	var wbTextId = DRAW_TEXT + wbId;
	var wbRectId = DRAW_RECT + wbId;
	var wbEllipseId = DRAW_ELLIPSE + wbId;
	
	if(state){
		if(userSid == hostSid){
			if(wbPointId == selId){
				drawObj.setDrawType(DRAW_POINTER);
				Ext.getCmp(wbPointId).toggle(true);
				
			} 
		}
		
		if(wbLineId == selId){
			drawObj.setDrawType(DRAW_LINE);
			Ext.getCmp(wbLineId).toggle(true);
			
		}else if(wbArrowLineId == selId){
			drawObj.setDrawType(DRAW_ARROWLINE);
			Ext.getCmp(wbArrowLineId).toggle(true);
			
		}else if(wbPolyLineId == selId){
			drawObj.setDrawType(DRAW_POLYLINE);
			Ext.getCmp(wbPolyLineId).toggle(true);
			
		}else if(wbTextId == selId){
			drawObj.setDrawType(DRAW_TEXT);
			Ext.getCmp(wbTextId).toggle(true);
			
		}else if(wbRectId == selId){
			drawObj.setDrawType(DRAW_RECT);
			Ext.getCmp(wbRectId).toggle(true);
			
		}else if(wbEllipseId == selId){
			drawObj.setDrawType(DRAW_ELLIPSE);
			Ext.getCmp(wbEllipseId).toggle(true);
		}
		whiteBoardUpdateMenuState(wbId,selId,true);
	} else {
		var menuToggled = whiteBoardMenuToggled(wbId);
		if(menuToggled == selId && menuToggled!=wbPointId){
			drawObj.setDrawType(DRAW_POINTER);
			if(userSid == hostSid){
				Ext.getCmp(wbPointId).toggle(true);
			}
		}else{
			if(menuToggled != ""){
				Ext.getCmp(menuToggled).toggle(false);
			}
			whiteBoardUpdateMenuState(selId,false);
			
			drawObj.setDrawType("");
			var wbPointId = DRAW_POINTERIMG+wbId;
			var whiteboardContentId = whiteboardContentPrefix+wbId;
			var imgDiv = document.getElementById(wbPointId);
			if(imgDiv){
				document.getElementById(whiteboardContentId).removeChild(imgDiv);
			}
		}
	}
}

/**
 * 初始化白板内容
 * 
 * @param fileid
 * @param filename
 * @return
 */
function whiteBoardInitContent(wbId,imageName,msg){
	var whiteboardContentId = whiteboardContentPrefix+wbId;
	var imgHeight = contentPanel.getHeight()-60;
	var imgWidth = contentPanel.getWidth();
	$("#"+whiteboardContentId).css("width",imgWidth);
	$("#"+whiteboardContentId).css("height",imgHeight);
	
	WhiteBoardService.createWhiteBoardContent(wbId,imageName,imgWidth,imgHeight,function(){
		var imgId = whiteboardContentImgPrefix+wbId;
		var imgHtml = "<img id='"+imgId+"' src='../meeting?oper=whiteBoardView&wbId="+wbId+"&filename="+imageName+"' onmousedown='return false;' onmousemove='return false;' onmouseup='return false;'/>";
		$("#"+whiteboardContentId).attr("innerHTML",imgHtml);
		
		var drawO = new whiteBoardDrawer(whiteboardContentId);
		drawO.init(wbId,imageName,imgWidth,imgHeight);
		drawO.setStroke(5);
		drawO.setFont("14");
		drawO.setDrawType(DRAW_POLYLINE);
		drawO.setColor(USERCOLOR);
		wbDrawMap[wbId] = drawO;
		whiteboardMenuMap[wbId] = [[wbPolyLineId,true],[wbLineId,false],[wbArrowLineId,false],[wbTextId,false],[wbRectId,false],[wbEllipseId,false],[wbPointId,false]];
		
		var wbPointId = DRAW_POINTER + wbId;
		var wbLineId = DRAW_LINE + wbId;
		var wbArrowLineId = DRAW_ARROWLINE + wbId;
		var wbPolyLineId = DRAW_POLYLINE + wbId;
		var wbTextId = DRAW_TEXT + wbId;
		var wbRectId = DRAW_RECT + wbId;
		var wbEllipseId = DRAW_ELLIPSE + wbId;
		Ext.getCmp(wbPolyLineId).toggle(true);
		Ext.getCmp(wbLineId).toggle(false);
		Ext.getCmp(wbArrowLineId).toggle(false);
		Ext.getCmp(wbTextId).toggle(false);
		Ext.getCmp(wbRectId).toggle(false);
		Ext.getCmp(wbEllipseId).toggle(false);
		if(userSid == hostSid){
			Ext.getCmp(wbPointId).toggle(false);
		} else{
			drawO.setDrawType("");
			Ext.getCmp(wbPolyLineId).disable();
			Ext.getCmp(wbLineId).disable();
			Ext.getCmp(wbArrowLineId).disable();
			Ext.getCmp(wbTextId).disable();
			Ext.getCmp(wbRectId).disable();
			Ext.getCmp(wbEllipseId).disable();
		}
		
		if(msg != null && msg.length>0){
			whiteBoardRedrawTextCallback(wbId,msg);
		}
	});
}

/**
 * 更新菜单状态
 * 
 * @param key
 * @return
 */
function whiteBoardUpdateMenuState(wbId,mId,flag){
	var wbMenuArray = whiteboardMenuMap[wbId];
	if(wbMenuArray){
		var length = wbMenuArray.length;
		var tmpMenuStateArray = [];
		for(var i=0;i<length;i++){
			var menuStateArr = wbMenuArray[i];
			var menuId = menuStateArr[0];
			if(menuId == mId){
				tmpMenuStateArray.push([menuId,flag]);
			}else{
				tmpMenuStateArray.push([menuId,false]);
			}
		}
		whiteboardMenuMap[wbId]=tmpMenuStateArray;
	}
}

/**
 * 获取按下的菜单
 * 
 * @return
 */
function whiteBoardMenuToggled(wbId){
	var wbMenuArray = whiteboardMenuMap[wbId];
	var length = wbMenuArray.length;
	for(var i=0;i<length;i++){
		var menuStateArr = wbMenuArray[i];
		var menuState = menuStateArr[1];
		if(menuState){
			return menuStateArr[0];
		}
	}
	return "";
}

/**
 * 获取绘图对象
 * 
 * @param wbId
 * @return
 */
function whiteBoardDrawerObj(wbId){
	var drawO = wbDrawMap[wbId];
	return drawO;
}

/**
 * 绘图面板鼠标按下事件
 * 
 * @param e
 * @return
 */
function whiteBoardOnmousedown(wbId,e) {
	e = window.event || e;
	var o = whiteBoardDrawerObj(wbId);
	if(!o){
		return;
	}
	var drawtype = o.getDrawType();
	if (drawtype == DRAW_POLYLINE) {
		// 画曲线
		o.setDraw(true);
		o.drawLineString = new stringBuilder();
	} else if (drawtype == DRAW_TEXT) {
		// 写字
		var whiteboardContentId = whiteboardContentPrefix+o.wbId;
		whiteBoardInitDrawText(o.wbId,o.textleft,o.texttop);
		e = window.event || e;
		o.textleft = e.x || e.pageX;
		o.texttop = e.y -24 || e.pageY -24;
		inputTxt = document.createElement("input");
		inputTxt.id=DRAW_TEXT_INPUT+wbId;
		inputTxt.className = "drawTextInput";
		inputTxt.style.left=o.textleft;
		inputTxt.style.top=o.texttop;
		inputTxt.style.display="block";
		inputTxt.onkeyup=function(e){
			var event = window.event || e;
			var ie = navigator.appName == "Microsoft Internet Explorer" ? true : false;
			if (ie) {
				if ((!event.shiftKey) && (event.keyCode == 13)) {
					whiteBoardInitDrawText(o.wbId,o.textleft,o.texttop);
				}
			} else {
				 if (isKeyTrigger(e, 13, true)) {
					 whiteBoardInitDrawText(o.wbId,o.textleft,o.texttop);
				 }
			}
		}
		document.getElementById(whiteboardContentId).appendChild(inputTxt);
		inputTxt.focus();
	} else if(drawtype == DRAW_ARROWLINE || drawtype == DRAW_LINE || drawtype == DRAW_RECT || drawtype == DRAW_ELLIPSE){
		o.setDraw(true);
		o.drawLineString = new stringBuilder();
		o.startX = e.x || e.pageX;
		o.startY = e.y-24 || e.pageY-24;
		o.drawLineString.append(o.startX).append(",").append(o.startY).append(" ");
	} else if(drawtype == DRAW_POINTER){
		o.setDraw(true);
		
		var wbPointId = DRAW_POINTERIMG+o.wbId;
		var whiteboardContentId = whiteboardContentPrefix+o.wbId;
		var imgDiv = document.getElementById(wbPointId);
		if(imgDiv){
			o.setStart(null);
			document.getElementById(whiteboardContentId).removeChild(imgDiv);
		}
		
		if(!o.start){
			var whiteboardContentId = whiteboardContentPrefix+o.wbId;
			var pointImgId = DRAW_POINTERIMG+o.wbId;
			var pointerImg = document.createElement("img");
			var x = e.x || e.pageX;
			var y = e.y-24 || e.pageY-24;
			pointerImg.src="../images/netmeeting/whiteboard_pointer.gif";
			pointerImg.id=pointImgId;
			pointerImg.style.left=x;
			pointerImg.style.top=y;
			pointerImg.className="pointerImg";
			document.getElementById(whiteboardContentId).appendChild(pointerImg);
		}
	} 
}

/**
 * 绘图面板鼠标移动事件
 * 
 * @param e
 * @return
 */
function whiteBoardOnmousemove(wbId,e) {
	e = window.event || e;
	var o = whiteBoardDrawerObj(wbId);
	if(!o)
		return;
	var drawtype = o.getDrawType();
	var draw = o.getDraw();
	var start = o.getStart();
	if (!draw)
		return;
	if (drawtype == DRAW_POLYLINE) {
		if (o.start) {
			o.endX = e.x || e.pageX;
			o.endY = e.y-24 || e.pageY-24;
			o.drawLineString.append(o.startX).append(",").append(o.startY).append(" ");
			o.drawLineString.append(o.endX).append(",").append(o.endY).append(" ");
			o.startX = o.endX;
			o.startY = o.endY;
			
			o.end = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
			o.gc.drawLine.apply(o.gc, o.start.concat(o.end));
			o.gc.paint();
			o.start = o.end;				
			return;
		}
		o.startX = e.x || e.pageX;
		o.startY = e.y-24 || e.pageY-24;
		o.start = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
	} else if(drawtype == DRAW_ARROWLINE){
		o.gc.clear();
		if (o.start) {
			o.end = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
			o.gc.drawLine.apply(o.gc, o.start.concat(o.end));
			o.gc.paint();
			return;
		}
		o.start = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
	} else if(drawtype == DRAW_LINE){
		o.gc.clear();
		if (o.start) {
			o.end = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
			o.gc.drawLine.apply(o.gc, o.start.concat(o.end));
			o.gc.paint();
			return;
		}
		o.start = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
	} else if(drawtype == DRAW_RECT){
		o.gc.clear();
		if (o.start) {
			o.endX = e.x || e.pageX;
			o.endY = e.y-24 || e.pageY-24;
			o.gc.drawLine.apply(o.gc,[o.startX,o.startY, o.startX, o.endY]);
			o.gc.paint();
			o.gc.drawLine.apply(o.gc,[o.startX,o.startY, o.endX, o.startY]);
			o.gc.paint();
			o.gc.drawLine.apply(o.gc,[o.startX, o.endY, o.endX, o.endY]);
			o.gc.paint();
			o.gc.drawLine.apply(o.gc,[o.endX, o.startY, o.endX, o.endY]);
			o.gc.paint();
			return;
		}
		o.startX = e.x || e.pageX;
		o.startY = e.y-24 || e.pageY-24;
		o.start = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
	} else if(drawtype == DRAW_ELLIPSE){
		o.gc.clear();
		if (o.start) {
			o.endX = e.x || e.pageX;
			o.endY = e.y-24 || e.pageY-24;
			o.gc.drawEllipse.apply(o.gc,[o.startX,o.startY, Math.abs(o.endX-o.startX), Math.abs(o.endY-o.startY)]);
			o.gc.paint();
			return;
		}
		o.startX = e.x || e.pageX;
		o.startY = e.y-24 || e.pageY-24;
		o.start = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
	} else if(drawtype == DRAW_POINTER){
		o.start = [ e.x || e.pageX, e.y-24 || e.pageY-24 ];
	}
}

/**
 * 绘图面板鼠标弹起时的事件
 * 
 * @param e
 * @return
 */
function whiteBoardOnmouseup(wbId,e) {
	e = window.event || e;
	var o = whiteBoardDrawerObj(wbId);
	if (o.drawtype == DRAW_POLYLINE) {
		o.draw = false;
		o.start = null;
		var newImgName = getTime()+".gif";
		WhiteBoardService.drawPolyLine(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),function(){
			o.setImageName(newImgName);
		});
	} else if (o.drawtype == DRAW_TEXT) {
		var textObj = document.getElementById(DRAW_TEXT_INPUT+wbId);
		if(textObj){
			textObj.focus();
		}
	} else if(o.drawtype == DRAW_ARROWLINE){
		o.draw = false;
		o.start = null;
		o.endX = e.x || e.pageX;
		o.endY = e.y-24 || e.pageY-24;
		if(o.startX == o.endX && o.startY == o.endY){
			return;
		}else{
			o.drawLineString.append(o.endX).append(",").append(o.endY);
			var newImgName = getTime()+".gif";
			WhiteBoardService.drawArrowLine(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),function(){
				o.setImageName(newImgName);
			});
		}
	} else if(o.drawtype == DRAW_LINE){
		o.draw = false;
		o.start = null;
		o.endX = e.x || e.pageX;
		o.endY = e.y-24 || e.pageY-24;
		if(o.startX == o.endX && o.startY == o.endY){
			return;
		}else{
			o.drawLineString.append(o.endX).append(",").append(o.endY);
			var newImgName = getTime()+".gif";
			WhiteBoardService.drawLine(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),function(){
				o.setImageName(newImgName);
			});
		}
	} else if(o.drawtype == DRAW_RECT){
		o.draw = false;
		o.start = null;
		o.endX = e.x || e.pageX;
		o.endY = e.y-24 || e.pageY-24;
		if(o.startX == o.endX && o.startY == o.endY){
			return;
		}else{
			o.drawLineString.append(o.endX).append(",").append(o.endY);
			var newImgName = getTime()+".gif";
			WhiteBoardService.drawRectangle(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),function(){
				o.setImageName(newImgName);
			});
		}
	} else if(o.drawtype == DRAW_ELLIPSE){
		o.draw = false;
		o.start = null;
		o.endX = e.x || e.pageX;
		o.endY = e.y-24 || e.pageY-24;
		if(o.startX == o.endX && o.startY == o.endY){
			return;
		}else{
			var tmpX = Math.abs((o.endX - o.startX)/2);
			var tmpY = Math.abs((o.endY - o.startY)/2);
			o.drawLineString.append(o.endX).append(",").append(o.endY);
			var newImgName = getTime()+".gif";
			WhiteBoardService.drawEllipse(USERCOLOR,o.wbId,newImgName,o.drawLineString.toString(),tmpX,tmpY,function(){
				o.setImageName(newImgName);
			});
		}
	}
}

/**
 * 绘图面板鼠标双击事件
 * 
 * @param e
 * @return
 */
function whiteBoardOndblclick(wbId,e){
	var o = whiteBoardDrawerObj(wbId);
	if(o.drawtype == DRAW_POINTER){
		o.draw = false;	
		o.start = null;
		var wbPointId = DRAW_POINTERIMG+o.wbId;
		var whiteboardContentId = whiteboardContentPrefix+o.wbId;
		var imgDiv = document.getElementById(wbPointId);
		if(imgDiv)
			document.getElementById(whiteboardContentId).removeChild(imgDiv);
	}
}


/**
 * 定义whiteBoardDrawer类
 */
function whiteBoardDrawer(el) {
	el = this.el = document.getElementById(el);
	this.gc = new jsGraphics(el);
	this.stroke = 3;
	this.color = "#000000";
	this.drawLineString = new stringBuilder();
	this.wbId = "";
	this.imageName = "";
	this.width=0;
	this.height=0;
	this.draw=false;
	this.start=null;
	this.drawtype="";
	this.toTop=0;
	o = this;
}

/**
 * 设置whiteBoardDrawer的成员函数
 */
whiteBoardDrawer.prototype = {
	init : function(id,name,width,height){
		this.wbId = id;
		this.imageName = name;
		this.width = width;
		this.height = height;
	},
	setToTop : function(v){
		this.toTop=v;
	},
	getToTop : function(){
		return this.toTop;
	},
	setDraw : function(f){
		this.draw = f;
	},
	setStart : function(s){
		this.start = s;
	},
	setDrawType : function(s){
		this.drawtype = s;
	},
	getDraw : function(){
		return this.draw;
	},
	getStart : function(){
		return this.start;
	},
	getDrawType : function(){
		return this.drawtype;
	},
	setImageName : function(s){
		this.imageName = s;
	},
	setColor : function(c) {
		this.color = c;
		this.gc.setColor(c);
	},
	setFont : function(size){
		this.gc.setFont( "宋体", size, Font.BOLD ); 
	},
	setStroke : function(c) {
		this.stroke = c;
		this.gc.setStroke(c);
	},
	clear : function(c) {
		this.gc.clear();
	},
	drawText : function(msg,x,y) {
		this.gc.drawString(msg, x,y);
		this.gc.paint();
	},
	runCode : function(s) {
		s = s.split(';');
		this.repaint(s);
	},
	repaint : function(s) {
		if(s.length){
			var o = this,i=0,l=s.length;
			for(;i<l;i++){
				var p = s[i];
				eval(p);
			}
		}
	},
	undoPaint : function(){
		this.setColor("#FFFFFF");
		var lineString = this.linePoints.join('');
		var s = lineString.split(';');
        this.repaint(s);
		this.setColor("#000000");
	}
};

/**
 * 闪光点移动
 */
function whiteBoardPointerMove(x,y,pointerId){
	var pointerImg = document.getElementById(pointerId);
	pointerImg.style.left=x;
	pointerImg.style.top=y;
}

/**
 * 服务器回调
 * 
 * @param imagePath
 * @return
 */
function whiteBoardDrawlineCallback(wbId,imageName){
	whiteBoardDrawerObj(wbId).clear();
	var imgId = whiteboardContentImgPrefix+wbId;
	var imgUrl = "../meeting?oper=whiteBoardView&wbId="+wbId+"&filename="+imageName+"&tt="+getTime();
	$("#"+imgId).attr("src",imgUrl);
}

/**
 * 初始化写字
 * 
 * @param wbId
 * @param x
 * @param y
 * @return
 */
function whiteBoardInitDrawText(wbId,x,y){
	var whiteboardContentId = whiteboardContentPrefix+wbId;
	var drawTxtInput = DRAW_TEXT_INPUT + wbId;
	var inputTxt = document.getElementById(drawTxtInput);
	var whiteboardObj = document.getElementById(whiteboardContentId);
	if(inputTxt && whiteboardObj){
		var inputVal = inputTxt.value;
		if(inputVal != null && inputVal.length >0){
			WhiteBoardService.drawText(wbId,inputVal,x,y,USERCOLOR);
		}
		inputTxt.style.display="none";
		whiteboardObj.removeChild(inputTxt);
	}
}

/**
 * 服务器回调写字
 * 
 * @param wbId
 * @param x
 * @param y
 * @return
 */
function whiteBoardDrawTextCallback(wbId,msg,x,y,color){
	var drawO = whiteBoardDrawerObj(wbId);
	drawO.setColor(color);
	drawO.drawText(msg,x,y);
	drawO.setColor(USERCOLOR);
}

/**
 * 服务器回调重写文字
 * 
 * @param wbId
 * @param text
 * @return
 */
function whiteBoardRedrawTextCallback(wbId,text){
	var json = JSON.parse(text);
	whiteBoardDrawerObj(wbId).clear();
	for(var i=0;i<json.length;i++){
		var msg = json[i]["msg"];
		var x = json[i]["x"];
		var y = json[i]["y"];
		var color = json[i]["color"];
		whiteBoardDrawTextCallback(wbId,msg,x,y,color);
	}
}

/**
 * 撤销
 * 
 * @param wbId
 * @return
 */
function whiteBoardMenuUndo(wbId){
	WhiteBoardService.drawUndo(wbId);
}

/**
 * 重做
 * 
 * @param wbId
 * @return
 */
function whiteBoardMenuRedo(wbId){
	WhiteBoardService.drawRedo(wbId);
}

/**
 * 清除白板
 * 
 * @param wbId
 * @return
 */
function whiteBoardMenuClear(wbId){
	var drawObj = whiteBoardDrawerObj(wbId);
	var newImgName = getTime()+".gif";
	WhiteBoardService.drawClear(wbId,newImgName,drawObj.width,drawObj.height);
}

/**
 * 服务器回调删除某个白板
 * 
 * @param wbId
 * @return
 */
function whiteboardDeleteCallback(wbId){
	var whiteBoardId = whiteboardPrefix+wbId;
	contentPanel.remove(whiteBoardId);
}


// ---------------------------远程桌面共享处理函数----------------------------------------

var ScreenSharePrefix = "ScreenShare";
var ScreenShareTabPrefix = ScreenSharePrefix+"Tab_";
var ScreenShareDivPrefix = ScreenSharePrefix+"Div_";
var ScreenShareMenuPrefix = ScreenSharePrefix+"Menu_";
var ScreenShareContentPrefix = ScreenSharePrefix+"Content_";

var ScreenShareMenuFullScreenPrefix = ScreenShareMenuPrefix+"FullScreen_";
var ScreenShareMenuPausePrefix = ScreenShareMenuPrefix+"Pause_";
var ScreenShareMenuStopPrefix = ScreenShareMenuPrefix+"Stop_";
var ScreenShareMenuReconnectPrefix = ScreenShareMenuPrefix+"Reconnect_";
var ScreenShareMenuSavePrefix = ScreenShareMenuPrefix+"Save_";
var ScreenShareMenuSelectPrefix = ScreenShareMenuPrefix+"Select_";

/**
 * 初始化桌面共享
 */
function screenShareInit(){
	Dialog.confirm("桌面共享需下载‘桌面共享控制器’是否下载？<br/>下载后：<br/>&nbsp;&nbsp;1.解压下载后的ZIP文件<br/>&nbsp;&nbsp;2.运行其中的应用程序。",function(){
		var sspId = 1000+gen_random(1000,8999);
		var password = 1000+gen_random(1000,8999);
		
		screenShareInitSspId(sspId);
		
		dwr.engine.setAsync(false);
		DesktopShareService.screenShareStartNewVncProxy(sspId,serverName,password,{
			callback : function(data) {
				dwr.engine.openInDownload(data);
				screenShareInitContent(sspId);
				dwr.engine.setAsync(true); 
			},
			async : false
		});
	},function(){
		Ext.getCmp('desktopShareMenuItem').enable();
	});
}

/**
 * 初始化桌面共享
 * 
 * @param sspId
 * @param password
 * @return
 */
function screenShareInitSspId(sspId){
	var ssHtml = new stringBuilder();
	var ScreenShareMenuId = ScreenShareMenuPrefix + sspId;
	var ScreenShareContentId = ScreenShareContentPrefix + sspId;
	var ScreenShareDivId = ScreenShareDivPrefix + sspId;
	var ScreenShareTabId = ScreenShareTabPrefix + sspId;
	
	ssHtml.append('<div id="');
	ssHtml.append(ScreenShareMenuId);
	ssHtml.append('"></div><div id="');
	ssHtml.append(ScreenShareContentId);
	ssHtml.append('" style="overflow:auto;background:#ffffff;"></div>');
	var divEl = document.createElement("div");
	divEl.id=ScreenShareDivId;
	divEl.innerHTML=ssHtml.toString();
	document.getElementById("ScreenShareDiv").appendChild(divEl);
	if(userSid == hostSid){
		contentPanel.add( {
			id : ScreenShareTabId,
			title : '桌面共享',
			iconCls : 'screenShare_monitor',
			closable : true,
			contentEl : ScreenShareDivId
		});
	}else{
		contentPanel.add( {
			id : ScreenShareTabId,
			title : '桌面共享',
			iconCls : 'screenShare_monitor',
			contentEl : ScreenShareDivId
		});
	}
	contentPanel.setActiveTab(ScreenShareTabId);
	
	screenShareInitMenu(sspId);
}

/**
 * 初始化菜单
 * 
 * @return
 */
function screenShareInitMenu(sspId){
	var ScreenShareMenuId = ScreenShareMenuPrefix + sspId;
	var ScreenShareMenuReconnectId = ScreenShareMenuReconnectPrefix + sspId;
	var ScreenShareMenuFullScreenId = ScreenShareMenuFullScreenPrefix + sspId;
	var ScreenShareMenuSaveId = ScreenShareMenuFullScreenPrefix + sspId;
	var ScreenShareMenuSelectId = ScreenShareMenuSelectPrefix + sspId;
	var ScreenShareTabId = ScreenShareTabPrefix + sspId;
	
	var ssToolBar = new Ext.Toolbar( {
		buttonAlign : 'left'
	});
	ssToolBar.render(ScreenShareMenuId);
	
	ssToolBar.add({
		id : ScreenShareMenuFullScreenId,
		iconCls : 'screenShare_delete',
		tooltip : '退出',
		listeners : {
			'click' : function() {
				Dialog.confirm("是否确定退出桌面共享?",function(){
					if(userSid == hostSid){
						screenShareShutdown(sspId);
					}else{
						contentPanel.remove(ScreenShareTabId);
					}
				});
			}
		}
	});
	
	ssToolBar.doLayout();
}

/**
 * 初始化桌面共享body
 * 
 * @param sspId
 * @return
 */
function screenShareInitContent(sspId){
	var ScreenShareContentId = ScreenShareContentPrefix + sspId;
	var html = new stringBuilder();
	html.append("<table align='center' style='margin-top:150px;width:350px;font-size:13px;padding:20px;'>");
	html.append("<tr>");
	html.append("<td style='font-weight:bold;height:25px;'>您已经成功下载了\"桌面共享控制器.zip\"<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' style='height:25px;'>1.首先解压此zip文件<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' style='height:25px;'>2.运行其中的exe程序<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' id='sspTd_"+sspId+"' style='height:25px;'>剩余时间：<span id='sspSpan_"+sspId+"'></span><td>");
	html.append("</tr>");
	html.append("</table>");
	document.getElementById(ScreenShareContentId).innerHTML = html.toString();
}

/**
 * 服务更新用户运行exe剩余时间
 * 
 * @param count
 * @return
 */
function screenShareServerWaitStatusCallback(sspId,count){
	var spanId = "sspSpan_"+sspId;
	var spanObj = document.getElementById(spanId);
	if(spanObj)
		spanObj.innerHTML = count+"秒";
}

/**
 * 用户已经运行了exe，发送请求使与会者启用桌面共享
 * 
 * @return
 */
function screenShareServerConnectedCallback(sspId,flag){
	var tdId = "sspTd_"+sspId;
	var tdObj = document.getElementById(tdId);
	if(tdObj){
		if(flag){
			tdObj.innerHTML = "<span style='color:green;margin-top:100px;font-size:13px;font-weight:bold;'>您的桌面正在共享中...</span>";
			DesktopShareService.screenShareNotifyViewers(sspId);
		}else{
			tdObj.innerHTML = "<span style='color:red;margin-top:100px;font-size:13px;font-weight:bold;'>启动桌面共享控制器超时...</span>";
		}
	}
}

/**
 * 服务端回调，通知客户端观看桌面共享
 * 
 * @param sspId
 * @param port
 * @param password
 * @return
 */
function screenShareNotifyViewersCallbackApplet(sspId,port,password,securityPort){
	screenShareInitSspId(sspId);
	var ScreenShareContentId = ScreenShareContentPrefix + sspId;
	var imgHeight = contentPanel.getHeight()-60;
	var imgWidth = contentPanel.getWidth();
	var appletHtml = new stringBuilder();
	appletHtml.append('<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" width="'+imgWidth+'" height="'+imgHeight+'" border="0">');
	appletHtml.append('<param name="code" value="com.meeting.applet.TightVncViewer" />');
	appletHtml.append('<param name="archive" value="../screenApplet.jar" />');
	appletHtml.append('<param name="netHost" value="'+serverName+'" />');
	appletHtml.append('<param name="netPort" value="'+port+'" />');
	appletHtml.append('<param name="netVncPassword" value="'+password+'" />');
		appletHtml.append('<embed width="'+imgWidth+'" height="'+imgHeight+'" ');
			appletHtml.append('type="application/x-java-applet" ');
			appletHtml.append('code="com.meeting.applet.TightVncViewer" ');
			appletHtml.append('archive="../screenApplet.jar" ');
			appletHtml.append('netHost="'+serverName+'" ');
			appletHtml.append('netPort="'+port+'" ');
			appletHtml.append('netVncPassword="'+password+'" ');
			appletHtml.append('<noembed>');
				appletHtml.append('您的浏览器不支持Java环境，请安装Java运行时环境。');
			appletHtml.append('</noembed>');
		appletHtml.append('</embed>');
	appletHtml.append('</object>');
	document.getElementById(ScreenShareContentId).innerHTML = appletHtml.toString();
}

/**
 * 服务端回调，通知客户端观看桌面共享
 * 
 * @param sspId
 * @param port
 * @param password
 * @return
 */
function screenShareNotifyViewersCallback(sspId,port,password,securityPort){
	screenShareInitSspId(sspId);
	
	var ScreenShareContentId = ScreenShareContentPrefix + sspId;
	var imgHeight = contentPanel.getHeight()-60;
	var imgWidth = contentPanel.getWidth();
	var flashVal = new stringBuilder();
	flashVal.append("../Flashlight.swf");
	flashVal.append("?hideControls=true");
	flashVal.append("&autoConnect=true");
	flashVal.append("&encoding=tight");
	flashVal.append("&scale=true");
	flashVal.append("&colorDepth=16");
	flashVal.append("&viewOnly=true");
	flashVal.append("&host="+serverName);
	flashVal.append("&port="+port);
	flashVal.append("&password="+password);
	flashVal.append("&securityPort="+securityPort);
	
	var flashid = "Flashlight_"+sspId;
	var flashHtml = new stringBuilder();
	flashHtml.append("<object id='"+flashid+"' width='100%' height='"+imgHeight+"' type='application/x-shockwave-flash' data='"+flashVal.toString()+"'>");
	flashHtml.append("<param name='movie' value='"+flashVal.toString()+"'/>");
	flashHtml.append("<param name='allowScriptAccess' value='always'/>");
	flashHtml.append("<param name='allowFullScreen' value='true' />");
	flashHtml.append("<param name='wmode' value='opaque'/>");
	flashHtml.append("</object>");
	document.getElementById(ScreenShareContentId).innerHTML = flashHtml.toString();
	
}

/**
 * 主持人关闭桌面共享
 * 
 * @param id
 * @return
 */
function screenShareShutdown(sspId){
	DesktopShareService.screenShareShutdown(sspId,function(){
		Ext.getCmp('desktopShareMenuItem').enable();
	});
}

/**
 * 服务端回调关闭客户端桌面共享标签页
 * 
 * @param sspId
 * @return
 */
function screenShareShutdownCallback(sspId){
	showMsg("主持人关闭了桌面共享！");
	var ScreenShareTabId = ScreenShareTabPrefix + sspId;
	contentPanel.remove(ScreenShareTabId);
}



// ---------------------------远程桌面协助处理函数----------------------------------------

var ScreenControlPrefix = "ScreenControl";
var ScreenControlTabPrefix = ScreenControlPrefix+"Tab_";
var ScreenControlDivPrefix = ScreenControlPrefix+"Div_";
var ScreenControlMenuPrefix = ScreenControlPrefix+"Menu_";
var ScreenControlContentPrefix = ScreenControlPrefix+"Content_";

var ScreenControlMenuFullScreenPrefix = ScreenControlMenuPrefix+"FullScreen_";
var ScreenControlMenuPausePrefix = ScreenControlMenuPrefix+"Pause_";
var ScreenControlMenuStopPrefix = ScreenControlMenuPrefix+"Stop_";
var ScreenControlMenuReconnectPrefix = ScreenControlMenuPrefix+"Reconnect_";
var ScreenControlMenuSavePrefix = ScreenControlMenuPrefix+"Save_";
var ScreenControlMenuSelectPrefix = ScreenControlMenuPrefix+"Select_";

var globalSspId = "";
var globalSspDlg;

/**
 * 远程协助面板
 * 
 * @return
 */
function desktopControlDialog(){
	globalSspDlg = new Dialog();
	globalSspDlg.Width = 350;
	globalSspDlg.Height = 150;
	globalSspDlg.Title = "选择远程协助的人员";
	globalSspDlg.URL = "../meeting/screen_control.jsp?meetingId="+meetingId+"&sessionId="+userSid;
	globalSspDlg.MessageTitle = "<span style='font-size:13px;'>请选择需要协助的人员</span>";
	globalSspDlg.Message = "<span style='font-size:13px;'>您可以单击用户列表，右键选择\"远程协助\"</span>";
	globalSspDlg.show();
}

/**
 * 关闭对话框
 * 
 * @return
 */
function sspDialogClose(){
	globalSspDlg.close();
	Ext.getCmp('desktopControlMenuItem').enable();
}

/**
 * 关闭对话框
 * 
 * @return
 */
function sspDialogUserSel(sessionid,userSelName){
	globalSspDlg.close();
	Ext.getCmp('desktopControlMenuItem').disable();
	screenControlConfrim(userSid,sessionid);
	Ext.getCmp('desktopControlMenuItem').disable();
	showMsg("已发向【"+userSelName+"】送远程协助请求");
}

/**
 * 请求控制者确认是否同意远程控制
 * 
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlConfrim(serverId,viewerId){
	DesktopControlService.screenControlConfrim(serverId,viewerId);
}

/**
 * 服务端回调 客户端是否同意远程协助
 * 
 * @param username
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlConfrimCallback(username,serverId,viewerId){
	Dialog.confirm("【"+username+"】请求您远程协助，是否同意？",function(){
		DesktopControlService.screenControlConfrimResult(serverId,viewerId,true);
	},function(){
		DesktopControlService.screenControlConfrimResult(serverId,viewerId,false);
	})
}

/**
 * 被请求人同意远程协助
 * 
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlConfrimResultOKCallback(serverId, viewerId){
	screenControlInit(serverId,viewerId);
}

/**
 * 被请求人不同意远程协助
 * 
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlConfrimResultNOCallback(username,serverId, viewerId){
	Dialog.confirm("【"+username+"】不同意远程协助，是否再次邀请？",function(){
		DesktopControlService.screenControlConfrim(serverId,viewerId);
	},function(){
		Ext.getCmp('desktopControlMenuItem').enable();
	});
}

/**
 * 初始化远程协助
 * 
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlInit(serverId,viewerId){
	Dialog.confirm("远程协助需下载‘远程协助控制器’是否下载？<br/>下载后：<br/>&nbsp;&nbsp;1.解压下载后的ZIP文件<br/>&nbsp;&nbsp;2.运行其中的应用程序。",function(){
		var sspId = 1000+gen_random(1000,8999);
		var password = 1000+gen_random(1000,8999);
		
		screenControlInitSspId(sspId,serverId,viewerId);
		
		dwr.engine.setAsync(false);
		DesktopControlService.screenControlStartNewVncProxy(sspId,serverName,password,serverId,viewerId,{
			callback : function(data) {
				dwr.engine.openInDownload(data);
				screenControlInitContent(sspId);
				dwr.engine.setAsync(true); 
			},
			async : false
		});
	},function(){
		Ext.getCmp('desktopControlMenuItem').enable();
	});
}

/**
 * 初始化远程协助
 * 
 * @param sspId
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlInitSspId(sspId,serverId,viewerId){
	var ssHtml = new stringBuilder();
	var ScreenControlMenuId = ScreenControlMenuPrefix + sspId;
	var ScreenControlContentId = ScreenControlContentPrefix + sspId;
	var ScreenControlDivId = ScreenControlDivPrefix + sspId;
	var ScreenControlTabId = ScreenControlTabPrefix + sspId;
	
	ssHtml.append('<div id="');
	ssHtml.append(ScreenControlMenuId);
	ssHtml.append('"></div><div id="');
	ssHtml.append(ScreenControlContentId);
	ssHtml.append('" style="overflow:auto;background:#ffffff;"></div>');
	var divEl = document.createElement("div");
	divEl.id=ScreenControlDivId;
	divEl.innerHTML=ssHtml.toString();
	document.getElementById("ScreenControlDiv").appendChild(divEl);
	if(userSid == serverId){
		contentPanel.add( {
			id : ScreenControlTabId,
			title : '远程协助',
			iconCls : 'desktopControl_menu',
			closable : true,
			contentEl : ScreenControlDivId
		});
	}else{
		contentPanel.add( {
			id : ScreenControlTabId,
			title : '远程协助',
			iconCls : 'desktopControl_menu',
			contentEl : ScreenControlDivId
		});
	}
	contentPanel.setActiveTab(ScreenControlTabId);
	
	screenControlInitMenu(sspId);
}

/**
 * 初始化菜单
 * 
 * @return
 */
function screenControlInitMenu(sspId){
	var ScreenControlMenuId = ScreenControlMenuPrefix + sspId;
	var ScreenControlMenuReconnectId = ScreenControlMenuReconnectPrefix + sspId;
	var ScreenControlMenuFullScreenId = ScreenControlMenuFullScreenPrefix + sspId;
	var ScreenControlMenuSaveId = ScreenControlMenuFullScreenPrefix + sspId;
	var ScreenControlMenuSelectId = ScreenControlMenuSelectPrefix + sspId;
	var ScreenControlTabId = ScreenControlTabPrefix + sspId;
	
	var ssToolBar = new Ext.Toolbar( {
		buttonAlign : 'left'
	});
	ssToolBar.render(ScreenControlMenuId);
	
	ssToolBar.add({
		id : ScreenControlMenuFullScreenId,
		iconCls : 'desktopControl_quit',
		tooltip : '退出',
		listeners : {
			'click' : function() {
				Dialog.confirm("是否确定退出远程协助?",function(){
					screenControlShutdown(sspId);
				});
			}
		}
	});
	
	ssToolBar.doLayout();
}

/**
 * 初始化远程协助body
 * 
 * @param sspId
 * @return
 */
function screenControlInitContent(sspId){
	var ScreenControlContentId = ScreenControlContentPrefix + sspId;
	var html = new stringBuilder();
	html.append("<table align='center' style='margin-top:150px;width:350px;font-size:13px;padding:20px;'>");
	html.append("<tr>");
	html.append("<td style='font-weight:bold;height:25px;'>您已经成功下载了\"远程协助控制器.zip\"<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' style='height:25px;'>1.首先解压此zip文件<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' style='height:25px;'>2.运行其中的exe程序<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' id='sspTd_"+sspId+"' style='height:25px;'>剩余时间：<span id='sspSpan_"+sspId+"'></span><td>");
	html.append("</tr>");
	html.append("</table>");
	document.getElementById(ScreenControlContentId).innerHTML = html.toString();
}

/**
 * 服务更新用户运行exe剩余时间
 * 
 * @param count
 * @return
 */
function screenControlServerWaitStatusCallback(sspId,count){
	var spanId = "sspSpan_"+sspId;
	var spanObj = document.getElementById(spanId);
	if(spanObj)
		spanObj.innerHTML = count+"秒";
}

/**
 * 用户已经运行了exe，发送请求使与会者启用远程协助
 * 
 * @param sspId
 * @param flag
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlServerConnectedCallback(sspId,flag,serverId,viewerId){
	var tdId = "sspTd_"+sspId;
	var tdObj = document.getElementById(tdId);
	if(tdObj){
		if(flag){
			tdObj.innerHTML = "<span style='color:green;margin-top:100px;font-size:13px;font-weight:bold;'>您的桌面正在控制中...</span>";
			DesktopControlService.screenControlNotifyViewers(sspId,serverId,viewerId);
		}else{
			tdObj.innerHTML = "<span style='color:red;margin-top:100px;font-size:13px;font-weight:bold;'>启动远程协助控制器超时...</span>";
		}
	}
}

/**
 * 服务端回调，通知客户端观看远程协助
 * 
 * @param sspId
 * @param port
 * @param password
 * @return
 */
function screenControlNotifyViewersCallbackApplet(sspId,port,password,securityPort){
	screenControlInitSspId(sspId);
	var ScreenControlContentId = ScreenControlContentPrefix + sspId;
	var imgHeight = contentPanel.getHeight()-60;
	var imgWidth = contentPanel.getWidth();
	var appletHtml = new stringBuilder();
	appletHtml.append('<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" width="'+imgWidth+'" height="'+imgHeight+'" border="0">');
	appletHtml.append('<param name="code" value="com.meeting.applet.TightVncViewer" />');
	appletHtml.append('<param name="archive" value="../screenApplet.jar" />');
	appletHtml.append('<param name="netHost" value="'+serverName+'" />');
	appletHtml.append('<param name="netPort" value="'+port+'" />');
	appletHtml.append('<param name="netVncPassword" value="'+password+'" />');
		appletHtml.append('<embed width="'+imgWidth+'" height="'+imgHeight+'" ');
			appletHtml.append('type="application/x-java-applet" ');
			appletHtml.append('code="com.meeting.applet.TightVncViewer" ');
			appletHtml.append('archive="../screenApplet.jar" ');
			appletHtml.append('netHost="'+serverName+'" ');
			appletHtml.append('netPort="'+port+'" ');
			appletHtml.append('netVncPassword="'+password+'" ');
			appletHtml.append('<noembed>');
				appletHtml.append('您的浏览器不支持Java环境，请安装Java运行时环境。');
			appletHtml.append('</noembed>');
		appletHtml.append('</embed>');
	appletHtml.append('</object>');
	document.getElementById(ScreenControlContentId).innerHTML = appletHtml.toString();
}

/**
 * 服务端回调，通知客户端观看远程协助
 * 
 * @param sspId
 * @param port
 * @param password
 * @return
 */
function screenControlNotifyViewersCallback(sspId,port,password,securityPort){
	screenControlInitSspId(sspId);
	
	var ScreenControlContentId = ScreenControlContentPrefix + sspId;
	var imgHeight = contentPanel.getHeight()-60;
	var imgWidth = contentPanel.getWidth();
	var flashVal = new stringBuilder();
	flashVal.append("../Flashlight.swf");
	flashVal.append("?hideControls=true");
	flashVal.append("&autoConnect=true");
	flashVal.append("&encoding=tight");
	flashVal.append("&scale=true");
	flashVal.append("&colorDepth=16");
	flashVal.append("&viewOnly=false");
	flashVal.append("&host="+serverName);
	flashVal.append("&port="+port);
	flashVal.append("&password="+password);
	flashVal.append("&securityPort="+securityPort);
	
	var flashid = "Flashlight_"+sspId;
	var flashHtml = new stringBuilder();
	flashHtml.append("<object id='"+flashid+"' width='100%' height='"+imgHeight+"' type='application/x-shockwave-flash' data='"+flashVal.toString()+"'>");
	flashHtml.append("<param name='movie' value='"+flashVal.toString()+"'/>");
	flashHtml.append("<param name='allowScriptAccess' value='always'/>");
	flashHtml.append("<param name='allowFullScreen' value='true' />");
	flashHtml.append("<param name='wmode' value='opaque'/>");
	flashHtml.append("</object>");
	document.getElementById(ScreenControlContentId).innerHTML = flashHtml.toString();
	
}

/**
 * 主持人关闭远程协助
 * 
 * @param id
 * @return
 */
function screenControlShutdown(sspId){
	DesktopControlService.screenControlShutdown(sspId,function(){
		Ext.getCmp('desktopControlMenuItem').enable();
	});
}

/**
 * 服务端回调关闭客户端远程协助标签页
 * 
 * @param sspId
 * @return
 */
function screenControlShutdownCallback(sspId){
	showMsg("主持人关闭了远程协助！");
	var ScreenControlTabId = ScreenControlTabPrefix + sspId;
	globalSspId = sspId;
	contentPanel.remove(ScreenControlTabId);
}


// ---------------------------文档分发函数调用----------------------------------------
// 文档分发，主持人向参会者分发文档，可以从文档共享中选择，可以选择分发用户，可以选择分发文件格式

var documentDispatchDlg;
var documentDispatchUploadDlg;

/**
 * 文档分发
 */
function documentdispatchWin(){
	documentDispatchDlg = new Dialog();
	documentDispatchDlg.Width = 950;
	documentDispatchDlg.Height = 350;
	documentDispatchDlg.Title = "分发文档管理";
	documentDispatchDlg.URL = "../meeting?oper=documentList&action=dispatch";
	documentDispatchDlg.show();
}

/**
 * 在文档列表中文档上传
 * 
 * @return
 */
function documentdispatchUpload(){
	documentDispatchUploadDlg = new Dialog();
	documentDispatchUploadDlg.Width = 550;
	documentDispatchUploadDlg.Height = 210;
	documentDispatchUploadDlg.Title = "上传文档";
	documentDispatchUploadDlg.URL = "../meeting/filedispatch_upload.jsp";
	documentDispatchUploadDlg.show();
}

/**
 * 关闭对话框
 * 
 * @return
 */
function documentDispatchDiagClose(){
	documentDispatchUploadDlg.close();
	documentDispatchDlg.close();
	Dialog.alert("上传成功！",function(){
		documentdispatchWin();
	});
}

/**
 * 文档分发进度监视
 * 
 * @param fileid
 * @param filename
 * @return
 */
function documentDispatchStatus(fileid,filename){
	documentDispatchDlg.close();
	contentPanel.add( {
		id : "documentDispatch",
		title : '文档分发',
		iconCls : 'documentDispatch_menu',
		closable : true,
		contentEl : "DocumentDisDiv"
	});
	contentPanel.setActiveTab("documentDispatch");
	var url = "../meeting?oper=getMeetingUserList";
	$.post(url,{meetingId:meetingId,userSid:userSid},function(data){
        var json = JSON.parse(data);
        if(json == "" || json.length <1){
        	$("#DocumentDisDiv").html("没有与会人员！");
        	return;
        }
        var html = new stringBuilder();
    	html.append("<div align='center' style='margin-top:150px;margin-bottom:20px;'>");
    	html.append("文件《"+filename+"》上传完毕，正在分发中...");
    	html.append("</div>");
    	
    	html.append("<table cellspacing='0' cellpadding='0' width='60%' align='center' class='dispatchTable'>");
    	html.append("<tr class='dispatchTrHead'>");
    	html.append("<td width='20%' align='center' height='25px'>");
    	html.append("姓名")
    	html.append("</td>");
    	html.append("<td align='center'>");
    	html.append("下载状态")
    	html.append("</td>");
    	html.append("</tr>");
        for ( var i = 0; i < json.length; i++) {
            var sessionid = json[i]["sessionid"];
            var usercode = json[i]["usercode"];
            var username = json[i]["username"];
            html.append("<tr>");
        	html.append("<td width='50%' align='center' height='25px' class='dispatchTd'>");
        	html.append(username)
        	html.append("</td>");
        	html.append("<td align='center' class='dispatchTd' id='dispatchTd_"+sessionid+"'>");
        	html.append("--")
        	html.append("</td>");
        	html.append("</tr>");
        }
        html.append("</table>");
        $("#DocumentDisDiv").html(html.toString());
        
        DocumentDispatchService.documentDispatch(fileid);
    });
}

/**
 * 分发文档
 * 
 * @param username
 * @param fileid
 * @param filename
 * @return
 */
function documentDispatchCallback(username,fileid,filename){
	contentPanel.add( {
		id : "documentDispatch",
		title : '文档分发',
		iconCls : 'documentDispatch_menu',
		contentEl : "DocumentDisDiv"
	});
	contentPanel.setActiveTab("documentDispatch");
	
	var html = new stringBuilder();
	html.append("<div align='center' style='margin-top:150px;margin-bottom:20px;'>");
	html.append("【"+username+"】分发文件《"+filename+"》给您，是否接受...");
	html.append("<div style='margin-top:25px;'>");
	html.append("<input type='button' id='dispatchAccept' name='dispatchAccept' value=' 接  受 ' onclick='documentDispatchAccept(\""+fileid+"\")'/>");
	html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	html.append("<input type='button' id='dispatchReject' name='dispatchReject' value=' 拒  绝 ' onclick='documentDispatchReject(\""+fileid+"\")'/>");
	html.append("</div>");
	html.append("</div>");
	
    $("#DocumentDisDiv").html(html.toString());
}

/**
 * 同意接受文件
 * 
 * @param fileid
 * @return
 */
function documentDispatchAccept(fileid){
	$("#dispatchAccept").attr('disabled',true);
	$("#dispatchReject").attr('disabled',true);
	
	dwr.engine.setAsync(false);
	DocumentDispatchService.documentDispatchAccept(fileid,
		{
			callback : function(data) {
			dwr.engine.openInDownload(data);
			dwr.engine.setAsync(true); 
		},
		async : false
	});
}

/**
 * 拒绝接受文件
 * 
 * @param fileid
 * @return
 */
function documentDispatchReject(fileid){
	$("#dispatchAccept").attr('disabled',true);
	$("#dispatchReject").attr('disabled',true);
	DocumentDispatchService.documentDispatchReject(fileid);
}

/**
 * 服务回调分发
 * 
 * @param sessionId
 * @param flag
 * @return
 */
function documentDispatchStatusCallback(sessionId,flag){
	var tdId = "dispatchTd_"+sessionId;
	if(flag){
		$("#"+tdId).html("<span style='color:green'>下载中</span>");
	}else{
		$("#"+tdId).html("<span style='color:red'>已拒绝</span>");
	}
}

/**
 * 关闭分发标签
 * 
 * @param id
 * @return
 */
function documentDispatchDelete(id){
	DocumentDispatchService.documentDispatchDelete(id);
}

/**
 * 服务回调关闭分发标签页
 * 
 * @param id
 * @return
 */
function documentDispatchDeleteCallback(id,username){
	contentPanel.remove(id);
	showMsg("【"+username+"】关闭文档分发！");
}

// ---------------------------视频播放函数----------------------------------------

var videoPlayDlg;
var videoPlayUploadDlg;
var videoPlayToolBar;
var videoPlayExist = false;

/**
 * 视频管理
 */
function videoPlayWin(){
	videoPlayDlg = new Dialog();
	videoPlayDlg.Width = 950;
	videoPlayDlg.Height = 350;
	videoPlayDlg.Title = "视频管理";
	videoPlayDlg.URL = "../meeting?oper=videoList";
	videoPlayDlg.show();
}

/**
 * 在视频列表中上传
 * 
 * @return
 */
function videoPlayUpload(){
	videoPlayUploadDlg = new Dialog();
	videoPlayUploadDlg.Width = 550;
	videoPlayUploadDlg.Height = 210;
	videoPlayUploadDlg.Title = "上传视频";
	videoPlayUploadDlg.URL = "../meeting/videoplay_upload.jsp";
	videoPlayUploadDlg.show();
}


/**
 * 预览视频
 * 
 * @param videoId
 * @return
 */
function videoPlayPreview(videoId){
	diag = new Dialog();
    diag.Width = 950;
    diag.Height = 600;
    diag.Title = "预览音视频";
    diag.URL = "../meeting/videoplay_preview.jsp?videoid="+videoId;
    diag.show();
}

/**
 * 播放视频
 * 
 * @param videoId
 * @return
 */
function videoPlayFile(videoId){
	videoPlayDlg.close();
	VideoPlayService.VideoPlay(videoId);
}

/**
 * 关闭上传视频对话框
 * 
 * @return
 */
function videoPlayUploadClose(){
	videoPlayUploadDlg.close();
}

/**
 * 关闭对话框
 * 
 * @return
 */
function videoPlayDiagClose(){
	videoPlayUploadDlg.close();
	videoPlayDlg.close();
	Dialog.alert("上传成功！",function(){
		videoPlayWin();
	});
}

/**
 * 服务回调播放视频
 * 
 * @param filename
 * @param videoname
 * @param rtmpurl
 * @return
 */
function videoPlayCallback(filename,videoname){
	var ssHtml = new stringBuilder();
	ssHtml.append('<div id="VideoPlayMenuId"></div>');
	ssHtml.append('<div id="VideoPlayContentId" style="overflow:auto;background:#ffffff;"></div>');
	$("#VideoPlayDiv").html(ssHtml.toString());
	
	videoPlayToolBar = new Ext.Toolbar( {
		buttonAlign : 'center'
	});
	videoPlayToolBar.render("VideoPlayMenuId");
	videoPlayToolBar.addText("正在播放视频：《"+videoname+"》");
	videoPlayToolBar.doLayout();
	
	if(!videoPlayExist){
		videoPlayExist = true;
		if(userSid == hostSid){
			contentPanel.add( {
				id : "videoPlayId",
				title : '视频播放',
				iconCls : 'documentDispatch_menu',
				closable : true,
				contentEl : "VideoPlayDiv"
			});
		}else{
			contentPanel.add( {
				id : "videoPlayId",
				title : '视频播放',
				iconCls : 'documentDispatch_menu',
				contentEl : "VideoPlayDiv"
			});
		}
	}
	contentPanel.setActiveTab("videoPlayId");
	
	var imgWidth = contentPanel.getWidth();
	var imgHeight = contentPanel.getHeight()-55;
	
	var flashvars = {'file':filename,'streamer':RED5_OFLADEMO,'autostart':'true'};
    var params = {'allowfullscreen':'true','allowscriptaccess':'always','wmode':'opaque'};
    var attributes = {};
    swfobject.embedSWF("../js/jwplayer/pl.swf", "VideoPlayContentId", imgWidth, imgHeight,
    	 "9.0.0", false, flashvars, params, attributes);
}

/**
 * 关闭
 * 
 * @param videoId
 * @return
 */
function videoPlayDelete(videoId){
	VideoPlayService.videoRemove(videoId);
}

/**
 * 服务回调关闭分发标签页
 * 
 * @param id
 * @return
 */
function videoRemoveCallback(id,username){
	contentPanel.remove(id);
	showMsg("【"+username+"】关闭视频！");
}

// ---------------------------视频共享函数----------------------------------------

var videoState_OK = "<img src='../images/netmeeting/videoShare_camera.png' border='0' align='absmiddle'/>&nbsp;";
var videoState_None = "";

/**
 * SWF回调获得rtmp的uri
 * 
 * @return
 */
function swfServerURI() {
	return RED5_SOSAMPLE;
}

/**
 * SWF调用获得当前用户id
 * 
 * @return
 */
function swfCurrentUid(){
	return userSid;
}

/**
 * SWF调用判断是否为主持人
 * 
 * @return
 */
function swfIsHost(){
	if(userSid == hostSid){
		return 'y';
	} else {
		return 'n';
	}
}

/**
 * SWF调用初始化flash
 * 
 * @param status
 * @param message
 * @return
 */
function swfInitVideoPanel(camstatus, micstatus) {
	if (camstatus == -1) {
		Dialog.alert("您的机器尚未安装摄像头设备！");
	}
	if (micstatus == -1){
		Dialog.alert("您的机器尚未安装麦克风设备！");
	}
	VideoShareService.swfInitVideoPanel(camstatus,micstatus);
}

/**
 * 服务器回调 获得当前会议中有摄像头的用户
 * 
 * @param data
 * @return
 */
function swfInitVideoPanelCallback(data){
	var vuserArray = [];
	vuserArray.push( {
		"label" : "关闭此窗口视频",
		"data" : "0"
	});
	var json = JSON.parse(data);
	for ( var i = 0; i < json.length; i++) {
		var sessionid = json[i]["sessionid"];
		var username = json[i]["username"];
		vuserArray.push( {
			"label" : username,
			"data" : sessionid
		});
	}
    var swf = swfobject.getObjectById("videoshare_3");
    if(swf){
    	swf.vsVideoUserList(vuserArray);
    }
}

/**
 * SWF回调发布视频流
 * 
 * @param sessionid
 * @param videopanelId
 * @return
 */
function swfPublishVideo(sessionid,videopanelId){
	VideoShareService.videoSharePublishVideo(sessionid, videopanelId);
	$("#video_"+sessionid).html(videoState_OK);
}

/**
 * SWF回调选择视频的结果
 * 
 * @param camchoice
 * @param publishid
 * @param publishedName
 * @param panelid
 * @return
 */
function swfCameraChoiceCallback(camchoice, publishid, publishedName, panelid){
	if (camchoice == 0) {// 用户不同意使用视频
		showMsg("您必须选择【允许】使用摄像头");
		if (hostSid != publishid) {
			VideoShareService.showMsg(meetingId,hostSid,publishedName
					+ "拒绝使用摄像头");
		}
		var swf = swfobject.getObjectById("videoshare_3");
		if(swf){
			swf.vsPublishVideoShowPrivacy(panelid);
		}
		Dialog.alert("请在右侧的《视频会议》窗口中，选择\"允许\"，然后点击\"关闭\"<br/>");
		$("#video_"+publishid).html(audioState_None);
	} else if (camchoice == 1) {// 用户同意使用视频
		VideoShareService.videoShareComsumeVideo(publishid, panelid);
	} 
}

/**
 * SWF回调同意接受打开摄像头
 * 
 * @param sessionid
 * @return
 */
function swfVideoAgree(sessionid,videopanelId){
	VideoShareService.swfVideoAgree(sessionid,videopanelId);
}

/**
 * SWF回调关闭其他
 * 
 * @param videopanelId
 * @return
 */
function swfCloseVideo(sessionid,videopanelId){
	VideoShareService.videoShareClose(sessionid,videopanelId);
}

/**
 * 视频共享切换
 * 
 * @return
 */
function videoShareSwitch(){
	var vpanel = Ext.getCmp("east_panel");
	if (vpanel.isVisible()) {
		Dialog.confirm("是否确认关闭视频会议？",function(){
			VideoShareService.videoShareSwitch();
		});
	} else {
		VideoShareService.videoShareSwitch();
	}
}

/**
 * 服务端回调视频共享
 * 
 * @return
 */
function videoShareSwitchCallback(){
	var vpanel = Ext.getCmp("east_panel");
	if (!vpanel.isVisible()) {
		vpanel.setVisible(true);
		Ext.getCmp("viewport").doLayout();
		if(userSid == hostSid){
			Ext.getCmp('videoShareMenuItem').setText("关闭视频共享");
			Ext.getCmp('videoShareMenuItem').setIconClass("videoShare_remove"); 
		}
		videoShareInit();
	} else {
		vpanel.setVisible(false);
		Ext.getCmp("viewport").doLayout();
		if(userSid == hostSid){
			Ext.getCmp('videoShareMenuItem').setText("开启视频共享");
			Ext.getCmp('videoShareMenuItem').setIconClass("videoShare_add");
		}
		$("#RightDiv").html("");
		
		for(var i=0;i<userlistArray.length;i++){
			var sessionid = userlistArray[i]["sessionid"];
			$("#video_"+sessionid).html(videoState_None);
		}
		
		contentTabPanelWidthUpdate();
	}
}

/**
 * 视频共享
 * 
 * @return
 */
function videoShareInit(){
	var flashHtml = new stringBuilder();
	flashHtml.append("<object id='videoshare_3' width='230' height='510' type='application/x-shockwave-flash' data='../videoshare_3.swf'>");
	flashHtml.append("<param name='movie' value='../videoshare_3.swf'/>");
	flashHtml.append("<param name='allowScriptAccess' value='always'/>");
	flashHtml.append("<param name='allowFullScreen' value='true' />");
	flashHtml.append("<param name='wmode' value='opaque'/>");
	flashHtml.append("</object>");
	$("#RightDiv").html(flashHtml.toString());
	VideoShareService.videoShareInitVideoPanel();
	contentTabPanelWidthUpdate();
}

/**
 * 服务回调接受视频流，调用本地SWF接口
 * 
 * @param sessionid
 * @param username
 * @param panelId
 * @return
 */
function videoShareComsumeVideoCallback(sessionid, username, panelId){
	var swf = swfobject.getObjectById("videoshare_3");
    if(swf){
    	swf.vsConsumeVideo(panelId, sessionid, username);
    }
    $("#video_"+sessionid).html(videoState_OK);
}

/**
 * 服务回调发布视频流，调用本地SWF接口
 * 
 * @return
 */
function videoSharePublishVideoCallback(sessionid, username, panelId){
	var swf = swfobject.getObjectById("videoshare_3");
    if(swf){
    	swf.vsPublishVideo(panelId, sessionid, username);
    }
    $("#video_"+sessionid).html(videoState_OK);
}

/**
 * 服务回调关闭视频流
 * 
 * @param panelId
 * @return
 */
function videoShareCloseCallback(sessionid,panelId){
	var swf = swfobject.getObjectById("videoshare_3");
    if(swf){
    	swf.vsCloseOthers(panelId);
    }
    $("#video_"+sessionid).html(audioState_None);
}

/**
 * 发布视频前关闭视频流
 * 
 * @param oriPanelId
 * @param sessionId
 * @param newPanelId
 * @return
 */
function videoShareCloseBeforePublishCallback(oriPanelId,sessionId,newPanelId){
	var swf = swfobject.getObjectById("videoshare_3");
    if(swf){
    	swf.vsCloseBeforePublish(oriPanelId,newPanelId,sessionId);
    }
}


// ---------------------------音频共享函数----------------------------------------

var globalAudioDlg;
var audioState_Speak = "<img src='../images/netmeeting/audioShare_Status_on.png' border='0' align='absmiddle'/>&nbsp;";
var audioState_Muted = "<img src='../images/netmeeting/audioShare_Status_off.png' border='0' align='absmiddle'/>&nbsp;";
var audioState_None = "";

/**
 * SWF回调检测声卡状态
 * 
 * @return
 */
function swfInitAudioPanel(micstatus){
	// alert(micstatus);
}

/**
 * SWF回调获得当前用户名
 * 
 * @return
 */
function swfCurrentName(){
	return username;
}

/**
 * SWF回调，初始加载接收语音
 * 
 * @return
 */
function swfAudioConsumeInit(){
	AudioShareService.audioShareInitComsumeAudio();
}

/**
 * SWF回调，静音
 * 
 * @return
 */
function swfAudioMuted(publishId){
	AudioShareService.audioShareMute(publishId);
}

/**
 * SWF回调，取消静音
 * 
 * @return
 */
function swfAudioOk(publishId){
	AudioShareService.audioShareMuteCacel(publishId);
}

/**
 * SWF回调，已经选择接受语音
 * 
 * @return
 */
function swfAudioAgree(publishId){
	AudioShareService.audioSharePublishAgree(publishId);
}

/**
 * SWF回调选择音频的结果
 * 
 * @param micchoice
 * @param publishid
 * @param publishedName
 * @param panelid
 * @return
 */
function swfMicphoneChoiceCallback(micchoice, publishid,publishedName){
	if (micchoice == 0) {// 用户不同意使用音频
		showMsg("您必须选择【允许】使用音频");
		if (hostSid != publishid) {
			AudioShareService.showMsg(meetingId,hostSid,publishedName
					+ "拒绝使用音频");
		}
		var swf = swfobject.getObjectById("audioshare");
		if(swf){
			swf.publishAudioShowPrivacy();
		}
		Dialog.alert("请在左侧的《语音会议》窗口中，选择\"允许\"，然后点击\"关闭\"<br/>");
		$("#audio_"+publishid).html(audioState_None);
	} else if (micchoice == 1) {// 用户同意使用音频
		AudioShareService.audioShareComsumeAudio(publishid,function(){
			AudioShareService.audioSharePublishAgree(publishid);
		});
	} else if (micchoice == -1) {
	}
}

/**
 * 音频会议切换
 * 
 * @return
 */
function audioShareSwitch(){
	var apanel = Ext.getCmp("left_audio_panel");
	if (apanel.isVisible()) {
		Dialog.confirm("是否确认关闭音频会议？",function(){
			AudioShareService.audioShareInit(false);
		});
	} else {
		AudioShareService.audioShareInit(true);
	}
}

/**
 * 关闭语音共享
 * 
 * @return
 */
function audioShareOffCallback(){
	if(isChatWinShow){
		Ext.getCmp("leftSouthWrapId").setHeight(0);
	}else{
		Ext.getCmp("leftSouthWrapId").setHeight(250);
	}
	Ext.getCmp("left_audio_panel").setVisible(false);
	Ext.getCmp("west_panel").doLayout();
	if(userSid == hostSid){
		Ext.getCmp('audioShareMenuItem').setText("开启音频共享");
		Ext.getCmp('audioShareMenuItem').setIconClass("audioShare_add"); 
	}
	$("#AudioDiv").html("");
	
	for(var i=0;i<userlistArray.length;i++){
		var sessionid = userlistArray[i]["sessionid"];
		$("#audio_"+sessionid).html(audioState_None);
	}
}

/**
 * 开启语音共享
 * 
 * @return
 */
function audioShareOnCallback(){
	if(isChatWinShow){
		Ext.getCmp("leftSouthWrapId").setHeight(210);
	}else{
		Ext.getCmp("leftSouthWrapId").setHeight(455);
	}
	Ext.getCmp("left_audio_panel").setVisible(true);
	Ext.getCmp("west_panel").doLayout();
	if(userSid == hostSid){
		Ext.getCmp('audioShareMenuItem').setText("关闭音频共享");
		Ext.getCmp('audioShareMenuItem').setIconClass("audioShare_del");
	}
	audioShareInitSwf();
}

/**
 * 服务器回调 初始化音频共享面板
 * 
 * @return
 */
function audioShareInitSwf(){
	var flashHtml = new stringBuilder();
	flashHtml.append("<object id='audioshare' width='240' height='180' type='application/x-shockwave-flash' data='../audioshare.swf'>");
	flashHtml.append("<param name='movie' value='../audioshare.swf'/>");
	flashHtml.append("<param name='allowScriptAccess' value='always'/>");
	flashHtml.append("<param name='allowFullScreen' value='true' />");
	flashHtml.append("<param name='wmode' value='opaque'/>");
	flashHtml.append("</object>");
	$("#AudioDiv").html(flashHtml.toString());
}

/**
 * 服务器回调接收音频流
 * 
 * @param publishId
 * @param publishName
 * @return
 */
function audioShareComsumeAudioCallback(publishId,publishName){
	if(publishId != userSid){
		var swf = swfobject.getObjectById("audioshare");
		if(swf){
			swf.vsConsumeAudio(publishId);
		}
	}
}

/**
 * 用户列表选择开启语音
 * 
 * @param publishId
 * @return
 */
function audioSharePublish(publishId){
	AudioShareService.audioSharePublish(publishId);
}

/**
 * 服务器回调发布音频流
 * 
 * @param publishId
 * @return
 */
function audioSharePublishCallback(publishId){
	if(publishId == userSid){
		showMsg("您必须选择\"接受\"！");
		var swf = swfobject.getObjectById("audioshare");
		if(swf){
			swf.vsPublishAudio();
		}
	}
}

/**
 * 服务器回调同意发布音频流
 * 
 * @param publishId
 * @return
 */
function audioSharePublishAgreeCallback(publishId){
	if(publishId != userSid){
		var swf = swfobject.getObjectById("audioshare");
		if(swf){
			swf.vsConsumeAudio(publishId);
		}
	}
	$("#audio_"+publishId).html(audioState_Speak);
}

/**
 * 关闭音频流
 * 
 * @param publishId
 * @return
 */
function audioShareClose(publishId){
	AudioShareService.audioShareClose(publishId);
}

/**
 * 服务器回调关闭音频流
 * 
 * @param publishId
 * @return
 */
function audioShareCloseCallback(publishId){
	$("#audio_"+publishId).html("");
	
	if(publishId == userSid){
		var swf = swfobject.getObjectById("audioshare");
		if(swf){
			swf.closePublishAudios();
		}
	}
}

/**
 * 服务器回调，静音，更新用户列表状态
 * 
 * @return
 */
function audioShareMuteCallback(publishId){
	$("#audio_"+publishId).html(audioState_Muted);
}

/**
 * 服务器回调，取消静音，更新用户列表状态
 * 
 * @return
 */
function audioShareMuteCacelCallback(publishId){
	$("#audio_"+publishId).html(audioState_Speak);
}

/**
 * 弹出对话框，选择授权语音的人员
 * 
 * @return
 */
function audioShareWin(){
	globalAudioDlg = new Dialog();
	globalAudioDlg.Width = 350;
	globalAudioDlg.Height = 250;
	globalAudioDlg.Title = "语音会议设置";
	globalAudioDlg.URL = "../meeting/audio_setting.jsp?meetingId="+meetingId;
	globalAudioDlg.show();
}

/**
 * 开启某人的语音
 * 
 * @param sid
 * @return
 */
function audioShareWinStart(sid){
	globalAudioDlg.close();
	audioSharePublish(sid);
}

/**
 * 关闭某人的语音
 * 
 * @param sid
 * @return
 */
function audioShareWinStop(sid){
	globalAudioDlg.close();
	audioShareClose(sid);
}



// ---------------------------会议相关----------------------------------------

var meetingDlg;

/**
 * 会议设置
 * 
 * @return
 */
function meetingSetting(){
	meetingDlg = new Dialog();
	meetingDlg.Width = 350;
	meetingDlg.Height = 250;
	meetingDlg.Title = "会议设置";
	meetingDlg.URL = "../meeting/meeting_setting.jsp?meetingId="+meetingId;
	meetingDlg.show();
}


/**
 * 服务器回调，会议时间到
 * 
 * @param msg
 * @return
 */
function meetingTimeup(msg) {
	Dialog.alert(msg, function() {
		window.onbeforeunload = {};
		window.onunload = {};
		if (hostSid == userSid) {
			window.location = "../manage/index.jsp";
		} else {
			window.location = "../login.jsp";
		}
	});
}


/**
 * 离开会议之前
 * 
 * @return
 */
window.onbeforeunload = function() {
	var n = window.event.screenX - window.screenLeft;
	var b = n > document.documentElement.scrollWidth - 20;
	if (b && window.event.clientY < 0 || window.event.altKey) {
		event.returnValue = "是否确认退出会议？";
	}
}

/**
 * 离开会议
 * 
 * @return
 */
window.onunload = function() {
	window.location = "../meeting?oper=leave";
}


// ---------------------------中间标签页----------------------------------------


/**
 * 标签页关闭前调用
 * 
 * @param ct
 * @param cmp
 * @return
 */
function contentTabClose(ct, cmp){
	var cmpId = cmp.id;
	var id = cmpId.split("_")[1];
	if(userSid == hostSid){
		// 清除当前锁定的标签(如果存在)
		IndexService.contentTabUnlock();
		
		// 关闭标签页后相关处理
		if(cmpId.startWith(documentPrefix)){
			DocumentService.documentDelete(id);
		} else if(cmpId.startWith(whiteboardPrefix)){
			WhiteBoardService.whiteboardDelete(id);
		} else if(cmpId.startWith(ScreenSharePrefix)){
			screenShareShutdown(id);
		} else if(cmpId.startWith("documentDispatch")){
			documentDispatchDelete("documentDispatch");
		} else if(cmpId.startWith("videoPlayId")){
			videoPlayDelete("videoPlayId");
		}
	}
	if(cmpId.startWith(ScreenControlPrefix)){
		screenControlShutdown(id);
	}
	return true;
}

/**
 * 标签页切换时调用
 * 
 * @param ct
 * @param cmp
 * @return
 */
function contentTabChage(ct,cmp){
	if(userSid == hostSid){
		var cmpId = cmp.id;
		if(cmpId != "meetingInfoId")
			IndexService.contentTabChanged(cmpId);
	}
}

/**
 * 设置激活的标签
 * 
 * @param id
 * @return
 */
function contentTabChangeCallback(id){
	contentPanel.setActiveTab(id);
	
	contentTabPanelWidthUpdate();
}

/**
 * 更新中间区域的面板
 * 
 * @param flag
 * @return
 */
function contentTabPanelWidthUpdate(){
	var cmpId = contentPanel.getActiveTab().getId();
	var id = cmpId.split("_")[1];
	var contentId;
	if(cmpId.startWith(documentPrefix)){
		contentId = documentContentPrefix+id;
	} else if(cmpId.startWith(whiteboardPrefix)){
		contentId = whiteboardContentPrefix+id;
	} else if(cmpId.startWith(ScreenSharePrefix)){
		contentId = ScreenShareContentPrefix+id;
	} else if(cmpId.startWith(ScreenControlPrefix)){
		contentId = ScreenControlContentPrefix+id;
	} else if(cmpId.startWith("documentDispatch")){
		contentId="documentDispatch";
	} else if(cmpId.startWith("videoPlayId")){
		contentId="videoPlayId";
	}
	var cWidth = contentPanel.getWidth();
	$("#"+contentId).css("width",cWidth);
}

/**
 * 服务器回调锁定标签
 * 
 * @param id
 * @return
 */
function contentTabLockCallback(id){
	tabLockId = id;
}



// ---------------------------页面加载完毕----------------------------------------
Ext.onReady(function() {
	var loadingMask = Ext.get('loading-mask');
	var loading = Ext.get('loading');
	loading.fadeOut( {
		duration : 0.2,
		remove : true
	});
	loadingMask.setOpacity(0.9);
	loadingMask.shift( {
		xy : loading.getXY(),
		width : loading.getWidth(),
		height : loading.getHeight(),
		remove : true,
		duration : 1,
		opacity : 0.1,
		easing : 'bounceOut'
	});
	Ext.extmsg.init();
	Ext.QuickTips.init();

	viewport = new Ext.Viewport( {
		id : 'viewport',
		layout : 'border',
		items : [ topPanel, leftPanel, contentPanel, rightPanel, bottomPanel ]
	});
	
	/**
	 * sdf
	 */
	initChatShow();
	initChatMenu();
	initChatInput();

	dwr.engine.setActiveReverseAjax(true);
	IndexService.startService(function() {
		UserListService.startService();
		ChatService.startService();
		DocumentService.startService();
		WhiteBoardService.startService();
		DesktopShareService.startService();
		DesktopControlService.startService();
		VideoPlayService.startService();
		VideoShareService.startService();
		AudioShareService.startService();
	});
	
	correctPNG();
	window.status="网络会议";
	
	if(config_handup == '1'){
		Ext.getCmp('meetinghandsupId').enable();
	}else{
		Ext.getCmp('meetinghandsupId').disable();
	}
	
	if(config_desktopcontrol != '1'){
		if(userSid != hostSid){
			Ext.getCmp('desktopControlMenuItem').disable();
		}		
	}
});