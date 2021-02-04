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
public class MyThread extends Thread {

    private RedisComponent redisComponent;

    public MyThread(RedisComponent redisComponent) {
        this.redisComponent = redisComponent;
    }


    @Override
    public void run() {
        lock();
    }

    public void lock() {
        log.info("线程:{} 执行时间:{}", Thread.currentThread().getName(), new Date());
        String s = redisComponent.lock4Run("lock", () -> {
            log.info("线程:{} do something", Thread.currentThread().getName());
            return "success";
        });
        log.info("线程:{} 返回结果:{}", Thread.currentThread().getName(), s);
    }

}
