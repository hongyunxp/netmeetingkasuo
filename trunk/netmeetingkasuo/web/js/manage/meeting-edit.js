$(document).ready(function(){
	$.post("../manage/meeting?oper=meetingGet", {
		meetingid : meetingid,
		usercode : usercode
	}, function(data) {
		var json = JSON.parse(data);
		var subject = json["subject"];
		var begintime = json["begintime"];
		var agenda = json["agenda"];
		var verifycode = json["verifycode"];
		var hour = json["hour"];
		var minute = json["minute"];
		$("#subject").val(subject);
		$("#begintime").val(begintime);
		$("#hour").val(hour);
		$("#minute").val(minute);
		$("#agenda").val(agenda);
		$("#hour").val(hour);
		$("#minute").val(minute);
		$("#verifycode").val(verifycode);
		$("#verifycode2").val(verifycode);
	});
	
	$('#meeting_frm').ajaxForm(function(data) {
		var json = JSON.parse(data);
		var ret = json["ret"];
		var text = json["text"];
		Dialog.alert(text,function(){
			if (ret == '1') {
				return;
			} else {
				parent.dialogMeetingClose();
			}
		});
	});
});