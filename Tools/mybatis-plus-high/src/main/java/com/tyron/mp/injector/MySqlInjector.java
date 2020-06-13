package com.tyron.mp.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.extension.injector.methods.additional.InsertBatchSomeColumn;
import com.tyron.mp.method.DeleteAllMethod;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 自定义SQL注入器
 * @Author: tyron
 * @Date: Created in 2020/5/25
 */
@Component
public class MySqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass) {
        List<AbstractMethod> methodList = super.getMethodList(mapperClass);
        methodList.add(new DeleteAllMethod());
        methodList.add(new InsertBatchSomeColumn(t -> !t.isLogicDelete() && !t.getColumn().equals("age")));// 自带批量插入注入器(逻辑删除字段和年龄字段不填充）
        return methodList;
    }
}
