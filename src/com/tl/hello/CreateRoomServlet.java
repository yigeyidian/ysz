package com.tl.hello;

import com.tl.hello.dao.RoomDao;
import com.tl.hello.dao.UserDao;
import com.tl.hello.utils.Tools;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CreateRoomServlet
 */
@WebServlet("/createroom")
public class CreateRoomServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Content-Type", "text/html;charset=UTF-8");

		String id = request.getParameter("id");
		String basescore = request.getParameter("basescore");
		String look = request.getParameter("look");
		String compare = request.getParameter("compare");
		String boutcount = request.getParameter("boutcount");
		String waittime = request.getParameter("waittime");
		String thinktime = request.getParameter("thinktime");
		String maxstake = request.getParameter("maxstake");
		String ships = request.getParameter("ships");
		String award = request.getParameter("award");
		String maxcount = request.getParameter("maxcount");
		String awardtypestr = request.getParameter("awardtype");
		String eattypestr = request.getParameter("eattype");
		if ((id == null) || (id.length() == 0)) {
			response.getWriter().write(
					Tools.getResponse(-1, "id is null", "创建房间失败"));
			return;
		}
		int uid = 0;
		int base = 0;
		int lookcount = 0;
		int comparecount = 0;
		int bout = 8;
		int wait = 0;
		int think = 0;
		int max = 0;
		int awardnum = 0;
		int maxPlayer = 3;
		int awardtype = 0;
		int eattype = 0;
		try {
			uid = Integer.parseInt(id);
			base = Integer.parseInt(basescore);
			lookcount = Integer.parseInt(look);
			comparecount = Integer.parseInt(compare);
			bout = Integer.parseInt(boutcount);
			wait = Integer.parseInt(waittime);
			think = Integer.parseInt(thinktime);
			max = Integer.parseInt(maxstake);
			awardnum = Integer.parseInt(award);
			maxPlayer = Integer.parseInt(maxcount);
			awardtype = Integer.parseInt(awardtypestr);
			eattype = Integer.parseInt(eattypestr);
		} catch (Exception e) {
			response.getWriter().write(
					Tools.getResponse(-1, "param is error", "创建房间失败"));
			return;
		}
		if (UserDao.getCardNum(uid) <= 0) {
			response.getWriter().write(
					Tools.getResponse(-1, "card is 0", "房卡不足"));
			return;
		}
		boolean flag = RoomDao.createRoom(uid, base, ships, lookcount,
				comparecount, max, wait, think, bout, awardnum,maxPlayer,awardtype,eattype);
		if (!flag) {
			response.getWriter().write(
					Tools.getResponse(-1, "db error", "创建房间失败"));
			return;
		}
		response.getWriter().write(
				Tools.getResponse(200, RoomDao.getRoomId(uid) + "", "创建房间成功"));
	}

}
