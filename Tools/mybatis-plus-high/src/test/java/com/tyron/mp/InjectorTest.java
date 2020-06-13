package com.tyron.mp;

import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

/**
 * @Description: 注入器测试
 * @Author: tyron
 * @Date: Created in 2020/5/25
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class InjectorTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 删除所有行数
     * <p>
     * ==>  Preparing: delete from user
     * ==> Parameters:
     * <==    Updates: 7
     * 影响的行数：7
     */
    @Test
    public void deleteAll() {
        int effectNums = userMapper.deleteAll();
        System.out.println("影响的行数：" + effectNums);
    }

    /**
     * ==> Preparing: INSERT INTO user (id,create_time,name,update_time,manager_id,version,email,age) VALUES (?,?,?,?,?,?,?,?) , (?,?,?,?,?,?,?,?)
     * ==> Parameters: 1271815324794200065(Long), 2020-06-13T22:42:57.281(LocalDateTime), 黄呼呼(String), null, 1087982257332887553(Long), null, hhh@tyron.com(String), 33(Integer), 1271815324823560193(Long), 2020-06-13T22:42:57.281(LocalDateTime), 张云云(String), null, 1087982257332887553(Long), null, zyy@tyron.com(String), 23(Integer)
     * <==    Updates: 2
     * 影响的行数：2
     */
    @Test
    public void insertBatchSomeColumn() {
        User user1 = new User();
        user1.setName("黄呼呼");
        user1.setEmail("hhh@tyron.com");
        user1.setManagerId(1087982257332887553L);
        user1.setAge(33);

        User user2 = new User();
        user2.setName("张云云");
        user2.setEmail("zyy@tyron.com");
        user2.setManagerId(1087982257332887553L);
        user2.setAge(23);
        int effectNums = userMapper.insertBatchSomeColumn(Arrays.asList(user1, user2));
        System.out.println("影响的行数：" + effectNums);
    }
}
