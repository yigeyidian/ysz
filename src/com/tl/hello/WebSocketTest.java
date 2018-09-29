package com.tl.hello;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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
public class WebSocketTest
{
  private static int onlineCount = 0;
  private static CopyOnWriteArraySet<WebSocketTest> webSocketSet = new CopyOnWriteArraySet();
  private static ConcurrentHashMap<String, WebSocketTest> webSocketMap = new ConcurrentHashMap();
  private Session session;
  
  @OnOpen
  public void onOpen(Session session)
  {
    this.session = session;
    webSocketSet.add(this);
    addOnlineCount();
    System.out.println("有新链接加入----" + getOnlineCount());
  }
  
  @OnClose
  public void onClose()
  {
    webSocketSet.remove(this);
    subOnlineCount();
    System.out.println("有一连接关闭----" + getOnlineCount());
  }
  
  @OnMessage
  public void onMessage(String message, Session session)
  {
    System.out.println(message);
    JSONObject jsonObject = JSON.parseObject(message);
    if (jsonObject.containsKey("uid"))
    {
      int uid = jsonObject.getIntValue("uid");
      String type = jsonObject.getString("type");
      String msg = jsonObject.getString("msg");
      if (type.equals("create"))
      {
        boolean flag = RoomDao.createRoom(uid, 1, "2-4-8-20", 1, 1, 
          200, 20, 20, 8, 0,6);
        if (!flag)
        {
          send("系统消息", "创建房间失败");
          sendMsg("error", "创建房间失败");
          return;
        }
        int roomId = RoomDao.getRoomId(uid);
        flag = RoomDao.jionRoom(uid, RoomDao.getRoomId(uid));
        if (!flag)
        {
          send("系统消息", "加入房间失败");
          sendMsg("error", "加入房间失败");
          return;
        }
        send("系统消息", "创建房间成功，房号:" + roomId);
        sendMsg("room", RoomDao.getRoomDetail(roomId));
        webSocketMap.put(uid+"", this);
      }
      else if (type.equals("jion"))
      {
        boolean flag = RoomDao.jionRoom(uid, Integer.parseInt(msg));
        if (!flag)
        {
        	send("系统消息", "加入房间失败");
            sendMsg("error", "加入房间失败");
          return;
        }
        try
        {
          int roomid = Integer.parseInt(msg);
          webSocketMap.put(uid+"", this);
          sendAll(roomid, "系统消息", uid + "加入房间" + msg);
          sendAllMsg(roomid, "jion", RoomDao.getAllPlayer(roomid));
          sendMsg("room", RoomDao.getRoomDetail(roomid));
        }
        catch (Exception e)
        {
          sendMsg("error", "加入房间失败");
          e.printStackTrace();
        }
      }
      else if (type.equals("ready"))
      {
        int roomid = RoomDao.getRoomIdByPlayer(uid);
        if (roomid == 0)
        {
          send("系统消息", "未加入房间");
          sendMsg("error", "准备失败，未加入房间");
          return;
        }
        boolean flag = RoomDao.ready(uid, 1);
        if (!flag)
        {
          send("系统消息", "准备失败");
          sendMsg("error", "准备失败");
          return;
        }
        if (RoomDao.checkReady(roomid))
        {
          sendAll(roomid, "系统消息", "所有玩家已准备，等待发牌");
          sendAllMsg(roomid, "dealcard", "开始发票");
          
          flag = RoomDao.dealCard(roomid);
          if (flag)
          {
            sendAll(roomid, "系统消息", 
              "发票完毕，该" + RoomDao.getCurPlayer(roomid) + 
              "说话，剩余玩家" + RoomDao.getRemain(roomid));
            JSONObject json = new JSONObject();
            json.put("cur", RoomDao.getCurPlayer(roomid));
            json.put("room", RoomDao.getRoomDetail(roomid));
            json.put("player", RoomDao.getAllPlayer(roomid));
            sendAllMsg(roomid, "waiting", json.toJSONString());
          }
          else
          {
            sendAll(roomid, "系统消息", "发牌失败");
            sendAllMsg(roomid, "error", "发牌失败");
          }
        }
        else
        {
          sendAll(roomid, uid+"", "已准备");
          sendAllMsg(roomid, "ready", RoomDao.getAllPlayer(roomid));
        }
      }
      else if (type.equals("stake"))
      {
        int roomid = RoomDao.getRoomIdByPlayer(uid);
        if ((roomid == 0) || (!RoomDao.isPlaying(roomid)))
        {
          send("系统消息", "游戏未开始");
          sendMsg("error", "游戏未开始");
          return;
        }
        int stake = Integer.parseInt(msg);
        int curstake = RoomDao.curstake(roomid);
        if (stake < RoomDao.curstake(roomid))
        {
          send("系统消息", "下注失败，最少下注" + curstake);
          sendMsg("error", "下注失败，最少下注" + curstake);
          return;
        }
        boolean flag = RoomDao.stake(roomid, uid, stake);
        if (flag)
        {
          sendAll(roomid, uid+"", "下注" + msg);
          sendAll(roomid, "系统消息", "改" + RoomDao.getCurPlayer(roomid) + 
            "说话");
          
          sendAllMsg(roomid, "stake", msg);
          JSONObject json = new JSONObject();
          json.put("cur", RoomDao.getCurPlayer(roomid));
          json.put("room", RoomDao.getRoomDetail(roomid));
          json.put("player", RoomDao.getAllPlayer(roomid));
          sendAllMsg(roomid, "waiting", json.toJSONString());
        }
        else
        {
          send("系统消息", "下注失败");
          sendMsg("error", "下注失败");
        }
      }
      else if (type.equals("abandon"))
      {
        int roomid = RoomDao.getRoomIdByPlayer(uid);
        if ((roomid == 0) || (!RoomDao.isPlaying(roomid)))
        {
        	send("系统消息", "游戏未开始");
            sendMsg("error", "游戏未开始");
          return;
        }
        boolean flag = RoomDao.abandon(roomid, uid);
        if (flag)
        {
          String remain = RoomDao.getRemain(roomid);
          sendAll(roomid, uid+"", "弃牌");
          sendAllMsg(roomid, "abandon", uid+"");
          if (!remain.contains("-"))
          {
            RoomDao.currentOver(roomid);
            sendAll(roomid, "系统消息", "游戏已结束，本轮游戏赢家：" + remain);
            
            sendAllMsg(roomid, "win", remain);
          }
          else
          {
            sendAll(roomid, "系统消息", 
              "改" + RoomDao.getCurPlayer(roomid) + "玩家说话，剩余玩家" + 
              remain);
            JSONObject json = new JSONObject();
            json.put("cur", RoomDao.getCurPlayer(roomid));
            json.put("room", RoomDao.getRoomDetail(roomid));
            json.put("player", RoomDao.getAllPlayer(roomid));
            sendAllMsg(roomid, "waiting", json.toJSONString());
          }
          if (RoomDao.isGameOver(roomid))
          {
            sendAll(roomid, "系统消息", "游戏结束");
            sendAllMsg(roomid, "gameover", "游戏结束");
          }
        }
        else
        {
          send("系统消息", "下注失败");
          sendMsg("error", "弃牌失败");
        }
      }
      else if (type.equals("compare"))
      {
        int roomid = RoomDao.getRoomIdByPlayer(uid);
        if ((roomid == 0) || (!RoomDao.isPlaying(roomid)))
        {
        	send("系统消息", "游戏未开始");
            sendMsg("error", "游戏未开始");
          return;
        }
        sendAll(roomid, uid+"", "与" + msg + "比牌");
        
        boolean flag = RoomDao.compareCard(roomid, uid, Integer.parseInt(msg));
        if (flag)
        {
          RoomDao.abandon(roomid, Integer.parseInt(msg));
          sendAll(roomid, "系统消息", uid + "比牌赢," + msg + "弃牌");
          
          sendAllMsg(roomid, "compare", uid + "-" + msg);
        }
        else
        {
          RoomDao.abandon(roomid, uid);
          sendAll(roomid, "系统消息", uid + "比牌输," + uid + "弃牌");
          sendAllMsg(roomid, "compare", msg + "-" + uid);
        }
        String remain = RoomDao.getRemain(roomid);
        if (!remain.contains("-"))
        {
          RoomDao.currentOver(roomid);
          sendAll(roomid, "系统消息", "游戏已结束，本轮游戏赢家：" + remain);
          sendAllMsg(roomid, "win", remain);
        }
        else
        {
          
          sendAll(roomid, "系统消息", 
                  "改" + RoomDao.getCurPlayer(roomid) + "玩家说话，剩余玩家" + 
                  remain);
          JSONObject json = new JSONObject();
          json.put("cur", RoomDao.getCurPlayer(roomid));
          json.put("room", RoomDao.getRoomDetail(roomid));
          json.put("player", RoomDao.getAllPlayer(roomid));
          sendAllMsg(roomid, "waiting", json.toJSONString());
          
        }
        if (RoomDao.isGameOver(roomid))
        {
        	sendAll(roomid, "系统消息", "游戏结束");
            sendAllMsg(roomid, "gameover", "游戏结束");
        }
      }
      else if (type.equals("scancard"))
      {
        int roomid = RoomDao.getRoomIdByPlayer(uid);
        if ((roomid == 0) || (!RoomDao.isPlaying(roomid)))
        {
          sendMsg("error", "游戏未开始");
          return;
        }
        String flag = RoomDao.lookCard(uid);
        send("系统消息", "看牌：" + flag);
        sendMsg("look", flag);
      }
      else if (type.equals("msg"))
      {
        int roomid = RoomDao.getRoomIdByPlayer(uid);
        sendAllMsg(roomid, "msg", msg);
      }
    }
  }
  
