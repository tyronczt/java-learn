package com.tyron.design.pattern.design.handle;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 公司工厂
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
public class CompanyFactory {

    // 存放策略的Map
    public static Map<String, Handle> strategyMap = new HashMap<>();

    /**
     * 将策略注册到map中
     *
     * @param company 公司
     * @param handle  策略
     */
    public static void register(String company, Handle handle) {
        // 判空
        if (StringUtils.isEmpty(company) || null == handle) {
            return;
        }
        strategyMap.put(company, handle);
    }

    /**
     * 根据公司名从map中获取相应策略
     *
     * @param company 公司名
     * @return
     */
    public static Handle getInvokeStrategy(String company) {
        // 判空
        if (StringUtils.isEmpty(company)) {
            return null;
        }
        Handle handle = strategyMap.get(company);
        if (handle == null) {
            throw new UnsupportedOperationException("没有此公司的入库实现");
        }
        return handle;
    }
}
