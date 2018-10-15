package com.tl.hello.test;

import com.tl.hello.dao.RoomDao;
import com.tl.hello.dao.UserDao;

import java.io.PrintStream;

import org.junit.Test;

public class HelloTest
{
  @Test
  public void testUser()
  {
    //int roomid = RoomDao.getRoomId(4);
    
    System.out.println("" + UserDao.saveUser("aaa", "会飞的🐟",
    		"https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTI1NJSEuuWZrm81aficaLZEK2WnzUMDf47J1ickhvINMA6mIgr4sIicia8KYp56Uj1icSicnjLABVrqNhUg/132",
    		"1", "", "", ""));
  }
}
