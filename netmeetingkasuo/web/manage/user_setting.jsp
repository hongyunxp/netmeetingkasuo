<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ page import="com.meeting.utils.AppConfigure" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    String usercode = request.getParameter("usercode");
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>个人设置</title>
<link rel="stylesheet" type="text/css" href="../css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="../css/ext-my.css" />
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<script type="text/javascript">
var usercode = "<%=usercode%>";
var allowhandup = "<%=AppConfigure.KEY_ALLOWHANDUP %>";
var allowdesktopcontrol = "<%=AppConfigure.KEY_ALLOWDESKTOPCONTROL %>";
var allowwhiteboard = "<%=AppConfigure.KEY_ALLOWWHITEBOARD %>";
</script>
</head>
<body>
<div>
    <!-- 修改个人账号信息 -->
    <form id="user_frm" name="user_frm" action="../manage/user?oper=userMod">
    <input type="hidden" id="picpath" name="picpath" value=""/>
    <input type="hidden" id="userrole" name="userrole" value=""/>
    <table cellspacing="0" cellpadding="0" class="mDetailTable">
        <tr class="mDetailTableHead" onclick="userChange()">
            <td>
                &nbsp;&nbsp;<img alt="" src="../images/netmeeting/userlist_menu.png" border="0" align="absmiddle">&nbsp;&nbsp;修改个人信息
            </td>
            <td colspan="3" align="right">
                <img id="userMinus" alt="" src="../images/netmeeting/bullet_toggle_minus.png" border="0" align="absmiddle"  style="display:none;">
                <img id="userPlus" alt="" src="../images/netmeeting/bullet_toggle_plus.png" border="0" align="absmiddle">
            </td>
        </tr>
        <tbody id="userBody" style="display:none;">
	        <tr>
	            <td class="reg_td">用户账号：</td>
	            <td colspan="3">
	                <input type="text" id="usercode" name="usercode" class="reg_input_readonly" readonly="readonly"/>
	            </td>
	        </tr>
	         <tr>
                <td class="reg_td">用户姓名：</td>
                <td>
                    <input type="text" id="username" name="username" class="reg_input"/>
                </td>
                <td class="reg_td">用户邮箱：</td>
                <td>
                    <input type="text" id="email" name="email" class="reg_input"/>
                </td>
            </tr>
	        <tr>
	            <td class="reg_td">用户密码：</td>
	            <td>
	                <input type="password" id="password" name="password" class="reg_input"/>
	            </td>
	            <td class="reg_td">密码确认：</td>
	            <td>
	                <input type="password" id="verifypwd" name="verifypwd" class="reg_input"/>
	            </td>
	        </tr>
	        <tr>
	            <td align="right" style="padding-right:5px;">
	               <span id="spanButtonPlaceHolder" title="上传图片">&nbsp;</span>
	            </td>
                <td colspan="3" style="padding:5px;">
                    &nbsp;&nbsp;<img id="userpic" alt="" src="../images/netmeeting/user_avatar.gif" border="0" width="96" height="96">
                </td>
            </tr>
            <tr>
                <td colspan="4">
                    <br/>
                    <input type="submit" name="submit" value=" 保  存 "/>
                </td>
            </tr>
        </tbody>
    </table>
    </form>
    
    <!-- 修改会议设置 -->
    <form id="setting_frm" name="setting_frm" action="../manage/user?oper=userSettingUpdate">
    <table cellspacing="0" cellpadding="0" class="mDetailTable">
        <tr class="mDetailTableHead">
            <td colspan="4">
                &nbsp;&nbsp;<img alt="" src="../images/netmeeting/userlist_menu.png" border="0" align="absmiddle">&nbsp;&nbsp;修改会议设置
            </td>
        </tr>
        <tbody id="meetingBody">
            <tr>
	            <td colspan="4">
	                <span>
	                    <input type="checkbox" id="<%=AppConfigure.KEY_ALLOWHANDUP %>" name="<%=AppConfigure.KEY_ALLOWHANDUP %>" value="1"/>&nbsp;&nbsp;
	                                                    是否允许会议中参会人员举手 （如果允许，请勾选；否则，请取消勾选。）
	                </span>
	            </td>
	        </tr>
	        <tr>
                <td colspan="4">
                    <span>
                        <input type="checkbox" id="<%=AppConfigure.KEY_ALLOWDESKTOPCONTROL %>" name="<%=AppConfigure.KEY_ALLOWDESKTOPCONTROL %>" value="1"/>&nbsp;&nbsp;
                                                                是否允许会议中参会人员使用远程协助（如果允许，请勾选；否则，请取消勾选。）
                    </span>
                </td>
            </tr>
            <tr>
                <td colspan="4">
                    <span>
                        <input type="checkbox" id="<%=AppConfigure.KEY_ALLOWWHITEBOARD %>" name="<%=AppConfigure.KEY_ALLOWWHITEBOARD %>" value="1"/>&nbsp;&nbsp;
                                                                是否允许会议中参会人员使用电子白板（如果允许，请勾选；否则，请取消勾选。）
                    </span>
                </td>
            </tr>
            <tr>
                <td colspan="4">
                    <br/>
                    <input type="submit" name="submit" value=" 保  存 "/>
                </td>
            </tr>
        </tbody>
    </table>
    </form>
</div>
    
<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../js/dateutils.js"></script>
<script type="text/javascript" src="../js/upload/swfupload.swfobject.js"></script>
<script type="text/javascript" src="../js/upload/swfupload.js"></script>
<script type="text/javascript" src="../js/manage/user-setting.js"></script>
</body>
</html>