package com.tyron.mp;

import com.tyron.mp.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 逻辑删除测试
 * @Author: tyron
 * @Date: Created in 2020/5/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TableLogicTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 逻辑删除 TableLogic的使用
     *
     *
     */
    @Test
    public void deleteById() {
        int effectNum = userMapper.deleteById(1094592041087729666L);
        System.out.println("影响行数：" + effectNum);
    }
}
