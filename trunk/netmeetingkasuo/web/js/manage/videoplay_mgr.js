/**
 * �ĵ���Ƶ
 * 
 * @return
 */
function videoUpload() {
	parent.videoUpload();
}

/**
 * Ԥ����Ƶ
 * 
 * @param fileid
 * @return
 */
function videoPreview(videoid) {
	parent.videoPreview(videoid);
}

/**
 * ɾ����Ƶ
 * 
 * @param fileid
 * @return
 */
function videoDelete(videoid) {
	var url = "../manage/video?oper=videoDelete";
	Dialog.confirm('���棺��ȷ��Ҫɾ����Ƶ��', function() {
		$.post(url, {
			videoid : videoid
		}, function(data) {
			var json = JSON.parse(data);
			var ret = json["ret"];
			var text = json["text"];
			Dialog.alert(text, function() {
				if (ret == '1') {
					return;
				} else {
					parent.toVideoManagePage();
				}
			});
		});
	});
}
