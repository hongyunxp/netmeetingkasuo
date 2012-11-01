<%@ page language="java" contentType="text/html; charset=GBK"
	pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>异常</title>
<style type="text/css">
body{
	background:#ffffff;
}
</style>
</head>
<body>
<div style="margin-top:150px;margin-left:300px;">
	<img alt="" src="http://localhost/console/images/dialog_warning.png" width="48px" align="absmiddle">&nbsp;&nbsp;操作异常:<br/>
	<div style="font-size:14px;color:red;line-height:20px;margin-left:50px;margin-top:30px;">${error }</div>
</div>
</body>
</html>