package com.tyron.service.xzqh.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tyron.core.AbstractService;
import com.tyron.mapper.xzqh.ChinaXzqhMapper;
import com.tyron.pojo.entity.xzqh.ChinaXzqh;
import com.tyron.service.xzqh.ChinaXzqhService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: 中国行政区划控制类
 * @Author: tyronchen
 * @Date: Created in 2020/04/27
 */
@Service
public class ChinaXzqhServiceImpl extends AbstractService<ChinaXzqh> implements ChinaXzqhService {

    @Resource
    private ChinaXzqhMapper chinaXzqhMapper;

    private static String GD_URL = "https://restapi.amap.com/v3/place/text";
    private static String GD_KEY = "2dc01264926fb4b75e80a991da977a93";

    @Override
    public int setGdJwd() {
        // 全国
        List<ChinaXzqh> chinaXzqhs = chinaXzqhMapper.selectList(null);
        // 浙江
//        List<ChinaXzqh> chinaXzqhs = chinaXzqhMapper.selectList(new QueryWrapper<ChinaXzqh>().lambda().likeRight(ChinaXzqh::getCode, "33"));
        int effectedNums = 0;
        for (ChinaXzqh chinaXzqh : chinaXzqhs) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("keywords", chinaXzqh.getNameplus());
            paramMap.put("output", "JSON");
            paramMap.put("city", chinaXzqh.getCode());
            paramMap.put("offset", 1);
            paramMap.put("page", 1);
            paramMap.put("key", GD_KEY);
            paramMap.put("extensions", "all");
            String urlResult = HttpUtil.get(GD_URL, paramMap);
            JSONObject jsonResult = JSONUtil.parseObj(urlResult);
            // 当请求成功时，开始解析结果
            if (jsonResult.getStr("status").equals("1")) {
                JSONArray jsonArray = jsonResult.getJSONArray("pois");
                JSONObject result = JSONUtil.parseObj(jsonArray.get(0));
                String location = result.getStr("location");
                // 解析经纬度
                if (StringUtils.isNotBlank(location)) {
                    String[] locations = location.split(",");
                    if (locations.length == 2) {
                        chinaXzqh.setGdJd(locations[0]);
                        chinaXzqh.setGdWd(locations[1]);
                    }
                }
                chinaXzqh.setAddress(result.getStr("address"));
                chinaXzqh.setTel(result.getStr("tel"));
                chinaXzqh.setCitycode(result.getStr("citycode"));
                chinaXzqh.setCityname(result.getStr("cityname"));
                chinaXzqh.setFullname(result.getStr("name"));
                chinaXzqh.setPname(result.getStr("pname"));
                chinaXzqh.setPcode(result.getStr("pcode"));
                int effectedNum = chinaXzqhMapper.updateById(chinaXzqh);
                effectedNums = +effectedNum;
            }
        }
        return effectedNums;
    }
}
