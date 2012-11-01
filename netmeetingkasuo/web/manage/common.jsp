<%@ page language="java" contentType="text/html; charset=GBK"
    pageEncoding="GBK"%>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+path+"/console/";
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
<script type="text/javascript" src="../js/jquery.form.js"></script>

<script type="text/javascript" src="../js/zDialog/zDrag.js"></script>
<script type="text/javascript" src="../js/zDialog/zDialog.js"></script>

<script type="text/javascript" src="../js/json.js"></script>
<script type="text/javascript" src="../js/toolbar/toolbar.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<script type="text/javascript" src="../js/stringBuilder.js"></script>
<script type="text/javascript">
var IMAGESPATH = '../js/zDialog/images/'; //ͼƬ·������
var toolbarPath = '../js/toolbar/';
Ext.BLANK_IMAGE_URL = '../images/s.gif';
var basePath = "<%=basePath%>";
Ext.onReady(function() {
    var loadingMask = Ext.get('loading-mask');
    var loading = Ext.get('loading');
    loading.fadeOut( {
        duration : 0.2,
        remove : true
    });
    loadingMask.setOpacity(0.9);
    loadingMask.shift( {
        xy : loading.getXY(),
        width : loading.getWidth(),
        height : loading.getHeight(),
        remove : true,
        duration : 1,
        opacity : 0.1,
        easing : 'bounceOut'
    });
    Ext.extmsg.init();
    Ext.QuickTips.init();
});

/**
 * ���½ǵ�����ʾ��
 * 
 * @param msg
 * @return
 */
function showmessager(msgs) {
    //Ext.extmsg.msg("ϵͳ��ʾ", msgs, '');
    new Ext.ux.ToastWindow( {
        title : '<img border="0" src="../images/netmeeting/info.png" align="absmiddle"/>&nbsp;ϵͳ��ʾ',
        html : msgs
    }).show(document);
}
</script>

<!------ ҳ������� ------>
<div id="loading-mask" align="center">
</div>
<div id="loading">
    <span id="loading-message">&nbsp;&nbsp;&nbsp;���ݼ����У������ĵȴ�......</span>
</div>
<!------ ҳ����ؽ��� ------>