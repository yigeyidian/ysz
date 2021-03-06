package com.tl.hello;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tl.hello.dao.UserDao;
import com.tl.hello.utils.HttpUtils;
import com.tl.hello.utils.Tools;

/**
 * Servlet implementation class BindPhoneServlet
 */
@WebServlet("/bindphone")
public class BindPhoneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BindPhoneServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "text/html;charset=UTF-8");

		String idstr = request.getParameter("id");
		String phone = request.getParameter("phone");
		String code = request.getParameter("code");

		if ((idstr == null) || (idstr.trim().length() == 0)) {
			response.getWriter().write(
					Tools.getResponse(-1, "id is null", "绑定失败"));
			return;
		}

		if ((phone == null) || (phone.trim().length() == 0)) {
			response.getWriter().write(
					Tools.getResponse(-1, "phone is null", "绑定失败"));
			return;
		}

		if ((code == null) || (code.trim().length() == 0)) {
			response.getWriter().write(
					Tools.getResponse(-1, "code is null", "绑定失败"));
			return;
		}

		try {

			String msg_id = UserDao.getCode(phone);
			if (msg_id == null || msg_id.length() == 0) {
				response.getWriter().write(
						Tools.getResponse(-1, "验证码错误", "绑定失败"));
				return;
			}

			System.out.println(msg_id);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("code", code);
			String rw = HttpUtils.doPost("https://api.sms.jpush.cn/v1/codes/"
					+ msg_id + "/valid", jsonObject.toJSONString());
			if (rw == null || rw.length() == 0) {
				response.getWriter().write(
						Tools.getResponse(-1, "验证码错误", "绑定失败"));
				return;
			}
			JSONObject jo = JSON.parseObject(rw);
			if (jo.getBoolean("is_valid")) {
				int id = Integer.parseInt(idstr);
				boolean flag = UserDao.bindPhone(id, phone);
				if (flag) {
					response.getWriter().write(
							Tools.getResponse(200, "bind success", "绑定成功"));
				} else {
					response.getWriter().write(
							Tools.getResponse(-1, "bind error", "绑定失败"));
				}
			} else {
				response.getWriter().write(
						Tools.getResponse(-1, "验证码错误", "绑定失败"));
				return;
			}

		} catch (Exception e) {
			response.getWriter().write(
					Tools.getResponse(-1, "id is error", "绑定失败"));
			return;
		}

	}

}
