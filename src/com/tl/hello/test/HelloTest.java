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
    
    System.out.println("---" + UserDao.bindPhone("aaaaaaaaaa", 14, "13111111111"));
  }
}
