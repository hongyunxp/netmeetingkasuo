function documentSearch() {
	Dialog.alert("��������");
}

/**
 * ת���ĵ�
 * 
 * @param usercode
 * @param documentid
 * @return
 */
function docConvert(fileid) {
	parent.documentConvert(fileid);
}

/**
 * Ԥ���ĵ�
 * 
 * @param usercode
 * @param documentid
 * @return
 */
function docPreview(fileid) {
	window.location = "../manage/document?oper=docPreview&fileid=" + fileid;
}

/**
 * ɾ���ĵ�
 * 
 * @param usercode
 * @param documentid
 * @return
 */
function docDelete(fileid) {
	var url = "../manage/document?oper=docDelete";
	Dialog.confirm('���棺��ȷ��Ҫɾ�����ĵ���', function() {
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