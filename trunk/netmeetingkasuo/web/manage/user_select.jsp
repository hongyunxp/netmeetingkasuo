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
<title>�û�ע��</title>
<link rel="stylesheet" type="text/css" href="../css/common.css" />
</head>
<body >
<table width="97%" style="margin-left:10px;border-bottom:dotted 1px #006699;font-size:13px;">
    <tr>
        <td>
            <a href="#" onclick="selectAll(true)">ȫѡ</a>/<a href="#" onclick="selectAll(false)">ȡ��</a>&nbsp;
            <input type="text" id="username" name="username"/>
			<input type="button" value="�� ��" onclick="findUsername()"/>
        </td>
    </tr>
</table>
<div style="width:399px;height:350px;overflow:auto;">
	<c:forEach var="user" items="${userlist}">
	    <table width="100%" style="border-bottom:dotted 1px #006699;font-size:13px;">
	        <tr>
	            <td width="150px">
	                <input type="checkbox" id="usersel" name="usersel" value="${user.usercode }"/> ${user.username }
	            </td>
	            <td>${user.useremail }</td>
	        </tr>
	    </table>
	</c:forEach>  
</div>

<jsp:include page="common.jsp" flush="true"></jsp:include>
<script type="text/javascript">
function selectAll(b){
	var input = document.getElementsByTagName("input");
    for (var i=0;i<input.length ;i++ ){
        if(input[i].type=="checkbox")
            input[i].checked = b;
    }
    if(b){
        $("#checkSpan").html("ȡ��");
    }else{
    	$("#checkSpan").html("ȫѡ");
    }
}

function findUsername(){
    var username = encodeURI($("#username").val());
    window.location="../manage/user?oper=findUsers&username="+username;
}

</script>
</body>
</html>