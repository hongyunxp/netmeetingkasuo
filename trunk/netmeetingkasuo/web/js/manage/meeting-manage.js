
function meetingSearch() {
	Dialog.alert("��������");
}

/**
 * �鿴��������
 * @param usercode
 * @param meetingid
 * @return
 */
function meetingView(usercode, meetingid) {
	parent.toPage("../manage/meeting?oper=meetingDetail&usercode="+usercode+"&meetingid="+meetingid);
}

/**
 * �༭δ��ʼ�Ļ���
 * @param usercode
 * @param meetingid
 * @return
 */
function meetingEdit(usercode, meetingid) {
	parent.meetingEdit(usercode,meetingid);
}

/**
 * ɾ��δ��ʼ�Ļ���
 * @param usercode
 * @param meetingid
 * @return
 */
function meetingDel(usercode, meetingid) {
	var url = "../manage/meeting?oper=meetingDel";
	Dialog.confirm('���棺��ȷ��Ҫɾ���˻�����',function(){
		$.post(url,{usercode:usercode,meetingid:meetingid},function(data){
			var json = JSON.parse(data);
	        var ret = json["ret"];
	        var text = json["text"];
	        Dialog.alert(text,function(){
	        	if(ret == '1'){
		            return;
		        }else{
		            parent.toMeetingManagePage();
		        }
			});
		});
	});
}