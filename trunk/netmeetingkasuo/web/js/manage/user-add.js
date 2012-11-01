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
 * 刷新添加用户的验证码
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
 * 检查添加的用户账户是否已经存在
 * 
 * @return
 */
function checkUser() {
	var usercode = $("#usercode").val();
	if (usercode == "") {
		Dialog.alert("用户账户不能为空！");
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