package com.tl.hello.utils;



 

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

 





import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import cn.jiguang.common.utils.Base64;

 
public class HttpUtils {

	public static String doGet(String url) {

        try {

        	HttpClient client = new DefaultHttpClient();

            //发送get请求

            HttpGet request = new HttpGet(url);

            HttpResponse response = client.execute(request);

 

            /**请求发送成功，并得到响应**/

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

                /**读取服务器返回过来的json字符串数据**/

                String strResult = EntityUtils.toString(response.getEntity());

                

                return strResult;

            }

        } 

        catch (IOException e) {

        	e.printStackTrace();

        }

        

        return null;

	}

	
	public static String doPost(String url, Map params){

		

		BufferedReader in = null;  

        try {  

            // 定义HttpClient  

            HttpClient client = new DefaultHttpClient();  

            // 实例化HTTP方法  

            HttpPost request = new HttpPost();  

            request.setURI(new URI(url));

           

            //设置参数

            List<NameValuePair> nvps = new ArrayList<NameValuePair>(); 

            for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {

    			String name = (String) iter.next();

    			String value = String.valueOf(params.get(name));

    			nvps.add(new BasicNameValuePair(name, value));
    		}

            request.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));

            

            HttpResponse response = client.execute(request);  
            System.out.println(response);

            int code = response.getStatusLine().getStatusCode();

            if(code == 200){	//请求成功

            	in = new BufferedReader(new InputStreamReader(response.getEntity()  

                        .getContent(),"utf-8"));

                StringBuffer sb = new StringBuffer("");  

                String line = "";  

                String NL = System.getProperty("line.separator");  

                while ((line = in.readLine()) != null) {  

                    sb.append(line + NL);  

                }

                

                in.close();  

                

                return sb.toString();

            }

            else{	//

            	System.out.println("状态码：" + code);

            	return null;

            }

        }

        catch(Exception e){

        	e.printStackTrace();
        	
        	

        	return null;

        }

	}


	public static String doPost(String url, String params) throws Exception {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);// 创建httpPost   
    	//httpPost.setHeader("Accept", "application/json"); 
    	httpPost.setHeader("Content-type", "application/json; charset=utf-8");
    	httpPost.setHeader("Authorization", "Basic "+new String(Base64.encode("e9c8d95d917dccde804b392d:3eb731238e39ad65311f30f0".getBytes()))); 

    	Header[] header = httpPost.getAllHeaders();
    	for(Header h : header){
    		System.out.println(h.getValue());
    	}
    	
    	//System.out.println(httpPost.getAllHeaders());
    	String charSet = "UTF-8";

    	StringEntity entity = new StringEntity(params, charSet);

    	httpPost.setEntity(entity);        

        CloseableHttpResponse response = null;
        try {
        	response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            System.out.println(response);
            int state = status.getStatusCode();
            System.out.println(state+"");
            if (state == HttpStatus.SC_OK) {
            	HttpEntity responseEntity = response.getEntity();
            	String jsonString = EntityUtils.toString(responseEntity);
            	return jsonString;
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
        finally {
            if (response != null) {

                try {

                    response.close();

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

            try {

				httpclient.close();

			} catch (IOException e) {

				e.printStackTrace();

			}

        }

        return "";

	}

}

