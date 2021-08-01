package com.tyron.design.pattern.design.handle.handleimpl;

import com.tyron.design.pattern.constans.CompanyConstans;
import com.tyron.design.pattern.design.handle.CompanyFactory;
import com.tyron.design.pattern.design.handle.Handle;
import org.springframework.stereotype.Component;

/**
 * @Description: 京东入库实现类
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
@Component
public class JdHandleImpl implements Handle {

    @Override
    public void save2DB(String company) {
        // 入库逻辑
        System.out.println("京东数据入库。。。");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CompanyFactory.register(CompanyConstans.JD, this);
    }
}
