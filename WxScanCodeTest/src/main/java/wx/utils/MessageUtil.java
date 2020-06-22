package wx.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;

import wx.entity.TextMessage;

/**
 * 
 * @Title: MessageUtil
 * @Package wx.utils
 * @Description: 微信服务器推送消息，xml转map
 * @author:songy
 * @date:2020年2月15日 上午10:55:30
 * @version V1.0
 */
public class MessageUtil {
	
	/**
	 * 
	 * @Description: 请求request数据xml转为map
	 * @author songy
	 * @date 2020年2月15日 上午10:56:04
	 */
	public static Map<String, String> xmlToMap(HttpServletRequest request) throws IOException{
        Map<String, String> map = new HashMap<String, String>();
        SAXReader reader = new SAXReader();
        
        InputStream ins = null;
        try {
            ins = request.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        Document doc = null;
        try {
            doc = reader.read(ins);
            Element root = doc.getRootElement();
            
            List<Element> list = root.elements();
            
            for (Element e : list) {
                map.put(e.getName(), e.getText());
            }
            
            return map;
        } catch (DocumentException e1) {
            e1.printStackTrace();
        }finally{
            ins.close();
        }
        
        return null;
    }
	
	/**
	 * 
	 * @Description: 实体对象转换为xml文件
	 * @author songy
	 * @date 2020年2月15日 上午11:21:32
	 */
	public static String textMessageToXml(TextMessage textMessage){
        XStream xstream = new XStream();
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }
}
