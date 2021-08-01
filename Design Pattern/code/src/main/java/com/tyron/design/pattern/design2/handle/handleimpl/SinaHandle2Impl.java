package com.tyron.design.pattern.design2.handle.handleimpl;

import com.tyron.design.pattern.constans.CompanyConstans;
import com.tyron.design.pattern.design2.handle.CompanyFactory;
import com.tyron.design.pattern.design2.handle.AbstractHandler;
import org.springframework.stereotype.Component;

/**
 * @Description: 新浪入库实现类
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
@Component
public class SinaHandle2Impl extends AbstractHandler {

    @Override
    public String save2DBReturnString(String company) {
        // 入库逻辑
        return "参数返回：新浪数据入库。。。";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        CompanyFactory.register(CompanyConstans.SINA, this);
    }
}
