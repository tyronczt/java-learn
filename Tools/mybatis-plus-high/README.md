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