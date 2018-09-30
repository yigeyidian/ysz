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
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			String sql = "SELECT * FROM room WHERE (state=0 OR state=1) AND createid = ?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, createId);
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
	
	public static int getRoomMaxPlayer(int roomid){
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
		if (isUserPlaying(playerid)>0) {
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
				&& getPlayers(roomid).split("-").length >= max) {
			System.out.println("房间已满");
			return false;
		}
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			
			if (players == null || players.length() == 0) {
				players = playerid + "";
			} else {
				players = players + "-" + playerid;
			}
			String sql = "UPDATE room SET players=? WHERE roomid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, players);
			stmt.setInt(2, roomid);
			if (stmt.executeUpdate() > 0) {
				sql ="INSERT INTO player(roomid,playerid,stake,cards,abandon,ready,islook,curstake,createtime) VALUES("+roomid+","+playerid+",200,'',0,0,0,0,CURRENT_TIME())";
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
			
			players = Tools.removeId(players, playerid+"");
			String sql = "UPDATE room SET players=? WHERE roomid=?";
			stmt = conn.prepareStatement(sql);	
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
		if(players == null || players.length() ==0){
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
				String sql = "SELECT ready FROM player WHERE playerid="+ playersid[i];
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
		if(players == null || players.length() ==0){
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

			String sql = "SELECT remainplayers FROM room WHERE roomid= "+ roomid;
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

	public static boolean nextPlayer(int roomid) {
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
		if ((curplayer == null) || (curplayer.length() == 0)) {
			System.out.println("游戏已结束");
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
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			String sql = "UPDATE player SET abandon=1 WHERE playerid="
					+ playerid;
			flag = stmt.executeUpdate(sql) > 0;
			if (curPlay.equals(playerid+"")) {
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

	public static boolean currentOver(int roomid) {
		if (!checkRoom(roomid)) {
			System.out.println("房间号不存在");
			return false;
		}
		String remain = getRemain(roomid);
		if(remain.contains("-")){
			return false;
		}

		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
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
