package wx.utils;

public class Constants {

	//获取access_token的微信服务器地址
	public static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	//获取ticket微信服务器地址
	public static String TICKET_URL = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=ACCESS_TOKEN";
	//获取二维码图片的微信服务器地址
	public static String QRCODE_URL = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
	
	//获取用户信息根据openid
	public static String GET_USER_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID";
	//二维码类型，QR_SCENE：临时二维码，QR_LIMIT_SCENE：永久二维码
	public static String QR_SCENE = "QR_SCENE";
	public static String QR_LIMIT_SCENE = "QR_LIMIT_SCENE";
	
	public static String TOKEN = "WX_TEST";
	
	/** 
     * 返回消息类型：文本 
     */  
    public static final String RESP_MESSAGE_TYPE_TEXT = "text";  
  
    /** 
     * 返回消息类型：音乐 
     */  
    public static final String RESP_MESSAGE_TYPE_MUSIC = "music";  
  
    /** 
     * 返回消息类型：图文 
     */  
    public static final String RESP_MESSAGE_TYPE_NEWS = "news";  
  
    /** 
     * 请求消息类型：文本 
     */  
    public static final String REQ_MESSAGE_TYPE_TEXT = "text";  
  
    /** 
     * 请求消息类型：图片 
     */  
    public static final String REQ_MESSAGE_TYPE_IMAGE = "image";  
  
    /** 
     * 请求消息类型：链接 
     */  
    public static final String REQ_MESSAGE_TYPE_LINK = "link";  
  
    /** 
     * 请求消息类型：地理位置 
     */  
    public static final String REQ_MESSAGE_TYPE_LOCATION = "location";  
  
    /** 
     * 请求消息类型：音频 
     */  
    public static final String REQ_MESSAGE_TYPE_VOICE = "voice";  
  
    /** 
     * 请求消息类型：推送 
     */  
    public static final String REQ_MESSAGE_TYPE_EVENT = "event";  
  
    /** 
     * 事件类型：subscribe(订阅) 
     */  
    public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";  
  
    /** 
     * 事件类型：unsubscribe(取消订阅) 
     */  
    public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";  
  
    /** 
     * 事件类型：CLICK(自定义菜单点击事件) 
     */  
    public static final String EVENT_TYPE_CLICK = "CLICK";
    /** 
     * 事件类型：SCAN(用户已关注事件) 
     */  
    public static final String EVENT_TYPE_SCAN = "SCAN";
    
}
