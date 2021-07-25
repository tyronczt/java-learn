package com.tyron.springAopLog.annotation;

import com.tyron.springAopLog.emun.BusinessType;
import com.tyron.springAopLog.emun.OperatorType;

import java.lang.annotation.*;

/**
 * @Description: 日志注解
 * @Author: tyron
 * @Date: Created in 2021/7/24
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块
     */
    public String title() default "";

    /**
     * 功能
     */
    public BusinessType businessType() default BusinessType.OTHER;

    /**
     * 操作人类别
     */
    public OperatorType operatorType() default OperatorType.MANAGE;

    /**
     * 是否保存请求的参数
     */
    public boolean isSaveRequestData() default true;
}
