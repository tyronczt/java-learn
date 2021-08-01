package com.tyron.design.pattern.design.handle;

import org.springframework.beans.factory.InitializingBean;

/**
 * @Description: 策略模式
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
public interface Handle extends InitializingBean {

    /**
     * 根据公司名保存入库
     *
     * @param company 公司名
     */
    public void save2DB(String company);
}
