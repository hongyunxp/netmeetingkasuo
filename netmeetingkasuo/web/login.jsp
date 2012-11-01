<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.meeting.model.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	Object obj = session.getAttribute("usermodel");
	if(obj != null){
		response.sendRedirect("manage/index.jsp");
	}
%>

<%@page import="com.meeting.model.UserModel"%><html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>系统登录</title>
<style type="text/css">
<!--
a{ color:#008EE3}
a:link  { text-decoration: none;color:#008EE3}
A:visited {text-decoration: none;color:#666666}
A:active {text-decoration: underline}
A:hover {text-decoration: underline;color: #0066CC}
A.b:link {
    text-decoration: none;
    font-size:12px;
    font-family: "Helvetica,微软雅黑,宋体";
    color: #FFFFFF;
}
A.b:visited {
    text-decoration: none;
    font-size:12px;
    font-family: "Helvetica,微软雅黑,宋体";
    color: #FFFFFF;
}
A.b:active {
    text-decoration: underline;
    color: #FF0000;

}
A.b:hover {
    text-decoration: underline; 
    color: #ffffff
}
.table1 {
    border: 1px solid #CCCCCC;
}
.font {
    font-size: 12px;
    text-decoration: none;
    color: #999999;
    line-height: 20px;
}
.input {
    font-size: 12px;
    color: #999999;
    text-decoration: none;
    border: 0px none #999999;
}
td {
    font-size: 12px;
    color: #007AB5;
}
form {
    margin: 1px;
    padding: 1px;
}
input {
    border: 0px;
    height: 26px;
    color: #007AB5;
    border: thin none #FFFFFF;
}
.unnamed1 {
    border: thin none #FFFFFF;
}
select {
    border: 1px solid #cccccc;
    height: 18px;
    color: #666666;
    border: thin none #FFFFFF;
}
body {
    background-repeat: no-repeat;
    background-color: #9CDCF9;
    background-position: 0px 0px;
}
.tablelinenotop {
    border-top: 0px solid #CCCCCC;
    border-right: 1px solid #CCCCCC;
    border-bottom: 0px solid #CCCCCC;
    border-left: 1px solid #CCCCCC;
}
.tablelinenotopdown {
    border-top: 1px solid #eeeeee;
    border-right: 1px solid #eeeeee;
    border-bottom: 1px solid #eeeeee;
    border-left: 1px solid #eeeeee;
}
.style6 {
    FONT-SIZE: 9pt; 
    color: #7b8ac3; 
}

-->
</style>
</head>
<body>
<table width="681" border="0" align="center" cellpadding="0" cellspacing="0" style="margin-top:120px">
    <form id="login_frm" name="login_frm" action="manage/user?oper=userLogin">
    <tr>
        <td width="353" height="259" align="center" valign="bottom" background="images/login_1.gif">
            <table width="90%" border="0" cellspacing="3" cellpadding="0">
                  <tr>
                      <td align="right" valign="bottom" style="color:#05B8E4;padding-bottom:5px;padding-right:10px;">
                          Power by <a href="http://localhost/" target="_blank">kasuo</a> Copyright 2011
                      </td>
                  </tr>
            </table>
        </td>
        <td width="195" background="images/login_2.gif">
            <table width="190" height="106" border="0" align="center" cellpadding="2" cellspacing="0">
                <form method="post" onSubmit="return chk(this);" name="NETSJ_Login">
                    <tr>
                      <td height="50" colspan="2" align="left">&nbsp;</td>
                    </tr>
                    <tr>
                      <td width="60" height="30" align="left">登陆名称</td>
                      <td>
                          <input name="usercode" value="admin" type="TEXT" style="background:url(images/login_6.gif) repeat-x; border:solid 1px #27B3FE; height:20px; background-color:#FFFFFF; width:120px;" id="usercode">
                      </td>
                    </tr>
                    <tr>
                      <td height="30" align="left">登陆密码</td>
                      <td>
                          <input name="password" value="suntek" TYPE="PASSWORD" style="background:url(images/login_6.gif) repeat-x; border:solid 1px #27B3FE; height:20px; background-color:#FFFFFF; width:120px;" id="password">
                      </td>
                    </tr>
                    <tr>
                      <td height="30"> 验 证 码 </td>
                      <td>
                           <input name="verifycode" type="text" id="verifycode" size="4" style="background:url(images/login_6.gif) repeat-x; border:solid 1px #27B3FE; height:20px; background-color:#FFFFFF" maxlength="4">
                           <span id="authCode" onclick="refresh()" style="cursor:hand;">
                              <img src="image.jsp" width="50" height="21" align="absmiddle"/>
                           </span>
                      </td>
                    </tr>
                    <tr>
                       <td height="40" colspan="2" align="center">
                           <span style="display:none;"><img src="images/login_8.gif" width="16" height="16"> 请勿非法登陆！</span>
                       </td>
                    </tr>
                    <tr>
		               <td colspan="2" align="center">
		                      <input type="submit" name="submit" style="background:url(images/login_5.gif) no-repeat;cursor:hand;" value=" 登  陆 "> 
		                      <input type="reset" name="Submit" style="background:url(images/login_5.gif) no-repeat;cursor:hand;" value=" 重  置   ">
		                 </td>
                    </tr>
                    <tr>
                        <td height="15" colspan="2" align="right" style="padding-top:10px;">现在<a href="#" onclick="register()">注册</a>?</td>
                    </tr>
                </form>
            </table>
        </td>
        <td width="133" background="images/login_3.gif">&nbsp;</td>
    </tr>
  <tr>
    <td height="161" colspan="3" background="images/login_4.gif"></td>
  </tr>
  </form>
</table>
<script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="js/jquery.form.js"></script>
<script type="text/javascript" src="js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="js/zDialog/zDialog.js"></script>
<script type="text/javascript" src="js/json.js"></script>
<script type="text/javascript">
var IMAGESPATH = 'js/zDialog/images/'; //图片路径配置
$(document).ready(function() {
	$("#usercode").focus();
	$('#login_frm').ajaxForm(function(data) { 
        var json = JSON.parse(data);
        var ret = json["ret"];
        var text = json["text"];
        if(ret == '1'){
        	Dialog.alert(text);
        	$("#verifycode").focus();
        	$("#verifycode").val("");
        	refresh();
            return;
        }else{
            window.location="manage/index.jsp";
        }
    }); 
});


var diag;
function register(){
    diag = new Dialog();
    diag.Width = 550;
    diag.Height = 280;
    diag.Title = "注册用户";
    diag.URL = "register.jsp";
    diag.MessageTitle = "注册用户";
    diag.Message = "请按照提示，详细填写下面的注册信息";
    diag.show();
}

function registerClose(){
    diag.close();
}

function refresh(){
    document.getElementById("authCode").innerHTML = "&nbsp;";
    document.getElementById("authCode").innerHTML="<img src='image.jsp?time="+new Date().getTime()+"' width='50' height='21' align='absmiddle'/>";
}
</script>
</body>
</html>