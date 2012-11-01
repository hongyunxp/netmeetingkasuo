<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String meetingId = request.getParameter("meetingId");
    String sessionId = request.getParameter("sessionId");
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>ѡ���û�</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>

<div style="margin-left:20px;margin-top:15px;">
   <div style="margin:10px;">��ҪԶ��Э������Ա�б�</div>
   <div style="margin:10px;">
        <div id="meetingusers"></div>
   </div>
</div>

<div id="closeBtn" align="center" style="margin-top:20px;">
    <input type="button" id="sppOk" name="sppOk" value=" ȷ  �� " onclick="sspSelectOk()"/>
    <input type="button" id="sspClose" name="sspClose" value=" ��  �� " onclick="parent.sspDialogClose()"/>
</div>

<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>

<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/stringBuilder.js"></script>

<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>

<script type="text/javascript">
var IMAGESPATH = '../js/zDialog/images/'; //ͼƬ·������
var meetingId = "<%=meetingId%>";
var userSid = "<%=sessionId%>";
$(document).ready(function() {
    var url = "../meeting?oper=getMeetingUserList";
    $.post(url,{meetingId:meetingId,userSid:userSid},function(data){
        var json = JSON.parse(data);
        if(json == "" || json.length <1){
        	$("#meetingusers").html("û�п�ѡ��Э������Ա��");
        	return;
        }
        var html = new stringBuilder();
        html.append('<select id="sspusersel" name="sspusersel" style="width:200px;">');
        html.append('<option value="0">--��ѡ��--</option>');
        for ( var i = 0; i < json.length; i++) {
            var sessionid = json[i]["sessionid"];
            var usercode = json[i]["usercode"];
            var username = json[i]["username"];
            html.append('<option value="'+sessionid+'">'+username+'</option>');
        }
        html.append('</select>');
        $("#meetingusers").html("��ѡ��"+html);
    });
});

function sspSelectOk(){
    var userSel = $("#sspusersel").val();
    var userName = $("#sspusersel").find("option:selected").text();
    if(userSel == '0'){
        Dialog.alert("��δѡ����ҪЭ������Ա��");
        return;
    }
    parent.sspDialogUserSel(userSel,userName);
}
</script>
</body>
</html>