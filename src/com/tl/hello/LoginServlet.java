package com.tl.hello;

import com.alibaba.fastjson.JSONObject;
import com.tl.hello.dao.UserDao;
import com.tl.hello.utils.Tools;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet({ "/login" })
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "text/html;charset=UTF-8");

		String code = request.getParameter("code");
		if ((code == null) || (code.trim().length() == 0)) {
			response.getWriter().write(
					Tools.getResponse(-1, "code is null", "登陆失败"));
			return;
		}
		Map<String, String> map = new HashMap();
		map.put("appid", "wxca8e969f649af183");
		map.put("secret", "3152fdd60a11d7ff89aec98da986975a");
		map.put("code", code);
		map.put("grant_type", "authorization_code");
		String result = Tools.doGet(
				"https://api.weixin.qq.com/sns/oauth2/access_token", map);
		System.out.println(result);
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject.containsKey("errcode")) {
			response.getWriter().write(
					Tools.getResponse(-1, "code is error", "登陆失败"));
			return;
		}
		String openid = jsonObject.getString("openid");
		String access_token = jsonObject.getString("access_token");
		String unionid = jsonObject.getString("unionid");

		Map<String, String> map2 = new HashMap();
		map2.put("access_token", access_token);
		map2.put("openid", openid);
		String result2 = Tools.doGet("https://api.weixin.qq.com/sns/userinfo",
				map2);
		JSONObject jsonObject2 = JSONObject.parseObject(result2);
		System.out.println(result2);
		if (jsonObject.containsKey("errcode")) {
			response.getWriter().write(
					Tools.getResponse(-1, "failed", "get user detail failed"));
			return;
		}
		String nickname = jsonObject2.getString("nickname");
		String sex = jsonObject2.getString("sex");
		String province = jsonObject2.getString("province");
		String city = jsonObject2.getString("city");
		String country = jsonObject2.getString("country");
		String head = jsonObject2.getString("headimgurl");
		boolean flag = UserDao.saveUser(openid, nickname, head, sex, province,
				city, country);
		if (flag) {
			response.getWriter().write(
					Tools.getResponse(200, UserDao.getUser(openid), "登陆成功"));
		} else {
			response.getWriter().write(
					Tools.getResponse(-1, "register is error", "登陆失败"));
		}
	}
}
