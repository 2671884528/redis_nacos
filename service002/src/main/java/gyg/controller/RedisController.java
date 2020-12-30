package gyg.controller;

import gyg.utils.JedisUtils;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @auther 郭永钢
 * @data 2020/12/29 0:56
 * @desc:
 */
@RestController
public class RedisController {
    @Autowired
    StringRedisTemplate redisTemplate;

    @Value("${server.port}")
    String port;

    @GetMapping("/buy")
    @ResponseBody
    public String get() throws Exception {
        String res = redisTemplate.opsForValue().get("goods:001");
        int num = res == null ? 0 : Integer.parseInt(res);

        String value = UUID.randomUUID().toString() + Thread.currentThread().getName();
        String lock = "gyg";
        try {

            //分布式锁，原生的。相当于String得nx
            Boolean flag = redisTemplate.opsForValue().setIfAbsent(lock, value);
            if (!flag) {
                return "抢占失败";
            }

            if (num > 0) {
                int realnum = num - 1;
                //有弊端，会导致误删别得线程的锁
                //A线程加锁，当A线程调用别的业务时，别的业务宕机了，A线程时间超过10秒
                //删除锁，B线程检测到没有锁，自己加锁进入，当别的业务恢复，A线程往下走
                //删除锁，此时删除的是B线程所加的锁。
                //解决的办法就是，每一个锁有一个唯一标识，判断唯一标识是不是自己的在删除
                redisTemplate.opsForValue().set("goods:001", String.valueOf(realnum), 10L, TimeUnit.SECONDS);

                System.out.println("剩余：" + realnum + "服务端口：" + port);
                return "剩余：" + realnum + "服务端口：" + port;
            } else {
                return "商品售罄";
            }
        } finally {
         /*   // value的值必须是随机数主要是为了更安全的释放锁，释放锁的时候使用脚本告诉Redis:
            // 只有key存在并且存储的值和我指定的值一样才能告诉我删除成功。可以通过以下Lua脚本实现
            if (redisTemplate.opsForValue().get(lock).equalsIgnoreCase(value))
                //释放锁
                redisTemplate.delete(lock);

            ////////////////////////

            while (true) {
                //CAS
                redisTemplate.watch(lock);
                //开启事务
                redisTemplate.setEnableTransactionSupport(true);
                if (redisTemplate.opsForValue().get(lock).equalsIgnoreCase(value)) {
                    {
                        //开始事务
                        redisTemplate.multi();
                        //释放锁
                        redisTemplate.delete(lock);
                        //结束事务
                        List<Object> list = redisTemplate.exec();

                        if (list == null) {
                            continue;
                        }
                    }
                    redisTemplate.unwatch();
                    break;
                }
            }*/
            Jedis jedis = JedisUtils.getJedis();
            try {
                String script = "if redis.call('get',KEYS[1]) == ARGV[1] " +
                        "then" +
                        "    return redis.call('del',KEYS[1]) " +
                        "else " +
                        "    return 0 " +
                        "end";
                Object eval = jedis.eval(script, Collections.singletonList(lock), Collections.singletonList(value));
                if ("1" == eval.toString()) {
                    System.out.println("------del ok-------");
                } else {
                    System.out.println("------del fail--------");
                }
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
    }

}
