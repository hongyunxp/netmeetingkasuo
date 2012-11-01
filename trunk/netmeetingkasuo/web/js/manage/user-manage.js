var diag;

Ext.onReady(function() {
});

/**
 * 用户编辑
 * 
 * @param usercode
 * @return
 */
function userEdit(usercode) {
	parent.userEdit(usercode);
}

/**
 * 用户删除
 * 
 * @param usercode
 * @return
 */
function userDel(usercode) {
	var url = "../manage/user?oper=userDel";
	Dialog.confirm('警告：您确认要删除此用户吗？',function(){
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
 * 关闭对话框
 * 
 * @return
 */
function dialogClose(){
    diag.close();
}

/**
 * 重新设置窗口大小
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
 * 查找用户
 * 
 * @return
 */
function userSearch(){
	$("#searchDiv").is(":visible")?$('#searchDiv').slideUp('normal'):$('#searchDiv').slideDown('normal');
}


