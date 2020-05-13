package com.tyron.mp;

import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 自动填充测试
 * @Author: tyron
 * @Date: Created in 2020/5/13
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class FillTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 新增用户，会自动填充操作时间
     *
     * ---insertFill---
     * ==>  Preparing: INSERT INTO user ( id, create_time, name, manager_id, email, age ) VALUES ( ?, ?, ?, ?, ?, ? )
     * ==> Parameters: 1260593237899411457(Long), 2020-05-13T23:28:14.124(LocalDateTime), 陈海华(String), 1088250446457389058(Long), chh@tyron.com(String), 23(Integer)
     * <==    Updates: 1
     * 影响行数：1
     */
    @Test
    public void insert() {
        User user = new User();
        user.setAge(23);
        user.setName("陈海华");
        user.setEmail("chh@tyron.com");
        user.setManagerId(1088250446457389058L);
        int effectNum = userMapper.insert(user);
        System.out.println("影响行数：" + effectNum);
    }

    /**
     * 更新用户信息，会自动填充更新时间
     *
     * ---updateFill---
     * ==>  Preparing: UPDATE user SET update_time=?, age=? WHERE id=? AND deleted=0
     * ==> Parameters: 2020-05-13T23:37:12.779(LocalDateTime), 24(Integer), 1260593237899411457(Long)
     * <==    Updates: 1
     * 影响行数：1
     */
    @Test
    public void updateById() {
        User user = new User();
        user.setId(1260593237899411457L);
        user.setAge(24);
        int effectNum = userMapper.updateById(user);
        System.out.println("影响行数：" + effectNum);
    }
}
