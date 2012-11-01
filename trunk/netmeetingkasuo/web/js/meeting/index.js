// ---------------------------ȫ�ֲ�������----------------------------------------
var msghint = '<img border="0" src="../images/netmeeting/info.png" align="absmiddle"/>&nbsp;ϵͳ��ʾ';

var userlistArray = [];

/**
 * �����ı�ǩID
 */
var tabLockId = "";

/**
 * ��ʾ��Ϣ����
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
 * ���½ǵ�����ʾ��
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
 * �ж��Ƿ�ΪIE�����
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
 * ��ȡ�����ɫ
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
 * ����PNG��ʽ��ͼƬ
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
 * ����min��max֮��������
 * 
 * @param min
 * @param max
 * @return
 */
function gen_random(min, max){   
	return Math.floor(Math.random() * (max- min) + min);   
} 


// ---------------------------�û��б��������----------------------------------------
var isitemclicked = false;
var itemclickArr = new Array();
var selectedUser = "";
var sessionIdArr = new Array();

var USERCOLOR="";
/**
 * �ص�-��ʼ���û��б�
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

	// ��ʼ���û��б�˵�
	if (userSid == hostSid) {
		initHostUserListMenu();
	} else {
		initAttendUserListMenu();
	}
}

/**
 * �û��������
 */
function userlistMouseover(elid, sessionid) {
	elid.style.background = '#D6E3F2';
}

/**
 * �û�����Ƴ�
 */
function userlistMouseout(elid, sessionid) {
	if (!isitemclicked || itemclickArr[0].elid != elid) {
		elid.style.background = '#ffffff';
	}
}

/**
 * �û������
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
 * �������߲˵�
 * 
 * @return
 */
function initAttendUserListMenu(){
	var userListMenuObj = document.createElement("div");
	userListMenuObj.className = "contextMenu";
	userListMenuObj.id = "userListMenu";
	var menuList = new stringBuilder();
	menuList.append('<ul style="font-size:12px;">');
	menuList.append('<li id="item_1">Զ��Э��</li>');
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
						Dialog.alert("��ֹ�˲����������������Լ�Զ��Э����");
					} else {
						screenControlConfrim(userSid,viewerId);
					}
				}
			}
		});
	}
}

/**
 * ��������˲˵�
 * 
 * @return
 */
function initHostUserListMenu() {
	var userListMenuObj = document.createElement("div");
	userListMenuObj.className = "contextMenu";
	userListMenuObj.id = "userListMenu";
	var menuList = new stringBuilder();
	menuList.append('<ul style="font-size:12px;">');
	menuList.append('<li id="item_1">�߳�����</li>');
	menuList.append('<li id="item_2">Զ��Э��</li>');
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
						Dialog.alert("��ֹ�˲����������������Լ�Զ��Э����");
					} else {
						screenControlConfrim(userSid,viewerId);
					}
				}
			}
		});
	}
}

/**
 * ����˻ص����þ��ֹ���
 * 
 * @return
 */
function disableHandupCallback(flag){
	if(flag == "true"){
		showMsg("����Ա���������");
		Ext.getCmp('meetinghandsupId').enable();
	}else{
		showMsg("����Ա��ֹ�����");
		Ext.getCmp('meetinghandsupId').disable();
	}
}

// ---------------------------�����������----------------------------------------
var chatToolbar = null;
var chatDataArray = [ [ '1', '������' ] ];
var chatComboDataSource = null;
var chatSelectCombo = null;

var chatToolbar2 = null;
var chatDataArray2 = [ [ '1', '������' ] ];
var chatComboDataSource2 = null;
var chatSelectCombo2 = null;

var inputHintMsg = "�������֣���[�س�]����[����]��ť������Ϣ����ס[Shift+�س�]����";
var chatWin = null;
var isChatWinShow = false;

/**
 * ��ʼ��������ʾ�����Ϣ
 * 
 * @return
 */
function initChatShow() {
	var html = '<span style="color:gray;">��ܰ��ʾ�������ı���������������Ϣ~</span>';
	$("#ChatNorthDiv").attr("innerHTML", html);
}

/**
 * ��ʼ������˵�
 */
