<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+path+"/console/";
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>网络会议</title>
<link rel="stylesheet" type="text/css" href="../css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../css/ext-my.css" />
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>
<!------ 页面布局 ------>
<div id="TopDiv" class="x-hide-display"></div>

<!-- 左侧面板 -->
<div id="LeftDiv" class="x-hide-display">
    <div id="UserListDiv"></div>
    <div id="AudioDiv" align='center'></div>
    <div id="ChatDiv">
        <div id="ChatNorthDiv" style="height:145px;"></div>
        <div id="ChatCenterDiv" style="height:24px;"></div>
        <div id="ChatSouthDiv"></div>
    </div>
</div>

<!-- 中间面板 -->
<div id="ContentDiv" class="x-hide-display">
    <div id="DocumentDiv" class="x-hide-display"></div>
    <div id="WhiteBoardDiv" class="x-hide-display"></div>
    <div id="ScreenShareDiv" class="x-hide-display"></div>
    <div id="ScreenControlDiv" class="x-hide-display"></div>
    <div id="DocumentDisDiv" class="x-hide-display"></div>
    <div id="VideoPlayDiv" class="x-hide-display" ></div>
</div>

<!-- 右侧面板 -->
<div id="RightDiv" class="x-hide-display" style="padding:2px;">
    
</div>

<!-- 底部面板 -->
<div id="BottomDiv" class="x-hide-display">
    <table style="width:100%;border:0;height:20px;font-size:11px;background-image:url(../images/toolbar/bg.gif);">
            <tr>
                <td width="50%">
                                                新太科技股份有限公司
                </td>
                <td align="right">
                    <span id="onlinetime" style="text-align:left;">在线时间  00:00:00</span>&nbsp;
                </td>
                <td id="trainingstatue" style="text-align:left;background:url(../images/elearning/training_signalall.gif) no-repeat -0px -64px;height:16px;width:32px">
                    &nbsp;
                </td>
            </tr>
        </table>
</div>
<!------ 页面布局结束 ------>

<div id="meetingrecord-win" class="x-hidden">
    <div class="x-window-header">
        &nbsp;会议记录
    </div>
</div>

<!-- 笑脸 -->
<div id="laughfaces" style="display:none;"></div>

<!-- 聊天窗口 -->
<div id="ChatPanelDiv" class="x-hidden"></div>

<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/meeting/smiles.js" ></script>
<script type="text/javascript" src="../js/meeting/layout.js" ></script>
<script type="text/javascript" src="../js/meeting/menu.js" ></script>
<script type="text/javascript" src="../js/meeting/index.js" ></script>

<script type='text/javascript' src='../js/jwplayer/swfobject.js'></script>

<script type="text/javascript" src="../dwr/engine.js" ></script>
<script type="text/javascript" src="../dwr/util.js" ></script>
<script type="text/javascript" src="../dwr/interface/IndexService.js" ></script>
<script type="text/javascript" src="../dwr/interface/UserListService.js" ></script>
<script type="text/javascript" src="../dwr/interface/ChatService.js" ></script>
<script type="text/javascript" src="../dwr/interface/DocumentService.js" ></script>
<script type="text/javascript" src="../dwr/interface/WhiteBoardService.js" ></script>
<script type="text/javascript" src="../dwr/interface/DesktopShareService.js" ></script>
<script type="text/javascript" src="../dwr/interface/DesktopControlService.js" ></script>
<script type="text/javascript" src="../dwr/interface/DocumentDispatchService.js" ></script>
<script type="text/javascript" src="../dwr/interface/VideoPlayService.js" ></script>
<script type="text/javascript" src="../dwr/interface/VideoShareService.js" ></script>
<script type="text/javascript" src="../dwr/interface/AudioShareService.js" ></script>
<script type="text/javascript" src="../dwr/interface/DocumentWhiteBoardService.js" ></script>
</body>
</html>