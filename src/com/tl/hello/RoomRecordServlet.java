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
 * Servlet implementation class RoomRecordServlet
 */
@WebServlet("/roomrecord")
public class RoomRecordServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RoomRecordServlet() {
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
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "text/html;charset=UTF-8");

		String idstr = request.getParameter("id");
		if ((idstr == null) || (idstr.trim().length() == 0)) {
			response.getWriter().write(
					Tools.getResponse(-1, "id is null", "获取失败"));
			return;
		}

		try {
			int id = Integer.parseInt(idstr);
			response.getWriter().write(
					Tools.getResponse(200, UserDao.roomRecord(id), "绑定成功"));

		} catch (Exception e) {
			response.getWriter().write(
					Tools.getResponse(-1, "id is error", "获取失败"));
			return;
		}
	}

}
