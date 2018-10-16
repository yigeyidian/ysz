package com.tl.hello.dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDao {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://rm-wz9iyqu4qf6mr0s35so.mysql.rds.aliyuncs.com:3306/HELLO?user=root&password=Wang4664&useUnicode=true&characterEncoding=utf8&autoReconnect=true";
	static final String USER = "root";
	static final String PASS = "Wang4664";
	

	public static String getUser(String openid) {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL);
			stmt = conn.createStatement();
			String sql = "SELECT * FROM user WHERE openid= '" + openid + "'";
			ResultSet rs = stmt.executeQuery(sql);

			JSONObject jsonObject = new JSONObject();
			if (rs.next()) {
				jsonObject.put("id", rs.getInt("id"));
				jsonObject.put("openid", rs.getString("openid"));
				jsonObject.put("nickname", rs.getString("nickname"));
				jsonObject.put("head", rs.getString("head"));
				jsonObject.put("sex", rs.getInt("sex"));
				jsonObject.put("province", rs.getString("province"));
				jsonObject.put("city", rs.getString("city"));
				jsonObject.put("country", rs.getString("country"));
				jsonObject.put("cardnum", rs.getInt("cardnum"));
				jsonObject.put("level", rs.getInt("level"));
				jsonObject.put("phone", rs.getString("phone"));
			}
			rs.close();
			stmt.close();
			conn.close();
			return jsonObject.toJSONString();
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
		return "";
	}

	public static int getCardNum(int id) {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager
					.getConnection(
							DB_URL,
							USER, PASS);
			stmt = conn.createStatement();
			String sql = "SELECT * FROM user WHERE id= " + id;
			ResultSet rs = stmt.executeQuery(sql);
			int num = 0;
			if (rs.next()) {
				num = rs.getInt("cardnum");
			}
			rs.close();
			stmt.close();
			conn.close();
			return num;
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
		return 0;
	}

	public static boolean bindPhone(String openid,int id,String phone){
		
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager
					.getConnection(
							DB_URL,
							USER, PASS);
			String sql = "UPDATE user SET phone=? WHERE id=?&&openid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, phone);
			stmt.setInt(2, id);
			stmt.setString(3, openid);
			
			flag = stmt.executeUpdate() > 0;
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return flag;
	}
	
	public static String roomRecord(int id) {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager
					.getConnection(
							DB_URL,
							USER, PASS);
			String sql = "SELECT * FROM room WHERE createid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			
			ResultSet set = stmt.executeQuery();
			JSONArray jsonArray = new JSONArray();
			while(set.next()){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("roomid", set.getInt("roomid"));
				jsonObject.put("createid", set.getInt("createid"));
				jsonObject.put("players", set.getString("players"));
				jsonObject.put("state", set.getInt("state"));
				jsonObject.put("basescore", set.getInt("basescore"));
				jsonObject.put("look", set.getInt("look"));
				jsonObject.put("compare", set.getInt("compare"));
				jsonObject.put("maxstake", set.getInt("maxstake"));
				jsonObject.put("waittime", set.getInt("waittime"));
				jsonObject.put("thinktime", set.getInt("thinktime"));
				jsonObject.put("boutcount", set.getInt("boutcount"));
				jsonObject.put("award", set.getInt("award"));
				jsonObject.put("maxplayer", set.getInt("maxplayer"));
				jsonObject.put("createtime", set.getInt("createtime"));
				jsonArray.add(jsonObject);
			}
			return jsonArray.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return "";
	}
	
	public static String gameRecord(int id) {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager
					.getConnection(
							DB_URL,
							USER, PASS);
			String sql = "SELECT * FROM player WHERE playerid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			
			ResultSet set = stmt.executeQuery();
			JSONArray jsonArray = new JSONArray();
			while(set.next()){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", set.getInt("id"));
				jsonObject.put("playerid", set.getInt("playerid"));
				jsonObject.put("roomid", set.getInt("roomid"));
				jsonObject.put("stake", set.getInt("stake"));
				jsonObject.put("endtime", set.getString("endtime"));
				jsonArray.add(jsonObject);
			}
			return jsonArray.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return "";
	}
	
	public static String cardRecord(int id) {
		
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager
					.getConnection(
							DB_URL,
							USER, PASS);
			String sql = "SELECT * FROM card WHERE uid=?";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, id);
			
			ResultSet set = stmt.executeQuery();
			JSONArray jsonArray = new JSONArray();
			while(set.next()){
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", set.getInt("id"));
				jsonObject.put("uid", set.getInt("uid"));
				jsonObject.put("num", set.getInt("num"));
				jsonObject.put("type", set.getInt("type"));
				jsonObject.put("createtime", set.getString("createtime"));
				jsonArray.add(jsonObject);
			}
			return jsonArray.toJSONString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(stmt, conn);
		}
		return "";
	}
	
	public static boolean saveUser(String openid, String nickname, String head,
			String sex, String province, String city, String country) {
		Connection conn = null;
		Statement stmt = null;
		boolean flag = false;
		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager
					.getConnection(DB_URL);
			stmt = conn.createStatement();

			String sql = "SELECT * FROM user WHERE openid= '" + openid + "'";

			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				sql = "UPDATE user SET nickname='" + nickname + "',head='"
						+ head + "',sex=" + sex + ",province='" + province
						+ "',city='" + city + "',country='" + country
						+ "' WHERE openid='" + openid + "'";
				System.out.println(sql);
				flag = stmt.executeUpdate(sql) > 0;
			} else {
				sql =

				"INSERT INTO user(openid,nickname,head,sex,province,city,country) VALUES('"
						+ openid + "','" + nickname + "','" + head + "'," + sex
						+ ",'" + province + "','" + city + "','" + country
						+ "')";
				System.out.println("--" + sql);
				flag = stmt.executeUpdate(sql) > 0;
			}
			stmt.close();
			conn.close();
			return flag;
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
