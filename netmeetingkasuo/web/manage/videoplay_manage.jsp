<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="/WEB-INF/extremecomponents.tld" prefix="ec" %>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>��Ƶ�б�</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<link type="text/css" rel="stylesheet" href="../js/toolbar/toolbar.css"/>
<link rel="stylesheet" type="text/css" href="../css/extremecomponents.css" />
<script type="text/javascript">
var IMAGESPATH = '../js/zDialog/images/'; //ͼƬ·������
var toolbarPath = '../js/toolbar/';
</script>
</head>
<body>
<div style="margin:0">
	<ul class="my_tool_bar">
	    <li class="toolbar_btn" onclick="videoUpload()">
	        <img src='../images/netmeeting/menu_user_add.png' alt="" width="16px"/><div>�ϴ���Ƶ</div>
	    </li>
	</ul>
</div>
<ec:table 
    items="videolist"
    action="../manage/video?oper=videoList"
    imagePath="../images/table/*.gif"
    width="100%"
    rowsDisplayed="5" showStatusBar="true">
    <ec:row>
        <ec:column property="videoName" alias="����"/>
        <ec:column property="videoUser" alias="�ϴ���"/>
        <ec:column property="videoSize" alias="��С"/>
        <ec:column property="videoExt" alias="��չ��"/>
        <ec:column property="videoCreate" alias="����ʱ��"/>
        <ec:column property="operate" alias="����"/>
    </ec:row>
</ec:table>
<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="../js/jquery.progressbar.min.js"></script>

<script type="text/javascript" src="../js/toolbar/toolbar.js"></script>
<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/stringBuilder.js"></script>

<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>

<script type="text/javascript" src="../js/upload/swfupload.swfobject.js"></script>
<script type="text/javascript" src="../js/upload/swfupload.js"></script>

<script type="text/javascript" src="../js/manage/videoplay_mgr.js"></script>
</body>
</html>