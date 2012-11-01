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
<title>ԤԼ����</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body>
<form id="meeting_frm" name="meeting_frm" action="../manage/meeting?oper=meetingAdd">
	<table class="reg_table">
	    <tr class="reg_tr">
	        <td class="reg_td">�������⣺</td>
	        <td>
	            <input type="text" id="subject" name="subject" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">��ʼʱ�䣺</td>
	        <td>
	            <input id="begintime" name="begintime" class="Wdate" type="text" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">Ԥ�Ƴ���ʱ�䣺</td>
	        <td style="font-size:13px;">
	            <select id="hour" name="hour">
	               <option value="1">1</option>
	               <option value="2">2</option>
	               <option value="3">3</option>
	               <option value="4">4</option>
	               <option value="5">5</option>
	            </select>Сʱ
	             <select id="minute" name="minute">
                   <option value="10">10</option>
                   <option value="20">20</option>
                   <option value="30">30</option>
                   <option value="40">40</option>
                   <option value="50">50</option>
                   <option value="60">60</option>
                </select>����
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td" valign="top">������̣�</td>
	        <td>
	           <textarea id="agenda" name="agenda" class="meeting_agenda"></textarea>
	        </td>
	    </tr>
	    <tr>
	        <td class="reg_td">������֤�룺</td>
	        <td>
	            <input type="password" id="verifycode" name="verifycode" class="reg_input"/>
	        </td>
	    </tr>
	    <tr>
            <td class="reg_td">ȷ����֤�룺</td>
            <td>
                <input type="password" id="verifycode2" name="verifycode2" class="reg_input"/>
            </td>
        </tr>
        <tr>
            <td class="reg_td">�������룺</td>
            <td>
                <input type="text" id="invites" name="invites" class="reg_input" readonly="readonly"/>
                <span style="font:normal 12px verdana;color:red;">
                    <a href="#" onclick="userSelect()">
                        <img src="../images/netmeeting/ext_menu_user_add.png" align="absmiddle" border='0'/>
                    </a>&nbsp;(ѡ��������Ա)
                </span>
            </td>
        </tr>
        <tr>
            <td class="reg_td">�ʼ�֪ͨ��</td>
            <td>
                <input type="checkbox" id="mailnotify" name="mailnotify"/> 
                <span style="font:normal 13px verdana; cursor:hand;" onclick="alert('sdf');">�Ƿ�����ʼ�֪ͨ��</span>
            </td>
        </tr>
	    <tr>
	        <td colspan="2" align="center">
	             <br/>
	            <input type="submit" name="submit" value=" ��  �� "/>
	            &nbsp;&nbsp;&nbsp;&nbsp;
	            <input type="button" name="cancel" value=" ȡ  �� " onclick="parent.dialogMeetingClose()"/>
	        </td>
	    </tr>
	</table>
</form>
<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript" src="../js/My97DatePicker/WdatePicker.js"></script>
<script type="text/javascript" src="../js/dateutils.js"></script>
<script type="text/javascript" src="../js/manage/meeting-add.js"></script>
</body>
</html>