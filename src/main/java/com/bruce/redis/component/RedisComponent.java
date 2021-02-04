package com.bruce.redis.component;

import com.bruce.redis.lock.Action;
import com.bruce.redis.lock.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc redis 操作类
 * @ProjectName redis
 * @Date 2021/2/4 21:32
 * @Author Bruce
 */
@Component
@Slf4j
public class RedisComponent implements Lock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final long DEFAULT_VALUE = 1;

    private static final long DEFAULT_TIMEOUT = 1;

    private static final TimeUnit DEFAULT_UNIT = TimeUnit.SECONDS;

    /**
     * 加锁标志
     */
    public static final String LOCKED = "LOCKED";


    /**
     * 判断是否存在这个键
     *
     * @param key 键名
     * @return 存在则返回 true，否则返回 false
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 根据前缀获取键
     *
     * @param prefix 前缀，支持 *
     * @return 该前缀查询出来的所有 key
     */
    public Set<String> getKeys(String prefix) {
        return redisTemplate.keys(prefix);
    }


    /**
     * 删除一个键
     *
     * @param key 键名
     */
    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 为设置的键值对设置有效期，有效期默认为 1000 毫秒
     *
     * @param key   键名
     * @param value 值
     */
    public void setExpire(String key, String value) {
        setExpire(key, value, DEFAULT_TIMEOUT, DEFAULT_UNIT);
    }

    /**
     * 为设置的键值对设置有效期
     *
     * @param key      键名
     * @param value    值
     * @param timeout  时间
     * @param timeUnit 时间粒度
     */
    public void setExpire(String key, String value, Long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 返回数据类型
     *
     * @param key 键名
     * @return 数据类型
     * NONE("none"),
     * STRING("string"),
     * LIST("list"),
     * SET("set"),
     * ZSET("zset"),
     * HASH("hash");
     */
    public String getType(String key) {
        return String.valueOf(redisTemplate.type(key));
    }

    /**
     * set
     *
     * @param key   键名称
     * @param value 值
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 值增加减少
     *
     * @param key 键名
     * @param num 正数为加，负数为减
     */
    public void incr(String key, Integer num) {
        redisTemplate.opsForValue().increment(key, num);
    }

    /**
     * get
     *
     * @param key 键名称
     * @return 对应的值
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //redisTemplate.opsForHash()操作Hash表

    /**
     * hset     设置键值对
     *
     * @param hashName 哈希表名
     * @param key      键名称
     * @param value    值
     */
    public void hSet(String hashName, String key, String value) {
        redisTemplate.opsForHash().put(hashName, key, value);
    }

    /**
     * hmset    批量设置
     *
     * @param hashName 哈希表名
     * @param map      存放多个键值对
     */
    public void hmSet(String hashName, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(hashName, map);
    }

    /**
     * hdel 删除
     *
     * @param hashName 哈希表名
     * @param keys     可以同时删除多个 getKey
     */
    public void hDel(String hashName, String... keys) {
        redisTemplate.opsForHash().delete(hashName, keys);
    }

    /**
     * 判断键是否存在
     *
     * @param hashName 哈希表名
     * @param key      键名称
     * @return 键是否存在，存在返回 true，否则返回 false
     */
    public Boolean hasHashKey(String hashName, String key) {
        return redisTemplate.opsForHash().hasKey(hashName, key);
    }

    /**
     * hget    获取某个键值对
     *
     * @param hashName 哈希表名
     * @param key      键名称
     * @return 值
     */
    public String hGet(String hashName, String key) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(hashName, key);
    }

    /**
     * 判断 hashName 是否存在
     *
     * @param hashName 哈希表名
     * @return 值
     */
    public boolean hExist(String hashName) {
        return redisTemplate.opsForHash().size(hashName) > 0;
    }


    /**
     * 根据 key 获取对应 hash 表的所有键值
     *
     * @param key 哈希表名
     * @return 值
     */
    public Set<String> hKeys(String key) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        return opsForHash.keys(key);
    }

    /**
     * 一次删除多个key
     *
     * @param set
     */
    public void deleteKeys(Collection<String> set) {
        redisTemplate.delete(set);
    }

    /**
     * 对某个 key 的值 +value 并延长 key 的 ttl
     *
     * @param key    键
     * @param value  增加值
     * @param expire 秒
     */
    public void incrAndExpire(String key, long value, long expire) {
        executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] bytes = key.getBytes();
                connection.incrBy(bytes, value);
                connection.expire(bytes, expire);
                // 必须返回 null
                return null;
            }
        });
        // redisTemplate.executePipelined((RedisConnection call) -> {
        //     byte[] bytes = key.getBytes();
        //     call.incrBy(bytes, value);
        //     call.expire(bytes, expire);
        //     return null;
        // });
    }

    public void incrAndExpire(String key, long expire) {
        incrAndExpire(key, DEFAULT_VALUE, expire);
    }

    public void incrAndExpire(String key) {
        incrAndExpire(key, DEFAULT_VALUE, DEFAULT_TIMEOUT);
    }

    /**
     * 用于延迟 expire 时间
     *
     * @param key      key 值
     * @param timeout  延长时间
     * @param timeUnit 单位
     */
    public void expire(String key, Long timeout, TimeUnit timeUnit) {
        redisTemplate.expire(key, timeout, timeUnit);
    }

    /**
     * 获得锁
     * 插入成功返回 true，否则表明已经上锁
     *
     * @param key 锁名称
     */
    public boolean generateLock(String key) {
        return generateLock(key, DEFAULT_TIMEOUT);
    }

    /**
     * 获得锁
     * 插入成功返回 true，否则表明已经上锁
     *
     * @param key     锁名称
     * @param seconds 秒
     */
    public boolean generateLock(String key, long seconds) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, LOCKED, seconds, DEFAULT_UNIT);
        return success != null && success;
    }

    /**
     * 释放锁
     *
     * @param lockName 锁名称
     */
    public void releaseLock(String lockName) {
        deleteKey(lockName);
    }


    public void executePipelined(RedisCallback<?> action) {
        redisTemplate.executePipelined(action);
    }

    @Override
    public <T> T lock4Run(String lockKey, Action<T> action) {
        boolean locked;
        try {
            locked = generateLock(lockKey, 10);
        } catch (Exception e) {
            // 降级处理, 避免redis 挂了导致获取不到锁
            locked = true;
            log.error("获取锁资源失败,lockKey={}", lockKey);
        }
        if (locked) {
            try {
                return action.run();
            } finally {
                releaseLock(lockKey);
            }
        } else {
            log.error("获取锁资源失败,lockKey={}", lockKey);
            throw new RuntimeException("获取锁资源失败,lockKey=" + lockKey);
        }
    }

    @Override
    public <T> T lock4Run(String lockKey, Action<T> action, int retryNum) {
        for (int i = 0; i < retryNum; i++) {
            boolean locked;
            try {
                locked = generateLock(lockKey, 10);
            } catch (Exception e) {
                // 降级处理, 避免redis 挂了导致获取不到锁
                locked = true;
                log.error("获取锁资源失败,lockKey={}", lockKey);
            }
            if (locked) {
                try {
                    return action.run();
                } finally {
                    releaseLock(lockKey);
                }
            } else {
                log.error("获取锁资源失败,lockKey={}", lockKey);
            }
        }
        log.error("尝试" + retryNum + "次，分布式锁均失败");
        //正常，不可能执行到这里
        throw new RuntimeException("尝试" + retryNum + "次，分布式锁均失败,lockKey=" + lockKey);
    }

}
