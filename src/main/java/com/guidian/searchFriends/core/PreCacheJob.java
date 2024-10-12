package com.guidian.searchFriends.core;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guidian.searchFriends.model.User;
import com.guidian.searchFriends.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {

    @Autowired
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    //重点用户
    private List<Long> userIdList = Arrays.asList(1L);

    @Resource
    private RedissonClient redissonClient;



    @Scheduled(cron = "0 0 0 * * ?")
    public void doCacheRecommendUser() {

        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");

        try {
            if(lock.tryLock(0,30000L, TimeUnit.MILLISECONDS)){
                for (Long id : userIdList) {
                    // 从数据库中获取数据
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("yupao:user:recommend:%s", id);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        // 写入Redis(注意设置过期时间)
                        valueOperations.set(redisKey, userPage.getRecords(), 1, TimeUnit.HOURS);
                    } catch (Exception e) {
                        log.error("redis set key error", e);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }


    }
}
