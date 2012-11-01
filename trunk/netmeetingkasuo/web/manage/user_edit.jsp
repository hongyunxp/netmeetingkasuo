<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String usercode = request.getParameter("usercode");
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>用户注册</title>
<script type="text/javascript">
var usercode = "<%= usercode%>";
</script>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>
<form id="user_frm" name="user_frm" action="../manage/user?oper=userMod">
	<table class="reg_table">
	    <tr class="reg_tr">
	        <td class="reg_td">用户账号：</td>
	        <td>
	            <input type="text" id="usercode" name="usercode" class="reg_input_readonly" readonly="readonly"/>
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
            <td class="reg_td">用户角色：</td>
            <td>
                <input type="radio" value="0" name="userrole" /><span style="font-size:13px;">管理员</span>
                <input type="radio" value="1" name="userrole" /><span style="font-size:13px;">普通用户</span>
            </td>
        </tr>
	    <tr>
	        <td colspan="2" align="center">
	             <br/><br/>
	            <input type="submit" name="submit" value=" 保  存 "/>
	            &nbsp;&nbsp;&nbsp;&nbsp;
	            <input type="button" name="cancel" value=" 取  消 " onclick="parent.dialogUserClose()"/>
	        </td>
	    </tr>
	</table>
</form>
<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/manage/user-edit.js"></script>
</body>
</html>