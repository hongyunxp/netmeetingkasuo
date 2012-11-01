Ext.onReady(function() {
	viewport = new Ext.Viewport( {
		layout : 'border',
		items : [ topPanel, leftPanel, contentPanel, bottomPanel ]
	});
});

var menuId = "";
var diag;

/**
 * �����ͣ�ڲ˵��Ϸ�
 * @param id
 * @return
 */
function menuOver(id) {
	$("#"+id).css("background","#FFE88C");
}

/**
 * ����Ƴ��ڲ˵��Ϸ�
 * @param id
 * @return
 */
function menuOut(id) {
	if(menuId != id)
		$("#"+id).css("background","#FFFFFF");
}

/**
 * ������˵�
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
 * ��ת����Ƶ����ҳ��
 * @return
 */
function toVideoManagePage(){
	Ext.getCmp('contentPanel').setTitle("��Ƶ����","videoShare_film");
	toPage("../manage/video?oper=videoList");
}

/**
 * ��ת���ĵ�����ҳ��
 * @return
 */
function toDocumentManagePage(){
	Ext.getCmp('contentPanel').setTitle("�ĵ�����","doc_share");
	toPage("../manage/document?oper=docList");
}

/**
 * ��ת���û��б�ҳ��
 * @return
 */
function toUserManagePage(){
	Ext.getCmp('contentPanel').setTitle("�û�����","ext_menu_user");
	toPage("../manage/user?oper=userList");
}

/**
 * ��ת���û��б�ҳ��
 * @return
 */
function toUseSettinigPage(){
	Ext.getCmp('contentPanel').setTitle("��������","ext_menu_msetting");
	toPage("user_setting.jsp?usercode="+usercode);
}

/**
 * ��ת������������
 * @return
 */
function toMeetingManagePage(){
	Ext.getCmp('contentPanel').setTitle("�������","ext_menu_meeting");
	toPage("../manage/meeting?oper=meetingList");
}

/**
 * ʹ��content��ת��ҳ��URL
 * @param url
 * @return
 */
function toPage(url){
	$("#contentFrame").attr("src",url);
}


////////////////////////////�û�����/////////////////////////////
/**
 * ����û��Ի���
 * @return
 */
function userAdd(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 280;
    diag.Title = "����û�";
    diag.URL = "user_add.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>����û�</span>";
    diag.Message = "<span style='font-size:13px;'>�밴����ʾ����ϸ��д������û���Ϣ</span>";
    diag.show();
}

/**
 * �༭�û��Ի���
 * @return
 */
function userEdit(usercode){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 260;
    diag.Title = "�༭�û�";
    diag.URL = "user_edit.jsp?usercode="+usercode;
    diag.MessageTitle = "<span style='font-size:13px;'>�༭�û�</span>";
    diag.Message = "<span style='font-size:13px;'>�밴����ʾ����ϸ��д������û���Ϣ</span>";
    diag.show();
}

/**
 * �����û�
 * @return
 */
function userSearch(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 250;
    diag.Title = "����û�";
    diag.URL = "user_search.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>�����û�</span>";
    diag.Message = "<span style='font-size:13px;'>�������ѯ����</span>";
    diag.show();
}

/**
 * �رնԻ���
 * @return
 */
function dialogUserClose(){
    diag.close();
    toUserManagePage();
}

/**
 * �رնԻ���
 * @return
 */
function dialogUserClose2(){
    diag.close();
}

////////////////////////////�������/////////////////////////////

/**
 * ��ӻ���
 */
function meetingAdd(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 320;
    diag.Title = "ԤԼ����";
    diag.URL = "meeting_add.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>ԤԼ����</span>";
    diag.Message = "<span style='font-size:13px;'>�밴����ʾ����ϸ��д����Ļ�����Ϣ</span>";
    diag.show();
}

/**
 * �༭����Ի���
 * @return
 */
function meetingEdit(usercode,meetingid){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 320;
    diag.Title = "�༭����";
    diag.URL = "meeting_edit.jsp?usercode="+usercode+"&meetingid="+meetingid;
    diag.MessageTitle = "<span style='font-size:13px;'>�༭����</span>";
    diag.Message = "<span style='font-size:13px;'>�밴����ʾ����ϸ��д����Ļ�����Ϣ</span>";
    diag.show();
}

/**
 * �رնԻ���
 * @return
 */
function dialogMeetingClose(){
    diag.close();
    toMeetingManagePage();
}

/**
 * �رնԻ���
 * @return
 */
function dialogMeetingClose2(){
    diag.close();
}


////////////////////////////�ĵ�����/////////////////////////////

/**
 * ����ĵ�
 */
function documentAdd(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 240;
    diag.Title = "�ϴ��ĵ�";
    diag.URL = "document_add.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>�ϴ��ĵ�</span>";
    diag.Message = "<span style='font-size:13px;'>���ϴ�pdf,doc,ppt,txt,jpg,png,gif�ȸ�ʽ���ĵ�</span>";
    diag.show();
}

/**
 * ת���ĵ�
 * @param fileid
 * @return
 */
function documentConvert(fileid){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 240;
    diag.Title = "ת���ĵ�";
    diag.URL = "document_add.jsp?fileid="+fileid;
    diag.MessageTitle = "<span style='font-size:13px;'>ת���ĵ�</span>";
    diag.Message = "<span style='font-size:13px;'>ֻ��ת��pdf,doc,ppt,txt,jpg,png,gif�ȸ�ʽ���ĵ�</span>";
    diag.show();
}

/**
 * �رնԻ���
 * @return
 */
function dialogDocumentClose(){
	diag.close();
	toDocumentManagePage();
}

////////////////////////////��Ƶ����/////////////////////////////

/**
 * �ϴ���Ƶ�Ի���
 */
function videoUpload(){
	diag = new Dialog();
    diag.Width = 550;
    diag.Height = 240;
    diag.Title = "�ϴ�����Ƶ";
    diag.URL = "videoplay_upload.jsp";
    diag.MessageTitle = "<span style='font-size:13px;'>�ϴ�����Ƶ</span>";
    diag.Message = "<span style='font-size:13px;'>���ϴ�flv��ʽ��ý��</span>";
    diag.show();
}

/**
 * Ԥ����Ƶ
 * @param videoid
 * @return
 */
function videoPreview(videoid){
	diag = new Dialog();
    diag.Width = 950;
    diag.Height = 600;
    diag.Title = "Ԥ������Ƶ";
    diag.URL = "videoplay_preview.jsp?videoid="+videoid;
    diag.show();
}


/**
 * �رնԻ���
 * @return
 */
function videoDialogClose(){
	diag.close();
	toVideoManagePage();
}