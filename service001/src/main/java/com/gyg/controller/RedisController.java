package com.gyg.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * @auther 郭永钢
 * @data 2020/12/29 0:56
 * @desc:
 */
@RestController
public class RedisController {
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    private Redisson redisson;
    @Value("${server.port}")
    String port;

    @GetMapping("/buy")
    @ResponseBody
    public String get() {
        String res = redisTemplate.opsForValue().get("goods:001");
        int num = res == null ? 0 : Integer.parseInt(res);
        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
        String lock = "gyg";
        //
        RLock rLock = redisson.getLock(lock);
        rLock.lock();
        try {
            if (num > 0) {
                int realnum = num - 1;
                redisTemplate.opsForValue().set("goods:001", String.valueOf(realnum));
                System.out.println("剩余：" + realnum + "服务端口：" + port);
                return "剩余：" + realnum + "服务端口：" + port;
            } else {
                return "商品售罄";
            }
        }finally {

            if (rLock.isLocked()){
                if (rLock.isHeldByCurrentThread()){
                    rLock.unlock();
                }
            }
        }
    }

}
