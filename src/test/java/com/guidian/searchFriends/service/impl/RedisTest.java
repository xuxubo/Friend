/**
 * @作者 徐振博
 * @创建时间 2024/9/18 21:48
 */
package com.guidian.searchFriends.service.impl;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;


@SpringBootTest
public class RedisTest {
    //通过名称来进行注入
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("String", "fight");
        valueOperations.set("Int", 1);
        valueOperations.set("Double", 2.0);
//        Object stringValue = valueOperations.get("String");
//        System.out.println(stringValue);
//        valueOperations.set("String","fight1");
//        stringValue = valueOperations.get("String");
//        System.out.println(stringValue);
//
//        redisTemplate.delete("String");
//        redisTemplate.delete("Int");
//        redisTemplate.delete("Double");
    }

}

