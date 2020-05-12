## [MyBatis-Plus](https://mybatis.plus/) 的快速入门

[MyBatis-Plus](https://github.com/baomidou/mybatis-plus)（简称 MP）是一个 [MyBatis](http://www.mybatis.org/mybatis-3/) 的增强工具，在 MyBatis 的基础上只做增强不做改变，为简化开发、提高效率而生。

MyBatis-Plus的特点：

- 润物无声：只做增强不做改变，引入它不会对现有工程产生影响，如丝般顺滑；
- 效率至上：只需简单配置，即可快速进行 CRUD 操作，从而节省大量时间；
- 丰富功能：热加载、代码生成、分页、性能分析等功能一应俱全。

本文主要基于 MyBatis-Plus 的**官方文档**：https://mybatis.plus/guide/ 及 慕课网的 **MyBatis-Plus入门**：https://www.imooc.com/learn/1130 进行案例说明。

快速开始参考官方文档：[https://mybatis.plus/guide/quick-start.html](https://mybatis.plus/guide/quick-start.html#初始化工程)【很详细】

本项目GitHub地址：https://github.com/tyronczt/java-learn/tree/master/Tools/mybatis-plus

### CRUD--增加(Create)

GitHub地址：https://github.com/tyronczt/java-learn/blob/master/Tools/mybatis-plus/src/test/java/com/tyron/mp/InsertTest.java

```java
public void insert() {
    User user = new User();
    user.setName("彭万里");
    user.setAge(39);
    user.setCreateTime(LocalDateTime.now());
    user.setManagerId(1088248166370832385L);
    int effectNum = userMapper.insert(user);
    Assert.assertEquals(1, effectNum);
}
```

### CRUD--读取(Retrieve)

GitHub地址：https://github.com/tyronczt/java-learn/blob/master/Tools/mybatis-plus/src/test/java/com/tyron/mp/SelectTests.java

```java
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
```

### CRUD--更新(Update)

GitHub地址：https://github.com/tyronczt/java-learn/blob/master/Tools/mybatis-plus/src/test/java/com/tyron/mp/UpdateTest.java

```java
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
```

### CRUD--删除(Delete)

GitHub地址：https://github.com/tyronczt/java-learn/blob/master/Tools/mybatis-plus/src/test/java/com/tyron/mp/DeleteTest.java

```java
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
```

### AR模式

GitHub地址：https://github.com/tyronczt/java-learn/blob/master/Tools/mybatis-plus/src/test/java/com/tyron/mp/ARModelTest.java

```java
 	/**
     * AR 模式新增记录
     * 需要在User类中继承Model<User>
     * 
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
```

### 服务类接口

GitHub：https://github.com/tyronczt/java-learn/blob/master/Tools/mybatis-plus/src/test/java/com/tyron/mp/ServiceTest.java

服务接口需要继承IService，形如：extends IService<User>

服务接口实现类需要继承ServiceImpl，形如：extends ServiceImpl<UserMapper, User>

```java
	/**
     * 批量保存
     * 
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
     * 
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
     * 
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
     * 
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
```

