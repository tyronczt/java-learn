## [MyBatis-Plus](https://mybatis.plus/) 的进阶

### 逻辑删除的运用

1、在删除字段上增加@TableLogic注解

```java
/**
 * 删除标识，0未删除，1已删除
 */
@TableLogic
private Integer deleted;
```

2、配置文件中增加删除标识的值，默认是0表示未删除，1表示已删除

```yaml
mybatis-plus:
  global-config:
    db-config:
      logic-delete-value: 1
      logic-not-delete-value: 0
```

