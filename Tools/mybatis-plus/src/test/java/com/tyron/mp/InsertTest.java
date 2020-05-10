package com.tyron.mp;

import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

/**
 * @Description: 插入测试
 * @Author: tyron
 * @Date: Created in 2020/5/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InsertTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void insert() {
        User user = new User();
        user.setName("彭万里");
        user.setAge(39);
        user.setCreateTime(LocalDateTime.now());
        user.setManagerId(1088248166370832385L);
        int effectNum = userMapper.insert(user);
        Assert.assertEquals(1, effectNum);
    }
}
