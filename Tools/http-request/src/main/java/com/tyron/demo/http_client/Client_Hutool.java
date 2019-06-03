package com.tyron.demo.http_client;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.HashMap;
import java.util.Map;

import static com.tyron.demo.http_client.Client_HttpURLConnection.parseJson;

/**
 * @Description: Hutool
 * @Author: tyron
 * @Date: Created in 2019/6/3
 */
public class Client_Hutool {

    public static void main(String[] args) {
        // 最简单的HTTP请求，可以自动通过header等信息判断编码，不区分HTTP和HTTPS
        String result1 = HttpUtil.get("http://localhost:8080/getReq");
        System.out.println(result1);

        // 当无法识别页面编码的时候，可以自定义请求页面的编码
        String result2 = HttpUtil.get("http://localhost:8080/getReq", CharsetUtil.CHARSET_UTF_8);
        System.out.println(result2);

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "tyron");
        String result = HttpUtil.post("http://localhost:8080/stringPostReq", paramMap);
        System.out.println(result);

        Map<String, Object> map = new HashMap<>();
        map.put("name", "tyron");
        map.put("age", 18);
        System.out.println(HttpUtil.post("http://localhost:8080/jsonPostReq", JSONUtil.parseFromMap(map).toString()));
    }
}
