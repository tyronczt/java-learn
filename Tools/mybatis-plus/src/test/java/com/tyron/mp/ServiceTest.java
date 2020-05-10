package com.tyron.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tyron.mp.entity.User;
import com.tyron.mp.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: 服务类测试
 * @Author: tyron
 * @Date: Created in 2020/5/10
 *
 * 服务接口需要继承IService，形如：extends IService<User>
 * @see com.tyron.mp.service.UserService
 *
 * 服务接口实现类需要继承ServiceImpl，形如：extends ServiceImpl<UserMapper, User>
 * @see com.tyron.mp.service.impl.UserServiceImpl
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ServiceTest {

    @Autowired
    private UserService userService;

    /**
     * 批量保存
     * <p>
     * ==>  Preparing: INSERT INTO user ( id, name, age ) VALUES ( ?, ?, ? )
     * ==> Parameters: 1259497162086076417(Long), 张华(String), 33(Integer)
     * ==> Parameters: 1259497162241265666(Long), 岳华(String), 32(Integer)
     * true
     */
    @Test
    public void saveBatch() {
        User user1 = new User();
        user1.setName("张华");
        user1.setAge(33);

        User user2 = new User();
        user2.setName("岳华");
        user2.setAge(32);
        List<User> userList = Arrays.asList(user1, user2);
        boolean saveBatch = userService.saveBatch(userList);
        System.out.println(saveBatch);
    }

    /**
     * 批量保存或更新
     * <p>
     * ==>  Preparing: INSERT INTO user ( id, name, age ) VALUES ( ?, ?, ? )
     * ==> Parameters: 1259497692317458433(Long), 张华(String), 31(Integer)
     * ==>  Preparing: SELECT id,create_time,name,manager_id,email,age FROM user WHERE id=?
     * ==> Parameters: 1259497162241265666(Long)
     * <==    Columns: id, create_time, name, manager_id, email, age
     * <==        Row: 1259497162241265666, null, 岳华, null, null, 32
     * <==      Total: 1
     * ==>  Preparing: UPDATE user SET name=?, age=? WHERE id=?
     * ==> Parameters: 岳华花(String), 30(Integer), 1259497162241265666(Long)
     * true
     */
    @Test
    public void saveOrUpdateBatch() {
        User user1 = new User();
        user1.setName("张华");
        user1.setAge(31);

        User user2 = new User();
        user2.setId(1259497162241265666L);
        user2.setName("岳华花");
        user2.setAge(30);
        List<User> userList = Arrays.asList(user1, user2);
        boolean saveBatch = userService.saveOrUpdateBatch(userList);
        System.out.println(saveBatch);
    }

    /**
     * Expected one result (or null) to be returned by selectOne(), but found: 2
     * 增加false参数
     * <p>
     * ==>  Preparing: SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ?)
     * ==> Parameters: %雨%(String)
     * <==    Columns: id, create_time, name, manager_id, email, age
     * <==        Row: 1094590409767661570, 2019-01-14 09:15:15, 张雨琪, 1088248166370832385, zjq@baomidou.com, 32
     * <==        Row: 1094592041087729666, 2019-01-14 09:48:16, 刘红雨, 1088248166370832385, lhm@baomidou.com, 32
     * <==      Total: 2
     * Warn: execute Method There are  2 results.
     * User(id=1094590409767661570, name=张雨琪, age=32, email=zjq@baomidou.com, managerId=1088248166370832385, createTime=2019-01-14T09:15:15)
     */
    @Test
    public void selectOne() {
        User user = userService.getOne(new QueryWrapper<User>().lambda().like(User::getName, "雨"), false);
        System.out.println(user);
    }

    /**
     * selectList
     * <p>
     * ==>  Preparing: SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ?)
     * ==> Parameters: %雨%(String)
     * <==    Columns: id, create_time, name, manager_id, email, age
     * <==        Row: 1094590409767661570, 2019-01-14 09:15:15, 张雨琪, 1088248166370832385, zjq@baomidou.com, 32
     * <==        Row: 1094592041087729666, 2019-01-14 09:48:16, 刘红雨, 1088248166370832385, lhm@baomidou.com, 32
     * <==      Total: 2
     * User(id=1094590409767661570, name=张雨琪, age=32, email=zjq@baomidou.com, managerId=1088248166370832385, createTime=2019-01-14T09:15:15)
     * User(id=1094592041087729666, name=刘红雨, age=32, email=lhm@baomidou.com, managerId=1088248166370832385, createTime=2019-01-14T09:48:16)
     */
    @Test
    public void selectList() {
        List<User> users = userService.lambdaQuery().like(User::getName, "雨").list();
        users.forEach(System.out::println);
    }

    /**
     *按条件更新记录
     * ==>  Preparing: UPDATE user SET age=? WHERE (name LIKE ?)
     * ==> Parameters: 28(Integer), %雨%(String)
     * <==    Updates: 2
     * true
     */
    @Test
    public void updateByWapper() {
        boolean update = userService.lambdaUpdate().like(User::getName, "雨").set(User::getAge, 28).update();
        System.out.println(update);
    }

    /**
     * 按条件删除记录
     * ==>  Preparing: DELETE FROM user WHERE (name LIKE ?)
     * ==> Parameters: %华%(String)
     * <==    Updates: 3
     * true
     */
    @Test
    public void remove() {
        boolean remove = userService.lambdaUpdate().like(User::getName, "华").remove();
        System.out.println(remove);
    }
}