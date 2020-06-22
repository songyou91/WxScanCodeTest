package wx.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import wx.service.HandleWxService;
import wx.utils.Constants;
import wx.utils.Global;
import wx.utils.HttpTools;
import wx.utils.JedisUtils;
import wx.utils.ShaUtils;

@Controller
@RequestMapping("/sign")
public class GetWxSginController {

	private  Logger logger = LoggerFactory.getLogger(GetWxSginController.class);

	@Autowired
	public HandleWxService handleWxService;
	/**
	 * 
	 * @Description: 校验微信服务端推送消息的请求来源，接收推送消息，对特定消息事件进行处理
	 * @author songy
	 * @date 2020年2月15日 下午2:21:06
	 */
    @ResponseBody
    @RequestMapping(value = "/testwx")
    public String getWxUserInfo(HttpServletRequest request,
                               @RequestParam(required = false) String echostr,
                                @RequestParam(required = false) String signature,
                                @RequestParam(required = false) String timestamp,
                                @RequestParam(required =false) String nonce
    ) {
        try {
        	logger.info("进入校验请求来源是否为微信方法-------------------------------");
    		String[] arr = new String[] { Constants.TOKEN, timestamp, nonce };  
            // 将token、timestamp、nonce三个参数进行字典序排序  
            Arrays.sort(arr);  
            // 将三个参数字符串拼接成一个字符串进行sha1加密  
            String tmpStr = ShaUtils.encode(arr[0] + arr[1] + arr[2]);  
            // 将sha1加密后的字符串可与signature对比，标识该请求来源于微信  
            boolean checkResult = tmpStr != null ? tmpStr.equals(signature.toUpperCase()) : false;
            logger.info("校验请求是否来自微信服务端："+checkResult);
            if(checkResult){
            	 logger.info("开始接收到微信服务端推送消息，执行对应的业务逻辑----------------------");
         		return handleWxService.handlerWx(request);
            }
    		return null;
        } catch (Exception e) {
            logger.info("测试微信公众号的接口配置信息发生异常：", e);
            return "错误！！！";
        }
    }
    /**
     * 
     * @Description: 获取二维码图片
     * @author songy
     * @date 2020年2月15日 下午2:20:04
     */
    @ResponseBody
    @RequestMapping(value = "/qrcode")
    public String getWxQrcode(HttpServletRequest request) {
    	JSONObject result = new JSONObject();
    	String ticket = null;
    	//开始获取微信二维码图片路径
        logger.info("开始获取微信二维码=========================");
        Long scene_id = System.currentTimeMillis();
        Map<String,String> map = handleWxService.getAccessToken(); 
    	String access_token = map.get("access_token");
    	String expires_in = map.get("expires_in");
    	if(StringUtils.isEmpty(access_token) || StringUtils.isEmpty(expires_in)){
    		result.put("msgcode", "0");
    		result.put("message", "获取access_token失败，请稍后重试");
    		return result.toString();
    	}
    	JSONObject objMap = new JSONObject();
    	objMap.put("expire_seconds", expires_in);
    	objMap.put("action_name", Constants.QR_SCENE);
    	JSONObject sceneIdObj = new JSONObject();
    	sceneIdObj.put("scene_id", scene_id);
    	JSONObject sceneObj = new JSONObject();
    	sceneObj.put("scene", sceneIdObj);
    	objMap.put("action_info", sceneObj);
    	logger.info("获取ticket的请求参数为："+objMap);
    	String ticketUrl = Constants.TICKET_URL.replace("ACCESS_TOKEN", access_token);
    	logger.info("ticketUrl:"+ticketUrl);
    	String ticketResult = HttpTools.doPostStr(ticketUrl, objMap.toString());
    	if(StringUtils.isNotEmpty(ticketResult)){
    		JSONObject object = JSONObject.parseObject(ticketResult);
    		ticket = object.get("ticket").toString();
    		logger.info("获取到ticket："+ticket);
    		JedisUtils.set("scendId",scene_id.toString());
    	}else{
    		result.put("msgcode", "0");
    		result.put("message", "获取微信二维码ticket失败，请稍后重试");
    		return result.toString();
    	}
    	result.put("msgcode", "1");
		result.put("message", ticket);
        logger.info("结束获取微信二维码=========================");
        return result.toString();
    }
    
    /**
     * 
     * @Description: 定时获取微信扫描二维码事件信息
     * @author songy
     * @date 2020年2月15日 下午2:19:30
     */
    @ResponseBody
    @RequestMapping(value = "/getEventMsg")
    public String getEventMsg(HttpServletRequest request) {
        return JedisUtils.get("msg");
    }
}
