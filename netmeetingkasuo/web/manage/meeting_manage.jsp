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
<title>用户列表</title>
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
</style>
</head>
<body>
<ul class="my_tool_bar">
    <li class="toolbar_btn" onclick="parent.meetingAdd()">
        <img src='../images/netmeeting/menu_user_add.png' alt="" width="16px"/><div>预约会议</div>
    </li>
    <li class="toolbar_btn" onclick="meetingSearch()">
        <img src='../images/netmeeting/menu_user_search.png' alt="" width="16px"/><div>搜索会议</div>
    </li>
</ul>
<ec:table 
    items="usermeetinglist"
    action="../manage/meeting?oper=meetingList"
    imagePath="../images/table/*.gif"
    width="100%"
    rowsDisplayed="5" showStatusBar="true">
    <ec:row>
        <ec:column property="subject" alias="会议主题"/>
        <ec:column property="username" alias="预约人"/>
        <ec:column property="agenda" alias="会议议程"/>
        <ec:column property="begintime" alias="开始时间"/>
        <ec:column property="duration" alias="持续时间"/>
        <ec:column property="state" alias="会议状态"/>
        <ec:column property="operate" alias="操作"/>
    </ec:row>
</ec:table>

<!-- 查找 -->
<div id="searchDiv" class="search">
	<form id="user_frm" name="user_frm" action="user_list.jsp?oper=search" method="post" target="contentFrame">
	    <table class="reg_table">
	        <tr class="reg_tr">
	            <td class="reg_td">用户账号：</td>
	            <td>
	                <input type="text" id="usercode" name="usercode" class="reg_input"/>
	            </td>
	        </tr>
	        <tr>
	            <td class="reg_td">用户姓名：</td>
	            <td>
	                <input type="text" id="username" name="username" class="reg_input"/>
	            </td>
	        </tr>
	        <tr>
	            <td class="reg_td">用户邮箱：</td>
	            <td>
	                <input type="text" id="email" name="email" class="reg_input"/>
	            </td>
	        </tr>
	        <tr>
	            <td class="reg_td">用户角色：</td>
	            <td>
	                <input type="radio" value="0" name="userrole" /><span style="font-size:13px;">管理员</span>
	                <input type="radio" value="1" name="userrole" /><span style="font-size:13px;">普通用户</span>
	            </td>
	        </tr>
	        <tr>
	            <td class="reg_td">注册时间：</td>
	            <td style="font-size:13px;">
	                                            开始:<input id="regtime_begin" name="regtime_begin" class="Wdate" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
	                <br/>
	                                            结束:<input id="regtime_end" name="regtime_end" class="Wdate" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
	            </td>
	        </tr>
	        <tr>
	            <td colspan="2" align="center">
	                 <br/><br/>
	                <input type="submit" name="submit" value=" 查  找 "/>
	                &nbsp;&nbsp;&nbsp;&nbsp;
	                <input type="button" name="cancel" value=" 关  闭 " onclick="$('#searchDiv').hide()"/>
	            </td>
	        </tr>
	    </table>
	</form>
</div>
<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../js/dateutils.js"></script>
<script type="text/javascript" src="../js/manage/meeting-manage.js"></script>

</body>
</html>