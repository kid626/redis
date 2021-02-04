package com.bruce.redis.lock;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName redis
 * @Date 2021/2/4 21:34
 * @Author Bruce
 */
public interface Lock {

    /**
     * 加锁
     *
     * @param lockKey 锁名称
     * @param action  函数式接口，表示需要完成的动作内容
     * @param <T>     返回值类型
     * @return T
     */
    <T> T lock4Run(String lockKey, Action<T> action);

    /**
     * 加锁
     *
     * @param lockKey  锁名称
     * @param action   函数式接口，表示需要完成的动作内容
     * @param retryNum 允许重试次数
     * @param <T>      返回值类型
     * @return T
     */
    <T> T lock4Run(String lockKey, Action<T> action, int retryNum);
}
