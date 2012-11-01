<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String fileid = request.getParameter("fileid");
    if(fileid == null){
    	fileid = "";
    }
    String documentMgr = request.getParameter("oper");
    if(documentMgr == null){
    	documentMgr = "";
    }
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>�ϴ��ĵ�</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<script type="text/javascript">
var IMAGESPATH = '../js/zDialog/images/'; //ͼƬ·������
var FILEID = "<%=fileid %>";
var FILENAME = "";
var DOCUMENTMGR = "<%=documentMgr %>";
</script>
</head>
<body>

<div id="btns" style="margin:20px;">
	<span id="spanButtonPlaceHolder" title="�ϴ��ĵ�">&nbsp;</span>
	<span id="convertImg" style="display: none;cursor:hand;" onclick="convert()"/>
	   <img alt="" src="../images/netmeeting/upload_convert.gif" border="0">
	</span>
</div>

<div style="margin-left:20px;margin-top:15px;">
    <span id="processing" style="display:none;">
                �ϴ����ȣ� <span id="uploadpic"></span>
                <span id="closeUpload" style="margin-left:10px;"></span>
    </span>
    <span id="uploadtxt" style="margin-left:20px;font-size:12px;color:green;">&nbsp;</span>
</div>

<div id="uploadTable" style="margin:20px;width:500px;">
    &nbsp;
</div>

<div id="closeBtn" align="center" style="margin-top:20px;">
    <input type="button" id="docClose" name="cancel" value=" ��  �� " onclick="documentUploadClose()"/>
    <input type="button" id="docPlay" name="docPlay" value=" ��  �� " onclick="documentPlay()" style="display:none;"/>
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
<script type="text/javascript" src="../js/meeting/document_upload.js"></script>
</body>
</html>