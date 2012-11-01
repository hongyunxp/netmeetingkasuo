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
<title>文档列表</title>
<link rel="stylesheet" type="text/css" href="../css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../css/ext-my.css" />
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<link rel="stylesheet" type="text/css" href="../css/extremecomponents.css" />  
<style type="text/css">
.search{
    background: #dfefef;
    position:absolute;
    top:30px;
    left:2px;
    width:450px;
    height:250px;
    border:solid 1px #ccc;
    z-index: 9999;
    display: none;
}

v\:* { 
    behavior: url(#default#VML);
}

img.highqual { 
    -ms-interpolation-mode:bicubic 
}
</style>
<script type="text/javascript">
var fileid = "${filemodel.fileId}";
</script>
</head>
<body onload="init()">

<div align="right" style="margin:10px;">
    <input type="hidden" id="seq" name="seq" value="1"/>
    <input type="hidden" id="total" name="total" value="${filemodel.filePage}"/>

    <span style="margin-right:100px;">《${filemodel.fileName }》</span>
    <a href="#" onclick="parent.toDocumentManagePage()"><span style="margin-right:20px;cursor:hand;">返回</span></a>
    <a href="#" onclick="enlarge()">放大</a>&nbsp;| 
    <a href="#" onclick="shrink()">缩小</a>&nbsp;&nbsp;&nbsp;&nbsp;
    <a href="#" onclick="handlepage('f')">首页</a>&nbsp;| 
    <a href="#" onclick="handlepage('p')">上一页</a>&nbsp;| 
    <a href="#" onclick="handlepage('n')">下一页</a>&nbsp;|
    <a href="#" onclick="handlepage('l')">末页</a> &nbsp;
    <span id="seqpage">1</span>/<span id="totalpage">${filemodel.filePage }</span>&nbsp;&nbsp;
</div>

<center>
	<div id="showimg" style="width:96%;margin:0px;padding:5px;border:1px solid #cccccc;overflow:auto;cursor:hand;" onclick="handlepage('n')" title="点击跳转到下一页">
	</div>
</center>

<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../js/dateutils.js"></script>
<script type="text/javascript" src="../js/manage/document-preview.js"></script>
</body>
</html>