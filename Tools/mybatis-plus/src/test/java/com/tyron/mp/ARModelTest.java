package com.tyron.mp;

import com.tyron.mp.entity.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

/**
 * @Description: AR模式
 * @Author: tyron
 * @Date: Created in 2020/5/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class ARModelTest {

    /**
     * AR 模式新增记录
     * 需要在User类中继承Model<User>
     * <p>
     * ==>  Preparing: INSERT INTO user ( id, create_time, name, manager_id, age ) VALUES ( ?, ?, ?, ?, ? )
     * ==>  Parameters: 1259474874313797634(Long), 2020-05-10T21:26:24.419(LocalDateTime), 哈哈哈(String), 1088248166370832385(Long), 29(Integer)
     * <==  Updates: 1
     */
    @Test
    public void insert() {
        User user = new User();
        user.setName("哈哈哈");
        user.setAge(29);
        user.setCreateTime(LocalDateTime.now());
        user.setManagerId(1088248166370832385L);
        boolean ifInsert = user.insert();
        Assert.assertEquals(true, ifInsert);
    }


    /**
     * ==>  Preparing: SELECT id,create_time,name,manager_id,email,age FROM user WHERE id=?
     * ==>  Parameters: 1259474874313797634(Long)
     * <==  Columns: id, create_time, name, manager_id, email, age
     * <==  Row: 1259474874313797634, 2020-05-10 11:26:24, 哈哈哈, 1088248166370832385, null, 29
     * <==  Total: 1
     * User(id=1259474874313797634, name=哈哈哈, age=29, email=null, managerId=1088248166370832385, createTime=2020-05-10T11:26:24)
     * false
     */
    @Test
    public void selectById() {
        User user = new User();
        user.setId(1259474874313797634L);
        User selectUser = user.selectById();
        System.out.println(selectUser);
        System.out.println(user == selectUser);
    }

    /**
     * ==> Preparing: UPDATE user SET age=? WHERE id=?
     * ==> Parameters: 30(Integer), 1259474874313797634(Long)
     * <==    Updates: 1
     * true
     */
    @Test
    public void updateById() {
        User user = new User();
        user.setId(1259474874313797634L);
        user.setAge(30);
        boolean ifUpdate = user.updateById();
        System.out.println(ifUpdate);
    }


    /**
     * ==> Preparing: DELETE FROM user WHERE id=?
     * ==> Parameters: 1259474874313797634(Long)
     * <==    Updates: 1
     * true
     */
    @Test
    public void deleteById() {
        User user = new User();
        user.setId(1259474874313797634L);
        boolean ifDelete = user.deleteById();
        System.out.println(ifDelete);
    }

}