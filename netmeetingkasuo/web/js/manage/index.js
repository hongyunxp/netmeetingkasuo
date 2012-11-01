Ext.onReady(function() {
	viewport = new Ext.Viewport( {
		layout : 'border',
		items : [ topPanel, leftPanel, contentPanel, bottomPanel ]
	});
});

var menuId = "";
var diag;

/**
 * 鼠标悬停在菜单上方
 * @param id
 * @return
 */
function menuOver(id) {
	$("#"+id).css("background","#FFE88C");
}

/**
 * 鼠标移出在菜单上方
 * @param id
 * @return
 */
function menuOut(id) {
	if(menuId != id)
		$("#"+id).css("background","#FFFFFF");
}

/**
 * 鼠标点击菜单
 * @param id
 * @return
 */
function menuClick(id) {
	if(menuId != ""){
		$("#"+menuId).css("background","#FFFFFF");
	}
	menuId = id;
	$("#"+menuId).css("background","#FFE88C");
	if(id == "menuUserManage"){
		toUserManagePage();
	}else if(id == "menuUserSetting"){
		toUseSettinigPage();
	}else if(id == "menuMeeting"){
		toMeetingManagePage();
	}else if(id == "menuDocument"){
		toDocumentManagePage();
	}else if(id == "menuVideo"){
		toVideoManagePage();
	}
}

/**
 * 跳转到视频管理页面
 * @return
 */
function toVideoManagePage(){
	Ext.getCmp('contentPanel').setTitle("视频管理","videoShare_film");
	toPage("../manage/video?oper=videoList");
}

/**
 * 跳转到文档管理页面
 * @return
 */
function toDocumentManagePage(){
	Ext.getCmp('contentPanel').setTitle("文档管理","doc_share");
	toPage("../manage/document?oper=docList");
}

/**
 * 跳转到用户列表页面
 * @return
 */
function toUserManagePage(){
	Ext.getCmp('contentPanel').setTitle("用户管理","ext_menu_user");
	toPage("../manage/user?oper=userList");
}

/**
 * 跳转到用户列表页面
 * @return
 */
function toUseSettinigPage(){
	Ext.getCmp('contentPanel').setTitle("个人设置","ext_menu_msetting");
	toPage("user_setting.jsp?usercode="+usercode);
}

/**
 * 跳转到会议管理界面
 * @return
 */
function toMeetingManagePage(){
	Ext.getCmp('contentPanel').setTitle("会议管理","ext_menu_meeting");
	toPage("../manage/meeting?oper=meetingList");
}

/**
 * 使用content跳转到页面URL
 * @param url
 * @return
 */
function toPage(url){
	$("#contentFrame").attr("src",url);
}


////////////////////////////用户管理/////////////////////////////
/**
 * 添加用户对话框
 * @return
 */
function userAdd(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 280;
    diag.Title = "添加用户";
    diag.URL = "user_add.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>添加用户</span>";
    diag.Message = "<span style='font-size:13px;'>请按照提示，详细填写下面的用户信息</span>";
    diag.show();
}

/**
 * 编辑用户对话框
 * @return
 */
function userEdit(usercode){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 260;
    diag.Title = "编辑用户";
    diag.URL = "user_edit.jsp?usercode="+usercode;
    diag.MessageTitle = "<span style='font-size:13px;'>编辑用户</span>";
    diag.Message = "<span style='font-size:13px;'>请按照提示，详细填写下面的用户信息</span>";
    diag.show();
}

/**
 * 查找用户
 * @return
 */
function userSearch(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 250;
    diag.Title = "添加用户";
    diag.URL = "user_search.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>查找用户</span>";
    diag.Message = "<span style='font-size:13px;'>请输入查询条件</span>";
    diag.show();
}

/**
 * 关闭对话框
 * @return
 */
function dialogUserClose(){
    diag.close();
    toUserManagePage();
}

/**
 * 关闭对话框
 * @return
 */
function dialogUserClose2(){
    diag.close();
}

////////////////////////////会议管理/////////////////////////////

/**
 * 添加会议
 */
function meetingAdd(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 320;
    diag.Title = "预约会议";
    diag.URL = "meeting_add.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>预约会议</span>";
    diag.Message = "<span style='font-size:13px;'>请按照提示，详细填写下面的会议信息</span>";
    diag.show();
}

/**
 * 编辑会议对话框
 * @return
 */
function meetingEdit(usercode,meetingid){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 320;
    diag.Title = "编辑会议";
    diag.URL = "meeting_edit.jsp?usercode="+usercode+"&meetingid="+meetingid;
    diag.MessageTitle = "<span style='font-size:13px;'>编辑会议</span>";
    diag.Message = "<span style='font-size:13px;'>请按照提示，详细填写下面的会议信息</span>";
    diag.show();
}

/**
 * 关闭对话框
 * @return
 */
function dialogMeetingClose(){
    diag.close();
    toMeetingManagePage();
}

/**
 * 关闭对话框
 * @return
 */
function dialogMeetingClose2(){
    diag.close();
}


////////////////////////////文档管理/////////////////////////////

/**
 * 添加文档
 */
function documentAdd(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 240;
    diag.Title = "上传文档";
    diag.URL = "document_add.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>上传文档</span>";
    diag.Message = "<span style='font-size:13px;'>请上传pdf,doc,ppt,txt,jpg,png,gif等格式的文档</span>";
    diag.show();
}

/**
 * 转换文档
 * @param fileid
 * @return
 */
function documentConvert(fileid){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 240;
    diag.Title = "转换文档";
    diag.URL = "document_add.jsp?fileid="+fileid;
    diag.MessageTitle = "<span style='font-size:13px;'>转换文档</span>";
    diag.Message = "<span style='font-size:13px;'>只能转换pdf,doc,ppt,txt,jpg,png,gif等格式的文档</span>";
    diag.show();
}

/**
 * 关闭对话框
 * @return
 */
function dialogDocumentClose(){
	diag.close();
	toDocumentManagePage();
}

////////////////////////////视频管理/////////////////////////////

/**
 * 上传视频对话框
 */
function videoUpload(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 240;
    diag.Title = "上传音视频";
    diag.URL = "videoplay_upload.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>上传音视频</span>";
    diag.Message = "<span style='font-size:13px;'>请上传flv格式的媒体</span>";
    diag.show();
}

/**
 * 预览视频
 * @param videoid
 * @return
 */
function videoPreview(videoid){
	diag = new Dialog();
    diag.Width = 950;
    diag.Height = 600;
    diag.Title = "预览音视频";
    diag.URL = "videoplay_preview.jsp?videoid="+videoid;
    diag.show();
}


/**
 * 关闭对话框
 * @return
 */
function videoDialogClose(){
	diag.close();
	toVideoManagePage();
}