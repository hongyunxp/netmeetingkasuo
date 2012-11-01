//********************************�˵����Լ���������ʼ��*********************************//
var menutoolbar;

var docDataArray = [];
var pageCombo;
var pComboDataSource;

var HANDUP = 1;
var AUDIO = 2;
var VIDEO = 3;

/**
 * ��ʼ��ҳ��head
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
	text : '�����ĵ�',
	iconCls : 'doc_share', // <-- icon
	listeners : {
		'click' : function() {
			documentManagement();
		}
	}
};

var docDistributeMenuItem = {
	text : '�ַ��ĵ�',
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
	text : '��������',
	iconCls : 'screenShare_monitor', // <-- icon
	listeners : {
		'click' : function() {
			screenShareInit();
			Ext.getCmp('desktopShareMenuItem').disable();
		}
	}
};

var videoPlayMenuItem = {
	text : '������Ƶ',
	iconCls : 'videoShare_film', // <-- icon
	listeners : {
		'click' : function() {
			videoPlayWin();
		}
	}
};

var audioShareMenuItem = {
	id : 'audioShareMenuItem',
	text : '������Ƶ����',
	iconCls : 'audioShare_add', // <-- icon
	listeners : {
		'click' : function() {
			audioShareSwitch();
		}
	}
};

var videoShareMenuItem = {
	id : 'videoShareMenuItem',
	text : '������Ƶ����',
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
	text : '��������',
	iconCls : 'meeting_config', // <-- icon
	listeners : {
		'click' : function() {
			meetingSetting();
		}
	}
};

var leaveMenuItem = {
	text : '�˳�����',
	iconCls : 'meeting_logout', // <-- icon
	listeners : {
		'click' : function() {
			meetingLeave();
		}
	}
};

var endMenuItem = {
	text : '��������',
	iconCls : 'meeting_exit', // <-- icon
	listeners : {
		'click' : function() {
			Dialog.alert("��������");
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
		text : 'ˢ��',
		iconCls : 'netmeeting_refresh',
		listeners : {
			'click' : function() {
				Dialog.confirm("�Ƿ�ȷ��ˢ������ҳ�棿", function() {
					window.location = "../meeting/index.jsp";
				});
			}
		}
	});
	if (userSid == hostSid) {
		menutoolbar.add("-");
		menutoolbar.add( {
			text : '�½��ĵ�',
			iconCls : 'doc_new',
			listeners : {
				'click' : function() {
					documentUpload();
				}
			}
		});
		menutoolbar.add( {
			text : '�½��װ�',
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
		text : 'Զ��Э��',
		iconCls : 'desktopControl_menu',
		listeners : {
			'click' : function() {
				desktopControlDialog();
			}
		}
	});
	menutoolbar.add( {
		id : 'meetinghandsupId',
		text : '����',
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
			text : '���������ǩ',
			tooltip : '���������ǩ',
			enableToggle : true,
			iconCls : 'netmeeting_unlock',
			toggleHandler : function(b, state) {
				if (state) {
					Ext.getCmp('tabLockId').setIconClass("netmeeting_lock");
					Ext.getCmp('tabLockId').setTooltip('������������ǩ');
					Ext.getCmp('tabLockId').setText('������ǩ');
					IndexService.contentTabLock(contentPanel.getActiveTab()
							.getId());
				} else {
					Ext.getCmp('tabLockId').setIconClass("netmeeting_unlock");
					Ext.getCmp('tabLockId').setTooltip('���������ǩ');
					Ext.getCmp('tabLockId').setText('���������ǩ');
					IndexService.contentTabUnlock();
				}
			}
		});
		menutoolbar.add("-");
		menutoolbar.add( {
			text : '����',
			iconCls : 'netmeeting_operation', // <-- icon
			menu : operMenu
		});
	}
	menutoolbar.add( {
		text : '����',
		iconCls : 'netmeeting_meeting', // <-- icon
		menu : meetingMenu
	});
	menutoolbar.doLayout();

});

// ********************************�˵�/��������������*********************************//

/**
 * �û����������ť�ص�����
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
					+ "\")' title='���ء�"
					+ username
					+ "���ľ�������'>"
					+ "<img border='0' align='absmiddle' src='../images/netmeeting/netmeeting_wavehand.gif'/>&nbsp;"
					+ "</a>";
		} else {
			html += "<img border='0' align='absmiddle' src='../images/netmeeting/netmeeting_wavehand.gif' alt='��"
					+ username + "���������뷢��'/>&nbsp;";
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
