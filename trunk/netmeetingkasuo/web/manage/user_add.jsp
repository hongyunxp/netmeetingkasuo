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
<title>�û�ע��</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>
<form id="user_frm" name="user_frm" action="../manage/user?oper=userAdd">
	<table class="reg_table">
	    <tr class="reg_tr">
	        <td class="reg_td">�û��˺ţ�</td>
	        <td>
	            <input type="text" id="usercode" name="usercode" class="reg_input"/>
	            &nbsp;&nbsp;
	            <input type="button" value=" ����û� " onclick="checkUser()"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">�û����룺</td>
	        <td>
	            <input type="password" id="password" name="password" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">����ȷ�ϣ�</td>
	        <td>
	            <input type="password" id="verifypwd" name="verifypwd" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">�û�������</td>
	        <td>
	            <input type="text" id="username" name="username" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">�û����䣺</td>
	        <td>
	            <input type="text" id="email" name="email" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">��֤�룺</td>
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
	            <input type="submit" name="submit" value=" ��  �� "/>
	            &nbsp;&nbsp;&nbsp;&nbsp;
	            <input type="button" name="cancel" value=" ȡ  �� " onclick="top.window.dialogUserClose()"/>
	        </td>
	    </tr>
	</table>
</form>
<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/manage/user-add.js"></script>
</body>
</html>