package com.tl.hello.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tl.hello.bean.RoomBean;
import com.tl.hello.utils.CardUtils;
import com.tl.hello.utils.Tools;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class RoomDao {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
	static final String USER = "root";
	static final String PASS = "Wang4664";

	public static boolean createRoom(int createid, int baseScore, String ship,
			int look, int compare, int maxstake, int waittime, int thinktime,
			int boutcount, int award, int maxPlayer, int awardtype, int eattype) {

		if (isUserPlaying(createid) > 0) {
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "INSERT INTO room(createid,baseScore,ship,look,compare,maxstake,waittime,thinktime,boutcount,state,curplayer,award,maxplayer,awardtype,eattype)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,0,?,?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, createid);
			stmt.setInt(2, baseScore);
			stmt.setString(3, ship);
			stmt.setInt(4, look);
			stmt.setInt(5, compare);
			stmt.setInt(6, maxstake);
			stmt.setInt(7, waittime);
			stmt.setInt(8, thinktime);
			stmt.setInt(9, boutcount);
			stmt.setInt(10, createid);
			stmt.setInt(11, award);
			stmt.setInt(12, maxPlayer);
			stmt.setInt(13, awardtype);
			stmt.setInt(14, eattype);

			if (stmt.executeUpdate() > 0) {
				int cardnum = 0;
				if (boutcount == 8) {
					if (maxPlayer == 4) {
						cardnum = 1;
					} else if (maxPlayer == 6) {
						cardnum = 2;
					} else if (maxPlayer == 9) {
						cardnum = 3;
					} else {
						cardnum = 4;
					}
				} else if (boutcount == 12) {
					if (maxPlayer == 4) {
						cardnum = 2;
					} else if (maxPlayer == 6) {
						cardnum = 3;
					} else if (maxPlayer == 9) {
						cardnum = 4;
					} else {
						cardnum = 5;
					}
				} else {
					if (maxPlayer == 4) {
						cardnum = 3;
					} else if (maxPlayer == 6) {
						cardnum = 4;
					} else if (maxPlayer == 9) {
						cardnum = 5;
					} else {
						cardnum = 6;
					}
				}
				sql = "INSERT INTO card(uid,num,type) VALUES(" + createid + ","
						+ cardnum + ",1)";
				// stmt = conn.prepareStatement(sql);
				// stmt.setInt(1, createid);
				// stmt.setInt(2, cardnum);
				// stmt.setInt(3, 1);

				return stmt.executeUpdate(sql) > 0;
			}

			return false;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return false;
	}

	public static int isUserPlaying(int id) {
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "SELECT * FROM room WHERE (state=0 OR state=1) AND players LIKE ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "%" + id + "%");
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("roomid");
			}
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return -1;
	}

	public static int getRoomId(int createId) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "SELECT * FROM room WHERE (state=0 OR state=1) AND createid = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, createId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("roomid");
			}
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return -1;
	}

	public static int getCreateRoomId(int createId) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "SELECT * FROM room WHERE state=0 AND createid = ? order by createtime desc";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, createId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt("roomid");
			}
			return -1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return -1;
	}

	public static boolean checkRoom(int roomid) {
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String sql = "SELECT count(*) FROM room WHERE (state=0 OR state=1) AND roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, roomid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				if (rs.getInt(1) > 0) {
					flag = true;
				}
			}
			rs.close();

			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return false;
	}

	public static int getRoomMaxPlayer(int roomid) {
		Connection conn = null;
		PreparedStatement stmt = null;
		int count = -1;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String sql = "SELECT maxplayer FROM room WHERE roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, roomid);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			return count;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return count;
	}

	public static boolean jionRoom(int playerid, int roomid) {
		if (isUserPlaying(playerid) > 0) {
			System.out.println("正在游戏中");
			return false;
		}
		if (!checkRoom(roomid)) {
			System.out.println("房间号不存在");
			return false;
		}

		int max = getRoomMaxPlayer(roomid);

		String players = getPlayers(roomid);
		if (players != null && players.length() > 0
				&& players.split("-").length >= max) {
			System.out.println("房间已满");
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		boolean isFirst = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			if (players == null || players.length() == 0) {
				players = playerid + "";
				isFirst = true;
			} else {
				players = players + "-" + playerid;
			}
			String sql = "UPDATE room SET players=?"
					+ (isFirst ? (", curplayer=" + players) : "")
					+ " WHERE roomid=?";
			System.out.println(sql);
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, players);
			stmt.setInt(2, roomid);
			if (stmt.executeUpdate() > 0) {
				sql = "INSERT INTO player(roomid,playerid,stake,cards,abandon,ready,islook,curstake,createtime) VALUES("
						+ roomid
						+ ","
						+ playerid
						+ ",200,'',0,0,0,0,CURRENT_TIME())";
				flag = stmt.executeUpdate(sql) > 0;
				sql = "UPDATE player p SET head = (SELECT head FROM user WHERE id =p.playerid) WHERE playerid="
						+ playerid;
				System.out.println(sql);
				flag = stmt.executeUpdate(sql) > 0;
			}
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return false;
	}

	public static boolean exitRoom(int playerid, int roomid) {
		if (isUserPlaying(playerid) == -1) {
			System.out.println("没有游戏");
			return false;
		}
		if (!checkRoom(roomid)) {
			System.out.println("房间号不正确");
			return false;
		}

		String players = getPlayers(roomid);
		if (players == null || players.length() == 0) {
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			players = Tools.removeId(players, playerid + "");
			String sql = "UPDATE room SET players=? WHERE roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, players);
			stmt.setInt(2, roomid);
			return stmt.executeUpdate(sql) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return false;
	}

	public static boolean closeRoom(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String sql = "UPDATE room SET state=2 WHERE roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, roomid);
			return stmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return false;
	}

	public static boolean ready(int roomid, int playerid, int ready) {
		if (!checkRoom(roomid)) {
			System.out.println("房间号不存在");
			return false;
		}
		if (isUserPlaying(playerid) != roomid) {
			System.out.println("玩家不在游戏");
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "UPDATE player SET ready=? WHERE playerid=? AND roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, ready);
			stmt.setInt(2, playerid);
			stmt.setInt(3, roomid);
			flag = stmt.executeUpdate() > 0;
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static boolean checkReady(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间号不存在");
			return false;
		}

		String players = getPlayers(roomid);
		int max = getRoomMaxPlayer(roomid);
		if (players == null || players.split("-").length == 0
				|| players.split("-").length < max) {
			System.out.println("没有玩家");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String[] playersid = players.split("-");

			for (int i = 0; i < playersid.length; i++) {
				String sql = "SELECT ready FROM player WHERE playerid="
						+ playersid[i];
				ResultSet set = stmt.executeQuery(sql);
				if (set.next()) {
					flag = set.getInt(1) > 0;
				}
				set.close();
				if (!flag) {
					break;
				}
			}
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static boolean dealCard(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		String players = getPlayers(roomid);
		if (players == null || players.length() == 0) {
			System.out.println("没有玩家");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			CardUtils.initCard();
			String[] playersid = players.split("-");
			for (int i = 0; i < playersid.length; i++) {
				String sql = "UPDATE player SET cards='" + CardUtils.getCard()
						+ "' WHERE playerid=" + playersid[i];
				System.out.println("--" + sql);
				flag = stmt.executeUpdate(sql) > 0;
			}

			String sql = "UPDATE room SET remainplayers='" + players
					+ "',state=1 WHERE roomid=" + roomid;
			System.out.println("--" + sql);
			flag = stmt.executeUpdate(sql) > 0;
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static String getRemain(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		String curplayer = "";
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT remainplayers FROM room WHERE roomid= "
					+ roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				curplayer = rs.getString(1);
			}
			rs.close();
			return curplayer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return curplayer;
	}

	public static String getPlayers(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		String curplayer = "";
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT players FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				curplayer = rs.getString(1);
			}
			rs.close();
			return curplayer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return curplayer;
	}

	public static String getCurPlayer(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		String curplayer = "";
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT curplayer FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				curplayer = rs.getString(1);
			}
			rs.close();
			return curplayer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return curplayer;
	}

	public static boolean isAbandon(int roomid, int playerid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		if (isUserPlaying(playerid) != roomid) {
			System.out.println("玩家不在当前房间");
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "SELECT abandon FROM player WHERE playerid=? AND roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, playerid);
			stmt.setInt(2, roomid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				flag = rs.getInt(1) == 0;
			}
			rs.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static boolean isPlaying(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean curplayer = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT state FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				curplayer = rs.getInt(1) == 1;
			}
			rs.close();
			return curplayer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return curplayer;
	}

	public static int curstake(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return -1;
		}
		Connection conn = null;
		Statement stmt = null;
		int curstake = -1;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT curstake FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				curstake = rs.getInt(1);
			}
			rs.close();
			return curstake;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return -1;
	}

	public static boolean nextPlayer(int roomid, int uid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		String remain = getRemain(roomid);
		if (remain == null || remain.length() == 0 || !remain.contains("-")) {
			System.out.println("游戏已结束");
			return false;
		}

		String curplayer = getCurPlayer(roomid);
		if (curplayer == null || curplayer.length() == 0) {
			System.out.println("游戏已结束");
			return false;
		}
		if (!curplayer.equals("" + uid)) {
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String[] remains = remain.split("-");
			for (int i = 0; i < remains.length; i++) {
				if (remains[i].equals(curplayer)) {
					if (i == remains.length - 1) {
						curplayer = remains[0];
						break;
					}
					curplayer = remains[(i + 1)];
					break;
				}
			}
			String sql = "UPDATE room SET curplayer=? WHERE roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, curplayer);
			stmt.setInt(2, roomid);
			flag = stmt.executeUpdate() > 0;
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static boolean abandon(int roomid, int playerid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		if (isUserPlaying(playerid) != roomid) {
			System.out.println("玩家不在当前房间");
			return false;
		}
		String remain = getRemain(roomid);
		String curPlay = getCurPlayer(roomid);

		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			String sql = "UPDATE player SET abandon=1 WHERE playerid=? AND roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, playerid);
			stmt.setInt(2, roomid);
			flag = stmt.executeUpdate() > 0;
			if (curPlay.equals(playerid + "")) {
				nextPlayer(roomid, playerid);
			}
			remain = Tools.removeId(remain, playerid + "");

			sql = "UPDATE room SET remainplayers='" + remain
					+ "' WHERE roomid=" + roomid;
			System.out.println(sql);
			flag = stmt.executeUpdate(sql) > 0;

			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static boolean isGameOver(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean curplayer = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT curbout,boutcount FROM room WHERE roomid= "
					+ roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				int curbout = rs.getInt(1);
				int boutcount = rs.getInt(2);
				curplayer = curbout > boutcount;
			}
			rs.close();
			return curplayer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return curplayer;
	}

	public static boolean gameover(int roomid) {
		return closeRoom(roomid);
	}

	public static boolean isNoLook(int roomid, int playerid) {

		if (!checkRoom(roomid)) {
			System.out.println("房间号不存在");
			return false;
		}
		if (isUserPlaying(playerid) != roomid) {
			System.out.println("玩家不在当前房间");
			return false;
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "SELECT nolook FROM player WHERE playerid=? AND roomid=?";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, playerid);
			stmt.setInt(2, roomid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				flag = rs.getInt(1) == 1;
			}
			rs.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}

		return false;
	}

	public static boolean isLook(int roomid, int playerid) {

		if (!checkRoom(roomid)) {
			System.out.println("房间号不存在");
			return false;
		}
		if (isUserPlaying(playerid) != roomid) {
			System.out.println("玩家不在当前房间");
			return false;
		}

		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "SELECT islook FROM player WHERE playerid=? AND roomid=?";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, playerid);
			stmt.setInt(2, roomid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				flag = rs.getInt(1) == 1;
			}
			rs.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}

		return false;
	}

	public static boolean currentOver(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间号不存在");
			return false;
		}
		String remain = getRemain(roomid);
		if (remain.contains("-")) {
			return false;
		}

		String cards = lookCard(roomid, Integer.parseInt(remain));

		String allPlayer = getPlayers(roomid);
		System.out.println(allPlayer);
		boolean nolook = isNoLook(roomid, Integer.parseInt(remain));

		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		int awardCount = 0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "SELECT allstake,award,awardtype FROM room WHERE roomid= "
					+ roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				int allstake = rs.getInt(1);
				int award = rs.getInt(2);
				int awardtype = rs.getInt(3);

				if (CardUtils.isAAA(cards) > 0 && award > 0) {
					if (awardtype == 0) {
						String[] players = allPlayer.split("-");
						allstake += players.length * award;
						awardCount = award;
					} else {
						if (nolook) {
							String[] players = allPlayer.split("-");
							allstake += players.length * award;
							awardCount = award;
						}
					}
				}

				if (allstake > 0) {
					sql = "UPDATE player SET stake=stake+" + allstake
							+ " WHERE playerid=" + remain + " AND roomid="
							+ roomid;
					System.out.println(sql);
					flag = stmt.executeUpdate(sql) > 0;
				}
				sql =

				"UPDATE room SET curplayer="
						+ remain
						+ ",allstake=0,minstake=0,curbout=curbout+1,curstake=0,state=0 WHERE roomid="
						+ roomid;
				System.out.println(sql);
				flag = stmt.executeUpdate(sql) > 0;
			}
			rs.close();
			if (flag) {
				String[] playerids = allPlayer.split("-");
				for (int i = 0; i < playerids.length; i++) {
					sql = "UPDATE player SET ready=0,abandon=0,islook=0,curstake=0,stake=stake-"
							+ awardCount + " WHERE playerid=" + playerids[i];
					System.out.println(sql);
					flag = stmt.executeUpdate(sql) > 0;
				}
			}
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static String lookCard(int roomid, int playerid) {
		if (isUserPlaying(playerid) != roomid) {
			System.out.println("玩家不在当前房间");
			return "";
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		String curplayer = "";
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "SELECT cards FROM player WHERE playerid=? AND roomid=?";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, playerid);
			stmt.setInt(2, roomid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				curplayer = rs.getString(1);
			}
			rs.close();

			sql = "UPDATE player SET islook=1 WHERE roomid=" + roomid
					+ " AND playerid=" + playerid;
			stmt.executeLargeUpdate(sql);

			return curplayer;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return curplayer;
	}

	public static boolean stake(int roomid, int playerid, int stake) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		if (isUserPlaying(playerid) != roomid) {
			System.out.println("玩家不在当前房间");
			return false;
		}
		String curplayer = getCurPlayer(roomid);
		if (!curplayer.equals(playerid + "")) {
			System.out.println("不该发言");
			return false;
		}

		boolean isLook = isLook(roomid, playerid);
		boolean isnolook = isNoLook(roomid, playerid);
		int nolook = 0;

		if (isnolook) {
			nolook = 1;
		} else {
			if (!isLook) {
				nolook = 1;
			}
		}

		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "UPDATE player SET stake=stake-" + stake
					+ ",curstake=" + stake + ",nolook=" + nolook
					+ ",stakecount=stakecount+1 WHERE playerid=" + playerid
					+ " AND roomid=" + roomid;
			flag = stmt.executeUpdate(sql) > 0;

			sql = "UPDATE room SET allstake=allstake+" + stake + ",curstake="
					+ (isLook ? stake : (int) (stake * 2.5)) + " WHERE roomid="
					+ roomid;
			System.out.println(sql);
			flag = stmt.executeUpdate(sql) > 0;
			// nextPlayer(roomid);
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static boolean checkAllStake(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT * FROM room WHERE roomid=" + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				int allstake = rs.getInt("allstake");
				int maxstake = rs.getInt("maxstake");
				flag = allstake >= maxstake;
			}
			rs.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return false;
	}

	public static boolean compareAll(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}

		String remain = getRemain(roomid);

		String[] remainPlayers = remain.split("-");

		int max = 0;
		for (int i = 1; i < remainPlayers.length; i++) {
			if (compareCard(roomid, Integer.parseInt(remainPlayers[i]),
					Integer.parseInt(remainPlayers[max]))) {
				abandon(roomid, Integer.parseInt(remainPlayers[max]));
				max = i;
			} else {
				abandon(roomid, Integer.parseInt(remainPlayers[i]));
			}
		}

		// currentOver(roomid);

		return false;
	}

	public static int stakeCount(int roomid, int playerid) {
		if (isUserPlaying(playerid) != roomid) {
			System.out.println("玩家不在当前房间");
			return -1;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		int count = 0;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String sql = "SELECT stakecount FROM player WHERE playerid=? AND roomid=?";

			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, playerid);
			stmt.setInt(2, roomid);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();

			return count;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return -1;
	}

	public static boolean isEating(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT eattype FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				flag = rs.getInt(1) == 1;
			}
			rs.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return false;
	}

	/* Error */
	public static boolean compareCard(int roomid, int id1, int id2) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return false;
		}

		String card1 = lookCard(roomid, id1);
		String card2 = lookCard(roomid, id2);

		if (isEating(roomid)) {
			if (CardUtils.is235(card1) > 0 && CardUtils.isAAA(card2) > 0) {
				return true;
			}

			if (CardUtils.isAAA(card1) > 0 && CardUtils.is235(card2) > 0) {
				return false;
			}
		}
		if (CardUtils.isAAA(card1) > CardUtils.isAAA(card2)) {
			return true;
		}

		if (CardUtils.isTHS(card1) > CardUtils.isTHS(card2)) {
			return true;
		}

		if (CardUtils.isTH(card1) > CardUtils.isTH(card2)) {
			return true;
		}

		if (CardUtils.isSort(card1) > CardUtils.isSort(card2)) {
			return true;
		}

		if (CardUtils.isDouble(card1) > CardUtils.isDouble(card2)) {
			return true;
		}

		if (CardUtils.isDouble(card1) == CardUtils.isDouble(card2)
				&& CardUtils.isDouble(card1) != -1) {
			if (CardUtils.isDouble2(card1) > CardUtils.isDouble2(card2)) {
				return true;
			}
			return false;
		}

		if (CardUtils.getMax(card1) > CardUtils.getMax(card2)) {
			return true;
		}

		return false;
	}

	public static String getRoomDetail(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT * FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			JSONObject jsonObject = new JSONObject();
			if (rs.next()) {
				jsonObject.put("roomid", Integer.valueOf(rs.getInt("roomid")));
				jsonObject.put("createid",
						Integer.valueOf(rs.getInt("createid")));
				jsonObject.put("state", Integer.valueOf(rs.getInt("state")));
				jsonObject.put("players", rs.getString("players"));
				jsonObject.put("basescore",
						Integer.valueOf(rs.getInt("basescore")));
				jsonObject.put("look", Integer.valueOf(rs.getInt("look")));
				jsonObject
						.put("compare", Integer.valueOf(rs.getInt("compare")));
				jsonObject.put("ship", rs.getString("ship"));
				jsonObject.put("maxstake", rs.getInt("maxstake"));
				jsonObject.put("waittime", rs.getInt("waittime"));
				jsonObject.put("thinktime", rs.getInt("thinktime"));
				jsonObject.put("boutcount", rs.getInt("boutcount"));
				jsonObject.put("award", rs.getInt("award"));
				jsonObject.put("awardtype", rs.getInt("awardtype"));
				jsonObject.put("eattype", rs.getInt("eattype"));
			}
			rs.close();
			return jsonObject.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return "";
	}

	public static RoomBean getRoom(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return null;
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT * FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			RoomBean bean = new RoomBean();
			if (rs.next()) {
				bean.setRoomid(rs.getInt("roomid"));
				bean.setCreateid(rs.getInt("createid"));
				bean.setState(rs.getInt("state"));
				bean.setPlayers(rs.getString("players"));
				bean.setBasescore(rs.getInt("basescore"));
				bean.setLook(rs.getInt("look"));
				bean.setCompare(rs.getInt("compare"));
				bean.setShip(rs.getString("ship"));
				bean.setMaxstake(rs.getInt("maxstake"));
				bean.setWaittime(rs.getInt("waittime"));
				bean.setThinktime(rs.getInt("thinktime"));
				bean.setBoutcount(rs.getInt("boutcount"));
				bean.setCurbout(rs.getInt("curbout"));
				bean.setAward(rs.getInt("award"));
				bean.setAwardtype(rs.getInt("awardtype"));
				bean.setEattype(rs.getInt("eattype"));
				bean.setCurplayer(rs.getInt("curplayer"));
				bean.setAllstake(rs.getInt("allstake"));
				bean.setMinstake(rs.getInt("minstake"));
				bean.setCurstake(rs.getInt("curstake"));
			}
			rs.close();
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return null;
	}

	public static String getAllPlayer(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间不存在");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT * FROM player WHERE roomid= " + roomid;
			// sql =
			// "SELECT a.id,a.roomid,a.playerid,a.stake,a.cards,a.abandon,a.ready,a.islook,b.head FROM player AS a,user AS b WHERE a.playerid=b.id AND a.roomid="
			// + roomid;
			// sql =
			// "SELECT * FROM player AS a,user AS b WHERE a.playerid=b.id AND a.roomid="
			// + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			JSONArray jsonArray = new JSONArray();
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", Integer.valueOf(rs.getInt("id")));
				jsonObject.put("roomid", Integer.valueOf(rs.getInt("roomid")));
				jsonObject.put("playerid",
						Integer.valueOf(rs.getInt("playerid")));
				jsonObject.put("stake", Integer.valueOf(rs.getInt("stake")));
				jsonObject.put("cards", rs.getString("cards"));
				jsonObject
						.put("abandon", Integer.valueOf(rs.getInt("abandon")));
				jsonObject.put("ready", Integer.valueOf(rs.getInt("ready")));
				jsonObject.put("islook", Integer.valueOf(rs.getInt("islook")));
				jsonObject.put("head", rs.getString("head"));

				jsonArray.add(jsonObject);
			}
			rs.close();
			return jsonArray.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return "";
	}

	private static void close(Statement stmt, Connection conn) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException localSQLException1) {
		}
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException se) {
			se.printStackTrace();
		}
	}
}
