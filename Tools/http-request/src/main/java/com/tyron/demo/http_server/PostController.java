package com.tyron.demo.http_server;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: post请求
 * @Author: tyron
 * @Date: Created in 2019/6/3
 */
@Controller
public class PostController {

    @PostMapping("/jsonPostReq")
    @ResponseBody
    public String jsonPostReq(@RequestBody User user) {
        return "用户：" + user.getName() + ",年龄：" + user.getAge();
    }

    @PostMapping("/stringPostReq")
    @ResponseBody
    public String stringPostReq(@RequestParam String name) {
        return "hello：" + name;
    }


}
