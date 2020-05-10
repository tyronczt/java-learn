package com.tyron.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 删除测试
 * @Author: tyron
 * @Date: Created in 2020/5/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DeleteTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 根据id删除记录
     * <p>
     * ==>  Preparing: DELETE FROM user WHERE id=?
     * ==>  Parameters: 1251736344246480897(Long)
     * <==  Updates: 1
     * 影响记录数：1
     */
    @Test
    public void delete() {
        int effectNum = userMapper.deleteById(1251736344246480897L);
        System.out.println("影响记录数：" + effectNum);
    }

    /**
     * 根据map删除记录
     * <p>
     * ==>  Preparing: DELETE FROM user WHERE name = ?
     * ==>  Parameters: 彭万里(String)
     * <==  Updates: 1
     * 影响记录数：1
     */
    @Test
    public void deleteByMap() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("name", "彭万里");
        int effectNum = userMapper.deleteByMap(columnMap);
        System.out.println("影响记录数：" + effectNum);
    }

    /**
     * 批量删除记录
     * <p>
     * ==>  Preparing: DELETE FROM user WHERE id IN ( ? , ? )
     * ==>  Parameters: 1259468580001419266(Long), 1259468591753863169(Long)
     * <==  Updates: 2
     * 影响记录数：2
     */
    @Test
    public void deleteBatchIds() {
        int effectNum = userMapper.deleteBatchIds(Arrays.asList(1259468580001419266L, 1259468591753863169L));
        System.out.println("影响记录数：" + effectNum);
    }

    /**
     * 根据条件删除记录
     * <p>
     * ==>  Preparing: DELETE FROM user WHERE (name = ?)
     * ==>  Parameters: 欧阳雨(String)
     * <==  Updates: 1
     * 影响记录数：1
     */
    @Test
    public void deleteByWapper() {
        int effectNum = userMapper.delete(new QueryWrapper<User>().lambda().eq(User::getName, "欧阳雨"));
        System.out.println("影响记录数：" + effectNum);
    }
}
