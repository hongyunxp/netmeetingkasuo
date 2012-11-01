function documentSearch() {
	Dialog.alert("搜索会议");
}

/**
 * 转换文档
 * 
 * @param usercode
 * @param documentid
 * @return
 */
function docConvert(fileid) {
	parent.documentConvert(fileid);
}

/**
 * 预览文档
 * 
 * @param usercode
 * @param documentid
 * @return
 */
function docPreview(fileid) {
	window.location = "../manage/document?oper=docPreview&fileid=" + fileid;
}

/**
 * 删除文档
 * 
 * @param usercode
 * @param documentid
 * @return
 */
function docDelete(fileid) {
	var url = "../manage/document?oper=docDelete";
	Dialog.confirm('警告：您确认要删除此文档吗？', function() {
		$.post(url, {
			fileid : fileid
		}, function(data) {
			var json = JSON.parse(data);
			var ret = json["ret"];
			var text = json["text"];
			Dialog.alert(text, function() {
				if (ret == '1') {
					return;
				} else {
					parent.toDocumentManagePage();
				}
			});
		});
	});
}