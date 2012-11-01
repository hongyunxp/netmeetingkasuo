/**
 * 文档上传
 * @return
 */
function documentUpload(){
	parent.documentManagementUpload();
}

/**
 * 文档转换
 * @param fileid
 * @return
 */
function documentConvert(fileid){
	docDiag = new Dialog();
	docDiag.Width = 550;
	docDiag.Height = 240;
	docDiag.Title = "转换文档";
	docDiag.URL = "meeting/document_upload.jsp?fileid="+fileid;
	docDiag.MessageTitle = "<span style='font-size:13px;'>转换文档</span>";
	docDiag.Message = "<span style='font-size:13px;'>只能转换pdf,doc,ppt,txt,jpg,png,gif等格式的文档</span>";
	docDiag.show();
}

/**
 * 预览文档
 * @param fileid
 * @return
 */
function documentPreview(fileid,filename){
	parent.documentPlay(fileid,filename,1);
}

/**
 * 删除文档
 * @param fileid
 * @return
 */
function documentDelete(fileid){
	
}
