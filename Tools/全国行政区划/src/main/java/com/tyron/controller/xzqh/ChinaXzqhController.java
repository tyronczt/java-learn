package com.tyron.controller.xzqh;

import com.tyron.core.Result;
import com.tyron.core.ResultGenerator;
import com.tyron.pojo.entity.xzqh.ChinaXzqh;
import com.tyron.service.xzqh.ChinaXzqhService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description: 中国行政区划控制类
 * @Author: tyronchen
 * @Date: Created in 2020/04/27
 */
@RestController
@RequestMapping("/china/xzqh")
public class ChinaXzqhController {
    @Resource
    private ChinaXzqhService chinaXzqhService;

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        ChinaXzqh chinaXzqh = chinaXzqhService.findById(id);
        return ResultGenerator.genSuccessResult(chinaXzqh);
    }

    @GetMapping("/setGdJwd")
    public Result setGdJwd() {
        int count = chinaXzqhService.setGdJwd();
        if (count > 0) {
            return ResultGenerator.genSuccessResult(count);
        } else {
            return ResultGenerator.genFailResult("设值高德经纬度出错！");
        }
    }
}
