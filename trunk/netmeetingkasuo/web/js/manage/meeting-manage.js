
function meetingSearch() {
	Dialog.alert("搜索会议");
}

/**
 * 查看会议详情
 * @param usercode
 * @param meetingid
 * @return
 */
function meetingView(usercode, meetingid) {
	parent.toPage("../manage/meeting?oper=meetingDetail&usercode="+usercode+"&meetingid="+meetingid);
}

/**
 * 编辑未开始的会议
 * @param usercode
 * @param meetingid
 * @return
 */
function meetingEdit(usercode, meetingid) {
	parent.meetingEdit(usercode,meetingid);
}

/**
 * 删除未开始的会议
 * @param usercode
 * @param meetingid
 * @return
 */
function meetingDel(usercode, meetingid) {
	var url = "../manage/meeting?oper=meetingDel";
	Dialog.confirm('警告：您确认要删除此会议吗？',function(){
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