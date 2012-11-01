<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/extremecomponents.tld" prefix="ec" %>
<%
    String videoid = request.getParameter("videoid");
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>文档列表</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<script type="text/javascript">
var RED5_OFLADEMO = "rtmp://<%=request.getServerName()%>/oflaDemo";
var videoid = "<%=videoid%>";
function init(){
    var url = "../manage/video?oper=videoPreview";
    $.post(url,{videoid:videoid},function(data){
        var json = JSON.parse(data);
        var filename=json["videoName"];

        var flashvars = {'file':filename,'streamer':RED5_OFLADEMO,'autostart':'true'};
        var params = {'allowfullscreen':'true','allowscriptaccess':'always','wmode':'opaque'};
        var attributes = {};
        swfobject.embedSWF("../js/jwplayer/pl.swf",
        "VideoPlayContentId", "950", "600",
        "9.0.0", false, flashvars, params, attributes);
    });
}
</script>
</head>
<body onload="init()">
<div id="VideoPlayContentId"></div>
<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>
<script type='text/javascript' src='../js/jwplayer/swfobject.js'></script>
</body>
</html>