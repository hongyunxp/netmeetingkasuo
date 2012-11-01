//********************************菜单栏以及工具栏初始化*********************************//
var menutoolbar;

var docDataArray = [];
var pageCombo;
var pComboDataSource;

var HANDUP = 1;
var AUDIO = 2;
var VIDEO = 3;

/**
 * 初始化页面head
 * 
 * @return
 */
function initHeader() {
	var html = new stringBuilder();
	html.append('<table width="100%" border="0" cellspacing="0">');
	html.append('<tr style="background-image:url(../images/toolbar/bg.gif);">');
	html.append('<td height="29px" width="75%">');
	html
			.append('<img alt="" src="../images/elearn_logo.png" border="0" align="absmiddle">');
	html.append('</td>');
	html.append('<td width="30%" align="right">');
	html.append('<div id="menusdiv">');
	html.append('</div>');
	html.append('</td>');
	html.append('</tr>');
	html.append('</table>');
	$("#TopDiv").attr("innerHTML", html.toString());
}

var docShareMenuItem = {
	text : '共享文档',
	iconCls : 'doc_share', // <-- icon
	listeners : {
		'click' : function() {
			documentManagement();
		}
	}
};

var docDistributeMenuItem = {
	text : '分发文档',
	id : 'documentDispatch',
	iconCls : 'documentDispatch_menu',
	listeners : {
		'click' : function() {
			documentdispatchWin();
		}
	}
};

var desktopShareMenuItem = {
	id : 'desktopShareMenuItem',
	text : '共享桌面',
	iconCls : 'screenShare_monitor', // <-- icon
	listeners : {
		'click' : function() {
			screenShareInit();
			Ext.getCmp('desktopShareMenuItem').disable();
		}
	}
};

var videoPlayMenuItem = {
	text : '播放视频',
	iconCls : 'videoShare_film', // <-- icon
	listeners : {
		'click' : function() {
			videoPlayWin();
		}
	}
};

var audioShareMenuItem = {
	id : 'audioShareMenuItem',
	text : '开启音频共享',
	iconCls : 'audioShare_add', // <-- icon
	listeners : {
		'click' : function() {
			audioShareSwitch();
		}
	}
};

var videoShareMenuItem = {
	id : 'videoShareMenuItem',
	text : '开启视频共享',
	iconCls : 'videoShare_add', // <-- icon
	listeners : {
		'click' : function() {
			videoShareSwitch();
		}
	}
};

var operMenu = new Ext.menu.Menu( {
	id : 'mainMenu',
	style : {
		overflow : 'visible'
	},
	items : [ docShareMenuItem, docDistributeMenuItem, desktopShareMenuItem,
			videoPlayMenuItem, audioShareMenuItem, videoShareMenuItem ]
});

var settingsItem = {
	text : '会议设置',
	iconCls : 'meeting_config', // <-- icon
	listeners : {
		'click' : function() {
			meetingSetting();
		}
	}
};

var leaveMenuItem = {
	text : '退出会议',
	iconCls : 'meeting_logout', // <-- icon
	listeners : {
		'click' : function() {
			meetingLeave();
		}
	}
};

var endMenuItem = {
	text : '结束会议',
	iconCls : 'meeting_exit', // <-- icon
	listeners : {
		'click' : function() {
			Dialog.alert("结束会议");
		}
	}
};

var meetingMenu;
if (hostSid == userSid) {
	meetingMenu = new Ext.menu.Menu( {
		id : 'meetingMenu',
		style : {
			overflow : 'visible'
		},
		items : [ settingsItem, leaveMenuItem, endMenuItem ]
	});
} else {
	meetingMenu = new Ext.menu.Menu( {
		id : 'meetingMenu',
		style : {
			overflow : 'visible'
		},
		items : [ leaveMenuItem ]
	});
}

var userPlace = '<img src="../images/netmeeting/netmeeting_user.png" border="0" align="absmiddle"/>&nbsp;' + username
if (userSid == hostSid) {
	userPlace = '<img src="../images/netmeeting/netmeeting_admin.png" border="0" align="absmiddle"/>&nbsp;' + username
}

