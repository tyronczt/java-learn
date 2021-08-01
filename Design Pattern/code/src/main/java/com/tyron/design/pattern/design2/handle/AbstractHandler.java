package com.tyron.design.pattern.design2.handle;

import org.springframework.beans.factory.InitializingBean;

/**
 * @Description: 抽象策略加模板处理器
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
public abstract class AbstractHandler implements InitializingBean {

    /**
     * 存入数据库无参返回
     *
     * @param company 公司名
     */
    public void save2DBNoReturn(String company) {
        throw new UnsupportedOperationException();
    }

    /**
     * 存入数据库有String返回
     *
     * @param company 公司名
     * @return
     */
    public String save2DBReturnString(String company) {
        throw new UnsupportedOperationException();
    }
}