function initChatMenu() {
	chatToolbar = new Ext.Toolbar( {
		buttonAlign : 'left'
	});
	chatToolbar.render('ChatCenterDiv');
	
	// ������
	chatComboDataSource = new Ext.data.Store( {
		proxy : new Ext.data.MemoryProxy(chatDataArray), // ����Դ
		reader : new Ext.data.ArrayReader( {}, [ // ��ν���
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
		tooltip : 'Ц��',
		listeners : {
			'click' : function() {
				chatSmile();
			}
		}
	});
	chatToolbar.add( {
		iconCls : 'chat_remove',
		tooltip : '���������Ϣ',
		listeners : {
			'click' : function() {
				chatRemove();
			}
		}
	});
	if (hostSid == userSid) {
		chatToolbar.add( {
			iconCls : 'chat_removeAll',
			tooltip : '�������������Ϣ',
			listeners : {
				'click' : function() {
					chatRemoveAll();
				}
			}
		});
	}
	chatToolbar.add( {
		iconCls : 'chat_save',
		tooltip : '����������Ϣ',
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
 * ��ʼ������������Ϣ
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
 * �������ص������û������б���Ϣ
 * 
 * @param jsonArr
 * @return
 */
function chatInitSelectCallback(jsonArr) {
	chatDataArray = [ [ '1', '������' ] ];
	chatDataArray2 = [ [ '1', '������' ] ];
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
 * ��ʼ��������Ϣ
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
 * ����ʱ�����̵����¼�
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
 * ����Ц��
 * 
 * @return
 */
function chatSmile() {
	var html = new stringBuilder();
	html.append('<table border="0" width="100px">');
	html.append('<tr>');
	html
			.append('<td height="16px"><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':)' + '\')"><img border="0" src="../images/smile/smile.gif" alt="΢Ц :)"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':D' + '\')"><img border="0" src="../images/smile/open-mouthedSmile.gif" alt="��Ц :D"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ';)' + '\')"><img border="0" src="../images/smile/winkingSmile.gif" alt="գ�� ;)"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':-O' + '\')"><img border="0" src="../images/smile/surprisedSmile.gif" alt="���� :-O"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':P' + '\')"><img border="0" src="../images/smile/smileWithTongueOut.gif" alt="����Ц�� :P"/></a></td>');
	html.append('</tr>');
	html.append('<tr>');
	html
			.append('<td height="16px"><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':@' + '\')"><img border="0" src="../images/smile/angrySmile.gif" alt="���� :@"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':S' + '\')"><img border="0" src="../images/smile/confusedSmile.gif" alt="���� :S"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':$' + '\')"><img border="0" src="../images/smile/embarrassed-.gif" alt="���� :$"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':(' + '\')"><img border="0" src="../images/smile/sad.gif" alt="���� :("/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':&' + '\')"><img border="0" src="../images/smile/crying.gif" alt="�������� :&"/></a></td>');
	html.append('</tr>');
	html.append('<tr>');
	html
			.append('<td height="16px"><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':|' + '\')"><img border="0" src="../images/smile/Disappointed.gif" alt="ʧ�� :|"/></a></td>');
	html
			.append('<td><a href="javascript:void(0)" onclick="handleSmiles(\'' + ':-#' + '\')"><img border="0" src="../images/smile/donotTell.gif" alt="�������� :-#"/></a></td>');
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
 * ����ѡ��Ц���¼�
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
 * ɾ��������Ϣ
 * 
 * @return
 */
function chatRemove() {
	Dialog.confirm("���������Ϣ��", function() {
		ChatService.remove();
	});
}

/**
 * ɾ�����е�������Ϣ
 * 
 * @return
 */
function chatRemoveAll() {
	Dialog.confirm("��������˵�������Ϣ", function() {
		ChatService.removeAll();
	});
}

/**
 * �������ص�=���������Ϣ
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
 * ����������Ϣ
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
 * ����������Ϣ
 * 
 * @return
 */
function chatSend() {
	if(isChatWinShow){
		var txtareaId = document.getElementById("chatInput2");
		var color = $("#chatInput2").css("color");
		if (color != 'gray') {
			var receiver = chatSelectCombo2.getValue();
			var msg = dwr.util.getValue('chatInput2').trim(); // �����Ϣ����
			if (msg == "" || msg.length < 1) {
				showMsg("�����������Ϣ��");
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
			var msg = dwr.util.getValue('chatInput').trim(); // �����Ϣ����
			if (msg == "" || msg.length < 1) {
				showMsg("�����������Ϣ��");
			} else {
				ChatService.send(receiver, msg, function() {
					$("#chatInput").val("");
				});
			}
		}
	}
}

/**
 * ����������Ϣ
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
		msgHtml.append("<b>���</b>˵:");
	} else {
		msgHtml.append("<b>");
		msgHtml.append(receiverName);
		msgHtml.append("</b>˵:");
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
 * �л��������
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
			title: '<img src="../images/netmeeting/chat_menu.png" border="0" align="absmiddle"/>&nbsp;��������',
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
 * ��ʼ��������ʾ�����Ϣ
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
 * ��ʼ������˵�
 */
function initChatMenu2() {
	
	chatToolbar2 = new Ext.Toolbar( {
		buttonAlign : 'left'
	});
	chatToolbar2.render('ChatCenterDiv2');
	
	// ������
	chatComboDataSource2 = new Ext.data.Store( {
		proxy : new Ext.data.MemoryProxy(chatDataArray2), // ����Դ
		reader : new Ext.data.ArrayReader( {}, [ // ��ν���
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
		tooltip : 'Ц��',
		listeners : {
			'click' : function() {
				chatSmile();
			}
		}
	});
	chatToolbar2.add( {
		iconCls : 'chat_remove',
		tooltip : '���������Ϣ',
		listeners : {
			'click' : function() {
				chatRemove();
			}
		}
	});
	if (hostSid == userSid) {
		chatToolbar2.add( {
			iconCls : 'chat_removeAll',
			tooltip : '�������������Ϣ',
			listeners : {
				'click' : function() {
					chatRemoveAll();
				}
			}
		});
	}
	chatToolbar2.add( {
		iconCls : 'chat_save',
		tooltip : '����������Ϣ',
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
 * ��ʼ������������Ϣ
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


// ---------------------------�ĵ������������----------------------------------------
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
 * �ĵ�����װ廭ͼ����
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
 * �ĵ��ϴ�
 * 
 * @return
 */
function documentUpload() {
	docDiag = new Dialog();
	docDiag.Width = 550;
	docDiag.Height = 210;
	docDiag.Title = "�ϴ��ĵ�";
	docDiag.URL = "../meeting/document_upload.jsp";
	docDiag.MessageTitle = "<span style='font-size:13px;'>�ϴ��ĵ�</span>";
	docDiag.Message = "<span style='font-size:13px;'>�밴����ʾ���ĵ��ϴ�������ת���������ĵȴ���</span>";
	docDiag.show();
}

/**
 * �رնԻ���
 * 
 * @return
 */
function documentDiagClose() {
	docDiag.close();
}

/**
 * �ĵ���ʼ��
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
 * ��ʼ���ĵ��˵���
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
	// ҳ��������
	var docDataArray = [ [ '0/0', 'ȫ��' ] ];
	var docComboDataSource = new Ext.data.Store( {
		proxy : new Ext.data.MemoryProxy(docDataArray), // ����Դ
		reader : new Ext.data.ArrayReader( {}, [ // ��ν���
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

	// �Զ���ҳ������
	autoPlayStore = new Ext.data.ArrayStore({
        fields: ['id', 'name'],
        data : [
                ['5','5��'],
                ['10','10��'],
                ['15','15��'],
                ['20','20��'],
                ['25','25��'],
                ['30','30��']
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
			tooltip : '��һҳ',
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
			tooltip : '��һҳ',
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
				tooltip:'��ʼ�Զ���ҳ', 
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
		tooltip : '�Ŵ�',
		listeners : {
			'click' : function() {
				documentMenuZoomOut(fileid);
			}
		}
	});
	docToolbar.add(ExtBtnSpacer2);
	docToolbar.add( {
		iconCls : 'doc_zoomin',
		tooltip : '��С',
		listeners : {
			'click' : function() {
				documentMenuZoomIn(fileid);
			}
		}
	});
	docToolbar.add(ExtBtnSpacer2);
	docToolbar.add( {
		iconCls : 'doc_save',
		tooltip : '���ظ��ĵ�',
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
			tooltip : 'ֱ��',
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
			tooltip : '��ͷ��',
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
			tooltip : '����',
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
			tooltip : '��Բ',
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
			tooltip : '�ֱ�',
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
			tooltip : '�ı�',
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
				tooltip : '����',
				listeners : {
					'click' : function() {
						whiteBoardMenuUndo(fileid);
					}
				}
			});
			docToolbar.add(ExtBtnSpacer2);
			docToolbar.add( {
				iconCls : 'whiteboard_redo',
				tooltip : '����',
				listeners : {
					'click' : function() {
						whiteBoardMenuRedo(fileid);
					}
				}
			});
			docToolbar.add(ExtBtnSpacer2);
			docToolbar.add( {
				iconCls : 'whiteboard_trash',
				tooltip : 'ɾ������',
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
 * ��ʼ���ĵ�����
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
 * �����ĵ�
 * 
 * @return
 */
function documentPlay(fileid, filename, seq) {
	documentInit(fileid, filename);
	DocumentService.documentPlay(fileid, seq, function() {
		if(docDiag){
			documentDiagClose();
		}
		showMsg("��"+filename+"���ĵ����سɹ���");
	});
}

/**
 * �����ĵ�
 * 
 * @return
 */
function documentPlay2(fileid,seq) {
	DocumentService.documentPlay(fileid, seq);
}

/**
 * �������ص�ҳ���ʼ����
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
 * �������ص������ĵ�
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
 * ����ص���ʼ���ĵ�����ҳ��
 * 
 * @param fileid
 * @param pages
 * @return
 */
function documentPageSelect(fileid,pages){
	var docMenuPage = documentMenuPagePrefix+fileid;
	var docDataArray = [ [ '0/0', 'ȫ��' ] ];
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
 * �ĵ���ͼ
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
 * ��ȡͼƬ
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
	$("#"+docContentId).attr("innerHTML", "<span style='color:red'>�ĵ����ڼ�����......</span>");
}

/**
 * ͼƬ�������
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
		html = "<img class='highqual' alt='ͼƬ' id='"+docImgId+"' src='" + path
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
 * ������һҳ
 * 
 * @param fileid
 * @return
 */
function documentMenuPrePage(fileid){
	DocumentService.documentPre(fileid);
}

/**
 * �����ƶ�ҳ
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
 * ������һҳ
 * 
 * @param fileid
 * @return
 */
function documentMenuNextPage(fileid){
	DocumentService.documentNext(fileid);
}

/**
 * ��ʾ�����ĵ�ͼƬ
 * 
 * @return
 */
function documentShowAllCallback(fileid,fileCol){
	// �������·�ҳ
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
 * ��ʾ�ĵ�
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
 * �����Զ������ĵ���ʱ��
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
 * �Զ������ĵ�
 * 
 * @param fileid
 * @return
 */
function documentMenuAutoplay(fileid,value){
	DocumentService.documentPlay(fileid, 1);
	DocumentService.documentAutoPlay(fileid,value);
}

/**
 * �Ŵ��ĵ�
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
 * ��С�ĵ�
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
 * ��ʾ�ĵ��б�
 * 
 * @return
 */
function documentManagement(){
	docDiag = new Dialog();
	docDiag.Width = 950;
	docDiag.Height = 350;
	docDiag.Title = "�ĵ��б�";
	docDiag.URL = "../meeting?oper=documentList&action=share";
	docDiag.show();
}

/**
 * ���ĵ��б����ĵ��ϴ�
 * 
 * @return
 */
function documentManagementUpload(){
	docUploadDiag = new Dialog();
	docUploadDiag.Width = 550;
	docUploadDiag.Height = 210;
	docUploadDiag.Title = "�ϴ��ĵ�";
	docUploadDiag.URL = "../meeting/document_upload.jsp?oper=documentMgr";
	docUploadDiag.MessageTitle = "<span style='font-size:13px;'>�ϴ��ĵ�</span>";
	docUploadDiag.Message = "<span style='font-size:13px;'>�밴����ʾ���ĵ��ϴ�������ת���������ĵȴ���</span>";
	docUploadDiag.show();
}

/**
 * ���ĵ��б��йر��ϴ��ĵ��Ի���
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
 * �ر��ĵ�
 * 
 * @return
 */
function documentDeleteCallback(fileid){
	var docId = documentPrefix+fileid;
	contentPanel.remove(docId);
}

/**
 * �˵���ѡ�л�ͼ��ť�¼�
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
 * ���²˵�״̬
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
 * ��ȡ���µĲ˵�
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
 * ��ȡ��ͼ����
 * 
 * @param wbId
 * @return
 */
function docWhiteBoardDrawerObj(fileid){
	var drawO = docWbDrawMap[fileid];
	return drawO;
}

/**
 * ��ʼ��д��
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
 * ��꾭����״
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
 * ����Ƴ�
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
 * ��ͼ�����갴���¼�
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
		// ������
		o.setDraw(true);
		o.drawLineString = new stringBuilder();
	} else if (drawtype == DRAW_TEXT) {
		// д��
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
 * ��ͼ�������ƶ��¼�
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
 * ��ͼ�����굯��ʱ���¼�
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
 * ��ͼ������˫���¼�
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
 * �������ص�
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
 * �������ص���д����
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
 * �������ص�д��
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
 * ��ʼ��д��
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

// ---------------------------�װ干����----------------------------------------
var whiteboardDivPrefix = "whiteboardDiv_";
var whiteboardPrefix = "whiteboard_";
var whiteboardMenuPrefix = "whiteboardMenu_";
var whiteboardMenuFontSizePrefix = "whiteboardMenuFontSize_";
var whiteboardContentPrefix = "whiteboardContent_"
var whiteboardContentImgPrefix = "whiteboardContentImg_";

var whiteboardNamePrefix = "�װ�_";

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
 * �װ��ʼ��
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
 * ����˻ص���ʼ���װ�
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
 * ��ʼ���װ�˵���
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
	
	// �����С������
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
    		tooltip : '���������˰װ幦��',
    		enableToggle : true,
    		toggleHandler : function(b,state){
    			if(state){
    				WhiteBoardService.enableWhiteBoard(wbId,"true",function(){
    					Ext.getCmp(wbEnable).setIconClass("whiteboard_disable");
    					Ext.getCmp(wbEnable).setTooltip('�ر������˰װ幦��'); 
    				});
    			}else{
    				WhiteBoardService.enableWhiteBoard(wbId,"false",function(){
    					Ext.getCmp(wbEnable).setIconClass("whiteboard_enable");
    					Ext.getCmp(wbEnable).setTooltip('���������˰װ幦��');
    				});
    			}
    		}
    	});
    	wbToolbar.add( {
    		id : wbPointId,
    		iconCls : 'whiteboard_hand',
    		tooltip : 'ָ��',
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
		tooltip : 'ֱ��',
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
		tooltip : '��ͷ��',
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
		tooltip : '����',
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
		tooltip : '��Բ',
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
		tooltip : '�ֱ�',
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
		tooltip : '�ı�',
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
			tooltip : '����',
			listeners : {
				'click' : function() {
					whiteBoardMenuUndo(wbId);
				}
			}
		});
		wbToolbar.add(ExtBtnSpacer2);
		wbToolbar.add( {
			iconCls : 'whiteboard_redo',
			tooltip : '����',
			listeners : {
				'click' : function() {
					whiteBoardMenuRedo(wbId);
				}
			}
		});
		wbToolbar.add(ExtBtnSpacer2);
		wbToolbar.add( {
			iconCls : 'whiteboard_trash',
			tooltip : 'ɾ������',
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
 * ����/�رհװ�
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
		showMsg("�����˿��������İװ廭ͼ����");
	} else{
		drawObj.setDrawType("");
		Ext.getCmp(wbPolyLineId).disable();
		Ext.getCmp(wbLineId).disable();
		Ext.getCmp(wbArrowLineId).disable();
		Ext.getCmp(wbTextId).disable();
		Ext.getCmp(wbRectId).disable();
		Ext.getCmp(wbEllipseId).disable();
		showMsg("�����˽�ֹ�����İװ廭ͼ����");
		
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
 * �˵���ѡ�а�ť�¼�
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
 * ��ʼ���װ�����
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
 * ���²˵�״̬
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
 * ��ȡ���µĲ˵�
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
 * ��ȡ��ͼ����
 * 
 * @param wbId
 * @return
 */
function whiteBoardDrawerObj(wbId){
	var drawO = wbDrawMap[wbId];
	return drawO;
}

/**
 * ��ͼ�����갴���¼�
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
		// ������
		o.setDraw(true);
		o.drawLineString = new stringBuilder();
	} else if (drawtype == DRAW_TEXT) {
		// д��
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
 * ��ͼ�������ƶ��¼�
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
 * ��ͼ�����굯��ʱ���¼�
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
 * ��ͼ������˫���¼�
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
 * ����whiteBoardDrawer��
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
 * ����whiteBoardDrawer�ĳ�Ա����
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
		this.gc.setFont( "����", size, Font.BOLD ); 
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
 * ������ƶ�
 */
function whiteBoardPointerMove(x,y,pointerId){
	var pointerImg = document.getElementById(pointerId);
	pointerImg.style.left=x;
	pointerImg.style.top=y;
}

/**
 * �������ص�
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
 * ��ʼ��д��
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
 * �������ص�д��
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
 * �������ص���д����
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
 * ����
 * 
 * @param wbId
 * @return
 */
function whiteBoardMenuUndo(wbId){
	WhiteBoardService.drawUndo(wbId);
}

/**
 * ����
 * 
 * @param wbId
 * @return
 */
function whiteBoardMenuRedo(wbId){
	WhiteBoardService.drawRedo(wbId);
}

/**
 * ����װ�
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
 * �������ص�ɾ��ĳ���װ�
 * 
 * @param wbId
 * @return
 */
function whiteboardDeleteCallback(wbId){
	var whiteBoardId = whiteboardPrefix+wbId;
	contentPanel.remove(whiteBoardId);
}


// ---------------------------Զ�����湲������----------------------------------------

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
 * ��ʼ�����湲��
 */
function screenShareInit(){
	Dialog.confirm("���湲�������ء����湲����������Ƿ����أ�<br/>���غ�<br/>&nbsp;&nbsp;1.��ѹ���غ��ZIP�ļ�<br/>&nbsp;&nbsp;2.�������е�Ӧ�ó���",function(){
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
 * ��ʼ�����湲��
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
			title : '���湲��',
			iconCls : 'screenShare_monitor',
			closable : true,
			contentEl : ScreenShareDivId
		});
	}else{
		contentPanel.add( {
			id : ScreenShareTabId,
			title : '���湲��',
			iconCls : 'screenShare_monitor',
			contentEl : ScreenShareDivId
		});
	}
	contentPanel.setActiveTab(ScreenShareTabId);
	
	screenShareInitMenu(sspId);
}

/**
 * ��ʼ���˵�
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
		tooltip : '�˳�',
		listeners : {
			'click' : function() {
				Dialog.confirm("�Ƿ�ȷ���˳����湲��?",function(){
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
 * ��ʼ�����湲��body
 * 
 * @param sspId
 * @return
 */
function screenShareInitContent(sspId){
	var ScreenShareContentId = ScreenShareContentPrefix + sspId;
	var html = new stringBuilder();
	html.append("<table align='center' style='margin-top:150px;width:350px;font-size:13px;padding:20px;'>");
	html.append("<tr>");
	html.append("<td style='font-weight:bold;height:25px;'>���Ѿ��ɹ�������\"���湲�������.zip\"<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' style='height:25px;'>1.���Ƚ�ѹ��zip�ļ�<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' style='height:25px;'>2.�������е�exe����<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' id='sspTd_"+sspId+"' style='height:25px;'>ʣ��ʱ�䣺<span id='sspSpan_"+sspId+"'></span><td>");
	html.append("</tr>");
	html.append("</table>");
	document.getElementById(ScreenShareContentId).innerHTML = html.toString();
}

/**
 * ��������û�����exeʣ��ʱ��
 * 
 * @param count
 * @return
 */
function screenShareServerWaitStatusCallback(sspId,count){
	var spanId = "sspSpan_"+sspId;
	var spanObj = document.getElementById(spanId);
	if(spanObj)
		spanObj.innerHTML = count+"��";
}

/**
 * �û��Ѿ�������exe����������ʹ������������湲��
 * 
 * @return
 */
function screenShareServerConnectedCallback(sspId,flag){
	var tdId = "sspTd_"+sspId;
	var tdObj = document.getElementById(tdId);
	if(tdObj){
		if(flag){
			tdObj.innerHTML = "<span style='color:green;margin-top:100px;font-size:13px;font-weight:bold;'>�����������ڹ�����...</span>";
			DesktopShareService.screenShareNotifyViewers(sspId);
		}else{
			tdObj.innerHTML = "<span style='color:red;margin-top:100px;font-size:13px;font-weight:bold;'>�������湲���������ʱ...</span>";
		}
	}
}

/**
 * ����˻ص���֪ͨ�ͻ��˹ۿ����湲��
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
				appletHtml.append('�����������֧��Java�������밲װJava����ʱ������');
			appletHtml.append('</noembed>');
		appletHtml.append('</embed>');
	appletHtml.append('</object>');
	document.getElementById(ScreenShareContentId).innerHTML = appletHtml.toString();
}

/**
 * ����˻ص���֪ͨ�ͻ��˹ۿ����湲��
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
 * �����˹ر����湲��
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
 * ����˻ص��رտͻ������湲���ǩҳ
 * 
 * @param sspId
 * @return
 */
function screenShareShutdownCallback(sspId){
	showMsg("�����˹ر������湲��");
	var ScreenShareTabId = ScreenShareTabPrefix + sspId;
	contentPanel.remove(ScreenShareTabId);
}



// ---------------------------Զ������Э��������----------------------------------------

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
 * Զ��Э�����
 * 
 * @return
 */
function desktopControlDialog(){
	globalSspDlg = new Dialog();
	globalSspDlg.Width = 350;
	globalSspDlg.Height = 150;
	globalSspDlg.Title = "ѡ��Զ��Э������Ա";
	globalSspDlg.URL = "../meeting/screen_control.jsp?meetingId="+meetingId+"&sessionId="+userSid;
	globalSspDlg.MessageTitle = "<span style='font-size:13px;'>��ѡ����ҪЭ������Ա</span>";
	globalSspDlg.Message = "<span style='font-size:13px;'>�����Ե����û��б��Ҽ�ѡ��\"Զ��Э��\"</span>";
	globalSspDlg.show();
}

/**
 * �رնԻ���
 * 
 * @return
 */
function sspDialogClose(){
	globalSspDlg.close();
	Ext.getCmp('desktopControlMenuItem').enable();
}

/**
 * �رնԻ���
 * 
 * @return
 */
function sspDialogUserSel(sessionid,userSelName){
	globalSspDlg.close();
	Ext.getCmp('desktopControlMenuItem').disable();
	screenControlConfrim(userSid,sessionid);
	Ext.getCmp('desktopControlMenuItem').disable();
	showMsg("�ѷ���"+userSelName+"����Զ��Э������");
}

/**
 * ���������ȷ���Ƿ�ͬ��Զ�̿���
 * 
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlConfrim(serverId,viewerId){
	DesktopControlService.screenControlConfrim(serverId,viewerId);
}

/**
 * ����˻ص� �ͻ����Ƿ�ͬ��Զ��Э��
 * 
 * @param username
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlConfrimCallback(username,serverId,viewerId){
	Dialog.confirm("��"+username+"��������Զ��Э�����Ƿ�ͬ�⣿",function(){
		DesktopControlService.screenControlConfrimResult(serverId,viewerId,true);
	},function(){
		DesktopControlService.screenControlConfrimResult(serverId,viewerId,false);
	})
}

/**
 * ��������ͬ��Զ��Э��
 * 
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlConfrimResultOKCallback(serverId, viewerId){
	screenControlInit(serverId,viewerId);
}

/**
 * �������˲�ͬ��Զ��Э��
 * 
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlConfrimResultNOCallback(username,serverId, viewerId){
	Dialog.confirm("��"+username+"����ͬ��Զ��Э�����Ƿ��ٴ����룿",function(){
		DesktopControlService.screenControlConfrim(serverId,viewerId);
	},function(){
		Ext.getCmp('desktopControlMenuItem').enable();
	});
}

/**
 * ��ʼ��Զ��Э��
 * 
 * @param serverId
 * @param viewerId
 * @return
 */
function screenControlInit(serverId,viewerId){
	Dialog.confirm("Զ��Э�������ء�Զ��Э�����������Ƿ����أ�<br/>���غ�<br/>&nbsp;&nbsp;1.��ѹ���غ��ZIP�ļ�<br/>&nbsp;&nbsp;2.�������е�Ӧ�ó���",function(){
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
 * ��ʼ��Զ��Э��
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
			title : 'Զ��Э��',
			iconCls : 'desktopControl_menu',
			closable : true,
			contentEl : ScreenControlDivId
		});
	}else{
		contentPanel.add( {
			id : ScreenControlTabId,
			title : 'Զ��Э��',
			iconCls : 'desktopControl_menu',
			contentEl : ScreenControlDivId
		});
	}
	contentPanel.setActiveTab(ScreenControlTabId);
	
	screenControlInitMenu(sspId);
}

/**
 * ��ʼ���˵�
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
		tooltip : '�˳�',
		listeners : {
			'click' : function() {
				Dialog.confirm("�Ƿ�ȷ���˳�Զ��Э��?",function(){
					screenControlShutdown(sspId);
				});
			}
		}
	});
	
	ssToolBar.doLayout();
}

/**
 * ��ʼ��Զ��Э��body
 * 
 * @param sspId
 * @return
 */
function screenControlInitContent(sspId){
	var ScreenControlContentId = ScreenControlContentPrefix + sspId;
	var html = new stringBuilder();
	html.append("<table align='center' style='margin-top:150px;width:350px;font-size:13px;padding:20px;'>");
	html.append("<tr>");
	html.append("<td style='font-weight:bold;height:25px;'>���Ѿ��ɹ�������\"Զ��Э��������.zip\"<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' style='height:25px;'>1.���Ƚ�ѹ��zip�ļ�<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' style='height:25px;'>2.�������е�exe����<td>");
	html.append("</tr>");
	html.append("<tr>");
	html.append("<td align='left' id='sspTd_"+sspId+"' style='height:25px;'>ʣ��ʱ�䣺<span id='sspSpan_"+sspId+"'></span><td>");
	html.append("</tr>");
	html.append("</table>");
	document.getElementById(ScreenControlContentId).innerHTML = html.toString();
}

/**
 * ��������û�����exeʣ��ʱ��
 * 
 * @param count
 * @return
 */
function screenControlServerWaitStatusCallback(sspId,count){
	var spanId = "sspSpan_"+sspId;
	var spanObj = document.getElementById(spanId);
	if(spanObj)
		spanObj.innerHTML = count+"��";
}

/**
 * �û��Ѿ�������exe����������ʹ���������Զ��Э��
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
			tdObj.innerHTML = "<span style='color:green;margin-top:100px;font-size:13px;font-weight:bold;'>�����������ڿ�����...</span>";
			DesktopControlService.screenControlNotifyViewers(sspId,serverId,viewerId);
		}else{
			tdObj.innerHTML = "<span style='color:red;margin-top:100px;font-size:13px;font-weight:bold;'>����Զ��Э����������ʱ...</span>";
		}
	}
}

/**
 * ����˻ص���֪ͨ�ͻ��˹ۿ�Զ��Э��
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
				appletHtml.append('�����������֧��Java�������밲װJava����ʱ������');
			appletHtml.append('</noembed>');
		appletHtml.append('</embed>');
	appletHtml.append('</object>');
	document.getElementById(ScreenControlContentId).innerHTML = appletHtml.toString();
}

/**
 * ����˻ص���֪ͨ�ͻ��˹ۿ�Զ��Э��
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
 * �����˹ر�Զ��Э��
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
 * ����˻ص��رտͻ���Զ��Э����ǩҳ
 * 
 * @param sspId
 * @return
 */
function screenControlShutdownCallback(sspId){
	showMsg("�����˹ر���Զ��Э����");
	var ScreenControlTabId = ScreenControlTabPrefix + sspId;
	globalSspId = sspId;
	contentPanel.remove(ScreenControlTabId);
}


// ---------------------------�ĵ��ַ���������----------------------------------------
// �ĵ��ַ�����������λ��߷ַ��ĵ������Դ��ĵ�������ѡ�񣬿���ѡ��ַ��û�������ѡ��ַ��ļ���ʽ

var documentDispatchDlg;
var documentDispatchUploadDlg;

/**
 * �ĵ��ַ�
 */
function documentdispatchWin(){
	documentDispatchDlg = new Dialog();
	documentDispatchDlg.Width = 950;
	documentDispatchDlg.Height = 350;
	documentDispatchDlg.Title = "�ַ��ĵ�����";
	documentDispatchDlg.URL = "../meeting?oper=documentList&action=dispatch";
	documentDispatchDlg.show();
}

/**
 * ���ĵ��б����ĵ��ϴ�
 * 
 * @return
 */
function documentdispatchUpload(){
	documentDispatchUploadDlg = new Dialog();
	documentDispatchUploadDlg.Width = 550;
	documentDispatchUploadDlg.Height = 210;
	documentDispatchUploadDlg.Title = "�ϴ��ĵ�";
	documentDispatchUploadDlg.URL = "../meeting/filedispatch_upload.jsp";
	documentDispatchUploadDlg.show();
}

/**
 * �رնԻ���
 * 
 * @return
 */
function documentDispatchDiagClose(){
	documentDispatchUploadDlg.close();
	documentDispatchDlg.close();
	Dialog.alert("�ϴ��ɹ���",function(){
		documentdispatchWin();
	});
}

/**
 * �ĵ��ַ����ȼ���
 * 
 * @param fileid
 * @param filename
 * @return
 */
function documentDispatchStatus(fileid,filename){
	documentDispatchDlg.close();
	contentPanel.add( {
		id : "documentDispatch",
		title : '�ĵ��ַ�',
		iconCls : 'documentDispatch_menu',
		closable : true,
		contentEl : "DocumentDisDiv"
	});
	contentPanel.setActiveTab("documentDispatch");
	var url = "../meeting?oper=getMeetingUserList";
	$.post(url,{meetingId:meetingId,userSid:userSid},function(data){
        var json = JSON.parse(data);
        if(json == "" || json.length <1){
        	$("#DocumentDisDiv").html("û�������Ա��");
        	return;
        }
        var html = new stringBuilder();
    	html.append("<div align='center' style='margin-top:150px;margin-bottom:20px;'>");
    	html.append("�ļ���"+filename+"���ϴ���ϣ����ڷַ���...");
    	html.append("</div>");
    	
    	html.append("<table cellspacing='0' cellpadding='0' width='60%' align='center' class='dispatchTable'>");
    	html.append("<tr class='dispatchTrHead'>");
    	html.append("<td width='20%' align='center' height='25px'>");
    	html.append("����")
    	html.append("</td>");
    	html.append("<td align='center'>");
    	html.append("����״̬")
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
 * �ַ��ĵ�
 * 
 * @param username
 * @param fileid
 * @param filename
 * @return
 */
function documentDispatchCallback(username,fileid,filename){
	contentPanel.add( {
		id : "documentDispatch",
		title : '�ĵ��ַ�',
		iconCls : 'documentDispatch_menu',
		contentEl : "DocumentDisDiv"
	});
	contentPanel.setActiveTab("documentDispatch");
	
	var html = new stringBuilder();
	html.append("<div align='center' style='margin-top:150px;margin-bottom:20px;'>");
	html.append("��"+username+"���ַ��ļ���"+filename+"���������Ƿ����...");
	html.append("<div style='margin-top:25px;'>");
	html.append("<input type='button' id='dispatchAccept' name='dispatchAccept' value=' ��  �� ' onclick='documentDispatchAccept(\""+fileid+"\")'/>");
	html.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
	html.append("<input type='button' id='dispatchReject' name='dispatchReject' value=' ��  �� ' onclick='documentDispatchReject(\""+fileid+"\")'/>");
	html.append("</div>");
	html.append("</div>");
	
    $("#DocumentDisDiv").html(html.toString());
}

/**
 * ͬ������ļ�
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
 * �ܾ������ļ�
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
 * ����ص��ַ�
 * 
 * @param sessionId
 * @param flag
 * @return
 */
function documentDispatchStatusCallback(sessionId,flag){
	var tdId = "dispatchTd_"+sessionId;
	if(flag){
		$("#"+tdId).html("<span style='color:green'>������</span>");
	}else{
		$("#"+tdId).html("<span style='color:red'>�Ѿܾ�</span>");
	}
}

/**
 * �رշַ���ǩ
 * 
 * @param id
 * @return
 */
function documentDispatchDelete(id){
	DocumentDispatchService.documentDispatchDelete(id);
}

/**
 * ����ص��رշַ���ǩҳ
 * 
 * @param id
 * @return
 */
function documentDispatchDeleteCallback(id,username){
	contentPanel.remove(id);
	showMsg("��"+username+"���ر��ĵ��ַ���");
}

// ---------------------------��Ƶ���ź���----------------------------------------

var videoPlayDlg;
var videoPlayUploadDlg;
var videoPlayToolBar;
var videoPlayExist = false;

/**
 * ��Ƶ����
 */
function videoPlayWin(){
	videoPlayDlg = new Dialog();
	videoPlayDlg.Width = 950;
	videoPlayDlg.Height = 350;
	videoPlayDlg.Title = "��Ƶ����";
	videoPlayDlg.URL = "../meeting?oper=videoList";
	videoPlayDlg.show();
}

/**
 * ����Ƶ�б����ϴ�
 * 
 * @return
 */
function videoPlayUpload(){
	videoPlayUploadDlg = new Dialog();
	videoPlayUploadDlg.Width = 550;
	videoPlayUploadDlg.Height = 210;
	videoPlayUploadDlg.Title = "�ϴ���Ƶ";
	videoPlayUploadDlg.URL = "../meeting/videoplay_upload.jsp";
	videoPlayUploadDlg.show();
}


/**
 * Ԥ����Ƶ
 * 
 * @param videoId
 * @return
 */
function videoPlayPreview(videoId){
	diag = new Dialog();
    diag.Width = 950;
    diag.Height = 600;
    diag.Title = "Ԥ������Ƶ";
    diag.URL = "../meeting/videoplay_preview.jsp?videoid="+videoId;
    diag.show();
}

/**
 * ������Ƶ
 * 
 * @param videoId
 * @return
 */
function videoPlayFile(videoId){
	videoPlayDlg.close();
	VideoPlayService.VideoPlay(videoId);
}

/**
 * �ر��ϴ���Ƶ�Ի���
 * 
 * @return
 */
function videoPlayUploadClose(){
	videoPlayUploadDlg.close();
}

/**
 * �رնԻ���
 * 
 * @return
 */
function videoPlayDiagClose(){
	videoPlayUploadDlg.close();
	videoPlayDlg.close();
	Dialog.alert("�ϴ��ɹ���",function(){
		videoPlayWin();
	});
}

/**
 * ����ص�������Ƶ
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
	videoPlayToolBar.addText("���ڲ�����Ƶ����"+videoname+"��");
	videoPlayToolBar.doLayout();
	
	if(!videoPlayExist){
		videoPlayExist = true;
		if(userSid == hostSid){
			contentPanel.add( {
				id : "videoPlayId",
				title : '��Ƶ����',
				iconCls : 'documentDispatch_menu',
				closable : true,
				contentEl : "VideoPlayDiv"
			});
		}else{
			contentPanel.add( {
				id : "videoPlayId",
				title : '��Ƶ����',
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
 * �ر�
 * 
 * @param videoId
 * @return
 */
function videoPlayDelete(videoId){
	VideoPlayService.videoRemove(videoId);
}

/**
 * ����ص��رշַ���ǩҳ
 * 
 * @param id
 * @return
 */
function videoRemoveCallback(id,username){
	contentPanel.remove(id);
	showMsg("��"+username+"���ر���Ƶ��");
}

// ---------------------------��Ƶ������----------------------------------------

var videoState_OK = "<img src='../images/netmeeting/videoShare_camera.png' border='0' align='absmiddle'/>&nbsp;";
var videoState_None = "";

/**
 * SWF�ص����rtmp��uri
 * 
 * @return
 */
function swfServerURI() {
	return RED5_SOSAMPLE;
}

/**
 * SWF���û�õ�ǰ�û�id
 * 
 * @return
 */
function swfCurrentUid(){
	return userSid;
}

/**
 * SWF�����ж��Ƿ�Ϊ������
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
 * SWF���ó�ʼ��flash
 * 
 * @param status
 * @param message
 * @return
 */
function swfInitVideoPanel(camstatus, micstatus) {
	if (camstatus == -1) {
		Dialog.alert("���Ļ�����δ��װ����ͷ�豸��");
	}
	if (micstatus == -1){
		Dialog.alert("���Ļ�����δ��װ��˷��豸��");
	}
	VideoShareService.swfInitVideoPanel(camstatus,micstatus);
}

/**
 * �������ص� ��õ�ǰ������������ͷ���û�
 * 
 * @param data
 * @return
 */
function swfInitVideoPanelCallback(data){
	var vuserArray = [];
	vuserArray.push( {
		"label" : "�رմ˴�����Ƶ",
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
 * SWF�ص�������Ƶ��
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
 * SWF�ص�ѡ����Ƶ�Ľ��
 * 
 * @param camchoice
 * @param publishid
 * @param publishedName
 * @param panelid
 * @return
 */
function swfCameraChoiceCallback(camchoice, publishid, publishedName, panelid){
	if (camchoice == 0) {// �û���ͬ��ʹ����Ƶ
		showMsg("������ѡ������ʹ������ͷ");
		if (hostSid != publishid) {
			VideoShareService.showMsg(meetingId,hostSid,publishedName
					+ "�ܾ�ʹ������ͷ");
		}
		var swf = swfobject.getObjectById("videoshare_3");
		if(swf){
			swf.vsPublishVideoShowPrivacy(panelid);
		}
		Dialog.alert("�����Ҳ�ġ���Ƶ���顷�����У�ѡ��\"����\"��Ȼ����\"�ر�\"<br/>");
		$("#video_"+publishid).html(audioState_None);
	} else if (camchoice == 1) {// �û�ͬ��ʹ����Ƶ
		VideoShareService.videoShareComsumeVideo(publishid, panelid);
	} 
}

/**
 * SWF�ص�ͬ����ܴ�����ͷ
 * 
 * @param sessionid
 * @return
 */
function swfVideoAgree(sessionid,videopanelId){
	VideoShareService.swfVideoAgree(sessionid,videopanelId);
}

/**
 * SWF�ص��ر�����
 * 
 * @param videopanelId
 * @return
 */
function swfCloseVideo(sessionid,videopanelId){
	VideoShareService.videoShareClose(sessionid,videopanelId);
}

/**
 * ��Ƶ�����л�
 * 
 * @return
 */
function videoShareSwitch(){
	var vpanel = Ext.getCmp("east_panel");
	if (vpanel.isVisible()) {
		Dialog.confirm("�Ƿ�ȷ�Ϲر���Ƶ���飿",function(){
			VideoShareService.videoShareSwitch();
		});
	} else {
		VideoShareService.videoShareSwitch();
	}
}

/**
 * ����˻ص���Ƶ����
 * 
 * @return
 */
function videoShareSwitchCallback(){
	var vpanel = Ext.getCmp("east_panel");
	if (!vpanel.isVisible()) {
		vpanel.setVisible(true);
		Ext.getCmp("viewport").doLayout();
		if(userSid == hostSid){
			Ext.getCmp('videoShareMenuItem').setText("�ر���Ƶ����");
			Ext.getCmp('videoShareMenuItem').setIconClass("videoShare_remove"); 
		}
		videoShareInit();
	} else {
		vpanel.setVisible(false);
		Ext.getCmp("viewport").doLayout();
		if(userSid == hostSid){
			Ext.getCmp('videoShareMenuItem').setText("������Ƶ����");
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
 * ��Ƶ����
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
 * ����ص�������Ƶ�������ñ���SWF�ӿ�
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
 * ����ص�������Ƶ�������ñ���SWF�ӿ�
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
 * ����ص��ر���Ƶ��
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
 * ������Ƶǰ�ر���Ƶ��
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


// ---------------------------��Ƶ������----------------------------------------

var globalAudioDlg;
var audioState_Speak = "<img src='../images/netmeeting/audioShare_Status_on.png' border='0' align='absmiddle'/>&nbsp;";
var audioState_Muted = "<img src='../images/netmeeting/audioShare_Status_off.png' border='0' align='absmiddle'/>&nbsp;";
var audioState_None = "";

/**
 * SWF�ص��������״̬
 * 
 * @return
 */
function swfInitAudioPanel(micstatus){
	// alert(micstatus);
}

/**
 * SWF�ص���õ�ǰ�û���
 * 
 * @return
 */
function swfCurrentName(){
	return username;
}

/**
 * SWF�ص�����ʼ���ؽ�������
 * 
 * @return
 */
function swfAudioConsumeInit(){
	AudioShareService.audioShareInitComsumeAudio();
}

/**
 * SWF�ص�������
 * 
 * @return
 */
function swfAudioMuted(publishId){
	AudioShareService.audioShareMute(publishId);
}

/**
 * SWF�ص���ȡ������
 * 
 * @return
 */
function swfAudioOk(publishId){
	AudioShareService.audioShareMuteCacel(publishId);
}

/**
 * SWF�ص����Ѿ�ѡ���������
 * 
 * @return
 */
function swfAudioAgree(publishId){
	AudioShareService.audioSharePublishAgree(publishId);
}

/**
 * SWF�ص�ѡ����Ƶ�Ľ��
 * 
 * @param micchoice
 * @param publishid
 * @param publishedName
 * @param panelid
 * @return
 */
function swfMicphoneChoiceCallback(micchoice, publishid,publishedName){
	if (micchoice == 0) {// �û���ͬ��ʹ����Ƶ
		showMsg("������ѡ������ʹ����Ƶ");
		if (hostSid != publishid) {
			AudioShareService.showMsg(meetingId,hostSid,publishedName
					+ "�ܾ�ʹ����Ƶ");
		}
		var swf = swfobject.getObjectById("audioshare");
		if(swf){
			swf.publishAudioShowPrivacy();
		}
		Dialog.alert("�������ġ��������顷�����У�ѡ��\"����\"��Ȼ����\"�ر�\"<br/>");
		$("#audio_"+publishid).html(audioState_None);
	} else if (micchoice == 1) {// �û�ͬ��ʹ����Ƶ
		AudioShareService.audioShareComsumeAudio(publishid,function(){
			AudioShareService.audioSharePublishAgree(publishid);
		});
	} else if (micchoice == -1) {
	}
}

/**
 * ��Ƶ�����л�
 * 
 * @return
 */
function audioShareSwitch(){
	var apanel = Ext.getCmp("left_audio_panel");
	if (apanel.isVisible()) {
		Dialog.confirm("�Ƿ�ȷ�Ϲر���Ƶ���飿",function(){
			AudioShareService.audioShareInit(false);
		});
	} else {
		AudioShareService.audioShareInit(true);
	}
}

/**
 * �ر���������
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
		Ext.getCmp('audioShareMenuItem').setText("������Ƶ����");
		Ext.getCmp('audioShareMenuItem').setIconClass("audioShare_add"); 
	}
	$("#AudioDiv").html("");
	
	for(var i=0;i<userlistArray.length;i++){
		var sessionid = userlistArray[i]["sessionid"];
		$("#audio_"+sessionid).html(audioState_None);
	}
}

/**
 * ������������
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
		Ext.getCmp('audioShareMenuItem').setText("�ر���Ƶ����");
		Ext.getCmp('audioShareMenuItem').setIconClass("audioShare_del");
	}
	audioShareInitSwf();
}

/**
 * �������ص� ��ʼ����Ƶ�������
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
 * �������ص�������Ƶ��
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
 * �û��б�ѡ��������
 * 
 * @param publishId
 * @return
 */
function audioSharePublish(publishId){
	AudioShareService.audioSharePublish(publishId);
}

/**
 * �������ص�������Ƶ��
 * 
 * @param publishId
 * @return
 */
function audioSharePublishCallback(publishId){
	if(publishId == userSid){
		showMsg("������ѡ��\"����\"��");
		var swf = swfobject.getObjectById("audioshare");
		if(swf){
			swf.vsPublishAudio();
		}
	}
}

/**
 * �������ص�ͬ�ⷢ����Ƶ��
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
 * �ر���Ƶ��
 * 
 * @param publishId
 * @return
 */
function audioShareClose(publishId){
	AudioShareService.audioShareClose(publishId);
}

/**
 * �������ص��ر���Ƶ��
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
 * �������ص��������������û��б�״̬
 * 
 * @return
 */
function audioShareMuteCallback(publishId){
	$("#audio_"+publishId).html(audioState_Muted);
}

/**
 * �������ص���ȡ�������������û��б�״̬
 * 
 * @return
 */
function audioShareMuteCacelCallback(publishId){
	$("#audio_"+publishId).html(audioState_Speak);
}

/**
 * �����Ի���ѡ����Ȩ��������Ա
 * 
 * @return
 */
function audioShareWin(){
	globalAudioDlg = new Dialog();
	globalAudioDlg.Width = 350;
	globalAudioDlg.Height = 250;
	globalAudioDlg.Title = "������������";
	globalAudioDlg.URL = "../meeting/audio_setting.jsp?meetingId="+meetingId;
	globalAudioDlg.show();
}

/**
 * ����ĳ�˵�����
 * 
 * @param sid
 * @return
 */
function audioShareWinStart(sid){
	globalAudioDlg.close();
	audioSharePublish(sid);
}

/**
 * �ر�ĳ�˵�����
 * 
 * @param sid
 * @return
 */
function audioShareWinStop(sid){
	globalAudioDlg.close();
	audioShareClose(sid);
}



// ---------------------------�������----------------------------------------

var meetingDlg;

/**
 * ��������
 * 
 * @return
 */
function meetingSetting(){
	meetingDlg = new Dialog();
	meetingDlg.Width = 350;
	meetingDlg.Height = 250;
	meetingDlg.Title = "��������";
	meetingDlg.URL = "../meeting/meeting_setting.jsp?meetingId="+meetingId;
	meetingDlg.show();
}


/**
 * �������ص�������ʱ�䵽
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
 * �뿪����֮ǰ
 * 
 * @return
 */
window.onbeforeunload = function() {
	var n = window.event.screenX - window.screenLeft;
	var b = n > document.documentElement.scrollWidth - 20;
	if (b && window.event.clientY < 0 || window.event.altKey) {
		event.returnValue = "�Ƿ�ȷ���˳����飿";
	}
}

/**
 * �뿪����
 * 
 * @return
 */
window.onunload = function() {
	window.location = "../meeting?oper=leave";
}


// ---------------------------�м��ǩҳ----------------------------------------


/**
 * ��ǩҳ�ر�ǰ����
 * 
 * @param ct
 * @param cmp
 * @return
 */
function contentTabClose(ct, cmp){
	var cmpId = cmp.id;
	var id = cmpId.split("_")[1];
	if(userSid == hostSid){
		// �����ǰ�����ı�ǩ(�������)
		IndexService.contentTabUnlock();
		
		// �رձ�ǩҳ����ش���
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
 * ��ǩҳ�л�ʱ����
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
 * ���ü���ı�ǩ
 * 
 * @param id
 * @return
 */
function contentTabChangeCallback(id){
	contentPanel.setActiveTab(id);
	
	contentTabPanelWidthUpdate();
}

/**
 * �����м���������
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
 * �������ص�������ǩ
 * 
 * @param id
 * @return
 */
function contentTabLockCallback(id){
	tabLockId = id;
}



// ---------------------------ҳ��������----------------------------------------
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
	window.status="�������";
	
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