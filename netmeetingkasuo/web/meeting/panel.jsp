<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+path+"/console/";
%>
<style type="text/css">
v\:* { 
    behavior: url(#default#VML);
}

img.highqual { 
    -ms-interpolation-mode:bicubic 
}
</style>

<link type="text/css" rel="stylesheet" href="../js/toolbar/toolbar.css"/>

<script type="text/javascript" src="../js/ext-base.js"></script>
<script type="text/javascript" src="../js/ext-all.js"></script>
<script type="text/javascript" src="../js/ext-popup.js"></script>
<script type="text/javascript" src="../js/ext-msg.js"></script>

<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="../js/jquery.form.js"></script>

<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>

<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/toolbar/toolbar.js"></script>
<script type="text/javascript" src="../js/stringBuilder.js"></script>
<script type="text/javascript">

var IMAGESPATH = '../js/zDialog/images/'; //图片路径配置
var toolbarPath = '../js/toolbar/';
Ext.BLANK_IMAGE_URL = '../images/s.gif';
var basePath = "<%=basePath%>";

var hostSid = "${hostmodel.sessionid}";
var userSid = "${usermodel.sessionid}";

Ext.onReady(function() {
    Ext.QuickTips.init();
});

/**
 * 显示提示信息
 */
function showMsg(msg){
	parent.showmessager(msg);
}

/**
 * 显示提示信息
 */
function showMsg2(msg){
	parent.showmessager2(msg);
}
</script>

<!------ 页面加载中 ------>
<div id="loading-mask" align="center">
</div>
<div id="loading">
    <span id="loading-message">&nbsp;&nbsp;&nbsp;数据加载中，请耐心等待......</span>
</div>
<!------ 页面加载结束 ------>