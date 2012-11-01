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
<title>��������</title>
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
    <!-- �޸ĸ����˺���Ϣ -->
    <form id="user_frm" name="user_frm" action="../manage/user?oper=userMod">
    <input type="hidden" id="picpath" name="picpath" value=""/>
    <input type="hidden" id="userrole" name="userrole" value=""/>
    <table cellspacing="0" cellpadding="0" class="mDetailTable">
        <tr class="mDetailTableHead" onclick="userChange()">
            <td>
                &nbsp;&nbsp;<img alt="" src="../images/netmeeting/userlist_menu.png" border="0" align="absmiddle">&nbsp;&nbsp;�޸ĸ�����Ϣ
            </td>
            <td colspan="3" align="right">
                <img id="userMinus" alt="" src="../images/netmeeting/bullet_toggle_minus.png" border="0" align="absmiddle"  style="display:none;">
                <img id="userPlus" alt="" src="../images/netmeeting/bullet_toggle_plus.png" border="0" align="absmiddle">
            </td>
        </tr>
        <tbody id="userBody" style="display:none;">
	        <tr>
	            <td class="reg_td">�û��˺ţ�</td>
	            <td colspan="3">
	                <input type="text" id="usercode" name="usercode" class="reg_input_readonly" readonly="readonly"/>
	            </td>
	        </tr>
	         <tr>
                <td class="reg_td">�û�������</td>
                <td>
                    <input type="text" id="username" name="username" class="reg_input"/>
                </td>
                <td class="reg_td">�û����䣺</td>
                <td>
                    <input type="text" id="email" name="email" class="reg_input"/>
                </td>
            </tr>
	        <tr>
	            <td class="reg_td">�û����룺</td>
	            <td>
	                <input type="password" id="password" name="password" class="reg_input"/>
	            </td>
	            <td class="reg_td">����ȷ�ϣ�</td>
	            <td>
	                <input type="password" id="verifypwd" name="verifypwd" class="reg_input"/>
	            </td>
	        </tr>
	        <tr>
	            <td align="right" style="padding-right:5px;">
	               <span id="spanButtonPlaceHolder" title="�ϴ�ͼƬ">&nbsp;</span>
	            </td>
                <td colspan="3" style="padding:5px;">
                    &nbsp;&nbsp;<img id="userpic" alt="" src="../images/netmeeting/user_avatar.gif" border="0" width="96" height="96">
                </td>
            </tr>
            <tr>
                <td colspan="4">
                    <br/>
                    <input type="submit" name="submit" value=" ��  �� "/>
                </td>
            </tr>
        </tbody>
    </table>
    </form>
    
    <!-- �޸Ļ������� -->
    <form id="setting_frm" name="setting_frm" action="../manage/user?oper=userSettingUpdate">
    <table cellspacing="0" cellpadding="0" class="mDetailTable">
        <tr class="mDetailTableHead">
            <td colspan="4">
                &nbsp;&nbsp;<img alt="" src="../images/netmeeting/userlist_menu.png" border="0" align="absmiddle">&nbsp;&nbsp;�޸Ļ�������
            </td>
        </tr>
        <tbody id="meetingBody">
            <tr>
	            <td colspan="4">
	                <span>
	                    <input type="checkbox" id="<%=AppConfigure.KEY_ALLOWHANDUP %>" name="<%=AppConfigure.KEY_ALLOWHANDUP %>" value="1"/>&nbsp;&nbsp;
	                                                    �Ƿ���������вλ���Ա���� ����������빴ѡ��������ȡ����ѡ����
	                </span>
	            </td>
	        </tr>
	        <tr>
                <td colspan="4">
                    <span>
                        <input type="checkbox" id="<%=AppConfigure.KEY_ALLOWDESKTOPCONTROL %>" name="<%=AppConfigure.KEY_ALLOWDESKTOPCONTROL %>" value="1"/>&nbsp;&nbsp;
                                                                �Ƿ���������вλ���Աʹ��Զ��Э������������빴ѡ��������ȡ����ѡ����
                    </span>
                </td>
            </tr>
            <tr>
                <td colspan="4">
                    <span>
                        <input type="checkbox" id="<%=AppConfigure.KEY_ALLOWWHITEBOARD %>" name="<%=AppConfigure.KEY_ALLOWWHITEBOARD %>" value="1"/>&nbsp;&nbsp;
                                                                �Ƿ���������вλ���Աʹ�õ��Ӱװ壨��������빴ѡ��������ȡ����ѡ����
                    </span>
                </td>
            </tr>
            <tr>
                <td colspan="4">
                    <br/>
                    <input type="submit" name="submit" value=" ��  �� "/>
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