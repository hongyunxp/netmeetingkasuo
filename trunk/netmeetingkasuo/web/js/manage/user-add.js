$(document).ready(function() {
	$('#user_frm').ajaxForm(function(data) {
		var json = JSON.parse(data);
		var ret = json["ret"];
		var text = json["text"];
		Dialog.alert(text,function(){
			if (ret == '1') {
				return;
			} else {
				parent.dialogUserClose();
			}
		});
	});
});

/**
 * ˢ������û�����֤��
 * 
 * @return
 */
function refresh() {
	document.getElementById("authCode").innerHTML = "&nbsp;";
	document.getElementById("authCode").innerHTML = "<img src='../image.jsp?time="
			+ new Date().getTime()
			+ "' width='50' height='21' align='absmiddle'/>";
}

/**
 * �����ӵ��û��˻��Ƿ��Ѿ�����
 * 
 * @return
 */
function checkUser() {
	var usercode = $("#usercode").val();
	if (usercode == "") {
		Dialog.alert("�û��˻�����Ϊ�գ�");
		return;
	}
	$.post('../manage/user?oper=checkUser', {
		usercode : usercode
	}, function(data) {
		var json = JSON.parse(data);
		var ret = json["ret"];
		var text = json["text"];
		Dialog.alert(text);
		return;
	});
}