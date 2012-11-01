<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>上传视频</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<script type="text/javascript">
var IMAGESPATH = '../js/zDialog/images/'; //图片路径配置
</script>
</head>
<body>

<div id="btns" style="margin:20px;">
	<span id="spanButtonPlaceHolder" title="上传文档">&nbsp;</span>
</div>

<div style="margin-left:20px;margin-top:15px;">
    <span id="processing" style="display:none;">
                上传进度： <span id="uploadpic"></span>
                <span id="closeUpload" style="margin-left:10px;"></span>
    </span>
    <span id="uploadtxt" style="margin-left:20px;font-size:12px;color:green;">&nbsp;</span>
</div>

<div id="uploadTable" style="margin:20px;width:500px;">
    &nbsp;
</div>

<div id="closeBtn" align="center" style="margin-top:20px;">
    <input type="button" id="docClose" name="cancel" value=" 关  闭 " onclick="videoUploadClose()"/>
</div>

<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="../js/jquery.progressbar.min.js"></script>

<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/stringBuilder.js"></script>

<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>

<script type="text/javascript" src="../js/upload/swfupload.swfobject.js"></script>
<script type="text/javascript" src="../js/upload/swfupload.js"></script>
<script type="text/javascript" src="../js/meeting/videoplay_upload.js"></script>
</body>
</html>