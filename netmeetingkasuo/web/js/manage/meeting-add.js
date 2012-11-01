$(document).ready(function() {
	var time = DateFormat.format(new Date(), "yyyy-MM-dd hh:mm:ss");
	var time2 = DateFormat.format(new Date(), "hh_mm_ss");
	$("#subject").val("�ҵĻ���" + time2);
	$("#begintime").val(time);
	$("#agenda").val("��� " + time2);
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
 * �����û�
 * @return
 */
function userSelect(){
	diag = new Dialog();
    diag.Width = 400;
    diag.Height = 400;
    diag.Title = "�����û�";
    diag.URL = "../manage/user?oper=userSelList";
    diag.MessageTitle = "<span style='font-size:13px;'>�����û�</span>";
    diag.Message = "<span style='font-size:13px;'>�밴����ʾ����ϸ��д������û���Ϣ</span>";
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