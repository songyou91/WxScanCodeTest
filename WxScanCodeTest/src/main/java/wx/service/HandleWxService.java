package wx.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;

import wx.entity.TextMessage;
import wx.utils.Constants;
import wx.utils.Global;
import wx.utils.HttpTools;
import wx.utils.JedisUtils;
import wx.utils.MessageUtil;

@Service("handleWxService")
@Transactional(readOnly = true)
public class HandleWxService {

	private static Logger logger = LoggerFactory.getLogger(HandleWxService.class);
	
	
	public String handlerWx(HttpServletRequest request) throws IOException{
		String respMessage = null;
		JedisUtils.del("msg");
		// xml请求解析
        Map<String, String> requestMap = MessageUtil.xmlToMap(request);
        logger.info("获取到微信服务器发送的报文内容为："+requestMap);
        // 发送方帐号（open_id）
        String fromUserName = requestMap.get("FromUserName");
        // 公众帐号
        String toUserName = requestMap.get("ToUserName");
        // 消息类型
        String msgType = requestMap.get("MsgType");
        // 消息内容
        String content = requestMap.get("Content");
        
        String eventMsg = null;
        logger.info("FromUserName is:" + fromUserName + ", ToUserName is:" + toUserName + ", MsgType is:" + msgType);

        if(Constants.REQ_MESSAGE_TYPE_EVENT.equals(msgType)){
        	 String eventType = requestMap.get("Event");// 事件类型
        	 logger.info("获取到event事件类型为："+eventType);
             // 订阅
             if (Constants.EVENT_TYPE_SUBSCRIBE.equals(eventType)) {
                 logger.info("获取到订阅事件");
                 eventMsg = "用户订阅事件";
                 String eventKey = requestMap.get("EventKey");
            	 logger.info("获取到EventKey："+eventKey);
                /* TextMessage text = new TextMessage();
                 text.setContent("欢迎关注，xxx");
                 text.setToUserName(fromUserName);
                 text.setFromUserName(toUserName);
                 text.setCreateTime(new Date().getTime() + "");
                 text.setMsgType(Constants.RESP_MESSAGE_TYPE_TEXT);
                 respMessage = MessageUtil.textMessageToXml(text);*/
             }else if(Constants.EVENT_TYPE_UNSUBSCRIBE.equals(eventType)){
            	 logger.info("获取到取消订阅事件");
            	 eventMsg = "用户取消订阅";
             }else if(Constants.EVENT_TYPE_SCAN.equals(eventType)){
            	 logger.info("获取到用户已关注事件");
            	 eventMsg = "用户已关注事件";
            	 String eventKey = requestMap.get("EventKey");
            	 logger.info("获取到EventKey："+eventKey);
             }
             String access_token = JedisUtils.get("access_token");
             if(StringUtils.isNotEmpty(access_token)){
            	 JSONObject userInfo = this.getUserInfo(access_token, fromUserName); 
            	 logger.info("获取到用户信息json串为："+userInfo);
            	 if(userInfo != null){
            		 String nickname = userInfo.get("nickname").toString();
            		 eventMsg = "当前操作用户为："+nickname+",当前操作为："+eventMsg;            		 
            	 }
             }
             JedisUtils.set("msg", eventMsg);
        }
        logger.info("返回的报文内容为："+respMessage);
        logger.info("结束接收到微信服务端推送消息，执行对应的业务逻辑----------------------");
        return respMessage;
	}
	
	/**
	 * 
	 * @Description: 根据access_token、openid获取用户信息
	 * @author songy
	 * @date 2020年2月16日 下午4:58:45
	 */
	public JSONObject getUserInfo(String accessToken,String openId){
		JSONObject userInfo = new JSONObject();
		if(StringUtils.isNotEmpty(accessToken) && StringUtils.isNotEmpty(openId)){
			logger.info("开始用户信息，当前accessToken:"+accessToken+",openId:"+openId);
			String userURL = Constants.GET_USER_URL.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openId);
			String result = HttpTools.doGet(userURL, null);
			if(StringUtils.isNotEmpty(result)){
				JSONObject obj = JSONObject.parseObject(result);
				//如果返回错误编码为42001，则说明access_token过期，需要重新发送请求到微信服务端获取access_token
				if(obj.get("errcode") != null && "42001".equals(obj.get("errcode"))){
					
				}
			}
			
			logger.info("获取用户信息，调用微信服务端接口返回结果为："+result);
			userInfo = JSONObject.parseObject(result);
		}
		return userInfo;
	}
	
	/**
	 * 
	 * @Description: 发送请求到微信服务端获取access_token
	 * @author songy
	 * @date 2020年6月12日 上午10:05:25
	 */
	public Map<String,String> getAccessToken(){
		Map<String,String> map = new HashMap<String,String>();
		String appID = Global.getConfig("appID");
        String appSecret = Global.getConfig("appSecret");
        logger.info("获取到appID："+appID+",appSecret:"+appSecret);
        String accessTokenUrl = Constants.ACCESS_TOKEN_URL.replace("APPID", appID).replace("APPSECRET", appSecret);
        String resultAccessToken = HttpTools.doGet(accessTokenUrl, null);
        if(StringUtils.isNotEmpty(resultAccessToken)){
        	JSONObject token = JSONObject.parseObject(resultAccessToken);
        	String access_token = token.get("access_token").toString();
        	String expires_in = token.get("expires_in").toString();
        	JedisUtils.set("access_token", access_token);
        	logger.info("获取到access_token："+access_token+",expires_in:"+expires_in);
        	map.put("access_token", access_token);
        	map.put("expires_in", expires_in);
        }
        return map;
	}
}
