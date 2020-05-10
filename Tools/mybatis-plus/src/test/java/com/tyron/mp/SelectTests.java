package com.tyron.mp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.additional.query.impl.LambdaQueryChainWrapper;
import com.tyron.mp.entity.User;
import com.tyron.mp.mapper.UserMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
public class SelectTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void select() {
        List<User> users = userMapper.selectList(null);
        Assert.assertEquals(5, users.size());
        users.forEach(System.out::println);
    }

    @Test
    public void selectById() {
        User user = userMapper.selectById(1088248166370832385L);
        System.out.println(user);
    }

    @Test
    public void selectBatchIds() {
        List<User> users = userMapper.selectBatchIds(Arrays.asList(1088248166370832385L, 1251736344246480897L));
        users.forEach(System.out::println);
    }

    @Test
    public void selectByMap() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "王天风");
        List<User> users = userMapper.selectByMap(hashMap);
        users.forEach(System.out::println);
    }

    /**
     * 1、名字中包含雨并且年龄小于40
     * name like '%雨%' and age<40
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ? AND age < ?)
     * Parameters: %雨%(String), 40(Integer)
     */
    @Test
    public void selectOne() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().like(User::getName, "雨").lt(User::getAge, 40));
        users.forEach(System.out::println);
    }

    /**
     * 2、名字中包含雨年并且龄大于等于20且小于等于40并且email不为空
     * name like '%雨%' and age between 20 and 40 and email is not null
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ? AND age BETWEEN ? AND ? AND email IS NOT NULL)
     * Parameters: %雨%(String), 20(Integer), 40(Integer)
     */
    @Test
    public void selectTwo() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().like(User::getName, "雨").between(User::getAge, 20, 40).isNotNull(User::getEmail));
        users.forEach(System.out::println);
    }

    /**
     * 3、名字为王姓或者年龄大于等于25，按照年龄降序排列，年龄相同按照id升序排列
     * name like '王%' or age>=25 order by age desc,id asc
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ? OR age >= ?) ORDER BY age DESC , id ASC
     * Parameters: 王%(String), 25(Integer)
     */
    @Test
    public void selectThree() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().likeRight(User::getName, "王")
                .or().ge(User::getAge, 25).orderByDesc(User::getAge).orderByAsc(User::getId));
        users.forEach(System.out::println);
    }

    /**
     * 4、创建日期大于当天并且直属上级为名字为王姓
     * date_format(create_time,'%Y-%m-%d')>= LocalDate.now() and manager_id in (select id from user where name like '王%')
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (date_format(create_time,'%Y-%m-%d') >= ? AND manager_id IN
     * (select id from user where name like '王%'))
     * 
     * Parameters: 2020-04-19(LocalDate)
     */
    @Test
    public void selectFour() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().apply("date_format(create_time,'%Y-%m-%d') >= {0}", LocalDate.now()).
                inSql(User::getManagerId, "select id from user where name like '王%'"));
        users.forEach(System.out::println);
    }

    /**
     * 5、名字为王姓并且（年龄小于40或邮箱不为空）
     * name like '王%' and (age<40 or email is not null)
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ? AND ( (age < ? OR email IS NOT NULL) ))
     * 王%(String), 40(Integer)
     */
    @Test
    public void selectFive() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().likeRight(User::getName, "王").
                and(i -> i.lt(User::getAge, 40).or().isNotNull(User::getEmail)));
        users.forEach(System.out::println);
    }

    /**
     * 6、名字为王姓或者（年龄小于40并且年龄大于20并且邮箱不为空）
     * name like '王%' or (age<40 and age>20 and email is not null)
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ? OR ( (age < ? AND age > ? AND email IS NOT NULL) ))
     * 王%(String), 40(Integer), 20(Integer)
     */
    @Test
    public void selectSix() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().likeRight(User::getName, "王").
                or(i -> i.lt(User::getAge, 40).gt(User::getAge, 20).isNotNull(User::getEmail)));
        users.forEach(System.out::println);
    }

    /**
     * 7、（年龄小于40或邮箱不为空）并且名字为王姓
     * (age<40 or email is not null) and name like '王%'
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (( (age < ? OR email IS NOT NULL) ) AND name LIKE ?)
     * 40(Integer), 王%(String)
     */
    @Test
    public void selectSeven() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().and(i -> i.lt(User::getAge, 40).
                or().isNotNull(User::getEmail)).likeRight(User::getName, "王"));
        users.forEach(System.out::println);
    }

    /**
     * 8、年龄为30、31、34、35
     * age in (30、31、34、35)
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (age IN (?,?,?,?))
     * 30(Integer), 31(Integer), 34(Integer), 35(Integer)
     */
    @Test
    public void selectEight() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().in(User::getAge, Arrays.asList(30, 31, 34, 35)));
        users.forEach(System.out::println);
    }

    /**
     * 9、只返回满足条件的其中一条语句即可
     * limit 1
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (age IN (?,?,?,?)) limit 1
     * 30(Integer), 31(Integer), 34(Integer), 35(Integer)
     */
    @Test
    public void selectNine() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().in(User::getAge, Arrays.asList(30, 31, 34, 35)).last("limit 1"));
        users.forEach(System.out::println);
    }

    /**
     * 10、名字中包含雨并且年龄小于40(需求1加强版)
     * 第一种情况：select id,name
     * from user
     * where name like '%雨%' and age<40
     * 
     * SELECT id,name FROM user WHERE (name LIKE ? AND age < ?)
     * Parameters: %雨%(String), 40(Integer)
     * 
     * 第二种情况：select id,name,age,email
     * from user
     * where name like '%雨%' and age<40
     * 
     * SELECT id,name,email,age FROM user WHERE (name LIKE ? AND age < ?)
     * Parameters: %雨%(String), 40(Integer)
     */
    @Test
    public void selectTen1() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().select(User::getId, User::getName).like(User::getName, "雨").lt(User::getAge, 40));
        users.forEach(System.out::println);
    }

    @Test
    public void selectTen2() {
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().select(User.class, one -> !one.getColumn().equals("create_time")
                && !one.getColumn().equals("manager_id")).like(User::getName, "雨").lt(User::getAge, 40));
        users.forEach(System.out::println);
    }

    /**
     * 11、按照直属上级分组，查询每组的平均年龄、最大年龄、最小年龄。并且只取年龄总和小于500的组。
     * select avg(age) avg_age,min(age) min_age,max(age) max_age
     * from user group by manager_id
     * having sum(age) <500
     * 
     * SELECT avg(age) avg_age,min(age) min_age,max(age) max_age FROM user GROUP BY manager_id HAVING sum(age) < ?
     * Parameters: 500(Integer)
     */
    @Test
    public void selectEleven() {
        List<Map<String, Object>> users = userMapper.selectMaps(new QueryWrapper<User>().select("avg(age) avg_age", "min(age) min_age", "max(age) max_age").lambda().
                groupBy(User::getManagerId).having("sum(age) < {0}", 500));
        users.forEach(System.out::println);
    }

    /**
     * 12、按条件查询用户，姓名为王(查询条件有姓名和email）
     * 当email为空时，不作为查询条件
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ?)
     * Parameters: %王%(String)
     */
    @Test
    public void selectTwelve() {
        String name = "王";
        String email = "";
        List<User> users = userMapper.selectList(new QueryWrapper<User>().lambda().like(StringUtils.isNotEmpty(name), User::getName, name).
                like(StringUtils.isNotEmpty(email), User::getEmail, email));
        users.forEach(System.out::println);
    }

    /**
     * 根据 Wrapper 条件，查询全部记录。注意： 只返回第一个字段的值
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ?)
     * Parameters: %雨%(String)
     * result: <==      Total: 3
     * 1094590409767661570
     * 1094592041087729666
     * 1251822253776068610
     */
    @Test
    public void selectObjs() {
        List<Object> users = userMapper.selectObjs(new QueryWrapper<User>().lambda().like(User::getName, "雨"));
        users.forEach(System.out::println);
    }

    /**
     * 根据 Wrapper 条件，查询总记录数
     * 
     * SELECT COUNT( 1 ) FROM user WHERE (name LIKE ?)
     * Parameters: %雨%(String)
     * 查询数量：3
     */
    @Test
    public void selectCount() {
        Integer count = userMapper.selectCount(new QueryWrapper<User>().lambda().like(User::getName, "雨"));
        System.out.println("查询数量：" + count);
    }

    /**
     * 根据 entity 条件，查询一条记录
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ?) limit 1
     * Parameters: %雨%(String)
     */
    @Test
    public void selectOneUser() {
        User user = userMapper.selectOne(new QueryWrapper<User>().lambda().like(User::getName, "雨").last("limit 1"));
        System.out.println("查询用户：" + user);
    }

    /**
     * 通过map查询列表
     * 
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name = ? AND age = ?)
     * Parameters: 李艺伟(String), 28(Integer)
     */
    @Test
    public void selectAlleq() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("name", "李艺伟");
        hashMap.put("age", 28);
        hashMap.put("email", null);
        List<User> users = userMapper.selectList(new QueryWrapper<User>().allEq(hashMap, false));
        users.forEach(System.out::println);
    }

    /**
     * 为了减少实现类的代码量 --> LambdaQueryChainWrapper
     * 
     * Preparing: SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ?)
     * Parameters: %雨%(String)
     */
    @Test
    public void selectLambdaQueryChainWrapper() {
        List<User> users = new LambdaQueryChainWrapper<>(userMapper).like(User::getName, "雨").list();
        users.forEach(System.out::println);
    }

    /*********************************分页方法***************************************/
    /**
     * SELECT id,create_time,name,manager_id,email,age FROM user WHERE (name LIKE ?) LIMIT ?,?
     * Parameters: %雨%(String), 0(Long), 2(Long)
     * 
     * User(id=1094590409767661570, name=张雨琪, age=31, email=zjq@baomidou.com, managerId=1088248166370832385, createTime=2019-01-14T09:15:15)
     * User(id=1094592041087729666, name=刘红雨, age=32, email=lhm@baomidou.com, managerId=1088248166370832385, createTime=2019-01-14T09:48:16)
     */
    @Test
    public void selectPage() {
        Page<User> page = new Page<>(1, 2, false);
        IPage<User> userIPage = userMapper.selectPage(page, new QueryWrapper<User>().lambda().like(User::getName, "雨"));
        List<User> records = userIPage.getRecords();
        records.forEach(System.out::println);
    }

    /**
     * Preparing: SELECT name FROM user WHERE (name LIKE ?) LIMIT ?,?
     * Parameters: %雨%(String), 0(Long), 2(Long)
     * 
     * {name=张雨琪}
     * {name=刘红雨}
     */
    @Test
    public void selectMapsPage() {
        Page<User> page = new Page<>(1, 2, false);
        IPage<Map<String, Object>> userIPage = userMapper.selectMapsPage(page, new QueryWrapper<User>().lambda().select(User::getName).like(User::getName, "雨"));
        List<Map<String, Object>> records = userIPage.getRecords();
        records.forEach(System.out::println);
    }
}
