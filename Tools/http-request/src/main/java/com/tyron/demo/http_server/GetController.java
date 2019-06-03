package com.tyron.demo.http_server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Description: get请求
 * @Author: tyron
 * @Date: Created in 2019/6/3
 */
@Controller
public class GetController {

    @GetMapping("/getReq")
    @ResponseBody
    public String getReq(){
        return "hello get";
    }
}
