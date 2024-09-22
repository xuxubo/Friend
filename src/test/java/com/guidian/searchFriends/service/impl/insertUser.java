/**
 * @作者 徐振博
 * @创建时间 2024/9/8 19:28
 */
package com.guidian.searchFriends.service.impl;

import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.LinkedList;
import java.util.List;

@SpringBootTest
public class insertUser {

    @Autowired
    private UserService userService;
    @Test
    public void doInsertUser() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User> userList = new LinkedList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUserName("假用户");
            user.setUserAccount("fakeRokie");

            user.setAvatarUrl("https://img2.baidu.com/it/u=1790834130,1952230725&fm=253&fmt=auto&a pp=138&f=JPEG?w=500&h=500");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setPhone("132132132");
            user.setEmail("13213132@qq.com");
            user.setUserStatus(0);
            user.setUserRole(0);
            user.setPlanetCode("1111");
            userList.add(user);
        }
        userService.saveBatch(userList, 100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }
}
