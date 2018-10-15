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
		String nickname = request.getParameter("nickname");
		String sex = request.getParameter("sex");
		String province = request.getParameter("province");
		String city = request.getParameter("city");
		String country = request.getParameter("country");
		String head = request.getParameter("head");
		
		if(nickname == null || nickname.trim().length() == 0){
			response.getWriter().write(
					Tools.getResponse(-1, "nickname is null", "登陆失败"));
			return;
		}
		if (code == null || code.trim().length() == 0) {
			response.getWriter().write(
					Tools.getResponse(-1, "code is null", "登陆失败"));
			return;
		}
		if(head == null || head.trim().length() == 0){
			response.getWriter().write(
					Tools.getResponse(-1, "head is null", "登陆失败"));
			return;
		}
		if(sex == null || sex.trim().length() == 0){
			response.getWriter().write(
					Tools.getResponse(-1, "sex is null", "登陆失败"));
			return;
		}
		
		if(province == null || province.trim().length() == 0){
			province="";
		}
		
		if(city == null || city.trim().length() == 0){
			city="";
		}
		if(country == null || country.trim().length() == 0){
			country="";
		}
		
		Map<String, String> map = new HashMap();
		map.put("appid", "wxca8e969f649af183");
		map.put("secret", "7aed41c5d955b84949b62383f834c5ea");
		map.put("js_code", code);
		map.put("grant_type", "authorization_code");
		String result = Tools.doGet(
				"https://api.weixin.qq.com/sns/jscode2session", map);
		System.out.println("result1:"+result);
		JSONObject jsonObject = JSONObject.parseObject(result);
		if (jsonObject == null || jsonObject.containsKey("errcode")) {
			response.getWriter().write(
					Tools.getResponse(-1, "code is error", "登陆失败"));
			return;
		}
		String openid = jsonObject.getString("openid");
		
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
