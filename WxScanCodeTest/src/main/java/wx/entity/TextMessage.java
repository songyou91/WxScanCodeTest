package wx.entity;

/**
 * 
 * @Title: TextMessage
 * @Package wx.entity
 * @Description: 微信推送消息响应内容实体
 * @author:songy
 * @date:2020年2月15日 上午11:20:15
 * @version V1.0
 */
public class TextMessage {

	private String content;
	private String toUserName;
	private String fromUserName;
	private String createTime;
	private String msgType;
	
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getMsgType() {
		return msgType;
	}
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
}
