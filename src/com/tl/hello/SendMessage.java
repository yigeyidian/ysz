package com.tl.hello;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.connection.Http2Client;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jiguang.common.utils.Base64;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tl.hello.dao.UserDao;
import com.tl.hello.utils.HttpUtils;
import com.tl.hello.utils.Tools;

/**
 * Servlet implementation class SendMessage
 */
@WebServlet("/sendmessage")
public class SendMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SendMessage() {
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

		String phone = request.getParameter("phone");
		if (phone == null || phone.length() == 0) {
			response.getWriter().write(
					Tools.getResponse(-1, "phone is error", "手机号码错误"));
			return;
		}

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("mobile", phone);
		jsonObject.put("temp_id", 1);

		try {
			String rw = HttpUtils.doPost("https://api.sms.jpush.cn/v1/codes",
					jsonObject.toJSONString());
			if (rw != null && rw.length() > 0) {
				if (JSON.parseObject(rw).containsKey("msg_id")) {
					if (UserDao.saveCode(phone,
							JSON.parseObject(rw).getString("msg_id"))) {
						response.getWriter().write(
								Tools.getResponse(-1, "get code failed",
										"获取验证码失败"));
						return;
					} else {
						response.getWriter().write(
								Tools.getResponse(-1, "get code failed",
										"获取验证码失败"));
						return;
					}
				} else {
					response.getWriter()
							.write(Tools.getResponse(-1, "get code failed",
									"获取验证码失败"));
					return;
				}
			} else {
				response.getWriter().write(
						Tools.getResponse(-1, "get code failed", "获取验证码失败"));
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		response.getWriter().write(
				Tools.getResponse(-1, "get code failed", "获取验证码失败"));
		return;
	}

}
