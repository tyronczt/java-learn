package com.tyron.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 乐观锁测试
 * @Author: tyron
 * @Date: Created in 2020/5/17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class OptLockTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 乐观锁-更新用户数据
     *
     * ==>  Preparing: UPDATE user SET update_time=?, version=?, age=? WHERE id=? AND version=? AND deleted=0
     * ==> Parameters: 2020-05-17T20:42:39.527(LocalDateTime), 2(Integer), 29(Integer), 1260593237899411457(Long), 1(Integer)
     * <==    Updates: 1
     * 影响行数：1
     */
    @Test
    public void updateById() {
        // 模拟从数据库取出版本信息
        int version = 1;

        User user = new User();
        user.setId(1260593237899411457L);
        user.setVersion(version);
        user.setAge(29);
        int effectNum = userMapper.updateById(user);
        System.out.println("影响行数：" + effectNum);
    }

    /**
     * 在 update(entity, wrapper) 方法下, wrapper 不能复用!!!
     *
     * ==>  Preparing: UPDATE user SET update_time=?, version=?, email=? WHERE deleted=0 AND (age = ? AND version = ?)
     * ==> Parameters: 2020-05-17T20:54:40.675(LocalDateTime), 3(Integer), 123@321.com(String), 29(Integer), 2(Integer)
     * <==    Updates: 1
     * 影响行数：1
     * ==>  Preparing: UPDATE user SET update_time=?, version=? WHERE deleted=0 AND (age = ? AND version = ? AND age = ? AND version = ?)
     * ==> Parameters: 2020-05-17T20:54:41.847(LocalDateTime), 4(Integer), 29(Integer), 2(Integer), 29(Integer), 3(Integer)
     * <==    Updates: 0
     * 影响行数：0
     */
    @Test
    public void updateError() {
        int version2 = 2;
        User user2 = new User();
        user2.setId(1260593237899411457L);
        user2.setVersion(version2);
        user2.setEmail("123@321.com");
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.lambda().eq(User::getAge, 29);
        int effectNum2 = userMapper.update(user2, userQueryWrapper);
        System.out.println("影响行数：" + effectNum2);


        int version3 = 3;
        User user3 = new User();
        user3.setId(1260593237899411457L);
        user3.setVersion(version3);
        user2.setEmail("323@123.com");
        userQueryWrapper.lambda().eq(User::getAge, 29);
        int effectNum3 = userMapper.update(user3, userQueryWrapper);
        System.out.println("影响行数：" + effectNum3);
    }

}
