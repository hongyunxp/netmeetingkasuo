$(document).ready(function() {
	var time = DateFormat.format(new Date(), "yyyy-MM-dd hh:mm:ss");
	var time2 = DateFormat.format(new Date(), "hh_mm_ss");
	$("#subject").val("我的会议" + time2);
	$("#begintime").val(time);
	$("#agenda").val("议程 " + time2);
	$("#verifycode").val("12");
	$("#verifycode2").val("12");
	$('#meeting_frm').ajaxForm(function(data) {
		var json = JSON.parse(data);
		var ret = json["ret"];
		var text = json["text"];
		Dialog.alert(text, function() {
			if (ret == '1') {
				return;
			} else {
				parent.dialogMeetingClose();
			}
		});
	});
});


/**
 * 查找用户
 * @return
 */
function userSelect(){
	diag = new Dialog();
    diag.Width = 400;
    diag.Height = 400;
    diag.Title = "查找用户";
    diag.URL = "../manage/user?oper=userSelList";
    diag.MessageTitle = "<span style='font-size:13px;'>查找用户</span>";
    diag.Message = "<span style='font-size:13px;'>请按照提示，详细填写下面的用户信息</span>";
    diag.OKEvent=function(){
    	var input = diag.innerFrame.contentWindow.document.getElementsByTagName("input");
		var val = "";
	    for (var i=0;i<input.length ;i++ ){
	        if(input[i].type=="checkbox" && input[i].checked){
	        	val += input[i].value+";";
	        }
	    }
		$("#invites").val(val);
		diag.close();
	};
    diag.show();
}


function userSelectCallback(val){
	alert(val);
}