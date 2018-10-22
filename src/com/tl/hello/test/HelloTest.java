package com.tl.hello.test;

import cn.jiguang.common.ClientConfig;
import cn.jiguang.common.connection.Http2Client;
import cn.jiguang.common.resp.ResponseWrapper;
import cn.jiguang.common.utils.Base64;

import com.alibaba.fastjson.JSONObject;
import com.tl.hello.dao.RoomDao;
import com.tl.hello.dao.UserDao;
import com.tl.hello.utils.HttpUtils;
import com.tl.hello.utils.Tools;

import java.io.PrintStream;

import org.junit.Test;

public class HelloTest
{
  @Test
  public void testUser()
  {
    //int roomid = RoomDao.getRoomId(4);
    
//    System.out.println("" + UserDao.saveUser("aaa", "会飞的🐟",
//    		"https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI1NJSEuuWZrm81aficaLZEK2WnzUMDf47J1ickhvINMA6mIgr4sIicia8KYp56Uj1icSicnjLABVrqNhUg/132",
//    		"1", "", "", ""));
//	  int flag = RoomDao.getCreateRoomId(10002);
	  System.out.println(UserDao.getCode("18123286739"));
//	  JSONObject jsonObject = new JSONObject();
//		jsonObject.put("mobile", "18123286739");
//		jsonObject.put("temp_id", 1);
//		System.out.println(jsonObject.toJSONString());
//		
//		try {
//			String rw = HttpUtils.doPost("https://api.sms.jpush.cn/v1/codes", jsonObject.toJSONString());
//			System.out.println("---"+rw.toString());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
  }
}
