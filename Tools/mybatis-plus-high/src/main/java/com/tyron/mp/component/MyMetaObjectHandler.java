package com.tyron.mp.component;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @Description: 自定义表对象处理器
 * @Author: tyron
 * @Date: Created in 2020/5/13
 */
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