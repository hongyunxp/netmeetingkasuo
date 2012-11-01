<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page import="com.meeting.model.*" %>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+path+"/console/";
	Object obj = session.getAttribute("usermodel");
	UserModel usermodel = null;
    if(obj == null){
        response.sendRedirect("/login.jsp");
    }else{
    	usermodel = (UserModel)session.getAttribute("usermodel");
    	request.setAttribute("username",usermodel.getUsername());
    	request.setAttribute("usercode",usermodel.getUsercode());
    	request.setAttribute("userrole",usermodel.getUserrole());
    }
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>管理控制台</title>
<link rel="stylesheet" type="text/css" href="../css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../css/ext-my.css" />
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<script type="text/javascript">
var userrole = "${userrole}";
var usercode = "${usercode}";
</script>
</head>
<body>
<!------ 页面布局 ------>
<div id="TopDiv" class="x-hide-display">
    <table width="100%" border="0" cellspacing="0">
        <tr style="background-image:url(../images/toolbar/bg.gif);">
            <td height="30px" width="75%">
                <img alt="" src="../images/elearn_logo.png" border="0" align="absmiddle">
            </td>
            <td width="30%" align="right">
                <div id="menusdiv">
                    <span style="font-size:12px;font-weight:bold;margin-right:10px;">
                          [用户：${username }]
                        <a href="../manage/user?oper=userLogout">
                            <span style="margin-left:20px;font-size:12px;">
                                                                            退出
                            </span>
                        </a>
                    </span>
                </div>
            </td>
        </tr>
    </table>
</div>
<div id="LeftDiv" class="x-hide-display">
    <!-- 左侧菜单 -->
    <table class="menu_table">
        <tr>
            <td id="menuMeeting" class="menu_td" onmouseover="menuOver(this.id)" onmouseout="menuOut(this.id)" onclick="menuClick(this.id)">
                <img alt="" src="../images/netmeeting/ext_menu_meeting.png" align="absmiddle" class="menu_img"/>&nbsp;会议管理
            </td>
        </tr>
        <tr>
            <td id="menuDocument" class="menu_td" onmouseover="menuOver(this.id)" onmouseout="menuOut(this.id)" onclick="menuClick(this.id)">
                <img alt="" src="../images/netmeeting/ext_menu_document.png" align="absmiddle" class="menu_img"/>&nbsp;文档管理
            </td>
        </tr>
        <tr>
            <td id="menuVideo" class="menu_td" onmouseover="menuOver(this.id)" onmouseout="menuOut(this.id)" onclick="menuClick(this.id)">
                <img alt="" src="../images/netmeeting/menu_video.png" align="absmiddle" class="menu_img"/>&nbsp;视频管理
            </td>
        </tr>
        <c:if test="${userrole == 0}">
            <tr>
	            <td id="menuUserManage" class="menu_td" onmouseover="menuOver(this.id)" onmouseout="menuOut(this.id)" onclick="menuClick(this.id)">
	                <img alt="" src="../images/netmeeting/menu_user_list.png" align="absmiddle" class="menu_img"/>&nbsp;用户管理
	            </td>
	        </tr>
        </c:if>
        <tr>
            <td id="menuUserSetting" class="menu_td" onmouseover="menuOver(this.id)" onmouseout="menuOut(this.id)" onclick="menuClick(this.id)">
                <img alt="" src="../images/netmeeting/ext_menu_msetting.gif" align="absmiddle" class="menu_img"/>&nbsp;个人设置
            </td>
        </tr>
    </table>
    <!-- 左侧菜单结束 -->
</div>
<div id="ContentDiv" class="x-hide-display">
</div>
<div id="BottomDiv" class="x-hide-display">
    版权所有
</div>
<!------ 页面布局结束 ------>

<jsp:include page="common.jsp" flush="true"></jsp:include>

<script type="text/javascript" src="../js/manage/layout.js"></script>
<script type="text/javascript" src="../js/manage/index.js"></script>
</body>
</html>