Ext.onReady(function() {
	initHeader();
	menutoolbar = new Ext.Toolbar( {
		border : false,
		buttonAlign : 'right'
	});
	menutoolbar.render('menusdiv');
	menutoolbar.addText(userPlace);
	menutoolbar.add("-");
	menutoolbar.add( {
		text : '刷新',
		iconCls : 'netmeeting_refresh',
		listeners : {
			'click' : function() {
				Dialog.confirm("是否确认刷新整个页面？", function() {
					window.location = "../meeting/index.jsp";
				});
			}
		}
	});
	if (userSid == hostSid) {
		menutoolbar.add("-");
		menutoolbar.add( {
			text : '新建文档',
			iconCls : 'doc_new',
			listeners : {
				'click' : function() {
					documentUpload();
				}
			}
		});
		menutoolbar.add( {
			text : '新建白板',
			iconCls : 'whiteboard_new',
			listeners : {
				'click' : function() {
					whiteBoardInit('0');
				}
			}
		});
	}
	menutoolbar.add( {
		id : 'desktopControlMenuItem',
		text : '远程协助',
		iconCls : 'desktopControl_menu',
		listeners : {
			'click' : function() {
				desktopControlDialog();
			}
		}
	});
	menutoolbar.add( {
		id : 'meetinghandsupId',
		text : '举手',
		iconCls : 'netmeeting_handup',
		enableToggle : true,
		toggleHandler : function(b, state) {
			if (state) {
				UserListService.userListState(userSid, HANDUP);
			} else {
				UserListService.userListState(userSid, HANDUP);
			}
		}
	});
	menutoolbar.add("-");
	if (userSid == hostSid) {
		menutoolbar.add( {
			id : 'tabLockId',
			text : '解除锁定标签',
			tooltip : '点击锁定标签',
			enableToggle : true,
			iconCls : 'netmeeting_unlock',
			toggleHandler : function(b, state) {
				if (state) {
					Ext.getCmp('tabLockId').setIconClass("netmeeting_lock");
					Ext.getCmp('tabLockId').setTooltip('点击解除锁定标签');
					Ext.getCmp('tabLockId').setText('锁定标签');
					IndexService.contentTabLock(contentPanel.getActiveTab()
							.getId());
				} else {
					Ext.getCmp('tabLockId').setIconClass("netmeeting_unlock");
					Ext.getCmp('tabLockId').setTooltip('点击锁定标签');
					Ext.getCmp('tabLockId').setText('解除锁定标签');
					IndexService.contentTabUnlock();
				}
			}
		});
		menutoolbar.add("-");
		menutoolbar.add( {
			text : '操作',
			iconCls : 'netmeeting_operation', // <-- icon
			menu : operMenu
		});
	}
	menutoolbar.add( {
		text : '会议',
		iconCls : 'netmeeting_meeting', // <-- icon
		menu : meetingMenu
	});
	menutoolbar.doLayout();

});

// ********************************菜单/工具栏操作函数*********************************//

/**
 * 用户点击操作按钮回调函数
 * 
 * @return
 */
function userListStateCallback(sessionid, username, type, flag) {
	// alert("sessionid: " + sessionid + ",username: " + username + ", type: "
	// + type + ", flag: " + flag);
	var html = "";
	switch (type) {
	case HANDUP:
		if (!flag)
			break;
		if (hostSid == userSid) {
			html += "<a href='#' onclick='userHandupForcedown(\""
					+ sessionid
					+ "\")' title='隐藏【"
					+ username
					+ "】的举手请求'>"
					+ "<img border='0' align='absmiddle' src='../images/netmeeting/netmeeting_wavehand.gif'/>&nbsp;"
					+ "</a>";
		} else {
			html += "<img border='0' align='absmiddle' src='../images/netmeeting/netmeeting_wavehand.gif' alt='【"
					+ username + "】举手申请发言'/>&nbsp;";
		}
		break;
	case AUDIO:

		break;
	case VIDEO:

		break;
	}
	var userlistId = "handup_" + sessionid;
	var element = document.getElementById(userlistId);
	if (element)
		element.innerHTML = html;
}
