package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Zz
 **/
public class RedisShardedPool {
    public static ShardedJedisPool pool;//sharded jedis连接池

    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total", "20"));    //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle", "10"));
    //在jedispool中最大的idle状态的（空闲的）jedis实例个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle", "2"));
    //在jedispool中最小的idle状态的（空闲的）jedis实例个数
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow", "true"));
    //在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true，则得到的jedis实例是可用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return", "true"));
    //在return一个jedis实例的时候，是否要进行验证操作，如果赋值true，则返回成功

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip = PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port = Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));

    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxIdle(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽时是否阻塞，false会抛出异常，true阻塞直到超时

//        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);
        JedisShardInfo info1 = new JedisShardInfo(redis1Ip, redis1Port, 1000 * 2);
        JedisShardInfo info2 = new JedisShardInfo(redis2Ip, redis2Port, 1000 * 2);

        List<JedisShardInfo> jedisShardInfos = new ArrayList<>(2);

        jedisShardInfos.add(info1);
        jedisShardInfos.add(info2);

        pool = new ShardedJedisPool(config, jedisShardInfos, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    //静态加载
    static {
        initPool();
    }

    public static ShardedJedis getJedis() {
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }

    public static void returnBrokeResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {

        ShardedJedis shardedJedis = pool.getResource();
        for (int i = 0; i < 10; i++) {
            shardedJedis.set("key" + i, "value" + i);
        }
        returnResource(shardedJedis);

    }
}
