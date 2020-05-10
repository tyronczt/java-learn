package com.tyron.mp;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.additional.update.impl.LambdaUpdateChainWrapper;
import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 更新测试
 * @Author: tyron
 * @Date: Created in 2020/5/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据Id更新记录
     * <p>
     * UPDATE user SET email=? WHERE id=?
     * ==> Parameters: aaa@aa.com(String), 1251822253776068610(Long)
     * <==    Updates: 1
     * 更新记录数：1
     */
    @Test
    public void updateById() {
        User user = new User();
        user.setId(1251822253776068610L);
        user.setEmail("aaa@aa.com");
        int effectNum = userMapper.updateById(user);
        System.out.println("更新记录数：" + effectNum);
    }

    /**
     * 根据条件更新记录
     * <p>
     * ==>  Preparing: UPDATE user SET age=? WHERE (name LIKE ?)
     * ==>  Parameters: 30(Integer), %雨%(String)
     * <==  Updates: 3
     * 更新记录数：3
     */
    @Test
    public void updateByWapper() {
        User user = new User();
        user.setAge(30);
        int effectNum = userMapper.update(user, new UpdateWrapper<User>().lambda().like(User::getName, "雨"));
        System.out.println("更新记录数：" + effectNum);
    }

    /**
     * 根据条件更新记录(不设置实体类）
     * <p>
     * ==>  Preparing: UPDATE user SET age=? WHERE (name LIKE ?)
     * ==>  Parameters: 31(Integer), %雨%(String)
     * <==  Updates: 3
     * 更新记录数：3
     */
    @Test
    public void updateByWapperNoEntity() {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.like("name", "雨").set("age", 31);
        int effectNum = userMapper.update(null, updateWrapper);
        System.out.println("更新记录数：" + effectNum);
    }

    /**
     * 通过lambda链并根据条件更新记录
     * <p>
     * ==>  Preparing: UPDATE user SET age=? WHERE (name LIKE ?)
     * ==>  Parameters: 32(Integer), %雨%(String)
     * <==  Updates: 3
     * 是否更新成功：true
     */
    @Test
    public void updateByWapperLambdaChain() {
        boolean ifUpdate = new LambdaUpdateChainWrapper<>(userMapper).like(User::getName, "雨").set(User::getAge, 32).update();
        System.out.println("是否更新成功：" + ifUpdate);
    }
}
