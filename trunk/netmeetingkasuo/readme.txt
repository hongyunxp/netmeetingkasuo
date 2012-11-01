2011-03-22
1.修复和优化核心功能
2.论文撰写，核心技术实现（文档/白板共享，远程桌面/协助，语音会议），参考论文，搭建论文框架



2011-03-21
1.适当优化
2.加入视频播放监控，加入视图查询
3,管理端搜索功能

4.配置功能
5.退出会议，以及退出时内存的清理（各个模块）
6.关闭会议

论文开始撰写

2011-03-18
1.媒体会议采用在red5下开发插件的方式发布
2.flex与red5服务端交互，从而控制音频和视频


2011-03-15
1测试red5 oflaDemo成功
2下载mediaplayer包
测试ok
<script type='text/javascript'>
  var so = new SWFObject('player.swf','ply','470','320','9','#000000');
  so.addParam('allowfullscreen','true');
  so.addParam('allowscriptaccess','always');
  so.addParam('wmode','opaque');
  so.addVariable('file','avatar.flv');
  so.addVariable('streamer','rtmp://192.168.0.52/oflaDemo&id=avatar.flv');
  so.write('mediaspace');
</script>


2011-02-28
1.把通过Applet方式的远程桌面及远程协助调通
2.调试Flash方式的
3.语音视频部分


2011-02-26
1.文本切换问题js
2.远程桌面/协助
3.文件分发


2011-02-25
1.多标签问题+文档与白板整合
2.画图工具栏优化
3.文档分发，下载进度（每个用户下载时候，定时请求下载进度服务）
4.视频上传
5.会议详细信息
6.桌面共享


2011-02-21
1.通过wz_jsgraphics画图，将点传到后台，后台通过JMagick画图，返回页面image
        每次生成的图片都是单独一张图片，每张图片的ID都不同，每一张都比前一张多一笔，存入Map
        保存时候将北京图片和文档图片合成下载
   
        文字在页面上只显示文字，保存时候，将文字和图片合成，当文档放大/缩小时候，文字的大小比例和位置也和文档同步
        白板保存时候，将图片改为白色
        生成一张没有背景色的图片
        
         
convert -size 300x200 fill none -stroke black -draw "line 20,50,90,10" c:/upload/test.gif c:/upload/est1.gif
convert -draw "fill none stroke red polyline 6.6,7.7  25.0,75.0 42.2,85.4 75.8,94.7 51.5,39.3  92.5,66.6" c:/upload/background1.png c:/upload/backgroundasdfasdf.png

    写字乱码

2011-02-18
1.完成用户列表、聊天开发
2.完善登陆、退出、底部计时器、会议定时器、异常会议清理
3.文档共享
4.白板共享
5.桌面共享/远程协助
6.语音视频共享


2011-01-19
服务器启动顺序
1.配置各项参数
2.启动GUI
3.启动数据库
4.启动WebServer
5.启动Apache