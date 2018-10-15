package com.tl.hello.utils;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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
}
