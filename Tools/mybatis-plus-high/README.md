## [MyBatis-Plus](https://mybatis.plus/) 的进阶

### [逻辑删除的运用](https://mybatis.plus/guide/logic-delete.html)

1、在删除字段上增加 `@TableLogic` 注解

```java
/**
 * 删除标识，0未删除，1已删除
 * @TableLogic 描述：表字段逻辑处理注解（逻辑删除）
 */
@TableLogic
private Integer deleted;
```

2、配置文件中增加删除标识的值，默认是 0 表示未删除，1 表示已删除

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-value: 1 # 逻辑已删除值(默认为 1)
      logic-not-delete-value: 0 # 逻辑未删除值(默认为 0)
```

3、测试方法

```java
 /**
   * 逻辑删除（TableLogic的使用）
   *
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
   *
   * ==>  Preparing: SELECT id,deleted,create_time,name,update_time,manager_id,version,email,age FROM user WHERE id=? AND deleted=0
   * ==> Parameters: 1094592041087729666(Long)
   * <==      Total: 0
   * 用户信息：null
   *
   * 在deleted字段上增加注解：@TableField(select = false) 不显示deleted字段。
   * ==>  Preparing: SELECT id,create_time,name,update_time,manager_id,version,email,age FROM user WHERE id=? AND deleted=0
   */
@Test
public void selectById() {
    User user = userMapper.selectById(1094592041087729666L);
    System.out.println("用户信息：" + user);
}
```

注意事项：自定义方法中 @TableLogic 不会起作用

### [自动填充的运用](https://mybatis.plus/guide/auto-fill-metainfo.html)

1、在自动填充字段上增加 `@TableField(fill = FieldFill.XXX)` 注解

```java
/**
 * 创建时间
 */
@TableField(fill = FieldFill.INSERT)
private LocalDateTime createTime;

/**
 * 更新时间
 */
@TableField(fill = FieldFill.UPDATE)
private LocalDateTime updateTime;
```

2、自定义表对象处理器 MyMetaObjectHandler，实现 MetaObjectHandler 接口

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        System.out.println("---insertFill---");
        setInsertFieldValByName("createTime", LocalDateTime.now(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        System.out.println("---updateFill---");
        setUpdateFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
}
```

3、测试方法

```java
/**
 *  新增用户，会自动填充操作时间
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
```

4、根据实际情况对处理器优化

```java
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        // 优化1：当有自动填充字段时再进行自动填充
        boolean hasSetter = metaObject.hasSetter("createTime");
        if (hasSetter) {
            System.out.println("---insertFill---");
            setInsertFieldValByName("createTime", LocalDateTime.now(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 优化2：当字段值没有设值时再自动填充
        Object updateTime = getFieldValByName("updateTime", metaObject);
        if (null == updateTime) {
            System.out.println("---updateFill---");
            setUpdateFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }
    }
}
```

### [乐观锁插件的运用](https://mybatis.plus/guide/optimistic-locker-plugin.html)

悲观锁(Pessimistic Locking)，悲观锁是指在数据处理过程，使数据处于锁定状态，一般使用数据库的锁机制实现。适合在写多读少的并发环境中使用，虽然无法维持非常高的性能，但是在乐观锁无法提更好的性能前提下，可以做到数据的安全性。

乐观锁相对悲观锁而言，它认为数据一般情况下不会造成冲突，所以在数据进行提交更新的时候，才会正式对数据的冲突与否进行检测，如果发现冲突了，则让返回错误信息，让用户决定如何去做。

利用数据版本号（**version**）机制是乐观锁最常用的一种实现方式。

1、配置乐观锁插件

```JAVA
/**
 * 乐观锁插件
 */
@Bean
public OptimisticLockerInterceptor optimisticLockerInterceptor() {
	return new OptimisticLockerInterceptor();
}
```

2、在版本字段上增加 `@Version` 注解

```java
@Version
private Integer version;
```

3、测试栗子

```java
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
```

### [执行 SQL 分析打印](https://mybatis.plus/guide/p6spy.html)

1、Maven：

```xml
<dependency>
  <groupId>p6spy</groupId>
  <artifactId>p6spy</artifactId>
  <version>最新版本</version>
</dependency>
```

2、application.yml 配置：

```yaml
spring:
  datasource:
    driver-class-name: com.p6spy.engine.spy.P6SpyDriver
    url: jdbc:p6spy:h2:mem:test
    ...
```

3、spy.properties 配置：

```properties
#3.2.1以上使用
#modulelist=com.baomidou.mybatisplus.extension.p6spy.MybatisPlusLogFactory,com.p6spy.engine.outage.P6OutageFactory
#3.2.1以下使用或者不配置
modulelist=com.p6spy.engine.logging.P6LogFactory,com.p6spy.engine.outage.P6OutageFactory
# 自定义日志打印
logMessageFormat=com.baomidou.mybatisplus.extension.p6spy.P6SpyLogger
#日志输出到控制台
#appender=com.baomidou.mybatisplus.extension.p6spy.StdoutLogger
#日志输出到文件中
logfile=tyron.log
# 使用日志系统记录 sql
#appender=com.p6spy.engine.spy.appender.Slf4JLogger
# 设置 p6spy driver 代理
deregisterdrivers=true
# 取消JDBC URL前缀
useprefix=true
# 配置记录 Log 例外,可去掉的结果集有error,info,batch,debug,statement,commit,rollback,result,resultset.
excludecategories=info,debug,result,commit,resultset
# 日期格式
dateformat=yyyy-MM-dd HH:mm:ss
# 实际驱动可多个
#driverlist=org.h2.Driver
# 是否开启慢SQL记录
outagedetection=true
# 慢SQL记录标准 2 秒
outagedetectioninterval=2
```

### [Sql 注入器](https://mybatis.plus/guide/sql-injector.html)

1、创建定义方法的类

```java
@Override
public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
    // 执行的SQL
    String sql = "delete from " + tableInfo.getTableName();
    // Mapper接口方法名
    String method = "deleteAll";
    SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
    return addDeleteMappedStatement(mapperClass, method, sqlSource);
}
```

2、创建注入器

```java
@Component
public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new DeleteAllMethod());
        return methodList;
    }
}

```

3、在Mapper中加入自定义方法

```java
/**
 * 删除所有数据
 *
 * @return 影响的行数
 */
int deleteAll();
```

4、测试

```java
/**
 * 删除所有行数
 *
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
```

