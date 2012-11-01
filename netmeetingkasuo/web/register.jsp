<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>用户注册</title>
<link rel="stylesheet" type="text/css" href="css/common.css" />
</head>
<body>
<form id="reg_frm" name="reg_frm" action="manage/user?oper=userAdd">
	<table class="reg_table">
	    <tr class="reg_tr">
	        <td class="reg_td">用户账号：</td>
	        <td>
	            <input type="text" id="usercode" name="usercode" class="reg_input"/>
	            &nbsp;&nbsp;
	            <input type="button" value=" 检测用户 " onclick="checkUser()"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">用户密码：</td>
	        <td>
	            <input type="password" id="password" name="password" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">密码确认：</td>
	        <td>
	            <input type="password" id="verifypwd" name="verifypwd" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">用户姓名：</td>
	        <td>
	            <input type="text" id="username" name="username" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">用户邮箱：</td>
	        <td>
	            <input type="text" id="email" name="email" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">验证码：</td>
	        <td>
	            <input type="text" id="verifycode" name="verifycode" class="reg_verify"/>
	            &nbsp;
	            <span id="authCode" onclick="refresh()" style="cursor:hand;">
	                <img src="image.jsp" width="50" height="21" align="absmiddle"/>
	            </span>
	        </td>
	    </tr>
	    <tr>
	        <td colspan="2" align="center">
	             <br/><br/>
	            <input type="submit" name="submit" value=" 提  交 "/>
	        </td>
	    </tr>
	</table>
</form>
<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="js/jquery.form.js"></script>
<script type="text/javascript" src="js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="js/zDialog/zDialog.js"></script>
<script type="text/javascript" src="js/json.js"></script>
<script type="text/javascript">
var IMAGESPATH = 'js/zDialog/images/'; //图片路径配置
$(document).ready(function() { 
    $('#reg_frm').ajaxForm(function(data) { 
        var json = JSON.parse(data);
        var ret = json["ret"];
        var text = json["text"];
        Dialog.alert(text,function(){
        	if(ret == '1'){
                return;
            }else{
                parent.registerClose();
            }
        });
    }); 
});

function refresh(){
    document.getElementById("authCode").innerHTML = "&nbsp;";
    document.getElementById("authCode").innerHTML="<img src='image.jsp?time="+new Date().getTime()+"' width='50' height='21' align='absmiddle'/>";
}

function checkUser(){
	var usercode = $("#usercode").val();
	if(usercode == ""){
		Dialog.alert("用户账户不能为空！");
		return;
	}
    $.post('manage/user?oper=checkUser',{usercode:usercode},function(data){
    	var json = JSON.parse(data);
        var ret = json["ret"];
        var text = json["text"];
        Dialog.alert(text);
        return;
    });
    //Dialog.confirm('警告：您确认要XXOO吗？',function(){Dialog.alert("yeah，周末到了，正是好时候")});
}
</script>
</body>
</html>