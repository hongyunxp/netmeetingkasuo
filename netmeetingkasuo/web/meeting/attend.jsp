<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%
    String path = request.getContextPath()+"/";
	Object obj = request.getAttribute("error");
	String error = "";
	if(obj != null){
	    error = (String)obj; 
	}
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>加入会议</title>
<link type="text/css" rel="stylesheet"  href="css/style_login.css"/>
<script type="text/javascript" src="../js/jquery-1.3.2.min.js"></script>
<script type="text/javascript" src="../js/jquery.form.js"></script>
<script type="text/javascript" src="../js/json.js"></script>
<script language="JavaScript">
var error = "<%=error%>";
$(function(){    
    if(error != ""){
        $("#backMsg").html(error);
        return;
    }
    $('#attendFrm').ajaxForm(function(data) {
        var json = JSON.parse(data);
        var ret = json["ret"];
        var text = json["text"];
        if (ret == '1'){
        	$("#backMsg").html(text);
        	return;
        }else{
            window.location="meeting/index.jsp";
        }
    });
});

function correctPNG(){
    var arVersion = navigator.appVersion.split("MSIE") 
    var version = parseFloat(arVersion[1]) 
    if ((version >= 5.5) && (document.body.filters)) 
    { 
       for(var a=0; a<document.images.length; a++) 
       { 
          var img = document.images[a] 
          var imgName = img.src.toUpperCase() 
          if (imgName.substring(imgName.length-3, imgName.length) == "PNG") 
          { 
             var imgID = (img.id) ? "id='" + img.id + "' " : "" 
             var imgClass = (img.className) ? "class='" + img.className + "' " : "" 
             var imgTitle = (img.title) ? "title='" + img.title + "' " : "title='" + img.alt + "' " 
             var imgStyle = "display:inline-block;" + img.style.cssText 
             if (img.align == "left") imgStyle = "float:left;" + imgStyle 
             if (img.align == "right") imgStyle = "float:right;" + imgStyle 
             if (img.parentElement.href) imgStyle = "cursor:hand;" + imgStyle 
             var strNewHTML = "<span " + imgID + imgClass + imgTitle 
             + " style=\"" + "width:" + img.width + "px; height:" + img.height + "px;" + imgStyle + ";" 
             + "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader" 
             + "(src=\'" + img.src + "\', sizingMethod='scale');\"></span>" 
             img.outerHTML = strNewHTML 
             a = a-1 
          } 
       } 
    }     
} 

function MM_preloadImages() { //v3.0
      var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
        var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
        if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
    }
    function MM_swapImgRestore() { //v3.0
      var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
    }
    function MM_findObj(n, d) { //v4.01
      var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
        d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
      if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
      for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
      if(!x && d.getElementById) x=d.getElementById(n); return x;
    }

    function MM_swapImage() { //v3.0
      var i,j=0,x,a=MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
       if ((x=MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
    }

    function login(){
        var usercode = $('#usercode').val();
        var password = $('#password').val();
        if(usercode == null || usercode == ''){
        	$("#backMsg").html("用户账号不能为空！");
            return false;
        }
        if(password == null || password == ''){
        	$("#backMsg").html("会议密码不能为空！");
            return false;
        }
        $("#attendFrm").submit();
        
    }
    
    function keydownEvent(event){
    	if(event.keyCode == 13) {    
            login();  
        }
    }

</script>
</head>

<body scroll="no" onkeydown="keydownEvent(event)" onload="correctPNG()">
<div id="container" style="margin-top: 150px;">
	<div id="main">
		<form id="attendFrm" method="post" name="attendFrm" action="meeting?oper=attend">
		    <div id="logo"><img src="images/login/logo_07.png"></div>
			<div id="input">
				<table width="270" border="0" cellspacing="0" cellpadding="2" style="font-size:12px;">
				    <tr>
				        <td width="61">用户账号：</td>
				        <td width="109"><input type="text" name="usercode" id="usercode"
				            style="width: 100px; height: 20px;" class="input_out"
				            onfocus="this.className='input_on';this.onmouseout=''"
				            onblur="this.className='input_off';this.onmouseout=function(){this.className='input_out'};"
				            onmousemove="this.className='input_move'"
				            onmouseout="this.className='input_out'" tabindex="1"/></td>
				        <td width="88" rowspan="2"><a href="#"
				            onMouseOut="MM_swapImgRestore()"
				            onMouseOver="MM_swapImage('Image2','','images/login/btn_on_06.gif',1)" onclick="login()"><img
				            src="images/login/btn_06.gif" alt="登录" name="Image2" width="54"
				            height="53" border="0"></a></td>
				    </tr>
				    <tr>
				        <td>会议密码：</td>
				        <td><input type="password" name="password" id="password"
				            style="width: 100px; height: 20px;" class="input_out"
				            onfocus="this.className='input_on';this.onmouseout=''"
				            onblur="this.className='input_off';this.onmouseout=function(){this.className='input_out'};"
				            onmousemove="this.className='input_move'"
				            onmouseout="this.className='input_out'"  tabindex="2"//></td>
				    </tr>
				</table>
			    <div id="backMsg" style="color:red"></div>
			</div>
		</form>
	</div>
	<div id="banquan">Copyright &copy; 2011 新太科技. 版权所有.</div>
</div>
</body>
</html>
