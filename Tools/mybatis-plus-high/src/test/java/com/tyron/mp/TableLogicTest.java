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
     * <p>
     * ==>  Preparing: UPDATE user SET deleted=1 WHERE id=? AND deleted=0
     * ==> Parameters: 1094592041087729666(Long)
     * <==    Updates: 1
     * 影响行数：1
     */
    @Test
    public void deleteById() {
        int effectNum = userMapper.deleteById(1094592041087729666L);
        System.out.println("影响行数：" + effectNum);
    }

    /**
     * 根据id查询用户信息，会增加delete=0的条件
     * <p>
     * ==>  Preparing: SELECT id,deleted,create_time,name,update_time,manager_id,version,email,age FROM user WHERE id=? AND deleted=0
     * ==> Parameters: 1094592041087729666(Long)
     * <==      Total: 0
     * 用户信息：null
     * <p>
     * 在deleted字段上增加注解：@TableField(select = false) 不显示deleted字段。
     * ==>  Preparing: SELECT id,create_time,name,update_time,manager_id,version,email,age FROM user WHERE id=? AND deleted=0
     */
    @Test
    public void selectById() {
        User user = userMapper.selectById(1094592041087729666L);
        System.out.println("用户信息：" + user);
    }

    /**
     * 更新用户信息，会增加delete=0的条件
     * <p>
     * ==>  Preparing: UPDATE user SET age=? WHERE id=? AND deleted=0
     * ==> Parameters: 26(Integer), 1094592041087729666(Long)
     * <==    Updates: 0
     * 影响行数：0
     */
    @Test
    public void updateById() {
        User user = new User();
        user.setId(1094592041087729666L);
        user.setAge(26);
        int effectNum = userMapper.updateById(user);
        System.out.println("影响行数：" + effectNum);
    }

    /**
     * 自定义方法中TableLogic不会起作用
     *
     * ==>  Preparing: select * from user WHERE (age >= ?)
     * ==> Parameters: 30(Integer)
     * <==    Columns: id, name, age, email, manager_id, create_time, update_time, version, deleted
     * <==        Row: 1087982257332887553, 大boss, 40, boss@baomidou.com, null, 2019-01-11 14:20:20, null, 1, 0
     * <==        Row: 1094592041087729666, 刘红雨, 31, lhm@baomidou.com, 1088248166370832385, 2019-01-14 09:48:16, null, 1, 1
     * <==      Total: 2
     * User(id=1087982257332887553, name=大boss, age=40, email=boss@baomidou.com, managerId=null, createTime=2019-01-11T14:20:20, updateTime=null, version=1, deleted=0)
     * User(id=1094592041087729666, name=刘红雨, age=31, email=lhm@baomidou.com, managerId=1088248166370832385, createTime=2019-01-14T09:48:16, updateTime=null, version=1, deleted=1)
     */
    @Test
    public void selectAll() {
//        List<User> userList = userMapper.selectAll(new QueryWrapper<User>().lambda().ge(User::getAge, 30));
        List<User> userList = userMapper.selectAll(Wrappers.<User>lambdaQuery().ge(User::getAge, 30));
        userList.forEach(System.out::println);
    }
}
