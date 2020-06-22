package wx.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


/**
 * 基于 httpclient 4.3.1版本的 http工具类
 * @author songy
 *
 */
public class HttpTools {

    private static final CloseableHttpClient httpClient;

    public static final String CHARSET = "UTF-8";
    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(60000).setSocketTimeout(15000).build();
        httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    public static String doGet(String url, Map<String, String> params){
        return doGet(url, params,CHARSET);
    }
    public static String doPost(String url, Map<String, String> params){
        return doPost(url, params, CHARSET);
    }
    
    public static String doPostStr(String url, String params){
        return post(url, params, CHARSET);
    }
    /**
     * HTTP Get 获取内容
     * @param url  请求的url地址 ?之前的地址
     * @param params	请求的参数
     * @param charset	编码格式
     * @return	页面内容
     * 
	 * 微信端响应码在200-300之间，我们认为请求成功，正确应答
	 * 200 OK 一切正常，对GET和POST请求的应答文档跟在后面。 
		201 Created 服务器已经创建了文档，Location头给出了它的URL。 
		202 Accepted 已经接受请求，但处理尚未完成。 
		203 Non-Authoritative Information 文档已经正常地返回，但一些应答头可能不正确，因为使用的是文档的拷贝 
		204 No Content 没有新文档，浏览器应该继续显示原来的文档。如果用户定期地刷新页面，而Servlet可以确定用户文档足够新，这个状态代码是很有用的。 
		205 Reset Content 没有新的内容，但浏览器应该重置它所显示的内容。用来强制浏览器清除表单输入内容 
		206 Partial Content 客户发送了一个带有Range头的GET请求，服务器完成了它。 
		300 Multiple Choices 客户请求的文档可以在多个位置找到，这些位置已经在返回的文档内列出。如果服务器要提出优先选择，则应该在Location应答头指明。 
			 
     */
    public static String doGet(String url,Map<String,String> params,String charset){
    	if(StringUtils.isBlank(url)){
    		return null;
    	}
    	try {
    		if(params != null && !params.isEmpty()){
    			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
    			for(Map.Entry<String,String> entry : params.entrySet()){
    				String value = entry.getValue();
    				if(value != null){
    					pairs.add(new BasicNameValuePair(entry.getKey(),value));
    				}
    			}
    			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
    		}
    		HttpGet httpGet = new HttpGet(url);
    		CloseableHttpResponse response = httpClient.execute(httpGet);
    		int statusCode = response.getStatusLine().getStatusCode();
    		if (statusCode != 200) {
    			httpGet.abort();
    			throw new RuntimeException("HttpClient,error status code :" + statusCode);
    		}
    		HttpEntity entity = response.getEntity();
    		String result = null;
    		if (entity != null){
    			result = EntityUtils.toString(entity, "utf-8");
    		}
    		EntityUtils.consume(entity);
    		response.close();
    		return result;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    
    /** 
     * 调用 API 
     *  
     * @param parameters 
     * @return 
     */  
    public static String post(String url,String parameters,String charset) {
    	if(StringUtils.isBlank(url)){
    		return null;
    	}
        HttpPost httpPost = new HttpPost(url);
        try {
        	if (parameters != null  && !"".equals(parameters.trim())) {
         	   // 建立一个NameValuePair数组，用于存储欲传送的参数 
        	 httpPost.addHeader("Content-type","application/json; charset=utf-8");  
             httpPost.setHeader("Accept", "application/json");
             httpPost.setEntity(new StringEntity(parameters,charset)); 
        	}
        	CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != 200) {
    			httpPost.abort();
    			throw new RuntimeException("HttpClient,error status code :" + statusCode);
    		}

            HttpEntity entity = response.getEntity();
    		String result = null;
    		if (entity != null){
    			result = EntityUtils.toString(entity, charset);
    		}
    		EntityUtils.consume(entity);
    		response.close();
    		return result;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return null;
    }
  
    
    /**
     * HTTP Post 获取内容
     * @param url  请求的url地址 ?之前的地址
     * @param params	请求的参数
     * @param charset	编码格式
     * @return	页面内容
     */
    public static String doPost(String url,Map<String,String> params,String charset){
    	if(StringUtils.isBlank(url)){
    		return null;
    	}
    	try {
    		List<NameValuePair> pairs = null;
    		if(params != null && !params.isEmpty()){
    			pairs = new ArrayList<NameValuePair>(params.size());
    			for(Map.Entry<String,String> entry : params.entrySet()){
    				String value = entry.getValue();
    				if(value != null){
    					pairs.add(new BasicNameValuePair(entry.getKey(),value));
    				}
    			}
    		}
    		HttpPost httpPost = new HttpPost(url);
    		if(pairs != null && pairs.size() > 0){
    			httpPost.setEntity(new UrlEncodedFormEntity(pairs,CHARSET));
    		}
    		CloseableHttpResponse response = httpClient.execute(httpPost);
    		int statusCode = response.getStatusLine().getStatusCode();
    		if (statusCode != 200) {
    			httpPost.abort();
    			throw new RuntimeException("HttpClient,error status code :" + statusCode);
    		}
    		HttpEntity entity = response.getEntity();
    		String result = null;
    		if (entity != null){
    			result = EntityUtils.toString(entity, "utf-8");
    		}
    		EntityUtils.consume(entity);
    		response.close();
    		return result;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    
    /**
     * 单个文件上传
     * @param url
     * @param filePath
     * @param charset
     * @return
     */
    public static String doPostFile(String url,String filePath){
		CloseableHttpResponse response = null;
		
		HttpPost httpPost = new HttpPost(url);
		
		try {
			FileBody fb = new FileBody(new File(filePath));
			HttpEntity reqEntity = (HttpEntity) MultipartEntityBuilder.create().addPart("media", fb).build();
			httpPost.setEntity(reqEntity);
			response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
    		if (statusCode != 200) {
    			httpPost.abort();
    			throw new RuntimeException("HttpClient,error status code :" + statusCode);
    		}
    		HttpEntity entity = response.getEntity();
    		String result = null;
    		if (entity != null){
    			result = EntityUtils.toString(entity, "utf-8");
    		}
    		EntityUtils.consume(entity);
    		response.close();
    		return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
    	
    }
    
   
    public static InputStream doGetMedia(String url,Map<String,String> params){
    	if(StringUtils.isBlank(url)){
    		return null;
    	}
    	try {
    		if(params != null && !params.isEmpty()){
    			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
    			for(Map.Entry<String,String> entry : params.entrySet()){
    				String value = entry.getValue();
    				if(value != null){
    					pairs.add(new BasicNameValuePair(entry.getKey(),value));
    				}
    			}
    			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, CHARSET));
    		}
    		HttpGet httpGet = new HttpGet(url);
    		CloseableHttpResponse response = httpClient.execute(httpGet);
    		int statusCode = response.getStatusLine().getStatusCode();
    		if (statusCode != 200) {
    			httpGet.abort();
    			throw new RuntimeException("HttpClient,error status code :" + statusCode);
    		}
    		HttpEntity entity = response.getEntity();
    		
    		InputStream input = null;
    		if (entity != null){
    			input = entity.getContent();
    		}
    		return input;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
}
