<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<html>
<head>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>欢迎页面</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
<style type="text/css">
.topcontainer{
    width:100%;
    height:270px;
    margin:20 20 10;
}

.topcontainer .tcLeft{
    float:left;
    width:50%;
    height:270px;
    margin:0;
}

.topcontainer .tcLeft .invitedStyle{
    height:265px;
    margin-right:8px;
    border:solid 1px #D6E3F2;
}

.topcontainer .tcLeft .attendedStyle{
    height:265px;
    margin-left:8px;
    border:solid 1px #D6E3F2;
}

.bottomcontainer{
    width:100%;
    height:290px;
    border:solid 1px #D6E3F2;
    margin:10 20 10;
}

.panel_head{
    background-image:url(../images/toolbar/bg.gif);
    height:25px;
    line-height: 25px;
    font-size:13px;
    font-weight:bold;
    color:#006699;
    padding-left:10px;
}

.panel_body{
    padding:5px;
}

.panel_body table{
    font-size:13px;
    table-layout:fixed;
    border-bottom:dotted 1px #006699;
    width:98%;
}

.panel_body table .tdTtile{
    white-space:nowrap;
    overflow:hidden;
    text-overflow:ellipsis;
}
</style>
</head>
<body style="background:#fff;overflow: hidden;">

<div class="topcontainer">
    <div class="tcLeft">
        <div class="invitedStyle">
            <div class="panel_head">
                <div style="float:left">被邀请的会议</div>
                <div style="float:right;">
                    <img alt="" src="../images/netmeeting/netmeeting_refresh_24.png" align="absmiddle" width="20px">&nbsp;
                    <a href="">更多...</a>
                </div>
            </div>
            <div class="panel_body">
                 <c:forEach var="hm" items="${umInvitedList}" varStatus="status">
                    <table width="100%">
                        <tr>
                            <td class="tdTtile">${status.count}.&nbsp;&nbsp;${hm.meetingModel.subject }</td>
                            <td width="150px">${hm.meetingModel.begintime }</td>
                            <td align="right" width="40px">
                                <a href="#" onclick="detail(${hm.meetingModel.meetingId})">进入</a>
                            </td>
                        </tr>
                    </table>
                </c:forEach>
            </div>
        </div>
    </div>
    <div class="tcLeft">
        <div class="attendedStyle">
            <div class="panel_head">
                <div style="float:left">已参加的会议</div>
                <div style="float:right;">
                    <img alt="" src="../images/netmeeting/netmeeting_refresh_24.png" align="absmiddle" width="20px">&nbsp;
                    <a href="">更多...</a>
                </div>
            </div>
            <div class="panel_body">
                <c:forEach var="hm" items="${umHisList}" varStatus="status">
                    <table width="100%">
                        <tr>
                            <td class="tdTtile">${status.count}.&nbsp;&nbsp;${hm.meetingModel.subject }</td>
                            <td width="150px">${hm.meetingModel.begintime }</td>
                            <td align="right" width="40px">
                                <a href="#" onclick="detail(${hm.meetingModel.meetingId})">进入</a>
                            </td>
                        </tr>
                    </table>
                </c:forEach>
            </div>
        </div>
    </div>
</div>
<div class="bottomcontainer">
    <div class="panel_head">
        <div style="float:left">正在进行的会议</div>
        <div style="float:right;">
            <img alt="" src="../images/netmeeting/netmeeting_refresh_24.png" align="absmiddle" width="20px">&nbsp;
            <a href="">更多...</a>
        </div>
    </div>
    <div class="panel_body">
         <c:forEach var="hm" items="${umOnlineList}" varStatus="status">
              <table width="100%">
                  <tr>
                      <td class="tdTtile">${status.count}.&nbsp;&nbsp;${hm.meetingModel.subject }</td>
                      <td width="100px" align="center">
                          ${hm.userModel.username }
                      </td>
                      <td width="150px">${hm.meetingModel.begintime }</td>
                      <td align="right" width="40px">
                          <a href="#" onclick="detail(${hm.meetingModel.meetingId})">进入</a>
                      </td>
                  </tr>
              </table>
          </c:forEach>
    </div>
</div>

<img alt="网络会议" src="../images/elearn_logo.png" style="position: absolute;right: 5px;bottom: 5px;">
<script type="text/javascript">
var usercode = "${usermodel.usercode}";
function detail(meetingid){
	parent.toPage("../manage/meeting?oper=meetingDetail&usercode="+usercode+"&meetingid="+meetingid);
}
</script>
</body>
</html>