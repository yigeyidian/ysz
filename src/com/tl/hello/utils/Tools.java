package com.tl.hello.utils;

import com.alibaba.fastjson.JSONObject;
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
		return "";
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
