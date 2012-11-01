$(document).ready(function(){
	$.post("../manage/user?oper=userGet", {
		usercode : usercode
	}, function(data) {
		var json = JSON.parse(data);
		var usercode = json["usercode"];
		var password = json["password"];
		var username = json["username"];
		var userrole = json["userrole"];
		var useremail = json["useremail"];
		var userpic = json["userpic"];
		$("#usercode").val(usercode);
		$("#password").val(password);
		$("#verifypwd").val(password);
		$("#verifypwdtr").hide();
		$("#username").val(username);
		$("#email").val(useremail);
		$("#verifycodeTr").hide();
		if(userrole == '0'){
			$("input[name='userrole'][value=0]").attr("checked",true);
		}else{
			$("input[name='userrole'][value=1]").attr("checked",true);
		}
	});
	
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