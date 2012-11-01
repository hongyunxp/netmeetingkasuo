<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String meetingId = request.getParameter("meetingId");
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>��������</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>

<div id="audioList" style="margin:10px;"></div>

<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>

<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/stringBuilder.js"></script>

<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>

<script type="text/javascript">
var IMAGESPATH = '../js/zDialog/images/'; //ͼƬ·������
var meetingId = "<%=meetingId%>";
$(document).ready(function() {
    var url = "../meeting?oper=getAudioUserList";
    $.post(url,{meetingId:meetingId},function(data){
        var json = JSON.parse(data);
        var html = new stringBuilder();

        var audioOnImg = "<img src='../images/netmeeting/audioShare_add.png' border='0' align='absmiddle' title='�������˵���˷�'/>";
        var audioOffImg = "<img src='../images/netmeeting/audioShare_del.png' border='0' align='absmiddle' title='�رմ��˵���˷�'/>";
        
        html.append("<table width='100%' border='0' cellspacing='0' cellpadding='0' class='dispatchTable'>");
        html.append("<tr  class='dispatchTrHead'>");
        html.append("<td width='30%' height='25px' align='center'>����</td>");
        html.append("<td width='30%' align='center'>״̬</td>");
        html.append("<td width='40%' align='center'>����</td>");
        html.append("</tr>");
        for ( var i = 0; i < json.length; i++) {
            var sessionid = json[i]["sessionid"];
            var username = json[i]["username"];
            var audiostate = json[i]["audiostate"];

            var audioStateImg = "";
            var audioLink = "";
            if(audiostate == 1){
            	audioStateImg = "--";
                audioLink = "<a href='#' onclick='audioOn(\""+sessionid+"\")'>"+audioOnImg+"</a>";
            }else if(audiostate == 2){
            	audioStateImg = "<img src='../images/netmeeting/audioShare_Status_on.png' border='0' align='absmiddle' title='����ʹ����˷�'/>";
                audioLink = "<a href='#' onclick='audioOff(\""+sessionid+"\")'>"+audioOffImg+"</a>";
            }else {
            	audioStateImg = "<img src='../images/netmeeting/audioShare_Status_off.png' border='0' align='absmiddle' title='��˷��Ѿ���'/>";
                audioLink = "<a href='#' onclick='audioOff(\""+sessionid+"\")'>"+audioOffImg+"</a>";
            }
             
        	html.append("<tr id='audioTr_"+sessionid+"'>");
            html.append("<td height='25px' class='dispatchTd' align='center'>"+username+"</td>");
            html.append("<td class='dispatchTd' align='center'>"+audioStateImg+"</td>");
            html.append("<td align='center' class='dispatchTd'>"+audioLink+"</td>");
            html.append("</tr>");
        }
        html.append("</table>");
        $("#audioList").html(html.toString());
    });
});

/**
 * ѡ���˿�������������Ա
 */
function audioOn(sid){
    parent.audioShareWinStart(sid);
}

/**
 * ѡ���˹رյ���������Ա
 */
function audioOff(sid){
	Dialog.confirm("�Ƿ�ȷ�Ϲر�����?",function(){
		parent.audioShareWinStop(sid);
	});
}

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