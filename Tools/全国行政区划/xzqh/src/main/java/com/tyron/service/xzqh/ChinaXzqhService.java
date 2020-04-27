package com.tyron.service.xzqh;

import com.tyron.core.Service;
import com.tyron.pojo.entity.xzqh.ChinaXzqh;

/**
 * @Description: 中国行政区划控制类
 * @Author: tyronchen
 * @Date: Created in 2020/04/27
 */
public interface ChinaXzqhService extends Service<ChinaXzqh> {

    /**
     * 设置高德经纬度
     *
     * @return 成功条数
     */
    int setGdJwd();
}
