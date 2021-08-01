package com.tyron.design.pattern.design2.handle;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 公司工厂类
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
public class CompanyFactory {

    private static Map<String, AbstractHandler> strategyMap = new HashMap<>();

    public static void register(String company, AbstractHandler abstractHandler) {
        // 判空
        if (StringUtils.isEmpty(company) || null == abstractHandler) {
            return;
        }
        strategyMap.put(company, abstractHandler);
    }

    public static AbstractHandler getInvokeStrategy(String company) {
        if (StringUtils.isEmpty(company)) {
            return null;
        }
        AbstractHandler abstractHandler = strategyMap.get(company);
        if (null == abstractHandler) {
            throw new UnsupportedOperationException("该公司无入库业务方法");
        }
        return abstractHandler;
    }
}
