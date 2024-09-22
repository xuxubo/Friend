package com.guidian.searchFriends.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guidian.searchFriends.mapper.UserMapper;
import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.service.UserService;
import com.guidian.searchFriends.vo.UserPure;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.PublicKey;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceImplTest {

    @Autowired
    private UserService userService;
    @Test
    void searchUserByTags() {
        List<String> tagNameList = Arrays.asList("Java", "Python");
        List<UserPure> userPures = userService.searchUserByTags(tagNameList);
        Assert.assertNotNull(userPures);
    }

    @Test
    void selectUserPage() {

        Page<User> userPage = userService.selectUserPage(1, 5);
        System.out.println(userPage.toString());
    }
}