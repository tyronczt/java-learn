package com.tyron.service.impl;

import com.tyron.service.IUserService;

/**
 * @Description: 用户接口实现B
 * @Author: tyron
 * @Date: Created in 2021/8/18
 */
public class UserServiceBImpl implements IUserService {
    @Override
    public void user1() {
        System.out.println("我是实现类B中的user1!");
    }

    @Override
    public void user2() {
        System.out.println("我是实现类B中的user2!");
    }

    @Override
    public void user3() {
        System.out.println("我是实现类B中的user3!");
    }
}
