package com.tyron.design.pattern;

import com.tyron.design.pattern.constans.CompanyConstans;
import com.tyron.design.pattern.design.handle.CompanyFactory;
import com.tyron.design.pattern.design.handle.Handle;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DesignPatternApplicationTest1 {

    /**
     * 策略加工厂模式
     * 定义策略接口：Handle，定义Spring工厂：CompanyFactory
     * 每个实现类都需要注册到Spring容器中，实现方法，并注入到策略Map中
     *
     * 策略模式：定义一系列的算法,把每一个算法封装起来, 并且使它们可相互替换
     * 优点：
     * 1）多重条件语句不易维护，而使用策略模式可以避免使用多重条件语句，如 if...else 语句、switch...case 语句
     * 2）策略模式提供了一系列的可供重用的算法族，恰当使用继承可以把算法族的公共代码转移到父类里面，从而避免重复的代码
     * 3）策略模式可以提供相同行为的不同实现，客户可以根据不同时间或空间要求选择不同的。
     * 4）策略模式提供了对开闭原则的完美支持，可以在不修改原代码的情况下，灵活增加新算法。
     * 5）策略模式把算法的使用放到环境类中，而算法的实现移到具体策略类中，实现了二者的分离。
     *
     * 缺点：
     * 1）客户端必须理解所有策略算法的区别，以便适时选择恰当的算法类
     * 2）策略模式造成很多的策略类，增加维护难度
     * 3）Strategy和 Factory之间的通信开销
     */
    @Test
    public void strategy(){
        Handle sinaHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.SINA);
        sinaHandle.save2DB(CompanyConstans.SINA);
        Handle alibabaHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.ALIBABA);
        alibabaHandle.save2DB(CompanyConstans.ALIBABA);
        Handle tencentHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.TENCENT);
        tencentHandle.save2DB(CompanyConstans.TENCENT);
        Handle jdHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.JD);
        jdHandle.save2DB(CompanyConstans.JD);
        Handle com360Handle = CompanyFactory.getInvokeStrategy(CompanyConstans.COM360);
        com360Handle.save2DB(CompanyConstans.COM360);
        Handle baiduHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.BAIDU);
        baiduHandle.save2DB(CompanyConstans.BAIDU);
    }
}
/**
 * 输出结果：
 *
 * 新浪数据入库。。。
 * 阿里巴巴数据入库。。。
 * 腾讯数据入库。。。
 * 京东数据入库。。。
 * 360数据入库。。。
 * Baidu数据入库。。。
 */