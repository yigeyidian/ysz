package com.tl.hello;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tl.hello.dao.UserDao;
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "text/html;charset=UTF-8");
		
		String openid = request.getParameter("openid");
		String idstr = request.getParameter("id");
		String phone = request.getParameter("phone");
		if ((openid == null) || (openid.trim().length() == 0)) {
			response.getWriter().write(
					Tools.getResponse(-1, "openid is null", "绑定失败"));
			return;
		}
		
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
		
		try{
			int id = Integer.parseInt(idstr);
			boolean flag = UserDao.bindPhone(openid, id, phone);
			if(flag){
				response.getWriter().write(
						Tools.getResponse(200, "bind success", "绑定成功"));
			}else{
				response.getWriter().write(
						Tools.getResponse(-1, "bind error", "绑定失败"));
			}
		}catch(Exception e){
			response.getWriter().write(
					Tools.getResponse(-1, "id is error", "绑定失败"));
			return;
		}
		
	}

}
