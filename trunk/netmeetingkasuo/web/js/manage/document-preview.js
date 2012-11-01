/**
 * 判断是否为IE浏览器
 * 
 * @return
 */
function isIE(){
	var ua = navigator.userAgent.toLowerCase();
	if(ua.match(/msie ([\d.]+)/)){
		return true;
	}else{
		return false;
	}
}

/**
 * 处理翻页
 * 
 * @param param
 * @return
 */
function handlepage(param){
	var seq = parseInt($("#seq").val());
	var total = parseInt($("#total").val());
	switch(param){
	case 'f':
		seq = 1;
		break;
	case 'l':
		seq = total;
		break;
	case 'n':
		seq++;
		if(seq == total+1)
			seq = total;
		break;
	case 'p':
		seq--;
		if(seq == 0)
			seq = 1;
		break;
	}
	
	$("#seq").val(seq);
	$("#total").val(total);
	$("#seqpage").attr("innerHTML",seq);
	$("#totalpage").attr("innerHTML",total);
	
	var imgurl = "../manage/document?oper=imgPreview&fileid="+fileid+"&seq="+seq;
	loadImage(imgurl);
	
}

/**
 * 获取图片
 * 
 * @param url
 * @return
 */
function loadImage(url){
	var image = new Image();   
    image.src = url;
	if (image.complete) {
		imageLoaded(image, url);
		return;
	}
	image.onload = function() {
		imageLoaded(image, url);
	}
	$("#showimg").attr("innerHTML","<span style='color:red'>文档正在加载中......</span>");
}

/**
 * 图片加载完成
 * 
 * @param image
 * @param path
 * @return
 */
function imageLoaded(image, path) {
	var widthf = parseFloat(image.width);
	var heightf = parseFloat(image.height);
	var rate = widthf/heightf;
	var imgwidth = $("#showimg").width() - 20;
	var imgheight = parseInt(imgwidth/rate);
	var html = "";
	if(isIE()){
		html = "<v:image id='fileimage' src='"+path+"' style='width:"+imgwidth+"px;height:"+imgheight+"px;display:block;' onmousedown='return false;' onmousemove='return false;' onmouseup='return false;'/>";
	}else{
		html = "<img class='highqual' alt='图片' id='fileimage' src='"+path+"' width='"+imgwidth+"' height='"+imgheight+"' onmousedown='return false;' onmousemove='return false;' onmouseup='return false;'>";
	}
	$("#showimg").attr("innerHTML",html);
}

/**
 * 放大
 * 
 * @return
 */
function enlarge(){
	var imghf = parseFloat($("#fileimage").css('height'));
	var imgwf = parseFloat($("#fileimage").css('width'));
	var rate = imgwf/imghf;
	var imgwidth = parseInt($("#fileimage").css('width'))+80;
	var imgheight = parseInt(imgwidth/rate);
	$("#fileimage").css('height', imgheight);
	$("#fileimage").css('width', imgwidth);
	var offsetWidth = document.getElementById("showimg").offsetWidth;
	document.getElementById("showimg").scrollLeft = (imgwidth - offsetWidth)/2;
}

/**
 * 缩小
 * 
 * @return
 */
function shrink(){
	var imghf = parseFloat($("#fileimage").css('height'));
	var imgwf = parseFloat($("#fileimage").css('width'));
	var rate = imgwf/imghf;
	var imgwidth = parseInt($("#fileimage").css('width'))-80;
	var imgheight = parseInt(imgwidth/rate);
	$("#fileimage").css('height', imgheight);
	$("#fileimage").css('width', imgwidth);
	var offsetWidth = document.getElementById("showimg").offsetWidth;
	document.getElementById("showimg").scrollLeft = (imgwidth - offsetWidth)/2;
}

/**
 * 初始化
 */
function init(){
	var height = document.body.clientHeight - 50;
	$("#showimg").css('height', height);
	var url = "../manage/document?oper=imgPreview&fileid="+fileid+"&seq=1";
	loadImage(url);
}