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
    
    System.out.println("---" + RoomDao.isUserPlaying(10888));
  }
}
