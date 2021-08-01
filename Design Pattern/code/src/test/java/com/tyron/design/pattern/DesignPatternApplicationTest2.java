package com.tyron.design.pattern;

import com.tyron.design.pattern.constans.CompanyConstans;
import com.tyron.design.pattern.design2.handle.CompanyFactory;
import com.tyron.design.pattern.design2.handle.AbstractHandler;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DesignPatternApplicationTest2 {

    /**
     * 策略加工厂加模板方法模式
     * 定义策略接口：Handle，定义Spring工厂：CompanyFactory
     * 每个实现类都需要注册到Spring容器中，实现方法，并注入到策略Map中
     * 主要解决的问题是 针对不同公司的数据并不能通用save2DB方法，比如新浪、腾讯的数据需要有返回信息，原先的方法无法满足
     *
     * 模板方法（TemplateMethod）模式：定义一个操作中的算法骨架，而将算法的一些步骤延迟到子类中，
     * 使得子类可以不改变该算法结构的情况下重定义该算法的某些特定步骤
     *
     * 特点：
     * 1）它封装了不变部分，扩展可变部分。它把认为是不变部分的算法封装到父类中实现，而把可变部分算法由子类继承实现，便于子类继续扩展
     * 2）它在父类中提取了公共的部分代码，便于代码复用
     * 3）部分方法是由子类实现的，因此子类可以通过扩展方式增加相应的功能，符合开闭原则
     *
     * 缺点：
     * 1）对每个不同的实现都需要定义一个子类，这会导致类的个数增加，系统更加庞大，设计也更加抽象，间接地增加了系统实现的复杂度
     * 2）父类中的抽象方法由子类实现，子类执行的结果会影响父类的结果，这导致一种反向的控制结构，它提高了代码阅读的难度
     * 3）由于继承关系自身的缺点，如果父类添加新的抽象方法，则所有子类都要改一遍
     */
    @Test
    public void strategy(){
        AbstractHandler sinaHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.SINA);
        System.out.println(sinaHandle.save2DBReturnString(CompanyConstans.SINA));
        AbstractHandler alibabaHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.ALIBABA);
        alibabaHandle.save2DBNoReturn(CompanyConstans.ALIBABA);
        AbstractHandler tencentHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.TENCENT);
        System.out.println(tencentHandle.save2DBReturnString(CompanyConstans.TENCENT));
        AbstractHandler jdHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.JD);
        jdHandle.save2DBNoReturn(CompanyConstans.JD);
        AbstractHandler com360Handle = CompanyFactory.getInvokeStrategy(CompanyConstans.COM360);
        com360Handle.save2DBNoReturn(CompanyConstans.COM360);
        AbstractHandler baiduHandle = CompanyFactory.getInvokeStrategy(CompanyConstans.BAIDU);
        baiduHandle.save2DBNoReturn(CompanyConstans.BAIDU);
    }
}
/**
 * 输出结果：
 *
 * 参数返回：新浪数据入库。。。
 * 阿里巴巴数据入库。。。
 * 参数返回：腾讯数据入库。。。
 * 京东数据入库。。。
 * 360数据入库。。。
 * Baidu数据入库。。。
 */