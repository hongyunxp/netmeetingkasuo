<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String chatpath = request.getParameter("chatpath");
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>聊天记录</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>

<div id="chatDiv"></div>

<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>

<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/stringBuilder.js"></script>

<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>
<script type="text/javascript">
var chatpath = "<%=chatpath%>";
$(function(){
    var url = "../monitor?oper=chatView";
	$.post(url,{chatpath:chatpath},function(data){
		$("#chatDiv").html(data);
	});
});
</script>
</body>
</html>