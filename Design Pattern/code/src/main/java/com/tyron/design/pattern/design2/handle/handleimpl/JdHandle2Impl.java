package com.tyron.design.pattern.design2.handle.handleimpl;

import com.tyron.design.pattern.constans.CompanyConstans;
import com.tyron.design.pattern.design2.handle.CompanyFactory;
import com.tyron.design.pattern.design2.handle.AbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @Description: 京东入库实现类
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
@Component
public class JdHandle2Impl extends AbstractHandler {

    @Override
    public void save2DBNoReturn(String company) {
        // 入库逻辑
        System.out.println("京东数据入库。。。");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CompanyFactory.register(CompanyConstans.JD, this);
    }
}
