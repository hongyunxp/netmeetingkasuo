var diag;

Ext.onReady(function() {
});

/**
 * �û��༭
 * 
 * @param usercode
 * @return
 */
function userEdit(usercode) {
	parent.userEdit(usercode);
}

/**
 * �û�ɾ��
 * 
 * @param usercode
 * @return
 */
function userDel(usercode) {
	var url = "../manage/user?oper=userDel";
	Dialog.confirm('���棺��ȷ��Ҫɾ�����û���',function(){
		$.post(url,{usercode:usercode},function(data){
			var json = JSON.parse(data);
	        var ret = json["ret"];
	        var text = json["text"];
	        Dialog.alert(text,function(){
	        	if(ret == '1'){
		            return;
		        }else{
		            parent.toUserListPage();
		        }
			});
		});
	});
}

/**
 * �رնԻ���
 * 
 * @return
 */
function dialogClose(){
    diag.close();
}

/**
 * �������ô��ڴ�С
 * 
 * @return
 */
function resizeWindow() {
	var height = document.body.clientHeight - 10;
	var width = document.body.clientWidth - 10;
	grid.setHeight(height);
	grid.setWidth(width);
}

/**
 * �����û�
 * 
 * @return
 */
function userSearch(){
	$("#searchDiv").is(":visible")?$('#searchDiv').slideUp('normal'):$('#searchDiv').slideDown('normal');
}