  public void sendAll(int roomid, String uid, String msg) {}
  
  public void send(String uid, String msg) {}
  
  public void sendAllMsg(int roomid, String type, String msg)
  {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("type", type);
    jsonObject.put("msg", msg);
    String players = RoomDao.getPlayers(roomid);
    Iterator<String> iterator = webSocketMap.keySet().iterator();
    while (iterator.hasNext())
    {
      String key = (String)iterator.next();
      if (players.contains(key))
      {
        WebSocketTest item = (WebSocketTest)webSocketMap.get(key);
        try
        {
          item.sendMessage(jsonObject.toJSONString());
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
  }
  
  public void sendMsg(String type, String msg)
  {
    try
    {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("type", type);
      jsonObject.put("msg", msg);
      sendMessage(jsonObject.toJSONString());
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
  
  @OnError
  public void onError(Session session, Throwable error)
  {
    System.out.println("��������");
    error.printStackTrace();
  }
  
  public void sendMessage(String message)
    throws IOException
  {
    this.session.getBasicRemote().sendText(message);
  }
  
  public static synchronized int getOnlineCount()
  {
    return onlineCount;
  }
  
  public static synchronized void addOnlineCount()
  {
    onlineCount += 1;
  }
  
  public static synchronized void subOnlineCount()
  {
    onlineCount -= 1;
  }
}
