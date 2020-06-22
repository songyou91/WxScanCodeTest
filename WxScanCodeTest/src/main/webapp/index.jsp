<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
	<title>微信扫码关注公众好测试</title>
	<meta name="decorator" content="default"/>
	<script src="./static/jquery/jquery-1.9.1.min.js" type="text/javascript"></script>
</head>
<body>
<h2>微信扫码登录</h2>
<a href="javascript:void(0);" onclick="getqrcode()">获取二维码</a>
<div>用户扫码后的事件信息：<span id="msg" style="color:red"></span></div>
<div>
<img id="qrcode" style="height: 300px;width: 300px;margin-left: 500px;margin-top: 100px;">
</div>

<script type="text/javascript">
//获取二维码   /wxtest/sign/qrcode
function getqrcode(){
	$.ajax({
       type: 'POST', 
       url: "/WxScanCodeTest/sign/qrcode.do",
       success: function(data) {
    	   if(data != null){
    		   var result = JSON.parse(data);
    		   console.log(result);
    		   if(result.msgcode == '0'){
    		   	  $("#msg").html(result.message);    			   
    		   }else{
    			 //gQEI7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyYkFldHR0N3Q4bF8xcW13Nmh1MWsAAgR2hEZeAwQgHAAA
        		 var showQrcode = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket="+result.message;
    	    	 $("#qrcode").attr("src",showQrcode);
    		   }
    	   }else{
    		   $("#msg").html("获取微信二维码失败,请稍后重试");   
    	   }
       },
       error: function(XMLHttpRequest, textStatus, errorThrow) {
			alert("获取二维码出错!");
		}
	});
}
var t2 = window.setInterval(function() {
	$.ajax({
       type: 'POST', 
       url: "/WxScanCodeTest/sign/getEventMsg.do",
       success: function(data) {
    	   console.log(data);
    	   if(data != null){
    		   $("#msg").html(data);
    		   testMsg = 1;
    	   }
       },
       error: function(XMLHttpRequest, textStatus, errorThrow) {
			console.log("获取二维码出错!");
		}
	});
},2000)
</script>	
</body>
</html>