package com.tyron.springAopLog.conntroller;

import com.tyron.springAopLog.annotation.Log;
import com.tyron.springAopLog.emun.BusinessType;
import com.tyron.springAopLog.model.User;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: Hello控制器
 * @Author: tyron
 * @Date: Created in 2021/7/24
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    @Log(title = "打招呼", businessType = BusinessType.SELECT)
    public String hello() {
        return "hello";
    }

    @PostMapping("/post")
    @Log(title = "添加用户", businessType = BusinessType.INSERT)
    public String post(@RequestBody User user) {
        // TODO 用户校验，插入数据库
        // 模拟空指针异常
        if (user != null){
            int length = user.getNickName().length();
            System.out.println(length);
        }
        return "插入成功：" + user.toString();
    }
}
