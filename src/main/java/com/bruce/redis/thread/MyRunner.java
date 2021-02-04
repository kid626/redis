package com.bruce.redis.thread;

import com.bruce.redis.component.RedisComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName redis
 * @Date 2021/2/4 21:35
 * @Author Bruce
 */
@Slf4j
public class MyRunner implements Runnable {

    private RedisComponent redisComponent;

    public MyRunner(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }

    @Override
    public void run() {
        log.info("线程:{} 执行时间:{}", Thread.currentThread().getName(), new Date());
        String key = "lock";
        if (!redisComponent.generateLock(key)) {
            log.info("线程:{} 获取资源失败", Thread.currentThread().getName());
            throw new RuntimeException("获取锁资源失败,lockKey=" + key);
        }
        try {
            log.info("线程:{} do something", Thread.currentThread().getName());
            log.info("线程:{} 返回结果:{}", Thread.currentThread().getName(), "success");
        } finally {
            redisComponent.releaseLock(key);
        }
    }
}
