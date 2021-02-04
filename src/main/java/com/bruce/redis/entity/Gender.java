package com.bruce.redis.entity;

import java.io.Serializable;

/**
 * @Copyright Copyright © 2021 fanzh . All rights reserved.
 * @Desc
 * @ProjectName redis
 * @Date 2021/2/4 21:33
 * @Author Bruce
 */
public class Gender implements Serializable {

    private Long id;

    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Gender{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
