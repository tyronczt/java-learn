package com.tyron;

import com.tyron.service.IUserService;
import com.tyron.service.impl.UserServiceAImpl;
import com.tyron.service.impl.UserServiceBImpl;
import com.tyron.service.impl.UserServiceProxy;
import org.junit.jupiter.api.Test;

/**
 * @Description: 代理测试
 * @Author: tyron
 * @Date: Created in 2021/8/18
 */
public class ProxyTest {

    @Test
    public void test1() {
        UserServiceAImpl serviceA = new UserServiceAImpl();
        UserServiceBImpl serviceB = new UserServiceBImpl();
        serviceA.user1();
        serviceA.user2();
        serviceA.user3();

        serviceB.user1();
        serviceB.user2();
        serviceB.user3();
    }

    /**
     * 我是实现类A中的user1!
     * 我是实现类A中的user2!
     * 我是实现类A中的user3!
     * 我是实现类B中的user1!
     * 我是实现类B中的user2!
     * 我是实现类B中的user3!
     */

    /********************************************************************/
    /**
     * 需求：调用 IUserService 接口中的任何方法的时候，需要记录方法的耗时。
     *
     * 方法1：可以为 IUserService 接口创建一个代理类，通过这个代理类来间接访问 IUserService 接口的实现类，
     * 在这个代理类中去做耗时及发送至监控的代码：{@link com.tyron.service.impl.UserServiceProxy}
     *
     * 方法2：jdk动态代理  实现用到的类：java.lang.reflect.Proxy java.lang.reflect.InvocationHandler
     *      方式一： {@link com.tyron.service.impl.UserServiceProxy}
     *             1.调用 Proxy.getProxyClass方法获取代理类的 Class 对象
     *             2.使用 InvocationHandler 接口创建代理类的处理器
     *             3.通过代理类和 InvocationHandler 创建代理对象
     *             4.上面已经创建好代理对象了，接着我们就可以使用代理对象了
     *      方式二：1.使用 InvocationHandler 接口创建代理类的处理器
     *            2.使用 Proxy 类的静态方法 newProxyInstance 直接创建代理对象
     *            3.使用代理对象
     * 方法3：cglib代理
     */
    @Test
    public void test2() {
        IUserService serviceA = new UserServiceProxy(new UserServiceAImpl());
        IUserService serviceB = new UserServiceProxy(new UserServiceBImpl());
        serviceA.user1();
        serviceA.user2();
        serviceA.user3();

        serviceB.user1();
        serviceB.user2();
        serviceB.user3();
    }
    /**
     * 我是实现类A中的user1!
     * class com.tyron.service.impl.UserServiceAImpl.user1()方法耗时(纳秒):55800
     * 我是实现类A中的user2!
     * class com.tyron.service.impl.UserServiceAImpl.user2()方法耗时(纳秒):17300
     * 我是实现类A中的user3!
     * class com.tyron.service.impl.UserServiceAImpl.user3()方法耗时(纳秒):15700
     * 我是实现类B中的user1!
     * class com.tyron.service.impl.UserServiceBImpl.user1()方法耗时(纳秒):19400
     * 我是实现类B中的user2!
     * class com.tyron.service.impl.UserServiceBImpl.user2()方法耗时(纳秒):14800
     * 我是实现类B中的user3!
     * class com.tyron.service.impl.UserServiceBImpl.user3()方法耗时(纳秒):14200
     */
}
