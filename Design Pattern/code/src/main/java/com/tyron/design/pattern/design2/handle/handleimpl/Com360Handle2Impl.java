package com.tyron.design.pattern.design2.handle.handleimpl;

import com.tyron.design.pattern.constans.CompanyConstans;
import com.tyron.design.pattern.design2.handle.CompanyFactory;
import com.tyron.design.pattern.design2.handle.AbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @Description: 360入库实现类
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
@Component
public class Com360Handle2Impl extends AbstractHandler {

    @Override
    public void save2DBNoReturn(String company) {
        // 入库逻辑
        System.out.println("360数据入库。。。");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CompanyFactory.register(CompanyConstans.COM360, this);
    }
}
