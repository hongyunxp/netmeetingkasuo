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
<title>�û�ע��</title>
<script type="text/javascript">
var usercode = "<%= usercode%>";
</script>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>
<form id="user_frm" name="user_frm" action="../manage/user?oper=userMod">
	<table class="reg_table">
	    <tr class="reg_tr">
	        <td class="reg_td">�û��˺ţ�</td>
	        <td>
	            <input type="text" id="usercode" name="usercode" class="reg_input_readonly" readonly="readonly"/>
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
            <td class="reg_td">�û���ɫ��</td>
            <td>
                <input type="radio" value="0" name="userrole" /><span style="font-size:13px;">����Ա</span>
                <input type="radio" value="1" name="userrole" /><span style="font-size:13px;">��ͨ�û�</span>
            </td>
        </tr>
	    <tr>
	        <td colspan="2" align="center">
	             <br/><br/>
	            <input type="submit" name="submit" value=" ��  �� "/>
	            &nbsp;&nbsp;&nbsp;&nbsp;
	            <input type="button" name="cancel" value=" ȡ  �� " onclick="parent.dialogUserClose()"/>
	        </td>
	    </tr>
	</table>
</form>
<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/manage/user-edit.js"></script>
</body>
</html>