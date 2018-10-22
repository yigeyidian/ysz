package com.tl.hello.utils;

import cn.jiguang.common.utils.Base64;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;

public class Tools {
	public static String getResponse(int code, String data, String msg) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", Integer.valueOf(code));
		jsonObject.put("data", data);
		jsonObject.put("msg", msg);
		return jsonObject.toJSONString();
	}

	/* Error */
	public static String doGet(String httpurl, Map<String, String> params) {
		HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br = null;
        String result = null;// 返回结果字符串
        try {
            // 创建远程url连接对象
            URL url = new URL(appendParams(httpurl, params));
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }

        return result;
	}

	protected static String appendParams(String url, Map<String, String> params) {
		if ((url == null) || (params == null) || (params.isEmpty())) {
			return url;
		}
		String paramStr = "";
		Set<String> keys = params.keySet();
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			paramStr = paramStr + "&" + key + "=" + (String) params.get(key);
		}
		paramStr = paramStr.substring(1, paramStr.length());
		return url + "?" + paramStr;
	}

	public static String removeId(String ids, String id) {
		if ((ids == null) || (id == null) || (ids.length() == 0)
				|| (id.length() == 0)) {
			return ids;
		}
		String[] idss = ids.split("-");
		String newIds = "";
		for (int i = 0; i < idss.length; i++) {
			if (!idss[i].equals(id)) {
				newIds = newIds + "-" + idss[i];
			}
		}
		if (newIds.startsWith("-")) {
			newIds = newIds.substring(1);
		}
		if (newIds.endsWith("-")) {
			newIds = newIds.substring(0, newIds.length());
		}
		return newIds;
	}
	
	public static boolean httpPostWithJson(String url,String content){
	    boolean isSuccess = false;
	    
	    HttpPost post = null;
	    try {
	        HttpClient httpClient = new DefaultHttpClient();

	        // 设置超时时间
	        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
	        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 2000);
	            
	        post = new HttpPost(url);
	        // 构造消息头
	        post.setHeader("Content-type", "application/json; charset=utf-8");
	        post.setHeader("Connection", "Close");
	        post.setHeader("Authorization", "Basic "+Base64.encode("appKey:e9c8d95d917dccde804b392d".getBytes()));
	        // 构建消息实体
	        StringEntity entity = new StringEntity(content, Charset.forName("UTF-8"));
	        entity.setContentEncoding("UTF-8");
	        // 发送Json格式的数据请求
	        entity.setContentType("application/json");
	        post.setEntity(entity);
	        
	            
	        HttpResponse response = httpClient.execute(post);
	            
	        // 检验返回码
	        System.out.println(response.toString());
	    } catch (Exception e) {
	        e.printStackTrace();
	        isSuccess = false;
	    }finally{
	        if(post != null){
	            try {
	                post.releaseConnection();
	                Thread.sleep(500);
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	    return isSuccess;
	}


}
