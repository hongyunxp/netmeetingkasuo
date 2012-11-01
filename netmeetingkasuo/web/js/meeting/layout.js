//*************************************ҳ�沼��***************************************

var viewport;

/**
 * ����logo���Լ�ϵͳ�����˵�
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
 * ���-�Ϸ���Ա�б����
 */
var leftNorthPanel = {
	region : 'center',
	id : 'left_north_panel',
	border : false,
	contentEl : 'UserListDiv'
};

/**
 * ���-�м��������
 */
var leftAudioPanel
if (userSid == hostSid) {
	leftAudioPanel = {
		title : '��������',
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
		title : '��������',
		region : 'center',
		id : 'left_audio_panel',
		contentEl : 'AudioDiv',
		border : false,
		hidden : true,
		iconCls : 'audioShare'
	};
}

/**
 * ���-�·��������
 */
var leftSouthPanel = {
	title : '��������',
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
 * ������
 */
var leftPanel = {
	title : '�λ��б�',
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
			Dialog.alert("���»����б��������Ա�б�");
		}
	} ]
};

/**
 * ��Ҫ�������
 */
randId = getTime() + rand(100000);
var contentPanel = new Ext.TabPanel(
		{
			id : 'content_panel',
			title : '�������',
			region : 'center',
			contentEl : 'ContentDiv',
			deferredRender : false,
			enableTabScroll : true,
			activeTab : 0,
			resizeTabs : true,
			items : [ {
				id : 'meetingInfoId',
				title : '������Ϣ',
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
						if (!confirm("�Ƿ�ȷ�Ϲرմ�ҳ?")) {
							return false;
						}
						return contentTabClose(ct, cmp);
					} else if (cmpId.startWith(ScreenControlPrefix)) {
						if (globalSspId != "") {
							globalSspId = "";
							return true;
						}
						if (!confirm("�Ƿ�ȷ�Ϲرմ�ҳ?")) {
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
 * �Ҳ���Ƶ���
 */
var rightPanel;
if (userSid == hostSid) {
	rightPanel = {
		title : '��Ƶ����',
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
		title : '��Ƶ����',
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
 * �ײ����
 */
var bottomPanel = {
	region : 'south',
	id : 'south_panel',
	contentEl : 'BottomDiv',
	height : 20,
	margins : '0 0 0 0',
	border : false
};
