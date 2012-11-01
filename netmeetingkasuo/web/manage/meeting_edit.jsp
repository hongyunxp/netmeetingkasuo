<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String meetingid = request.getParameter("meetingid");
    String usercode = request.getParameter("usercode");
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>�༭����</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<script type="text/javascript">
var meetingid = "<%= meetingid%>";
var usercode = "<%= usercode%>";
</script>
</head>
<body>
<form id="meeting_frm" name="meeting_frm" action="../manage/meeting?oper=meetingMod">
    <input type="hidden" id="meetingid" name="meetingid" value="<%= meetingid%>"/>
	<table class="reg_table">
	    <tr class="reg_tr">
	        <td class="reg_td">�������⣺</td>
	        <td>
	            <input type="text" id="subject" name="subject" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">��ʼʱ�䣺</td>
	        <td>
	            <input id="begintime" name="begintime" class="Wdate" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">Ԥ�Ƴ���ʱ�䣺</td>
	        <td style="font-size:13px;">
	            <select id="hour" name="hour">
	               <option value="1">1</option>
	               <option value="2">2</option>
	               <option value="3">3</option>
	               <option value="4">4</option>
	               <option value="5">5</option>
	            </select>Сʱ
	             <select id="minute" name="minute">
                   <option value="10">10</option>
                   <option value="20">20</option>
                   <option value="30">30</option>
                   <option value="40">40</option>
                   <option value="50">50</option>
                   <option value="60">60</option>
                </select>����
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td" valign="top">������̣�</td>
	        <td>
	           <textarea id="agenda" name="agenda" class="meeting_agenda"></textarea>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">������֤�룺</td>
	        <td>
	            <input type="password" id="verifycode" name="verifycode" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
            <td class="reg_td">ȷ����֤�룺</td>
            <td>
                <input type="password" id="verifycode2" name="verifycode2" class="reg_input"/>
            </td>
        </tr>
	    <tr>
	        <td colspan="2" align="center">
	             <br/><br/>
	            <input type="submit" name="submit" value=" ��  �� "/>
	            &nbsp;&nbsp;&nbsp;&nbsp;
	            <input type="button" name="cancel" value=" ȡ  �� " onclick="parent.dialogMeetingClose()"/>
	        </td>
	    </tr>
	</table>
</form>
<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../js/dateutils.js"></script>
<script type="text/javascript" src="../js/manage/meeting-edit.js"></script>
</body>
</html>