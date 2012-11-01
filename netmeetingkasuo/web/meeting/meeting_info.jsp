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
<title>���������Ϣ</title>
<style type="text/css">
body{
    background:#fff;
    font-size:12px;
}
</style>
</head>
<body>
<img src="../images/netmeeting/netmeeting_book.jpg" style="width:200px;position:absolute;right:30px;bottom:30px;z-index:9999;"/>
<div style="overflow:auto">
    <table width="100%" border="0" style="margin-top: 220px; line-height: 30px;font-size:12px;">
        <tr>
            <td width="35%" align="right"><b>�������:</b></td>
            <td>&nbsp;&nbsp;<span id="msubject">${meeting.subject }</span></td>
        </tr>
        <tr>
            <td align="right"><b>����������:</b></td>
            <td>&nbsp;&nbsp;<span id="username">${hostmodel.username }</span></td>
        </tr>
        <tr>
            <td align="right"><b>����������:</b></td>
            <td>&nbsp;&nbsp;<span id="useremail">${hostmodel.useremail }</span></td>
        </tr>
        <tr>
            <td align="right"><b>������:</b></td>
            <td>&nbsp;&nbsp;<span id="mid">${meeting.meetingId }</span></td>
        </tr>
        <tr>
            <td align="right"><b>����·��:</b></td>
            <td>&nbsp;&nbsp;<span id="meetingpath">http://<%=request.getServerName() %>:<%=request.getServerPort() %>/meeting?oper=getMeeting&meetingid=${meeting.meetingId }</span></td>
        </tr>
        <tr>
            <td align="right"><b>��������:</b></td>
            <td>&nbsp;&nbsp;<span id="verifyCode">${meeting.verifyCode }</span></td>
        </tr>
        <tr>
            <td align="right"><b>�������:</b></td>
            <td>&nbsp;&nbsp;<span id="magenda">${meeting.agenda }</span></td>
        </tr>
        <tr>
            <td align="right"><b>��ʼʱ��:</b></td>
            <td>&nbsp;&nbsp;<span id="mbegintime">${meeting.begintime }</span></td>
        </tr>
        <tr>
            <td align="right"><b>����ʱ��:</b></td>
            <td>&nbsp;&nbsp;<span id="duration">${meeting.duration }</span>����</td>
        </tr>
    </table>
</div>
</body>
</html>