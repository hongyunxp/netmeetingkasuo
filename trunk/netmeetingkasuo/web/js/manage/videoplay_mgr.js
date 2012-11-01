/**
 * 文档视频
 * 
 * @return
 */
function videoUpload() {
	parent.videoUpload();
}

/**
 * 预览视频
 * 
 * @param fileid
 * @return
 */
function videoPreview(videoid) {
	parent.videoPreview(videoid);
}

/**
 * 删除视频
 * 
 * @param fileid
 * @return
 */
function videoDelete(videoid) {
	var url = "../manage/video?oper=videoDelete";
	Dialog.confirm('警告：您确认要删除视频吗？', function() {
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
