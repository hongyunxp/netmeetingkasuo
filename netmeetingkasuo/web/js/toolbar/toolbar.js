

$(function() {
	loadCss("toolbar.css");
	// ��ʼ��ÿ��������
	$('.my_tool_bar ').each(function() { 
		// ��ʼ��ÿ����ť
		$(this).children().each(function() {
			$(this).addClass('toolbar_btn');
			$(this).bind('mouseover', function() {
				$(this).addClass('mouser_over');
			});
			$(this).bind('mouseout', function() {
				$(this).removeClass('mouser_over');
			});
		});
	});
	
	var text = '';
	$(".btnAdd").each(function() {
		text = this.innerHTML;
		this.innerHTML = '<img src="' + toolbarPath + 'images/add.png" alt="" /><div>' + text + '</div>';
	});
	$(".btnEdit").each(function() {
		text = this.innerHTML;
		this.innerHTML = '<img src="' + toolbarPath + 'images/edit.png" alt="" /><div>' + text + '</div>';
	});
	$(".btnDel").each(function() {
		text = this.innerHTML;
		this.innerHTML = '<img src="' + toolbarPath + 'images/delete.png" alt="" /><div>' + text + '</div>';
	});
	$(".btnCancel").each(function() {
		text = this.innerHTML;
		this.innerHTML = '<img src="' + toolbarPath + 'images/cancel.png" alt="" /><div>' + text + '</div>';
	});
	$(".btnQuery").each(function() {
		text = this.innerHTML;
		this.innerHTML = '<img src="' + toolbarPath + 'images/query.png" alt="" /><div>' + text + '</div>';
	});
});

function loadCss(file) { 
	var cssTag = document.getElementById('loadCss'); 
	var head = document.getElementsByTagName('head').item(0); 
	if(cssTag)
		return; 
	cssTag = document.createElement('link'); 
	cssTag.href = toolbarPath + file; 
	cssTag.rel = 'stylesheet'; 
	cssTag.type = 'text/css'; 
	cssTag.id = 'loadCss'; 
	head.appendChild(cssTag); 
} 
