<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>用户注册</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>
<form id="user_frm" name="user_frm" action="../manage/user?oper=userAdd">
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
	                <img src="../image.jsp" width="50" height="21" align="absmiddle"/>
	            </span>
	        </td>
	    </tr>
	    <tr>
	        <td colspan="2" align="center">
	             <br/><br/>
	            <input type="submit" name="submit" value=" 提  交 "/>
	            &nbsp;&nbsp;&nbsp;&nbsp;
	            <input type="button" name="cancel" value=" 取  消 " onclick="top.window.dialogUserClose()"/>
	        </td>
	    </tr>
	</table>
</form>
<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/manage/user-add.js"></script>
</body>
</html>