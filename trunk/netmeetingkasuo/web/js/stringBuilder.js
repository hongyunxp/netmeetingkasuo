/**
 * 定义连接字符串的对象
 * 
 * @return
 */
var stringBuilder = function() {
	this.strArr = new Array();
	this.Description = "连接字符串";
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
 * 获取连接器里的字符串
 */
stringBuilder.prototype.toString = function() {
	return this.strArr.join("");
}

/**
 * 清空连接器里的字符串
 */
stringBuilder.prototype.empty = function() {
	this.strArr.length = 0;
}

// 使用方法
// var strBuilder = new stringBuilder();
// strBuilder.append("laone say:");
// strBuilder.appendFormat("hello {0}", "world");
// strBuilder.append("!");

// 输出:laone say:hello world!
