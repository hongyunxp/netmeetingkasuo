//*************************************页面布局***************************************

var viewport;

var topPanel = {
	region : 'north',
	id : 'north-panel',
	contentEl : 'TopDiv',
	height : 30,
	margins : '0 0 0 0',
	border : false
};

var leftPanel = {
			title : '网络会议',
			region : 'west',
			contentEl : 'LeftDiv',
			id : 'west-panel',
			split: true,
			iconCls : 'ext_menu_panel',
			width : 220,
			margins : '0 1 0 0'
		};

var contentPanel = {
	id : 'contentPanel',
	title : '网络会议',
	region : 'center',
	fitToFrame: true,                   
	html: '<iframe id="contentFrame" name="contentFrame" src="../manage/meeting?oper=meetingWelcome" frameborder="0" width="100%" height="100%"></iframe>'
};

var bottomPanel = {
	region : 'south',
	id : 'south-panel',
	contentEl : 'BottomDiv',
	height : 20,
	margins : '0 0 0 0',
	border : true
};
