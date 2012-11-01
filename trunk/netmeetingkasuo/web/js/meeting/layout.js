//*************************************页面布局***************************************

var viewport;

/**
 * 顶部logo，以及系统操作菜单
 */
var topPanel = {
	region : 'north',
	id : 'north_panel',
	contentEl : 'TopDiv',
	height : 30,
	margins : '0 0 0 0',
	border : false
};

/**
 * 左侧-上方人员列表面板
 */
var leftNorthPanel = {
	region : 'center',
	id : 'left_north_panel',
	border : false,
	contentEl : 'UserListDiv'
};

/**
 * 左侧-中间语音面板
 */
var leftAudioPanel
if (userSid == hostSid) {
	leftAudioPanel = {
		title : '语音会议',
		region : 'center',
		id : 'left_audio_panel',
		contentEl : 'AudioDiv',
		border : false,
		hidden : true,
		iconCls : 'audioShare',
		tools : [ {
			id : 'gear',
			handler : function(event, toolEl, panel) {
				audioShareWin();
			}
		}, {
			id : 'close',
			handler : function(event, toolEl, panel) {
				audioShareSwitch();
			}
		} ]
	};
} else {
	leftAudioPanel = {
		title : '语音会议',
		region : 'center',
		id : 'left_audio_panel',
		contentEl : 'AudioDiv',
		border : false,
		hidden : true,
		iconCls : 'audioShare'
	};
}

/**
 * 左侧-下方聊天面板
 */
var leftSouthPanel = {
	title : '互动讨论',
	region : 'south',
	id : 'left_south_panel',
	contentEl : 'ChatDiv',
	height : 250,
	border : false,
	collapsible : true,
	animCollapse : false,
	iconCls : 'chat_menu',
	tools : [ {
		id : 'pin',
		handler : function(event, toolEl, panel) {
			if (Ext.getCmp("left_audio_panel").isVisible()) {
				Ext.getCmp('leftSouthWrapId').setHeight(205);
			} else {
				Ext.getCmp('leftSouthWrapId').setHeight(0);
			}
			Ext.getCmp('left_south_panel').setVisible(false);
			Ext.getCmp('west_panel').doLayout();

			chatPanelSwitch();
		}
	} ],
	listeners : {
		'beforecollapse' : function() {
			if (Ext.getCmp("left_audio_panel").isVisible()) {
				Ext.getCmp('leftSouthWrapId').setHeight(205);
			} else {
				Ext.getCmp('leftSouthWrapId').setHeight(30);
			}
			Ext.getCmp('west_panel').doLayout();
		},
		'beforeexpand' : function() {
			if (Ext.getCmp("left_audio_panel").isVisible()) {
				Ext.getCmp('leftSouthWrapId').setHeight(455);
			} else {
				Ext.getCmp('leftSouthWrapId').setHeight(250);
			}
			Ext.getCmp('west_panel').doLayout();
		}
	}
};

/**
 * 左侧面板
 */
var leftPanel = {
	title : '参会列表',
	region : 'west',
	contentEl : 'LeftDiv',
	id : 'west_panel',
	split : true,
	iconCls : 'userlist_menu',
	width : 250,
	layout : 'border',
	items : [ leftNorthPanel, {
		id : 'leftSouthWrapId',
		region : 'south',
		layout : 'border',
		height : 250,
		items : [ leftAudioPanel, leftSouthPanel ]
	} ],
	tools : [{
		id : 'refresh',
		handler : function(event, toolEl, panel) {
			Dialog.alert("更新会议列表和聊天人员列表");
		}
	} ]
};

/**
 * 主要内容面板
 */
randId = getTime() + rand(100000);
var contentPanel = new Ext.TabPanel(
		{
			id : 'content_panel',
			title : '网络会议',
			region : 'center',
			contentEl : 'ContentDiv',
			deferredRender : false,
			enableTabScroll : true,
			activeTab : 0,
			resizeTabs : true,
			items : [ {
				id : 'meetingInfoId',
				title : '会议信息',
				iconCls : 'netmeeting_info',
				autoScroll : true,
				fitToFrame : true,
				html : '<iframe id="contentFrame" name="contentFrame" src="meeting_info.jsp?time=' + randId + '" frameborder="0" width="100%" height="100%"></iframe>'
			} ],
			listeners : {
				'beforeremove' : function(ct, cmp) {
					var cmpId = cmp.id;
					if (userSid == hostSid && globalSspId == "") {
						if (cmp.id == 'fullscreen')
							return true;
						if (!confirm("是否确认关闭此页?")) {
							return false;
						}
						return contentTabClose(ct, cmp);
					} else if (cmpId.startWith(ScreenControlPrefix)) {
						if (globalSspId != "") {
							globalSspId = "";
							return true;
						}
						if (!confirm("是否确认关闭此页?")) {
							return false;
						}
						return contentTabClose(ct, cmp);
					}
				},
				'tabchange' : function(ct, cmp) {
					if (tabLockId != "") {
						contentPanel.setActiveTab(tabLockId);
					} else {
						contentTabChage(ct, cmp);
					}
				}
			}
		});

/**
 * 右侧视频面板
 */
var rightPanel;
if (userSid == hostSid) {
	rightPanel = {
		title : '视频会议',
		region : 'east',
		contentEl : 'RightDiv',
		id : 'east_panel',
		hidden : true,
		split : true,
		iconCls : 'videoShare_camera',
		width : 230,
		margins : '0 1 0 0',
		tools : [ {
			id : 'close',
			handler : function(event, toolEl, panel) {
				videoShareSwitch();
			}
		} ]
	};
} else {
	rightPanel = {
		title : '视频会议',
		region : 'east',
		contentEl : 'RightDiv',
		id : 'east_panel',
		hidden : true,
		split : true,
		iconCls : 'videoShare_camera',
		width : 230,
		margins : '0 1 0 0'
	};
}

/**
 * 底部面板
 */
var bottomPanel = {
	region : 'south',
	id : 'south_panel',
	contentEl : 'BottomDiv',
	height : 20,
	margins : '0 0 0 0',
	border : false
};
