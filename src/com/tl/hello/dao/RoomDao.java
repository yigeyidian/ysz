package com.tl.hello.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
	static final String DB_URL = "jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8";
	static final String USER = "root";
	static final String PASS = "Wang4664";

	public static boolean createRoom(int createid, int baseScore, String ship,
			int look, int compare, int maxstake, int waittime, int thinktime,
			int boutcount, int award, int maxPlayer) {
		
		if(isUserPlaying(createid) > 0){
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			String sql = "INSERT INTO room(createid,baseScore,ship,look,compare,maxstake,waittime,thinktime,boutcount,state,curplayer,award,maxplayer)"
					+ " VALUES(?,?,?,?,?,?,?,?,?,0,?,?,?)";
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

			return stmt.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return false;
	}

	public static int isUserPlaying(int id){
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			String sql = "SELECT * FROM room WHERE (state=0 OR state=1) AND players LIKE ?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, "%"+id+"%");
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
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
		Statement stmt = null;
		int flag = -1;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();

			String sql = "SELECT roomid FROM room WHERE createid= " + createId;
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				flag = rs.getInt(1);
			}
			rs.close();
			stmt.close();
			conn.close();
			return flag;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException localSQLException4) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return flag;
	}

	public static boolean checkRoom(int roomid) {
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT count(*) FROM room WHERE roomid= " + roomid;

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt(1) > 0) {
					System.out.println("����������" + rs.getInt(1));
					flag = true;
				}
			}
			rs.close();
			stmt.close();
			conn.close();
			return flag;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException localSQLException4) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return false;
	}

	public static boolean checkPlayer(int id) {
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT count(*) FROM player WHERE playerid= " + id;

			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt(1) > 0) {
					System.out.println("��������������" + rs.getInt(1));
					flag = true;
				}
			}
			rs.close();
			stmt.close();
			conn.close();
			return flag;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException localSQLException4) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return false;
	}

	public static boolean jionRoom(int playerid, int roomid) {
		if (checkPlayer(playerid)) {
			System.out.println("��������������");
			return false;
		}
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		String players = getPlayers(roomid);
		if ((players != null) && (players.length() > 0)
				&& (getPlayers(roomid).split("-").length >= 6)) {
			System.out.println("��������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();
			if ((players == null) || (players.length() == 0)) {
				players = playerid + "";
			} else {
				players = players + "-" + playerid;
			}
			String sql = "UPDATE room SET players='" + players
					+ "' WHERE roomid=" + roomid;
			System.out.println(sql);
			if (stmt.executeUpdate(sql) > 0) {
				sql =

				"INSERT INTO player(roomid,playerid,stake,cards,abandon,ready,islook,curstake) VALUES("
						+ roomid
						+ ","
						+ playerid
						+ ","
						+ 200
						+ ",'',"
						+ 0
						+ "," + 0 + "," + 0 + "," + 0 + ")";
				System.out.println("--" + sql);
				flag = stmt.executeUpdate(sql) > 0;
			}
			stmt.close();
			conn.close();
			return flag;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException localSQLException4) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return false;
	}

	public static boolean exitRoom(int playerid, int roomid) {
		if (!checkPlayer(playerid)) {
			return false;
		}
		if (!checkRoom(roomid)) {
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT players FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String players = rs.getString(1);
				if ((players == null) || (players.length() == 0)) {
				} else {
					players = players.replace(playerid + "", "").replace("--",
							"-");
					if (players.startsWith("-")) {
						players = players.substring(1);
					}
					if (players.endsWith("-")) {
						players = players.substring(0, players.length() - 1);
					}
					sql = "UPDATE room SET players='" + players
							+ "' WHERE roomid=" + roomid;
					System.out.println(sql);
					if (stmt.executeUpdate(sql) > 0) {
						sql = "DELETE FROM player WHERE playerid=" + playerid;
						System.out.println("--" + sql);
						flag = stmt.executeUpdate(sql) > 0;
					}
				}
			}
			rs.close();
			stmt.close();
			conn.close();
			return flag;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException localSQLException4) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return false;
	}

	public static boolean closeRoom(int roomid) {
		if (!checkRoom(roomid)) {
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT players FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String players = rs.getString(1);
				if ((players == null) || (players.length() == 0)) {
				} else {
					String[] playersid = players.split("-");
					for (int i = 0; i < playersid.length; i++) {
						sql = "DELETE FROM player WHERE playerid="
								+ playersid[i];
						System.out.println("--" + sql);
						flag = stmt.executeUpdate(sql) > 0;
					}
				}
			}
			rs.close();
			sql = "DELETE FROM room WHERE roomid=" + roomid;
			System.out.println("--" + sql);
			flag = stmt.executeUpdate(sql) > 0;
			stmt.close();
			conn.close();
			return flag;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException localSQLException4) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return false;
	}

	public static boolean createPlayer(int playerid, int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		if (checkPlayer(playerid)) {
			System.out.println("��������������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "INSERT INTO player(roomid,playerid,stake,cards,abandon,ready,islook,curstake) VALUES("
					+ roomid
					+ ","
					+ playerid
					+ ","
					+ 200
					+ ",'',"
					+ 0
					+ ","
					+ 0 + "," + 0 + "," + 0 + ")";
			System.out.println("--" + sql);
			flag = stmt.execute(sql);
			stmt.close();
			conn.close();
			return flag;
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException localSQLException4) {
			}
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return false;
	}

	public static boolean ready(int roomid, int playerid, int ready) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		if (!checkPlayer(playerid)) {
			System.out.println("����������������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();
			String sql = "UPDATE player SET ready=" + ready
					+ " WHERE playerid=" + playerid;
			flag = stmt.executeUpdate(sql) > 0;
			stmt.close();
			conn.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static int getRoomIdByPlayer(int playerid) {
		Connection conn = null;
		Statement stmt = null;
		int flag = 0;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();
			String sql = "SELECT roomid FROM player WHERE playerid=" + playerid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				flag = rs.getInt(1);
			}
			rs.close();
			stmt.close();
			conn.close();
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return 0;
	}

	public static boolean ready(int playerid, int ready) {
		if (!checkPlayer(playerid)) {
			System.out.println("����������������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();
			String sql = "UPDATE player SET ready=" + ready
					+ " WHERE playerid=" + playerid;
			flag = stmt.executeUpdate(sql) > 0;
			stmt.close();
			conn.close();
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
			System.out.println("����������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT players FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String players = rs.getString(1);
				if ((players == null) || (players.length() == 0)) {
					System.out.println("��������������");
				} else {
					String[] playersid = players.split("-");
					flag = true;
					for (int i = 0; i < playersid.length; i++) {
						sql = "SELECT ready FROM player WHERE playerid="
								+ playersid[i];
						System.out.println("--" + sql);
						ResultSet set = stmt.executeQuery(sql);
						if (set.next()) {
							flag = set.getInt(1) > 0;
							if (!flag) {
								break;
							}
						}
					}
				}
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

	public static boolean dealCard(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT players FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String players = rs.getString(1);
				if ((players == null) || (players.length() == 0)) {
					System.out.println("��������������");
				} else {
					CardUtils.initCard();
					String[] playersid = players.split("-");
					for (int i = 0; i < playersid.length; i++) {
						sql = "UPDATE player SET cards='" + CardUtils.getCard()
								+ "' WHERE playerid=" + playersid[i];
						System.out.println("--" + sql);
						flag = stmt.executeUpdate(sql) > 0;
					}
				}
				sql = "UPDATE room SET remainplayers='" + players
						+ "',state=1 WHERE roomid=" + roomid;
				System.out.println("--" + sql);
				flag = stmt.executeUpdate(sql) > 0;
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

	public static String getRemain(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		String curplayer = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
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
			System.out.println("����������");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		String curplayer = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
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
			System.out.println("����������");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		String curplayer = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
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

	public static boolean isAbandon(int playerid) {
		if (!checkPlayer(playerid)) {
			System.out.println("����������������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT abandon FROM player WHERE playerid= "
					+ playerid;
			ResultSet rs = stmt.executeQuery(sql);
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
			System.out.println("����������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean curplayer = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
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
			System.out.println("����������");
			return -1;
		}
		Connection conn = null;
		Statement stmt = null;
		int curstake = -1;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
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

	public static boolean nextPlayer(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		String remain = getRemain(roomid);
		if ((remain == null) || (remain.length() == 0)
				|| (!remain.contains("-"))) {
			System.out.println("����������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT curplayer FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				String curplayer = rs.getString(1);
				if ((curplayer == null) || (curplayer.length() == 0)) {
					System.out.println("��������������");
				} else {
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
					sql = "UPDATE room SET curplayer='" + curplayer
							+ "' WHERE roomid=" + roomid;
					System.out.println("--" + sql);
					flag = stmt.executeUpdate(sql) > 0;
				}
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

	public static boolean abandon(int roomid, int playerid) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		if (!checkPlayer(playerid)) {
			System.out.println("����������������");
			return false;
		}
		String remain = getRemain(roomid);
		String curPlay = getCurPlayer(roomid);

		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();
			String sql = "UPDATE player SET abandon=1 WHERE playerid="
					+ playerid;
			flag = stmt.executeUpdate(sql) > 0;
			if (curPlay.equals(playerid)) {
				nextPlayer(roomid);
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
			System.out.println("����������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean curplayer = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
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
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();
			String sql = "DELETE FROM player WHERE roomid= " + roomid;
			flag = stmt.executeUpdate(sql) > 0;

			sql = "DELETE FROM room WHERE roomid= " + roomid;
			flag = stmt.executeUpdate(sql) > 0;
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	public static boolean currentOver(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		String remain = getRemain(roomid);

		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();
			String sql = "SELECT allstake FROM room WHERE roomid= " + roomid;
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				int allstake = rs.getInt(1);
				if (allstake > 0) {
					sql = "UPDATE player SET stake=stake+" + allstake
							+ " WHERE playerid=" + remain;
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
				String players = getPlayers(roomid);
				String[] playerids = players.split("-");
				for (int i = 0; i < playerids.length; i++) {
					sql = "UPDATE player SET ready=0,abandon=0,islook=0,curstake=0 WHERE playerid="
							+ remain;
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

	public static String lookCard(int playerid) {
		if (!checkPlayer(playerid)) {
			System.out.println("����������");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		String curplayer = "";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT cards FROM player WHERE playerid= " + playerid;
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

	public static boolean stake(int roomid, int playerid, int stake) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return false;
		}
		if (!checkPlayer(playerid)) {
			System.out.println("����������������");
			return false;
		}
		String curplayer = getCurPlayer(roomid);
		if (!curplayer.equals(playerid)) {
			System.out.println("��������");
			return false;
		}
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();
			String sql = "UPDATE player SET stake=stake-" + stake
					+ ",curstake=" + stake + " WHERE playerid=" + playerid;
			flag = stmt.executeUpdate(sql) > 0;

			sql = "UPDATE room SET allstake=allstake+" + stake + ",curstake="
					+ stake + " WHERE roomid=" + roomid;
			System.out.println(sql);
			flag = stmt.executeUpdate(sql) > 0;

			nextPlayer(roomid);
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}

	/* Error */
	public static boolean compareCard(int roomid, int id1, int id2) {
		return false;
	}

	public static String getRoomDetail(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
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
				jsonObject.put("maxstake", rs.getString("maxstake"));
				jsonObject.put("waittime", rs.getString("waittime"));
				jsonObject.put("thinktime", rs.getString("thinktime"));
				jsonObject.put("boutcount", rs.getString("boutcount"));
				jsonObject.put("award", rs.getString("award"));
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

	public static String getAllPlayer(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("����������");
			return "";
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager
					.getConnection(
							"jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf-8",
							"root", "Wang4664");
			stmt = conn.createStatement();

			String sql = "SELECT * FROM player WHERE roomid= " + roomid;
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
