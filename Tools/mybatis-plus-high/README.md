## [MyBatis-Plus](https://mybatis.plus/) 的进阶

### 逻辑删除的运用

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
      logic-delete-value: 1
      logic-not-delete-value: 0
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

### 自动填充的运用

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

