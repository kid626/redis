package com.bruce.redis.component;

import com.alibaba.fastjson.JSONObject;
import com.bruce.redis.entity.Gender;
import com.bruce.redis.entity.User;
import com.bruce.redis.thread.MyRunner;
import com.bruce.redis.thread.MyThread;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName redis
 * @Date 2021/2/4 21:37
 * @Author Bruce
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class RedisComponentTest {


    @Autowired
    private RedisComponent redisComponent;


    @Test
    public void exists() {
        System.out.println(redisComponent.exists("KEY"));
    }

    @Test
    public void getKeys() {
        System.out.println(redisComponent.getKeys("KEY*"));
    }

    @Test
    public void deleteKey() {
        redisComponent.deleteKey("KEY1");
    }

    @Test
    public void setExpire() {
        redisComponent.setExpire("KEY1", "test");
    }

    @Test
    public void hmSet() {
        Map<String, String> map = new HashMap<>();
        map.put("key1", "key1");
        map.put("key2", "key2");
        redisComponent.hmSet("HASH", map);
    }

    @Test
    public void hDel() {
        redisComponent.hDel("HASH", "key1", "key2", "key3");
    }


    @Test
    public void hasHashKey() {
        System.out.println(redisComponent.hasHashKey("HASH", "key1"));
    }

    @Test
    public void incrAndExpire() {
        redisComponent.incrAndExpire("test", 1, 100);
    }

    @Test
    public void generateLock() throws InterruptedException {
        boolean test1 = redisComponent.generateLock("test");
        System.out.println(test1);
        Thread.sleep(1000);
        boolean test2 = redisComponent.generateLock("test");
        System.out.println(test2);
    }

    @Test
    public void serialize() {
        String key = "user:{0}";
        User user = new User();
        user.setId(1L);
        user.setName("bruce");
        Gender gender = new Gender();
        gender.setId(1L);
        gender.setName("男");
        user.setGender(gender);
        String format = MessageFormat.format(key, user.getId());
        redisComponent.set(format, JSONObject.toJSONString(user));
        System.out.println(redisComponent.get(format));
    }

    @Test
    public void serializeGet() {
        String key = "user:{0}";
        String format = MessageFormat.format(key, 1);
        System.out.println(redisComponent.get(format));
    }

    @Test
    public void readObj() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("obj.txt"));
        //对象的反序列化。
        User p = (User) ois.readObject();
        System.out.println(p.getName() + ":" + p.getId());
        ois.close();
    }

    @Test
    public void writeObj() throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("obj.txt"));
        //对象序列化。  被序列化的对象必须实现Serializable接口。
        User user = new User();
        user.setId(1L);
        user.setName("bruce");
        Gender gender = new Gender();
        gender.setId(1L);
        gender.setName("男");
        user.setGender(gender);
        oos.writeObject(user);
        oos.close();
    }

    @Test
    public void executePipelined() {
        // redisComponent.executePipelined((RedisConnection connection) -> {
        //     for (int i = 0; i < 100; i++) {
        //         redisComponent.setExpire("test:" + i, String.valueOf(i), 60L, TimeUnit.SECONDS);
        //     }
        //     return null;
        // });
        redisComponent.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (int i = 0; i < 100; i++) {
                    redisComponent.setExpire("test:" + i, String.valueOf(i), 60L, TimeUnit.SECONDS);
                }
                return null;
            }
        });
    }

    @Test
    public void lockTest() throws InterruptedException {
        Thread thread1 = new Thread(new MyRunner(redisComponent));
        thread1.start();
        Thread.sleep(1000);
        Thread thread2 = new Thread(new MyRunner(redisComponent));
        thread2.start();
        while (true) {

        }
    }

    @Test
    public void lockTestV2() throws InterruptedException {
        MyThread thread1 = new MyThread(redisComponent);
        thread1.start();
        Thread.sleep(1000);
        MyThread thread2 = new MyThread(redisComponent);
        thread2.start();
        while (true) {

        }
    }


}
