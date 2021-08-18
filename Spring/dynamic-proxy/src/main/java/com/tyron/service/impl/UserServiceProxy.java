package com.tyron.service.impl;

import com.tyron.service.IUserService;

/**
 * @Description: 用户接口代理类
 * @Author: tyron
 * @Date: Created in 2021/8/18
 */
public class UserServiceProxy implements IUserService {

    //目标对象，被代理的对象
    private IUserService target;

    public UserServiceProxy(IUserService target) {
        this.target = target;
    }

    @Override
    public void user1() {
        long startTime = System.nanoTime();
        this.target.user1();
        long endTime = System.nanoTime();
        System.out.println(this.target.getClass() + ".user1()方法耗时(纳秒):" + (endTime - startTime));
    }

    @Override
    public void user2() {
        long startTime = System.nanoTime();
        this.target.user2();
        long endTime = System.nanoTime();
        System.out.println(this.target.getClass() + ".user2()方法耗时(纳秒):" + (endTime - startTime));
    }

    @Override
    public void user3() {
        long startTime = System.nanoTime();
        this.target.user3();
        long endTime = System.nanoTime();
        System.out.println(this.target.getClass() + ".user3()方法耗时(纳秒):" + (endTime - startTime));
    }
}
