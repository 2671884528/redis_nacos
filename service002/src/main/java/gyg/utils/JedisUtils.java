package gyg.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @auther 郭永钢
 * @data 2020/12/30 10:19
 * @desc:
 */

public class JedisUtils {

    private static JedisPool pool;
    static {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(10);
        jedisPoolConfig.setMaxTotal(20);
        pool = new JedisPool("101.37.116.241", 6379);


    }

    public static Jedis getJedis() throws Exception{
        if (pool!=null){
            Jedis jedis = pool.getResource();
            return jedis;
        }
        throw new Exception("Jedis is not ok");
    }
}
