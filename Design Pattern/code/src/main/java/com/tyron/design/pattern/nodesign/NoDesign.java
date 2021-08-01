package com.tyron.design.pattern.nodesign;

import com.tyron.design.pattern.constans.CompanyConstans;

/**
 * @Description: 无设计模式代码
 * @Author: tyron
 * @Date: Created in 2021/8/1
 */
public class NoDesign {

    /**
     * 业务场景是要对接多家公司的数据入库到自己的业务库中
     * 开始对接了三家，直接用if，else即可实现
     * 后面又对接了三家，导致业务代码中全部都是if,else
     * 所以改进用设计模式的理念，遵守开闭原则
     * 涉及到的设计模式有：策略设计模式 + 工厂模式 + 模板方法模式
     *
     * if-else写法的优缺点：
     * 优点：所见即所得，业务逻辑清晰
     * 缺点：后期维护成本大，不好扩展
     * 阿里巴巴java开发手册：超过 3 层的 if-else 的逻辑判断代码可以使用卫语句、策略模式、状态模式等来实现
     *
     * 参考视频讲解：https://www.bilibili.com/video/BV1b5411a7oa
     */
    public static void NoDesignBusiness(String company) {
        // 判断公司类型，然后进行数据入库操作
        if ("sina".equals(company)) {
            System.out.println("新浪数据入库。。。");
            return;
        }
        if ("alibaba".equals(company)) {
            System.out.println("阿里巴巴数据入库。。。");
            return;
        }
        if ("tencent".equals(company)) {
            System.out.println("腾讯数据入库。。。");
            return;
        }
        if ("baidu".equals(company)) {
            System.out.println("百度数据入库。。。");
            return;
        }
        if ("360".equals(company)) {
            System.out.println("360数据入库。。。");
            return;
        }
        if ("jd".equals(company)) {
            System.out.println("京东数据入库。。。");
            return;
        }
        return;
    }

    public static void main(String[] args) {
        // 处理业务
        NoDesignBusiness(CompanyConstans.BAIDU);
        NoDesignBusiness(CompanyConstans.JD);
        NoDesignBusiness(CompanyConstans.COM360);
        NoDesignBusiness(CompanyConstans.ALIBABA);
        NoDesignBusiness(CompanyConstans.TENCENT);
        NoDesignBusiness(CompanyConstans.SINA);
    }
}
