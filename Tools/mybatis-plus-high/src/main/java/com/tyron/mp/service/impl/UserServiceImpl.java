package com.tyron.mp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import com.tyron.mp.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @Description: 用户服务接口实现类
 * @Author: tyron
 * @Date: Created in 2020/5/10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
