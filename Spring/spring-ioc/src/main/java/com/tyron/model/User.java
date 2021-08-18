package com.tyron.model;

/**
 * @Description: 用户实例
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
public class User {

    private String name;

    public User() {
        System.out.println("------第一步：User对象实例化工作完成！------");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        System.out.println("------第二步：User对象属性注入完成！------" + name);
        this.name = name;
    }
}
