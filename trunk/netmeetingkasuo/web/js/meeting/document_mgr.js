/**
 * �ĵ��ϴ�
 * @return
 */
function documentUpload(){
	parent.documentManagementUpload();
}

/**
 * �ĵ�ת��
 * @param fileid
 * @return
 */
function documentConvert(fileid){
	docDiag = new Dialog();
	docDiag.Width = 550;
	docDiag.Height = 240;
	docDiag.Title = "ת���ĵ�";
	docDiag.URL = "meeting/document_upload.jsp?fileid="+fileid;
	docDiag.MessageTitle = "<span style='font-size:13px;'>ת���ĵ�</span>";
	docDiag.Message = "<span style='font-size:13px;'>ֻ��ת��pdf,doc,ppt,txt,jpg,png,gif�ȸ�ʽ���ĵ�</span>";
	docDiag.show();
}

/**
 * Ԥ���ĵ�
 * @param fileid
 * @return
 */
function documentPreview(fileid,filename){
	parent.documentPlay(fileid,filename,1);
}

/**
 * ɾ���ĵ�
 * @param fileid
 * @return
 */
function documentDelete(fileid){
	
}
