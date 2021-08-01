package com.tyron.design.pattern;

import com.tyron.design.pattern.constans.CompanyConstans;
import com.tyron.design.pattern.design.handle.CompanyFactory;
import com.tyron.design.pattern.design.handle.Handle;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DesignPatternApplicationTest {

    /**
     * 策略加工厂模式
     * 定义策略接口：Handle，定义Spring工厂：CompanyFactory
     * 每个实现类都需要注册到Spring容器中，实现方法，并注入到策略Map中
     *
     * 策略模式：定义一系列的算法,把每一个算法封装起来, 并且使它们可相互替换
     * 优点：
     * 1）解耦，每个业务类专注自己的业务，避免使用多重条件转移语句，如if...else...语句、switch 语句，
     * 2）策略模式符合开闭原则
     *
     * 缺点：
     * 1）客户端必须知道所有的策略类，并自行决定使用哪一个策略类
     * 2）策略模式将造成产生很多策略类
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
