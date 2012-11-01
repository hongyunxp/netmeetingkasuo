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
<title>会议设置</title>
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
var IMAGESPATH = '../js/zDialog/images/'; //图片路径配置
var meetingId = "<%=meetingId%>";
$(document).ready(function() {
    var url = "../meeting?oper=getAudioUserList";
    $.post(url,{meetingId:meetingId},function(data){
        var json = JSON.parse(data);
        var html = new stringBuilder();

        var audioOnImg = "<img src='../images/netmeeting/audioShare_add.png' border='0' align='absmiddle' title='开启此人的麦克风'/>";
        var audioOffImg = "<img src='../images/netmeeting/audioShare_del.png' border='0' align='absmiddle' title='关闭此人的麦克风'/>";
        
        html.append("<table width='100%' border='0' cellspacing='0' cellpadding='0' class='dispatchTable'>");
        html.append("<tr  class='dispatchTrHead'>");
        html.append("<td width='30%' height='25px' align='center'>姓名</td>");
        html.append("<td width='30%' align='center'>状态</td>");
        html.append("<td width='40%' align='center'>操作</td>");
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
            	audioStateImg = "<img src='../images/netmeeting/audioShare_Status_on.png' border='0' align='absmiddle' title='正在使用麦克风'/>";
                audioLink = "<a href='#' onclick='audioOff(\""+sessionid+"\")'>"+audioOffImg+"</a>";
            }else {
            	audioStateImg = "<img src='../images/netmeeting/audioShare_Status_off.png' border='0' align='absmiddle' title='麦克风已静音'/>";
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
 * 选择了开启的语音的人员
 */
function audioOn(sid){
    parent.audioShareWinStart(sid);
}

/**
 * 选择了关闭的语音的人员
 */
function audioOff(sid){
	Dialog.confirm("是否确认关闭语音?",function(){
		parent.audioShareWinStop(sid);
	});
}

function sspSelectOk(){
    var userSel = $("#sspusersel").val();
    var userName = $("#sspusersel").find("option:selected").text();
    if(userSel == '0'){
        Dialog.alert("尚未选择需要协助的人员！");
        return;
    }
    parent.sspDialogUserSel(userSel,userName);
}
</script>
</body>
</html>