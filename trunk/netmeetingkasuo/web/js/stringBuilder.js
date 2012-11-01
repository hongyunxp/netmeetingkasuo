/**
 * ���������ַ����Ķ���
 * 
 * @return
 */
var stringBuilder = function() {
	this.strArr = new Array();
	this.Description = "�����ַ���";
}

/**
 * 
 */
stringBuilder.prototype.append = function(str) {
	this.strArr.push(str);
	return this;
}

/**
 * 
 */
stringBuilder.prototype.appendFormat = function(str) {
	var len = arguments.length;
	var pattern = "";
	for ( var i = 1; i < len; i++) {
		pattern = new RegExp("\\{" + (i - 1).toString() + "\\}", "g");
		str = str.replace(pattern, arguments[i]);
	}
	this.append(str);
}

/**
 * ��ȡ����������ַ���
 */
stringBuilder.prototype.toString = function() {
	return this.strArr.join("");
}

/**
 * �������������ַ���
 */
stringBuilder.prototype.empty = function() {
	this.strArr.length = 0;
}

// ʹ�÷���
// var strBuilder = new stringBuilder();
// strBuilder.append("laone say:");
// strBuilder.appendFormat("hello {0}", "world");
// strBuilder.append("!");

// ���:laone say:hello world!
