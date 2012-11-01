<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ page import="com.meeting.dao.*" %>
<%@ page import="com.meeting.model.*" %>
<%@ page import="com.meeting.utils.*" %>
<%@ page import="org.apache.log4j.Logger"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
    String apachePath = request.getScheme()+"://"+request.getServerName()+path+"/console/";
    
    Logger logger = Logger.getLogger("index.jsp");
    try{
        MeetingModel meeting = (MeetingModel)session.getAttribute(AppConfigure.MEETING);
        UserModel host = (UserModel)session.getAttribute(AppConfigure.HOST_USER);
        UserModel user = (UserModel)session.getAttribute(AppConfigure.CURRENT_USER);
        
        UserMeetingModel userMeetingModel = null;
        if(host.getUsercode().equals(user.getUsercode())){
            userMeetingModel = UserMeetingDao.getInstance().getMeeting(meeting.getMeetingId(),
            		host.getUsercode(),
                    AppConfigure.MEETING_ROLE_ADMIN);
            if(meeting.getState() == AppConfigure.MEETING_ENDED){
            	meeting.setState(AppConfigure.MEETING_IN_PROGRESS);
                MeetingDao.getInstance().modMeeting(meeting);
            }
        }else{
            userMeetingModel = UserMeetingDao.getInstance().getMeeting(meeting.getMeetingId(),
            		user.getUsercode(),
                    AppConfigure.MEETING_ROLE_COMMON);
        }
        if(userMeetingModel.getUmState() == AppConfigure.USER_MEETING_STATE_END){
            userMeetingModel
                    .setUmState(AppConfigure.USER_MEETING_STATE_REFRESH);
            UserMeetingDao.getInstance().modUserMeeting(userMeetingModel);
        }
    }catch(Exception e){
        logger.error("首页异常："+StackTraceUtil.getStackTrace(e));
    }
%>

<style type="text/css">
#loading-mask {
     position: absolute;
     top: 0;
     left: 0;
     width: 100%;
     height: 100%;
     background: lightblue;
     z-index: 1;
}
#loading {
     position: absolute;
     top: 40%;
     left: 45%;
     z-index: 2;
}
#loading span {
     background: url('../images/shared/large-loading.gif') no-repeat left center;
     padding: 10px 30px;
     display: block;
}

#loading-message{
    font-size:12px;
}

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
<script type="text/javascript" src="../js/jquery.contextmenu.r2.js"></script>
<script type="text/javascript" src="../js/jquery.form.js"></script>

<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>

<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/toolbar/toolbar.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/stringBuilder.js"></script>

<script type="text/javascript" src="../js/wz_jsgraphics.js"></script>

<script type="text/javascript">
var IMAGESPATH = '../js/zDialog/images/'; //图片路径配置
var toolbarPath = '../js/toolbar/';
Ext.BLANK_IMAGE_URL = '../images/s.gif';
var basePath = "<%=basePath%>";
var apachePath = "<%=apachePath%>";
var serverName = "<%=request.getServerName()%>";

var hostSid = "${hostmodel.sessionid}";
var hostname = "${hostmodel.username}";
var userSid = "${usermodel.sessionid}";
var username = "${usermodel.username}";
var meetingId = "${meeting.meetingId}";

var config_handup = "${ALLOWHANDUP}";
var config_desktopcontrol = "${ALLOWDESKTOPCONTROL}";
var config_whiteboard = "${ALLOWWHITEBOARD}";

var RED5_OFLADEMO = "rtmp://<%=request.getServerName()%>/oflaDemo";
var RED5_SOSAMPLE = "rtmp://<%=request.getServerName()%>/SOSample";
</script>

<!------ 页面加载中 ------>
<div id="loading-mask" align="center">
</div>
<div id="loading">
    <span id="loading-message">&nbsp;&nbsp;&nbsp;数据加载中，请耐心等待......</span>
</div>
<!------ 页面加载结束 ------>