package com.tl.hello;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tl.hello.bean.RoomBean;
import com.tl.hello.dao.RoomDao;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class WebSocketTest {
	private static int onlineCount = 0;
	private static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet();
	private static ConcurrentHashMap<String, WebSocketTest> webSocketMap = new ConcurrentHashMap();
	private Session session;

	@OnOpen
	public void onOpen(Session session) {
		this.session = session;
		webSocketSet.add(this);
		addOnlineCount();
		System.out.println("有新链接加入----" + getOnlineCount());
	}

	@OnClose
	public void onClose() {
		webSocketSet.remove(this);
		subOnlineCount();
		System.out.println("有一连接关闭----" + getOnlineCount());
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		System.out.println(message);
		JSONObject jsonObject = JSON.parseObject(message);
		if (jsonObject.containsKey("uid")) {
			int uid = jsonObject.getIntValue("uid");
			String type = jsonObject.getString("type");
			String msg = jsonObject.getString("msg");
			if (type.equals("create")) {
				doCreate(uid,msg);
			} else if (type.equals("jion")) {
				doJion(uid,msg);
			} else if (type.equals("ready")) {
				doReady(uid,msg);
			} else if (type.equals("stake")) {
				doStake(uid,msg);
			} else if (type.equals("abandon")) {
				doAbandon(uid,msg);
			} else if (type.equals("compare")) {
				doCompare(uid,msg);
			} else if (type.equals("scancard")) {
				doScan(uid,msg);
			} else if (type.equals("msg")) {
				int roomid = RoomDao.isUserPlaying(uid);
				if (roomid != -1) {
					sendAllMsg(roomid, "msg", msg);
				}
			}else if(type.equals("exit")){
				doExit(uid,msg);
			}
		}
	}
	
	private void doExit(int uid, String msg) {
		// TODO Auto-generated method stub
		int roomid = RoomDao.isUserPlaying(uid);
		if (roomid == -1) {
			sendMsg("error", "玩家没有在房间");
			return;
		}
		if(RoomDao.isPlaying(roomid)){
			boolean flag = RoomDao.abandon(roomid, uid);
			if (flag) {
				String remain = RoomDao.getRemain(roomid);
				sendAllMsg(roomid, "abandon", uid + "");
				if (!remain.contains("-")) {
					String cards = RoomDao.lookCard(roomid, Integer.parseInt(remain));
					RoomDao.currentOver(roomid);
					sendAllMsg(roomid, "win", remain+"-"+cards);
					if(RoomDao.isGameOver(roomid)){
						RoomDao.gameover(roomid);
						sendAllMsg(roomid, "gameover", "gameover");
					}
				} else {
					JSONObject json = new JSONObject();
					json.put("cur", RoomDao.getCurPlayer(roomid));
					json.put("room", RoomDao.getRoomDetail(roomid));
					json.put("player", RoomDao.getAllPlayer(roomid));
					sendAllMsg(roomid, "waiting", json.toJSONString());
				}
			} else {
				sendMsg("error", "弃牌失败");
			}
			if(RoomDao.exitRoom(uid, roomid)){
				sendAllMsg(roomid, "exit","exit-"+uid);
				webSocketMap.remove(""+uid);
			}else {
				sendMsg("error","退出失败");
			}
		}else{
			if(RoomDao.exitRoom(uid, roomid)){
				sendAllMsg(roomid, "exit","exit-"+uid);
				webSocketMap.remove(""+uid);
			}else {
				sendMsg("error","退出失败");
			}
		}
	}

	private void doScan(int uid, String msg) {
		// TODO Auto-generated method stub
		int roomid = RoomDao.isUserPlaying(uid);
		if (roomid == -1 || !RoomDao.isPlaying(roomid)) {
			sendMsg("error", "游戏未开始");
			return;
		}
		RoomBean bean = RoomDao.getRoom(roomid);
		if(RoomDao.stakeCount(roomid, uid) < bean.getLook()){
			sendAllMsg(roomid, "error", "look-"+bean.getLook());
			return;
		}
		
		String flag = RoomDao.lookCard(roomid, uid);
		sendMsg("look", flag);
	}

	private void doCompare(int uid, String msg) {
		// TODO Auto-generated method stub
		int roomid = RoomDao.isUserPlaying(uid);
		if (roomid == -1 || !RoomDao.isPlaying(roomid)) {
			sendMsg("error", "游戏未开始");
			return;
		}
		
		RoomBean bean = RoomDao.getRoom(roomid);
		if(RoomDao.stakeCount(roomid, uid) < bean.getCompare()){
			sendAllMsg(roomid, "error", "compare-"+bean.getCompare());
			return;
		}
		try{
			int oid = Integer.parseInt(msg);
			
			sendAll(roomid, uid + "", "与" + oid + "比牌");

			boolean flag = RoomDao.compareCard(roomid, uid,oid);
			if (flag) {
				RoomDao.abandon(roomid, Integer.parseInt(msg));
				sendAllMsg(roomid, "compare", uid + "-" + msg);
			} else {
				RoomDao.abandon(roomid, uid);
				sendAllMsg(roomid, "compare", msg + "-" + uid);
			}
			String remain = RoomDao.getRemain(roomid);
			if (!remain.contains("-")) {
				String cards = RoomDao.lookCard(roomid, Integer.parseInt(remain));
				RoomDao.currentOver(roomid);
				sendAllMsg(roomid, "win", remain+"-"+cards);
				
				if(RoomDao.isGameOver(roomid)){
					RoomDao.gameover(roomid);
					sendAllMsg(roomid, "gameover", "gameover");
				}
				
				if (RoomDao.isGameOver(roomid)) {
					sendAllMsg(roomid, "gameover", "游戏结束");
				}
			} else {
				sendAll(roomid, "系统消息", "改" + RoomDao.getCurPlayer(roomid)
						+ "玩家说话，剩余玩家" + remain);
				JSONObject json = new JSONObject();
				json.put("cur", RoomDao.getCurPlayer(roomid));
				json.put("room", RoomDao.getRoomDetail(roomid));
				json.put("player", RoomDao.getAllPlayer(roomid));
				sendAllMsg(roomid, "waiting", json.toJSONString());

			}
		}catch(Exception e){
			e.printStackTrace();
			sendMsg("error", "消息错误");
		}
	}

	public void doAbandon(int uid,String msg) {
		// TODO Auto-generated method stub
		int roomid = RoomDao.isUserPlaying(uid);
		if (roomid == -1 || !RoomDao.isPlaying(roomid)) {
			sendMsg("error", "游戏未开始");
			return;
		}
		boolean flag = RoomDao.abandon(roomid, uid);
		if (flag) {
			String remain = RoomDao.getRemain(roomid);
			sendAllMsg(roomid, "abandon", uid + "");
			if (!remain.contains("-")) {
				String cards = RoomDao.lookCard(roomid, Integer.parseInt(remain));
				RoomDao.currentOver(roomid);
				sendAllMsg(roomid, "win", remain+"-"+cards);
				if(RoomDao.isGameOver(roomid)){
					RoomDao.gameover(roomid);
					sendAll(roomid, "系统消息", "游戏已结束");
					sendAllMsg(roomid, "gameover", "gameover");
				}
			} else {
				JSONObject json = new JSONObject();
				json.put("cur", RoomDao.getCurPlayer(roomid));
				json.put("room", RoomDao.getRoomDetail(roomid));
				json.put("player", RoomDao.getAllPlayer(roomid));
				sendAllMsg(roomid, "waiting", json.toJSONString());
			}
			
		} else {
			sendMsg("error", "弃牌失败");
		}
	}

	private void doStake(int uid, String msg) {
		int roomid = RoomDao.isUserPlaying(uid);
		if (roomid == -1 || !RoomDao.isPlaying(roomid)) {
			sendMsg("error", "游戏未开始");
			return;
		}
		try{
			int stake = Integer.parseInt(msg);
			RoomBean bean = RoomDao.getRoom(roomid);
			boolean islook = RoomDao.isLook(roomid, uid);
			if(stake < (islook?bean.getCurstake():(int)(bean.getCurstake()/2.5f))){
				sendMsg("error", "下注失败，最少下注" + (islook?bean.getCurstake():(int)(bean.getCurstake()/2.5f)));
				return;
			}
			
			boolean flag = RoomDao.stake(roomid, uid, stake);
			if (flag) {
				sendAll(roomid, uid + "", "下注" + msg);
				
				sendAllMsg(roomid, "stake", uid+"-"+msg);
				
				if(RoomDao.nextPlayer(roomid,uid)){
					JSONObject json = new JSONObject();
					json.put("cur", RoomDao.getCurPlayer(roomid));
					json.put("room", RoomDao.getRoomDetail(roomid));
					json.put("player", RoomDao.getAllPlayer(roomid));
					sendAllMsg(roomid, "waiting", json.toJSONString());
					
					if(RoomDao.checkAllStake(roomid)){
						sendAllMsg(roomid, "allcompare", "筹码已满，一起比牌");
						RoomDao.compareAll(roomid);
						String remain = RoomDao.getRemain(roomid);
						if (!remain.contains("-")) {
							String cards = RoomDao.lookCard(roomid, Integer.parseInt(remain));
							RoomDao.currentOver(roomid);

							sendAllMsg(roomid, "win", remain+"-"+cards);
							
							if(RoomDao.isGameOver(roomid)){
								RoomDao.gameover(roomid);
								sendAllMsg(roomid, "gameover", "游戏已结束");
							}
						} 
					}
				}else {
					sendMsg("error", "切换玩家失败");
				}
			} else {
				sendMsg("error", "下注失败");
			}
		}catch(Exception e){
			sendMsg("error", "消息错误");
		}
	}

	public void doReady(int uid, String msg) {
		// TODO Auto-generated method stub
		int roomid = RoomDao.isUserPlaying(uid);
		if (roomid == -1) {
			sendMsg("error", "准备失败，未加入房间");
			return;
		}
		int ready = 1;
		try{
			ready = Integer.parseInt(msg);
		}catch(Exception e){
//			sendMsg("error", "消息错误");
//			return;
		}
		boolean flag = RoomDao.ready(roomid, uid, ready);
		if (!flag) {
			sendMsg("error", "操作失败");
			return;
		}
		if (RoomDao.checkReady(roomid)) {
			sendAll(roomid, "系统消息", "所有玩家已准备，等待发牌");
			sendAllMsg(roomid, "dealcard", "开始发票");

			flag = RoomDao.dealCard(roomid);
			if (flag) {
				sendAll(roomid, "系统消息",
						"发票完毕，该" + RoomDao.getCurPlayer(roomid)
								+ "说话，剩余玩家" + RoomDao.getRemain(roomid));
				JSONObject json = new JSONObject();
				json.put("cur", RoomDao.getCurPlayer(roomid));
				json.put("room", RoomDao.getRoomDetail(roomid));
				json.put("player", RoomDao.getAllPlayer(roomid));
				sendAllMsg(roomid, "waiting", json.toJSONString());
			} else {
				sendAll(roomid, "系统消息", "发牌失败");
				sendAllMsg(roomid, "error", "发牌失败");
			}
		} else {
			sendAll(roomid, uid + "", ready==1?"已准备":"取消准备");
			sendAllMsg(roomid, "ready", RoomDao.getAllPlayer(roomid));
		}
	}

	//加入房间
	public void doJion(int uid, String msg) {
		// TODO Auto-generated method stub
		try {
			int roomid = Integer.parseInt(msg);
			int r = RoomDao.isUserPlaying(uid);
			if(r == roomid){
				webSocketMap.remove(uid+"");
				webSocketMap.put(uid + "", this);
				sendAll(roomid, "系统消息", uid + "加入房间" + msg);
				sendAllMsg(roomid, "jion", RoomDao.getAllPlayer(roomid));
				sendMsg("room", RoomDao.getRoomDetail(roomid));
				return;
			}
			if (r > 0) {
				sendMsg("error", "玩家已在"+r+"房间中");
				return;
			}
			if (!RoomDao.checkRoom(roomid)) {
				sendMsg("error", "房间号不存在");
				return;
			}
			int max = RoomDao.getRoomMaxPlayer(roomid);
			String players = RoomDao.getPlayers(roomid);
			if (players != null && players.length() > 0
					&& players.split("-").length >= max) {
				sendMsg("error", "房间已满");
				return;
			}
			
			boolean flag = RoomDao.jionRoom(uid, roomid);
			if (!flag) {
				sendMsg("error", "加入房间失败");
				return;
			}
			webSocketMap.remove(uid+"");
			webSocketMap.put(uid + "", this);
			sendAll(roomid, "系统消息", uid + "加入房间" + msg);
			sendAllMsg(roomid, "jion", RoomDao.getAllPlayer(roomid));
			sendMsg("room", RoomDao.getRoomDetail(roomid));
		} catch (Exception e) {
			sendMsg("error", "加入房间失败");
			e.printStackTrace();
		}
	}

	//加入房间 测试专用
	private void doCreate(int uid, String msg) {
		// TODO Auto-generated method stub
		boolean flag = RoomDao.createRoom(uid, 1, "2-4-8-20", 1, 1,
				200, 20, 20, 8, 0, 6,0,0);
		if (!flag) {
			send("系统消息", "创建房间失败");
			sendMsg("error", "创建房间失败");
			return;
		}
		int roomId = RoomDao.getRoomId(uid);
		flag = RoomDao.jionRoom(uid, RoomDao.getRoomId(uid));
		if (!flag) {
			send("系统消息", "加入房间失败");
			sendMsg("error", "加入房间失败");
			return;
		}
		send("系统消息", "创建房间成功，房号:" + roomId);
		sendMsg("room", RoomDao.getRoomDetail(roomId));
		webSocketMap.put(uid + "", this);
	}

	public void sendAll(int roomid, String uid, String msg) {
	}

	public void send(String uid, String msg) {
	}

	public void sendAllMsg(int roomid, String type, String msg) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("type", type);
		jsonObject.put("msg", msg);
		String players = RoomDao.getPlayers(roomid);
		Iterator<String> iterator = webSocketMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			if (players.contains(key)) {
				WebSocketTest item = (WebSocketTest) webSocketMap.get(key);
				try {
					item.sendMessage(jsonObject.toJSONString());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void sendMsg(String type, String msg) {
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("type", type);
			jsonObject.put("msg", msg);
			sendMessage(jsonObject.toJSONString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@OnError
	public void onError(Session session, Throwable error) {
		System.out.println("��������");
		error.printStackTrace();
	}

	public void sendMessage(String message) throws IOException {
		if(this.session != null){
		this.session.getBasicRemote().sendText(message);}
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		onlineCount += 1;
	}

	public static synchronized void subOnlineCount() {
		onlineCount -= 1;
	}
}
