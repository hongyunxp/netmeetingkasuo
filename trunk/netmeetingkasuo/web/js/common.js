/**
 * 获取时间串
 * 
 * @return
 */
function getTime(){
	return new Date().getTime();
}

rnd.today=new Date();
rnd.seed=rnd.today.getTime();

/**
 * 随机数
 * 
 * @return
 */
function rnd() {
　　　　rnd.seed = (rnd.seed*9301+49297) % 233280;
　　　　return rnd.seed/(233280.0);
};

/**
 * 生成随机数
 * 
 * @param number
 * @return
 */
function rand(number) {
　　　　return Math.ceil(rnd()*number);
};

String.prototype.startWith=function(str){     
	  var reg=new RegExp("^"+str);     
	  return reg.test(this);        
	} 

String.prototype.endWith=function(str){     
	  var reg=new RegExp(str+"$");     
	  return reg.test(this);        
	} 



