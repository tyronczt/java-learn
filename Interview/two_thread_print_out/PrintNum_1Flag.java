package com.tyron;

import java.util.concurrent.atomic.AtomicInteger;

public class PrintNum_1Flag {

    /**
     * 静态原子整型变量，用于在多线程环境下进行原子操作。
     * 该变量初始值为0，通过AtomicInteger类提供的原子方法，可以确保在多线程环境下的线程安全操作。
     */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    /**
     * 静态变量 `flag` 是一个 volatile 修饰的布尔值。
     *
     * 该变量被声明为 `volatile`，意味着它的值对所有线程都是可见的，并且对它的修改会立即反映到主内存中，
     * 从而确保多线程环境下的可见性和一致性。`volatile` 关键字通常用于标记那些可能被多个线程同时访问的变量，
     * 以避免线程间的数据不一致问题。
     *
     * 该变量通常用于控制线程的执行状态，例如作为线程循环的退出条件。
     */
    private static volatile boolean flag = true;

    public static void main(String[] args) {
        // 创建一个线程，执行一个Lambda表达式
        new Thread(() -> {
            // 当atomicInteger的值小于等于200时，执行循环
            while (atomicInteger.get() <= 200) {
                // 如果flag为true，则输出当前线程的名称和atomicInteger的值，并将flag设置为false
                if (flag) {
                    System.out.println("Flag打印偶数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                    flag = false;
                }
            }
        }).start();

        // 创建另一个线程，执行另一个Lambda表达式
        new Thread(() -> {
            // 当atomicInteger的值小于等于200时，执行循环
            while (atomicInteger.get() <= 200) {
                // 如果flag为false，则输出当前线程的名称和atomicInteger的值，并将flag设置为true
                if (!flag) {
                    System.out.println("Flag打印奇数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                    flag = true;
                }
            }
        }).start();
    }
}

