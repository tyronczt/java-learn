# 获取最新2020全国行政区划及经纬度

> 源码：https://github.com/tyronczt/java-learn/tree/master/Tools/China-Xzqh

最后效果

![中国行政区划](https://github.com/tyronczt/java-learn/blob/master/Tools/China-Xzqh/xzqh-sql.png)

## 2020年中华人民共和国县以上行政区划代码

http://www.mca.gov.cn/article/sj/xzqh/2020/

## 新建数据库

```sql
-- ----------------------------
-- Table structure for china_xzqh
-- ----------------------------
DROP TABLE IF EXISTS `china_xzqh`;
CREATE TABLE `china_xzqh`  (
  `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'id',
  `code` char(6) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '行政区划编码',
  `name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '行政区划名称',
  `nameplus` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '名称重命名-加政府，用于匹配经纬度',
  `fullname` varchar(80) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '行政区划全称',
  `type` char(1) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '类型，1：省级，2：市级，3：县级',
  `citycode` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '城市编码',
  `cityname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '城市名',
  `pcode` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'POI所在省份编码',
  `pname` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'POI所在省份名称',
  `address` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '地址',
  `tel` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT 'POI的电话',
  `gd_jd` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高德经度',
  `gd_wd` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '高德纬度',
  `bd_jd` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '百度经度',
  `bd_wd` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '百度纬度',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '中国行政区划' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
```

**将从民政部获取得到的最新行政区划信息导入表中，作为原始数据**

## Java核心代码

> 使用 Spring Boot API Project Seed Plus 三步构建SpringBoot项目
>
> 使用笔记：https://blog.csdn.net/tian330726/article/details/106743836

```java
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
    private static String GD_KEY = "your-key";

    @Override
    public int setGdJwd() {
        // 全国
        List<ChinaXzqh> chinaXzqhs = chinaXzqhMapper.selectList(null);
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
```

前置条件：申请高德key

https://console.amap.com/dev/key/app

![gd-key](https://github.com/tyronczt/java-learn/blob/master/Tools/China-Xzqh/gd-key.png)<br/>