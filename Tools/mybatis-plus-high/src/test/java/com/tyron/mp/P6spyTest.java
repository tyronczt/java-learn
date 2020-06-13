package com.tyron.mp;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @Description: p6spy 测试  ！！！该插件有性能损耗，不建议生产环境使用!!!
 * @Author: tyron
 * @Date: Created in 2020/5/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class P6spyTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 查看SQL执行性能
     *
     *  Consume Time：29 ms 2020-05-17 21:17:22
     *  Execute SQL：select * from user WHERE (age >= 30)
     */
    @Test
    public void selectAll() {
        List<User> userList = userMapper.selectAll(Wrappers.<User>lambdaQuery().ge(User::getAge, 30));
        userList.forEach(System.out::println);
    }
}
